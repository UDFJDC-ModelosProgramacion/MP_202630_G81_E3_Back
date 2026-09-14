package co.edu.udistrital.mdp.pets.entities;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class ShelterAdministratorEntity extends BaseEntity {
 
    private String name;
    private String email;
    private String role;
 
    @PodamExclude
    @ManyToMany(mappedBy = "administrators")
    private List<ShelterEntity> shelters = new ArrayList<>();
}
 