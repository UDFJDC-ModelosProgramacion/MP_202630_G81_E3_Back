package co.edu.udistrital.mdp.pets.entities;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class CoexistenceTestEntity extends BaseEntity {

    private Date startDate;
    private String result;

    @PodamExclude
    @JsonIgnore
    @OneToOne(mappedBy = "coexistenceTest")
    private AdoptionEntity adoption;
}
