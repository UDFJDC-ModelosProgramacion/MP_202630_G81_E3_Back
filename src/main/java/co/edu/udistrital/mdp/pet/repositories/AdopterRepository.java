package co.edu.udistrital.mdp.pet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.pet.entities.AdopterEntity;

@Repository
public interface AdopterRepository extends JpaRepository<AdopterEntity, Long> {
}
