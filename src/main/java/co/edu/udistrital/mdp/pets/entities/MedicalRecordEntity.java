package co.edu.udistrital.mdp.pets.entities;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

/** Registro médico (atención veterinaria) de una mascota. */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class MedicalRecordEntity extends BaseEntity {

    private LocalDate date;

    private String reason;

    private String diagnosis;

    private String treatment;

    @PodamExclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    private PetEntity pet;

    @PodamExclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "veterinarian_id")
    private VeterinarianEntity veterinarian;
}
