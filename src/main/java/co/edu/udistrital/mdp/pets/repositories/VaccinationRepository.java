package co.edu.udistrital.mdp.pets.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.udistrital.mdp.pets.entities.VaccinationEntity;

public interface VaccinationRepository extends JpaRepository<VaccinationEntity, Long> {

    /** Carné de una mascota, de la aplicación más reciente a la más antigua. */
    List<VaccinationEntity> findByPetIdOrderByApplicationDateDescIdDesc(Long petId);
}
