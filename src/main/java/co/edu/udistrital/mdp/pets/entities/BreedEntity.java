package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data 
@Entity 
public class BreedEntity extends BaseEntity {

@Id 
private Long id;
private String description;
private String name;


@PodamExclude 
@OneToOne 
private PetEntity pet;
}
