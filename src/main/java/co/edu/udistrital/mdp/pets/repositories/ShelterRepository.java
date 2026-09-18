package co.edu.udistrital.mdp.pets.repositories;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
 
public interface ShelterRepository extends JpaRepository<ShelterEntity, Long> {
    List<ShelterEntity> findByCity(String city);
    boolean existsByNameAndCity(String name, String city);
}
