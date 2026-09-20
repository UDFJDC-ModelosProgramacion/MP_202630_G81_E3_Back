```java
package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.CoexistenceTestEntity;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.repositories.CoexistenceTestRepository;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;

@Slf4j
@Service
public class CoexistenceTestService {

    @Autowired
    private CoexistenceTestRepository coexistenceTestRepository;

    @Transactional
    public CoexistenceTestEntity createCoexistenceTest(
            CoexistenceTestEntity coexistenceTest)
            throws IllegalOperationException {

        log.info("Inicia proceso de creación de la prueba de convivencia");

        if (coexistenceTest.getStartDate() == null) {
            throw new IllegalOperationException(
                    "Start date is not valid");
        }

        if (coexistenceTest.getResult() == null ||
            coexistenceTest.getResult().isBlank()) {
            throw new IllegalOperationException(
                    "Result is not valid");
        }

        log.info(
                "Termina proceso de creación de la prueba de convivencia");

        return coexistenceTestRepository.save(coexistenceTest);
    }

    @Transactional
    public List<CoexistenceTestEntity> getCoexistenceTests() {

        log.info(
                "Inicia proceso de consultar todas las pruebas de convivencia");

        return coexistenceTestRepository.findAll();
    }

    @Transactional
    public CoexistenceTestEntity getCoexistenceTest(
            Long coexistenceTestId)
            throws EntityNotFoundException {

        log.info(
                "Inicia proceso de consultar la prueba de convivencia con id = {}",
                coexistenceTestId);

        Optional<CoexistenceTestEntity> coexistenceTest =
                coexistenceTestRepository.findById(coexistenceTestId);

        if (coexistenceTest.isEmpty()) {
            throw new EntityNotFoundException(
                    "Coexistence test not found");
        }

        return coexistenceTest.get();
    }

    @Transactional
    public CoexistenceTestEntity updateCoexistenceTest(
            Long coexistenceTestId,
            CoexistenceTestEntity coexistenceTest)
            throws EntityNotFoundException, IllegalOperationException {

        log.info(
                "Inicia proceso de actualizar la prueba de convivencia con id = {}",
                coexistenceTestId);

        Optional<CoexistenceTestEntity> existing =
                coexistenceTestRepository.findById(coexistenceTestId);

        if (existing.isEmpty()) {
            throw new EntityNotFoundException(
                    "Coexistence test not found");
        }

        if (coexistenceTest.getStartDate() == null) {
            throw new IllegalOperationException(
                    "Start date is not valid");
        }

        if (coexistenceTest.getResult() == null ||
            coexistenceTest.getResult().isBlank()) {
            throw new IllegalOperationException(
                    "Result is not valid");
        }

        coexistenceTest.setId(coexistenceTestId);

        log.info(
                "Termina proceso de actualizar la prueba de convivencia con id = {}",
                coexistenceTestId);

        return coexistenceTestRepository.save(coexistenceTest);
    }

    @Transactional
    public void deleteCoexistenceTest(Long coexistenceTestId)
            throws EntityNotFoundException, IllegalOperationException {

        log.info(
                "Inicia proceso de borrar la prueba de convivencia con id = {}",
                coexistenceTestId);

        Optional<CoexistenceTestEntity> coexistenceTest =
                coexistenceTestRepository.findById(coexistenceTestId);

        if (coexistenceTest.isEmpty()) {
            throw new EntityNotFoundException(
                    "Coexistence test not found");
        }

        if (coexistenceTest.get().getAdoption() != null) {
            throw new IllegalOperationException(
                    "Unable to delete coexistence test because it is associated with an adoption");
        }

        coexistenceTestRepository.deleteById(coexistenceTestId);

        log.info(
                "Termina proceso de borrar la prueba de convivencia con id = {}",
                coexistenceTestId);
    }
}
```
