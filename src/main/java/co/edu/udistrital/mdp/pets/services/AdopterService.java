package co.edu.udistrital.mdp.pets.services;

import co.edu.udistrital.mdp.pets.entities.AdopterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdopterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdopterService {

    private final AdopterRepository adopterRepository;

    @Transactional
    public AdopterEntity createAdopter(AdopterEntity adopter) throws IllegalOperationException {
        if (adopter == null || adopter.getName() == null || adopter.getName().trim().isEmpty()) {
            throw new IllegalOperationException("El nombre del adoptante es obligatorio.");
        }
        return adopterRepository.save(adopter);
    }

    @Transactional(readOnly = true)
    public List<AdopterEntity> getAdopters() {
        return adopterRepository.findAll();
    }

    @Transactional(readOnly = true)
    public AdopterEntity getAdopter(Long id) throws EntityNotFoundException {
        return adopterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El adoptante con ID " + id + " no fue encontrado."));
    }

    @Transactional
    public AdopterEntity updateAdopter(Long id, AdopterEntity adopter) throws EntityNotFoundException, IllegalOperationException {
        AdopterEntity existing = getAdopter(id);

        if (adopter.getName() == null || adopter.getName().trim().isEmpty()) {
            throw new IllegalOperationException("El nombre del adoptante no puede estar vacío.");
        }

        existing.setName(adopter.getName());
        existing.setEmail(adopter.getEmail());
        existing.setPhone(adopter.getPhone());
        existing.setAddress(adopter.getAddress());

        return adopterRepository.save(existing);
    }

    @Transactional
    public void deleteAdopter(Long id) throws EntityNotFoundException, IllegalOperationException {
        AdopterEntity existing = getAdopter(id);
        if (existing.getAdoptions() != null && !existing.getAdoptions().isEmpty()) {
            throw new IllegalOperationException("No se puede eliminar un adoptante que tiene adopciones registradas.");
        }
        adopterRepository.deleteById(id);
    }
}
