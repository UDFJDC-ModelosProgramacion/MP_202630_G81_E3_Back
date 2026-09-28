package co.edu.udistrital.mdp.pets.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PetDetailDTO extends PetDTO {

    // Pet 1 - N PetEvent (lado inverso: el FK vive en PetEvent)
    private List<PetEventDTO> events = new ArrayList<>();

    // Pet 1 - 1 Breed (lado inverso: el FK vive en Breed, por eso va aquí y no en PetDTO)
    private BreedDTO breed;
}
