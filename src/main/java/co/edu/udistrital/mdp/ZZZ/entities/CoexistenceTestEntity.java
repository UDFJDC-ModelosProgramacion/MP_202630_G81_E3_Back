package co.edu.udistrital.mdp.ZZZ.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

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
