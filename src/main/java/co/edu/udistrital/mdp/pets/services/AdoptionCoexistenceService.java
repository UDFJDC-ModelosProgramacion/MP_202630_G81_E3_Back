package co.edu.udistrital.mdp.pets.services;

import java.text.SimpleDateFormat;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.CoexistenceTestEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.repositories.CoexistenceTestRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Convivencia de prueba de una adopción: el adoptante vive con la mascota durante un periodo
 * acordado para ver si hay adaptación. Si al terminar no sigue adelante se registra la devolución.
 */
@Slf4j
@Service
public class AdoptionCoexistenceService {

    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String SUCCESSFUL = "SUCCESSFUL";
    public static final String UNSUCCESSFUL = "UNSUCCESSFUL";
    private static final Set<String> RESULTS = Set.of(IN_PROGRESS, SUCCESSFUL, UNSUCCESSFUL);

    private final AdoptionRepository adoptionRepository;
    private final CoexistenceTestRepository coexistenceTestRepository;
    private final CoexistenceTestService coexistenceTestService;
    private final AutoNotificationService notificationService;

    public AdoptionCoexistenceService(AdoptionRepository adoptionRepository,
            CoexistenceTestRepository coexistenceTestRepository, CoexistenceTestService coexistenceTestService,
            AutoNotificationService notificationService) {
        this.adoptionRepository = adoptionRepository;
        this.coexistenceTestRepository = coexistenceTestRepository;
        this.coexistenceTestService = coexistenceTestService;
        this.notificationService = notificationService;
    }

    @Transactional
    public CoexistenceTestEntity createCoexistenceTest(Long adoptionId, CoexistenceTestEntity test)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de creación de la convivencia de prueba de la adopción con id = {}", adoptionId);
        AdoptionEntity adoption = findAdoptionOrThrow(adoptionId);
        if (adoption.getCoexistenceTest() != null)
            throw new IllegalOperationException("The adoption already has a coexistence test");
        if (test.getResult() == null || test.getResult().isBlank())
            test.setResult(IN_PROGRESS);
        validateResult(test);
        validatePeriod(test);

        test.setId(null);
        CoexistenceTestEntity saved = coexistenceTestService.createCoexistenceTest(test);
        adoption.setCoexistenceTest(saved);
        adoption.setStatus("COHABITATION");
        if (adoption.getPet() != null)
            adoption.getPet().setStatus("IN_PROCESS");
        adoptionRepository.save(adoption);

        if (adoption.getPet() != null) {
            notificationService.notifyShelter(adoption.getPet().getShelter(),
                    "Convivencia de prueba programada para " + adoption.getPet().getName()
                            + " desde " + formatDate(saved) + ".");
        }
        return saved;
    }

    @Transactional
    public CoexistenceTestEntity getCoexistenceTest(Long adoptionId) throws EntityNotFoundException {
        AdoptionEntity adoption = findAdoptionOrThrow(adoptionId);
        if (adoption.getCoexistenceTest() == null)
            throw new EntityNotFoundException("Coexistence test not found for this adoption");
        return adoption.getCoexistenceTest();
    }

    @Transactional
    public CoexistenceTestEntity updateCoexistenceTest(Long adoptionId, CoexistenceTestEntity test)
            throws EntityNotFoundException, IllegalOperationException {
        AdoptionEntity adoption = findAdoptionOrThrow(adoptionId);
        CoexistenceTestEntity existing = adoption.getCoexistenceTest();
        if (existing == null)
            throw new EntityNotFoundException("Coexistence test not found for this adoption");
        validateResult(test);
        validatePeriod(test);

        CoexistenceTestEntity saved = coexistenceTestService.updateCoexistenceTest(existing.getId(), test);

        // Si la convivencia fue exitosa, la adopción se completa y la mascota queda adoptada.
        if (SUCCESSFUL.equals(saved.getResult())) {
            adoption.setStatus("COMPLETED");
            if (adoption.getPet() != null)
                adoption.getPet().setStatus("ADOPTED");
            adoptionRepository.save(adoption);
        }
        return saved;
    }

    @Transactional
    public void deleteCoexistenceTest(Long adoptionId) throws EntityNotFoundException {
        AdoptionEntity adoption = findAdoptionOrThrow(adoptionId);
        CoexistenceTestEntity existing = adoption.getCoexistenceTest();
        if (existing == null)
            throw new EntityNotFoundException("Coexistence test not found for this adoption");
        adoption.setCoexistenceTest(null);
        adoptionRepository.save(adoption);
        coexistenceTestRepository.deleteById(existing.getId());
    }

    private void validateResult(CoexistenceTestEntity test) throws IllegalOperationException {
        if (test.getResult() == null || !RESULTS.contains(test.getResult()))
            throw new IllegalOperationException("Result must be one of " + RESULTS);
    }

    private void validatePeriod(CoexistenceTestEntity test) throws IllegalOperationException {
        if (test.getStartDate() != null && test.getEndDate() != null && test.getEndDate().before(test.getStartDate()))
            throw new IllegalOperationException("The end date cannot be before the start date");
    }

    private String formatDate(CoexistenceTestEntity test) {
        return test.getStartDate() == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(test.getStartDate());
    }

    private AdoptionEntity findAdoptionOrThrow(Long adoptionId) throws EntityNotFoundException {
        return adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new EntityNotFoundException("Adoption not found"));
    }
}
