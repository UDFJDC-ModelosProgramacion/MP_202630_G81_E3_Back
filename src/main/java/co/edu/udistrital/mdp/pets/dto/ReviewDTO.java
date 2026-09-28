package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;

@Data
public class ReviewDTO {
    private Long id;
    private String comments;
    private String score;
    private AdoptionDTO adoption;
}