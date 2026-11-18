package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CaseDevolutionDetailDTO extends CaseDevolutionDTO {
    // Asociación de cardinalidad 1
    private AdoptionDTO adoption;
}