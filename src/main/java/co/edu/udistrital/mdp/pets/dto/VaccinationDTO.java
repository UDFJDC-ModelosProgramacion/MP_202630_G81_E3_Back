package co.edu.udistrital.mdp.pets.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class VaccinationDTO {

    private Long id;
    private String name;
    private LocalDate applicationDate;
    private LocalDate nextDueDate;
    private String notes;
    private VeterinarianDTO veterinarian;
}
