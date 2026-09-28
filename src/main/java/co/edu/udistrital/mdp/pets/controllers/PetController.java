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

import co.edu.udistrital.mdp.pets.dto.PetDTO;
import co.edu.udistrital.mdp.pets.dto.PetDetailDTO;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.PetService;

@RestController
@RequestMapping("/pets")
public class PetController {

    private PetService petService;
    private ModelMapper modelMapper;

    public PetController(PetService petService, ModelMapper modelMapper) {
        this.petService = petService;
        this.modelMapper = modelMapper;
    }

    /** Crea una mascota. El body es PetDetailDTO porque debe incluir su evento ARRIVAL inicial. */
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public PetDTO create(@RequestBody PetDetailDTO petDetailDTO)
            throws EntityNotFoundException, IllegalOperationException {
        PetEntity petEntity = petService.createPet(modelMapper.map(petDetailDTO, PetEntity.class));
        return modelMapper.map(petEntity, PetDTO.class);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<PetDetailDTO> findAll() {
        List<PetEntity> pets = petService.getPets();
        return modelMapper.map(pets, new TypeToken<List<PetDetailDTO>>() {
        }.getType());
    }

    /** Mascotas disponibles, con filtros opcionales: /pets/available?activityLevel=HIGH&spaceRequirement=LARGE */
    @GetMapping("/available")
    @ResponseStatus(code = HttpStatus.OK)
    public List<PetDTO> findAvailable(@RequestParam(required = false) String activityLevel,
            @RequestParam(required = false) String spaceRequirement) {
        List<PetEntity> pets = petService.getAvailablePetsByFilters(activityLevel, spaceRequirement);
        return modelMapper.map(pets, new TypeToken<List<PetDTO>>() {
        }.getType());
    }

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public PetDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        PetEntity petEntity = petService.getPet(id);
        return modelMapper.map(petEntity, PetDetailDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public PetDTO update(@PathVariable Long id, @RequestBody PetDTO petDTO)
            throws EntityNotFoundException, IllegalOperationException {
        PetEntity petEntity = petService.updatePet(id, modelMapper.map(petDTO, PetEntity.class));
        return modelMapper.map(petEntity, PetDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws EntityNotFoundException, IllegalOperationException {
        petService.deletePet(id);
    }
}
