package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jbfoster.podam.common.PodamExclude;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "adoption_requirements")
public class AdoptionRequirementEntity extends BaseEntity {

    private String minimumSpace;
    private Boolean allowsOtherPets;
    private Boolean requireYard;
    private Integer minAdopterAge;

    @PodamExclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id")
    private PetEntity pet;
}
