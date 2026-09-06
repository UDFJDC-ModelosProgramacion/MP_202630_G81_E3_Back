package co.edu.udistrital.mdp.pets.entities;
import java.sql.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class CaseDevolutionEntity extends BaseEntity {
    @Temporal(TemporalType.DATE)
    private Date date;
    private String reason;
    
    @PodamExclude
    @OneToOne
    @JoinColumn(name = "adoption_id", referencedColumnName = "id", unique = true)
    private AdoptionEntity adoption;
}