package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import jakarta.transaction.Transactional;

import co.edu.udistrital.mdp.pets.entities.EventCalendarEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.EventCalendarRepository;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;

@DataJpaTest
@Transactional
@Import(EventCalendarService.class)
class EventCalendarServiceTest {

    @Autowired
    private EventCalendarService eventCalendarService;

    @Autowired
    private EventCalendarRepository eventCalendarRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    private List<EventCalendarEntity> eventList =
            new ArrayList<>();

    private List<ShelterEntity> shelterList =
            new ArrayList<>();

    @BeforeEach
    void setUp() {

        eventCalendarRepository.deleteAll();
        shelterRepository.deleteAll();

        for (int i = 0; i < 3; i++) {

            ShelterEntity shelter = new ShelterEntity();

            shelter.setName("Shelter " + i);
            shelter.setAddress("Address " + i);
            shelter.setPhone("123456789");

            shelter = shelterRepository.save(shelter);

            shelterList.add(shelter);

            EventCalendarEntity event =
                    new EventCalendarEntity();

            event.setType("Adoption event " + i);
            event.setDate(new Date());
            event.setShelter(shelter);

            event =
                    eventCalendarRepository.save(event);

            eventList.add(event);
        }
    }

    @Test
    void createEventCalendarTest() throws Exception {

        EventCalendarEntity event =
                new EventCalendarEntity();

        event.setType("Vaccination");
        event.setDate(new Date());
        event.setShelter(shelterList.get(0));

        EventCalendarEntity result =
                eventCalendarService.createEventCalendar(event);

        assertNotNull(result);

        EventCalendarEntity entity =
                eventCalendarRepository
                        .findById(result.getId())
                        .get();

        assertEquals(
                "Vaccination",
                entity.getType());

        assertEquals(
                shelterList.get(0).getId(),
                entity.getShelter().getId());
    }

    @Test
    void createEventCalendarWithoutTypeTest() {

        EventCalendarEntity event =
                new EventCalendarEntity();

        event.setType("");
        event.setDate(new Date());
        event.setShelter(shelterList.get(0));

        assertThrows(
                IllegalOperationException.class,
                () -> eventCalendarService
                        .createEventCalendar(event));
    }

    @Test
    void createEventCalendarWithoutDateTest() {

        EventCalendarEntity event =
                new EventCalendarEntity();

        event.setType("Vaccination");
        event.setDate(null);
        event.setShelter(shelterList.get(0));

        assertThrows(
                IllegalOperationException.class,
                () -> eventCalendarService
                        .createEventCalendar(event));
    }

    @Test
    void createEventCalendarWithoutShelterTest() {

        EventCalendarEntity event =
                new EventCalendarEntity();

        event.setType("Vaccination");
        event.setDate(new Date());
        event.setShelter(null);

        assertThrows(
                IllegalOperationException.class,
                () -> eventCalendarService
                        .createEventCalendar(event));
    }

    @Test
    void createEventCalendarWithInvalidShelterTest() {

        EventCalendarEntity event =
                new EventCalendarEntity();

        ShelterEntity invalidShelter =
                new ShelterEntity();

        invalidShelter.setId(999L);

        event.setType("Vaccination");
        event.setDate(new Date());
        event.setShelter(invalidShelter);

        assertThrows(
                EntityNotFoundException.class,
                () -> eventCalendarService
                        .createEventCalendar(event));
    }

    @Test
    void getEventCalendarsTest() {

        List<EventCalendarEntity> result =
                eventCalendarService.getEventCalendars();

        assertEquals(3, result.size());
    }

    @Test
    void getEventCalendarTest() throws Exception {

        EventCalendarEntity event =
                eventList.get(0);

        EventCalendarEntity result =
                eventCalendarService.getEventCalendar(
                        event.getId());

        assertNotNull(result);

        assertEquals(
                event.getId(),
                result.getId());

        assertEquals(
                event.getType(),
                result.getType());
    }

    @Test
    void getEventCalendarNotFoundTest() {

        assertThrows(
                EntityNotFoundException.class,
                () -> eventCalendarService
                        .getEventCalendar(999L));
    }

    @Test
    void updateEventCalendarTest() throws Exception {

        EventCalendarEntity event =
                eventList.get(0);

        EventCalendarEntity updated =
                new EventCalendarEntity();

        updated.setType("Updated event");
        updated.setDate(new Date());
        updated.setShelter(shelterList.get(1));

        EventCalendarEntity result =
                eventCalendarService.updateEventCalendar(
                        event.getId(),
                        updated);

        assertEquals(
                event.getId(),
                result.getId());

        assertEquals(
                "Updated event",
                result.getType());

        assertEquals(
                shelterList.get(1).getId(),
                result.getShelter().getId());
    }

    @Test
    void updateEventCalendarNotFoundTest() {

        EventCalendarEntity event =
                new EventCalendarEntity();

        event.setType("Event");
        event.setDate(new Date());
        event.setShelter(shelterList.get(0));

        assertThrows(
                EntityNotFoundException.class,
                () -> eventCalendarService
                        .updateEventCalendar(
                                999L,
                                event));
    }

    @Test
    void deleteEventCalendarTest() throws Exception {

        EventCalendarEntity event =
                eventList.get(0);

        eventCalendarService.deleteEventCalendar(
                event.getId());

        assertThrows(
                EntityNotFoundException.class,
                () -> eventCalendarService
                        .getEventCalendar(
                                event.getId()));
    }

    @Test
    void deleteEventCalendarNotFoundTest() {

        assertThrows(
                EntityNotFoundException.class,
                () -> eventCalendarService
                        .deleteEventCalendar(999L));
    }
}
