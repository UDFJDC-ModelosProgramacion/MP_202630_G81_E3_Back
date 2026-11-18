package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.ReviewSummaryDTO;
import co.edu.udistrital.mdp.pets.entities.ReviewEntity;
import co.edu.udistrital.mdp.pets.services.ReviewService;

/** Reseñas públicas de todos los adoptantes; con `successStory=true` solo las historias de éxito. */
@RestController
@RequestMapping("/reviews")
public class ReviewCatalogController {

    private final ReviewService reviewService;

    public ReviewCatalogController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewSummaryDTO> findAll(@RequestParam(required = false) Boolean successStory) {
        return reviewService.getAllReviews(successStory).stream().map(ReviewCatalogController::toSummary).toList();
    }

    private static ReviewSummaryDTO toSummary(ReviewEntity review) {
        ReviewSummaryDTO dto = new ReviewSummaryDTO();
        dto.setId(review.getId());
        dto.setComments(review.getComments());
        dto.setScore(review.getScore());
        dto.setSuccessStory(Boolean.TRUE.equals(review.getSuccessStory()));
        if (review.getAdoption() != null) {
            if (review.getAdoption().getPet() != null)
                dto.setPetName(review.getAdoption().getPet().getName());
            if (review.getAdoption().getAdopter() != null)
                dto.setAdopterName(review.getAdoption().getAdopter().getName());
        }
        return dto;
    }
}
