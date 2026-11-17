package co.edu.udistrital.mdp.pets.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AdoptionContractDTO {
    private Long id;
    private String termsAndConditions;
    private LocalDate signedDate;
    private String status;

    // Asociación de cardinalidad 1
    private AdoptionDTO adoption;
}
