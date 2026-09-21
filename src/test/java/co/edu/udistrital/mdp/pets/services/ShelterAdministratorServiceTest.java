package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.ShelterAdministratorEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(ShelterAdministratorService.class)
class ShelterAdministratorServiceTest {

    @Autowired
    private ShelterAdministratorService administratorService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<ShelterEntity> shelterList = new ArrayList<>();
    private List<ShelterAdministratorEntity> administratorList = new ArrayList<>();

    
    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from ShelterEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from ShelterAdministratorEntity").executeUpdate();
        shelterList.clear();
        administratorList.clear();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            ShelterEntity shelterEntity = factory.manufacturePojo(ShelterEntity.class);
            entityManager.persist(shelterEntity);
            shelterList.add(shelterEntity);
        }

        for (int i = 0; i < 3; i++) {
            ShelterAdministratorEntity administratorEntity = factory.manufacturePojo(ShelterAdministratorEntity.class);
            entityManager.persist(administratorEntity);
            administratorList.add(administratorEntity);
        }

        link(administratorList.get(0), shelterList.get(0));
        link(administratorList.get(0), shelterList.get(1));
    }

    /*
     * Asocia ambos lados en memoria porque findById devuelve la misma instancia
     * gestionada, cuyas colecciones no se recargan desde la BD.
     */
    private void link(ShelterAdministratorEntity administrator, ShelterEntity shelter) {
        shelter.getAdministrators().add(administrator);
        administrator.getShelters().add(shelter);
    }

    private boolean containsAdministrator(ShelterEntity shelter, ShelterAdministratorEntity administrator) {
        return shelter.getAdministrators().stream().anyMatch(a -> a.getId().equals(administrator.getId()));
    }

    private ShelterAdministratorEntity buildNewAdministrator() {
        return factory.manufacturePojo(ShelterAdministratorEntity.class);
    }

    /* createAdministrator */

    @Test
    void testCreateAdministrator() throws IllegalOperationException {
        ShelterAdministratorEntity newEntity = buildNewAdministrator();

        ShelterAdministratorEntity result = administratorService.createAdministrator(newEntity);

        assertNotNull(result);
        ShelterAdministratorEntity entity = entityManager.find(ShelterAdministratorEntity.class, result.getId());
        assertEquals(newEntity.getName(), entity.getName());
        assertEquals(newEntity.getEmail(), entity.getEmail());
        assertEquals(newEntity.getRole(), entity.getRole());
    }

    @Test
    void testCreateAdministratorIgnoresGivenShelters() throws IllegalOperationException {
        ShelterAdministratorEntity newEntity = buildNewAdministrator();
        newEntity.getShelters().add(shelterList.get(2));

        ShelterAdministratorEntity result = administratorService.createAdministrator(newEntity);

        assertTrue(result.getShelters().isEmpty());
    }

    @Test
    void testCreateAdministratorWithNullName() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterAdministratorEntity newEntity = buildNewAdministrator();
            newEntity.setName(null);
            administratorService.createAdministrator(newEntity);
        });
    }

    @Test
    void testCreateAdministratorWithBlankName() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterAdministratorEntity newEntity = buildNewAdministrator();
            newEntity.setName("  ");
            administratorService.createAdministrator(newEntity);
        });
    }

    @Test
    void testCreateAdministratorWithNullEmail() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterAdministratorEntity newEntity = buildNewAdministrator();
            newEntity.setEmail(null);
            administratorService.createAdministrator(newEntity);
        });
    }

    @Test
    void testCreateAdministratorWithBlankEmail() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterAdministratorEntity newEntity = buildNewAdministrator();
            newEntity.setEmail("");
            administratorService.createAdministrator(newEntity);
        });
    }

    @Test
    void testCreateAdministratorWithNullRole() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterAdministratorEntity newEntity = buildNewAdministrator();
            newEntity.setRole(null);
            administratorService.createAdministrator(newEntity);
        });
    }

    @Test
    void testCreateAdministratorWithBlankRole() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterAdministratorEntity newEntity = buildNewAdministrator();
            newEntity.setRole(" ");
            administratorService.createAdministrator(newEntity);
        });
    }

    @Test
    void testCreateAdministratorWithExistingEmail() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterAdministratorEntity newEntity = buildNewAdministrator();
            newEntity.setEmail(administratorList.get(0).getEmail());
            administratorService.createAdministrator(newEntity);
        });
    }

    /* getAdministrators / getAdministrator */

    @Test
    void testGetAdministrators() {
        List<ShelterAdministratorEntity> list = administratorService.getAdministrators();
        assertEquals(administratorList.size(), list.size());
        for (ShelterAdministratorEntity administrator : administratorList) {
            assertTrue(list.stream().anyMatch(a -> a.getId().equals(administrator.getId())));
        }
    }

    @Test
    void testGetAdministrator() throws EntityNotFoundException {
        ShelterAdministratorEntity entity = administratorList.get(0);
        ShelterAdministratorEntity resultEntity = administratorService.getAdministrator(entity.getId());
        assertNotNull(resultEntity);
        assertEquals(entity.getId(), resultEntity.getId());
        assertEquals(entity.getName(), resultEntity.getName());
        assertEquals(entity.getEmail(), resultEntity.getEmail());
        assertEquals(entity.getRole(), resultEntity.getRole());
    }

    @Test
    void testGetInvalidAdministrator() {
        assertThrows(EntityNotFoundException.class, () -> {
            administratorService.getAdministrator(0L);
        });
    }

    //updateAdministrator

    @Test
    void testUpdateAdministrator() throws EntityNotFoundException, IllegalOperationException {
        ShelterAdministratorEntity entity = administratorList.get(1);
        ShelterAdministratorEntity pojoEntity = buildNewAdministrator();

        ShelterAdministratorEntity result = administratorService.updateAdministrator(entity.getId(), pojoEntity);

        assertEquals(entity.getId(), result.getId());
        ShelterAdministratorEntity resp = entityManager.find(ShelterAdministratorEntity.class, entity.getId());
        assertEquals(pojoEntity.getName(), resp.getName());
        assertEquals(pojoEntity.getEmail(), resp.getEmail());
        assertEquals(pojoEntity.getRole(), resp.getRole());
    }

    @Test
    void testUpdateAdministratorKeepingSameEmail() throws EntityNotFoundException, IllegalOperationException {
        ShelterAdministratorEntity entity = administratorList.get(1);
        ShelterAdministratorEntity pojoEntity = buildNewAdministrator();
        pojoEntity.setEmail(entity.getEmail());

        administratorService.updateAdministrator(entity.getId(), pojoEntity);

        ShelterAdministratorEntity resp = entityManager.find(ShelterAdministratorEntity.class, entity.getId());
        assertEquals(pojoEntity.getName(), resp.getName());
        assertEquals(entity.getEmail(), resp.getEmail());
    }

    @Test
    void testUpdateAdministratorWithEmailOfAnotherAdministrator() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterAdministratorEntity pojoEntity = buildNewAdministrator();
            pojoEntity.setEmail(administratorList.get(2).getEmail());
            administratorService.updateAdministrator(administratorList.get(1).getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateAdministratorWithNullName() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterAdministratorEntity pojoEntity = buildNewAdministrator();
            pojoEntity.setName(null);
            administratorService.updateAdministrator(administratorList.get(1).getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateAdministratorWithBlankEmail() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterAdministratorEntity pojoEntity = buildNewAdministrator();
            pojoEntity.setEmail("");
            administratorService.updateAdministrator(administratorList.get(1).getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateAdministratorWithNullRole() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterAdministratorEntity pojoEntity = buildNewAdministrator();
            pojoEntity.setRole(null);
            administratorService.updateAdministrator(administratorList.get(1).getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateInvalidAdministrator() {
        assertThrows(EntityNotFoundException.class, () -> {
            ShelterAdministratorEntity pojoEntity = buildNewAdministrator();
            administratorService.updateAdministrator(0L, pojoEntity);
        });
    }

    @Test
    void testUpdateAdministratorKeepsShelters() throws EntityNotFoundException, IllegalOperationException {
        ShelterAdministratorEntity entity = administratorList.get(0);
        ShelterAdministratorEntity pojoEntity = buildNewAdministrator();

        administratorService.updateAdministrator(entity.getId(), pojoEntity);

        entityManager.flush();
        entityManager.clear();
        ShelterAdministratorEntity resp = entityManager.find(ShelterAdministratorEntity.class, entity.getId());
        assertEquals(pojoEntity.getName(), resp.getName());
        assertEquals(2, resp.getShelters().size());
    }

    //deleteAdministrator

    @Test
    void testDeleteAdministrator() throws EntityNotFoundException, IllegalOperationException {
        ShelterAdministratorEntity entity = administratorList.get(1);
        administratorService.deleteAdministrator(entity.getId());
        ShelterAdministratorEntity deleted = entityManager.find(ShelterAdministratorEntity.class, entity.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteInvalidAdministrator() {
        assertThrows(EntityNotFoundException.class, () -> {
            administratorService.deleteAdministrator(0L);
        });
    }

    @Test
    void testDeleteAdministratorWithShelters() {
        assertThrows(IllegalOperationException.class, () -> {
            administratorService.deleteAdministrator(administratorList.get(0).getId());
        });
    }

    /* addShelter */

    @Test
    void testAddShelter() throws EntityNotFoundException, IllegalOperationException {
        ShelterAdministratorEntity administrator = administratorList.get(0);
        ShelterEntity shelter = shelterList.get(2);

        ShelterEntity result = administratorService.addShelter(administrator.getId(), shelter.getId());

        assertNotNull(result);
        assertEquals(shelter.getId(), result.getId());
        assertTrue(containsAdministrator(result, administrator));

        entityManager.flush();
        entityManager.clear();
        ShelterEntity stored = entityManager.find(ShelterEntity.class, shelter.getId());
        assertEquals(1, stored.getAdministrators().size());
        assertTrue(containsAdministrator(stored, administrator));
    }

    @Test
    void testAddShelterWithInvalidAdministrator() {
        assertThrows(EntityNotFoundException.class, () -> {
            administratorService.addShelter(0L, shelterList.get(2).getId());
        });
    }

    @Test
    void testAddShelterWithInvalidShelter() {
        assertThrows(EntityNotFoundException.class, () -> {
            administratorService.addShelter(administratorList.get(0).getId(), 0L);
        });
    }

    @Test
    void testAddShelterAlreadyAssociated() {
        assertThrows(IllegalOperationException.class, () -> {
            administratorService.addShelter(administratorList.get(0).getId(), shelterList.get(0).getId());
        });
    }

    //getShelters

    @Test
    void testGetShelters() throws EntityNotFoundException {
        List<ShelterEntity> list = administratorService.getShelters(administratorList.get(0).getId());

        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(s -> s.getId().equals(shelterList.get(0).getId())));
        assertTrue(list.stream().anyMatch(s -> s.getId().equals(shelterList.get(1).getId())));
    }

    @Test
    void testGetSheltersOfAdministratorWithoutShelters() throws EntityNotFoundException {
        List<ShelterEntity> list = administratorService.getShelters(administratorList.get(1).getId());
        assertTrue(list.isEmpty());
    }

    @Test
    void testGetSheltersWithInvalidAdministrator() {
        assertThrows(EntityNotFoundException.class, () -> {
            administratorService.getShelters(0L);
        });
    }

    //getShelter

    @Test
    void testGetShelter() throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity shelter = shelterList.get(0);

        ShelterEntity result = administratorService.getShelter(administratorList.get(0).getId(), shelter.getId());

        assertNotNull(result);
        assertEquals(shelter.getId(), result.getId());
        assertEquals(shelter.getName(), result.getName());
    }

    @Test
    void testGetShelterNotAssociated() {
        assertThrows(IllegalOperationException.class, () -> {
            administratorService.getShelter(administratorList.get(0).getId(), shelterList.get(2).getId());
        });
    }

    @Test
    void testGetShelterWithInvalidAdministrator() {
        assertThrows(EntityNotFoundException.class, () -> {
            administratorService.getShelter(0L, shelterList.get(0).getId());
        });
    }

    @Test
    void testGetShelterWithInvalidShelter() {
        assertThrows(EntityNotFoundException.class, () -> {
            administratorService.getShelter(administratorList.get(0).getId(), 0L);
        });
    }

    //removeShelter

    @Test
    void testRemoveShelter() throws EntityNotFoundException, IllegalOperationException {
        ShelterAdministratorEntity administrator = administratorList.get(0);
        ShelterEntity shelter = shelterList.get(0);

        administratorService.removeShelter(administrator.getId(), shelter.getId());

        entityManager.flush();
        entityManager.clear();
        ShelterEntity storedShelter = entityManager.find(ShelterEntity.class, shelter.getId());
        assertTrue(storedShelter.getAdministrators().isEmpty());
        ShelterAdministratorEntity storedAdministrator = entityManager.find(ShelterAdministratorEntity.class,
                administrator.getId());
        assertEquals(1, storedAdministrator.getShelters().size());
        assertEquals(shelterList.get(1).getId(), storedAdministrator.getShelters().get(0).getId());
    }

    @Test
    void testRemoveShelterNotAssociated() {
        assertThrows(IllegalOperationException.class, () -> {
            administratorService.removeShelter(administratorList.get(0).getId(), shelterList.get(2).getId());
        });
    }

    @Test
    void testRemoveShelterWithInvalidAdministrator() {
        assertThrows(EntityNotFoundException.class, () -> {
            administratorService.removeShelter(0L, shelterList.get(0).getId());
        });
    }

    @Test
    void testRemoveShelterWithInvalidShelter() {
        assertThrows(EntityNotFoundException.class, () -> {
            administratorService.removeShelter(administratorList.get(0).getId(), 0L);
        });
    }
}
