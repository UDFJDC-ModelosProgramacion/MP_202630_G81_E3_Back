package co.edu.udistrital.mdp.pets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.udistrital.mdp.pets.entities.VaccinationRegistrationEntity;

public interface VaccinationRegistrationRepository extends JpaRepository<VaccinationRegistrationEntity, Long> {

}
