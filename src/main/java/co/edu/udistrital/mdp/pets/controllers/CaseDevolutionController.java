package co.edu.udistrital.mdp.pets.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import co.edu.udistrital.mdp.pets.dto.CaseDevolutionDTO;
import co.edu.udistrital.mdp.pets.entities.CaseDevolutionEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.CaseDevolutionService;

/** Devolución de una adopción: cada adopción tiene como máximo una. */
@RestController
@RequestMapping("/adoptions/{adoptionId}/devolution")
public class CaseDevolutionController {

    private final CaseDevolutionService caseDevolutionService;
    private final ModelMapper modelMapper;

    public CaseDevolutionController(CaseDevolutionService caseDevolutionService, ModelMapper modelMapper) {
        this.caseDevolutionService = caseDevolutionService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CaseDevolutionDTO create(@PathVariable Long adoptionId, @RequestBody CaseDevolutionDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        CaseDevolutionEntity created = caseDevolutionService.createCaseDevolution(adoptionId,
                modelMapper.map(dto, CaseDevolutionEntity.class));
        return modelMapper.map(created, CaseDevolutionDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public CaseDevolutionDTO findOne(@PathVariable Long adoptionId) throws EntityNotFoundException {
        return modelMapper.map(caseDevolutionService.getCaseDevolution(adoptionId), CaseDevolutionDTO.class);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public CaseDevolutionDTO update(@PathVariable Long adoptionId, @RequestBody CaseDevolutionDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        CaseDevolutionEntity updated = caseDevolutionService.updateCaseDevolution(adoptionId,
                modelMapper.map(dto, CaseDevolutionEntity.class));
        return modelMapper.map(updated, CaseDevolutionDTO.class);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long adoptionId) throws EntityNotFoundException {
        caseDevolutionService.deleteCaseDevolution(adoptionId);
    }
}
