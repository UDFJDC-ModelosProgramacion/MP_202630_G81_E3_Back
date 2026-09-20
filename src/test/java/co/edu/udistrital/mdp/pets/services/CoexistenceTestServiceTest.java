package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.CoexistenceTestEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdopterRepository;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.repositories.CoexistenceTestRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;

@DataJpaTest
@Import(CoexistenceTestService.class)
class CoexistenceTestServiceTest {

    @Autowired
    private CoexistenceTestService coexistenceTestService;

    @Autowired
    private CoexistenceTestRepository coexistenceTestRepository;

    @Autowired
    private AdoptionRepository adoptionRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private AdopterRepository adopterRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    private CoexistenceTestEntity coexistenceTest;

    @BeforeEach
    void setUp() {
        adoptionRepository.deleteAll();
        coexistenceTestRepository.deleteAll();
        petRepository.deleteAll();
        adopterRepository.deleteAll();
        shelterRepository.deleteAll();

        coexistenceTest = new CoexistenceTestEntity();
        coexistenceTest.setStartDate(new Date());
        coexistenceTest.setResult("Aprobado");
    }

    @Test
    void createCoexistenceTestTest() throws Exception {
        CoexistenceTestEntity result =
                coexistenceTestService.createCoexistenceTest(
                        coexistenceTest);

        assertNotNull(result.getId());
        assertNotNull(result.getStartDate());
        assertEquals("Aprobado", result.getResult());
    }

    @Test
    void createCoexistenceTestWithoutStartDateTest() {
        coexistenceTest.setStartDate(null);

        assertThrows(
                IllegalOperationException.class,
                () -> coexistenceTestService.createCoexistenceTest(
                        coexistenceTest)
        );
    }

    @Test
    void createCoexistenceTestWithoutResultTest() {
        coexistenceTest.setResult("");

        assertThrows(
                IllegalOperationException.class,
                () -> coexistenceTestService.createCoexistenceTest(
                        coexistenceTest)
        );
    }

    @Test
    void getCoexistenceTestsTest() throws Exception {
        coexistenceTestService.createCoexistenceTest(coexistenceTest);

        List<CoexistenceTestEntity> result =
                coexistenceTestService.getCoexistenceTests();

        assertEquals(1, result.size());
        assertEquals("Aprobado", result.get(0).getResult());
    }

    @Test
    void getCoexistenceTestTest() throws Exception {
        CoexistenceTestEntity saved =
                coexistenceTestService.createCoexistenceTest(
                        coexistenceTest);

        CoexistenceTestEntity result =
                coexistenceTestService.getCoexistenceTest(
                        saved.getId());

        assertEquals(saved.getId(), result.getId());
        assertEquals("Aprobado", result.getResult());
    }

    @Test
    void getCoexistenceTestWithNonExistingIdTest() {
        assertThrows(
                EntityNotFoundException.class,
                () -> coexistenceTestService.getCoexistenceTest(
                        999999L)
        );
    }

    @Test
    void updateCoexistenceTestTest() throws Exception {
        CoexistenceTestEntity saved =
                coexistenceTestService.createCoexistenceTest(
                        coexistenceTest);

        CoexistenceTestEntity update =
                new CoexistenceTestEntity();

        update.setStartDate(new Date());
        update.setResult("No aprobado");

        CoexistenceTestEntity result =
                coexistenceTestService.updateCoexistenceTest(
                        saved.getId(), update);

        assertEquals(saved.getId(), result.getId());
        assertEquals("No aprobado", result.getResult());
        assertNotNull(result.getStartDate());
    }

    @Test
    void updateNonExistingCoexistenceTestTest() {
        assertThrows(
                EntityNotFoundException.class,
                () -> coexistenceTestService.updateCoexistenceTest(
                        999999L, coexistenceTest)
        );
    }

    @Test
    void updateCoexistenceTestWithoutStartDateTest()
            throws Exception {

        CoexistenceTestEntity saved =
                coexistenceTestService.createCoexistenceTest(
                        coexistenceTest);

        coexistenceTest.setStartDate(null);

        assertThrows(
                IllegalOperationException.class,
                () -> coexistenceTestService.updateCoexistenceTest(
                        saved.getId(), coexistenceTest)
        );
    }

    @Test
    void updateCoexistenceTestWithoutResultTest()
            throws Exception {

        CoexistenceTestEntity saved =
                coexistenceTestService.createCoexistenceTest(
                        coexistenceTest);

        coexistenceTest.setResult("");

        assertThrows(
                IllegalOperationException.class,
                () -> coexistenceTestService.updateCoexistenceTest(
                        saved.getId(), coexistenceTest)
        );
    }

    @Test
    void deleteCoexistenceTestTest() throws Exception {
        CoexistenceTestEntity saved =
                coexistenceTestService.createCoexistenceTest(
                        coexistenceTest);

        coexistenceTestService.deleteCoexistenceTest(
                saved.getId());

        assertFalse(
                coexistenceTestRepository
                        .findById(saved.getId())
                        .isPresent()
        );
    }

    @Test
    void deleteNonExistingCoexistenceTestTest() {
        assertThrows(
                EntityNotFoundException.class,
                () -> coexistenceTestService.deleteCoexistenceTest(
                        999999L)
        );
    }

    @Test
    void deleteCoexistenceTestWithAdoptionTest()
            throws Exception {

        ShelterEntity shelter = new ShelterEntity();
        shelter.setName("Refugio Test");
        shelter.setCity("Bogotá");
        shelter = shelterRepository.save(shelter);

        PetEntity pet = new PetEntity();
        pet.setName("Firulais");
        pet.setStatus("Disponible");
        pet.setAge(3);
        pet.setSex("Macho");
        pet.setSize("Mediano");
        pet.setShelter(shelter);
        pet = petRepository.save(pet);

        AdopterEntity adopter = new AdopterEntity();
        adopter.setName("Juan Test");
        adopter.setPhone("3000000000");
        adopter = adopterRepository.save(adopter);

        CoexistenceTestEntity saved =
                coexistenceTestService.createCoexistenceTest(
                        coexistenceTest);

        AdoptionEntity adoption = new AdoptionEntity();
        adoption.setPet(pet);
        adoption.setAdopter(adopter);
        adoption.setCoexistenceTest(saved);

        adoption = adoptionRepository.save(adoption);

        saved.setAdoption(adoption);

        assertThrows(
                IllegalOperationException.class,
                () -> coexistenceTestService.deleteCoexistenceTest(
                        saved.getId())
        );
    }
}
