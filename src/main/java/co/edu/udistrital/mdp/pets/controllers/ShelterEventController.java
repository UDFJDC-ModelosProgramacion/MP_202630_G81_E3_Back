package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.EventCalendarDTO;
import co.edu.udistrital.mdp.pets.entities.EventCalendarEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.EventCalendarService;
import co.edu.udistrital.mdp.pets.services.ShelterService;

/** Calendario del refugio: ferias de adopción, talleres y jornadas de vacunación. */
@RestController
@RequestMapping("/shelters/{shelterId}/events")
public class ShelterEventController {

    private static final String EVENT_NOT_FOUND = "Event calendar not found";

    private final EventCalendarService eventCalendarService;
    private final ShelterService shelterService;
    private final ModelMapper modelMapper;

    public ShelterEventController(EventCalendarService eventCalendarService, ShelterService shelterService,
            ModelMapper modelMapper) {
        this.eventCalendarService = eventCalendarService;
        this.shelterService = shelterService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventCalendarDTO create(@PathVariable Long shelterId, @RequestBody EventCalendarDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity shelter = shelterService.getShelter(shelterId);
        EventCalendarEntity entity = modelMapper.map(dto, EventCalendarEntity.class);
        entity.setId(null);
        entity.setShelter(shelter);
        return modelMapper.map(eventCalendarService.createEventCalendar(entity), EventCalendarDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventCalendarDTO> findAll(@PathVariable Long shelterId) throws EntityNotFoundException {
        return modelMapper.map(eventCalendarService.getEventCalendarsByShelter(shelterId),
                new TypeToken<List<EventCalendarDTO>>() {
                }.getType());
    }

    @PutMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventCalendarDTO update(@PathVariable Long shelterId, @PathVariable Long eventId,
            @RequestBody EventCalendarDTO dto) throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity shelter = shelterService.getShelter(shelterId);
        ensureBelongsToShelter(shelterId, eventId);
        EventCalendarEntity entity = modelMapper.map(dto, EventCalendarEntity.class);
        entity.setShelter(shelter);
        return modelMapper.map(eventCalendarService.updateEventCalendar(eventId, entity), EventCalendarDTO.class);
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long shelterId, @PathVariable Long eventId) throws EntityNotFoundException {
        shelterService.getShelter(shelterId);
        ensureBelongsToShelter(shelterId, eventId);
        eventCalendarService.deleteEventCalendar(eventId);
    }

    private void ensureBelongsToShelter(Long shelterId, Long eventId) throws EntityNotFoundException {
        EventCalendarEntity existing = eventCalendarService.getEventCalendar(eventId);
        if (existing.getShelter() == null || !shelterId.equals(existing.getShelter().getId()))
            throw new EntityNotFoundException(EVENT_NOT_FOUND);
    }
}
