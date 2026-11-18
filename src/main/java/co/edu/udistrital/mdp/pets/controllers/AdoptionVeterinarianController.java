package co.edu.udistrital.mdp.pets.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.VeterinarianDTO;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.AdoptionVeterinarianService;

/** Veterinario que acompaña una adopción: lo usa la página "Veterinario de la adopción". */
@RestController
@RequestMapping("/adoptions/{adoptionId}/veterinarian")
public class AdoptionVeterinarianController {

    private final AdoptionVeterinarianService adoptionVeterinarianService;
    private final ModelMapper modelMapper;

    public AdoptionVeterinarianController(AdoptionVeterinarianService adoptionVeterinarianService,
            ModelMapper modelMapper) {
        this.adoptionVeterinarianService = adoptionVeterinarianService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/{veterinarianId}")
    @ResponseStatus(HttpStatus.CREATED)
    public VeterinarianDTO assign(@PathVariable Long adoptionId, @PathVariable Long veterinarianId)
            throws EntityNotFoundException, IllegalOperationException {
        return modelMapper.map(adoptionVeterinarianService.assignVeterinarian(adoptionId, veterinarianId),
                VeterinarianDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public VeterinarianDTO find(@PathVariable Long adoptionId) throws EntityNotFoundException {
        return modelMapper.map(adoptionVeterinarianService.getVeterinarian(adoptionId), VeterinarianDTO.class);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long adoptionId) throws EntityNotFoundException {
        adoptionVeterinarianService.removeVeterinarian(adoptionId);
    }
}
