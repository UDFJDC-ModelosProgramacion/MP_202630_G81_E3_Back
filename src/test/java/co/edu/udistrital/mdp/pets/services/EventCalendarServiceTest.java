package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import co.edu.udistrital.mdp.pets.entities.EventCalendarEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.EventCalendarRepository;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;

@DataJpaTest
@Import(EventCalendarService.class)
class EventCalendarServiceTest {

    @Autowired
    private EventCalendarService eventCalendarService;

    @Autowired
    private EventCalendarRepository eventCalendarRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    private ShelterEntity shelter;
    private EventCalendarEntity eventCalendar;

    @BeforeEach
    void setUp() {
        eventCalendarRepository.deleteAll();
        shelterRepository.deleteAll();

        shelter = new ShelterEntity();
        shelter.setName("Refugio Test");
        shelter.setCity("Bogotá");
        shelter = shelterRepository.save(shelter);

        eventCalendar = new EventCalendarEntity();
        eventCalendar.setType("Vacunación");
        eventCalendar.setDate(new Date());
        eventCalendar.setShelter(shelter);
    }

    @Test
    void createEventCalendarTest() throws Exception {
        EventCalendarEntity result =
                eventCalendarService.createEventCalendar(eventCalendar);

        assertNotNull(result.getId());
        assertEquals("Vacunación", result.getType());
        assertNotNull(result.getDate());
        assertEquals(shelter.getId(), result.getShelter().getId());
    }

    @Test
    void createEventCalendarWithoutTypeTest() {
        eventCalendar.setType("");

        assertThrows(
                IllegalOperationException.class,
                () -> eventCalendarService.createEventCalendar(eventCalendar)
        );
    }

    @Test
    void createEventCalendarWithoutDateTest() {
        eventCalendar.setDate(null);

        assertThrows(
                IllegalOperationException.class,
                () -> eventCalendarService.createEventCalendar(eventCalendar)
        );
    }

    @Test
    void createEventCalendarWithoutShelterTest() {
        eventCalendar.setShelter(null);

        assertThrows(
                IllegalOperationException.class,
                () -> eventCalendarService.createEventCalendar(eventCalendar)
        );
    }

    @Test
    void createEventCalendarWithNonExistingShelterTest() {
        ShelterEntity invalidShelter = new ShelterEntity();
        invalidShelter.setId(999999L);

        eventCalendar.setShelter(invalidShelter);

        assertThrows(
                EntityNotFoundException.class,
                () -> eventCalendarService.createEventCalendar(eventCalendar)
        );
    }

    @Test
    void getEventCalendarsTest() throws Exception {
        eventCalendarService.createEventCalendar(eventCalendar);

        List<EventCalendarEntity> result =
                eventCalendarService.getEventCalendars();

        assertEquals(1, result.size());
        assertEquals("Vacunación", result.get(0).getType());
    }

    @Test
    void getEventCalendarTest() throws Exception {
        EventCalendarEntity saved =
                eventCalendarService.createEventCalendar(eventCalendar);

        EventCalendarEntity result =
                eventCalendarService.getEventCalendar(saved.getId());

        assertEquals(saved.getId(), result.getId());
        assertEquals("Vacunación", result.getType());
    }

    @Test
    void getEventCalendarWithNonExistingIdTest() {
        assertThrows(
                EntityNotFoundException.class,
                () -> eventCalendarService.getEventCalendar(999999L)
        );
    }

    @Test
    void updateEventCalendarTest() throws Exception {
        EventCalendarEntity saved =
                eventCalendarService.createEventCalendar(eventCalendar);

        EventCalendarEntity update = new EventCalendarEntity();
        update.setType("Consulta veterinaria");
        update.setDate(new Date());
        update.setShelter(shelter);

        EventCalendarEntity result =
                eventCalendarService.updateEventCalendar(
                        saved.getId(), update);

        assertEquals(saved.getId(), result.getId());
        assertEquals("Consulta veterinaria", result.getType());
        assertNotNull(result.getDate());
        assertEquals(shelter.getId(), result.getShelter().getId());
    }

    @Test
    void updateNonExistingEventCalendarTest() {
        assertThrows(
                EntityNotFoundException.class,
                () -> eventCalendarService.updateEventCalendar(
                        999999L, eventCalendar)
        );
    }

    @Test
    void updateEventCalendarWithoutTypeTest() throws Exception {
        EventCalendarEntity saved =
                eventCalendarService.createEventCalendar(eventCalendar);

        eventCalendar.setType("");

        assertThrows(
                IllegalOperationException.class,
                () -> eventCalendarService.updateEventCalendar(
                        saved.getId(), eventCalendar)
        );
    }

    @Test
    void updateEventCalendarWithoutDateTest() throws Exception {
        EventCalendarEntity saved =
                eventCalendarService.createEventCalendar(eventCalendar);

        eventCalendar.setDate(null);

        assertThrows(
                IllegalOperationException.class,
                () -> eventCalendarService.updateEventCalendar(
                        saved.getId(), eventCalendar)
        );
    }

    @Test
    void updateEventCalendarWithoutShelterTest() throws Exception {
        EventCalendarEntity saved =
                eventCalendarService.createEventCalendar(eventCalendar);

        eventCalendar.setShelter(null);

        assertThrows(
                IllegalOperationException.class,
                () -> eventCalendarService.updateEventCalendar(
                        saved.getId(), eventCalendar)
        );
    }

    @Test
    void updateEventCalendarWithNonExistingShelterTest()
            throws Exception {

        EventCalendarEntity saved =
                eventCalendarService.createEventCalendar(eventCalendar);

        ShelterEntity invalidShelter = new ShelterEntity();
        invalidShelter.setId(999999L);

        eventCalendar.setShelter(invalidShelter);

        assertThrows(
                EntityNotFoundException.class,
                () -> eventCalendarService.updateEventCalendar(
                        saved.getId(), eventCalendar)
        );
    }

    @Test
    void deleteEventCalendarTest() throws Exception {
        EventCalendarEntity saved =
                eventCalendarService.createEventCalendar(eventCalendar);

        eventCalendarService.deleteEventCalendar(saved.getId());

        assertFalse(
                eventCalendarRepository
                        .findById(saved.getId())
                        .isPresent()
        );
    }

    @Test
    void deleteNonExistingEventCalendarTest() {
        assertThrows(
                EntityNotFoundException.class,
                () -> eventCalendarService.deleteEventCalendar(999999L)
        );
    }
}
