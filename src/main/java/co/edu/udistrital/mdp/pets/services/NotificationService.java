package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.NotificationRepository;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    @Transactional
    public NotificationEntity createNotification(Long shelterId, NotificationEntity notificationEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de creación de una notificación para el refugio con id = {0}", shelterId);

        if (notificationEntity.getContent() == null || notificationEntity.getContent().isBlank())
            throw new IllegalOperationException("Content is not valid");

        if (notificationEntity.getDate() == null)
            throw new IllegalOperationException("Date is not valid");

        ShelterEntity shelter = findShelterOrThrow(shelterId);
        notificationEntity.setShelter(shelter);

        log.info("Termina proceso de creación de una notificación para el refugio con id = {0}", shelterId);
        return notificationRepository.save(notificationEntity);
    }

    @Transactional
    public List<NotificationEntity> getNotifications(Long shelterId) throws EntityNotFoundException {
        log.info("Inicia proceso de consultar las notificaciones del refugio con id = {0}", shelterId);
        findShelterOrThrow(shelterId);
        return notificationRepository.findByShelterId(shelterId);
    }

    @Transactional
    public NotificationEntity getNotification(Long shelterId, Long notificationId)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de consultar la notificación con id = {0} del refugio con id = {1}", notificationId, shelterId);
        findShelterOrThrow(shelterId);
        return findNotificationOrThrow(shelterId, notificationId);
    }

    @Transactional
    public void deleteNotification(Long shelterId, Long notificationId)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de borrar la notificación con id = {0} del refugio con id = {1}", notificationId, shelterId);

        findShelterOrThrow(shelterId);
        findNotificationOrThrow(shelterId, notificationId);

        notificationRepository.deleteById(notificationId);
        log.info("Termina proceso de borrar la notificación con id = {0} del refugio con id = {1}", notificationId, shelterId);
    }

    private ShelterEntity findShelterOrThrow(Long shelterId) throws EntityNotFoundException {
        Optional<ShelterEntity> shelterEntity = shelterRepository.findById(shelterId);
        if (shelterEntity.isEmpty())
            throw new EntityNotFoundException("Shelter not found");
        return shelterEntity.get();
    }

    private NotificationEntity findNotificationOrThrow(Long shelterId, Long notificationId)
            throws EntityNotFoundException, IllegalOperationException {
        Optional<NotificationEntity> notificationEntity = notificationRepository.findById(notificationId);
        if (notificationEntity.isEmpty())
            throw new EntityNotFoundException("Notification not found");

        NotificationEntity notification = notificationEntity.get();
        if (notification.getShelter() == null || !notification.getShelter().getId().equals(shelterId))
            throw new IllegalOperationException("The notification does not belong to the given shelter");

        return notification;
    }
}