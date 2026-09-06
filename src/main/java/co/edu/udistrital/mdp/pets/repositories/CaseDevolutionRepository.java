package co.edu.udistrital.mdp.pets.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import co.edu.udistrital.mdp.pets.entities.CaseDevolutionEntity;
 
@Repository
public interface CaseDevolutionRepository extends JpaRepository<CaseDevolutionEntity, Long> {
}
 