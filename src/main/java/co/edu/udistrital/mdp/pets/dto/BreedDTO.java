package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;

@Data
public class BreedDTO {

    private Long id;
    private String name;
    private String description;
    private PetDTO pet;
}
