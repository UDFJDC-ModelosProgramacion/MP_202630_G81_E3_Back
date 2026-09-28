package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.AdoptionRequirementEntity;
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
@Import(AdoptionRequirementService.class)

public class AdoptionRequirementServiceTest {
    @Autowired
    private AdoptionRequirementService adoptionRequirementService;

    @Autowired
    private TestEntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();

    private final List<AdoptionRequirementEntity> requirementList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from AdoptionRequirementEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            AdoptionRequirementEntity entity = factory.manufacturePojo(AdoptionRequirementEntity.class);
            entityManager.persist(entity);
            requirementList.add(entity);
        }
    }

    @Test
    void createAdoptionRequirementTest() throws IllegalOperationException {
        AdoptionRequirementEntity newEntity = factory.manufacturePojo(AdoptionRequirementEntity.class);

        AdoptionRequirementEntity result = adoptionRequirementService.createRequirement(newEntity);

        assertNotNull(result);
        assertNotNull(result.getId());

        AdoptionRequirementEntity entity = entityManager.find(AdoptionRequirementEntity.class, result.getId());
        assertEquals(newEntity.getId(), entity.getId());
    }

    @Test
    void getAdoptionRequirementsTest() {
        List<AdoptionRequirementEntity> list = adoptionRequirementService.getRequirements();
        assertEquals(requirementList.size(), list.size());
        for (AdoptionRequirementEntity entity : list) {
            boolean found = false;
            for (AdoptionRequirementEntity storedEntity : requirementList) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    void getAdoptionRequirementTest() throws EntityNotFoundException {
        AdoptionRequirementEntity entity = requirementList.getFirst();
        AdoptionRequirementEntity resultEntity = adoptionRequirementService.getRequirement(entity.getId());

        assertNotNull(resultEntity);
        assertEquals(entity.getId(), resultEntity.getId());
    }

    @Test
    void getAdoptionRequirementInvalidTest() {
        assertThrows(EntityNotFoundException.class, () -> adoptionRequirementService.getRequirement(0L));
    }

    @Test
    void updateAdoptionRequirementTest() throws EntityNotFoundException, IllegalOperationException {
        AdoptionRequirementEntity entity = requirementList.getFirst();
        AdoptionRequirementEntity pojoEntity = factory.manufacturePojo(AdoptionRequirementEntity.class);
        pojoEntity.setId(entity.getId());

        adoptionRequirementService.updateRequirement(entity.getId(), pojoEntity);

        AdoptionRequirementEntity resp = entityManager.find(AdoptionRequirementEntity.class, entity.getId());
        assertEquals(pojoEntity.getId(), resp.getId());
    }

    @Test
    void updateAdoptionRequirementInvalidTest() {
        assertThrows(EntityNotFoundException.class, () -> {
            AdoptionRequirementEntity pojoEntity = factory.manufacturePojo(AdoptionRequirementEntity.class);
            pojoEntity.setId(0L);
            adoptionRequirementService.updateRequirement(0L, pojoEntity);
        });
    }

    @Test
    void deleteAdoptionRequirementTest() throws EntityNotFoundException {
        AdoptionRequirementEntity entity = requirementList.getFirst();
        adoptionRequirementService.deleteRequirement(entity.getId());
        AdoptionRequirementEntity deleted = entityManager.find(AdoptionRequirementEntity.class, entity.getId());
        assertNull(deleted);
    }

    @Test
    void deleteAdoptionRequirementInvalidTest() {
        assertThrows(EntityNotFoundException.class, () -> adoptionRequirementService.deleteRequirement(0L));
    }
}
