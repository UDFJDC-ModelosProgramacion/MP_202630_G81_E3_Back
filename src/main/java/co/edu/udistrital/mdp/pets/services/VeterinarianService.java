package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
import co.edu.udistrital.mdp.pets.repositories.VeterinarianRepository;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

@Slf4j
@Service
public class VeterinarianService {

    private static final String VETERINARIAN_NOT_FOUND = "Veterinarian not found";

    private VeterinarianRepository veterinarianRepository;
    private ShelterRepository shelterRepository;

    public VeterinarianService(
            @Autowired VeterinarianRepository veterinarianRepository,
            @Autowired ShelterRepository shelterRepository) {
        this.veterinarianRepository = veterinarianRepository;
        this.shelterRepository = shelterRepository;
    }

    public static final int MIN_AGE = 18;
    public static final int MAX_AGE = 100;
    public static final List<String> GENDERS = List.of("FEMALE", "MALE", "OTHER");

    private void validatePersonalData(VeterinarianEntity veterinarian) throws IllegalOperationException {
        Integer age = veterinarian.getAge();
        if (age == null || age < MIN_AGE || age > MAX_AGE) {
            throw new IllegalOperationException("Veterinarian age is not valid");
        }
        if (veterinarian.getGender() == null || !GENDERS.contains(veterinarian.getGender())) {
            throw new IllegalOperationException("Veterinarian gender is not valid");
        }
    }

    @Transactional
    public VeterinarianEntity createVeterinarian(
            VeterinarianEntity veterinarian)
            throws EntityNotFoundException, IllegalOperationException {

        log.info("Inicia proceso de creación del veterinario");

        if (veterinarian.getName() == null ||
            veterinarian.getName().isBlank()) {
            throw new IllegalOperationException(
                    "Veterinarian name is not valid");
        }

        if (veterinarian.getSpeciality() == null ||
            veterinarian.getSpeciality().isBlank()) {
            throw new IllegalOperationException(
                    "Veterinarian speciality is not valid");
        }

        validatePersonalData(veterinarian);

        if (veterinarian.getShelter() == null) {
            throw new IllegalOperationException(
                    "Shelter is not valid");
        }

        Optional<ShelterEntity> shelter =
                shelterRepository.findById(
                        veterinarian.getShelter().getId());

        if (shelter.isEmpty()) {
            throw new EntityNotFoundException(
                    "Shelter not found");
        }

        veterinarian.setShelter(shelter.get());

        log.info("Termina proceso de creación del veterinario");

        return veterinarianRepository.save(veterinarian);
    }

    @Transactional
    public List<VeterinarianEntity> getVeterinarians() {

        log.info("Inicia proceso de consultar todos los veterinarios");

        return veterinarianRepository.findAll();
    }

    @Transactional
    public VeterinarianEntity getVeterinarian(Long veterinarianId)
            throws EntityNotFoundException {

        log.info(
                "Inicia proceso de consultar el veterinario con id = {}",
                veterinarianId);

        Optional<VeterinarianEntity> veterinarian =
                veterinarianRepository.findById(veterinarianId);

        if (veterinarian.isEmpty()) {
            throw new EntityNotFoundException(
                    VETERINARIAN_NOT_FOUND);
        }

        return veterinarian.get();
    }

    @Transactional
    public VeterinarianEntity updateVeterinarian(
            Long veterinarianId,
            VeterinarianEntity veterinarian)
            throws EntityNotFoundException, IllegalOperationException {

        log.info(
                "Inicia proceso de actualizar el veterinario con id = {}",
                veterinarianId);

        Optional<VeterinarianEntity> existing =
                veterinarianRepository.findById(veterinarianId);

        if (existing.isEmpty()) {
            throw new EntityNotFoundException(
                    VETERINARIAN_NOT_FOUND);
        }

        if (veterinarian.getName() == null ||
            veterinarian.getName().isBlank()) {
            throw new IllegalOperationException(
                    "Veterinarian name is not valid");
        }

        if (veterinarian.getSpeciality() == null ||
            veterinarian.getSpeciality().isBlank()) {
            throw new IllegalOperationException(
                    "Veterinarian speciality is not valid");
        }

        validatePersonalData(veterinarian);

        if (veterinarian.getShelter() == null) {
            throw new IllegalOperationException(
                    "Shelter is not valid");
        }

        Optional<ShelterEntity> shelter =
                shelterRepository.findById(
                        veterinarian.getShelter().getId());

        if (shelter.isEmpty()) {
            throw new EntityNotFoundException(
                    "Shelter not found");
        }

        veterinarian.setId(veterinarianId);
        veterinarian.setShelter(shelter.get());

        log.info(
                "Termina proceso de actualizar el veterinario con id = {}",
                veterinarianId);

        return veterinarianRepository.save(veterinarian);
    }

    @Transactional
    public void deleteVeterinarian(Long veterinarianId)
            throws EntityNotFoundException, IllegalOperationException {

        log.info(
                "Inicia proceso de borrar el veterinario con id = {}",
                veterinarianId);

        Optional<VeterinarianEntity> veterinarian =
                veterinarianRepository.findById(veterinarianId);

        if (veterinarian.isEmpty()) {
            throw new EntityNotFoundException(
                    VETERINARIAN_NOT_FOUND);
        }

        veterinarianRepository.deleteById(veterinarianId);

        log.info(
                "Termina proceso de borrar el veterinario con id = {}",
                veterinarianId);
    }
}
