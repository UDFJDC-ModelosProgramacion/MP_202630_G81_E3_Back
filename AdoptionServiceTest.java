package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@Import(AdoptionService.class)
public class AdoptionServiceTest {
    @Autowired
    private AdoptionService adoptionService;

    @Autowired
    private TestEntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();

    private final List<AdoptionEntity> adoptionList = new ArrayList<>();
    private AdopterEntity adopterEntity;
    private PetEntity petEntity;

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from AdoptionEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from AdopterEntity").executeUpdate();
    }

    private void insertData() {
        adopterEntity = factory.manufacturePojo(AdopterEntity.class);
        entityManager.persist(adopterEntity);

        petEntity = factory.manufacturePojo(PetEntity.class);
        entityManager.persist(petEntity);

        for (int i = 0; i < 3; i++) {
            AdoptionEntity entity = factory.manufacturePojo(AdoptionEntity.class);
            entity.setAdopter(adopterEntity);
            entity.setPet(petEntity);
            entityManager.persist(entity);
            adoptionList.add(entity);
        }
    }

    @Test
    void createAdoptionTest() throws IllegalOperationException{
        AdoptionEntity newEntity = factory.manufacturePojo(AdoptionEntity.class);
        newEntity.setAdopter(adopterEntity);
        newEntity.setPet(petEntity);

        AdoptionEntity result = adoptionService.createAdoption(newEntity);

        assertNotNull(result);
        assertNotNull(result.getId());

        AdoptionEntity entity = entityManager.find(AdoptionEntity.class, result.getId());
        assertEquals(newEntity.getId(), entity.getId());
    }

    @Test
    void getAdoptionsTest() {
        List<AdoptionEntity> list = adoptionService.getAdoptions();
        assertEquals(adoptionList.size(), list.size());
        for (AdoptionEntity entity : list) {
            boolean found = false;
            for (AdoptionEntity storedEntity : adoptionList) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    void getAdoptionTest() throws EntityNotFoundException {
        AdoptionEntity entity = adoptionList.getFirst();
        AdoptionEntity resultEntity = adoptionService.getAdoption(entity.getId());

        assertNotNull(resultEntity);
        assertEquals(entity.getId(), resultEntity.getId());
    }

    @Test
    void getAdoptionInvalidTest() {
        assertThrows(EntityNotFoundException.class, () -> adoptionService.getAdoption(0L));
    }

    @Test
    void updateAdoptionTest() throws EntityNotFoundException, IllegalOperationException {
        AdoptionEntity entity = adoptionList.getFirst();
        AdoptionEntity pojoEntity = factory.manufacturePojo(AdoptionEntity.class);
        pojoEntity.setId(entity.getId());
        pojoEntity.setAdopter(adopterEntity);
        pojoEntity.setPet(petEntity);

        adoptionService.updateAdoption(entity.getId(), pojoEntity);

        AdoptionEntity resp = entityManager.find(AdoptionEntity.class, entity.getId());
        assertEquals(pojoEntity.getId(), resp.getId());
    }

    @Test
    void updateAdoptionInvalidTest() {
        assertThrows(EntityNotFoundException.class, () -> {
            AdoptionEntity pojoEntity = factory.manufacturePojo(AdoptionEntity.class);
            pojoEntity.setId(0L);
            adoptionService.updateAdoption(0L, pojoEntity);
        });
    }

    @Test
    void deleteAdoptionTest() throws EntityNotFoundException {
        AdoptionEntity entity = adoptionList.getFirst();
        adoptionService.deleteAdoption(entity.getId());
        AdoptionEntity deleted = entityManager.find(AdoptionEntity.class, entity.getId());
        assertNull(deleted);
    }

    @Test
    void deleteAdoptionInvalidTest() {
        assertThrows(EntityNotFoundException.class, () -> adoptionService.deleteAdoption(0L));
    }
}
