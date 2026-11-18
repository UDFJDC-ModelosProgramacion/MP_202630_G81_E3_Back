package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.CoexistenceTestEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import jakarta.persistence.EntityManager;

@DataJpaTest
@Transactional
@Import({ AdoptionCoexistenceService.class, CoexistenceTestService.class, AutoNotificationService.class })
class AdoptionCoexistenceServiceTest {

    private static final long DAY = 24L * 60 * 60 * 1000;

    @Autowired
    private AdoptionCoexistenceService service;

    @Autowired
    private EntityManager entityManager;

    private PetEntity pet;
    private AdoptionEntity adoption;

    @BeforeEach
    void setUp() {
        pet = TestData.pet(entityManager, TestData.shelter(entityManager, "Huellas"), "Luna", "AVAILABLE");
        adoption = TestData.adoption(entityManager, pet, TestData.adopter(entityManager, "Ana"), "APPROVED");
    }

    private CoexistenceTestEntity test(String result, long startOffsetDays, long endOffsetDays) {
        CoexistenceTestEntity entity = new CoexistenceTestEntity();
        long now = System.currentTimeMillis();
        entity.setStartDate(new Date(now + startOffsetDays * DAY));
        entity.setEndDate(new Date(now + endOffsetDays * DAY));
        entity.setResult(result);
        return entity;
    }

    @Test
    void create_shouldLinkTestAndMovePetAndAdoptionToCohabitation() throws Exception {
        CoexistenceTestEntity created = service.createCoexistenceTest(adoption.getId(), test(null, 0, 14));

        assertNotNull(created.getId());
        assertEquals("IN_PROGRESS", created.getResult());
        assertEquals("COHABITATION", adoption.getStatus());
        assertEquals("IN_PROCESS", pet.getStatus());
        assertEquals(created.getId(), service.getCoexistenceTest(adoption.getId()).getId());
    }

    @Test
    void create_twice_shouldThrow() throws Exception {
        service.createCoexistenceTest(adoption.getId(), test("IN_PROGRESS", 0, 14));

        assertThrows(IllegalOperationException.class,
                () -> service.createCoexistenceTest(adoption.getId(), test("IN_PROGRESS", 0, 14)));
    }

    @Test
    void create_withEndBeforeStart_shouldThrow() {
        assertThrows(IllegalOperationException.class,
                () -> service.createCoexistenceTest(adoption.getId(), test("IN_PROGRESS", 5, 1)));
    }

    @Test
    void create_withInvalidResult_shouldThrow() {
        assertThrows(IllegalOperationException.class,
                () -> service.createCoexistenceTest(adoption.getId(), test("MAYBE", 0, 14)));
    }

    @Test
    void create_withUnknownAdoption_shouldThrow() {
        assertThrows(EntityNotFoundException.class,
                () -> service.createCoexistenceTest(999999L, test("IN_PROGRESS", 0, 14)));
    }

    @Test
    void update_withSuccessfulResult_shouldCompleteAdoptionAndAdoptPet() throws Exception {
        service.createCoexistenceTest(adoption.getId(), test("IN_PROGRESS", 0, 14));

        service.updateCoexistenceTest(adoption.getId(), test("SUCCESSFUL", 0, 14));

        assertEquals("COMPLETED", adoption.getStatus());
        assertEquals("ADOPTED", pet.getStatus());
    }

    @Test
    void get_withoutTest_shouldThrow() {
        assertThrows(EntityNotFoundException.class, () -> service.getCoexistenceTest(adoption.getId()));
    }

    @Test
    void delete_shouldUnlinkTest() throws Exception {
        service.createCoexistenceTest(adoption.getId(), test("IN_PROGRESS", 0, 14));

        service.deleteCoexistenceTest(adoption.getId());

        assertThrows(EntityNotFoundException.class, () -> service.getCoexistenceTest(adoption.getId()));
    }
}
