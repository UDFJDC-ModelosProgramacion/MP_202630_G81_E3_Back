package co.edu.udistrital.mdp.pets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.udistrital.mdp.pets.entities.BreedEntity;

public interface BreedRepository extends JpaRepository<BreedEntity, Long> {

}
