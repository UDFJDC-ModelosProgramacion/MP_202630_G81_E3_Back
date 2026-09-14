package co.edu.udistrital.mdp.pets.services;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.udistrital.mdp.pets.entities.ShelterAdministratorEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.ShelterAdministratorRepository;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ShelterAdministratorService {

    @Autowired
    private ShelterAdministratorRepository administratorRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    @Transactional
    public ShelterAdministratorEntity createAdministrator(ShelterAdministratorEntity administratorEntity)
            throws IllegalOperationException {
        log.info("Inicia proceso de creación de un administrador de refugio");

        if (administratorEntity.getName() == null || administratorEntity.getName().isBlank())
            throw new IllegalOperationException("El nombre no es válido");

        if (administratorEntity.getEmail() == null || administratorEntity.getEmail().isBlank())
            throw new IllegalOperationException("El email no es válido");

        if (administratorEntity.getRole() == null || administratorEntity.getRole().isBlank())
            throw new IllegalOperationException("El rol no es válido");

        if (administratorRepository.existsByEmail(administratorEntity.getEmail()))
            throw new IllegalOperationException("El email ya está registrado para otro administrador");

        // La asociación N:M con Shelter se maneja aparte con
        // addShelter/removeShelter, no en la creación.
        administratorEntity.setShelters(new ArrayList<>());

        log.info("Termina proceso de creación de un administrador de refugio");
        return administratorRepository.save(administratorEntity);
    }

    @Transactional
    public List<ShelterAdministratorEntity> getAdministrators() {
        log.info("Inicia proceso de consultar todos los administradores");
        return administratorRepository.findAll();
    }

    @Transactional
    public ShelterAdministratorEntity getAdministrator(Long administratorId) throws EntityNotFoundException {
        log.info("Inicia proceso de consultar el administrador con id = {0}", administratorId);
        return findAdministratorOrThrow(administratorId);
    }

    @Transactional
    public ShelterAdministratorEntity updateAdministrator(Long administratorId, ShelterAdministratorEntity administratorEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de actualizar el administrador con id = {0}", administratorId);

        if (administratorEntity.getName() == null || administratorEntity.getName().isBlank())
            throw new IllegalOperationException("El nombre no es válido");

        if (administratorEntity.getEmail() == null || administratorEntity.getEmail().isBlank())
            throw new IllegalOperationException("El email no es válido");

        if (administratorEntity.getRole() == null || administratorEntity.getRole().isBlank())
            throw new IllegalOperationException("El rol no es válido");

        ShelterAdministratorEntity existing = findAdministratorOrThrow(administratorId);

        if (administratorRepository.existsByEmailAndIdNot(administratorEntity.getEmail(), administratorId))
            throw new IllegalOperationException("El email ya está registrado para otro administrador");

        administratorEntity.setId(administratorId);
        administratorEntity.setShelters(existing.getShelters());

        log.info("Termina proceso de actualizar el administrador con id = {0}", administratorId);
        return administratorRepository.save(administratorEntity);
    }

    @Transactional
    public void deleteAdministrator(Long administratorId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de borrar el administrador con id = {0}", administratorId);

        ShelterAdministratorEntity administrator = findAdministratorOrThrow(administratorId);

        if (administrator.getShelters() != null && !administrator.getShelters().isEmpty())
            throw new IllegalOperationException("No se puede eliminar un administrador asociado con refugios");

        administratorRepository.deleteById(administratorId);
        log.info("Termina proceso de borrar el administrador con id = {0}", administratorId);
    }

    @Transactional
    public ShelterEntity addShelter(Long administratorId, Long shelterId)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de asociar el shelter con id = {0} al administrador con id = {1}", shelterId, administratorId);

        ShelterAdministratorEntity administrator = findAdministratorOrThrow(administratorId);
        ShelterEntity shelter = findShelterOrThrow(shelterId);

        if (shelter.getAdministrators().stream().anyMatch(a -> a.getId().equals(administratorId)))
            throw new IllegalOperationException("El administrador ya está asociado con este refugio");

        shelter.getAdministrators().add(administrator);

        log.info("Termina proceso de asociar el shelter con id = {0} al administrador con id = {1}", shelterId, administratorId);
        return shelterRepository.save(shelter);
    }

    @Transactional
    public List<ShelterEntity> getShelters(Long administratorId) throws EntityNotFoundException {
        log.info("Inicia proceso de consultar los shelters del administrador con id = {0}", administratorId);
        ShelterAdministratorEntity administrator = findAdministratorOrThrow(administratorId);
        return administrator.getShelters();
    }

    @Transactional
    public ShelterEntity getShelter(Long administratorId, Long shelterId)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de consultar el shelter con id = {0} del administrador con id = {1}", shelterId, administratorId);
        findAdministratorOrThrow(administratorId);
        ShelterEntity shelter = findShelterOrThrow(shelterId);

        if (shelter.getAdministrators().stream().noneMatch(a -> a.getId().equals(administratorId)))
            throw new IllegalOperationException("El refugio no está asociado con el administrador dado");

        return shelter;
    }

    @Transactional
    public void removeShelter(Long administratorId, Long shelterId)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de desasociar el shelter con id = {0} del administrador con id = {1}", shelterId, administratorId);

        findAdministratorOrThrow(administratorId);
        ShelterEntity shelter = findShelterOrThrow(shelterId);

        boolean removed = shelter.getAdministrators().removeIf(a -> a.getId().equals(administratorId));
        if (!removed)
            throw new IllegalOperationException("El administrador no está asociado con este refugio");

        shelterRepository.save(shelter);
        log.info("Termina proceso de desasociar el shelter con id = {0} del administrador con id = {1}", shelterId, administratorId);
    }

    private ShelterAdministratorEntity findAdministratorOrThrow(Long administratorId) throws EntityNotFoundException {
        Optional<ShelterAdministratorEntity> administratorEntity = administratorRepository.findById(administratorId);
        if (administratorEntity.isEmpty())
            throw new EntityNotFoundException("Administrador de refugio no encontrado");
        return administratorEntity.get();
    }

    private ShelterEntity findShelterOrThrow(Long shelterId) throws EntityNotFoundException {
        Optional<ShelterEntity> shelterEntity = shelterRepository.findById(shelterId);
        if (shelterEntity.isEmpty())
            throw new EntityNotFoundException("Refugio no encontrado");
        return shelterEntity.get();
    }
}