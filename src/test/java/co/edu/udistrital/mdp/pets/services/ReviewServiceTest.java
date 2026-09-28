package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ReviewEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

import jakarta.persistence.EntityManager;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(ReviewService.class)
class ReviewServiceTest {

    private static final Long NON_EXISTENT_ID = 999999L;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private EntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();

    private AdoptionEntity adoption;
    private final List<ReviewEntity> reviewList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.createQuery("delete from ReviewEntity").executeUpdate();
        entityManager.createQuery("delete from AdoptionEntity").executeUpdate();
        reviewList.clear();
    }

    private void insertData() {
        adoption = createAdoption();
        for (int i = 0; i < 3; i++) {
            reviewList.add(persistReview(adoption));
        }
    }

    private AdoptionEntity createAdoption() {
        ShelterEntity shelter = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(shelter);

        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        pet.setShelter(shelter);
        entityManager.persist(pet);

        AdopterEntity adopter = factory.manufacturePojo(AdopterEntity.class);
        entityManager.persist(adopter);

        AdoptionEntity entity = factory.manufacturePojo(AdoptionEntity.class);
        entity.setAdoptionDate(LocalDate.now());
        entity.setStatus("PENDING");
        entity.setPet(pet);
        entity.setAdopter(adopter);
        entityManager.persist(entity);
        return entity;
    }

    private ReviewEntity buildReview() {
        ReviewEntity entity = factory.manufacturePojo(ReviewEntity.class);
        entity.setComments("Valid comments");
        entity.setScore("5");
        return entity;
    }

    private ReviewEntity persistReview(AdoptionEntity owner) {
        ReviewEntity entity = buildReview();
        entity.setAdoption(owner);
        entityManager.persist(entity);
        return entity;
    }


    @Test
    void createReview_shouldPersistReview() throws EntityNotFoundException, IllegalOperationException {
        ReviewEntity newEntity = buildReview();

        ReviewEntity result = reviewService.createReview(adoption.getId(), newEntity);

        assertNotNull(result.getId());
        ReviewEntity stored = entityManager.find(ReviewEntity.class, result.getId());
        assertEquals(newEntity.getComments(), stored.getComments());
        assertEquals(newEntity.getScore(), stored.getScore());
        assertEquals(adoption.getId(), stored.getAdoption().getId());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    void createReview_withInvalidComments_shouldThrowException(String invalidComments) {
        ReviewEntity newEntity = buildReview();
        newEntity.setComments(invalidComments);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> reviewService.createReview(adoption.getId(), newEntity));
        assertEquals("Comments is not valid", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    void createReview_withInvalidScore_shouldThrowException(String invalidScore) {
        ReviewEntity newEntity = buildReview();
        newEntity.setScore(invalidScore);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> reviewService.createReview(adoption.getId(), newEntity));
        assertEquals("Score is not valid", ex.getMessage());
    }

    @Test
    void createReview_withInvalidAdoptionId_shouldThrowException() {
        ReviewEntity newEntity = buildReview();

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> reviewService.createReview(NON_EXISTENT_ID, newEntity));
        assertEquals("Adoption not found", ex.getMessage());
    }


    @Test
    void getReviews_shouldReturnOnlyReviewsOfAdoption() throws EntityNotFoundException {
        AdoptionEntity otherAdoption = createAdoption();
        persistReview(otherAdoption);

        List<ReviewEntity> result = reviewService.getReviews(adoption.getId());

        assertEquals(reviewList.size(), result.size());
        for (ReviewEntity review : result) {
            assertEquals(adoption.getId(), review.getAdoption().getId());
        }
    }

    @Test
    void getReviews_withAdoptionWithoutReviews_shouldReturnEmptyList() throws EntityNotFoundException {
        AdoptionEntity emptyAdoption = createAdoption();

        assertTrue(reviewService.getReviews(emptyAdoption.getId()).isEmpty());
    }

    @Test
    void getReviews_withInvalidAdoptionId_shouldThrowException() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> reviewService.getReviews(NON_EXISTENT_ID));
        assertEquals("Adoption not found", ex.getMessage());
    }

    @Test
    void getReview_shouldReturnReview() throws EntityNotFoundException, IllegalOperationException {
        ReviewEntity existing = reviewList.get(0);

        ReviewEntity result = reviewService.getReview(adoption.getId(), existing.getId());

        assertEquals(existing.getId(), result.getId());
        assertEquals(existing.getComments(), result.getComments());
    }

    @Test
    void getReview_withInvalidAdoptionId_shouldThrowException() {
        Long reviewId = reviewList.get(0).getId();

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> reviewService.getReview(NON_EXISTENT_ID, reviewId));
        assertEquals("Adoption not found", ex.getMessage());
    }

    @Test
    void getReview_withInvalidReviewId_shouldThrowException() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> reviewService.getReview(adoption.getId(), NON_EXISTENT_ID));
        assertEquals("Review not found", ex.getMessage());
    }

    @Test
    void getReview_withoutAdoption_shouldThrowException() {
        ReviewEntity orphan = persistReview(null);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> reviewService.getReview(adoption.getId(), orphan.getId()));
        assertEquals("The review does not belong to the given adoption", ex.getMessage());
    }

    @Test
    void getReview_ofAnotherAdoption_shouldThrowException() {
        AdoptionEntity otherAdoption = createAdoption();
        ReviewEntity foreign = persistReview(otherAdoption);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> reviewService.getReview(adoption.getId(), foreign.getId()));
        assertEquals("The review does not belong to the given adoption", ex.getMessage());
    }


    @Test
    void updateReview_shouldUpdateReview() throws EntityNotFoundException, IllegalOperationException {
        ReviewEntity existing = reviewList.get(0);
        ReviewEntity updated = buildReview();
        updated.setComments("Updated comments");
        updated.setScore("3");

        ReviewEntity result = reviewService.updateReview(adoption.getId(), existing.getId(), updated);

        assertEquals(existing.getId(), result.getId());
        assertEquals("Updated comments", result.getComments());
        assertEquals("3", result.getScore());
        assertEquals(adoption.getId(), result.getAdoption().getId());

        ReviewEntity stored = entityManager.find(ReviewEntity.class, existing.getId());
        assertEquals("Updated comments", stored.getComments());
        assertEquals("3", stored.getScore());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    void updateReview_withInvalidComments_shouldThrowException(String invalidComments) {
        Long reviewId = reviewList.get(0).getId();
        ReviewEntity updated = buildReview();
        updated.setComments(invalidComments);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> reviewService.updateReview(adoption.getId(), reviewId, updated));
        assertEquals("Comments is not valid", ex.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    void updateReview_withInvalidScore_shouldThrowException(String invalidScore) {
        Long reviewId = reviewList.get(0).getId();
        ReviewEntity updated = buildReview();
        updated.setScore(invalidScore);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> reviewService.updateReview(adoption.getId(), reviewId, updated));
        assertEquals("Score is not valid", ex.getMessage());
    }

    @Test
    void updateReview_withInvalidAdoptionId_shouldThrowException() {
        Long reviewId = reviewList.get(0).getId();
        ReviewEntity updated = buildReview();

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> reviewService.updateReview(NON_EXISTENT_ID, reviewId, updated));
        assertEquals("Adoption not found", ex.getMessage());
    }

    @Test
    void updateReview_withInvalidReviewId_shouldThrowException() {
        ReviewEntity updated = buildReview();

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> reviewService.updateReview(adoption.getId(), NON_EXISTENT_ID, updated));
        assertEquals("Review not found", ex.getMessage());
    }

    @Test
    void updateReview_ofAnotherAdoption_shouldThrowException() {
        AdoptionEntity otherAdoption = createAdoption();
        ReviewEntity foreign = persistReview(otherAdoption);
        ReviewEntity updated = buildReview();

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> reviewService.updateReview(adoption.getId(), foreign.getId(), updated));
        assertEquals("The review does not belong to the given adoption", ex.getMessage());
    }



    @Test
    void deleteReview_shouldDeleteReview() throws EntityNotFoundException, IllegalOperationException {
        ReviewEntity existing = reviewList.get(0);

        reviewService.deleteReview(adoption.getId(), existing.getId());

        assertNull(entityManager.find(ReviewEntity.class, existing.getId()));
        assertNotNull(entityManager.find(ReviewEntity.class, reviewList.get(1).getId()));
    }

    @Test
    void deleteReview_withInvalidAdoptionId_shouldThrowException() {
        Long reviewId = reviewList.get(0).getId();

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> reviewService.deleteReview(NON_EXISTENT_ID, reviewId));
        assertEquals("Adoption not found", ex.getMessage());
    }

    @Test
    void deleteReview_withInvalidReviewId_shouldThrowException() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> reviewService.deleteReview(adoption.getId(), NON_EXISTENT_ID));
        assertEquals("Review not found", ex.getMessage());
    }

    @Test
    void deleteReview_ofAnotherAdoption_shouldThrowException() {
        AdoptionEntity otherAdoption = createAdoption();
        ReviewEntity foreign = persistReview(otherAdoption);

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> reviewService.deleteReview(adoption.getId(), foreign.getId()));
        assertEquals("The review does not belong to the given adoption", ex.getMessage());
        assertNotNull(entityManager.find(ReviewEntity.class, foreign.getId()));
    }
}