package co.edu.udistrital.mdp.pets.dto;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

/** Reporte de gestión de un refugio: adopciones, devoluciones y eventos médicos. */
@Data
public class ShelterReportDTO {

    private Long shelterId;
    private String shelterName;
    private LocalDate from;
    private LocalDate to;

    private int totalPets;
    private int availablePets;
    private int inProcessPets;
    private int adoptedPets;

    private int totalAdoptions;
    private Map<String, Integer> adoptionsByStatus = new LinkedHashMap<>();

    private int totalReturns;
    private Map<String, Integer> returnsByReason = new LinkedHashMap<>();

    private int totalMedicalRecords;
    private int totalPetEvents;
    private Map<String, Integer> petEventsByType = new LinkedHashMap<>();
}
