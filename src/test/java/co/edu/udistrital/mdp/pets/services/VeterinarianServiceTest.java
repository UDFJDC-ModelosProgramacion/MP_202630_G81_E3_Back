package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
import co.edu.udistrital.mdp.pets.repositories.VeterinarianRepository;

@DataJpaTest
@Import(VeterinarianService.class)
class VeterinarianServiceTest {

    @Autowired
    private VeterinarianService veterinarianService;

    @Autowired
    private VeterinarianRepository veterinarianRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    private ShelterEntity shelter;
    private VeterinarianEntity veterinarian;

    @BeforeEach
    void setUp() {
        veterinarianRepository.deleteAll();
        shelterRepository.deleteAll();

        shelter = new ShelterEntity();
        shelter.setName("Refugio Test");
        shelter.setCity("Bogotá");
        shelter = shelterRepository.save(shelter);

        veterinarian = new VeterinarianEntity();
        veterinarian.setName("Carlos Pérez");
        veterinarian.setSpeciality("Cirugía");
        veterinarian.setShelter(shelter);
    }

    @Test
    void createVeterinarianTest() throws Exception {
        VeterinarianEntity result =
                veterinarianService.createVeterinarian(veterinarian);

        assertNotNull(result.getId());
        assertEquals("Carlos Pérez", result.getName());
        assertEquals("Cirugía", result.getSpeciality());
        assertEquals(shelter.getId(), result.getShelter().getId());
    }

    @Test
    void createVeterinarianWithoutNameTest() {
        veterinarian.setName("");

        assertThrows(
                IllegalOperationException.class,
                () -> veterinarianService.createVeterinarian(veterinarian)
        );
    }

    @Test
    void createVeterinarianWithoutSpecialityTest() {
        veterinarian.setSpeciality("");

        assertThrows(
                IllegalOperationException.class,
                () -> veterinarianService.createVeterinarian(veterinarian)
        );
    }

    @Test
    void createVeterinarianWithoutShelterTest() {
        veterinarian.setShelter(null);

        assertThrows(
                IllegalOperationException.class,
                () -> veterinarianService.createVeterinarian(veterinarian)
        );
    }

    @Test
    void createVeterinarianWithNonExistingShelterTest() {
        ShelterEntity invalidShelter = new ShelterEntity();
        invalidShelter.setId(999999L);

        veterinarian.setShelter(invalidShelter);

        assertThrows(
                EntityNotFoundException.class,
                () -> veterinarianService.createVeterinarian(veterinarian)
        );
    }

    @Test
    void getVeterinariansTest() throws Exception {
        veterinarianService.createVeterinarian(veterinarian);

        List<VeterinarianEntity> result =
                veterinarianService.getVeterinarians();

        assertEquals(1, result.size());
        assertEquals("Carlos Pérez", result.get(0).getName());
    }

    @Test
    void getVeterinarianTest() throws Exception {
        VeterinarianEntity saved =
                veterinarianService.createVeterinarian(veterinarian);

        VeterinarianEntity result =
                veterinarianService.getVeterinarian(saved.getId());

        assertEquals(saved.getId(), result.getId());
        assertEquals("Carlos Pérez", result.getName());
    }

    @Test
    void getVeterinarianWithNonExistingIdTest() {
        assertThrows(
                EntityNotFoundException.class,
                () -> veterinarianService.getVeterinarian(999999L)
        );
    }

    @Test
    void updateVeterinarianTest() throws Exception {
        VeterinarianEntity saved =
                veterinarianService.createVeterinarian(veterinarian);

        VeterinarianEntity update = new VeterinarianEntity();
        update.setName("Ana Gómez");
        update.setSpeciality("Dermatología");
        update.setShelter(shelter);

        VeterinarianEntity result =
                veterinarianService.updateVeterinarian(
                        saved.getId(), update);

        assertEquals(saved.getId(), result.getId());
        assertEquals("Ana Gómez", result.getName());
        assertEquals("Dermatología", result.getSpeciality());
        assertEquals(shelter.getId(), result.getShelter().getId());
    }

    @Test
    void updateNonExistingVeterinarianTest() {
        assertThrows(
                EntityNotFoundException.class,
                () -> veterinarianService.updateVeterinarian(
                        999999L, veterinarian)
        );
    }

    @Test
    void updateVeterinarianWithoutNameTest() throws Exception {
        VeterinarianEntity saved =
                veterinarianService.createVeterinarian(veterinarian);

        veterinarian.setName("");

        assertThrows(
                IllegalOperationException.class,
                () -> veterinarianService.updateVeterinarian(
                        saved.getId(), veterinarian)
        );
    }

    @Test
    void updateVeterinarianWithoutSpecialityTest() throws Exception {
        VeterinarianEntity saved =
                veterinarianService.createVeterinarian(veterinarian);

        veterinarian.setSpeciality("");

        assertThrows(
                IllegalOperationException.class,
                () -> veterinarianService.updateVeterinarian(
                        saved.getId(), veterinarian)
        );
    }

    @Test
    void updateVeterinarianWithoutShelterTest() throws Exception {
        VeterinarianEntity saved =
                veterinarianService.createVeterinarian(veterinarian);

        veterinarian.setShelter(null);

        assertThrows(
                IllegalOperationException.class,
                () -> veterinarianService.updateVeterinarian(
                        saved.getId(), veterinarian)
        );
    }

    @Test
    void updateVeterinarianWithNonExistingShelterTest()
            throws Exception {

        VeterinarianEntity saved =
                veterinarianService.createVeterinarian(veterinarian);

        ShelterEntity invalidShelter = new ShelterEntity();
        invalidShelter.setId(999999L);

        veterinarian.setShelter(invalidShelter);

        assertThrows(
                EntityNotFoundException.class,
                () -> veterinarianService.updateVeterinarian(
                        saved.getId(), veterinarian)
        );
    }

    @Test
    void deleteVeterinarianTest() throws Exception {
        VeterinarianEntity saved =
                veterinarianService.createVeterinarian(veterinarian);

        veterinarianService.deleteVeterinarian(saved.getId());

        assertFalse(
                veterinarianRepository
                        .findById(saved.getId())
                        .isPresent()
        );
    }

    @Test
    void deleteNonExistingVeterinarianTest() {
        assertThrows(
                EntityNotFoundException.class,
                () -> veterinarianService.deleteVeterinarian(999999L)
        );
    }
}
