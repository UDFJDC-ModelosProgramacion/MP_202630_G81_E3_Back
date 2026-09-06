package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class BreedEntity extends BaseEntity {

    private String description;
    private String name;

    @PodamExclude
    @OneToOne
    @JoinColumn(name = "pet_id", referencedColumnName = "id", unique = true)
    private PetEntity pet;
}
