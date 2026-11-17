package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import co.edu.udistrital.mdp.pets.dto.ShelterAdministratorDTO;
import co.edu.udistrital.mdp.pets.dto.ShelterAdministratorDetailDTO;
import co.edu.udistrital.mdp.pets.dto.ShelterDTO;
import co.edu.udistrital.mdp.pets.dto.ShelterDetailDTO;
import co.edu.udistrital.mdp.pets.entities.ShelterAdministratorEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.ShelterAdministratorService;

/** Administradores de refugios y los refugios que administran. */
@RestController
@RequestMapping("/administrators")
public class ShelterAdministratorController {

    private final ShelterAdministratorService administratorService;
    private final ModelMapper modelMapper;

    public ShelterAdministratorController(ShelterAdministratorService administratorService,
            ModelMapper modelMapper) {
        this.administratorService = administratorService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShelterAdministratorDetailDTO create(@RequestBody ShelterAdministratorDTO dto)
            throws IllegalOperationException {
        ShelterAdministratorEntity created = administratorService
                .createAdministrator(modelMapper.map(dto, ShelterAdministratorEntity.class));
        return modelMapper.map(created, ShelterAdministratorDetailDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ShelterAdministratorDetailDTO> findAll() {
        return modelMapper.map(administratorService.getAdministrators(),
                new TypeToken<List<ShelterAdministratorDetailDTO>>() {
                }.getType());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ShelterAdministratorDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        return modelMapper.map(administratorService.getAdministrator(id), ShelterAdministratorDetailDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ShelterAdministratorDetailDTO update(@PathVariable Long id, @RequestBody ShelterAdministratorDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        ShelterAdministratorEntity updated = administratorService.updateAdministrator(id,
                modelMapper.map(dto, ShelterAdministratorEntity.class));
        return modelMapper.map(updated, ShelterAdministratorDetailDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        administratorService.deleteAdministrator(id);
    }

    /** Asocia el administrador con el refugio. */
    @PostMapping("/{administratorId}/shelters/{shelterId}")
    @ResponseStatus(HttpStatus.OK)
    public ShelterDTO addShelter(@PathVariable Long administratorId, @PathVariable Long shelterId)
            throws EntityNotFoundException, IllegalOperationException {
        return modelMapper.map(administratorService.addShelter(administratorId, shelterId), ShelterDTO.class);
    }

    @GetMapping("/{administratorId}/shelters")
    @ResponseStatus(HttpStatus.OK)
    public List<ShelterDTO> findShelters(@PathVariable Long administratorId) throws EntityNotFoundException {
        return modelMapper.map(administratorService.getShelters(administratorId),
                new TypeToken<List<ShelterDTO>>() {
                }.getType());
    }

    @GetMapping("/{administratorId}/shelters/{shelterId}")
    @ResponseStatus(HttpStatus.OK)
    public ShelterDetailDTO findShelter(@PathVariable Long administratorId, @PathVariable Long shelterId)
            throws EntityNotFoundException, IllegalOperationException {
        return modelMapper.map(administratorService.getShelter(administratorId, shelterId),
                ShelterDetailDTO.class);
    }

    @DeleteMapping("/{administratorId}/shelters/{shelterId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeShelter(@PathVariable Long administratorId, @PathVariable Long shelterId)
            throws EntityNotFoundException, IllegalOperationException {
        administratorService.removeShelter(administratorId, shelterId);
    }
}
