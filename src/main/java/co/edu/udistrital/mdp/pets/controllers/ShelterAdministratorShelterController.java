package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import co.edu.udistrital.mdp.pets.dto.ShelterDTO;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.ShelterAdministratorService;

@RestController
@RequestMapping("/administrators")
public class ShelterAdministratorShelterController {

    private final ShelterAdministratorService administratorService;
    private final ModelMapper modelMapper;

    public ShelterAdministratorShelterController(ShelterAdministratorService administratorService,
            ModelMapper modelMapper) {
        this.administratorService = administratorService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/{administratorId}/shelters/{shelterId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ShelterDTO addShelter(@PathVariable Long administratorId, @PathVariable Long shelterId)
            throws EntityNotFoundException, IllegalOperationException {
        return modelMapper.map(administratorService.addShelter(administratorId, shelterId), ShelterDTO.class);
    }

    @GetMapping("/{administratorId}/shelters")
    @ResponseStatus(HttpStatus.OK)
    public List<ShelterDTO> getShelters(@PathVariable Long administratorId) throws EntityNotFoundException {
        return modelMapper.map(administratorService.getShelters(administratorId),
                new TypeToken<List<ShelterDTO>>() {}.getType());
    }

    @GetMapping("/{administratorId}/shelters/{shelterId}")
    @ResponseStatus(HttpStatus.OK)
    public ShelterDTO getShelter(@PathVariable Long administratorId, @PathVariable Long shelterId)
            throws EntityNotFoundException, IllegalOperationException {
        return modelMapper.map(administratorService.getShelter(administratorId, shelterId), ShelterDTO.class);
    }

    @DeleteMapping("/{administratorId}/shelters/{shelterId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeShelter(@PathVariable Long administratorId, @PathVariable Long shelterId)
            throws EntityNotFoundException, IllegalOperationException {
        administratorService.removeShelter(administratorId, shelterId);
    }
}
