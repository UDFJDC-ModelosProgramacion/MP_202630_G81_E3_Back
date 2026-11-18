package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.dto.ShelterReportDTO;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.CaseDevolutionEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import jakarta.persistence.EntityManager;

@DataJpaTest
@Transactional
@Import(ShelterReportService.class)
class ShelterReportServiceTest {

    @Autowired
    private ShelterReportService reportService;

    @Autowired
    private EntityManager entityManager;

    private ShelterEntity shelter;

    @BeforeEach
    void setUp() {
        shelter = TestData.shelter(entityManager, "Huellas");
        PetEntity adopted = TestData.pet(entityManager, shelter, "Luna", "ADOPTED");
        PetEntity returned = TestData.pet(entityManager, shelter, "Max", "AVAILABLE");
        TestData.pet(entityManager, shelter, "Rex", "IN_PROCESS");

        TestData.adoption(entityManager, adopted, TestData.adopter(entityManager, "Ana"), "COMPLETED");
        AdoptionEntity returnedAdoption = TestData.adoption(entityManager, returned,
                TestData.adopter(entityManager, "Luis"), "RETURNED");

        CaseDevolutionEntity devolution = new CaseDevolutionEntity();
        devolution.setAdoption(returnedAdoption);
        devolution.setDate(LocalDate.now());
        devolution.setReason("Incompatibilidad");
        entityManager.persist(devolution);

        // Otro refugio: no debe contarse.
        PetEntity foreign = TestData.pet(entityManager, TestData.shelter(entityManager, "Otro"), "Zeus", "ADOPTED");
        TestData.adoption(entityManager, foreign, TestData.adopter(entityManager, "Eva"), "COMPLETED");

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void getReport_shouldCountOnlyThisShelter() throws Exception {
        ShelterReportDTO report = reportService.getReport(shelter.getId(), null, null);

        assertEquals(3, report.getTotalPets());
        assertEquals(1, report.getAvailablePets());
        assertEquals(1, report.getInProcessPets());
        assertEquals(1, report.getAdoptedPets());
        assertEquals(2, report.getTotalAdoptions());
        assertEquals(1, report.getAdoptionsByStatus().get("COMPLETED"));
        assertEquals(1, report.getTotalReturns());
        assertEquals(1, report.getReturnsByReason().get("Incompatibilidad"));
    }

    @Test
    void getReport_withRangeInTheFuture_shouldBeEmptyForAdoptionsAndReturns() throws Exception {
        ShelterReportDTO report = reportService.getReport(shelter.getId(), LocalDate.now().plusDays(1), null);

        assertEquals(0, report.getTotalAdoptions());
        assertEquals(0, report.getTotalReturns());
        assertEquals(3, report.getTotalPets());
    }

    @Test
    void getReport_withInvertedRange_shouldThrow() {
        assertThrows(IllegalOperationException.class,
                () -> reportService.getReport(shelter.getId(), LocalDate.now(), LocalDate.now().minusDays(1)));
    }

    @Test
    void getReport_withUnknownShelter_shouldThrow() {
        assertThrows(EntityNotFoundException.class, () -> reportService.getReport(999999L, null, null));
    }
}
