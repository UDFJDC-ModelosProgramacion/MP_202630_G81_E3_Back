package co.edu.udistrital.mdp.pets.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.MedicalRecordDTO;
import co.edu.udistrital.mdp.pets.dto.PetDTO;
import co.edu.udistrital.mdp.pets.entities.MedicalRecordEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.ForbiddenOperationException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.MedicalRecordService;

/**
 * Mascotas a cargo de un veterinario y su historial médico. Si la mascota no está a su cargo la
 * respuesta es 403 sin datos del historial.
 */
@RestController
@RequestMapping("/veterinarians/{veterinarianId}/pets")
public class VeterinarianPetController {

    private final MedicalRecordService medicalRecordService;
    private final ModelMapper modelMapper;

    public VeterinarianPetController(MedicalRecordService medicalRecordService, ModelMapper modelMapper) {
        this.medicalRecordService = medicalRecordService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PetDTO> findPetsInCharge(@PathVariable Long veterinarianId) throws EntityNotFoundException {
        return modelMapper.map(medicalRecordService.getPetsInCharge(veterinarianId),
                new TypeToken<List<PetDTO>>() {
                }.getType());
    }

    @GetMapping("/{petId}/medical-records")
    @ResponseStatus(HttpStatus.OK)
    public List<MedicalRecordDTO> findMedicalRecords(@PathVariable Long veterinarianId, @PathVariable Long petId)
            throws EntityNotFoundException, ForbiddenOperationException {
        return modelMapper.map(medicalRecordService.getMedicalRecords(veterinarianId, petId),
                new TypeToken<List<MedicalRecordDTO>>() {
                }.getType());
    }

    @PostMapping("/{petId}/medical-records")
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalRecordDTO createMedicalRecord(@PathVariable Long veterinarianId, @PathVariable Long petId,
            @RequestBody MedicalRecordDTO dto)
            throws EntityNotFoundException, ForbiddenOperationException, IllegalOperationException {
        MedicalRecordEntity created = medicalRecordService.createMedicalRecord(veterinarianId, petId,
                modelMapper.map(dto, MedicalRecordEntity.class));
        return modelMapper.map(created, MedicalRecordDTO.class);
    }
}
