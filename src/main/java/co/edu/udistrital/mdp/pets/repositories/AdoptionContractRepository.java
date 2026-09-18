package co.edu.udistrital.mdp.pets.repositories;

import co.edu.udistrital.mdp.pets.entities.AdoptionContractEntity;

import org.springframework.data.jpa.repository.JpaRepository;


public interface AdoptionContractRepository extends JpaRepository<AdoptionContractEntity, Long> {
}