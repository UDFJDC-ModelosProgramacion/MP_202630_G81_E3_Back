package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;

@Data
public class AdoptionRequirementDTO {
    private Long id;
    private String description;
    private Boolean isMandatory;
    private Boolean isFulfilled;
}
