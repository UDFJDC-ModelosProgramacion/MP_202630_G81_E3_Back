package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.AdoptionDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionDetailDTO;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.AdoptionService;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adoptions")
public class AdoptionController {

    @Autowired
    private AdoptionService adoptionService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AdoptionDetailDTO> findAll() {
        List<AdoptionEntity> adoptions = adoptionService.getAdoptions();
        return modelMapper.map(adoptions, new TypeToken<List<AdoptionDetailDTO>>() {}.getType());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AdoptionDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        AdoptionEntity entity = adoptionService.getAdoption(id);
        return modelMapper.map(entity, AdoptionDetailDTO.class);
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