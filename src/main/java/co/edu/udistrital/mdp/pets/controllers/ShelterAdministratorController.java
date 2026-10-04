package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import co.edu.udistrital.mdp.pets.dto.ShelterAdministratorDTO;
import co.edu.udistrital.mdp.pets.dto.ShelterAdministratorDetailDTO;
import co.edu.udistrital.mdp.pets.entities.ShelterAdministratorEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.ShelterAdministratorService;

@RestController
@RequestMapping("/administrators")
public class ShelterAdministratorController {

    private final ShelterAdministratorService administratorService;
    private final ModelMapper modelMapper;

    public ShelterAdministratorController(ShelterAdministratorService administratorService, ModelMapper modelMapper) {
        this.administratorService = administratorService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShelterAdministratorDTO create(@RequestBody ShelterAdministratorDTO administratorDTO)
            throws IllegalOperationException {
        ShelterAdministratorEntity created = administratorService
                .createAdministrator(modelMapper.map(administratorDTO, ShelterAdministratorEntity.class));
        return modelMapper.map(created, ShelterAdministratorDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ShelterAdministratorDetailDTO> findAll() {
        return modelMapper.map(administratorService.getAdministrators(),
                new TypeToken<List<ShelterAdministratorDetailDTO>>() {}.getType());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ShelterAdministratorDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        return modelMapper.map(administratorService.getAdministrator(id), ShelterAdministratorDetailDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ShelterAdministratorDTO update(@PathVariable Long id, @RequestBody ShelterAdministratorDTO administratorDTO)
            throws EntityNotFoundException, IllegalOperationException {
        ShelterAdministratorEntity updated = administratorService.updateAdministrator(id,
                modelMapper.map(administratorDTO, ShelterAdministratorEntity.class));
        return modelMapper.map(updated, ShelterAdministratorDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        administratorService.deleteAdministrator(id);
    }
}
