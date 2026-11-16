package co.edu.udistrital.mdp.pets.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

/** Seguimiento de la adaptación de una mascota después de su adopción. */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class FollowUpEntity extends BaseEntity {

    private LocalDate date;

    private String adaptationStatus;

    private String observations;

    @PodamExclude
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adoption_id", nullable = false)
    private AdoptionEntity adoption;
}
