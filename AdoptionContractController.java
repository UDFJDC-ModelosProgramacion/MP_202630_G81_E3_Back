package co.edu.udistrital.mdp.pets.controllers;

import co.edu.udistrital.mdp.pets.dto.AdoptionContractDTO;
import co.edu.udistrital.mdp.pets.dto.AdoptionContractDetailDTO;
import co.edu.udistrital.mdp.pets.entities.AdoptionContractEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.AdoptionContractService;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adoption-contracts")
public class AdoptionContractController {

    @Autowired
    private AdoptionContractService contractService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AdoptionContractDetailDTO> findAll() {
        List<AdoptionContractEntity> contracts = contractService.getContracts();
        return modelMapper.map(contracts, new TypeToken<List<AdoptionContractDetailDTO>>() {}.getType());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AdoptionContractDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        AdoptionContractEntity entity = contractService.getContract(id);
        return modelMapper.map(entity, AdoptionContractDetailDTO.class);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdoptionContractDTO create(@RequestBody AdoptionContractDTO dto) throws IllegalOperationException {
        AdoptionContractEntity entity = modelMapper.map(dto, AdoptionContractEntity.class);
        AdoptionContractEntity createdEntity = contractService.createContract(entity);
        return modelMapper.map(createdEntity, AdoptionContractDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AdoptionContractDTO update(@PathVariable Long id, @RequestBody AdoptionContractDTO dto) throws EntityNotFoundException, IllegalOperationException {
        AdoptionContractEntity entity = modelMapper.map(dto, AdoptionContractEntity.class);
        AdoptionContractEntity updatedEntity = contractService.updateContract(id, entity);
        return modelMapper.map(updatedEntity, AdoptionContractDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        contractService.deleteContract(id);
    }
}