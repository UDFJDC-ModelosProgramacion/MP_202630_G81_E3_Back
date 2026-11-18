package co.edu.udistrital.mdp.pets.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ShelterDTO {
    private Long id;
    private String name;
    private String city;
    private List<String> photos = new ArrayList<>();
    private List<String> videos = new ArrayList<>();
}
