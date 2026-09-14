package co.edu.udistrital.mdp.pets.repositories;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.udistrital.mdp.pets.entities.PetEventEntity;

public interface PetEventRepository extends JpaRepository<PetEventEntity, Long> {

    List<PetEventEntity> findByPetIdOrderByDateAsc(Long petId);

    List<PetEventEntity> findByPetIdAndEventType(Long petId, String eventType);

    List<PetEventEntity> findByEventTypeAndDateBetween(String eventType, Date start, Date end);
}