package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class ReviewEntity extends BaseEntity {

    private String comments;
    private String score;

    @PodamExclude
    @ManyToOne
    private AdoptionEntity adoption;
}