package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.AdoptionRequirementDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionRequirementDetailDTO;
import co.edu.udistrital.mdp.pets.entities.AdoptionRequirementEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.AdoptionRequirementService;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adoption-requirements")
public class AdoptionRequirementController {

    @Autowired
    private AdoptionRequirementService requirementService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AdoptionRequirementDetailDTO> findAll() {
        List<AdoptionRequirementEntity> requirements = requirementService.getRequirements();
        return modelMapper.map(requirements, new TypeToken<List<AdoptionRequirementDetailDTO>>() {}.getType());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AdoptionRequirementDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        AdoptionRequirementEntity entity = requirementService.getRequirement(id);
        return modelMapper.map(entity, AdoptionRequirementDetailDTO.class);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdoptionRequirementDTO create(@RequestBody AdoptionRequirementDTO dto) throws IllegalOperationException {
        AdoptionRequirementEntity entity = modelMapper.map(dto, AdoptionRequirementEntity.class);
        AdoptionRequirementEntity createdEntity = requirementService.createRequirement(entity);
        return modelMapper.map(createdEntity, AdoptionRequirementDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AdoptionRequirementDTO update(@PathVariable Long id, @RequestBody AdoptionRequirementDTO dto) throws EntityNotFoundException, IllegalOperationException {
        AdoptionRequirementEntity entity = modelMapper.map(dto, AdoptionRequirementEntity.class);
        AdoptionRequirementEntity updatedEntity = requirementService.updateRequirement(id, entity);
        return modelMapper.map(updatedEntity, AdoptionRequirementDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        requirementService.deleteRequirement(id);
    }
}
