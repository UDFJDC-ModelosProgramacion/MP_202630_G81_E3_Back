package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import jakarta.transaction.Transactional;

import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
import co.edu.udistrital.mdp.pets.repositories.VeterinarianRepository;

@DataJpaTest
@Transactional
@Import(VeterinarianService.class)
class VeterinarianServiceTest {

    @Autowired
    private VeterinarianService veterinarianService;

    @Autowired
    private VeterinarianRepository veterinarianRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    private List<VeterinarianEntity> veterinarianList = new ArrayList<>();
    private List<ShelterEntity> shelterList = new ArrayList<>();

    @BeforeEach
    void setUp() {

        veterinarianRepository.deleteAll();
        shelterRepository.deleteAll();

        for (int i = 0; i < 3; i++) {

            ShelterEntity shelter = new ShelterEntity();

            shelter.setName("Shelter " + i);
            shelter.setCity("Bogota");

            shelter = shelterRepository.save(shelter);

            shelterList.add(shelter);

            VeterinarianEntity veterinarian = new VeterinarianEntity();

            veterinarian.setName("Veterinarian " + i);
            veterinarian.setSpeciality("Speciality " + i);
            veterinarian.setShelter(shelter);

            veterinarian = veterinarianRepository.save(veterinarian);

            veterinarianList.add(veterinarian);
        }
    }

    @Test
    void createVeterinarianTest() throws Exception {

        VeterinarianEntity newVeterinarian =
                new VeterinarianEntity();

        newVeterinarian.setName("New Veterinarian");
        newVeterinarian.setSpeciality("Surgery");
        newVeterinarian.setShelter(shelterList.get(0));

        VeterinarianEntity result =
                veterinarianService.createVeterinarian(
                        newVeterinarian);

        assertNotNull(result);

        VeterinarianEntity entity =
                veterinarianRepository.findById(
                        result.getId()).get();

        assertEquals("New Veterinarian", entity.getName());
        assertEquals("Surgery", entity.getSpeciality());
        assertEquals(
                shelterList.get(0).getId(),
                entity.getShelter().getId());
    }

    @Test
    void createVeterinarianWithoutNameTest() {

        VeterinarianEntity veterinarian =
                new VeterinarianEntity();

        veterinarian.setName("");
        veterinarian.setSpeciality("Surgery");
        veterinarian.setShelter(shelterList.get(0));

        assertThrows(
                IllegalOperationException.class,
                () -> veterinarianService.createVeterinarian(
                        veterinarian));
    }

    @Test
    void createVeterinarianWithoutSpecialityTest() {

        VeterinarianEntity veterinarian =
                new VeterinarianEntity();

        veterinarian.setName("Veterinarian");
        veterinarian.setSpeciality("");
        veterinarian.setShelter(shelterList.get(0));

        assertThrows(
                IllegalOperationException.class,
                () -> veterinarianService.createVeterinarian(
                        veterinarian));
    }

    @Test
    void createVeterinarianWithoutShelterTest() {

        VeterinarianEntity veterinarian =
                new VeterinarianEntity();

        veterinarian.setName("Veterinarian");
        veterinarian.setSpeciality("Surgery");
        veterinarian.setShelter(null);

        assertThrows(
                IllegalOperationException.class,
                () -> veterinarianService.createVeterinarian(
                        veterinarian));
    }

    @Test
    void createVeterinarianWithInvalidShelterTest() {

        VeterinarianEntity veterinarian =
                new VeterinarianEntity();

        ShelterEntity invalidShelter =
                new ShelterEntity();

        invalidShelter.setId(999L);

        veterinarian.setName("Veterinarian");
        veterinarian.setSpeciality("Surgery");
        veterinarian.setShelter(invalidShelter);

        assertThrows(
                EntityNotFoundException.class,
                () -> veterinarianService.createVeterinarian(
                        veterinarian));
    }

    @Test
    void getVeterinariansTest() {

        List<VeterinarianEntity> result =
                veterinarianService.getVeterinarians();

        assertEquals(3, result.size());
    }

    @Test
    void getVeterinarianTest() throws Exception {

        VeterinarianEntity entity =
                veterinarianList.get(0);

        VeterinarianEntity result =
                veterinarianService.getVeterinarian(
                        entity.getId());

        assertNotNull(result);
        assertEquals(entity.getId(), result.getId());
        assertEquals(entity.getName(), result.getName());
    }

    @Test
    void getVeterinarianNotFoundTest() {

        assertThrows(
                EntityNotFoundException.class,
                () -> veterinarianService.getVeterinarian(999L));
    }

    @Test
    void updateVeterinarianTest() throws Exception {

        VeterinarianEntity veterinarian =
                veterinarianList.get(0);

        VeterinarianEntity updated =
                new VeterinarianEntity();

        updated.setName("Updated Veterinarian");
        updated.setSpeciality("Dermatology");
        updated.setShelter(shelterList.get(1));

        VeterinarianEntity result =
                veterinarianService.updateVeterinarian(
                        veterinarian.getId(),
                        updated);

        assertEquals(
                veterinarian.getId(),
                result.getId());

        assertEquals(
                "Updated Veterinarian",
                result.getName());

        assertEquals(
                "Dermatology",
                result.getSpeciality());

        assertEquals(
                shelterList.get(1).getId(),
                result.getShelter().getId());
    }

    @Test
    void updateVeterinarianNotFoundTest() {

        VeterinarianEntity veterinarian =
                new VeterinarianEntity();

        veterinarian.setName("Veterinarian");
        veterinarian.setSpeciality("Surgery");
        veterinarian.setShelter(shelterList.get(0));

        assertThrows(
                EntityNotFoundException.class,
                () -> veterinarianService.updateVeterinarian(
                        999L,
                        veterinarian));
    }

    @Test
    void deleteVeterinarianTest() throws Exception {

        VeterinarianEntity veterinarian =
                veterinarianList.get(0);

        veterinarianService.deleteVeterinarian(
                veterinarian.getId());

        assertThrows(
                EntityNotFoundException.class,
                () -> veterinarianService.getVeterinarian(
                        veterinarian.getId()));
    }

    @Test
    void deleteVeterinarianNotFoundTest() {

        assertThrows(
                EntityNotFoundException.class,
                () -> veterinarianService.deleteVeterinarian(
                        999L));
    }
}
