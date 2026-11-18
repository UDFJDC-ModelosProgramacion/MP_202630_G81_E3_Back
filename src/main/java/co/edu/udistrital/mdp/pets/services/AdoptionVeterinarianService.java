package co.edu.udistrital.mdp.pets.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.repositories.VeterinarianRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Veterinario que acompaña una adopción. Reglas para asignarlo:
 * la adopción debe estar activa y sin veterinario, y el veterinario debe trabajar en el
 * refugio de la mascota y estar disponible (no acompañar otra adopción activa).
 */
@Slf4j
@Service
public class AdoptionVeterinarianService {

    private static final String ADOPTION_NOT_FOUND = "La adopción con ID %d no fue encontrada.";
    private static final String VETERINARIAN_NOT_FOUND = "Veterinarian not found";

    private final AdoptionRepository adoptionRepository;
    private final VeterinarianRepository veterinarianRepository;

    public AdoptionVeterinarianService(AdoptionRepository adoptionRepository,
            VeterinarianRepository veterinarianRepository) {
        this.adoptionRepository = adoptionRepository;
        this.veterinarianRepository = veterinarianRepository;
    }

    @Transactional
    public VeterinarianEntity assignVeterinarian(Long adoptionId, Long veterinarianId)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de asignar el veterinario {} a la adopción {}", veterinarianId, adoptionId);
        AdoptionEntity adoption = findAdoption(adoptionId);
        VeterinarianEntity veterinarian = veterinarianRepository.findById(veterinarianId)
                .orElseThrow(() -> new EntityNotFoundException(VETERINARIAN_NOT_FOUND));

        if (!adoption.isActive())
            throw new IllegalOperationException(
                    "Solo se puede asignar un veterinario a una adopción en proceso o en convivencia.");
        if (adoption.getVeterinarian() != null)
            throw new IllegalOperationException("La adopción ya tiene un veterinario asignado.");
        if (!worksInShelterOf(veterinarian, adoption))
            throw new IllegalOperationException(
                    "El veterinario no trabaja en el refugio de la mascota de esta adopción.");
        if (!veterinarian.isAvailable())
            throw new IllegalOperationException(
                    "El veterinario no está disponible: ya acompaña otra adopción en curso.");

        adoption.setVeterinarian(veterinarian);
        veterinarian.getAdoptions().add(adoption);
        adoptionRepository.save(adoption);
        log.info("Termina proceso de asignar el veterinario a la adopción");
        return veterinarian;
    }

    @Transactional(readOnly = true)
    public VeterinarianEntity getVeterinarian(Long adoptionId) throws EntityNotFoundException {
        VeterinarianEntity veterinarian = findAdoption(adoptionId).getVeterinarian();
        if (veterinarian == null)
            throw new EntityNotFoundException("La adopción no tiene veterinario asignado.");
        return veterinarian;
    }

    @Transactional
    public void removeVeterinarian(Long adoptionId) throws EntityNotFoundException {
        log.info("Inicia proceso de quitar el veterinario de la adopción {}", adoptionId);
        AdoptionEntity adoption = findAdoption(adoptionId);
        VeterinarianEntity veterinarian = adoption.getVeterinarian();
        if (veterinarian == null)
            throw new EntityNotFoundException("La adopción no tiene veterinario asignado.");

        veterinarian.getAdoptions().remove(adoption);
        adoption.setVeterinarian(null);
        adoptionRepository.save(adoption);
        log.info("Termina proceso de quitar el veterinario de la adopción");
    }

    private AdoptionEntity findAdoption(Long adoptionId) throws EntityNotFoundException {
        return adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new EntityNotFoundException(ADOPTION_NOT_FOUND.formatted(adoptionId)));
    }

    private boolean worksInShelterOf(VeterinarianEntity veterinarian, AdoptionEntity adoption) {
        return veterinarian.getShelter() != null
                && adoption.getPet() != null
                && adoption.getPet().getShelter() != null
                && veterinarian.getShelter().getId().equals(adoption.getPet().getShelter().getId());
    }
}
