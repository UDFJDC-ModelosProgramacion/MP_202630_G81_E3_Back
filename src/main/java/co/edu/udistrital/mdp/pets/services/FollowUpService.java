package co.edu.udistrital.mdp.pets.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.FollowUpEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ForbiddenOperationException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.repositories.FollowUpRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FollowUpService {

    public static final List<String> ADAPTATION_STATUSES = List.of("ADAPTING", "GOOD", "EXCELLENT", "DIFFICULT");

    /** Adopciones que aún no se concretan o ya no siguen: no admiten seguimiento. */
    public static final List<String> NOT_FOLLOWABLE_ADOPTION_STATUSES = List.of("PENDING", "IN_PROGRESS",
            "CANCELLED", "CANCELED", "REJECTED");

    public static final int MAX_OBSERVATIONS_LENGTH = 255;

    private static final String ADOPTION_NOT_FOUND = "Adopción no encontrada";
    private static final String ACCESS_DENIED = "No tienes acceso al seguimiento de esta adopción";

    private final FollowUpRepository followUpRepository;
    private final AdoptionRepository adoptionRepository;

    public FollowUpService(FollowUpRepository followUpRepository, AdoptionRepository adoptionRepository) {
        this.followUpRepository = followUpRepository;
        this.adoptionRepository = adoptionRepository;
    }

    /**
     * Registra un seguimiento. La adopción debe pertenecer al adoptante; si no, se deniega sin
     * revelar datos. Valida fecha, estado de adaptación y observaciones antes de guardar.
     */
    @Transactional
    public FollowUpEntity createFollowUp(Long adoptionId, Long adopterId, FollowUpEntity followUp)
            throws EntityNotFoundException, ForbiddenOperationException, IllegalOperationException {
        log.info("Inicia proceso de creación de un seguimiento para la adopción con id = {}", adoptionId);

        AdoptionEntity adoption = findOwnedAdoption(adoptionId, adopterId);

        if (adoption.getStatus() != null
                && NOT_FOLLOWABLE_ADOPTION_STATUSES.contains(adoption.getStatus().toUpperCase()))
            throw new IllegalOperationException("La adopción aún no admite seguimiento");

        if (followUp.getDate() == null || followUp.getDate().isAfter(LocalDate.now()))
            throw new IllegalOperationException("La fecha del seguimiento no es válida");

        if (followUp.getAdaptationStatus() == null || !ADAPTATION_STATUSES.contains(followUp.getAdaptationStatus()))
            throw new IllegalOperationException("El estado de adaptación no es válido");

        if (followUp.getObservations() == null || followUp.getObservations().isBlank())
            throw new IllegalOperationException("Las observaciones son obligatorias");

        if (followUp.getObservations().trim().length() > MAX_OBSERVATIONS_LENGTH)
            throw new IllegalOperationException("Las observaciones superan el máximo de caracteres");

        followUp.setId(null);
        followUp.setObservations(followUp.getObservations().trim());
        followUp.setAdoption(adoption);

        log.info("Termina proceso de creación de un seguimiento para la adopción con id = {}", adoptionId);
        return followUpRepository.save(followUp);
    }

    /** Historial de seguimientos de una adopción propia, del más reciente al más antiguo. */
    @Transactional(readOnly = true)
    public List<FollowUpEntity> getFollowUps(Long adoptionId, Long adopterId)
            throws EntityNotFoundException, ForbiddenOperationException {
        log.info("Inicia proceso de consultar los seguimientos de la adopción con id = {}", adoptionId);
        findOwnedAdoption(adoptionId, adopterId);
        return followUpRepository.findByAdoptionIdOrderByDateDescIdDesc(adoptionId);
    }

    private AdoptionEntity findOwnedAdoption(Long adoptionId, Long adopterId)
            throws EntityNotFoundException, ForbiddenOperationException {
        AdoptionEntity adoption = adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new EntityNotFoundException(ADOPTION_NOT_FOUND));

        if (adopterId == null || adoption.getAdopter() == null || !adopterId.equals(adoption.getAdopter().getId()))
            throw new ForbiddenOperationException(ACCESS_DENIED);

        return adoption;
    }
}
