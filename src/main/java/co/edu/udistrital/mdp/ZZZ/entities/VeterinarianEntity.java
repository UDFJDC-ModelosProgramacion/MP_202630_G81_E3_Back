package co.edu.udistrital.mdp.ZZZ.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class VeterinarianEntity extends BaseEntity {

    private String name;
    private String speciality;

    @PodamExclude
    @JsonIgnore
    @ManyToOne
    private ShelterEntity shelter;
}
