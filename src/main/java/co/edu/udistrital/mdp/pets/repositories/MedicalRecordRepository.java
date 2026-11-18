package co.edu.udistrital.mdp.pets.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.udistrital.mdp.pets.entities.MedicalRecordEntity;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecordEntity, Long> {

    /** Historial médico de una mascota, del más reciente al más antiguo. */
    List<MedicalRecordEntity> findByPetIdOrderByDateDescIdDesc(Long petId);

    List<MedicalRecordEntity> findByPetShelterId(Long shelterId);
}
