package co.edu.udistrital.mdp.pets.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.pets.entities.NotificationEntity;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findByShelterId(Long shelterId);
}