package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

import jakarta.persistence.EntityManager;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(NotificationService.class)
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private ShelterEntity shelter = new ShelterEntity();
    private List<NotificationEntity> notificationList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.createQuery("delete from NotificationEntity").executeUpdate();
        entityManager.createQuery("delete from ShelterEntity").executeUpdate();
    }

    private void insertData() {
        shelter = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(shelter);

        for (int i = 0; i < 3; i++) {
            NotificationEntity entity = factory.manufacturePojo(NotificationEntity.class);
            entity.setShelter(shelter);
            entityManager.persist(entity);
            notificationList.add(entity);
        }
    }

    @Test
    void createNotification_shouldPersistNotification() throws EntityNotFoundException, IllegalOperationException {
        NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
        NotificationEntity result = notificationService.createNotification(shelter.getId(), newEntity);

        assertNotNull(result);
        NotificationEntity stored = entityManager.find(NotificationEntity.class, result.getId());
        assertEquals(newEntity.getContent(), stored.getContent());
        assertEquals(shelter.getId(), stored.getShelter().getId());
    }

    @Test
    void createNotification_withInvalidShelterId_shouldThrowException() {
        NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
        assertThrows(EntityNotFoundException.class, () -> notificationService.createNotification(0L, newEntity));
    }

    @Test
    void createNotification_withBlankContent_shouldThrowException() {
        NotificationEntity newEntity = factory.manufacturePojo(NotificationEntity.class);
        newEntity.setContent("");
        assertThrows(IllegalOperationException.class, () -> notificationService.createNotification(shelter.getId(), newEntity));
    }

    @Test
    void getNotifications_shouldReturnAllNotificationsOfShelter() throws EntityNotFoundException {
        List<NotificationEntity> result = notificationService.getNotifications(shelter.getId());
        assertEquals(notificationList.size(), result.size());
    }

    @Test
    void getNotification_shouldReturnNotification() throws EntityNotFoundException, IllegalOperationException {
        NotificationEntity existing = notificationList.get(0);
        NotificationEntity result = notificationService.getNotification(shelter.getId(), existing.getId());
        assertNotNull(result);
        assertEquals(existing.getContent(), result.getContent());
    }

    @Test
    void deleteNotification_shouldDeleteNotification() throws EntityNotFoundException, IllegalOperationException {
        NotificationEntity existing = notificationList.get(0);
        notificationService.deleteNotification(shelter.getId(), existing.getId());
        assertNull(entityManager.find(NotificationEntity.class, existing.getId()));
    }

    @Test
    void deleteNotification_withInvalidShelterId_shouldThrowException() {
        NotificationEntity existing = notificationList.get(0);
        assertThrows(EntityNotFoundException.class, () -> notificationService.deleteNotification(0L, existing.getId()));
    }
}