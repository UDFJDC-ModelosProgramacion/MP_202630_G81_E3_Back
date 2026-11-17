package co.edu.udistrital.mdp.pets.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

@Data
public class AdoptionRequirementDTO {
    private Long id;
    private String description;

    @JsonAlias("isMandatory")
    private Boolean mandatory;

    private String minimumSpace;
    private Boolean allowsOtherPets;
    private Boolean requireYard;
    private Integer minAdopterAge;

    // Mascota a la que aplica el requisito (opcional)
    private PetDTO pet;
}
