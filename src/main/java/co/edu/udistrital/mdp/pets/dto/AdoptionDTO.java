package co.edu.udistrital.mdp.pets.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AdoptionDTO {
    private Long id;
    private LocalDate adoptionDate;
    private String status;
}