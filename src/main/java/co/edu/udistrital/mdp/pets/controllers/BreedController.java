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

import co.edu.udistrital.mdp.pets.dto.BreedDTO;
import co.edu.udistrital.mdp.pets.entities.BreedEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.BreedService;

@RestController
@RequestMapping("/pets/{petId}/breed")
public class BreedController {

    private BreedService breedService;
    private ModelMapper modelMapper;

    public BreedController(BreedService breedService, ModelMapper modelMapper) {
        this.breedService = breedService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public BreedDTO create(@PathVariable Long petId, @RequestBody BreedDTO breedDTO)
            throws EntityNotFoundException, IllegalOperationException {
        BreedEntity breed = breedService.createBreed(petId, modelMapper.map(breedDTO, BreedEntity.class));
        return modelMapper.map(breed, BreedDTO.class);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public BreedDTO findOne(@PathVariable Long petId) throws EntityNotFoundException {
        BreedEntity breed = breedService.getBreed(petId);
        return modelMapper.map(breed, BreedDTO.class);
    }

    @PutMapping
    @ResponseStatus(code = HttpStatus.OK)
    public BreedDTO update(@PathVariable Long petId, @RequestBody BreedDTO breedDTO)
            throws EntityNotFoundException, IllegalOperationException {
        BreedEntity breed = breedService.updateBreed(petId, modelMapper.map(breedDTO, BreedEntity.class));
        return modelMapper.map(breed, BreedDTO.class);
    }

    @DeleteMapping
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long petId) throws EntityNotFoundException {
        breedService.deleteBreed(petId);
    }
}
