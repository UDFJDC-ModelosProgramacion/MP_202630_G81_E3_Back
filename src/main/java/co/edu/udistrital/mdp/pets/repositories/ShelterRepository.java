package co.edu.udistrital.mdp.pets.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
 
public interface ShelterRepository extends JpaRepository<ShelterEntity, Long> {
}