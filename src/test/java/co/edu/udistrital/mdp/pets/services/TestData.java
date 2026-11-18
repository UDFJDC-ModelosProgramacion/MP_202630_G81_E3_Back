package co.edu.udistrital.mdp.pets.services;

import java.time.LocalDate;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import jakarta.persistence.EntityManager;

/** Datos mínimos y controlados para las pruebas de los servicios nuevos. */
final class TestData {

    private TestData() {
    }

    static ShelterEntity shelter(EntityManager em, String name) {
        ShelterEntity shelter = new ShelterEntity();
        shelter.setName(name);
        shelter.setCity("Bogotá");
        em.persist(shelter);
        return shelter;
    }

    static PetEntity pet(EntityManager em, ShelterEntity shelter, String name, String status) {
        PetEntity pet = new PetEntity();
        pet.setName(name);
        pet.setStatus(status);
        pet.setShelter(shelter);
        em.persist(pet);
        return pet;
    }

    static VeterinarianEntity veterinarian(EntityManager em, ShelterEntity shelter, String name) {
        VeterinarianEntity vet = new VeterinarianEntity();
        vet.setName(name);
        vet.setSpeciality("General");
        vet.setShelter(shelter);
        em.persist(vet);
        return vet;
    }

    static AdopterEntity adopter(EntityManager em, String name) {
        AdopterEntity adopter = new AdopterEntity();
        adopter.setName(name);
        adopter.setPhone("3001234567");
        em.persist(adopter);
        return adopter;
    }

    static AdoptionEntity adoption(EntityManager em, PetEntity pet, AdopterEntity adopter, String status) {
        AdoptionEntity adoption = new AdoptionEntity();
        adoption.setPet(pet);
        adoption.setAdopter(adopter);
        adoption.setAdoptionDate(LocalDate.now().minusDays(10));
        adoption.setStatus(status);
        em.persist(adoption);
        return adoption;
    }
}
