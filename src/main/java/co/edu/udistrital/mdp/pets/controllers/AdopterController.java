package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import co.edu.udistrital.mdp.pets.dto.AdopterDTO;
import co.edu.udistrital.mdp.pets.dto.AdopterDetailDTO;
import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.AdopterService;

@RestController
@RequestMapping("/adopters")
public class AdopterController {

    private final AdopterService adopterService;
    private final ModelMapper modelMapper;

    public AdopterController(AdopterService adopterService, ModelMapper modelMapper) {
        this.adopterService = adopterService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdopterDTO create(@RequestBody AdopterDTO adopterDTO) throws IllegalOperationException {
        AdopterEntity created = adopterService.createAdopter(modelMapper.map(adopterDTO, AdopterEntity.class));
        return modelMapper.map(created, AdopterDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AdopterDTO> findAll() {
        return modelMapper.map(adopterService.getAdopters(), new TypeToken<List<AdopterDTO>>() {}.getType());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AdopterDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        return modelMapper.map(adopterService.getAdopter(id), AdopterDetailDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AdopterDTO update(@PathVariable Long id, @RequestBody AdopterDTO adopterDTO)
            throws EntityNotFoundException, IllegalOperationException {
        AdopterEntity updated = adopterService.updateAdopter(id, modelMapper.map(adopterDTO, AdopterEntity.class));
        return modelMapper.map(updated, AdopterDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        adopterService.deleteAdopter(id);
    }
}