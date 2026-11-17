package co.edu.udistrital.mdp.pets.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.MedicalRecordEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ForbiddenOperationException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.MedicalRecordRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import co.edu.udistrital.mdp.pets.repositories.VeterinarianRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Historial médico de las mascotas. Un veterinario está autorizado a consultar y registrar
 * atenciones solo de las mascotas a su cargo, es decir, las de su mismo refugio.
 */
@Slf4j
@Service
public class MedicalRecordService {

    private static final String VETERINARIAN_NOT_FOUND = "Veterinario no encontrado";
    private static final String PET_NOT_FOUND = "Mascota no encontrada";
    private static final String ACCESS_DENIED = "No tienes autorización para consultar esta mascota";

    private final MedicalRecordRepository medicalRecordRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final PetRepository petRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository,
            VeterinarianRepository veterinarianRepository, PetRepository petRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.veterinarianRepository = veterinarianRepository;
        this.petRepository = petRepository;
    }

    /** Mascotas a cargo del veterinario (las de su refugio). */
    @Transactional(readOnly = true)
    public List<PetEntity> getPetsInCharge(Long veterinarianId) throws EntityNotFoundException {
        log.info("Inicia proceso de consultar las mascotas a cargo del veterinario con id = {}", veterinarianId);
        VeterinarianEntity veterinarian = findVeterinarianOrThrow(veterinarianId);
        if (veterinarian.getShelter() == null)
            return new ArrayList<>();
        return new ArrayList<>(veterinarian.getShelter().getPets());
    }

    /** Historial médico de una mascota a cargo del veterinario, del más reciente al más antiguo. */
    @Transactional(readOnly = true)
    public List<MedicalRecordEntity> getMedicalRecords(Long veterinarianId, Long petId)
            throws EntityNotFoundException, ForbiddenOperationException {
        log.info("Inicia proceso de consultar el historial médico de la mascota con id = {}", petId);
        authorize(veterinarianId, petId);
        return medicalRecordRepository.findByPetIdOrderByDateDescIdDesc(petId);
    }

    /** Registra una atención. Exige fecha (no futura) y motivo; diagnóstico y tratamiento son opcionales. */
    @Transactional
    public MedicalRecordEntity createMedicalRecord(Long veterinarianId, Long petId, MedicalRecordEntity record)
            throws EntityNotFoundException, ForbiddenOperationException, IllegalOperationException {
        log.info("Inicia proceso de registrar una atención médica para la mascota con id = {}", petId);
        PetEntity pet = authorize(veterinarianId, petId);

        if (record.getDate() == null || record.getDate().isAfter(LocalDate.now()))
            throw new IllegalOperationException("La fecha de atención no es válida");

        if (record.getReason() == null || record.getReason().isBlank())
            throw new IllegalOperationException("El motivo de consulta es obligatorio");

        record.setId(null);
        record.setPet(pet);
        record.setVeterinarian(veterinarianRepository.getReferenceById(veterinarianId));
        return medicalRecordRepository.save(record);
    }

    /** Verifica que la mascota exista y esté a cargo del veterinario; devuelve la mascota. */
    private PetEntity authorize(Long veterinarianId, Long petId)
            throws EntityNotFoundException, ForbiddenOperationException {
        VeterinarianEntity veterinarian = findVeterinarianOrThrow(veterinarianId);
        PetEntity pet = petRepository.findById(petId).orElseThrow(() -> new EntityNotFoundException(PET_NOT_FOUND));

        boolean inCharge = veterinarian.getShelter() != null && pet.getShelter() != null
                && veterinarian.getShelter().getId().equals(pet.getShelter().getId());
        if (!inCharge)
            throw new ForbiddenOperationException(ACCESS_DENIED);

        return pet;
    }

    private VeterinarianEntity findVeterinarianOrThrow(Long veterinarianId) throws EntityNotFoundException {
        return veterinarianRepository.findById(veterinarianId)
                .orElseThrow(() -> new EntityNotFoundException(VETERINARIAN_NOT_FOUND));
    }
}
