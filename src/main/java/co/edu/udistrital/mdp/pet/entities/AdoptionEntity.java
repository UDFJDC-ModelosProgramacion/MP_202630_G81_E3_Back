package co.edu.udistrital.mdp.pet.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

/**
 * NOTA: Esta es una versión TEMPORAL de AdoptionEntity, solo para que
 * mi parte compile mientras Amaury sube la versión real con todos los
 * atributos (date, status, strategy, observers) y sus demás relaciones.
 * No subire esta version a la rama main
 */
@Data
@Entity
public class AdoptionEntity extends BaseEntity {

    @PodamExclude
    @ManyToOne
    private AdopterEntity adopter;
}