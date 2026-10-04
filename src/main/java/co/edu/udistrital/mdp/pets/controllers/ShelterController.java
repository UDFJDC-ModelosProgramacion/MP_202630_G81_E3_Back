package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import co.edu.udistrital.mdp.pets.dto.ShelterDTO;
import co.edu.udistrital.mdp.pets.dto.ShelterDetailDTO;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.ShelterService;

@RestController
@RequestMapping("/shelters")
public class ShelterController {

    private final ShelterService shelterService;
    private final ModelMapper modelMapper;

    public ShelterController(ShelterService shelterService, ModelMapper modelMapper) {
        this.shelterService = shelterService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShelterDTO create(@RequestBody ShelterDTO shelterDTO) throws IllegalOperationException {
        ShelterEntity created = shelterService.createShelter(modelMapper.map(shelterDTO, ShelterEntity.class));
        return modelMapper.map(created, ShelterDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ShelterDetailDTO> findAll(@RequestParam(required = false) String city) {
        List<ShelterEntity> shelters = city == null ? shelterService.getShelters() : shelterService.getSheltersByCity(city);
        return modelMapper.map(shelters, new TypeToken<List<ShelterDetailDTO>>() {}.getType());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ShelterDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        return modelMapper.map(shelterService.getShelter(id), ShelterDetailDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ShelterDTO update(@PathVariable Long id, @RequestBody ShelterDTO shelterDTO)
            throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity updated = shelterService.updateShelter(id, modelMapper.map(shelterDTO, ShelterEntity.class));
        return modelMapper.map(updated, ShelterDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        shelterService.deleteShelter(id);
    }
}
