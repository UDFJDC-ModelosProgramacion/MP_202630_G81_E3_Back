package co.edu.udistrital.mdp.ZZZ.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import ja.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class ShelterEntity extends BaseEntity {

    private String name;
    private String city;

    @PodamExclude
    @JsonIgnore
    @OneToMany(mappedBy = "shelter", cascade = CascadeType.ALL)
    private List<PetEntity> pets = new ArrayList<>();

    @PodamExclude
    @JsonIgnore
    @OneToMany(mappedBy = "shelter", cascade = CascadeType.ALL)
    private List<VeterinarianEntity> veterinarians = new ArrayList<>();

    @PodamExclude
    @JsonIgnore
    @OneToMany(mappedBy = "shelter", cascade = CascadeType.ALL)
    private List<EventCalendarEntity> events = new ArrayList<>();
}
