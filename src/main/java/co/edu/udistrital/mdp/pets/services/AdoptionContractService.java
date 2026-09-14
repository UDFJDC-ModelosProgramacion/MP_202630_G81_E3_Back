package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.AdoptionContractEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdoptionContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdoptionContractService {
    private final AdoptionContractRepository contractRepository;
    @Transactional
    public AdoptionContractEntity createContract(AdoptionContractEntity contract) throws IllegalOperationException {
        if (contract == null || contract.getTermsAndConditions() == null || contract.getTermsAndConditions().trim().isEmpty()) {
            throw new IllegalOperationException("Los términos y condiciones del contrato no pueden estar vacíos.");
        }
        if (contract.getSignedDate() == null) {
            throw new IllegalOperationException("La fecha de firma es obligatoria.");
        }
        return contractRepository.save(contract);
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
        AdoptionContractEntity existing = getContract(id);
        if (contract.getTermsAndConditions() == null || contract.getTermsAndConditions().trim().isEmpty()) {
            throw new IllegalOperationException("Los términos y condiciones no pueden estar vacíos.");
        }
        existing.setTermsAndConditions(contract.getTermsAndConditions());
        existing.setSignedDate(contract.getSignedDate());
        return contractRepository.save(existing);
    }
    @Transactional
    public void deleteContract(Long id) throws EntityNotFoundException {
        getContract(id);
        contractRepository.deleteById(id);
    }
}
