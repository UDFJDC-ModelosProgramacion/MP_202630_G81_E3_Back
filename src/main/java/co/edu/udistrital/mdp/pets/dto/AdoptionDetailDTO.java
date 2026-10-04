package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdoptionDetailDTO extends AdoptionDTO {
    // Asociación de cardinalidad 1
    private AdoptionContractDTO contract;

    // Asociación de cardinalidad N
    private List<AdoptionRequirementDTO> requirements = new ArrayList<>();
}
