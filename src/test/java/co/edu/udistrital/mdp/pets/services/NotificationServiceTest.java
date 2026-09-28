package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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

    private static final Long NON_EXISTENT_ID = 999999L;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();

    private ShelterEntity shelter;
    private final List<NotificationEntity> notificationList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.createQuery("delete from NotificationEntity").executeUpdate();
        entityManager.createQuery("delete from ShelterEntity").executeUpdate();
        notificationList.clear();
    }

    private void insertData() {
        shelter = createShelter();
        for (int i = 0; i < 3; i++) {
            notificationList.add(persistNotification(shelter));
        }
    }

    private ShelterEntity createShelter() {
        ShelterEntity entity = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(entity);
        return entity;
    }

    private NotificationEntity buildNotification() {
        NotificationEntity entity = factory.manufacturePojo(NotificationEntity.class);
        entity.setContent("Valid content");
        entity.setDate(new Date());
        return entity;
    }

    private NotificationEntity persistNotification(ShelterEntity owner) {
        NotificationEntity entity = buildNotification();
        entity.setShelter(owner);
        entityManager.persist(entity);
        return entity;
    }


    @Test
    void createNotification_shouldPersistNotification()
            throws EntityNotFoundException, IllegalOperationException {
        NotificationEntity newEntity = buildNotification();

        NotificationEntity result = notificationService.createNotification(shelter.getId(), newEntity);

        assertNotNull(result.getId());
        NotificationEntity stored = entityManager.find(NotificationEntity.class, result.getId());
        assertEquals(newEntity.getContent(), stored.getContent());
        assertEquals(newEntity.getDate(), stored.getDate());
        assertEquals(shelter.getId(), stored.getShelter().getId());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    void createNotification_withInvalidContent_shouldThrowException(String invalidContent) {
        NotificationEntity newEntity = buildNotification();
        newEntity.setContent(invalidContent);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> notificationService.createNotification(shelter.getId(), newEntity));
        assertEquals("Content is not valid", ex.getMessage());
    }

    @Test
    void createNotification_withNullDate_shouldThrowException() {
        NotificationEntity newEntity = buildNotification();
        newEntity.setDate(null);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> notificationService.createNotification(shelter.getId(), newEntity));
        assertEquals("Date is not valid", ex.getMessage());
    }

    @Test
    void createNotification_withInvalidShelterId_shouldThrowException() {
        NotificationEntity newEntity = buildNotification();

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> notificationService.createNotification(NON_EXISTENT_ID, newEntity));
        assertEquals("Shelter not found", ex.getMessage());
    }


    @Test
    void getNotifications_shouldReturnOnlyNotificationsOfShelter() throws EntityNotFoundException {
        ShelterEntity otherShelter = createShelter();
        persistNotification(otherShelter);

        List<NotificationEntity> result = notificationService.getNotifications(shelter.getId());

        assertEquals(notificationList.size(), result.size());
        for (NotificationEntity notification : result) {
            assertEquals(shelter.getId(), notification.getShelter().getId());
        }
    }

    @Test
    void getNotifications_withShelterWithoutNotifications_shouldReturnEmptyList()
            throws EntityNotFoundException {
        ShelterEntity emptyShelter = createShelter();

        assertTrue(notificationService.getNotifications(emptyShelter.getId()).isEmpty());
    }

    @Test
    void getNotifications_withInvalidShelterId_shouldThrowException() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> notificationService.getNotifications(NON_EXISTENT_ID));
        assertEquals("Shelter not found", ex.getMessage());
    }

    @Test
    void getNotification_shouldReturnNotification()
            throws EntityNotFoundException, IllegalOperationException {
        NotificationEntity existing = notificationList.get(0);

        NotificationEntity result = notificationService.getNotification(shelter.getId(), existing.getId());

        assertEquals(existing.getId(), result.getId());
        assertEquals(existing.getContent(), result.getContent());
    }

    @Test
    void getNotification_withInvalidShelterId_shouldThrowException() {
        Long notificationId = notificationList.get(0).getId();

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> notificationService.getNotification(NON_EXISTENT_ID, notificationId));
        assertEquals("Shelter not found", ex.getMessage());
    }

    @Test
    void getNotification_withInvalidNotificationId_shouldThrowException() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> notificationService.getNotification(shelter.getId(), NON_EXISTENT_ID));
        assertEquals("Notification not found", ex.getMessage());
    }

    @Test
    void getNotification_withoutShelter_shouldThrowException() {
        NotificationEntity orphan = persistNotification(null);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> notificationService.getNotification(shelter.getId(), orphan.getId()));
        assertEquals("The notification does not belong to the given shelter", ex.getMessage());
    }

    @Test
    void getNotification_ofAnotherShelter_shouldThrowException() {
        ShelterEntity otherShelter = createShelter();
        NotificationEntity foreign = persistNotification(otherShelter);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> notificationService.getNotification(shelter.getId(), foreign.getId()));
        assertEquals("The notification does not belong to the given shelter", ex.getMessage());
    }


    @Test
    void deleteNotification_shouldDeleteNotification()
            throws EntityNotFoundException, IllegalOperationException {
        NotificationEntity existing = notificationList.get(0);

        notificationService.deleteNotification(shelter.getId(), existing.getId());

        assertNull(entityManager.find(NotificationEntity.class, existing.getId()));
        assertNotNull(entityManager.find(NotificationEntity.class, notificationList.get(1).getId()));
    }

    @Test
    void deleteNotification_withInvalidShelterId_shouldThrowException() {
        Long notificationId = notificationList.get(0).getId();

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> notificationService.deleteNotification(NON_EXISTENT_ID, notificationId));
        assertEquals("Shelter not found", ex.getMessage());
    }

    @Test
    void deleteNotification_withInvalidNotificationId_shouldThrowException() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> notificationService.deleteNotification(shelter.getId(), NON_EXISTENT_ID));
        assertEquals("Notification not found", ex.getMessage());
    }

    @Test
    void deleteNotification_ofAnotherShelter_shouldThrowException() {
        ShelterEntity otherShelter = createShelter();
        NotificationEntity foreign = persistNotification(otherShelter);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> notificationService.deleteNotification(shelter.getId(), foreign.getId()));
        assertEquals("The notification does not belong to the given shelter", ex.getMessage());
        assertNotNull(entityManager.find(NotificationEntity.class, foreign.getId()));
    }
}