package co.edu.udistrital.mdp.pets.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "adoption_contracts")
public class AdoptionContractEntity extends BaseEntity {

    private LocalDate signatureDate;

    @Lob
    private String termsAndConditions;

    private String status;

    @PodamExclude
    @OneToOne(mappedBy = "contract", fetch = FetchType.LAZY)
    private AdoptionEntity adoption;
}