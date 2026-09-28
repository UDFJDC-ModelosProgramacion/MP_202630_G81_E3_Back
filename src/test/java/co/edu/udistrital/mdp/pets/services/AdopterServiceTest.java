package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

import jakarta.persistence.EntityManager;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(AdopterService.class)
class AdopterServiceTest {

    private static final Long NON_EXISTENT_ID = 999999L;

    @Autowired
    private AdopterService adopterService;

    @Autowired
    private EntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();

    private final List<AdopterEntity> adopterList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.createQuery("delete from AdopterEntity").executeUpdate();
        adopterList.clear();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            AdopterEntity entity = createValidAdopter(i);
            entityManager.persist(entity);
            adopterList.add(entity);
        }
    }

    private AdopterEntity createValidAdopter(int index) {
        AdopterEntity entity = factory.manufacturePojo(AdopterEntity.class);
        entity.setName("Adopter " + index);
        entity.setPhone("300000000" + index);
        return entity;
    }

    private AdoptionEntity createAdoptionFor(AdopterEntity adopter) {
        ShelterEntity shelter = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(shelter);

        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        pet.setShelter(shelter);
        entityManager.persist(pet);

        AdoptionEntity adoption = factory.manufacturePojo(AdoptionEntity.class);
        adoption.setAdoptionDate(LocalDate.now());
        adoption.setStatus("PENDING");
        adoption.setPet(pet);
        adoption.setAdopter(adopter);
        entityManager.persist(adoption);
        return adoption;
    }



    @Test
    void createAdopter_shouldPersistAdopter() throws IllegalOperationException {
        AdopterEntity newEntity = createValidAdopter(10);

        AdopterEntity result = adopterService.createAdopter(newEntity);

        assertNotNull(result.getId());
        AdopterEntity stored = entityManager.find(AdopterEntity.class, result.getId());
        assertEquals(newEntity.getName(), stored.getName());
        assertEquals(newEntity.getPhone(), stored.getPhone());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    void createAdopter_withInvalidName_shouldThrowException(String invalidName) {
        AdopterEntity newEntity = createValidAdopter(11);
        newEntity.setName(invalidName);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> adopterService.createAdopter(newEntity));
        assertEquals("Name is not valid", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    void createAdopter_withInvalidPhone_shouldThrowException(String invalidPhone) {
        AdopterEntity newEntity = createValidAdopter(12);
        newEntity.setPhone(invalidPhone);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> adopterService.createAdopter(newEntity));
        assertEquals("Phone is not valid", ex.getMessage());
    }


    @Test
    void getAdopters_shouldReturnAllAdopters() {
        List<AdopterEntity> result = adopterService.getAdopters();

        assertEquals(adopterList.size(), result.size());
    }

    @Test
    void getAdopters_withNoAdopters_shouldReturnEmptyList() {
        clearData();

        assertTrue(adopterService.getAdopters().isEmpty());
    }

    @Test
    void getAdopter_shouldReturnAdopter() throws EntityNotFoundException {
        AdopterEntity existing = adopterList.get(0);

        AdopterEntity result = adopterService.getAdopter(existing.getId());

        assertEquals(existing.getId(), result.getId());
        assertEquals(existing.getName(), result.getName());
        assertEquals(existing.getPhone(), result.getPhone());
    }

    @Test
    void getAdopter_withInvalidId_shouldThrowException() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> adopterService.getAdopter(NON_EXISTENT_ID));
        assertEquals("Adopter not found", ex.getMessage());
    }


    @Test
    void updateAdopter_shouldUpdateAdopter() throws EntityNotFoundException, IllegalOperationException {
        AdopterEntity existing = adopterList.get(0);
        AdopterEntity updated = createValidAdopter(13);

        AdopterEntity result = adopterService.updateAdopter(existing.getId(), updated);

        assertEquals(existing.getId(), result.getId());
        assertEquals(updated.getName(), result.getName());
        assertEquals(updated.getPhone(), result.getPhone());

        AdopterEntity stored = entityManager.find(AdopterEntity.class, existing.getId());
        assertEquals(updated.getName(), stored.getName());
        assertEquals(updated.getPhone(), stored.getPhone());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    void updateAdopter_withInvalidName_shouldThrowException(String invalidName) {
        Long existingId = adopterList.get(0).getId();
        AdopterEntity updated = createValidAdopter(14);
        updated.setName(invalidName);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> adopterService.updateAdopter(existingId, updated));
        assertEquals("Name is not valid", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    void updateAdopter_withInvalidPhone_shouldThrowException(String invalidPhone) {
        Long existingId = adopterList.get(0).getId();
        AdopterEntity updated = createValidAdopter(15);
        updated.setPhone(invalidPhone);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> adopterService.updateAdopter(existingId, updated));
        assertEquals("Phone is not valid", ex.getMessage());
    }

    @Test
    void updateAdopter_withInvalidId_shouldThrowException() {
        AdopterEntity updated = createValidAdopter(16);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> adopterService.updateAdopter(NON_EXISTENT_ID, updated));
        assertEquals("Adopter not found", ex.getMessage());
    }



    @Test
    void deleteAdopter_withEmptyAdoptions_shouldDeleteAdopter()
            throws EntityNotFoundException, IllegalOperationException {
        AdopterEntity existing = adopterList.get(0);

        adopterService.deleteAdopter(existing.getId());

        assertNull(entityManager.find(AdopterEntity.class, existing.getId()));
    }

    @Test
    void deleteAdopter_withNullAdoptions_shouldDeleteAdopter()
            throws EntityNotFoundException, IllegalOperationException {
        // La colección se deja en null ANTES de persistir para que la misma
        // instancia (cache de primer nivel) llegue con adoptions == null al servicio.
        AdopterEntity adopter = createValidAdopter(20);
        adopter.setAdoptions(null);
        entityManager.persist(adopter);

        adopterService.deleteAdopter(adopter.getId());

        assertNull(entityManager.find(AdopterEntity.class, adopter.getId()));
    }

    @Test
    void deleteAdopter_withExistingAdoptions_shouldThrowException() {
        AdopterEntity existing = adopterList.get(0);
        AdoptionEntity adoption = createAdoptionFor(existing);
        existing.getAdoptions().add(adoption);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> adopterService.deleteAdopter(existing.getId()));
        assertEquals("Unable to delete adopter with existing adoptions", ex.getMessage());
        assertNotNull(entityManager.find(AdopterEntity.class, existing.getId()));
    }

    @Test
    void deleteAdopter_withInvalidId_shouldThrowException() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> adopterService.deleteAdopter(NON_EXISTENT_ID));
        assertEquals("Adopter not found", ex.getMessage());
    }
}