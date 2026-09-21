package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

import jakarta.persistence.EntityManager;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(AdopterService.class)
class AdopterServiceTest {

    @Autowired
    private AdopterService adopterService;

    @Autowired
    private EntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<AdopterEntity> adopterList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.createQuery("delete from AdopterEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            AdopterEntity entity = factory.manufacturePojo(AdopterEntity.class);
            entityManager.persist(entity);
            adopterList.add(entity);
        }
    }

    @Test
    void createAdopter_shouldPersistAdopter() throws IllegalOperationException {
        AdopterEntity newEntity = factory.manufacturePojo(AdopterEntity.class);
        AdopterEntity result = adopterService.createAdopter(newEntity);

        assertNotNull(result);
        AdopterEntity stored = entityManager.find(AdopterEntity.class, result.getId());
        assertEquals(newEntity.getName(), stored.getName());
        assertEquals(newEntity.getPhone(), stored.getPhone());
    }

    @Test
    void createAdopter_withBlankName_shouldThrowException() {
        AdopterEntity newEntity = factory.manufacturePojo(AdopterEntity.class);
        newEntity.setName("");
        assertThrows(IllegalOperationException.class, () -> adopterService.createAdopter(newEntity));
    }

    @Test
    void createAdopter_withBlankPhone_shouldThrowException() {
        AdopterEntity newEntity = factory.manufacturePojo(AdopterEntity.class);
        newEntity.setPhone("");
        assertThrows(IllegalOperationException.class, () -> adopterService.createAdopter(newEntity));
    }

    @Test
    void getAdopters_shouldReturnAllAdopters() {
        List<AdopterEntity> result = adopterService.getAdopters();
        assertEquals(adopterList.size(), result.size());
    }

    @Test
    void getAdopter_shouldReturnAdopter() throws EntityNotFoundException {
        AdopterEntity existing = adopterList.get(0);
        AdopterEntity result = adopterService.getAdopter(existing.getId());
        assertNotNull(result);
        assertEquals(existing.getName(), result.getName());
    }

    @Test
    void getAdopter_withInvalidId_shouldThrowException() {
        assertThrows(EntityNotFoundException.class, () -> adopterService.getAdopter(0L));
    }

    @Test
    void updateAdopter_shouldUpdateAdopter() throws EntityNotFoundException, IllegalOperationException {
        AdopterEntity existing = adopterList.get(0);
        AdopterEntity updated = factory.manufacturePojo(AdopterEntity.class);
        updated.setId(existing.getId());

        adopterService.updateAdopter(existing.getId(), updated);

        AdopterEntity stored = entityManager.find(AdopterEntity.class, existing.getId());
        assertEquals(updated.getName(), stored.getName());
        assertEquals(updated.getPhone(), stored.getPhone());
    }

    @Test
    void updateAdopter_withInvalidId_shouldThrowException() {
        AdopterEntity updated = factory.manufacturePojo(AdopterEntity.class);
        assertThrows(EntityNotFoundException.class, () -> adopterService.updateAdopter(0L, updated));
    }

    @Test
    void deleteAdopter_shouldDeleteAdopter() throws EntityNotFoundException, IllegalOperationException {
        AdopterEntity existing = adopterList.get(0);
        adopterService.deleteAdopter(existing.getId());
        assertNull(entityManager.find(AdopterEntity.class, existing.getId()) == null);
    }

    @Test
    void deleteAdopter_withInvalidId_shouldThrowException() {
        assertThrows(EntityNotFoundException.class, () -> adopterService.deleteAdopter(0L));
    }
}