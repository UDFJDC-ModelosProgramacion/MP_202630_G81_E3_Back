package co.edu.udistrital.mdp.pets.services;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.NotificationEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.repositories.NotificationRepository;

/**
 * Genera las notificaciones automáticas de un refugio (citas de convivencia, actualizaciones del
 * carné de vacunación, seguimientos y eventos médicos). Nunca falla por datos incompletos:
 * si no hay refugio o texto simplemente no hace nada.
 */
@Service
public class AutoNotificationService {

    private static final int MAX_LENGTH = 255;

    private final NotificationRepository notificationRepository;

    public AutoNotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void notifyShelter(ShelterEntity shelter, String content) {
        if (shelter == null || content == null || content.isBlank())
            return;
        NotificationEntity notification = new NotificationEntity();
        notification.setContent(content.length() > MAX_LENGTH ? content.substring(0, MAX_LENGTH) : content);
        notification.setDate(new Date());
        notification.setShelter(shelter);
        notificationRepository.save(notification);
    }
}
