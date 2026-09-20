package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.CaseDevolutionEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(CaseDevolutionService.class)
class CaseDevolutionServiceTest {

    private static final LocalDate ADOPTION_DATE = LocalDate.now().minusDays(30);

    @Autowired
    private CaseDevolutionService caseDevolutionService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<AdoptionEntity> adoptionList = new ArrayList<>();
    private CaseDevolutionEntity existingDevolution;

    /*
     * adoptionList.get(0) ya tiene una devolución registrada (existingDevolution);
     * adoptionList.get(1) no tiene devolución.
     */
    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from CaseDevolutionEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from AdoptionEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from BreedEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from AdopterEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from ShelterEntity").executeUpdate();
        adoptionList.clear();
    }

    private void insertData() {
        ShelterEntity shelterEntity = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(shelterEntity);

        PetEntity petEntity = factory.manufacturePojo(PetEntity.class);
        petEntity.setShelter(shelterEntity);
        entityManager.persist(petEntity);

        AdopterEntity adopterEntity = factory.manufacturePojo(AdopterEntity.class);
        entityManager.persist(adopterEntity);

        for (int i = 0; i < 2; i++) {
            AdoptionEntity adoptionEntity = new AdoptionEntity();
            adoptionEntity.setAdoptionDate(ADOPTION_DATE);
            adoptionEntity.setStatus("COMPLETED");
            adoptionEntity.setPet(petEntity);
            adoptionEntity.setAdopter(adopterEntity);
            entityManager.persist(adoptionEntity);
            adoptionList.add(adoptionEntity);
        }

        existingDevolution = buildDevolution();
        existingDevolution.setAdoption(adoptionList.get(0));
        entityManager.persist(existingDevolution);
    }

    private CaseDevolutionEntity buildDevolution() {
        CaseDevolutionEntity devolution = factory.manufacturePojo(CaseDevolutionEntity.class);
        devolution.setDate(LocalDate.now());
        return devolution;
    }

    // createCaseDevolution

    @Test
    void testCreateCaseDevolution() throws EntityNotFoundException, IllegalOperationException {
        CaseDevolutionEntity newEntity = buildDevolution();

        CaseDevolutionEntity result = caseDevolutionService.createCaseDevolution(adoptionList.get(1).getId(), newEntity);

        assertNotNull(result);
        CaseDevolutionEntity entity = entityManager.find(CaseDevolutionEntity.class, result.getId());
        assertEquals(newEntity.getReason(), entity.getReason());
        assertEquals(newEntity.getDate(), entity.getDate());
        assertEquals(adoptionList.get(1).getId(), entity.getAdoption().getId());
    }

    @Test
    void testCreateCaseDevolutionWithNullReason() {
        assertThrows(IllegalOperationException.class, () -> {
            CaseDevolutionEntity newEntity = buildDevolution();
            newEntity.setReason(null);
            caseDevolutionService.createCaseDevolution(adoptionList.get(1).getId(), newEntity);
        });
    }

    @Test
    void testCreateCaseDevolutionWithBlankReason() {
        assertThrows(IllegalOperationException.class, () -> {
            CaseDevolutionEntity newEntity = buildDevolution();
            newEntity.setReason("  ");
            caseDevolutionService.createCaseDevolution(adoptionList.get(1).getId(), newEntity);
        });
    }

    @Test
    void testCreateCaseDevolutionWithNullDate() {
        assertThrows(IllegalOperationException.class, () -> {
            CaseDevolutionEntity newEntity = buildDevolution();
            newEntity.setDate(null);
            caseDevolutionService.createCaseDevolution(adoptionList.get(1).getId(), newEntity);
        });
    }

    @Test
    void testCreateCaseDevolutionWithInvalidAdoption() {
        assertThrows(EntityNotFoundException.class, () -> {
            CaseDevolutionEntity newEntity = buildDevolution();
            caseDevolutionService.createCaseDevolution(0L, newEntity);
        });
    }

    @Test
    void testCreateCaseDevolutionAlreadyRegistered() {
        assertThrows(IllegalOperationException.class, () -> {
            CaseDevolutionEntity newEntity = buildDevolution();
            caseDevolutionService.createCaseDevolution(adoptionList.get(0).getId(), newEntity);
        });
    }

    @Test
    void testCreateCaseDevolutionBeforeAdoptionDate() {
        assertThrows(IllegalOperationException.class, () -> {
            CaseDevolutionEntity newEntity = buildDevolution();
            newEntity.setDate(ADOPTION_DATE.minusDays(1));
            caseDevolutionService.createCaseDevolution(adoptionList.get(1).getId(), newEntity);
        });
    }

    @Test
    void testCreateCaseDevolutionOnAdoptionDate() throws EntityNotFoundException, IllegalOperationException {
        CaseDevolutionEntity newEntity = buildDevolution();
        newEntity.setDate(ADOPTION_DATE);

        CaseDevolutionEntity result = caseDevolutionService.createCaseDevolution(adoptionList.get(1).getId(), newEntity);

        assertNotNull(entityManager.find(CaseDevolutionEntity.class, result.getId()));
    }

    @Test
    void testCreateCaseDevolutionWithAdoptionWithoutDate() throws EntityNotFoundException, IllegalOperationException {
        adoptionList.get(1).setAdoptionDate(null);
        CaseDevolutionEntity newEntity = buildDevolution();
        newEntity.setDate(ADOPTION_DATE.minusYears(1));

        CaseDevolutionEntity result = caseDevolutionService.createCaseDevolution(adoptionList.get(1).getId(), newEntity);

        assertNotNull(entityManager.find(CaseDevolutionEntity.class, result.getId()));
    }

    //getCaseDevolution

    @Test
    void testGetCaseDevolution() throws EntityNotFoundException {
        CaseDevolutionEntity result = caseDevolutionService.getCaseDevolution(adoptionList.get(0).getId());

        assertNotNull(result);
        assertEquals(existingDevolution.getId(), result.getId());
        assertEquals(existingDevolution.getReason(), result.getReason());
        assertEquals(existingDevolution.getDate(), result.getDate());
    }

    @Test
    void testGetCaseDevolutionInvalidAdoption() {
        assertThrows(EntityNotFoundException.class, () -> {
            caseDevolutionService.getCaseDevolution(0L);
        });
    }

    @Test
    void testGetCaseDevolutionNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            caseDevolutionService.getCaseDevolution(adoptionList.get(1).getId());
        });
    }

    //updateCaseDevolution

    @Test
    void testUpdateCaseDevolution() throws EntityNotFoundException, IllegalOperationException {
        CaseDevolutionEntity pojoEntity = buildDevolution();
        pojoEntity.setDate(LocalDate.now().minusDays(2));

        CaseDevolutionEntity result = caseDevolutionService.updateCaseDevolution(adoptionList.get(0).getId(), pojoEntity);

        assertEquals(existingDevolution.getId(), result.getId());
        CaseDevolutionEntity resp = entityManager.find(CaseDevolutionEntity.class, existingDevolution.getId());
        assertEquals(pojoEntity.getReason(), resp.getReason());
        assertEquals(pojoEntity.getDate(), resp.getDate());
        assertEquals(adoptionList.get(0).getId(), resp.getAdoption().getId());
    }

    @Test
    void testUpdateCaseDevolutionWithoutReassigning() throws EntityNotFoundException, IllegalOperationException {
        CaseDevolutionEntity pojoEntity = buildDevolution();
        pojoEntity.setAdoption(adoptionList.get(0));

        CaseDevolutionEntity result = caseDevolutionService.updateCaseDevolution(adoptionList.get(0).getId(), pojoEntity);

        assertEquals(adoptionList.get(0).getId(), result.getAdoption().getId());
    }

    @Test
    void testUpdateCaseDevolutionReassignAdoptionNotAllowed() {
        assertThrows(IllegalOperationException.class, () -> {
            CaseDevolutionEntity pojoEntity = buildDevolution();
            pojoEntity.setAdoption(adoptionList.get(1));
            caseDevolutionService.updateCaseDevolution(adoptionList.get(0).getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateCaseDevolutionWithNullReason() {
        assertThrows(IllegalOperationException.class, () -> {
            CaseDevolutionEntity pojoEntity = buildDevolution();
            pojoEntity.setReason(null);
            caseDevolutionService.updateCaseDevolution(adoptionList.get(0).getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateCaseDevolutionWithBlankReason() {
        assertThrows(IllegalOperationException.class, () -> {
            CaseDevolutionEntity pojoEntity = buildDevolution();
            pojoEntity.setReason("");
            caseDevolutionService.updateCaseDevolution(adoptionList.get(0).getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateCaseDevolutionWithNullDate() {
        assertThrows(IllegalOperationException.class, () -> {
            CaseDevolutionEntity pojoEntity = buildDevolution();
            pojoEntity.setDate(null);
            caseDevolutionService.updateCaseDevolution(adoptionList.get(0).getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateCaseDevolutionInvalidAdoption() {
        assertThrows(EntityNotFoundException.class, () -> {
            CaseDevolutionEntity pojoEntity = buildDevolution();
            caseDevolutionService.updateCaseDevolution(0L, pojoEntity);
        });
    }

    @Test
    void testUpdateCaseDevolutionNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            CaseDevolutionEntity pojoEntity = buildDevolution();
            caseDevolutionService.updateCaseDevolution(adoptionList.get(1).getId(), pojoEntity);
        });
    }

    // deleteCaseDevolution 

    @Test
    void testDeleteCaseDevolution() throws EntityNotFoundException {
        caseDevolutionService.deleteCaseDevolution(adoptionList.get(0).getId());

        CaseDevolutionEntity deleted = entityManager.find(CaseDevolutionEntity.class, existingDevolution.getId());
        assertNull(deleted);
        assertNotNull(entityManager.find(AdoptionEntity.class, adoptionList.get(0).getId()));
    }

    @Test
    void testDeleteCaseDevolutionInvalidAdoption() {
        assertThrows(EntityNotFoundException.class, () -> {
            caseDevolutionService.deleteCaseDevolution(0L);
        });
    }

    @Test
    void testDeleteCaseDevolutionNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            caseDevolutionService.deleteCaseDevolution(adoptionList.get(1).getId());
        });
    }
}
