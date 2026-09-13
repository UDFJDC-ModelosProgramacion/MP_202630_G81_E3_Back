package co.edu.udistrital.mdp.pets.entities;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class PetEventEntity extends BaseEntity {

    /**
     * Tipo de evento. Valores esperados: "VACCINATION", "ACCIDENT",
     * "ILLNESS", "SURGERY", "ARRIVAL", "OTHER".
     */
    private String eventType;

    @Temporal(TemporalType.DATE)
    private Date date;

    /**
     * Detalle del evento: nombre de la vacuna, qué cirugía fue, cómo ocurrió
     * el accidente, cómo llegó la mascota al refugio, etc.
     */
    private String description;

    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "pet_id")
    private PetEntity pet;
}