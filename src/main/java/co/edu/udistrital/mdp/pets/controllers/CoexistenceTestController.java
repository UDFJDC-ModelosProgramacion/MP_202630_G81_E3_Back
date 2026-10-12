package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import co.edu.udistrital.mdp.pets.dto.CoexistenceTestDTO;
import co.edu.udistrital.mdp.pets.dto.CoexistenceTestDetailDTO;
import co.edu.udistrital.mdp.pets.entities.CoexistenceTestEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.CoexistenceTestService;

@RestController
@RequestMapping("/coexistence-tests")
public class CoexistenceTestController {

    private final CoexistenceTestService coexistenceTestService;
    private final ModelMapper modelMapper;

    public CoexistenceTestController(
            CoexistenceTestService coexistenceTestService,
            ModelMapper modelMapper) {
        this.coexistenceTestService = coexistenceTestService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CoexistenceTestDTO create(
            @RequestBody CoexistenceTestDTO coexistenceTestDTO)
            throws IllegalOperationException {

        CoexistenceTestEntity created =
                coexistenceTestService.createCoexistenceTest(
                    modelMapper.map(
                        coexistenceTestDTO,
                        CoexistenceTestEntity.class
                    )
                );

        return modelMapper.map(created, CoexistenceTestDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CoexistenceTestDetailDTO> findAll() {

        List<CoexistenceTestEntity> tests =
                coexistenceTestService.getCoexistenceTests();

        return modelMapper.map(
            tests,
            new TypeToken<List<CoexistenceTestDetailDTO>>() {}.getType()
        );
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CoexistenceTestDetailDTO findOne(@PathVariable Long id)
            throws EntityNotFoundException {

        CoexistenceTestEntity test =
                coexistenceTestService.getCoexistenceTest(id);

        return modelMapper.map(test, CoexistenceTestDetailDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CoexistenceTestDTO update(
            @PathVariable Long id,
            @RequestBody CoexistenceTestDTO coexistenceTestDTO)
            throws EntityNotFoundException, IllegalOperationException {

        CoexistenceTestEntity updated =
                coexistenceTestService.updateCoexistenceTest(
                    id,
                    modelMapper.map(
                        coexistenceTestDTO,
                        CoexistenceTestEntity.class
                    )
                );

        return modelMapper.map(updated, CoexistenceTestDTO.class);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id)
            throws EntityNotFoundException, IllegalOperationException {

        coexistenceTestService.deleteCoexistenceTest(id);
    }
}
