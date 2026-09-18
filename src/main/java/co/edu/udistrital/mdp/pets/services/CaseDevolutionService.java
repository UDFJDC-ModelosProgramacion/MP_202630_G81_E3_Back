package co.edu.udistrital.mdp.pets.services;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.CaseDevolutionEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.repositories.CaseDevolutionRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CaseDevolutionService {

    private CaseDevolutionRepository caseDevolutionRepository;
    private AdoptionRepository adoptionRepository;

    public CaseDevolutionService(CaseDevolutionRepository caseDevolutionRepository,
             AdoptionRepository adoptionRepository) {
        this.caseDevolutionRepository = caseDevolutionRepository;
        this.adoptionRepository = adoptionRepository;
    }

    @Transactional
    public CaseDevolutionEntity createCaseDevolution(Long adoptionId, CaseDevolutionEntity caseDevolutionEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de creación de la devolución para la adopción con id = {0}", adoptionId);

        if (caseDevolutionEntity.getReason() == null || caseDevolutionEntity.getReason().isBlank())
            throw new IllegalOperationException("La razón de la devolución no es válida");

        if (caseDevolutionEntity.getDate() == null)
            throw new IllegalOperationException("La fecha de la devolución no es válida");

        AdoptionEntity adoption = findAdoptionOrThrow(adoptionId);

        if (caseDevolutionRepository.findByAdoptionId(adoptionId).isPresent())
            throw new IllegalOperationException("La adopción ya tiene una devolución registrada");

        if (adoption.getAdoptionDate() != null && caseDevolutionEntity.getDate().isBefore(adoption.getAdoptionDate()))
            throw new IllegalOperationException("La fecha de la devolución no puede ser anterior a la fecha de la adopción");

        caseDevolutionEntity.setAdoption(adoption);

        log.info("Termina proceso de creación de la devolución para la adopción con id = {0}", adoptionId);
        return caseDevolutionRepository.save(caseDevolutionEntity);
    }

    @Transactional
    public CaseDevolutionEntity getCaseDevolution(Long adoptionId) throws EntityNotFoundException {
        log.info("Inicia proceso de consultar la devolución de la adopción con id = {0}", adoptionId);
        findAdoptionOrThrow(adoptionId);
        return findCaseDevolutionOrThrow(adoptionId);
    }

    @Transactional
    public CaseDevolutionEntity updateCaseDevolution(Long adoptionId, CaseDevolutionEntity caseDevolutionEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de actualizar la devolución de la adopción con id = {0}", adoptionId);

        if (caseDevolutionEntity.getReason() == null || caseDevolutionEntity.getReason().isBlank())
            throw new IllegalOperationException("La razón de la devolución no es válida");

        if (caseDevolutionEntity.getDate() == null)
            throw new IllegalOperationException("La fecha de la devolución no es válida");

        AdoptionEntity adoption = findAdoptionOrThrow(adoptionId);
        CaseDevolutionEntity existing = findCaseDevolutionOrThrow(adoptionId);

        if (caseDevolutionEntity.getAdoption() != null && !adoptionId.equals(caseDevolutionEntity.getAdoption().getId()))
            throw new IllegalOperationException("Una devolución no puede ser reasignada a una adopción diferente");

        caseDevolutionEntity.setId(existing.getId());
        caseDevolutionEntity.setAdoption(adoption);

        log.info("Termina proceso de actualizar la devolución de la adopción con id = {0}", adoptionId);
        return caseDevolutionRepository.save(caseDevolutionEntity);
    }

    @Transactional
    public void deleteCaseDevolution(Long adoptionId) throws EntityNotFoundException {
        log.info("Inicia proceso de borrar la devolución de la adopción con id = {0}", adoptionId);

        findAdoptionOrThrow(adoptionId);
        CaseDevolutionEntity existing = findCaseDevolutionOrThrow(adoptionId);

        caseDevolutionRepository.deleteById(existing.getId());
        log.info("Termina proceso de borrar la devolución de la adopción con id = {0}", adoptionId);
    }

    private AdoptionEntity findAdoptionOrThrow(Long adoptionId) throws EntityNotFoundException {
        Optional<AdoptionEntity> adoptionEntity = adoptionRepository.findById(adoptionId);
        if (adoptionEntity.isEmpty())
            throw new EntityNotFoundException("Adopción no encontrada");
        return adoptionEntity.get();
    }

    private CaseDevolutionEntity findCaseDevolutionOrThrow(Long adoptionId) throws EntityNotFoundException {
        Optional<CaseDevolutionEntity> caseDevolutionEntity = caseDevolutionRepository.findByAdoptionId(adoptionId);
        if (caseDevolutionEntity.isEmpty())
            throw new EntityNotFoundException("Devolución no encontrada para esta adopción");
        return caseDevolutionEntity.get();
    }
}