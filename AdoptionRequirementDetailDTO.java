package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdoptionRequirementDetailDTO extends AdoptionRequirementDTO {
    // Asociación de cardinalidad N
    private List<AdoptionDTO> adoptions = new ArrayList<>();
}