package co.edu.udistrital.mdp.pets.dto;

import java.util.Date;

import lombok.Data;

@Data
public class EventCalendarDTO {

    private Long id;
    private String type;
    private Date date;
}
