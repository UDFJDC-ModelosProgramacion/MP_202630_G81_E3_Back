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
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.BreedEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(BreedService.class)
class BreedServiceTest {

    @Autowired
    private BreedService breedService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<PetEntity> petList = new ArrayList<>();
    private BreedEntity existingBreed;

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from BreedEntity");
        entityManager.getEntityManager().createQuery("delete from PetEntity");
        entityManager.getEntityManager().createQuery("delete from ShelterEntity");
    }

    private void insertData() {
        ShelterEntity shelterEntity = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(shelterEntity);

        for (int i = 0; i < 3; i++) {
            PetEntity petEntity = factory.manufacturePojo(PetEntity.class);
            petEntity.setShelter(shelterEntity);
            entityManager.persist(petEntity);
            petList.add(petEntity);
        }

        existingBreed = factory.manufacturePojo(BreedEntity.class);
        existingBreed.setPet(petList.get(0));
        entityManager.persist(existingBreed);
        petList.get(0).setBreed(existingBreed);
    }

    /*  createBreed  */

    @Test
    void testCreateBreed() throws EntityNotFoundException, IllegalOperationException {
        BreedEntity newEntity = factory.manufacturePojo(BreedEntity.class);
        newEntity.setPet(null);

        BreedEntity result = breedService.createBreed(petList.get(1).getId(), newEntity);

        assertNotNull(result);
        BreedEntity entity = entityManager.find(BreedEntity.class, result.getId());
        assertEquals(newEntity.getName(), entity.getName());
        assertEquals(petList.get(1).getId(), entity.getPet().getId());
    }

    @Test
    void testCreateBreedWithNoValidName() {
        assertThrows(IllegalOperationException.class, () -> {
            BreedEntity newEntity = factory.manufacturePojo(BreedEntity.class);
            newEntity.setName(null);
            breedService.createBreed(petList.get(1).getId(), newEntity);
        });
    }

    @Test
    void testCreateBreedWithInvalidPet() {
        assertThrows(EntityNotFoundException.class, () -> {
            BreedEntity newEntity = factory.manufacturePojo(BreedEntity.class);
            breedService.createBreed(0L, newEntity);
        });
    }

    @Test
    void testCreateBreedAlreadyAssociated() {
        assertThrows(IllegalOperationException.class, () -> {
            BreedEntity newEntity = factory.manufacturePojo(BreedEntity.class);
            breedService.createBreed(petList.get(0).getId(), newEntity);
        });
    }

    /*  getBreed  */

    @Test
    void testGetBreed() throws EntityNotFoundException {
        BreedEntity result = breedService.getBreed(petList.get(0).getId());
        assertNotNull(result);
        assertEquals(existingBreed.getId(), result.getId());
        assertEquals(existingBreed.getName(), result.getName());
    }

    @Test
    void testGetBreedNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            breedService.getBreed(petList.get(1).getId());
        });
    }

    @Test
    void testGetBreedInvalidPet() {
        assertThrows(EntityNotFoundException.class, () -> {
            breedService.getBreed(0L);
        });
    }

    /*  updateBreed  */

    @Test
    void testUpdateBreed() throws EntityNotFoundException, IllegalOperationException {
        BreedEntity pojoEntity = factory.manufacturePojo(BreedEntity.class);
        pojoEntity.setPet(null);

        breedService.updateBreed(petList.get(0).getId(), pojoEntity);

        BreedEntity resp = entityManager.find(BreedEntity.class, existingBreed.getId());
        assertEquals(pojoEntity.getName(), resp.getName());
        assertEquals(petList.get(0).getId(), resp.getPet().getId());
    }

    @Test
    void testUpdateBreedWithNoValidName() {
        assertThrows(IllegalOperationException.class, () -> {
            BreedEntity pojoEntity = factory.manufacturePojo(BreedEntity.class);
            pojoEntity.setName("");
            breedService.updateBreed(petList.get(0).getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateBreedNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            BreedEntity pojoEntity = factory.manufacturePojo(BreedEntity.class);
            pojoEntity.setPet(null);
            breedService.updateBreed(petList.get(1).getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateBreedWithoutReassigning() throws EntityNotFoundException, IllegalOperationException {
        BreedEntity pojoEntity = factory.manufacturePojo(BreedEntity.class);
        pojoEntity.setPet(petList.get(0));

        BreedEntity result = breedService.updateBreed(petList.get(0).getId(), pojoEntity);

        assertEquals(petList.get(0).getId(), result.getPet().getId());
    }

    @Test
    void testUpdateBreedReassignPetNotAllowed() {
        assertThrows(IllegalOperationException.class, () -> {
            BreedEntity pojoEntity = factory.manufacturePojo(BreedEntity.class);
            pojoEntity.setPet(petList.get(1));
            breedService.updateBreed(petList.get(0).getId(), pojoEntity);
        });
    }

    /*  deleteBreed */

    @Test
    void testDeleteBreed() throws EntityNotFoundException {
        breedService.deleteBreed(petList.get(0).getId());
        BreedEntity deleted = entityManager.find(BreedEntity.class, existingBreed.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteBreedNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            breedService.deleteBreed(petList.get(1).getId());
        });
    }
}