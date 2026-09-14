package co.edu.udistrital.mdp.pets.services;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ShelterService {
    private final ShelterRepository shelterRepository;

    ShelterService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

    @Transactional
    public ShelterEntity createShelter(ShelterEntity shelterEntity) throws IllegalOperationException {
        log.info("Inicia proceso de creación de un refugio");

        if (shelterEntity.getName() == null || shelterEntity.getName().isBlank())
            throw new IllegalOperationException("EL nombre no es válido");

        if (shelterEntity.getCity() == null || shelterEntity.getCity().isBlank())
            throw new IllegalOperationException("La ciudad no es válida");

        if (shelterRepository.existsByNameAndCity(shelterEntity.getName(), shelterEntity.getCity()))
            throw new IllegalOperationException("Un refugio con este nombre ya existe en esta ciudad");

        log.info("Termina proceso de creación de un refugio");
        return shelterRepository.save(shelterEntity);
    }

    @Transactional
    public List<ShelterEntity> getShelters() {
        log.info("Inicia proceso de consultar todos los refugios");
        return shelterRepository.findAll();
    }

    @Transactional
    public List<ShelterEntity> getSheltersByCity(String city) {
        log.info("Inicia proceso de consultar los refugios de la ciudad {0}", city);
        return shelterRepository.findByCity(city);
    }

    @Transactional
    public ShelterEntity getShelter(Long shelterId) throws EntityNotFoundException {
        log.info("Inicia proceso de consultar el refugio con id = {0}", shelterId);
        return findShelterOrThrow(shelterId);
    }

    @Transactional
    public ShelterEntity updateShelter(Long shelterId, ShelterEntity shelterEntity)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de actualizar el refugio con id = {0}", shelterId);

        if (shelterEntity.getName() == null || shelterEntity.getName().isBlank())
            throw new IllegalOperationException("EL nombre no es válido");

        if (shelterEntity.getCity() == null || shelterEntity.getCity().isBlank())
            throw new IllegalOperationException("La ciudad no es válida");

        ShelterEntity existing = findShelterOrThrow(shelterId);

        boolean changesIdentity = !existing.getName().equals(shelterEntity.getName())
                || !existing.getCity().equals(shelterEntity.getCity());

        if (changesIdentity && shelterRepository.existsByNameAndCity(shelterEntity.getName(), shelterEntity.getCity()))
            throw new IllegalOperationException("Un refugio con este nombre ya existe en esta ciudad");

        shelterEntity.setId(shelterId);
        shelterEntity.setPets(existing.getPets());
        shelterEntity.setVeterinarians(existing.getVeterinarians());
        shelterEntity.setEvents(existing.getEvents());
        shelterEntity.setAdministrators(existing.getAdministrators());

        log.info("Termina proceso de actualizar el refugio con id = {0}", shelterId);
        return shelterRepository.save(shelterEntity);
    }

    @Transactional
    public void deleteShelter(Long shelterId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de borrar el refugio con id = {0}", shelterId);

        ShelterEntity shelter = findShelterOrThrow(shelterId);

        if (shelter.getPets() != null && !shelter.getPets().isEmpty())
            throw new IllegalOperationException("No se puede eliminar el refugio con mascotas asociadas");

        if (shelter.getVeterinarians() != null && !shelter.getVeterinarians().isEmpty())
            throw new IllegalOperationException("No se puede eliminar el refugio con veterinarios asociados");

        if (shelter.getEvents() != null && !shelter.getEvents().isEmpty())
            throw new IllegalOperationException("No se puede eliminar el refugio con eventos asociados");

        if (shelter.getAdministrators() != null && !shelter.getAdministrators().isEmpty()) {
            shelter.getAdministrators().clear();
            shelterRepository.save(shelter);
        }

        shelterRepository.deleteById(shelterId);
        log.info("Termina proceso de borrar el refugio con id = {0}", shelterId);
    }

    private ShelterEntity findShelterOrThrow(Long shelterId) throws EntityNotFoundException {
        Optional<ShelterEntity> shelterEntity = shelterRepository.findById(shelterId);
        if (shelterEntity.isEmpty())
            throw new EntityNotFoundException("Refugio no encontrado");
        return shelterEntity.get();
    }
}