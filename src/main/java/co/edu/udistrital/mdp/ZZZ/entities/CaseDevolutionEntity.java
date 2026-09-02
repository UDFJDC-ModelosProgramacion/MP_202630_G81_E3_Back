package co.edu.udistrital.mdp.ZZZ.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;


@Data
@Entity
public class CaseDevolutionEntity extends BaseEntity {

    private Date date;
    private String reason;

    @PodamExclude
    @JsonIgnore
    @OneToOne(mappedBy = "devolution")
    private AdoptionEntity adoption;
}
