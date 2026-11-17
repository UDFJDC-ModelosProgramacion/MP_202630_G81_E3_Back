package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.AdoptionContractEntity;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdoptionContractRepository;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdoptionContractService {
    private final AdoptionContractRepository contractRepository;
    private final AdoptionRepository adoptionRepository;

    /**
     * Crea el contrato. Si trae una adopción (por id) se asocia a ella: el contrato queda
     * visible en el detalle de la adopción.
     */
    @Transactional
    public AdoptionContractEntity createContract(AdoptionContractEntity contract) throws IllegalOperationException {
        if (contract == null || contract.getTermsAndConditions() == null || contract.getTermsAndConditions().trim().isEmpty()) {
            throw new IllegalOperationException("Los términos y condiciones del contrato no pueden estar vacíos.");
        }
        if (contract.getSignedDate() == null) {
            throw new IllegalOperationException("La fecha de firma es obligatoria.");
        }

        AdoptionEntity adoption = null;
        if (contract.getAdoption() != null && contract.getAdoption().getId() != null) {
            adoption = adoptionRepository.findById(contract.getAdoption().getId())
                    .orElseThrow(() -> new IllegalOperationException("La adopción indicada no existe."));
            if (adoption.getContract() != null) {
                throw new IllegalOperationException("La adopción ya tiene un contrato asociado.");
            }
        }

        // La adopción es el lado propietario de la relación; aquí se deja sin asignar.
        contract.setAdoption(null);
        contract.setId(null);
        AdoptionContractEntity saved = contractRepository.save(contract);

        if (adoption != null) {
            adoption.setContract(saved);
            adoptionRepository.save(adoption);
        }
        return saved;
    }

    @Transactional(readOnly = true)
    public List<AdoptionContractEntity> getContracts() {
        return contractRepository.findAll();
    }

    @Transactional(readOnly = true)
    public AdoptionContractEntity getContract(Long id) throws EntityNotFoundException {
        return contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El contrato de adopción con ID " + id + " no fue encontrado."));
    }

    @Transactional
    public AdoptionContractEntity updateContract(Long id, AdoptionContractEntity contract) throws EntityNotFoundException, IllegalOperationException {
        AdoptionContractEntity existing = this.getContract(id);
        if (contract.getTermsAndConditions() == null || contract.getTermsAndConditions().trim().isEmpty()) {
            throw new IllegalOperationException("Los términos y condiciones no pueden estar vacíos.");
        }
        existing.setTermsAndConditions(contract.getTermsAndConditions());
        existing.setSignedDate(contract.getSignedDate());
        existing.setStatus(contract.getStatus());
        return contractRepository.save(existing);
    }

    @Transactional
    public void deleteContract(Long id) throws EntityNotFoundException {
        AdoptionContractEntity existing = this.getContract(id);
        // Se suelta el contrato de su adopción (lado propietario) antes de borrarlo.
        for (AdoptionEntity adoption : adoptionRepository.findAll()) {
            if (adoption.getContract() != null && existing.getId().equals(adoption.getContract().getId())) {
                adoption.setContract(null);
                adoptionRepository.save(adoption);
            }
        }
        contractRepository.deleteById(id);
    }
}
