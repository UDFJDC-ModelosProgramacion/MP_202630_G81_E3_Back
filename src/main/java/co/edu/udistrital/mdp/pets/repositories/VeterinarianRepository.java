package co.edu.udistrital.mdp.pets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;

public interface VeterinarianRepository extends JpaRepository<VeterinarianEntity, Long> {
}