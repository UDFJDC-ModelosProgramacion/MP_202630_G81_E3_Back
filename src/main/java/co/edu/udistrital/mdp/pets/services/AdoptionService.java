package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdoptionService {
    private final AdoptionRepository adoptionRepository;

    @Lazy
    private AdoptionService self;

    @Transactional
    public AdoptionEntity createAdoption(AdoptionEntity adoption) throws IllegalOperationException {
        if (adoption == null) {
            throw new IllegalOperationException("La adopción no puede ser nula.");
        }
        if (adoption.getAdopter() == null) {
            throw new IllegalOperationException("Una adopción debe estar asociada a un adoptante.");
        }
        if (adoption.getPet() == null) {
            throw new IllegalOperationException("Una adopción debe estar asociada a una mascota.");
        }
        if (adoption.getAdoptionDate() == null) {
            throw new IllegalOperationException("La fecha de adopción es obligatoria.");
        }
        return adoptionRepository.save(adoption);
    }
    @Transactional(readOnly = true)
    public List<AdoptionEntity> getAdoptions() {
        return adoptionRepository.findAll();
    }
    @Transactional(readOnly = true)
    public AdoptionEntity getAdoption(Long id) throws EntityNotFoundException {
        return adoptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La adopción con ID " + id + " no fue encontrada."));
    }
    @Transactional
    public AdoptionEntity updateAdoption(Long id, AdoptionEntity adoption) throws EntityNotFoundException, IllegalOperationException {
        AdoptionEntity existing = self.getAdoption(id);
        if (adoption.getAdoptionDate() == null) {
            throw new IllegalOperationException("La fecha de adopción no puede ser nula.");
        }
        existing.setAdoptionDate(adoption.getAdoptionDate());
        existing.setStatus(adoption.getStatus());
        return adoptionRepository.save(existing);
    }
    @Transactional
    public void deleteAdoption(Long id) throws EntityNotFoundException {
        self.getAdoption(id);
        adoptionRepository.deleteById(id);
    }
}
