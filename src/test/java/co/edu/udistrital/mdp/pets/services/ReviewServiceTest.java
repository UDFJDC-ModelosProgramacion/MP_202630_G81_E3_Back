package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private EntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private AdoptionEntity adoption = new AdoptionEntity();
    private List<ReviewEntity> reviewList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.createQuery("delete from ReviewEntity").executeUpdate();
        entityManager.createQuery("delete from AdoptionEntity").executeUpdate();
    }

    private void insertData() {
        ShelterEntity shelter = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(shelter);

        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        pet.setShelter(shelter);
        entityManager.persist(pet);

        AdopterEntity adopter = factory.manufacturePojo(AdopterEntity.class);
        entityManager.persist(adopter);

        adoption = factory.manufacturePojo(AdoptionEntity.class);
        adoption.setAdoptionDate(LocalDate.now());
        adoption.setStatus("PENDING");
        adoption.setPet(pet);
        adoption.setAdopter(adopter);
        entityManager.persist(adoption);

        for (int i = 0; i < 3; i++) {
            ReviewEntity entity = factory.manufacturePojo(ReviewEntity.class);
            entity.setAdoption(adoption);
            entityManager.persist(entity);
            reviewList.add(entity);
        }
    }


    @Test
    void createReview_shouldPersistReview() throws EntityNotFoundException, IllegalOperationException{
        ReviewEntity newEntity = factory.manufacturePojo(ReviewEntity.class);
        ReviewEntity result = reviewService.createReview(adoption.getId(), newEntity);

        assertNotNull(result);
        ReviewEntity stored = entityManager.find(ReviewEntity.class, result.getId());
        assertEquals(newEntity.getComments(), stored.getComments());
        assertEquals(adoption.getId(), stored.getAdoption().getId());
    }

    @Test
    void createReview_withInvalidAdoptionId_shouldThrowException(){
        ReviewEntity newEntity = factory.manufacturePojo(ReviewEntity.class);
        assertThrows(EntityNotFoundException.class, () -> reviewService.createReview(0L, newEntity));
    }

    @Test
    void createReview_withBlankComments_shouldThrowException(){
        ReviewEntity newEntity = factory.manufacturePojo(ReviewEntity.class);
        newEntity.setComments("");
        assertThrows(IllegalOperationException.class, () -> reviewService.createReview(adoption.getId(), newEntity));
    }

    @Test
    void getReviews_shouldReturnAllReviewsOfAdoption() throws EntityNotFoundException{
        List<ReviewEntity> result = reviewService.getReviews(adoption.getId());
        assertEquals(reviewList.size(), result.size());
    }

    @Test
    void getReview_shouldReturnReview() throws EntityNotFoundException, IllegalOperationException{
        ReviewEntity existing = reviewList.get(0);
        ReviewEntity result = reviewService.getReview(adoption.getId(), existing.getId());
        assertNotNull(result);
        assertEquals(existing.getComments(), result.getComments());
    }

    @Test
    void getReview_notBelongingToAdoption_shouldThrowException() {
        ReviewEntity orphan = factory.manufacturePojo(ReviewEntity.class);
        orphan.setAdoption(null);
        entityManager.persist(orphan);
        assertThrows(IllegalOperationException.class, () -> reviewService.getReview(adoption.getId(), orphan.getId()));
    }

    @Test
    void updateReview_shouldUpdateReview() throws EntityNotFoundException, IllegalOperationException{
        ReviewEntity existing = reviewList.get(0);
        ReviewEntity updated = factory.manufacturePojo(ReviewEntity.class);
        updated.setId(existing.getId());

        reviewService.updateReview(adoption.getId(), existing.getId(), updated);

        ReviewEntity stored = entityManager.find(ReviewEntity.class, existing.getId());
        assertEquals(updated.getComments(), stored.getComments());
        assertEquals(updated.getScore(), stored.getScore());
    }

    @Test
    void deleteReview_shouldDeleteReview() throws EntityNotFoundException, IllegalOperationException{
        ReviewEntity existing = reviewList.get(0);
        reviewService.deleteReview(adoption.getId(), existing.getId());
        assertNull(entityManager.find(ReviewEntity.class, existing.getId()));
    }

    @Test
    void deleteReview_withInvalidAdoptionId_shouldThrowException() {
        ReviewEntity existing = reviewList.get(0);
        assertThrows(EntityNotFoundException.class, () -> reviewService.deleteReview(0L, existing.getId()));
    }
}
