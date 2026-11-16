package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.VeterinarianDTO;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.ShelterService;
import co.edu.udistrital.mdp.pets.services.VeterinarianService;

/**
 * Veterinarios de un refugio. Permite registrar veterinarios para que el módulo de historial
 * médico tenga a quién autorizar (un veterinario atiende las mascotas de su refugio).
 */
@RestController
@RequestMapping("/shelters/{shelterId}/veterinarians")
public class ShelterVeterinarianController {

    private final VeterinarianService veterinarianService;
    private final ShelterService shelterService;
    private final ModelMapper modelMapper;

    public ShelterVeterinarianController(VeterinarianService veterinarianService, ShelterService shelterService,
            ModelMapper modelMapper) {
        this.veterinarianService = veterinarianService;
        this.shelterService = shelterService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VeterinarianDTO create(@PathVariable Long shelterId, @RequestBody VeterinarianDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity shelter = shelterService.getShelter(shelterId);
        VeterinarianEntity entity = modelMapper.map(dto, VeterinarianEntity.class);
        entity.setShelter(shelter);
        return modelMapper.map(veterinarianService.createVeterinarian(entity), VeterinarianDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<VeterinarianDTO> findAll(@PathVariable Long shelterId) throws EntityNotFoundException {
        List<VeterinarianEntity> veterinarians = shelterService.getShelter(shelterId).getVeterinarians();
        return modelMapper.map(veterinarians, new TypeToken<List<VeterinarianDTO>>() {
        }.getType());
    }
}
