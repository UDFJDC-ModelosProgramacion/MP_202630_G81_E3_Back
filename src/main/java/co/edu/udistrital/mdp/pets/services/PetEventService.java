package co.edu.udistrital.mdp.pets.services;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.PetEventEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.PetEventRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PetEventService {

    public static final String ARRIVAL_EVENT_TYPE = "ARRIVAL";
    public static final String VACCINATION_EVENT_TYPE = "VACCINATION";

    private PetEventRepository petEventRepository;
    private PetRepository petRepository;

    public PetEventService(PetEventRepository petEventRepository, PetRepository petRepository) {
        this.petEventRepository = petEventRepository;
        this.petRepository = petRepository;
    }

    /**
     * Method to create a new event for a pet. It validates the input and checks if the pet exists before saving the event.
     * @param petId
     * @param petEventEntity
     * @return
     * @throws EntityNotFoundException
     * @throws IllegalOperationException
     */
    @Transactional
    public PetEventEntity createEvent(Long petId, PetEventEntity petEventEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de creación de un evento para la mascota con id = {0}", petId);

        if (petEventEntity.getEventType() == null || petEventEntity.getEventType().isBlank())
            throw new IllegalOperationException("Event type is not valid");

        if (petEventEntity.getDate() == null)
            throw new IllegalOperationException("Date is not valid");

        if (ARRIVAL_EVENT_TYPE.equals(petEventEntity.getEventType()))
            throw new IllegalOperationException("Arrival events are generated automatically when the pet is created");

        PetEntity pet = findPetOrThrow(petId);
        petEventEntity.setPet(pet);

        log.info("Termina proceso de creación de un evento para la mascota con id = {0}", petId);
        return petEventRepository.save(petEventEntity);
    }

    @Transactional
    public List<PetEventEntity> getEvents(Long petId) throws EntityNotFoundException {
        log.info("Inicia proceso de consultar los eventos de la mascota con id = {0}", petId);
        findPetOrThrow(petId);
        return petEventRepository.findByPetIdOrderByDateAsc(petId);
    }

    @Transactional
    public List<PetEventEntity> getEventsByType(Long petId, String eventType) throws EntityNotFoundException {
        log.info("Inicia proceso de consultar los eventos de tipo {0} de la mascota con id = {1}", eventType, petId);
        findPetOrThrow(petId);
        return petEventRepository.findByPetIdAndEventType(petId, eventType);
    }

    @Transactional
    public PetEventEntity getEvent(Long petId, Long eventId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de consultar el evento con id = {0} de la mascota con id = {1}", eventId, petId);
        findPetOrThrow(petId);
        return findEventOrThrow(petId, eventId);
    }

    @Transactional
    public PetEventEntity updateEvent(Long petId, Long eventId, PetEventEntity petEventEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de actualizar el evento con id = {0} de la mascota con id = {1}", eventId, petId);

        if (petEventEntity.getEventType() == null || petEventEntity.getEventType().isBlank())
            throw new IllegalOperationException("Event type is not valid");

        if (petEventEntity.getDate() == null)
            throw new IllegalOperationException("Date is not valid");

        PetEntity pet = findPetOrThrow(petId);
        PetEventEntity existing = findEventOrThrow(petId, eventId);

        if (ARRIVAL_EVENT_TYPE.equals(existing.getEventType()))
            throw new IllegalOperationException("Arrival events cannot be modified");

        if (petEventEntity.getPet() != null && !petId.equals(petEventEntity.getPet().getId()))
            throw new IllegalOperationException("The pet of an existing event cannot be reassigned");

        petEventEntity.setId(eventId);
        petEventEntity.setPet(pet);

        log.info("Termina proceso de actualizar el evento con id = {0} de la mascota con id = {1}", eventId, petId);
        return petEventRepository.save(petEventEntity);
    }

    @Transactional
    public void deleteEvent(Long petId, Long eventId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de borrar el evento con id = {0} de la mascota con id = {1}", eventId, petId);

        findPetOrThrow(petId);
        PetEventEntity existing = findEventOrThrow(petId, eventId);

        if (VACCINATION_EVENT_TYPE.equals(existing.getEventType())
                && existing.getDate().before(new Date(System.currentTimeMillis())))
            throw new IllegalOperationException("Unable to delete a vaccination event that already occurred");

        petEventRepository.deleteById(eventId);
        log.info("Termina proceso de borrar el evento con id = {0} de la mascota con id = {1}", eventId, petId);
    }

    private PetEntity findPetOrThrow(Long petId) throws EntityNotFoundException {
        Optional<PetEntity> petEntity = petRepository.findById(petId);
        if (petEntity.isEmpty())
            throw new EntityNotFoundException("Pet not found");
        return petEntity.get();
    }

    private PetEventEntity findEventOrThrow(Long petId, Long eventId)
            throws EntityNotFoundException, IllegalOperationException {
        Optional<PetEventEntity> eventEntity = petEventRepository.findById(eventId);
        if (eventEntity.isEmpty())
            throw new EntityNotFoundException("Pet event not found");

        PetEventEntity event = eventEntity.get();
        if (event.getPet() == null || !event.getPet().getId().equals(petId))
            throw new IllegalOperationException("The event does not belong to the given pet");

        return event;
    }
}