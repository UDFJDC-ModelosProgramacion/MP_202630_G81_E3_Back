package co.edu.udistrital.mdp.pets.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/** Historial completo de una mascota: eventos registrados, adopciones y devoluciones. */
@Data
public class PetHistoryDTO {

    private Long petId;
    private String petName;
    private String status;
    private List<PetEventDTO> events = new ArrayList<>();
    private List<AdoptionHistoryEntryDTO> adoptions = new ArrayList<>();
}
