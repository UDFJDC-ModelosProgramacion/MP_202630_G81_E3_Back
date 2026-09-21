package co.edu.udistrital.mdp.pets.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.BreedEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.BreedRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BreedService {

    private static final String ACTION_1 = "action1"; // Compliant

    private BreedRepository breedRepository;
    private PetRepository petRepository;

    public BreedService(BreedRepository breedRepository, PetRepository petRepository) {
        this.breedRepository = breedRepository;
        this.petRepository = petRepository;
    }

    public void run() {
        prepare(ACTION_1); // Compliant
        execute(ACTION_1);
        release(ACTION_1);
    }

    private void prepare(String action) {
        // no-op
    }

    private void execute(String action) {
        // no-op
    }

    private void release(String action) {
        // no-op
    }

    @Transactional
    public BreedEntity createBreed(Long petId, BreedEntity breedEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de asociar una raza a la mascota con id = {0}", petId);

        if (breedEntity.getName() == null || breedEntity.getName().isBlank())
            throw new IllegalOperationException("Name is not valid");

        PetEntity pet = findPetOrThrow(petId);
        if (pet.getBreed() != null)
            throw new IllegalOperationException("The pet already has a breed associated");

        breedEntity.setPet(pet);

        log.info("Termina proceso de asociar una raza a la mascota con id = {0}", petId);
        return breedRepository.save(breedEntity);
    }

    @Transactional
    public BreedEntity getBreed(Long petId) throws EntityNotFoundException {
        log.info("Inicia proceso de consultar la raza de la mascota con id = {0}", petId);
        PetEntity pet = findPetOrThrow(petId);
        if (pet.getBreed() == null)
            throw new EntityNotFoundException("Breed not found for this pet");
        return pet.getBreed();
    }

    @Transactional
    public BreedEntity updateBreed(Long petId, BreedEntity breedEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de actualizar la raza de la mascota con id = {0}", petId);

        if (breedEntity.getName() == null || breedEntity.getName().isBlank())
            throw new IllegalOperationException("Name is not valid");

        PetEntity pet = findPetOrThrow(petId);
        if (pet.getBreed() == null)
            throw new EntityNotFoundException("Breed not found for this pet");

        if (breedEntity.getPet() != null && !petId.equals(breedEntity.getPet().getId()))
            throw new IllegalOperationException("A breed cannot be reassigned to a different pet");

        breedEntity.setId(pet.getBreed().getId());
        breedEntity.setPet(pet);

        log.info("Termina proceso de actualizar la raza de la mascota con id = {0}", petId);
        return breedRepository.save(breedEntity);
    }

    @Transactional
    public void deleteBreed(Long petId) throws EntityNotFoundException {
        log.info("Inicia proceso de borrar la raza de la mascota con id = {0}", petId);
        PetEntity pet = findPetOrThrow(petId);
        if (pet.getBreed() == null)
            throw new EntityNotFoundException("Breed not found for this pet");

        breedRepository.deleteById(pet.getBreed().getId());
        log.info("Termina proceso de borrar la raza de la mascota con id = {0}", petId);
    }

    private PetEntity findPetOrThrow(Long petId) throws EntityNotFoundException {
        Optional<PetEntity> petEntity = petRepository.findById(petId);
        if (petEntity.isEmpty())
            throw new EntityNotFoundException("Pet not found");
        return petEntity.get();
    }
}
