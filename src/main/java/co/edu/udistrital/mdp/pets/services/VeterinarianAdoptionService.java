package co.edu.udistrital.mdp.pets.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.FollowUpEntity;
import co.edu.udistrital.mdp.pets.entities.PetEventEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ForbiddenOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.repositories.FollowUpRepository;
import co.edu.udistrital.mdp.pets.repositories.PetEventRepository;
import co.edu.udistrital.mdp.pets.repositories.VeterinarianRepository;

/**
 * Adopciones asignadas a un veterinario para el seguimiento posadopción. Solo el veterinario
 * asignado puede ver los seguimientos y los eventos que registra el adoptante.
 */
@Service
public class VeterinarianAdoptionService {

    private static final String ACCESS_DENIED = "No tienes autorización para consultar esta adopción";

    private final AdoptionRepository adoptionRepository;
    private final VeterinarianRepository veterinarianRepository;
    private final FollowUpRepository followUpRepository;
    private final PetEventRepository petEventRepository;

    public VeterinarianAdoptionService(AdoptionRepository adoptionRepository,
            VeterinarianRepository veterinarianRepository, FollowUpRepository followUpRepository,
            PetEventRepository petEventRepository) {
        this.adoptionRepository = adoptionRepository;
        this.veterinarianRepository = veterinarianRepository;
        this.followUpRepository = followUpRepository;
        this.petEventRepository = petEventRepository;
    }

    @Transactional(readOnly = true)
    public List<AdoptionEntity> getAssignedAdoptions(Long veterinarianId) throws EntityNotFoundException {
        findVeterinarianOrThrow(veterinarianId);
        return adoptionRepository.findByVeterinarianId(veterinarianId);
    }

    @Transactional(readOnly = true)
    public List<FollowUpEntity> getFollowUps(Long veterinarianId, Long adoptionId)
            throws EntityNotFoundException, ForbiddenOperationException {
        authorize(veterinarianId, adoptionId);
        return followUpRepository.findByAdoptionIdOrderByDateDescIdDesc(adoptionId);
    }

    @Transactional(readOnly = true)
    public List<PetEventEntity> getPetEvents(Long veterinarianId, Long adoptionId)
            throws EntityNotFoundException, ForbiddenOperationException {
        AdoptionEntity adoption = authorize(veterinarianId, adoptionId);
        return petEventRepository.findByPetIdOrderByDateAsc(adoption.getPet().getId());
    }

    private AdoptionEntity authorize(Long veterinarianId, Long adoptionId)
            throws EntityNotFoundException, ForbiddenOperationException {
        findVeterinarianOrThrow(veterinarianId);
        AdoptionEntity adoption = adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new EntityNotFoundException("Adopción no encontrada"));
        if (adoption.getVeterinarian() == null || !veterinarianId.equals(adoption.getVeterinarian().getId()))
            throw new ForbiddenOperationException(ACCESS_DENIED);
        return adoption;
    }

    private void findVeterinarianOrThrow(Long veterinarianId) throws EntityNotFoundException {
        if (!veterinarianRepository.existsById(veterinarianId))
            throw new EntityNotFoundException("Veterinario no encontrado");
    }
}
