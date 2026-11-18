package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.MessageEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import jakarta.persistence.EntityManager;

@DataJpaTest
@Transactional
@Import({ MessageService.class, AutoNotificationService.class })
class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private EntityManager entityManager;

    private ShelterEntity shelter;
    private PetEntity pet;

    @BeforeEach
    void setUp() {
        shelter = TestData.shelter(entityManager, "Huellas");
        pet = TestData.pet(entityManager, shelter, "Luna", "AVAILABLE");
    }

    private MessageEntity message(String content, String name, String role) {
        MessageEntity entity = new MessageEntity();
        entity.setContent(content);
        entity.setSenderName(name);
        entity.setSenderRole(role);
        return entity;
    }

    @Test
    void createMessage_fromAdopter_shouldPersistAndNotifyShelter() throws Exception {
        MessageEntity result = messageService.createMessage(shelter.getId(), pet.getId(),
                message("¿Puedo visitarla el sábado?", "Ana", "ADOPTER"));

        assertNotNull(result.getId());
        assertNotNull(result.getSentAt());
        assertEquals(pet.getId(), result.getPet().getId());
        Long notifications = entityManager
                .createQuery("select count(n) from NotificationEntity n where n.shelter.id = :id", Long.class)
                .setParameter("id", shelter.getId()).getSingleResult();
        assertEquals(1L, notifications);
    }

    @Test
    void createMessage_fromShelter_shouldNotNotify() throws Exception {
        messageService.createMessage(shelter.getId(), null, message("Claro, los esperamos.", "Refugio", "SHELTER"));

        Long notifications = entityManager.createQuery("select count(n) from NotificationEntity n", Long.class)
                .getSingleResult();
        assertEquals(0L, notifications);
    }

    @Test
    void createMessage_withBlankContent_shouldThrow() {
        assertThrows(IllegalOperationException.class,
                () -> messageService.createMessage(shelter.getId(), null, message(" ", "Ana", "ADOPTER")));
    }

    @Test
    void createMessage_withInvalidRole_shouldThrow() {
        assertThrows(IllegalOperationException.class,
                () -> messageService.createMessage(shelter.getId(), null, message("Hola", "Ana", "ADMIN")));
    }

    @Test
    void createMessage_withPetFromOtherShelter_shouldThrow() {
        PetEntity foreign = TestData.pet(entityManager, TestData.shelter(entityManager, "Otro"), "Rex", "AVAILABLE");

        assertThrows(IllegalOperationException.class, () -> messageService.createMessage(shelter.getId(),
                foreign.getId(), message("Hola", "Ana", "ADOPTER")));
    }

    @Test
    void createMessage_withUnknownShelter_shouldThrow() {
        assertThrows(EntityNotFoundException.class,
                () -> messageService.createMessage(999999L, null, message("Hola", "Ana", "ADOPTER")));
    }

    @Test
    void getMessages_shouldFilterByPet() throws Exception {
        PetEntity second = TestData.pet(entityManager, shelter, "Max", "AVAILABLE");
        messageService.createMessage(shelter.getId(), pet.getId(), message("Sobre Luna", "Ana", "ADOPTER"));
        messageService.createMessage(shelter.getId(), second.getId(), message("Sobre Max", "Ana", "ADOPTER"));

        assertEquals(2, messageService.getMessages(shelter.getId(), null).size());
        assertEquals(1, messageService.getMessages(shelter.getId(), pet.getId()).size());
    }
}
