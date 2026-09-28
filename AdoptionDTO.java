package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import java.util.Date;

@Data
public class AdoptionDTO {
    private Long id;
    private Date date;
    private String status;

    // Asociaciones de cardinalidad 1
    private AdopterDTO adopter;
    private PetDTO pet;
}