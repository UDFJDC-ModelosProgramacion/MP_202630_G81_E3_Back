package co.edu.udistrital.mdp.pets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.udistrital.mdp.pets.entities.PetEntity;

public interface PetRepository extends JpaRepository<PetEntity, Long> {

}
