package co.edu.udistrital.mdp.pets.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShelterDetailDTO extends ShelterDTO {

    private List<PetDTO> pets = new ArrayList<>();

    private List<VeterinarianDTO> veterinarians = new ArrayList<>();

    private List<EventCalendarDTO> events = new ArrayList<>();

    private List<ShelterAdministratorDTO> administrators = new ArrayList<>();
}
