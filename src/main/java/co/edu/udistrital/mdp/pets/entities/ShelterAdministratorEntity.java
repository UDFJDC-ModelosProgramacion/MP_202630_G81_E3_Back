package co.edu.udistrital.mdp.pets.entities;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class ShelterAdministratorEntity extends BaseEntity {
 
    private String name;
    private String email;
    private String role;
 
    @PodamExclude
    @ManyToOne
    private ShelterEntity shelter;
}
 