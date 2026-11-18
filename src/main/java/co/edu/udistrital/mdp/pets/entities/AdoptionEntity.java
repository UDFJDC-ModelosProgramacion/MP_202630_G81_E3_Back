package co.edu.udistrital.mdp.pets.entities;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "adoptions")
public class AdoptionEntity extends BaseEntity {

    /** Estados en los que el proceso sigue vivo (la mascota y el veterinario están "ocupados"). */
    public static final List<String> ACTIVE_STATUSES = List.of("IN_PROGRESS", "COHABITATION");

    private LocalDate adoptionDate;

    private String status;

    @PodamExclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private PetEntity pet;

    @PodamExclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adopter_id", nullable = false)
    private AdopterEntity adopter;

    @PodamExclude
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", referencedColumnName = "id")
    private AdoptionContractEntity contract;

    @PodamExclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coexistence_test_id", referencedColumnName = "id")
    private CoexistenceTestEntity coexistenceTest;

    /** Veterinario que acompaña la adopción (opcional). */
    @PodamExclude
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id")
    private VeterinarianEntity veterinarian;

    @Transient
    private Object strategy;

    @JsonIgnore
    public boolean isActive() {
        return status != null && ACTIVE_STATUSES.contains(status);
    }
}