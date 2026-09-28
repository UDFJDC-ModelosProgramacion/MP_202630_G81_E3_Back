package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import co.edu.udistrital.mdp.pets.dto.NotificationDTO;
import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.NotificationService;

@RestController
@RequestMapping("/shelters/{shelterId}/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final ModelMapper modelMapper;

    public NotificationController(NotificationService notificationService, ModelMapper modelMapper) {
        this.notificationService = notificationService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationDTO create(@PathVariable Long shelterId, @RequestBody NotificationDTO notificationDTO)
            throws EntityNotFoundException, IllegalOperationException {
        NotificationEntity created = notificationService.createNotification(shelterId,
                modelMapper.map(notificationDTO, NotificationEntity.class));
        return modelMapper.map(created, NotificationDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationDTO> findAll(@PathVariable Long shelterId) throws EntityNotFoundException {
        return modelMapper.map(notificationService.getNotifications(shelterId),
                new TypeToken<List<NotificationDTO>>() {}.getType());
    }

    @GetMapping("/{notificationId}")
    @ResponseStatus(HttpStatus.OK)
    public NotificationDTO findOne(@PathVariable Long shelterId, @PathVariable Long notificationId)
            throws EntityNotFoundException, IllegalOperationException {
        return modelMapper.map(notificationService.getNotification(shelterId, notificationId),
                NotificationDTO.class);
    }

    @DeleteMapping("/{notificationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long shelterId, @PathVariable Long notificationId)
            throws EntityNotFoundException, IllegalOperationException {
        notificationService.deleteNotification(shelterId, notificationId);
    }
}