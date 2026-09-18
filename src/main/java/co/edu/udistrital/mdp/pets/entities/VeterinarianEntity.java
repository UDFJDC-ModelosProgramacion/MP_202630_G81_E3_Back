package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class VeterinarianEntity extends BaseEntity {

    private String name;
    private String speciality;

    @PodamExclude
    @JsonIgnore
    @ManyToOne
    private ShelterEntity shelter;
}


