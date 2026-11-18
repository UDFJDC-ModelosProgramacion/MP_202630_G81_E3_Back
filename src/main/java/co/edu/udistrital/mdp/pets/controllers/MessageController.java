package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.MessageDTO;
import co.edu.udistrital.mdp.pets.entities.MessageEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.MessageService;

/** Mensajes con un refugio; `petId` (opcional) enlaza la conversación con una mascota. */
@RestController
@RequestMapping("/shelters/{shelterId}/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDTO create(@PathVariable Long shelterId, @RequestParam(required = false) Long petId,
            @RequestBody MessageDTO dto) throws EntityNotFoundException, IllegalOperationException {
        MessageEntity entity = new MessageEntity();
        entity.setContent(dto.getContent());
        entity.setSenderName(dto.getSenderName());
        entity.setSenderRole(dto.getSenderRole());
        return toDto(messageService.createMessage(shelterId, petId != null ? petId : dto.getPetId(), entity));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MessageDTO> findAll(@PathVariable Long shelterId, @RequestParam(required = false) Long petId)
            throws EntityNotFoundException {
        return messageService.getMessages(shelterId, petId).stream().map(MessageController::toDto).toList();
    }

    private static MessageDTO toDto(MessageEntity entity) {
        MessageDTO dto = new MessageDTO();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setSenderName(entity.getSenderName());
        dto.setSenderRole(entity.getSenderRole());
        dto.setSentAt(entity.getSentAt());
        dto.setPetId(entity.getPet() == null ? null : entity.getPet().getId());
        return dto;
    }
}
