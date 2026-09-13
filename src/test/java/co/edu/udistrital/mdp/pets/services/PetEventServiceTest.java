package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.PetEventEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(PetEventService.class)
class PetEventServiceTest {

    private static final long ONE_DAY_MS = 24L * 60 * 60 * 1000;

    @Autowired
    private PetEventService petEventService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<PetEntity> petList = new ArrayList<>();
    private PetEventEntity arrivalEvent;

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from PetEventEntity");
        entityManager.getEntityManager().createQuery("delete from PetEntity");
        entityManager.getEntityManager().createQuery("delete from ShelterEntity");
    }

    private void insertData() {
        ShelterEntity shelterEntity = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(shelterEntity);

        for (int i = 0; i < 2; i++) {
            PetEntity petEntity = factory.manufacturePojo(PetEntity.class);
            petEntity.setShelter(shelterEntity);
            entityManager.persist(petEntity);
            petList.add(petEntity);
        }

        arrivalEvent = new PetEventEntity();
        arrivalEvent.setEventType(PetEventService.ARRIVAL_EVENT_TYPE);
        arrivalEvent.setDate(new Date(System.currentTimeMillis()));
        arrivalEvent.setDescription("Street rescue");
        arrivalEvent.setPet(petList.get(0));
        entityManager.persist(arrivalEvent);
    }

    /*  createEvent  */

    @Test
    void testCreateEvent() throws EntityNotFoundException, IllegalOperationException {
        PetEventEntity newEntity = factory.manufacturePojo(PetEventEntity.class);
        newEntity.setEventType("SURGERY");
        newEntity.setDate(new Date(System.currentTimeMillis() + ONE_DAY_MS));

        PetEventEntity result = petEventService.createEvent(petList.get(0).getId(), newEntity);

        assertNotNull(result);
        PetEventEntity entity = entityManager.find(PetEventEntity.class, result.getId());
        assertEquals("SURGERY", entity.getEventType());
        assertEquals(petList.get(0).getId(), entity.getPet().getId());
    }

    @Test
    void testCreateEventWithNoValidEventType() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEventEntity newEntity = factory.manufacturePojo(PetEventEntity.class);
            newEntity.setEventType(null);
            petEventService.createEvent(petList.get(0).getId(), newEntity);
        });
    }

    @Test
    void testCreateEventWithValidDate() throws EntityNotFoundException, IllegalOperationException {
        PetEventEntity newEntity = factory.manufacturePojo(PetEventEntity.class);
        newEntity.setEventType("ILLNESS");
        newEntity.setDate(new Date(System.currentTimeMillis()));

        PetEventEntity result = petEventService.createEvent(petList.get(0).getId(), newEntity);

        assertNotNull(result.getDate());
    }

    @Test
    void testCreateEventWithNoValidDate() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEventEntity newEntity = factory.manufacturePojo(PetEventEntity.class);
            newEntity.setEventType("ILLNESS");
            newEntity.setDate(null);
            petEventService.createEvent(petList.get(0).getId(), newEntity);
        });
    }

    @Test
    void testCreateEventNonArrivalAllowed() throws EntityNotFoundException, IllegalOperationException {
        PetEventEntity newEntity = factory.manufacturePojo(PetEventEntity.class);
        newEntity.setEventType("ACCIDENT");
        newEntity.setDate(new Date(System.currentTimeMillis()));

        PetEventEntity result = petEventService.createEvent(petList.get(0).getId(), newEntity);

        assertEquals("ACCIDENT", result.getEventType());
    }

    @Test
    void testCreateEventArrivalNotAllowed() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEventEntity newEntity = factory.manufacturePojo(PetEventEntity.class);
            newEntity.setEventType("ARRIVAL");
            newEntity.setDate(new Date(System.currentTimeMillis()));
            petEventService.createEvent(petList.get(0).getId(), newEntity);
        });
    }

    @Test
    void testCreateEventInvalidPet() {
        assertThrows(EntityNotFoundException.class, () -> {
            PetEventEntity newEntity = factory.manufacturePojo(PetEventEntity.class);
            newEntity.setEventType("ACCIDENT");
            newEntity.setDate(new Date(System.currentTimeMillis()));
            petEventService.createEvent(0L, newEntity);
        });
    }

    /*  getEvents / getEventsByType  */

    @Test
    void testGetEvents() throws EntityNotFoundException {
        List<PetEventEntity> result = petEventService.getEvents(petList.get(0).getId());
        assertEquals(1, result.size());
        assertEquals(arrivalEvent.getId(), result.get(0).getId());
    }

    @Test
    void testGetEventsInvalidPet() {
        assertThrows(EntityNotFoundException.class, () -> {
            petEventService.getEvents(0L);
        });
    }

    @Test
    void testGetEventsByType() throws EntityNotFoundException {
        List<PetEventEntity> result = petEventService.getEventsByType(petList.get(0).getId(), "ARRIVAL");
        assertEquals(1, result.size());
    }

    @Test
    void testGetEventsByTypeInvalidPet() {
        assertThrows(EntityNotFoundException.class, () -> {
            petEventService.getEventsByType(0L, "ARRIVAL");
        });
    }

    /*  getEvent  */

    @Test
    void testGetEvent() throws EntityNotFoundException, IllegalOperationException {
        PetEventEntity result = petEventService.getEvent(petList.get(0).getId(), arrivalEvent.getId());
        assertNotNull(result);
        assertEquals(arrivalEvent.getId(), result.getId());
    }

    @Test
    void testGetEventNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            petEventService.getEvent(petList.get(0).getId(), 0L);
        });
    }

    @Test
    void testGetEventNotBelongingToPet() {
        assertThrows(IllegalOperationException.class, () -> {
            petEventService.getEvent(petList.get(1).getId(), arrivalEvent.getId());
        });
    }

    /*  updateEvent  */

    private PetEventEntity persistVaccinationEvent(PetEntity pet, long offsetMillis) {
        PetEventEntity event = new PetEventEntity();
        event.setEventType("VACCINATION");
        event.setDate(new Date(System.currentTimeMillis() + offsetMillis));
        event.setDescription("Vacuna antirrábica");
        event.setPet(pet);
        entityManager.persist(event);
        return event;
    }

    @Test
    void testUpdateEvent() throws EntityNotFoundException, IllegalOperationException {
        PetEventEntity newEvent = persistVaccinationEvent(petList.get(0), ONE_DAY_MS);

        PetEventEntity pojoEntity = new PetEventEntity();
        pojoEntity.setEventType("VACCINATION");
        pojoEntity.setDate(new Date(System.currentTimeMillis() + 2 * ONE_DAY_MS));
        pojoEntity.setDescription("Vacuna antirrábica reprogramada");

        petEventService.updateEvent(petList.get(0).getId(), newEvent.getId(), pojoEntity);

        PetEventEntity resp = entityManager.find(PetEventEntity.class, newEvent.getId());
        assertEquals("Vacuna antirrábica reprogramada", resp.getDescription());
        assertEquals(petList.get(0).getId(), resp.getPet().getId());
    }

    @Test
    void testUpdateEventReassignPetNotAllowed() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEventEntity newEvent = persistVaccinationEvent(petList.get(0), ONE_DAY_MS);

            PetEventEntity pojoEntity = new PetEventEntity();
            pojoEntity.setEventType("VACCINATION");
            pojoEntity.setDate(new Date(System.currentTimeMillis() + 2 * ONE_DAY_MS));
            pojoEntity.setDescription("Vacuna antirrábica reprogramada");
            pojoEntity.setPet(petList.get(1));

            petEventService.updateEvent(petList.get(0).getId(), newEvent.getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateEventNonArrivalAllowed() throws EntityNotFoundException, IllegalOperationException {
        PetEventEntity newEvent = persistVaccinationEvent(petList.get(0), ONE_DAY_MS);

        PetEventEntity pojoEntity = new PetEventEntity();
        pojoEntity.setEventType("ILLNESS");
        pojoEntity.setDate(new Date(System.currentTimeMillis()));
        pojoEntity.setDescription("Cambio de diagnóstico");

        PetEventEntity result = petEventService.updateEvent(petList.get(0).getId(), newEvent.getId(), pojoEntity);

        assertEquals("ILLNESS", result.getEventType());
    }

    @Test
    void testUpdateEventArrivalNotAllowed() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEventEntity pojoEntity = factory.manufacturePojo(PetEventEntity.class);
            pojoEntity.setEventType("ILLNESS");
            pojoEntity.setDate(new Date(System.currentTimeMillis()));
            petEventService.updateEvent(petList.get(0).getId(), arrivalEvent.getId(), pojoEntity);
        });
    }

    @Test
    void testUpdateEventNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            PetEventEntity pojoEntity = factory.manufacturePojo(PetEventEntity.class);
            pojoEntity.setEventType("ILLNESS");
            pojoEntity.setDate(new Date(System.currentTimeMillis()));
            petEventService.updateEvent(petList.get(0).getId(), 0L, pojoEntity);
        });
    }

    /*  deleteEvent  */

    @Test
    void testDeleteVaccinationNotYetOccurred() throws EntityNotFoundException, IllegalOperationException {
        PetEventEntity futureVaccination = persistVaccinationEvent(petList.get(0), ONE_DAY_MS);

        petEventService.deleteEvent(petList.get(0).getId(), futureVaccination.getId());

        PetEventEntity deleted = entityManager.find(PetEventEntity.class, futureVaccination.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteVaccinationAlreadyOccurred() {
        assertThrows(IllegalOperationException.class, () -> {
            PetEventEntity pastVaccination = persistVaccinationEvent(petList.get(0), -ONE_DAY_MS);
            petEventService.deleteEvent(petList.get(0).getId(), pastVaccination.getId());
        });
    }

    @Test
    void testDeleteArrivalEventAllowed() throws EntityNotFoundException, IllegalOperationException {
        petEventService.deleteEvent(petList.get(0).getId(), arrivalEvent.getId());

        PetEventEntity deleted = entityManager.find(PetEventEntity.class, arrivalEvent.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteEvent() throws EntityNotFoundException, IllegalOperationException {
        PetEventEntity newEvent = new PetEventEntity();
        newEvent.setEventType("ILLNESS");
        newEvent.setDate(new Date(System.currentTimeMillis()));
        newEvent.setDescription("Gripe leve");
        newEvent.setPet(petList.get(0));
        entityManager.persist(newEvent);

        petEventService.deleteEvent(petList.get(0).getId(), newEvent.getId());

        PetEventEntity deleted = entityManager.find(PetEventEntity.class, newEvent.getId());
        assertNull(deleted);
    }

    @Test
    void testDeleteEventNotFound() {
        assertThrows(EntityNotFoundException.class, () -> {
            petEventService.deleteEvent(petList.get(0).getId(), 0L);
        });
    }
}
