package co.edu.udistrital.mdp.pets.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.udistrital.mdp.pets.entities.FollowUpEntity;

public interface FollowUpRepository extends JpaRepository<FollowUpEntity, Long> {

    /** Seguimientos de una adopción, del más reciente al más antiguo. */
    List<FollowUpEntity> findByAdoptionIdOrderByDateDescIdDesc(Long adoptionId);
}
