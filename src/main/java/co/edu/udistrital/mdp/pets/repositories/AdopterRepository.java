package co.edu.udistrital.mdp.pets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;

public interface AdopterRepository extends JpaRepository<AdopterEntity, Long> {
      
}
