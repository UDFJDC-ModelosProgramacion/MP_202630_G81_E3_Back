package co.edu.udistrital.mdp.pets.dto;

import java.util.Date;

import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private String content;
    private Date date;
}