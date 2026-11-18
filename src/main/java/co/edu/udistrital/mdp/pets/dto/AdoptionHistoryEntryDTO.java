package co.edu.udistrital.mdp.pets.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AdoptionHistoryEntryDTO {

    private Long adoptionId;
    private LocalDate adoptionDate;
    private String status;
    private String adopterName;
    private boolean returned;
    private LocalDate returnDate;
    private String returnReason;
}
