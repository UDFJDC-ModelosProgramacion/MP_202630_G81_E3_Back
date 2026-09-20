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

import co.edu.udistrital.mdp.pets.entities.EventCalendarEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterAdministratorEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(ShelterService.class)
class ShelterServiceTest {

    @Autowired
    private ShelterService shelterService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<ShelterEntity> shelterList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from PetEventEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from BreedEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from VeterinarianEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from EventCalendarEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from ShelterEntity").executeUpdate();
        shelterList.clear();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            ShelterEntity shelterEntity = factory.manufacturePojo(ShelterEntity.class);
            entityManager.persist(shelterEntity);
            shelterList.add(shelterEntity);
        }
    }

    //Los helpers asocian también en memoria porque findById devuelve la misma instancia gestionada, cuyas colecciones no se recargan desde la BD
    

    private PetEntity addPet(ShelterEntity shelter) {
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        pet.setShelter(shelter);
        entityManager.persist(pet);
        shelter.getPets().add(pet);
        return pet;
    }

    private VeterinarianEntity addVeterinarian(ShelterEntity shelter) {
        VeterinarianEntity veterinarian = factory.manufacturePojo(VeterinarianEntity.class);
        veterinarian.setShelter(shelter);
        entityManager.persist(veterinarian);
        shelter.getVeterinarians().add(veterinarian);
        return veterinarian;
    }

    private EventCalendarEntity addEvent(ShelterEntity shelter) {
        EventCalendarEntity event = factory.manufacturePojo(EventCalendarEntity.class);
        event.setShelter(shelter);
        entityManager.persist(event);
        shelter.getEvents().add(event);
        return event;
    }

    private ShelterAdministratorEntity addAdministrator(ShelterEntity shelter) {
        ShelterAdministratorEntity administrator = factory.manufacturePojo(ShelterAdministratorEntity.class);
        entityManager.persist(administrator);
        shelter.getAdministrators().add(administrator);
        administrator.getShelters().add(shelter);
        return administrator;
    }

    // createShelter

    @Test
    void testCreateShelter() throws IllegalOperationException {
        ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);

        ShelterEntity result = shelterService.createShelter(newEntity);

        assertNotNull(result);
        ShelterEntity entity = entityManager.find(ShelterEntity.class, result.getId());
        assertEquals(newEntity.getName(), entity.getName());
        assertEquals(newEntity.getCity(), entity.getCity());
    }

    @Test
    void testCreateShelterWithNullName() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
            newEntity.setName(null);
            shelterService.createShelter(newEntity);
        });
    }

    @Test
    void testCreateShelterWithBlankName() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
            newEntity.setName("   ");
            shelterService.createShelter(newEntity);
        });
    }

    @Test
    void testCreateShelterWithNullCity() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
            newEntity.setCity(null);
            shelterService.createShelter(newEntity);
        });
    }

    @Test
    void testCreateShelterWithBlankCity() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
            newEntity.setCity("");
            shelterService.createShelter(newEntity);
        });
    }

    @Test
    void testCreateShelterWithSameNameAndCity() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
            newEntity.setName(shelterList.get(0).getName());
            newEntity.setCity(shelterList.get(0).getCity());
            shelterService.createShelter(newEntity);
        });
    }

    @Test
    void testCreateShelterWithSameNameInAnotherCity() throws IllegalOperationException {
        ShelterEntity newEntity = factory.manufacturePojo(ShelterEntity.class);
        newEntity.setName(shelterList.get(0).getName());
        newEntity.setCity(shelterList.get(0).getCity() + " Norte");

        ShelterEntity result = shelterService.createShelter(newEntity);

        assertNotNull(entityManager.find(ShelterEntity.class, result.getId()));
    }

    // getShelters / getSheltersByCity / getShelter 

    @Test
    void testGetShelters() {
        List<ShelterEntity> list = shelterService.getShelters();
        assertEquals(shelterList.size(), list.size());
        for (ShelterEntity shelter : shelterList) {
            assertTrue(list.stream().anyMatch(s -> s.getId().equals(shelter.getId())));
        }
    }

    @Test
    void testGetSheltersByCity() {
        shelterList.get(0).setCity("Bogota");
        shelterList.get(1).setCity("Bogota");
        shelterList.get(2).setCity("Medellin");

        List<ShelterEntity> list = shelterService.getSheltersByCity("Bogota");

        assertEquals(2, list.size());
        assertTrue(list.stream().allMatch(s -> "Bogota".equals(s.getCity())));
    }

    @Test
    void testGetSheltersByCityWithoutResults() {
        List<ShelterEntity> list = shelterService.getSheltersByCity("Ciudad inexistente");
        assertTrue(list.isEmpty());
    }

    @Test
    void testGetShelter() throws EntityNotFoundException {
        ShelterEntity entity = shelterList.get(0);
        ShelterEntity resultEntity = shelterService.getShelter(entity.getId());
        assertNotNull(resultEntity);
        assertEquals(entity.getId(), resultEntity.getId());
        assertEquals(entity.getName(), resultEntity.getName());
        assertEquals(entity.getCity(), resultEntity.getCity());
    }

    @Test
    void testGetInvalidShelter() {
        assertThrows(EntityNotFoundException.class, () -> {
            shelterService.getShelter(0L);
        });
    }

    // updateShelter

    @Test
    void testUpdateShelter() throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity entity = shelterList.get(0);
        ShelterEntity pojoEntity = factory.manufacturePojo(ShelterEntity.class);

        ShelterEntity result = shelterService.updateShelter(entity.getId(), pojoEntity);

        assertEquals(entity.getId(), result.getId());
        ShelterEntity resp = entityManager.find(ShelterEntity.class, entity.getId());
        assertEquals(pojoEntity.getName(), resp.getName());
        assertEquals(pojoEntity.getCity(), resp.getCity());
    }

    @Test
    void testUpdateShelterKeepingSameNameAndCity() throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity entity = shelterList.get(0);
        ShelterEntity pojoEntity = factory.manufacturePojo(ShelterEntity.class);
        pojoEntity.setName(entity.getName());
        pojoEntity.setCity(entity.getCity());

        shelterService.updateShelter(entity.getId(), pojoEntity);

        ShelterEntity resp = entityManager.find(ShelterEntity.class, entity.getId());
        assertEquals(pojoEntity.getName(), resp.getName());
        assertEquals(pojoEntity.getCity(), resp.getCity());
    }

    @Test
    void testUpdateShelterWithNameAndCityOfAnotherShelter() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity pojoEntity = factory.manufacturePojo(ShelterEntity.class);
            pojoEntity.setName(shelterList.get(1).getName());
            pojoEntity.setCity(shelterList.get(1).getCity());
            shelterService.updateShelter(shelterList.get(0).getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateShelterWithNullName() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity pojoEntity = factory.manufacturePojo(ShelterEntity.class);
            pojoEntity.setName(null);
            shelterService.updateShelter(shelterList.get(0).getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateShelterWithBlankName() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity pojoEntity = factory.manufacturePojo(ShelterEntity.class);
            pojoEntity.setName("");
            shelterService.updateShelter(shelterList.get(0).getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateShelterWithNullCity() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity pojoEntity = factory.manufacturePojo(ShelterEntity.class);
            pojoEntity.setCity(null);
            shelterService.updateShelter(shelterList.get(0).getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateShelterWithBlankCity() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity pojoEntity = factory.manufacturePojo(ShelterEntity.class);
            pojoEntity.setCity("  ");
            shelterService.updateShelter(shelterList.get(0).getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateInvalidShelter() {
        assertThrows(EntityNotFoundException.class, () -> {
            ShelterEntity pojoEntity = factory.manufacturePojo(ShelterEntity.class);
            shelterService.updateShelter(0L, pojoEntity);
        });
    }

    @Test
    void testUpdateShelterKeepsAssociations() throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity entity = shelterList.get(0);
        addPet(entity);
        addVeterinarian(entity);
        addEvent(entity);
        addAdministrator(entity);
        ShelterEntity pojoEntity = factory.manufacturePojo(ShelterEntity.class);

        shelterService.updateShelter(entity.getId(), pojoEntity);

        entityManager.flush();
        entityManager.clear();
        ShelterEntity resp = entityManager.find(ShelterEntity.class, entity.getId());
        assertEquals(pojoEntity.getName(), resp.getName());
        assertEquals(1, resp.getPets().size());
        assertEquals(1, resp.getVeterinarians().size());
        assertEquals(1, resp.getEvents().size());
        assertEquals(1, resp.getAdministrators().size());
    }

    // deleteShelter

    @Test
    void testDeleteShelter() throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity entity = shelterList.get(1);
        shelterService.deleteShelter(entity.getId());
        ShelterEntity deleted = entityManager.find(ShelterEntity.class, entity.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteInvalidShelter() {
        assertThrows(EntityNotFoundException.class, () -> {
            shelterService.deleteShelter(0L);
        });
    }

    @Test
    void testDeleteShelterWithPets() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity entity = shelterList.get(0);
            addPet(entity);
            shelterService.deleteShelter(entity.getId());
        });
    }

    @Test
    void testDeleteShelterWithVeterinarians() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity entity = shelterList.get(0);
            addVeterinarian(entity);
            shelterService.deleteShelter(entity.getId());
        });
    }

    @Test
    void testDeleteShelterWithEvents() {
        assertThrows(IllegalOperationException.class, () -> {
            ShelterEntity entity = shelterList.get(0);
            addEvent(entity);
            shelterService.deleteShelter(entity.getId());
        });
    }

    @Test
    void testDeleteShelterWithAdministrators() throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity entity = shelterList.get(0);
        ShelterAdministratorEntity administrator = addAdministrator(entity);

        shelterService.deleteShelter(entity.getId());
        entityManager.flush();

        assertNull(entityManager.find(ShelterEntity.class, entity.getId()));
        assertNotNull(entityManager.find(ShelterAdministratorEntity.class, administrator.getId()));
    }
}
