package co.edu.udistrital.mdp.pets.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class PetEventDTO {

    private Long id;
    private String eventType;
    private Date date;
    private String description;

    private PetDTO pet;
}
