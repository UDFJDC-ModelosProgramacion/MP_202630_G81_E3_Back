package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import co.edu.udistrital.mdp.pets.dto.EventCalendarDTO;
import co.edu.udistrital.mdp.pets.dto.EventCalendarDetailDTO;
import co.edu.udistrital.mdp.pets.entities.EventCalendarEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.EventCalendarService;

@RestController
@RequestMapping("/event-calendars")
public class EventCalendarController {

    private final EventCalendarService eventCalendarService;
    private final ModelMapper modelMapper;

    public EventCalendarController(
            EventCalendarService eventCalendarService,
            ModelMapper modelMapper) {
        this.eventCalendarService = eventCalendarService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventCalendarDTO create(
            @RequestBody EventCalendarDTO eventCalendarDTO)
            throws IllegalOperationException {

        EventCalendarEntity created =
                eventCalendarService.createEventCalendar(
                    modelMapper.map(
                        eventCalendarDTO,
                        EventCalendarEntity.class
                    )
                );

        return modelMapper.map(created, EventCalendarDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventCalendarDetailDTO> findAll() {

        List<EventCalendarEntity> events =
                eventCalendarService.getEventCalendars();

        return modelMapper.map(
            events,
            new TypeToken<List<EventCalendarDetailDTO>>() {}.getType()
        );
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventCalendarDetailDTO findOne(@PathVariable Long id)
            throws EntityNotFoundException {

        EventCalendarEntity event =
                eventCalendarService.getEventCalendar(id);

        return modelMapper.map(event, EventCalendarDetailDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventCalendarDTO update(
            @PathVariable Long id,
            @RequestBody EventCalendarDTO eventCalendarDTO)
            throws EntityNotFoundException, IllegalOperationException {

        EventCalendarEntity updated =
                eventCalendarService.updateEventCalendar(
                    id,
                    modelMapper.map(
                        eventCalendarDTO,
                        EventCalendarEntity.class
                    )
                );

        return modelMapper.map(updated, EventCalendarDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id)
            throws EntityNotFoundException, IllegalOperationException {

        eventCalendarService.deleteEventCalendar(id);
    }
}
