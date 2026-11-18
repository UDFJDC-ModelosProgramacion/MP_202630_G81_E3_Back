package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.FollowUpDTO;
import co.edu.udistrital.mdp.pets.entities.FollowUpEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ForbiddenOperationException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.AutoNotificationService;
import co.edu.udistrital.mdp.pets.services.FollowUpService;

/**
 * Seguimiento de adaptación de una adopción. `adopterId` identifica al adoptante que consulta;
 * si la adopción no es suya la respuesta es 403 sin datos del seguimiento.
 */
@RestController
@RequestMapping("/adoptions/{adoptionId}/follow-ups")
public class FollowUpController {

    private final FollowUpService followUpService;
    private final ModelMapper modelMapper;

    private final AutoNotificationService notificationService;

    public FollowUpController(FollowUpService followUpService, ModelMapper modelMapper,
            AutoNotificationService notificationService) {
        this.followUpService = followUpService;
        this.modelMapper = modelMapper;
        this.notificationService = notificationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FollowUpDTO create(@PathVariable Long adoptionId, @RequestParam(required = false) Long adopterId,
            @RequestBody FollowUpDTO followUpDTO)
            throws EntityNotFoundException, ForbiddenOperationException, IllegalOperationException {
        FollowUpEntity created = followUpService.createFollowUp(adoptionId, adopterId,
                modelMapper.map(followUpDTO, FollowUpEntity.class));
        if (created.getAdoption() != null && created.getAdoption().getPet() != null) {
            notificationService.notifyShelter(created.getAdoption().getPet().getShelter(),
                    "Nuevo seguimiento de " + created.getAdoption().getPet().getName() + ": "
                            + created.getAdaptationStatus());
        }
        return modelMapper.map(created, FollowUpDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FollowUpDTO> findAll(@PathVariable Long adoptionId, @RequestParam(required = false) Long adopterId)
            throws EntityNotFoundException, ForbiddenOperationException {
        return modelMapper.map(followUpService.getFollowUps(adoptionId, adopterId),
                new TypeToken<List<FollowUpDTO>>() {
                }.getType());
    }
}
