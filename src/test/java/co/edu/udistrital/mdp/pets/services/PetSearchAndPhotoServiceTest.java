package co.edu.udistrital.mdp.pets.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import jakarta.persistence.EntityManager;

@DataJpaTest
@Transactional
@Import(PetService.class)
class PetSearchAndPhotoServiceTest {

    @Autowired
    private PetService petService;

    @Autowired
    private EntityManager entityManager;

    private ShelterEntity shelter;
    private PetEntity youngDog;
    private PetEntity oldCat;

    @BeforeEach
    void setUp() {
        shelter = TestData.shelter(entityManager, "Huellas");
        youngDog = pet("Luna", "Perro", 2, "Tamaño pequeño", true, false);
        oldCat = pet("Michi", "Gato", 9, "Tamaño mediano", false, true);
        PetEntity adopted = pet("Rex", "Perro", 3, "Tamaño grande", true, true);
        adopted.setStatus("ADOPTED");
    }

    private PetEntity pet(String name, String species, int age, String size, boolean children, boolean otherPets) {
        PetEntity pet = TestData.pet(entityManager, shelter, name, "AVAILABLE");
        pet.setSpecies(species);
        pet.setAge(age);
        pet.setSize(size);
        pet.setCompatibleWithChildren(children);
        pet.setCompatibleWithOtherPets(otherPets);
        return pet;
    }

    private List<PetEntity> search(String species, Integer min, Integer max, String size, Boolean children,
            Boolean others) {
        return petService.getAvailablePetsByFilters(null, null, species, min, max, size, children, others);
    }

    @Test
    void search_shouldExcludeNonAvailablePets() {
        assertEquals(2, search(null, null, null, null, null, null).size());
    }

    @Test
    void search_bySpecies_shouldIgnoreCase() {
        List<PetEntity> result = search("perro", null, null, null, null, null);

        assertEquals(1, result.size());
        assertEquals(youngDog.getId(), result.get(0).getId());
    }

    @Test
    void search_byAgeRange_shouldKeepPetsInsideTheRange() {
        List<PetEntity> result = search(null, 5, 12, null, null, null);

        assertEquals(1, result.size());
        assertEquals(oldCat.getId(), result.get(0).getId());
    }

    @Test
    void search_bySize_shouldMatchExactSize() {
        assertEquals(1, search(null, null, null, "tamaño pequeño", null, null).size());
    }

    @Test
    void search_byCompatibility_shouldOnlyRestrictWhenTrue() {
        assertEquals(1, search(null, null, null, null, true, null).size());
        assertEquals(1, search(null, null, null, null, null, true).size());
        assertEquals(2, search(null, null, null, null, false, false).size());
    }

    @Test
    void addPhoto_shouldAppendTrimmedUrl() throws Exception {
        PetEntity result = petService.addPhoto(youngDog.getId(), "  https://fotos.test/luna.jpg ");

        assertTrue(result.getPhotos().contains("https://fotos.test/luna.jpg"));
    }

    @Test
    void addPhoto_withInvalidUrl_shouldThrow() {
        assertThrows(IllegalOperationException.class, () -> petService.addPhoto(youngDog.getId(), "luna.jpg"));
        assertThrows(IllegalOperationException.class, () -> petService.addPhoto(youngDog.getId(), " "));
        assertThrows(IllegalOperationException.class,
                () -> petService.addPhoto(youngDog.getId(), "https://x.test/" + "a".repeat(260)));
    }

    @Test
    void addPhoto_withUnknownPet_shouldThrow() {
        assertThrows(EntityNotFoundException.class, () -> petService.addPhoto(999999L, "https://x.test/a.jpg"));
    }
}
