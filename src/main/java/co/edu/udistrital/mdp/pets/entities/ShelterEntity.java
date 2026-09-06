package co.edu.udistrital.mdp.pets.entities;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class ShelterEntity extends BaseEntity {

    private String name;
    private String city;

    @PodamExclude
    @OneToMany(mappedBy = "shelter", cascade = CascadeType.ALL)
    private List<PetEntity> pets = new ArrayList<>();

    @PodamExclude
    @OneToMany(mappedBy = "shelter", cascade = CascadeType.ALL)
    private List<VeterinarianEntity> veterinarians = new ArrayList<>();

    @PodamExclude
    @OneToMany(mappedBy = "shelter", cascade = CascadeType.ALL)
    private List<EventCalendarEntity> events = new ArrayList<>();

    
    @PodamExclude
    @OneToMany(mappedBy = "shelter", cascade = CascadeType.ALL)
    private List<ShelterAdministratorEntity> administrators = new ArrayList<>();
}