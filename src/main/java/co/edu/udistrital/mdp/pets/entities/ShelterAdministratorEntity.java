package co.edu.udistrital.mdp.pets.entities;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class ShelterAdministratorEntity extends BaseEntity {
 
    private String name;
    private String email;
    private String role;
 
    @PodamExclude
    @ManyToOne
    private ShelterEntity shelter;
}
 