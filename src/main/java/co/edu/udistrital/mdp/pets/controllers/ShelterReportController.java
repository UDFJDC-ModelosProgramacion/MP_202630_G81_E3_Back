package co.edu.udistrital.mdp.pets.controllers;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.udistrital.mdp.pets.dto.ShelterReportDTO;
import co.edu.udistrital.mdp.pets.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.pets.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.pets.services.ShelterReportService;

/** Reporte de un refugio; `from` y `to` (yyyy-MM-dd) acotan el periodo y son opcionales. */
@RestController
@RequestMapping("/shelters/{shelterId}/reports")
public class ShelterReportController {

    private final ShelterReportService shelterReportService;

    public ShelterReportController(ShelterReportService shelterReportService) {
        this.shelterReportService = shelterReportService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ShelterReportDTO findReport(@PathVariable Long shelterId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to)
            throws EntityNotFoundException, IllegalOperationException {
        return shelterReportService.getReport(shelterId, from, to);
    }
}
