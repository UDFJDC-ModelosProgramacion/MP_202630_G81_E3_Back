package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdopterRepository;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdoptionService {
    private final AdoptionRepository adoptionRepository;
    private final PetRepository petRepository;
    private final AdopterRepository adopterRepository;

    @Transactional
    public AdoptionEntity createAdoption(AdoptionEntity adoption) throws IllegalOperationException, EntityNotFoundException {
        if (adoption == null) {
            throw new IllegalOperationException("La adopción no puede ser nula.");
        }
        if (adoption.getAdopter() == null || adoption.getAdopter().getId() == null) {
            throw new IllegalOperationException("Una adopción debe estar asociada a un adoptante.");
        }
        if (adoption.getPet() == null || adoption.getPet().getId() == null) {
            throw new IllegalOperationException("Una adopción debe estar asociada a una mascota.");
        }
        if (adoption.getAdoptionDate() == null) {
            throw new IllegalOperationException("La fecha de adopción es obligatoria.");
        }
        // Se usan las entidades gestionadas y no los objetos armados desde el DTO.
        AdopterEntity adopter = adopterRepository.findById(adoption.getAdopter().getId())
                .orElseThrow(() -> new EntityNotFoundException("Adoptante no encontrado"));
        PetEntity pet = petRepository.findById(adoption.getPet().getId())
                .orElseThrow(() -> new EntityNotFoundException("Mascota no encontrada"));
        adoption.setAdopter(adopter);
        adoption.setPet(pet);
        adoption.setContract(null);
        if (adoption.getStatus() == null || adoption.getStatus().isBlank()) {
            adoption.setStatus("PENDING");
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
        AdoptionEntity existing = this.getAdoption(id);
        if (adoption.getAdoptionDate() == null) {
            throw new IllegalOperationException("La fecha de adopción no puede ser nula.");
        }
        existing.setAdoptionDate(adoption.getAdoptionDate());
        existing.setStatus(adoption.getStatus());
        return adoptionRepository.save(existing);
    }

    @Transactional
    public void deleteAdoption(Long id) throws EntityNotFoundException {
        this.getAdoption(id);
        adoptionRepository.deleteById(id);
    }
}
