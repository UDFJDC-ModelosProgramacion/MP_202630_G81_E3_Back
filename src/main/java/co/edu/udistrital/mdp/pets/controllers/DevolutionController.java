package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.CaseDevolutionDetailDTO;
import co.edu.udistrital.mdp.pets.services.CaseDevolutionService;

/** Historial de todas las devoluciones registradas (base del reporte de devoluciones). */
@RestController
@RequestMapping("/devolutions")
public class DevolutionController {

    private final CaseDevolutionService caseDevolutionService;
    private final ModelMapper modelMapper;

    public DevolutionController(CaseDevolutionService caseDevolutionService, ModelMapper modelMapper) {
        this.caseDevolutionService = caseDevolutionService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CaseDevolutionDetailDTO> findAll() {
        return modelMapper.map(caseDevolutionService.getCaseDevolutions(),
                new TypeToken<List<CaseDevolutionDetailDTO>>() {
                }.getType());
    }
}