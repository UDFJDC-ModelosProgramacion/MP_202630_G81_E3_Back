package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import co.edu.udistrital.mdp.pets.dto.ReviewDTO;
import co.edu.udistrital.mdp.pets.entities.ReviewEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.ReviewService;

@RestController
@RequestMapping("/adoptions/{adoptionId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final ModelMapper modelMapper;

    public ReviewController(ReviewService reviewService, ModelMapper modelMapper) {
        this.reviewService = reviewService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDTO create(@PathVariable Long adoptionId, @RequestBody ReviewDTO reviewDTO)
            throws EntityNotFoundException, IllegalOperationException {
        ReviewEntity created = reviewService.createReview(adoptionId, modelMapper.map(reviewDTO, ReviewEntity.class));
        return modelMapper.map(created, ReviewDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewDTO> findAll(@PathVariable Long adoptionId) throws EntityNotFoundException {
        return modelMapper.map(reviewService.getReviews(adoptionId), new TypeToken<List<ReviewDTO>>() {}.getType());
    }

    @GetMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewDTO findOne(@PathVariable Long adoptionId, @PathVariable Long reviewId)
            throws EntityNotFoundException, IllegalOperationException {
        return modelMapper.map(reviewService.getReview(adoptionId, reviewId), ReviewDTO.class);
    }

    @PutMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewDTO update(@PathVariable Long adoptionId, @PathVariable Long reviewId,
            @RequestBody ReviewDTO reviewDTO) throws EntityNotFoundException, IllegalOperationException {
        ReviewEntity updated = reviewService.updateReview(adoptionId, reviewId,
                modelMapper.map(reviewDTO, ReviewEntity.class));
        return modelMapper.map(updated, ReviewDTO.class);
    }

    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long adoptionId, @PathVariable Long reviewId)
            throws EntityNotFoundException, IllegalOperationException {
        reviewService.deleteReview(adoptionId, reviewId);
    }
}