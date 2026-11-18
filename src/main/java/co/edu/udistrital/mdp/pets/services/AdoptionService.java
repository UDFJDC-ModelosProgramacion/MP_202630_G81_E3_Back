package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.VeterinarianEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdopterRepository;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;
import co.edu.udistrital.mdp.pets.repositories.VeterinarianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdoptionService {
    private final AdoptionRepository adoptionRepository;
    private final PetRepository petRepository;
    private final AdopterRepository adopterRepository;
    private final VeterinarianRepository veterinarianRepository;

    /** Estados válidos de una adopción: solicitud, aprobación, convivencia, cierre y devolución. */
    public static final Set<String> STATUSES = Set.of("PENDING", "APPROVED", "REJECTED", "ACTIVE",
            "IN_PROGRESS", "COHABITATION", "COMPLETED", "RETURNED", "CANCELLED");

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
        if (!"AVAILABLE".equals(pet.getStatus())) {
            throw new IllegalOperationException("La mascota no está disponible para adopción.");
        }
        adoption.setAdopter(adopter);
        adoption.setPet(pet);
        adoption.setVeterinarian(null);
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
        if (adoption.getStatus() == null || !STATUSES.contains(adoption.getStatus())) {
            throw new IllegalOperationException("El estado de la adopción no es válido.");
        }
        existing.setAdoptionDate(adoption.getAdoptionDate());
        existing.setStatus(adoption.getStatus());
        syncPetStatus(existing);
        return adoptionRepository.save(existing);
    }

    /**
     * Asigna al veterinario responsable del seguimiento posadopción. Debe trabajar en el refugio
     * de la mascota adoptada.
     */
    @Transactional
    public AdoptionEntity assignVeterinarian(Long adoptionId, Long veterinarianId)
            throws EntityNotFoundException, IllegalOperationException {
        AdoptionEntity adoption = this.getAdoption(adoptionId);
        VeterinarianEntity veterinarian = veterinarianRepository.findById(veterinarianId)
                .orElseThrow(() -> new EntityNotFoundException("Veterinario no encontrado"));
        boolean sameShelter = veterinarian.getShelter() != null && adoption.getPet() != null
                && adoption.getPet().getShelter() != null
                && veterinarian.getShelter().getId().equals(adoption.getPet().getShelter().getId());
        if (!sameShelter) {
            throw new IllegalOperationException("El veterinario no trabaja en el refugio de la mascota.");
        }
        adoption.setVeterinarian(veterinarian);
        return adoptionRepository.save(adoption);
    }

    /** Mantiene el estado de la mascota coherente con el de su adopción. */
    private void syncPetStatus(AdoptionEntity adoption) {
        PetEntity pet = adoption.getPet();
        if (pet == null) {
            return;
        }
        switch (adoption.getStatus()) {
            case "APPROVED", "ACTIVE", "IN_PROGRESS", "COHABITATION" -> pet.setStatus("IN_PROCESS");
            case "COMPLETED" -> pet.setStatus("ADOPTED");
            case "REJECTED", "RETURNED", "CANCELLED" -> pet.setStatus("AVAILABLE");
            default -> { }
        }
    }

    @Transactional
    public void deleteAdoption(Long id) throws EntityNotFoundException {
        this.getAdoption(id);
        adoptionRepository.deleteById(id);
    }
}
