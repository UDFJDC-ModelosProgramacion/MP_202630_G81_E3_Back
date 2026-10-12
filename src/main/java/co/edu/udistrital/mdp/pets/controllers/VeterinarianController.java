package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import co.edu.udistrital.mdp.pets.dto.VeterinarianDTO;
import co.edu.udistrital.mdp.pets.dto.VeterinarianDetailDTO;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.VeterinarianService;

@RestController
@RequestMapping("/veterinarians")
public class VeterinarianController {

    private final VeterinarianService veterinarianService;
    private final ModelMapper modelMapper;

    public VeterinarianController(
            VeterinarianService veterinarianService,
            ModelMapper modelMapper) {
        this.veterinarianService = veterinarianService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VeterinarianDTO create(
            @RequestBody VeterinarianDTO veterinarianDTO)
            throws IllegalOperationException {

        VeterinarianEntity created =
                veterinarianService.createVeterinarian(
                    modelMapper.map(
                        veterinarianDTO,
                        VeterinarianEntity.class
                    )
                );

        return modelMapper.map(created, VeterinarianDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<VeterinarianDetailDTO> findAll() {

        List<VeterinarianEntity> veterinarians =
                veterinarianService.getVeterinarians();

        return modelMapper.map(
            veterinarians,
            new TypeToken<List<VeterinarianDetailDTO>>() {}.getType()
        );
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VeterinarianDetailDTO findOne(@PathVariable Long id)
            throws EntityNotFoundException {

        VeterinarianEntity veterinarian =
                veterinarianService.getVeterinarian(id);

        return modelMapper.map(
            veterinarian,
            VeterinarianDetailDTO.class
        );
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public VeterinarianDTO update(
            @PathVariable Long id,
            @RequestBody VeterinarianDTO veterinarianDTO)
            throws EntityNotFoundException, IllegalOperationException {

        VeterinarianEntity updated =
                veterinarianService.updateVeterinarian(
                    id,
                    modelMapper.map(
                        veterinarianDTO,
                        VeterinarianEntity.class
                    )
                );

        return modelMapper.map(updated, VeterinarianDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id)
            throws EntityNotFoundException, IllegalOperationException {

        veterinarianService.deleteVeterinarian(id);
    }
}
