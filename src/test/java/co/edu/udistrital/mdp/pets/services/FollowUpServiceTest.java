package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
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
import co.edu.udistrital.mdp.pets.entities.FollowUpEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ForbiddenOperationException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

import jakarta.persistence.EntityManager;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(FollowUpService.class)
class FollowUpServiceTest {

    private static final Long NON_EXISTENT_ID = 999999L;

    @Autowired
    private FollowUpService followUpService;

    @Autowired
    private EntityManager entityManager;

    private final PodamFactory factory = new PodamFactoryImpl();

    private AdopterEntity owner;
    private AdopterEntity stranger;
    private AdoptionEntity adoption;

    @BeforeEach
    void setUp() {
        entityManager.createQuery("delete from FollowUpEntity").executeUpdate();
        entityManager.createQuery("delete from AdoptionEntity").executeUpdate();

        ShelterEntity shelter = factory.manufacturePojo(ShelterEntity.class);
        entityManager.persist(shelter);
        PetEntity pet = factory.manufacturePojo(PetEntity.class);
        pet.setShelter(shelter);
        entityManager.persist(pet);

        owner = factory.manufacturePojo(AdopterEntity.class);
        entityManager.persist(owner);
        stranger = factory.manufacturePojo(AdopterEntity.class);
        entityManager.persist(stranger);

        adoption = buildAdoption(pet, "COMPLETED");
    }

    private AdoptionEntity buildAdoption(PetEntity pet, String status) {
        AdoptionEntity entity = new AdoptionEntity();
        entity.setAdoptionDate(LocalDate.now().minusDays(10));
        entity.setStatus(status);
        entity.setPet(pet);
        entity.setAdopter(owner);
        entityManager.persist(entity);
        return entity;
    }

    private FollowUpEntity buildFollowUp(LocalDate date) {
        FollowUpEntity entity = new FollowUpEntity();
        entity.setDate(date);
        entity.setAdaptationStatus("GOOD");
        entity.setObservations("Come bien y duerme tranquilo");
        return entity;
    }

    @Test
    void createFollowUp_shouldPersistDateStatusAndObservations() throws Exception {
        FollowUpEntity result = followUpService.createFollowUp(adoption.getId(), owner.getId(),
                buildFollowUp(LocalDate.now()));

        assertNotNull(result.getId());
        FollowUpEntity stored = entityManager.find(FollowUpEntity.class, result.getId());
        assertEquals(LocalDate.now(), stored.getDate());
        assertEquals("GOOD", stored.getAdaptationStatus());
        assertEquals("Come bien y duerme tranquilo", stored.getObservations());
        assertEquals(adoption.getId(), stored.getAdoption().getId());
    }

    @Test
    void createFollowUp_withFutureDate_shouldThrowException() {
        assertThrows(IllegalOperationException.class, () -> followUpService.createFollowUp(adoption.getId(),
                owner.getId(), buildFollowUp(LocalDate.now().plusDays(1))));
    }

    @Test
    void createFollowUp_withNullDate_shouldThrowException() {
        assertThrows(IllegalOperationException.class,
                () -> followUpService.createFollowUp(adoption.getId(), owner.getId(), buildFollowUp(null)));
    }

    @Test
    void createFollowUp_withUnknownStatus_shouldThrowException() {
        FollowUpEntity entity = buildFollowUp(LocalDate.now());
        entity.setAdaptationStatus("UNKNOWN");
        assertThrows(IllegalOperationException.class,
                () -> followUpService.createFollowUp(adoption.getId(), owner.getId(), entity));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { "   " })
    void createFollowUp_withInvalidObservations_shouldThrowException(String observations) {
        FollowUpEntity entity = buildFollowUp(LocalDate.now());
        entity.setObservations(observations);
        assertThrows(IllegalOperationException.class,
                () -> followUpService.createFollowUp(adoption.getId(), owner.getId(), entity));
    }

    @Test
    void createFollowUp_withTooLongObservations_shouldThrowException() {
        FollowUpEntity entity = buildFollowUp(LocalDate.now());
        entity.setObservations("a".repeat(FollowUpService.MAX_OBSERVATIONS_LENGTH + 1));
        assertThrows(IllegalOperationException.class,
                () -> followUpService.createFollowUp(adoption.getId(), owner.getId(), entity));
    }

    @Test
    void createFollowUp_whenAdoptionIsNotCompleted_shouldThrowException() {
        PetEntity pet = adoption.getPet();
        AdoptionEntity pending = buildAdoption(pet, "IN_PROGRESS");
        assertThrows(IllegalOperationException.class, () -> followUpService.createFollowUp(pending.getId(),
                owner.getId(), buildFollowUp(LocalDate.now())));
    }

    @Test
    void createFollowUp_onAnotherAdoptersAdoption_shouldBeForbidden() {
        assertThrows(ForbiddenOperationException.class, () -> followUpService.createFollowUp(adoption.getId(),
                stranger.getId(), buildFollowUp(LocalDate.now())));
        assertThrows(ForbiddenOperationException.class, () -> followUpService.createFollowUp(adoption.getId(),
                null, buildFollowUp(LocalDate.now())));
    }

    @Test
    void createFollowUp_withUnknownAdoption_shouldThrowNotFound() {
        assertThrows(EntityNotFoundException.class, () -> followUpService.createFollowUp(NON_EXISTENT_ID,
                owner.getId(), buildFollowUp(LocalDate.now())));
    }

    @Test
    void getFollowUps_shouldReturnNewestFirst() throws Exception {
        followUpService.createFollowUp(adoption.getId(), owner.getId(), buildFollowUp(LocalDate.now().minusDays(5)));
        followUpService.createFollowUp(adoption.getId(), owner.getId(), buildFollowUp(LocalDate.now()));
        followUpService.createFollowUp(adoption.getId(), owner.getId(), buildFollowUp(LocalDate.now().minusDays(2)));

        List<FollowUpEntity> result = followUpService.getFollowUps(adoption.getId(), owner.getId());

        assertEquals(3, result.size());
        assertEquals(LocalDate.now(), result.get(0).getDate());
        assertEquals(LocalDate.now().minusDays(2), result.get(1).getDate());
        assertEquals(LocalDate.now().minusDays(5), result.get(2).getDate());
    }

    @Test
    void getFollowUps_shouldBeEmptyWithoutRecords() throws Exception {
        assertEquals(0, followUpService.getFollowUps(adoption.getId(), owner.getId()).size());
    }

    @Test
    void getFollowUps_ofAnotherAdopter_shouldBeForbidden() {
        assertThrows(ForbiddenOperationException.class,
                () -> followUpService.getFollowUps(adoption.getId(), stranger.getId()));
    }

    @Test
    void getFollowUps_withUnknownAdoption_shouldThrowNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> followUpService.getFollowUps(NON_EXISTENT_ID, owner.getId()));
    }
}
