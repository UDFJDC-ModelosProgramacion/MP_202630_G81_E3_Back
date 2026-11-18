package co.edu.udistrital.mdp.pets.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.MessageEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.MessageRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
import lombok.extern.slf4j.Slf4j;

/** Mensajería entre los interesados en adoptar y un refugio (dudas sobre mascotas y coordinación de visitas). */
@Slf4j
@Service
public class MessageService {

    public static final String ADOPTER_ROLE = "ADOPTER";
    public static final String SHELTER_ROLE = "SHELTER";
    private static final Set<String> ROLES = Set.of(ADOPTER_ROLE, SHELTER_ROLE);
    private static final int MAX_CONTENT_LENGTH = 1000;
    private static final int MAX_NAME_LENGTH = 100;

    private final MessageRepository messageRepository;
    private final ShelterRepository shelterRepository;
    private final PetRepository petRepository;
    private final AutoNotificationService notificationService;

    public MessageService(MessageRepository messageRepository, ShelterRepository shelterRepository,
            PetRepository petRepository, AutoNotificationService notificationService) {
        this.messageRepository = messageRepository;
        this.shelterRepository = shelterRepository;
        this.petRepository = petRepository;
        this.notificationService = notificationService;
    }

    /** Envía un mensaje al refugio (o la respuesta del refugio). `petId` es opcional. */
    @Transactional
    public MessageEntity createMessage(Long shelterId, Long petId, MessageEntity message)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de envío de un mensaje al refugio con id = {}", shelterId);
        ShelterEntity shelter = findShelterOrThrow(shelterId);

        if (message.getContent() == null || message.getContent().isBlank())
            throw new IllegalOperationException("Content is not valid");
        if (message.getContent().length() > MAX_CONTENT_LENGTH)
            throw new IllegalOperationException("Content cannot exceed " + MAX_CONTENT_LENGTH + " characters");
        if (message.getSenderName() == null || message.getSenderName().isBlank()
                || message.getSenderName().length() > MAX_NAME_LENGTH)
            throw new IllegalOperationException("Sender name is not valid");
        if (message.getSenderRole() == null || !ROLES.contains(message.getSenderRole()))
            throw new IllegalOperationException("Sender role must be one of " + ROLES);

        PetEntity pet = null;
        if (petId != null) {
            pet = petRepository.findById(petId).orElseThrow(() -> new EntityNotFoundException("Pet not found"));
            if (pet.getShelter() == null || !shelterId.equals(pet.getShelter().getId()))
                throw new IllegalOperationException("The pet does not belong to this shelter");
        }

        message.setId(null);
        message.setShelter(shelter);
        message.setPet(pet);
        message.setSentAt(LocalDateTime.now());
        MessageEntity saved = messageRepository.save(message);

        if (ADOPTER_ROLE.equals(saved.getSenderRole()))
            notificationService.notifyShelter(shelter, "Nuevo mensaje de " + saved.getSenderName()
                    + (pet == null ? "." : " sobre " + pet.getName() + "."));
        return saved;
    }

    @Transactional(readOnly = true)
    public List<MessageEntity> getMessages(Long shelterId, Long petId) throws EntityNotFoundException {
        findShelterOrThrow(shelterId);
        return petId == null
                ? messageRepository.findByShelterIdOrderBySentAtAscIdAsc(shelterId)
                : messageRepository.findByShelterIdAndPetIdOrderBySentAtAscIdAsc(shelterId, petId);
    }

    private ShelterEntity findShelterOrThrow(Long shelterId) throws EntityNotFoundException {
        return shelterRepository.findById(shelterId).orElseThrow(() -> new EntityNotFoundException("Shelter not found"));
    }
}
