package co.edu.udistrital.mdp.pets.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jbfoster.podam.common.PodamExclude;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "adoption_contracts")
public class AdoptionContractEntity extends BaseEntity {

    @Temporal(TemporalType.DATE)
    private LocalDate signatureDate;

    @Lob
    private String termsAndConditions;

    private String status;

    @PodamExclude
    @OneToOne(mappedBy = "contract", fetch = FetchType.LAZY)
    private AdoptionEntity adoption;
}
