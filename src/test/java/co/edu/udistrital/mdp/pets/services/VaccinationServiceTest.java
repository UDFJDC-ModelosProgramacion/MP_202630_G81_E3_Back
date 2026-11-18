package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.VaccinationEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import jakarta.persistence.EntityManager;

@DataJpaTest
@Transactional
@Import({ VaccinationService.class, AutoNotificationService.class })
class VaccinationServiceTest {

    @Autowired
    private VaccinationService vaccinationService;

    @Autowired
    private EntityManager entityManager;

    private ShelterEntity shelter;
    private PetEntity pet;

    @BeforeEach
    void setUp() {
        shelter = TestData.shelter(entityManager, "Huellas");
        pet = TestData.pet(entityManager, shelter, "Luna", "AVAILABLE");
    }

    private VaccinationEntity vaccination(String name, LocalDate applied, LocalDate next) {
        VaccinationEntity entity = new VaccinationEntity();
        entity.setName(name);
        entity.setApplicationDate(applied);
        entity.setNextDueDate(next);
        return entity;
    }

    @Test
    void createVaccination_shouldPersistAndNotifyShelter() throws Exception {
        VaccinationEntity result = vaccinationService.createVaccination(pet.getId(),
                vaccination("Rabia", LocalDate.now().minusDays(1), LocalDate.now().plusDays(30)));

        assertNotNull(result.getId());
        assertEquals(pet.getId(), result.getPet().getId());
        Long notifications = entityManager
                .createQuery("select count(n) from NotificationEntity n where n.shelter.id = :id", Long.class)
                .setParameter("id", shelter.getId()).getSingleResult();
        assertEquals(1L, notifications);
    }

    @Test
    void createVaccination_withBlankName_shouldThrow() {
        assertThrows(IllegalOperationException.class, () -> vaccinationService.createVaccination(pet.getId(),
                vaccination(" ", LocalDate.now(), null)));
    }

    @Test
    void createVaccination_withFutureApplicationDate_shouldThrow() {
        assertThrows(IllegalOperationException.class, () -> vaccinationService.createVaccination(pet.getId(),
                vaccination("Rabia", LocalDate.now().plusDays(1), null)));
    }

    @Test
    void createVaccination_withNextDueBeforeApplication_shouldThrow() {
        assertThrows(IllegalOperationException.class, () -> vaccinationService.createVaccination(pet.getId(),
                vaccination("Rabia", LocalDate.now(), LocalDate.now().minusDays(2))));
    }

    @Test
    void createVaccination_withUnknownPet_shouldThrow() {
        assertThrows(EntityNotFoundException.class, () -> vaccinationService.createVaccination(999999L,
                vaccination("Rabia", LocalDate.now(), null)));
    }

    @Test
    void createVaccination_withVeterinarianFromOtherShelter_shouldThrow() {
        ShelterEntity other = TestData.shelter(entityManager, "Otro");
        VeterinarianEntity vet = TestData.veterinarian(entityManager, other, "Dra. Ruiz");
        VaccinationEntity entity = vaccination("Rabia", LocalDate.now(), null);
        entity.setVeterinarian(vet);

        assertThrows(IllegalOperationException.class, () -> vaccinationService.createVaccination(pet.getId(), entity));
    }

    @Test
    void getUpcomingVaccinations_shouldReturnOnlyFutureDosesSorted() throws Exception {
        vaccinationService.createVaccination(pet.getId(),
                vaccination("Vieja", LocalDate.now().minusDays(60), LocalDate.now().minusDays(5)));
        vaccinationService.createVaccination(pet.getId(),
                vaccination("Lejana", LocalDate.now().minusDays(1), LocalDate.now().plusDays(90)));
        vaccinationService.createVaccination(pet.getId(),
                vaccination("Cercana", LocalDate.now().minusDays(1), LocalDate.now().plusDays(10)));

        List<VaccinationEntity> upcoming = vaccinationService.getUpcomingVaccinations(pet.getId());

        assertEquals(2, upcoming.size());
        assertEquals("Cercana", upcoming.get(0).getName());
        assertEquals("Lejana", upcoming.get(1).getName());
    }

    @Test
    void deleteVaccination_shouldRemoveIt() throws Exception {
        VaccinationEntity created = vaccinationService.createVaccination(pet.getId(),
                vaccination("Rabia", LocalDate.now(), null));

        vaccinationService.deleteVaccination(pet.getId(), created.getId());

        assertEquals(0, vaccinationService.getVaccinations(pet.getId()).size());
    }
}
