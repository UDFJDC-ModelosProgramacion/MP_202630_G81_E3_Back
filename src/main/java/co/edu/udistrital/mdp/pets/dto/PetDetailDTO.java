package co.edu.udistrital.mdp.pets.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PetDetailDTO extends PetDTO {

    private List<PetEventDTO> events = new ArrayList<>();

    private BreedDTO breed;
}
