package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.PetEventEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PetService {

    //Initialize constants for event types and active adoption statuses
    public static final String ARRIVAL_EVENT_TYPE = "ARRIVAL";

    public static final List<String> ACTIVE_ADOPTION_STATUSES = List.of("IN_PROGRESS", "COHABITATION");

    private PetRepository petRepository;
    private ShelterRepository shelterRepository;
    private AdoptionRepository adoptionRepository;

    public PetService(PetRepository petRepository, ShelterRepository shelterRepository, AdoptionRepository adoptionRepository) {
        this.petRepository = petRepository;
        this.shelterRepository = shelterRepository;
        this.adoptionRepository = adoptionRepository;
    }


    @Transactional
    public PetEntity createPet(PetEntity petEntity) throws EntityNotFoundException, IllegalOperationException {

        log.info("Inicia proceso de creación de la mascota");

        if (petEntity.getName() == null || petEntity.getName().isBlank())
            throw new IllegalOperationException("Name is not valid");

        if (petEntity.getShelter() == null || petEntity.getShelter().getId() == null)
            throw new IllegalOperationException("Shelter is not valid");

        Optional<ShelterEntity> shelterEntity = shelterRepository.findById(petEntity.getShelter().getId());
        if (shelterEntity.isEmpty())
            throw new EntityNotFoundException("Shelter not found");

        List<PetEventEntity> events = petEntity.getEvents();
        //All the validation logic is in this boolean expression, so we can just check it and throw an exception if it's not valid
        boolean hasSingleArrivalEvent = events != null
                && events.size() == 1
                && ARRIVAL_EVENT_TYPE.equals(events.get(0).getEventType())
                && events.get(0).getDescription() != null
                && !events.get(0).getDescription().isBlank();
        
        //This if statement checks if the pet has exactly one ARRIVAL event, and if not, it throws an exception
        if (!hasSingleArrivalEvent)
            throw new IllegalOperationException("Pet must be created with exactly one ARRIVAL event");

        petEntity.setShelter(shelterEntity.get());
        events.get(0).setPet(petEntity);

        if (petEntity.getStatus() == null || petEntity.getStatus().isBlank())
            petEntity.setStatus("AVAILABLE");

        log.info("Termina proceso de creación de la mascota");
        return petRepository.save(petEntity);
    }

    @Transactional
    public List<PetEntity> getPets() {
        log.info("Inicia proceso de consultar todas las mascotas");
        return petRepository.findAll();
    }

    @Transactional
    public PetEntity getPet(Long petId) throws EntityNotFoundException {
        log.info("Inicia proceso de consultar la mascota con id = {0}", petId);
        Optional<PetEntity> petEntity = petRepository.findById(petId);
        if (petEntity.isEmpty())
            throw new EntityNotFoundException("Pet not found");
        return petEntity.get();
    }

    @Transactional
    public List<PetEntity> getAvailablePetsByFilters(String activityLevel, String spaceRequirement) {
        log.info("Inicia proceso de búsqueda avanzada de mascotas disponibles");
        List<PetEntity> pets = petRepository.findByStatus("AVAILABLE");
        return pets.stream()
                .filter(p -> activityLevel == null || activityLevel.equals(p.getActivityLevel()))
                .filter(p -> spaceRequirement == null || spaceRequirement.equals(p.getSpaceRequirement()))
                .toList();
    }

    @Transactional
    public PetEntity updatePet(Long petId, PetEntity pet) throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de actualizar la mascota con id = {0}", petId);
        Optional<PetEntity> petEntity = petRepository.findById(petId);
        if (petEntity.isEmpty())
            throw new EntityNotFoundException("Pet not found");

        PetEntity existing = petEntity.get();

        boolean changesShelter = pet.getShelter() != null
                && existing.getShelter() != null
                && !existing.getShelter().getId().equals(pet.getShelter().getId());

        if (changesShelter && hasActiveAdoption(petId))
            throw new IllegalOperationException("Unable to change shelter because the pet has an active adoption");

        pet.setId(petId);
        pet.setEvents(existing.getEvents());
        pet.setBreed(existing.getBreed());

        log.info("Termina proceso de actualizar la mascota con id = {0}", petId);
        return petRepository.save(pet);
    }

    @Transactional
    public void deletePet(Long petId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de borrar la mascota con id = {0}", petId);
        Optional<PetEntity> petEntity = petRepository.findById(petId);
        if (petEntity.isEmpty())
            throw new EntityNotFoundException("Pet not found");

        List<AdoptionEntity> adoptions = adoptionRepository.findByPetId(petId);
        if (!adoptions.isEmpty())
            throw new IllegalOperationException("Unable to delete pet because it has an adoption history");

        petRepository.deleteById(petId);
        log.info("Termina proceso de borrar la mascota con id = {0}", petId);
    }

    private boolean hasActiveAdoption(Long petId) {
        return adoptionRepository.findByPetId(petId).stream()
                .anyMatch(a -> ACTIVE_ADOPTION_STATUSES.contains(a.getStatus()));
    }
}
