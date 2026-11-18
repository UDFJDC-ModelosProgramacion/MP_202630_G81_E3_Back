package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.CaseDevolutionEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import jakarta.persistence.EntityManager;

/** Estados de la mascota y de la adopción según devoluciones, aprobaciones y veterinario asignado. */
@DataJpaTest
@Transactional
@Import({ CaseDevolutionService.class, AdoptionService.class })
class DevolutionAndAdoptionFlowTest {

    @Autowired
    private CaseDevolutionService devolutionService;

    @Autowired
    private AdoptionService adoptionService;

    @Autowired
    private EntityManager entityManager;

    private ShelterEntity shelter;
    private PetEntity pet;
    private AdoptionEntity adoption;

    @BeforeEach
    void setUp() {
        shelter = TestData.shelter(entityManager, "Huellas");
        pet = TestData.pet(entityManager, shelter, "Luna", "ADOPTED");
        adoption = TestData.adoption(entityManager, pet, TestData.adopter(entityManager, "Ana"), "COMPLETED");
    }

    @Test
    void createDevolution_shouldMakePetAvailableAndMarkAdoptionReturned() throws Exception {
        CaseDevolutionEntity devolution = new CaseDevolutionEntity();
        devolution.setDate(LocalDate.now());
        devolution.setReason("Cambio de circunstancias");

        devolutionService.createCaseDevolution(adoption.getId(), devolution);

        assertEquals("AVAILABLE", pet.getStatus());
        assertEquals("RETURNED", adoption.getStatus());
    }

    @Test
    void updateAdoption_toApproved_shouldMovePetToInProcess() throws Exception {
        pet.setStatus("AVAILABLE");
        AdoptionEntity changes = new AdoptionEntity();
        changes.setAdoptionDate(adoption.getAdoptionDate());
        changes.setStatus("APPROVED");

        adoptionService.updateAdoption(adoption.getId(), changes);

        assertEquals("IN_PROCESS", pet.getStatus());
    }

    @Test
    void updateAdoption_toRejected_shouldMakePetAvailable() throws Exception {
        pet.setStatus("IN_PROCESS");
        AdoptionEntity changes = new AdoptionEntity();
        changes.setAdoptionDate(adoption.getAdoptionDate());
        changes.setStatus("REJECTED");

        adoptionService.updateAdoption(adoption.getId(), changes);

        assertEquals("AVAILABLE", pet.getStatus());
    }

    @Test
    void updateAdoption_withUnknownStatus_shouldThrow() {
        AdoptionEntity changes = new AdoptionEntity();
        changes.setAdoptionDate(adoption.getAdoptionDate());
        changes.setStatus("WHATEVER");

        assertThrows(IllegalOperationException.class, () -> adoptionService.updateAdoption(adoption.getId(), changes));
    }

    @Test
    void createAdoption_withUnavailablePet_shouldThrow() {
        AdoptionEntity request = new AdoptionEntity();
        request.setPet(pet);
        request.setAdopter(adoption.getAdopter());
        request.setAdoptionDate(LocalDate.now());

        assertThrows(IllegalOperationException.class, () -> adoptionService.createAdoption(request));
    }

    @Test
    void assignVeterinarian_shouldRequireSameShelter() throws Exception {
        VeterinarianEntity sameShelter = TestData.veterinarian(entityManager, shelter, "Dr. Gómez");
        VeterinarianEntity foreign = TestData.veterinarian(entityManager, TestData.shelter(entityManager, "Otro"),
                "Dra. Ruiz");

        AdoptionEntity result = adoptionService.assignVeterinarian(adoption.getId(), sameShelter.getId());

        assertEquals(sameShelter.getId(), result.getVeterinarian().getId());
        assertThrows(IllegalOperationException.class,
                () -> adoptionService.assignVeterinarian(adoption.getId(), foreign.getId()));
    }
}
