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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.PetEventEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(PetService.class)
class PetServiceTest {

    @Autowired
    private PetService petService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<PetEntity> petList = new ArrayList<>();
    private List<ShelterEntity> shelterList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from PetEventEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PetEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from ShelterEntity").executeUpdate();
        petList.clear();
        shelterList.clear();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            ShelterEntity shelterEntity = factory.manufacturePojo(ShelterEntity.class);
            entityManager.persist(shelterEntity);
            shelterList.add(shelterEntity);
        }
        for (int i = 0; i < 3; i++) {
            PetEntity petEntity = factory.manufacturePojo(PetEntity.class);
            petEntity.setShelter(shelterList.get(0));
            entityManager.persist(petEntity);
            petList.add(petEntity);
        }
    }

    private PetEntity buildValidNewPet() {
        PetEntity newEntity = factory.manufacturePojo(PetEntity.class);
        newEntity.setShelter(shelterList.get(0));

        PetEventEntity arrival = new PetEventEntity();
        arrival.setEventType("ARRIVAL");
        arrival.setDate(new java.sql.Date(System.currentTimeMillis()));
        arrival.setDescription("Fue rescatado de la calle");

        List<PetEventEntity> events = new ArrayList<>();
        events.add(arrival);
        newEntity.setEvents(events);

        return newEntity;
    }

    /* ===================== createPet ===================== */

    @Test
    void testCreatePet() throws EntityNotFoundException, IllegalOperationException {
        PetEntity newEntity = buildValidNewPet();

        PetEntity result = petService.createPet(newEntity);

        assertNotNull(result);
        PetEntity entity = entityManager.find(PetEntity.class, result.getId());
        assertEquals(newEntity.getName(), entity.getName());
        assertEquals(shelterList.get(0).getId(), entity.getShelter().getId());
        assertEquals(1, entity.getEvents().size());
        assertEquals("ARRIVAL", entity.getEvents().get(0).getEventType());
    }

    @Test
    void testCreatePetWithNoValidName() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity newEntity = buildValidNewPet();
            newEntity.setName(null);
            petService.createPet(newEntity);
        });
    }

    @Test
    void testCreatePetWithNoValidShelter() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity newEntity = buildValidNewPet();
            newEntity.setShelter(null);
            petService.createPet(newEntity);
        });
    }

    @Test
    void testCreatePetWithInvalidShelter() {
        assertThrows(EntityNotFoundException.class, () -> {
            PetEntity newEntity = buildValidNewPet();
            ShelterEntity shelterEntity = new ShelterEntity();
            shelterEntity.setId(0L);
            newEntity.setShelter(shelterEntity);
            petService.createPet(newEntity);
        });
    }

    @Test
    void testCreatePetWithoutArrivalEvent() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity newEntity = buildValidNewPet();
            newEntity.setEvents(new ArrayList<>());
            petService.createPet(newEntity);
        });
    }

    @Test
    void testCreatePetWithNonArrivalInitialEvent() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity newEntity = buildValidNewPet();
            newEntity.getEvents().get(0).setEventType("SURGERY");
            petService.createPet(newEntity);
        });
    }

    @Test
    void testCreatePetDefaultStatus() throws EntityNotFoundException, IllegalOperationException {
        PetEntity newEntity = buildValidNewPet();
        newEntity.setStatus(null);

        PetEntity result = petService.createPet(newEntity);

        assertEquals("AVAILABLE", result.getStatus());
    }

    @Test
    void testCreatePetKeepsGivenStatus() throws EntityNotFoundException, IllegalOperationException {
        PetEntity newEntity = buildValidNewPet();
        newEntity.setStatus("IN_TREATMENT");

        PetEntity result = petService.createPet(newEntity);

        assertEquals("IN_TREATMENT", result.getStatus());
    }

    /* ===================== getPets / getPet ===================== */

    @Test
    void testGetPets() {
        List<PetEntity> list = petService.getPets();
        assertEquals(petList.size(), list.size());
    }

    @Test
    void testGetPet() throws EntityNotFoundException {
        PetEntity entity = petList.get(0);
        PetEntity resultEntity = petService.getPet(entity.getId());
        assertNotNull(resultEntity);
        assertEquals(entity.getId(), resultEntity.getId());
        assertEquals(entity.getName(), resultEntity.getName());
    }

    @Test
    void testGetInvalidPet() {
        assertThrows(EntityNotFoundException.class, () -> {
            petService.getPet(0L);
        });
    }

    /* ===================== getAvailablePetsByFilters ===================== */

    @Test
    void testGetAvailablePetsByFilters() {
        PetEntity entity = petList.get(0);
        entity.setStatus("AVAILABLE");
        entity.setActivityLevel("HIGH");

        List<PetEntity> result = petService.getAvailablePetsByFilters("HIGH", null);
        assertTrue(result.stream().anyMatch(p -> p.getId().equals(entity.getId())));
    }

    @Test
    void testGetAvailablePetsByFiltersNoMatch() {
        PetEntity entity = petList.get(0);
        entity.setStatus("AVAILABLE");
        entity.setActivityLevel("LOW");

        List<PetEntity> result = petService.getAvailablePetsByFilters("HIGH", null);
        assertTrue(result.stream().noneMatch(p -> p.getId().equals(entity.getId())));
    }

    /* ===================== updatePet ===================== */

    @Test
    void testUpdatePet() throws EntityNotFoundException, IllegalOperationException {
        PetEntity entity = petList.get(0);
        PetEntity pojoEntity = factory.manufacturePojo(PetEntity.class);
        pojoEntity.setId(entity.getId());
        pojoEntity.setShelter(entity.getShelter());

        petService.updatePet(entity.getId(), pojoEntity);

        PetEntity resp = entityManager.find(PetEntity.class, entity.getId());
        assertEquals(pojoEntity.getName(), resp.getName());
        assertEquals(pojoEntity.getStatus(), resp.getStatus());
    }

    @Test
    void testUpdatePetInvalid() {
        assertThrows(EntityNotFoundException.class, () -> {
            PetEntity pojoEntity = factory.manufacturePojo(PetEntity.class);
            pojoEntity.setId(0L);
            petService.updatePet(0L, pojoEntity);
        });
    }

    @Test
    void testUpdatePetChangeShelterWithoutActiveAdoption()
            throws EntityNotFoundException, IllegalOperationException {
        PetEntity entity = petList.get(0);
        PetEntity pojoEntity = factory.manufacturePojo(PetEntity.class);
        pojoEntity.setId(entity.getId());
        pojoEntity.setShelter(shelterList.get(1));

        PetEntity result = petService.updatePet(entity.getId(), pojoEntity);

        assertEquals(shelterList.get(1).getId(), result.getShelter().getId());
    }

    @Test
    void testUpdatePetShelterWithActiveAdoption() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity entity = petList.get(0);

            AdopterEntity adopterEntity = factory.manufacturePojo(AdopterEntity.class);
            entityManager.persist(adopterEntity);

            AdoptionEntity adoptionEntity = new AdoptionEntity();
            adoptionEntity.setAdoptionDate(LocalDate.now());
            adoptionEntity.setStatus("IN_PROGRESS");
            adoptionEntity.setPet(entity);
            adoptionEntity.setAdopter(adopterEntity);
            entityManager.persist(adoptionEntity);

            PetEntity pojoEntity = factory.manufacturePojo(PetEntity.class);
            pojoEntity.setId(entity.getId());
            pojoEntity.setShelter(shelterList.get(1));

            petService.updatePet(entity.getId(), pojoEntity);
        });
    }

    /* ===================== deletePet ===================== */

    @Test
    void testDeletePet() throws EntityNotFoundException, IllegalOperationException {
        PetEntity entity = petList.get(1);
        petService.deletePet(entity.getId());
        PetEntity deleted = entityManager.find(PetEntity.class, entity.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteInvalidPet() {
        assertThrows(EntityNotFoundException.class, () -> {
            petService.deletePet(0L);
        });
    }

    @Test
    void testDeletePetWithAdoptionHistory() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEntity entity = petList.get(0);

            AdopterEntity adopterEntity = factory.manufacturePojo(AdopterEntity.class);
            entityManager.persist(adopterEntity);

            AdoptionEntity adoptionEntity = new AdoptionEntity();
            adoptionEntity.setAdoptionDate(LocalDate.now());
            adoptionEntity.setStatus("COMPLETED");
            adoptionEntity.setPet(entity);
            adoptionEntity.setAdopter(adopterEntity);
            entityManager.persist(adoptionEntity);

            petService.deletePet(entity.getId());
        });
    }
}