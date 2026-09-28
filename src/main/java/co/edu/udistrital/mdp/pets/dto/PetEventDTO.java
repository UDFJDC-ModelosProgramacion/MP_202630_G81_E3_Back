package co.edu.udistrital.mdp.pets.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class PetEventDTO {

    private Long id;
    private String eventType;
    private Date date;
    private String description;

    // Asociación de cardinalidad 1 (PetEvent -> Pet): va en el DTO, no en el detalle
    private PetDTO pet;
}
