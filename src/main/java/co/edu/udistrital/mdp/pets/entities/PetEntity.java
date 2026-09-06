package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import uk.co.jemos.podam.common.PodamExclude;

public class PetEntity {
    
@Id 
private Long id;

private String name;

private String status;

@PodamExclude 
@OneToMany(mappedBy = "pet", cascade = CascadeType.PERSIST, orphanRemoval = true)
private VaccinationRegistrationEntity vaccinationRegistration;

@PodamExclude 
@OneToOne
private BreedEntity breed;

}
