package co.edu.udistrital.mdp.pets.dto;

import java.util.Date;

import lombok.Data;

@Data
public class CoexistenceTestDTO {

    private Long id;
    private Date startDate;
    private String result;
}
