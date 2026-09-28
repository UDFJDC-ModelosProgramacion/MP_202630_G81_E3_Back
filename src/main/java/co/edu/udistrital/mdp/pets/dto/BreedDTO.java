package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;

@Data
public class BreedDTO {

    private Long id;
    private String name;
    private String description;

    // Asociación de cardinalidad 1 (Breed -> Pet): va en el DTO, no en el detalle
    private PetDTO pet;
}
