package co.edu.udistrital.mdp.pets.services;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.pets.dto.ShelterReportDTO;
import co.edu.udistrital.mdp.pets.entities.AdoptionEntity;
import co.edu.udistrital.mdp.pets.entities.CaseDevolutionEntity;
import co.edu.udistrital.mdp.pets.entities.MedicalRecordEntity;
import co.edu.udistrital.mdp.pets.entities.PetEntity;
import co.edu.udistrital.mdp.pets.entities.PetEventEntity;
import co.edu.udistrital.mdp.pets.entities.ShelterEntity;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.repositories.AdoptionRepository;
import co.edu.udistrital.mdp.pets.repositories.CaseDevolutionRepository;
import co.edu.udistrital.mdp.pets.repositories.MedicalRecordRepository;
import co.edu.udistrital.mdp.pets.repositories.PetEventRepository;
import co.edu.udistrital.mdp.pets.repositories.ShelterRepository;

/** Estadísticas de adopciones, devoluciones y eventos médicos de un refugio, con rango de fechas opcional. */
@Service
public class ShelterReportService {

    private static final String UNKNOWN = "UNKNOWN";

    private final ShelterRepository shelterRepository;
    private final AdoptionRepository adoptionRepository;
    private final CaseDevolutionRepository caseDevolutionRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PetEventRepository petEventRepository;

    public ShelterReportService(ShelterRepository shelterRepository, AdoptionRepository adoptionRepository,
            CaseDevolutionRepository caseDevolutionRepository, MedicalRecordRepository medicalRecordRepository,
            PetEventRepository petEventRepository) {
        this.shelterRepository = shelterRepository;
        this.adoptionRepository = adoptionRepository;
        this.caseDevolutionRepository = caseDevolutionRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.petEventRepository = petEventRepository;
    }

    @Transactional(readOnly = true)
    public ShelterReportDTO getReport(Long shelterId, LocalDate from, LocalDate to)
            throws EntityNotFoundException, IllegalOperationException {
        ShelterEntity shelter = shelterRepository.findById(shelterId)
                .orElseThrow(() -> new EntityNotFoundException("Shelter not found"));
        if (from != null && to != null && to.isBefore(from))
            throw new IllegalOperationException("The end date cannot be before the start date");

        ShelterReportDTO report = new ShelterReportDTO();
        report.setShelterId(shelter.getId());
        report.setShelterName(shelter.getName());
        report.setFrom(from);
        report.setTo(to);

        // El inventario es una foto actual: no depende del rango de fechas.
        List<PetEntity> pets = shelter.getPets();
        report.setTotalPets(pets.size());
        report.setAvailablePets((int) pets.stream().filter(p -> "AVAILABLE".equals(p.getStatus())).count());
        report.setInProcessPets((int) pets.stream().filter(p -> "IN_PROCESS".equals(p.getStatus())).count());
        report.setAdoptedPets((int) pets.stream().filter(p -> "ADOPTED".equals(p.getStatus())).count());

        List<AdoptionEntity> adoptions = adoptionRepository.findByPetShelterId(shelterId).stream()
                .filter(a -> inRange(a.getAdoptionDate(), from, to)).toList();
        report.setTotalAdoptions(adoptions.size());
        count(adoptions, a -> a.getStatus() == null ? UNKNOWN : a.getStatus(), report.getAdoptionsByStatus());

        List<CaseDevolutionEntity> returns = caseDevolutionRepository.findByAdoptionPetShelterId(shelterId).stream()
                .filter(d -> inRange(d.getDate(), from, to)).toList();
        report.setTotalReturns(returns.size());
        count(returns, d -> d.getReason() == null ? UNKNOWN : d.getReason(), report.getReturnsByReason());

        List<MedicalRecordEntity> records = medicalRecordRepository.findByPetShelterId(shelterId).stream()
                .filter(r -> inRange(r.getDate(), from, to)).toList();
        report.setTotalMedicalRecords(records.size());

        List<PetEventEntity> events = petEventRepository.findByPetShelterId(shelterId).stream()
                .filter(e -> inRange(toLocalDate(e.getDate()), from, to)).toList();
        report.setTotalPetEvents(events.size());
        count(events, e -> e.getEventType() == null ? UNKNOWN : e.getEventType(), report.getPetEventsByType());
        return report;
    }

    private static boolean inRange(LocalDate date, LocalDate from, LocalDate to) {
        if (date == null)
            return from == null && to == null;
        return (from == null || !date.isBefore(from)) && (to == null || !date.isAfter(to));
    }

    private static LocalDate toLocalDate(java.util.Date date) {
        if (date == null)
            return null;
        // java.sql.Date no admite toInstant(); se convierte por los milisegundos.
        return java.time.Instant.ofEpochMilli(date.getTime()).atZone(ZoneOffset.UTC).toLocalDate();
    }

    private static <T> void count(List<T> items, Function<T, String> key, Map<String, Integer> target) {
        for (T item : items)
            target.merge(key.apply(item), 1, Integer::sum);
    }
}
