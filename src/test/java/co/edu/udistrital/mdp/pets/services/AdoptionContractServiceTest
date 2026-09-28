package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.AdoptionContractEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
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
@Import(AdoptionContractService.class)

public class AdoptionContractServiceTest {
    @Autowired
    private AdoptionContractService adoptionContractService;

    @Autowired
    private TestEntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();

    private final List<AdoptionContractEntity> contractList = new ArrayList<>();
    private AdoptionEntity adoptionEntity;

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from AdoptionContractEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from AdoptionEntity").executeUpdate();
    }

    private void insertData() {
        adoptionEntity = factory.manufacturePojo(AdoptionEntity.class);
        entityManager.persist(adoptionEntity);

        for (int i = 0; i < 3; i++) {
            AdoptionContractEntity entity = factory.manufacturePojo(AdoptionContractEntity.class);
            entity.setAdoption(adoptionEntity);
            entityManager.persist(entity);
            contractList.add(entity);
        }
    }

    @Test
    void createAdoptionContractTest() throws IllegalOperationException {
        AdoptionContractEntity newEntity = factory.manufacturePojo(AdoptionContractEntity.class);
        newEntity.setAdoption(adoptionEntity);

        AdoptionContractEntity result = adoptionContractService.createContract(newEntity);

        assertNotNull(result);
        assertNotNull(result.getId());

        AdoptionContractEntity entity = entityManager.find(AdoptionContractEntity.class, result.getId());
        assertEquals(newEntity.getId(), entity.getId());
    }

    @Test
    void getAdoptionContractsTest() {
        List<AdoptionContractEntity> list = adoptionContractService.getContracts();
        assertEquals(contractList.size(), list.size());
        for (AdoptionContractEntity entity : list) {
            boolean found = false;
            for (AdoptionContractEntity storedEntity : contractList) {
                if (entity.getId().equals(storedEntity.getId())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }

    @Test
    void getAdoptionContractTest() throws EntityNotFoundException {
        AdoptionContractEntity entity = contractList.getFirst();
        AdoptionContractEntity resultEntity = adoptionContractService.getContract(entity.getId());

        assertNotNull(resultEntity);
        assertEquals(entity.getId(), resultEntity.getId());
    }

    @Test
    void getAdoptionContractInvalidTest() {
        assertThrows(EntityNotFoundException.class, () -> adoptionContractService.getContract(0L));
    }

    @Test
    void updateAdoptionContractTest() throws EntityNotFoundException, IllegalOperationException {
        AdoptionContractEntity entity = contractList.getFirst();
        AdoptionContractEntity pojoEntity = factory.manufacturePojo(AdoptionContractEntity.class);
        pojoEntity.setId(entity.getId());
        pojoEntity.setAdoption(adoptionEntity);

        adoptionContractService.updateContract(entity.getId(), pojoEntity);

        AdoptionContractEntity resp = entityManager.find(AdoptionContractEntity.class, entity.getId());
        assertEquals(pojoEntity.getId(), resp.getId());
    }

    @Test
    void updateAdoptionContractInvalidTest() {
        assertThrows(EntityNotFoundException.class, () -> {
            AdoptionContractEntity pojoEntity = factory.manufacturePojo(AdoptionContractEntity.class);
            pojoEntity.setId(0L);
            adoptionContractService.updateContract(0L, pojoEntity);
        });
    }

    @Test
    void deleteAdoptionContractTest() throws EntityNotFoundException {
        AdoptionContractEntity entity = contractList.getFirst();
        adoptionContractService.deleteContract(entity.getId());
        AdoptionContractEntity deleted = entityManager.find(AdoptionContractEntity.class, entity.getId());
        assertNull(deleted);
    }

    @Test
    void deleteAdoptionContractInvalidTest() {
        assertThrows(EntityNotFoundException.class, () -> adoptionContractService.deleteContract(0L));
    }
}
