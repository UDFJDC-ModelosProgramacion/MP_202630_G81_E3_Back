package co.edu.udistrital.mdp.pets.entities;
import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class CaseDevolutionEntity extends BaseEntity {
    private LocalDate date;
    private String reason;
    
    @PodamExclude
    @OneToOne
    @JoinColumn(name = "adoption_id", referencedColumnName = "id", unique = true)
    private AdoptionEntity adoption;
}