package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.PetEventDTO;
import co.edu.udistrital.mdp.pets.entities.PetEventEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.PetEventService;

@RestController
@RequestMapping("/pets/{petId}/events")
public class PetEventController {

    private PetEventService petEventService;
    private ModelMapper modelMapper;

    public PetEventController(@Autowired  PetEventService petEventService,@Autowired  ModelMapper modelMapper) {
        this.petEventService = petEventService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public PetEventDTO create(@PathVariable Long petId, @RequestBody PetEventDTO petEventDTO)
            throws EntityNotFoundException, IllegalOperationException {
        PetEventEntity event = petEventService.createEvent(petId,
                modelMapper.map(petEventDTO, PetEventEntity.class));
        return modelMapper.map(event, PetEventDTO.class);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<PetEventDTO> findAll(@PathVariable Long petId,
            @RequestParam(required = false) String eventType) throws EntityNotFoundException {
        List<PetEventEntity> events = (eventType == null || eventType.isBlank())
                ? petEventService.getEvents(petId)
                : petEventService.getEventsByType(petId, eventType);
        return modelMapper.map(events, new TypeToken<List<PetEventDTO>>() {
        }.getType());
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(code = HttpStatus.OK)
    public PetEventDTO findOne(@PathVariable Long petId, @PathVariable Long eventId)
            throws EntityNotFoundException, IllegalOperationException {
        PetEventEntity event = petEventService.getEvent(petId, eventId);
        return modelMapper.map(event, PetEventDTO.class);
    }

    @PutMapping("/{eventId}")
    @ResponseStatus(code = HttpStatus.OK)
    public PetEventDTO update(@PathVariable Long petId, @PathVariable Long eventId,
            @RequestBody PetEventDTO petEventDTO) throws EntityNotFoundException, IllegalOperationException {
        PetEventEntity event = petEventService.updateEvent(petId, eventId,
                modelMapper.map(petEventDTO, PetEventEntity.class));
        return modelMapper.map(event, PetEventDTO.class);
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long petId, @PathVariable Long eventId)
            throws EntityNotFoundException, IllegalOperationException {
        petEventService.deleteEvent(petId, eventId);
    }
}
