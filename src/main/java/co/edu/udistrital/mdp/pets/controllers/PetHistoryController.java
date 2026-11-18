package co.edu.udistrital.mdp.pets.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.PetHistoryDTO;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.services.PetHistoryService;

/** Historial completo de una mascota: eventos, adopciones y devoluciones. */
@RestController
@RequestMapping("/pets/{petId}/history")
public class PetHistoryController {

    private final PetHistoryService petHistoryService;

    public PetHistoryController(PetHistoryService petHistoryService) {
        this.petHistoryService = petHistoryService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PetHistoryDTO findHistory(@PathVariable Long petId) throws EntityNotFoundException {
        return petHistoryService.getHistory(petId);
    }
}
