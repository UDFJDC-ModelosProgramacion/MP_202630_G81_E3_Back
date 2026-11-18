package co.edu.udistrital.mdp.pets.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

/** Mensaje entre un interesado (o adoptante) y un refugio, opcionalmente sobre una mascota. */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "shelter_messages")
public class MessageEntity extends BaseEntity {

    private String content;

    private String senderName;

    /** ADOPTER (quien escribe al refugio) o SHELTER (respuesta del refugio). */
    private String senderRole;

    private LocalDateTime sentAt;

    @PodamExclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "shelter_id", nullable = false)
    private ShelterEntity shelter;

    @PodamExclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "pet_id")
    private PetEntity pet;
}
