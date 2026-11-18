package co.edu.udistrital.mdp.pets.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.CoexistenceTestDTO;
import co.edu.udistrital.mdp.pets.entities.CoexistenceTestEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.AdoptionCoexistenceService;

@RestController
@RequestMapping("/adoptions/{adoptionId}/coexistence-test")
public class AdoptionCoexistenceController {

    private final AdoptionCoexistenceService coexistenceService;
    private final ModelMapper modelMapper;

    public AdoptionCoexistenceController(AdoptionCoexistenceService coexistenceService, ModelMapper modelMapper) {
        this.coexistenceService = coexistenceService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CoexistenceTestDTO create(@PathVariable Long adoptionId, @RequestBody CoexistenceTestDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        CoexistenceTestEntity created = coexistenceService.createCoexistenceTest(adoptionId,
                modelMapper.map(dto, CoexistenceTestEntity.class));
        return modelMapper.map(created, CoexistenceTestDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CoexistenceTestDTO findOne(@PathVariable Long adoptionId) throws EntityNotFoundException {
        return modelMapper.map(coexistenceService.getCoexistenceTest(adoptionId), CoexistenceTestDTO.class);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public CoexistenceTestDTO update(@PathVariable Long adoptionId, @RequestBody CoexistenceTestDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        CoexistenceTestEntity updated = coexistenceService.updateCoexistenceTest(adoptionId,
                modelMapper.map(dto, CoexistenceTestEntity.class));
        return modelMapper.map(updated, CoexistenceTestDTO.class);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long adoptionId) throws EntityNotFoundException {
        coexistenceService.deleteCoexistenceTest(adoptionId);
    }
}
