package co.edu.udistrital.mdp.pets.dto;

import java.util.List;

import lombok.Data;

@Data
public class PetDTO {

    private Long id;
    private String name;
    private String status;
    private Integer age;
    private String sex;
    private String size;
    private String temperament;
    private String description;
    private String specialNeeds;
    private List<String> photos;
    private String spaceRequirement;
    private Boolean compatibleWithChildren;
    private Boolean compatibleWithOtherPets;
    private String activityLevel;
}
