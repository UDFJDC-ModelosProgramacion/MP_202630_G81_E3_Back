package co.edu.udistrital.mdp.pets.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShelterAdministratorDetailDTO extends ShelterAdministratorDTO {

    private List<ShelterDTO> shelters = new ArrayList<>();
}
