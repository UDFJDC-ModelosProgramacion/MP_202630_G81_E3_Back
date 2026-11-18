package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

import jakarta.persistence.EntityManager;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(AdoptionVeterinarianService.class)
class AdoptionVeterinarianServiceTest {

    private static final Long NON_EXISTENT_ID = 999999L;

    @Autowired
    private AdoptionVeterinarianService service;

    @Autowired
    private EntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();

    private ShelterEntity shelter;
    private PetEntity pet;
    private AdopterEntity adopter;
    private AdoptionEntity adoption;
    private VeterinarianEntity veterinarian;

    @BeforeEach
    void setUp() {
        entityManager.createQuery("delete from AdoptionEntity").executeUpdate();

        shelter = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(shelter);
        pet = factory.manufacturePojo(PetEntity.class);
        pet.setShelter(shelter);
        entityManager.persist(pet);
        adopter = factory.manufacturePojo(AdopterEntity.class);
        entityManager.persist(adopter);

        adoption = buildAdoption("IN_PROGRESS");
        veterinarian = buildVeterinarian(shelter);
    }

    private AdoptionEntity buildAdoption(String status) {
        AdoptionEntity entity = new AdoptionEntity();
        entity.setAdoptionDate(LocalDate.now());
        entity.setStatus(status);
        entity.setPet(pet);
        entity.setAdopter(adopter);
        entityManager.persist(entity);
        return entity;
    }

    private VeterinarianEntity buildVeterinarian(ShelterEntity workplace) {
        VeterinarianEntity entity = new VeterinarianEntity();
        entity.setName("Laura Ruiz");
        entity.setSpeciality("Medicina general");
        entity.setAge(35);
        entity.setGender("FEMALE");
        entity.setShelter(workplace);
        entityManager.persist(entity);
        return entity;
    }

    @Test
    void assignVeterinarian_shouldLinkItAndMakeItUnavailable() throws Exception {
        assertTrue(veterinarian.isAvailable());

        VeterinarianEntity result = service.assignVeterinarian(adoption.getId(), veterinarian.getId());

        assertEquals(veterinarian.getId(), result.getId());
        assertEquals(veterinarian.getId(), adoption.getVeterinarian().getId());
        assertFalse(result.isAvailable());
    }

    @Test
    void assignVeterinarian_shouldAcceptCohabitationAdoptions() throws Exception {
        adoption.setStatus("COHABITATION");

        service.assignVeterinarian(adoption.getId(), veterinarian.getId());

        assertEquals(veterinarian.getId(), adoption.getVeterinarian().getId());
    }

    @Test
    void assignVeterinarian_shouldFailWithUnknownAdoption() {
        assertThrows(EntityNotFoundException.class,
                () -> service.assignVeterinarian(NON_EXISTENT_ID, veterinarian.getId()));
    }

    @Test
    void assignVeterinarian_shouldFailWithUnknownVeterinarian() {
        assertThrows(EntityNotFoundException.class,
                () -> service.assignVeterinarian(adoption.getId(), NON_EXISTENT_ID));
    }

    @Test
    void assignVeterinarian_shouldFailIfAdoptionIsNotActive() {
        adoption.setStatus("COMPLETED");

        assertThrows(IllegalOperationException.class,
                () -> service.assignVeterinarian(adoption.getId(), veterinarian.getId()));
    }

    @Test
    void assignVeterinarian_shouldFailIfAdoptionAlreadyHasOne() throws Exception {
        service.assignVeterinarian(adoption.getId(), veterinarian.getId());
        VeterinarianEntity other = buildVeterinarian(shelter);

        assertThrows(IllegalOperationException.class,
                () -> service.assignVeterinarian(adoption.getId(), other.getId()));
    }

    @Test
    void assignVeterinarian_shouldFailIfVeterinarianWorksInAnotherShelter() {
        ShelterEntity otherShelter = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(otherShelter);
        VeterinarianEntity outsider = buildVeterinarian(otherShelter);

        assertThrows(IllegalOperationException.class,
                () -> service.assignVeterinarian(adoption.getId(), outsider.getId()));
    }

    @Test
    void assignVeterinarian_shouldFailIfVeterinarianIsBusyWithAnotherActiveAdoption() throws Exception {
        service.assignVeterinarian(adoption.getId(), veterinarian.getId());
        AdoptionEntity second = buildAdoption("IN_PROGRESS");

        assertThrows(IllegalOperationException.class,
                () -> service.assignVeterinarian(second.getId(), veterinarian.getId()));
    }

    @Test
    void veterinarian_shouldBeAvailableAgainWhenItsAdoptionEnds() throws Exception {
        service.assignVeterinarian(adoption.getId(), veterinarian.getId());

        adoption.setStatus("COMPLETED");

        assertTrue(veterinarian.isAvailable());
        AdoptionEntity second = buildAdoption("IN_PROGRESS");
        service.assignVeterinarian(second.getId(), veterinarian.getId());
        assertEquals(veterinarian.getId(), second.getVeterinarian().getId());
    }

    @Test
    void getVeterinarian_shouldReturnTheAssignedOne() throws Exception {
        service.assignVeterinarian(adoption.getId(), veterinarian.getId());

        assertEquals(veterinarian.getId(), service.getVeterinarian(adoption.getId()).getId());
    }

    @Test
    void getVeterinarian_shouldFailWhenNoneIsAssignedOrAdoptionIsUnknown() {
        assertThrows(EntityNotFoundException.class, () -> service.getVeterinarian(adoption.getId()));
        assertThrows(EntityNotFoundException.class, () -> service.getVeterinarian(NON_EXISTENT_ID));
    }

    @Test
    void removeVeterinarian_shouldUnlinkAndFreeTheVeterinarian() throws Exception {
        service.assignVeterinarian(adoption.getId(), veterinarian.getId());

        service.removeVeterinarian(adoption.getId());

        assertNull(adoption.getVeterinarian());
        assertTrue(veterinarian.isAvailable());
    }

    @Test
    void removeVeterinarian_shouldFailWhenNoneIsAssignedOrAdoptionIsUnknown() {
        assertThrows(EntityNotFoundException.class, () -> service.removeVeterinarian(adoption.getId()));
        assertThrows(EntityNotFoundException.class, () -> service.removeVeterinarian(NON_EXISTENT_ID));
    }
}
