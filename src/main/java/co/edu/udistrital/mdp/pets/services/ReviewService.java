package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.ReviewEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.repositories.ReviewRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AdoptionRepository adoptionRepository;

    @Transactional
    public ReviewEntity createReview(Long adoptionId, ReviewEntity reviewEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de creación de un review para la adopción con id = {0}", adoptionId);

        if (reviewEntity.getComments() == null || reviewEntity.getComments().isBlank())
            throw new IllegalOperationException("Comments is not valid");

        if (reviewEntity.getScore() == null || reviewEntity.getScore().isBlank())
            throw new IllegalOperationException("Score is not valid");

        AdoptionEntity adoption = findAdoptionOrThrow(adoptionId);
        reviewEntity.setAdoption(adoption);

        log.info("Termina proceso de creación de un review para la adopción con id = {0}", adoptionId);
        return reviewRepository.save(reviewEntity);
    }

    @Transactional
    public List<ReviewEntity> getReviews(Long adoptionId) throws EntityNotFoundException {
        log.info("Inicia proceso de consultar los reviews de la adopción con id = {0}", adoptionId);
        findAdoptionOrThrow(adoptionId);
        return reviewRepository.findByAdoptionId(adoptionId);
    }

    @Transactional
    public ReviewEntity getReview(Long adoptionId, Long reviewId)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de consultar el review con id = {0} de la adopción con id = {1}", reviewId, adoptionId);
        findAdoptionOrThrow(adoptionId);
        return findReviewOrThrow(adoptionId, reviewId);
    }

    @Transactional
    public ReviewEntity updateReview(Long adoptionId, Long reviewId, ReviewEntity reviewEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de actualizar el review con id = {0} de la adopción con id = {1}", reviewId, adoptionId);

        if (reviewEntity.getComments() == null || reviewEntity.getComments().isBlank())
            throw new IllegalOperationException("Comments is not valid");

        if (reviewEntity.getScore() == null || reviewEntity.getScore().isBlank())
            throw new IllegalOperationException("Score is not valid");

        AdoptionEntity adoption = findAdoptionOrThrow(adoptionId);
        findReviewOrThrow(adoptionId, reviewId);

        reviewEntity.setId(reviewId);
        reviewEntity.setAdoption(adoption);

        log.info("Termina proceso de actualizar el review con id = {0} de la adopción con id = {1}", reviewId, adoptionId);
        return reviewRepository.save(reviewEntity);
    }

    @Transactional
    public void deleteReview(Long adoptionId, Long reviewId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de borrar el review con id = {0} de la adopción con id = {1}", reviewId, adoptionId);

        findAdoptionOrThrow(adoptionId);
        findReviewOrThrow(adoptionId, reviewId);

        reviewRepository.deleteById(reviewId);
        log.info("Termina proceso de borrar el review con id = {0} de la adopción con id = {1}", reviewId, adoptionId);
    }

    private AdoptionEntity findAdoptionOrThrow(Long adoptionId) throws EntityNotFoundException {
        Optional<AdoptionEntity> adoptionEntity = adoptionRepository.findById(adoptionId);
        if (adoptionEntity.isEmpty())
            throw new EntityNotFoundException("Adoption not found");
        return adoptionEntity.get();
    }

    private ReviewEntity findReviewOrThrow(Long adoptionId, Long reviewId)
            throws EntityNotFoundException, IllegalOperationException {
        Optional<ReviewEntity> reviewEntity = reviewRepository.findById(reviewId);
        if (reviewEntity.isEmpty())
            throw new EntityNotFoundException("Review not found");

        ReviewEntity review = reviewEntity.get();
        if (review.getAdoption() == null || !review.getAdoption().getId().equals(adoptionId))
            throw new IllegalOperationException("The review does not belong to the given adoption");

        return review;
    }
}