package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.VaccinationDTO;
import co.edu.udistrital.mdp.pets.entities.VaccinationEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.VaccinationService;

/** Carné de vacunación digital de una mascota; con `upcoming=true` solo las próximas dosis. */
@RestController
@RequestMapping("/pets/{petId}/vaccinations")
public class VaccinationController {

    private final VaccinationService vaccinationService;
    private final ModelMapper modelMapper;

    public VaccinationController(VaccinationService vaccinationService, ModelMapper modelMapper) {
        this.vaccinationService = vaccinationService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VaccinationDTO create(@PathVariable Long petId, @RequestBody VaccinationDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        VaccinationEntity created = vaccinationService.createVaccination(petId,
                modelMapper.map(dto, VaccinationEntity.class));
        return modelMapper.map(created, VaccinationDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<VaccinationDTO> findAll(@PathVariable Long petId,
            @RequestParam(required = false, defaultValue = "false") boolean upcoming)
            throws EntityNotFoundException {
        List<VaccinationEntity> vaccinations = upcoming
                ? vaccinationService.getUpcomingVaccinations(petId)
                : vaccinationService.getVaccinations(petId);
        return modelMapper.map(vaccinations, new TypeToken<List<VaccinationDTO>>() {
        }.getType());
    }

    @PutMapping("/{vaccinationId}")
    @ResponseStatus(HttpStatus.OK)
    public VaccinationDTO update(@PathVariable Long petId, @PathVariable Long vaccinationId,
            @RequestBody VaccinationDTO dto) throws EntityNotFoundException, IllegalOperationException {
        VaccinationEntity updated = vaccinationService.updateVaccination(petId, vaccinationId,
                modelMapper.map(dto, VaccinationEntity.class));
        return modelMapper.map(updated, VaccinationDTO.class);
    }

    @DeleteMapping("/{vaccinationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long petId, @PathVariable Long vaccinationId) throws EntityNotFoundException {
        vaccinationService.deleteVaccination(petId, vaccinationId);
    }
}
