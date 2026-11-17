package co.edu.udistrital.mdp.pets.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CaseDevolutionDTO {
    private Long id;
    private LocalDate date;
    private String reason;
}
