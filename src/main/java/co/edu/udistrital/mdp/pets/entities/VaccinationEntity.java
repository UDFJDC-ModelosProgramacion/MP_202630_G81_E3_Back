package co.edu.udistrital.mdp.pets.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

/** Registro del carné de vacunación digital: vacuna aplicada y fecha de la próxima dosis. */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class VaccinationEntity extends BaseEntity {

    private String name;

    private LocalDate applicationDate;

    /** Fecha prevista de la próxima dosis; es opcional. */
    private LocalDate nextDueDate;

    private String notes;

    @PodamExclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    private PetEntity pet;

    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "veterinarian_id")
    private VeterinarianEntity veterinarian;
}
