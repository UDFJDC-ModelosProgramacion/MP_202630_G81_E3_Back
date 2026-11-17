package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.AdoptionRequirementEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRequirementRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdoptionRequirementService {
    private final AdoptionRequirementRepository requirementRepository;
    private final PetRepository petRepository;

    @Transactional
    public AdoptionRequirementEntity createRequirement(AdoptionRequirementEntity requirement) throws IllegalOperationException {
        if (requirement == null || requirement.getDescription() == null || requirement.getDescription().trim().isEmpty()) {
            throw new IllegalOperationException("La descripción del requisito es obligatoria.");
        }
        requirement.setId(null);
        requirement.setPet(resolvePet(requirement.getPet()));
        return requirementRepository.save(requirement);
    }

    @Transactional(readOnly = true)
    public List<AdoptionRequirementEntity> getRequirements() {
        return requirementRepository.findAll();
    }

    /** Requisitos que aplican a una mascota (los que se asociaron a ella). */
    @Transactional(readOnly = true)
    public List<AdoptionRequirementEntity> getRequirementsByPet(Long petId) {
        return requirementRepository.findByPetId(petId);
    }

    @Transactional(readOnly = true)
    public AdoptionRequirementEntity getRequirement(Long id) throws EntityNotFoundException {
        return requirementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El requisito de adopción con ID " + id + " no fue encontrado."));
    }

    @Transactional
    public AdoptionRequirementEntity updateRequirement(Long id, AdoptionRequirementEntity requirement) throws EntityNotFoundException, IllegalOperationException {
        AdoptionRequirementEntity existing = this.getRequirement(id);
        if (requirement.getDescription() == null || requirement.getDescription().trim().isEmpty()) {
            throw new IllegalOperationException("La descripción del requisito no puede estar vacía.");
        }
        existing.setDescription(requirement.getDescription());
        existing.setMandatory(requirement.getMandatory());
        existing.setMinimumSpace(requirement.getMinimumSpace());
        existing.setAllowsOtherPets(requirement.getAllowsOtherPets());
        existing.setRequireYard(requirement.getRequireYard());
        existing.setMinAdopterAge(requirement.getMinAdopterAge());
        if (requirement.getPet() != null) {
            existing.setPet(resolvePet(requirement.getPet()));
        }
        return requirementRepository.save(existing);
    }

    @Transactional
    public void deleteRequirement(Long id) throws EntityNotFoundException {
        this.getRequirement(id);
        requirementRepository.deleteById(id);
    }

    private PetEntity resolvePet(PetEntity pet) throws IllegalOperationException {
        if (pet == null || pet.getId() == null) {
            return null;
        }
        return petRepository.findById(pet.getId())
                .orElseThrow(() -> new IllegalOperationException("La mascota indicada no existe."));
    }
}
