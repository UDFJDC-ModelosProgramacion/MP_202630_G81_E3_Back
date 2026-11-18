package co.edu.udistrital.mdp.pets.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.VaccinationEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import co.edu.udistrital.mdp.pets.repositories.VaccinationRepository;
import co.edu.udistrital.mdp.pets.repositories.VeterinarianRepository;
import lombok.extern.slf4j.Slf4j;

/** Carné de vacunación digital de una mascota y consulta de las próximas dosis. */
@Slf4j
@Service
public class VaccinationService {

    private static final String PET_NOT_FOUND = "Pet not found";
    private static final String VACCINATION_NOT_FOUND = "Vaccination not found";
    private static final int MAX_TEXT_LENGTH = 255;

    private final VaccinationRepository vaccinationRepository;
    private final PetRepository petRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final AutoNotificationService notificationService;

    public VaccinationService(VaccinationRepository vaccinationRepository, PetRepository petRepository,
            VeterinarianRepository veterinarianRepository, AutoNotificationService notificationService) {
        this.vaccinationRepository = vaccinationRepository;
        this.petRepository = petRepository;
        this.veterinarianRepository = veterinarianRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public VaccinationEntity createVaccination(Long petId, VaccinationEntity vaccination)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de registrar una vacuna para la mascota con id = {}", petId);
        PetEntity pet = findPetOrThrow(petId);
        validate(vaccination);
        vaccination.setId(null);
        vaccination.setPet(pet);
        vaccination.setVeterinarian(resolveVeterinarian(vaccination, pet));
        VaccinationEntity saved = vaccinationRepository.save(vaccination);
        notificationService.notifyShelter(pet.getShelter(),
                "Carné de vacunación actualizado: " + pet.getName() + " (" + saved.getName() + ")");
        log.info("Termina proceso de registrar una vacuna para la mascota con id = {}", petId);
        return saved;
    }

    @Transactional
    public List<VaccinationEntity> getVaccinations(Long petId) throws EntityNotFoundException {
        findPetOrThrow(petId);
        return vaccinationRepository.findByPetIdOrderByApplicationDateDescIdDesc(petId);
    }

    /** Vacunas con próxima dosis hoy o más adelante, de la más cercana a la más lejana. */
    @Transactional
    public List<VaccinationEntity> getUpcomingVaccinations(Long petId) throws EntityNotFoundException {
        findPetOrThrow(petId);
        LocalDate today = LocalDate.now();
        return vaccinationRepository.findByPetIdOrderByApplicationDateDescIdDesc(petId).stream()
                .filter(v -> v.getNextDueDate() != null && !v.getNextDueDate().isBefore(today))
                .sorted((a, b) -> a.getNextDueDate().compareTo(b.getNextDueDate()))
                .toList();
    }

    @Transactional
    public VaccinationEntity updateVaccination(Long petId, Long vaccinationId, VaccinationEntity vaccination)
            throws EntityNotFoundException, IllegalOperationException {
        PetEntity pet = findPetOrThrow(petId);
        findVaccinationOrThrow(petId, vaccinationId);
        validate(vaccination);
        vaccination.setId(vaccinationId);
        vaccination.setPet(pet);
        vaccination.setVeterinarian(resolveVeterinarian(vaccination, pet));
        VaccinationEntity saved = vaccinationRepository.save(vaccination);
        notificationService.notifyShelter(pet.getShelter(),
                "Carné de vacunación actualizado: " + pet.getName() + " (" + saved.getName() + ")");
        return saved;
    }

    @Transactional
    public void deleteVaccination(Long petId, Long vaccinationId) throws EntityNotFoundException {
        findPetOrThrow(petId);
        findVaccinationOrThrow(petId, vaccinationId);
        vaccinationRepository.deleteById(vaccinationId);
    }

    private void validate(VaccinationEntity vaccination) throws IllegalOperationException {
        if (vaccination.getName() == null || vaccination.getName().isBlank())
            throw new IllegalOperationException("Vaccine name is not valid");
        if (vaccination.getName().length() > MAX_TEXT_LENGTH)
            throw new IllegalOperationException("Vaccine name is too long");
        if (vaccination.getNotes() != null && vaccination.getNotes().length() > MAX_TEXT_LENGTH)
            throw new IllegalOperationException("Notes are too long");
        if (vaccination.getApplicationDate() == null || vaccination.getApplicationDate().isAfter(LocalDate.now()))
            throw new IllegalOperationException("Application date is not valid");
        if (vaccination.getNextDueDate() != null
                && vaccination.getNextDueDate().isBefore(vaccination.getApplicationDate()))
            throw new IllegalOperationException("Next due date cannot be before the application date");
    }

    /** El veterinario es opcional, pero si se indica debe existir y trabajar en el refugio de la mascota. */
    private VeterinarianEntity resolveVeterinarian(VaccinationEntity vaccination, PetEntity pet)
            throws EntityNotFoundException, IllegalOperationException {
        if (vaccination.getVeterinarian() == null || vaccination.getVeterinarian().getId() == null)
            return null;
        VeterinarianEntity veterinarian = veterinarianRepository.findById(vaccination.getVeterinarian().getId())
                .orElseThrow(() -> new EntityNotFoundException("Veterinarian not found"));
        boolean sameShelter = veterinarian.getShelter() != null && pet.getShelter() != null
                && veterinarian.getShelter().getId().equals(pet.getShelter().getId());
        if (!sameShelter)
            throw new IllegalOperationException("The veterinarian does not work at the pet's shelter");
        return veterinarian;
    }

    private PetEntity findPetOrThrow(Long petId) throws EntityNotFoundException {
        return petRepository.findById(petId).orElseThrow(() -> new EntityNotFoundException(PET_NOT_FOUND));
    }

    private VaccinationEntity findVaccinationOrThrow(Long petId, Long vaccinationId) throws EntityNotFoundException {
        VaccinationEntity vaccination = vaccinationRepository.findById(vaccinationId)
                .orElseThrow(() -> new EntityNotFoundException(VACCINATION_NOT_FOUND));
        if (vaccination.getPet() == null || !petId.equals(vaccination.getPet().getId()))
            throw new EntityNotFoundException(VACCINATION_NOT_FOUND);
        return vaccination;
    }
}
