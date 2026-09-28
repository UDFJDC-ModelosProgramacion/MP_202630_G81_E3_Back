package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;
import java.util.Date;

@Data
public class AdoptionContractDTO {
    private Long id;
    private String termsAndConditions;
    private Date signedDate;
    private Boolean isSigned;

    // Asociación de cardinalidad 1
    private AdoptionDTO adoption;
}
