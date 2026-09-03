package co.edu.udistrital.mdp.ZZZ.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class EventCalendarEntity extends BaseEntity {

    private String type;
    private Date date;

    @PodamExclude
    @JsonIgnore
    @ManyToOne
    private ShelterEntity shelter;
}
