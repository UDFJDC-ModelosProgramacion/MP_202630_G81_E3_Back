package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.AdoptionRequirementEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRequirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdoptionRequirementService {
  private final AdoptionRequirementRepository requirementRepository;
    @Transactional
    public AdoptionRequirementEntity createRequirement(AdoptionRequirementEntity requirement) throws IllegalOperationException {
        if (requirement == null || requirement.getDescription() == null || requirement.getDescription().trim().isEmpty()) {
            throw new IllegalOperationException("La descripción del requisito es obligatoria.");
        }
        return requirementRepository.save(requirement);
    }
    @Transactional(readOnly = true)
    public List<AdoptionRequirementEntity> getRequirements() {
        return requirementRepository.findAll();
    }
    @Transactional(readOnly = true)
    public AdoptionRequirementEntity getRequirement(Long id) throws EntityNotFoundException {
        return requirementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El requisito de adopción con ID " + id + " no fue encontrado."));
    }
    @Transactional
    public AdoptionRequirementEntity updateRequirement(Long id, AdoptionRequirementEntity requirement) throws EntityNotFoundException, IllegalOperationException {
        AdoptionRequirementEntity existing = getRequirement(id);

        if (requirement.getDescription() == null || requirement.getDescription().trim().isEmpty()) {
            throw new IllegalOperationException("La descripción del requisito no puede estar vacía.");
        }
        existing.setDescription(requirement.getDescription());
        existing.setMandatory(requirement.getMandatory());
        return requirementRepository.save(existing);
    }
    @Transactional
    public void deleteRequirement(Long id) throws EntityNotFoundException {
        getRequirement(id);
        requirementRepository.deleteById(id);
    }
}
