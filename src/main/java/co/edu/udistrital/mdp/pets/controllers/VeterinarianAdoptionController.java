package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.AdoptionDTO;
import co.edu.udistrital.mdp.pets.dto.FollowUpDTO;
import co.edu.udistrital.mdp.pets.dto.PetEventDTO;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ForbiddenOperationException;
import co.edu.udistrital.mdp.pets.services.VeterinarianAdoptionService;

/**
 * Adopciones que tiene asignadas un veterinario para el seguimiento posadopción, con los
 * seguimientos y los eventos que registra el adoptante. Si la adopción no es suya, responde 403.
 */
@RestController
@RequestMapping("/veterinarians/{veterinarianId}/adoptions")
public class VeterinarianAdoptionController {

    private final VeterinarianAdoptionService service;
    private final ModelMapper modelMapper;

    public VeterinarianAdoptionController(VeterinarianAdoptionService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AdoptionDTO> findAssigned(@PathVariable Long veterinarianId) throws EntityNotFoundException {
        return modelMapper.map(service.getAssignedAdoptions(veterinarianId), new TypeToken<List<AdoptionDTO>>() {
        }.getType());
    }

    @GetMapping("/{adoptionId}/follow-ups")
    @ResponseStatus(HttpStatus.OK)
    public List<FollowUpDTO> findFollowUps(@PathVariable Long veterinarianId, @PathVariable Long adoptionId)
            throws EntityNotFoundException, ForbiddenOperationException {
        return modelMapper.map(service.getFollowUps(veterinarianId, adoptionId),
                new TypeToken<List<FollowUpDTO>>() {
                }.getType());
    }

    @GetMapping("/{adoptionId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<PetEventDTO> findPetEvents(@PathVariable Long veterinarianId, @PathVariable Long adoptionId)
            throws EntityNotFoundException, ForbiddenOperationException {
        return modelMapper.map(service.getPetEvents(veterinarianId, adoptionId),
                new TypeToken<List<PetEventDTO>>() {
                }.getType());
    }
}
