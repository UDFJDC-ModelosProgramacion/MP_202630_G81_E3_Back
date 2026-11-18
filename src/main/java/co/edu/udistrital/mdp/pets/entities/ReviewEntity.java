package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class ReviewEntity extends BaseEntity {

    private String comments;
    private String score;

    /** Indica que la reseña cuenta la historia de una adopción exitosa. */
    private Boolean successStory = Boolean.FALSE;

    @PodamExclude
    @ManyToOne
    private AdoptionEntity adoption;
}