package co.edu.udistrital.mdp.pets.repositories;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface AdoptionRepository extends JpaRepository<AdoptionEntity, Long> {
    List<AdoptionEntity> findByPetId(Long petId);

    List<AdoptionEntity> findByVeterinarianId(Long veterinarianId);

    List<AdoptionEntity> findByPetShelterId(Long shelterId);
}