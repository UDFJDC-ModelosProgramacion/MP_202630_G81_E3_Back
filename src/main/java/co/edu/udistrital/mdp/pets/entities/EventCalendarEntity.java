package co.edu.udistrital.mdp.pets.entities;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class EventCalendarEntity extends BaseEntity {

    private String type;
    private Date date;

    @PodamExclude
    @JsonIgnore
    @ManyToOne
    private ShelterEntity shelter;
}
