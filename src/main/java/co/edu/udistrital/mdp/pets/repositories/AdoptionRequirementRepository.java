package co.edu.udistrital.mdp.pets.repositories;

import co.edu.udistrital.mdp.pets.entities.AdoptionRequirementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdoptionRequirementRepository extends JpaRepository<AdoptionRequirementEntity, Long> {
}