package co.edu.udistrital.mdp.pets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.udistrital.mdp.pets.entities.CoexistenceTestEntity;

public interface CoexistenceTestRepository extends JpaRepository<CoexistenceTestEntity, Long> {
}