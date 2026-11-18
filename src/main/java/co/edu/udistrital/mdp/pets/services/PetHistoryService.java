package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.dto.AdoptionHistoryEntryDTO;
import co.edu.udistrital.mdp.pets.dto.PetEventDTO;
import co.edu.udistrital.mdp.pets.dto.PetHistoryDTO;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.CaseDevolutionEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.PetEventEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.repositories.CaseDevolutionRepository;
import co.edu.udistrital.mdp.pets.repositories.PetEventRepository;
import co.edu.udistrital.mdp.pets.repositories.PetRepository;

/** Historial completo de una mascota: eventos, adopciones y devoluciones. */
@Service
public class PetHistoryService {

    private final PetRepository petRepository;
    private final PetEventRepository petEventRepository;
    private final AdoptionRepository adoptionRepository;
    private final CaseDevolutionRepository caseDevolutionRepository;

    public PetHistoryService(PetRepository petRepository, PetEventRepository petEventRepository,
            AdoptionRepository adoptionRepository, CaseDevolutionRepository caseDevolutionRepository) {
        this.petRepository = petRepository;
        this.petEventRepository = petEventRepository;
        this.adoptionRepository = adoptionRepository;
        this.caseDevolutionRepository = caseDevolutionRepository;
    }

    @Transactional(readOnly = true)
    public PetHistoryDTO getHistory(Long petId) throws EntityNotFoundException {
        PetEntity pet = petRepository.findById(petId).orElseThrow(() -> new EntityNotFoundException("Pet not found"));

        PetHistoryDTO history = new PetHistoryDTO();
        history.setPetId(pet.getId());
        history.setPetName(pet.getName());
        history.setStatus(pet.getStatus());

        List<PetEventEntity> events = petEventRepository.findByPetIdOrderByDateAsc(petId);
        for (PetEventEntity event : events) {
            PetEventDTO dto = new PetEventDTO();
            dto.setId(event.getId());
            dto.setEventType(event.getEventType());
            dto.setDate(event.getDate());
            dto.setDescription(event.getDescription());
            history.getEvents().add(dto);
        }

        List<AdoptionEntity> adoptions = adoptionRepository.findByPetId(petId);
        adoptions.sort((a, b) -> {
            if (a.getAdoptionDate() == null || b.getAdoptionDate() == null)
                return 0;
            return a.getAdoptionDate().compareTo(b.getAdoptionDate());
        });
        for (AdoptionEntity adoption : adoptions) {
            AdoptionHistoryEntryDTO entry = new AdoptionHistoryEntryDTO();
            entry.setAdoptionId(adoption.getId());
            entry.setAdoptionDate(adoption.getAdoptionDate());
            entry.setStatus(adoption.getStatus());
            entry.setAdopterName(adoption.getAdopter() == null ? null : adoption.getAdopter().getName());
            Optional<CaseDevolutionEntity> devolution = caseDevolutionRepository.findByAdoptionId(adoption.getId());
            if (devolution.isPresent()) {
                entry.setReturned(true);
                entry.setReturnDate(devolution.get().getDate());
                entry.setReturnReason(devolution.get().getReason());
            }
            history.getAdoptions().add(entry);
        }
        return history;
    }
}
