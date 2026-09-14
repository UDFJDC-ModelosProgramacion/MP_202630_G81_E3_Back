package co.edu.udistrital.mdp.pets.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdopterRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdopterService {

    @Autowired
    private AdopterRepository adopterRepository;

    @Transactional
    public AdopterEntity createAdopter(AdopterEntity adopterEntity) throws IllegalOperationException {
        log.info("Inicia proceso de creación de un adoptante");

        if (adopterEntity.getName() == null || adopterEntity.getName().isBlank())
            throw new IllegalOperationException("Name is not valid");

        if (adopterEntity.getPhone() == null || adopterEntity.getPhone().isBlank())
            throw new IllegalOperationException("Phone is not valid");

        log.info("Termina proceso de creación de un adoptante");
        return adopterRepository.save(adopterEntity);
    }

    @Transactional
    public List<AdopterEntity> getAdopters() {
        log.info("Inicia proceso de consultar todos los adoptantes");
        return adopterRepository.findAll();
    }

    @Transactional
    public AdopterEntity getAdopter(Long adopterId) throws EntityNotFoundException {
        log.info("Inicia proceso de consultar el adoptante con id = {0}", adopterId);
        return findAdopterOrThrow(adopterId);
    }

    @Transactional
    public AdopterEntity updateAdopter(Long adopterId, AdopterEntity adopterEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de actualizar el adoptante con id = {0}", adopterId);

        if (adopterEntity.getName() == null || adopterEntity.getName().isBlank())
            throw new IllegalOperationException("Name is not valid");

        if (adopterEntity.getPhone() == null || adopterEntity.getPhone().isBlank())
            throw new IllegalOperationException("Phone is not valid");

        findAdopterOrThrow(adopterId);
        adopterEntity.setId(adopterId);

        log.info("Termina proceso de actualizar el adoptante con id = {0}", adopterId);
        return adopterRepository.save(adopterEntity);
    }

    @Transactional
    public void deleteAdopter(Long adopterId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de borrar el adoptante con id = {0}", adopterId);

        AdopterEntity adopter = findAdopterOrThrow(adopterId);

        if (adopter.getAdoptions() != null && !adopter.getAdoptions().isEmpty())
            throw new IllegalOperationException("Unable to delete adopter with existing adoptions");

        adopterRepository.deleteById(adopterId);
        log.info("Termina proceso de borrar el adoptante con id = {0}", adopterId);
    }

    private AdopterEntity findAdopterOrThrow(Long adopterId) throws EntityNotFoundException {
        Optional<AdopterEntity> adopterEntity = adopterRepository.findById(adopterId);
        if (adopterEntity.isEmpty())
            throw new EntityNotFoundException("Adopter not found");
        return adopterEntity.get();
    }
}