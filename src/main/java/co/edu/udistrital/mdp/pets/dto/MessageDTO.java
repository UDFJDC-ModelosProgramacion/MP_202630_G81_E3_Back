package co.edu.udistrital.mdp.pets.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MessageDTO {

    private Long id;
    private String content;
    private String senderName;
    private String senderRole;
    private LocalDateTime sentAt;
    private Long petId;
}
