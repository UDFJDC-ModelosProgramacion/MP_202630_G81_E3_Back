package co.edu.udistrital.mdp.pets.dto;

import lombok.Data;

@Data
public class VeterinarianDTO {

    private Long id;
    private String name;
    private String speciality;
    private Integer age;
    private String gender;

    /** Solo lectura: false mientras acompaña una adopción activa. */
    private Boolean available;
}
