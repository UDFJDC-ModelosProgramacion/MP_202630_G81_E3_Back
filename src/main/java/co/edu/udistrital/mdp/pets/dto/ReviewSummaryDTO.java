package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;

/** Reseña pública: no incluye datos de contacto del adoptante. */
@Data
public class ReviewSummaryDTO {

    private Long id;
    private String comments;
    private String score;
    private Boolean successStory;
    private String petName;
    private String adopterName;
}
