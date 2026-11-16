package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.MedicalRecordEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ForbiddenOperationException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

import jakarta.persistence.EntityManager;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(MedicalRecordService.class)
class MedicalRecordServiceTest {

    private static final Long NON_EXISTENT_ID = 999999L;

    @Autowired
    private MedicalRecordService medicalRecordService;

    @Autowired
    private EntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();

    private ShelterEntity ownShelter;
    private VeterinarianEntity veterinarian;
    private PetEntity ownPet;
    private PetEntity foreignPet;

    @BeforeEach
    void setUp() {
        entityManager.createQuery("delete from MedicalRecordEntity").executeUpdate();

        ownShelter = persistShelter();
        ShelterEntity otherShelter = persistShelter();

        veterinarian = new VeterinarianEntity();
        veterinarian.setName("Carolina Pérez");
        veterinarian.setSpeciality("Medicina general");
        veterinarian.setShelter(ownShelter);
        entityManager.persist(veterinarian);

        ownPet = persistPet(ownShelter);
        foreignPet = persistPet(otherShelter);
        entityManager.flush();
        entityManager.refresh(ownShelter);
    }

    private ShelterEntity persistShelter() {
        ShelterEntity shelter = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(shelter);
        return shelter;
    }

    private PetEntity persistPet(ShelterEntity shelter) {
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        pet.setShelter(shelter);
        entityManager.persist(pet);
        return pet;
    }

    private MedicalRecordEntity persistRecord(PetEntity pet, LocalDate date, String reason) {
        MedicalRecordEntity record = new MedicalRecordEntity();
        record.setDate(date);
        record.setReason(reason);
        record.setDiagnosis("Diagnóstico " + reason);
        record.setTreatment("Tratamiento " + reason);
        record.setPet(pet);
        record.setVeterinarian(veterinarian);
        entityManager.persist(record);
        return record;
    }

    @Test
    void getPetsInCharge_shouldReturnOnlyPetsOfTheVeterinariansShelter() throws Exception {
        List<PetEntity> result = medicalRecordService.getPetsInCharge(veterinarian.getId());

        assertTrue(result.stream().anyMatch(p -> p.getId().equals(ownPet.getId())));
        assertTrue(result.stream().noneMatch(p -> p.getId().equals(foreignPet.getId())));
    }

    @Test
    void getPetsInCharge_withUnknownVeterinarian_shouldThrowNotFound() {
        assertThrows(EntityNotFoundException.class, () -> medicalRecordService.getPetsInCharge(NON_EXISTENT_ID));
    }

    @Test
    void getMedicalRecords_shouldReturnNewestFirst() throws Exception {
        persistRecord(ownPet, LocalDate.now().minusDays(30), "Vacuna");
        persistRecord(ownPet, LocalDate.now().minusDays(2), "Tos");
        persistRecord(foreignPet, LocalDate.now(), "Otra mascota");

        List<MedicalRecordEntity> result = medicalRecordService.getMedicalRecords(veterinarian.getId(),
                ownPet.getId());

        assertEquals(2, result.size());
        assertEquals("Tos", result.get(0).getReason());
        assertEquals("Vacuna", result.get(1).getReason());
        assertEquals("Diagnóstico Tos", result.get(0).getDiagnosis());
    }

    @Test
    void getMedicalRecords_withoutRecords_shouldReturnEmptyList() throws Exception {
        assertTrue(medicalRecordService.getMedicalRecords(veterinarian.getId(), ownPet.getId()).isEmpty());
    }

    @Test
    void getMedicalRecords_ofPetNotInCharge_shouldBeForbidden() {
        persistRecord(foreignPet, LocalDate.now(), "Privado");
        assertThrows(ForbiddenOperationException.class,
                () -> medicalRecordService.getMedicalRecords(veterinarian.getId(), foreignPet.getId()));
    }

    @Test
    void getMedicalRecords_withUnknownPetOrVeterinarian_shouldThrowNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> medicalRecordService.getMedicalRecords(veterinarian.getId(), NON_EXISTENT_ID));
        assertThrows(EntityNotFoundException.class,
                () -> medicalRecordService.getMedicalRecords(NON_EXISTENT_ID, ownPet.getId()));
    }

    @Test
    void createMedicalRecord_shouldPersistAndLinkPetAndVeterinarian() throws Exception {
        MedicalRecordEntity entity = new MedicalRecordEntity();
        entity.setDate(LocalDate.now());
        entity.setReason("Control");

        MedicalRecordEntity result = medicalRecordService.createMedicalRecord(veterinarian.getId(), ownPet.getId(),
                entity);

        assertNotNull(result.getId());
        MedicalRecordEntity stored = entityManager.find(MedicalRecordEntity.class, result.getId());
        assertEquals("Control", stored.getReason());
        assertEquals(ownPet.getId(), stored.getPet().getId());
        assertEquals(veterinarian.getId(), stored.getVeterinarian().getId());
    }

    @Test
    void createMedicalRecord_withInvalidData_shouldThrowException() {
        MedicalRecordEntity noReason = new MedicalRecordEntity();
        noReason.setDate(LocalDate.now());
        assertThrows(IllegalOperationException.class,
                () -> medicalRecordService.createMedicalRecord(veterinarian.getId(), ownPet.getId(), noReason));

        MedicalRecordEntity futureDate = new MedicalRecordEntity();
        futureDate.setDate(LocalDate.now().plusDays(1));
        futureDate.setReason("Control");
        assertThrows(IllegalOperationException.class,
                () -> medicalRecordService.createMedicalRecord(veterinarian.getId(), ownPet.getId(), futureDate));
    }

    @Test
    void createMedicalRecord_forPetNotInCharge_shouldBeForbidden() {
        MedicalRecordEntity entity = new MedicalRecordEntity();
        entity.setDate(LocalDate.now());
        entity.setReason("Control");
        assertThrows(ForbiddenOperationException.class,
                () -> medicalRecordService.createMedicalRecord(veterinarian.getId(), foreignPet.getId(), entity));
    }
}
