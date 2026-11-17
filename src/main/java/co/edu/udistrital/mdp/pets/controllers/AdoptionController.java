package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.AdoptionDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionDetailDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionRequirementDTO;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.AdoptionRequirementService;
import co.edu.udistrital.mdp.pets.services.AdoptionService;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adoptions")
public class AdoptionController {

    private AdoptionService adoptionService;
    private ModelMapper modelMapper;

    @Autowired
    private AdoptionRequirementService requirementService;

    /** Detalle de la adopción con los requisitos que aplican a su mascota. */
    private AdoptionDetailDTO toDetail(AdoptionEntity entity) {
        AdoptionDetailDTO dto = modelMapper.map(entity, AdoptionDetailDTO.class);
        if (entity.getPet() != null) {
            List<AdoptionRequirementDTO> requirements = modelMapper.map(
                    requirementService.getRequirementsByPet(entity.getPet().getId()),
                    new TypeToken<List<AdoptionRequirementDTO>>() {}.getType());
            dto.setRequirements(requirements);
        }
        return dto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AdoptionDetailDTO> findAll() {
        return adoptionService.getAdoptions().stream().map(this::toDetail).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AdoptionDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        return toDetail(adoptionService.getAdoption(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdoptionDTO create(@RequestBody AdoptionDTO dto) throws IllegalOperationException, EntityNotFoundException {
        AdoptionEntity entity = modelMapper.map(dto, AdoptionEntity.class);
        AdoptionEntity createdEntity = adoptionService.createAdoption(entity);
        return modelMapper.map(createdEntity, AdoptionDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AdoptionDTO update(@PathVariable Long id, @RequestBody AdoptionDTO dto) throws EntityNotFoundException, IllegalOperationException {
        AdoptionEntity entity = modelMapper.map(dto, AdoptionEntity.class);
        AdoptionEntity updatedEntity = adoptionService.updateAdoption(id, entity);
        return modelMapper.map(updatedEntity, AdoptionDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        adoptionService.deleteAdoption(id);
    }
}
