package co.edu.udistrital.mdp.pets.entities;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class NotificationEntity extends BaseEntity {

    private String content;

    @Temporal(TemporalType.DATE)
    private Date date;

    @PodamExclude
    @ManyToOne
    private ShelterEntity shelter;
}