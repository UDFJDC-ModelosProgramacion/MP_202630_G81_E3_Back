package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jbfoster.podam.common.PodamExclude;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "adoptions")
public class AdoptionEntity extends BaseEntity {

    @Temporal(TemporalType.DATE)
    private LocalDate date;

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

    // Atributos de patrones comportamentales marcados como @Transient
    @Transient
    private Object strategy;
}
