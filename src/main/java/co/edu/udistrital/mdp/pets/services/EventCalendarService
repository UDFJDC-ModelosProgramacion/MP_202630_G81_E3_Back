package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import co.edu.udistrital.mdp.pets.entities.EventCalendarEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.repositories.EventCalendarRepository;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

@Slf4j
@Service
public class EventCalendarService {

    @Autowired
    private EventCalendarRepository eventCalendarRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    @Transactional
    public EventCalendarEntity createEventCalendar(
            EventCalendarEntity eventCalendar)
            throws EntityNotFoundException, IllegalOperationException {

        log.info("Inicia proceso de creación del evento del calendario");

        if (eventCalendar.getType() == null ||
            eventCalendar.getType().isBlank()) {
            throw new IllegalOperationException(
                    "Event type is not valid");
        }

        if (eventCalendar.getDate() == null) {
            throw new IllegalOperationException(
                    "Event date is not valid");
        }

        if (eventCalendar.getShelter() == null) {
            throw new IllegalOperationException(
                    "Shelter is not valid");
        }

        Optional<ShelterEntity> shelter =
                shelterRepository.findById(
                        eventCalendar.getShelter().getId());

        if (shelter.isEmpty()) {
            throw new EntityNotFoundException(
                    "Shelter not found");
        }

        eventCalendar.setShelter(shelter.get());

        log.info("Termina proceso de creación del evento del calendario");

        return eventCalendarRepository.save(eventCalendar);
    }

    @Transactional
    public List<EventCalendarEntity> getEventCalendars() {

        log.info("Inicia proceso de consultar todos los eventos");

        return eventCalendarRepository.findAll();
    }

    @Transactional
    public EventCalendarEntity getEventCalendar(Long eventCalendarId)
            throws EntityNotFoundException {

        log.info(
                "Inicia proceso de consultar el evento con id = {}",
                eventCalendarId);

        Optional<EventCalendarEntity> eventCalendar =
                eventCalendarRepository.findById(eventCalendarId);

        if (eventCalendar.isEmpty()) {
            throw new EntityNotFoundException(
                    "Event calendar not found");
        }

        return eventCalendar.get();
    }

    @Transactional
    public EventCalendarEntity updateEventCalendar(
            Long eventCalendarId,
            EventCalendarEntity eventCalendar)
            throws EntityNotFoundException, IllegalOperationException {

        log.info(
                "Inicia proceso de actualizar el evento con id = {}",
                eventCalendarId);

        Optional<EventCalendarEntity> existing =
                eventCalendarRepository.findById(eventCalendarId);

        if (existing.isEmpty()) {
            throw new EntityNotFoundException(
                    "Event calendar not found");
        }

        if (eventCalendar.getType() == null ||
            eventCalendar.getType().isBlank()) {
            throw new IllegalOperationException(
                    "Event type is not valid");
        }

        if (eventCalendar.getDate() == null) {
            throw new IllegalOperationException(
                    "Event date is not valid");
        }

        if (eventCalendar.getShelter() == null) {
            throw new IllegalOperationException(
                    "Shelter is not valid");
        }

        Optional<ShelterEntity> shelter =
                shelterRepository.findById(
                        eventCalendar.getShelter().getId());

        if (shelter.isEmpty()) {
            throw new EntityNotFoundException(
                    "Shelter not found");
        }

        eventCalendar.setId(eventCalendarId);
        eventCalendar.setShelter(shelter.get());

        log.info(
                "Termina proceso de actualizar el evento con id = {}",
                eventCalendarId);

        return eventCalendarRepository.save(eventCalendar);
    }

    @Transactional
    public void deleteEventCalendar(Long eventCalendarId)
            throws EntityNotFoundException {

        log.info(
                "Inicia proceso de borrar el evento con id = {}",
                eventCalendarId);

        Optional<EventCalendarEntity> eventCalendar =
                eventCalendarRepository.findById(eventCalendarId);

        if (eventCalendar.isEmpty()) {
            throw new EntityNotFoundException(
                    "Event calendar not found");
        }

        eventCalendarRepository.deleteById(eventCalendarId);

        log.info(
                "Termina proceso de borrar el evento con id = {}",
                eventCalendarId);
    }
}
