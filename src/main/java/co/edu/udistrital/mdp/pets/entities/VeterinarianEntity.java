package co.edu.udistrital.mdp.pets.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class VeterinarianEntity extends BaseEntity {

    private String name;
    private String speciality;
    private String email;
    private String phone;

    /** Disponibilidad horaria en texto libre (por ejemplo, "Lunes a viernes 8:00-12:00"). */
    private String availability;

    /** Edad en años (18 a 100). */
    private Integer age;

    /** FEMALE, MALE u OTHER. */
    private String gender;

    /** Adopciones que acompaña (actuales o pasadas). Lado inverso de AdoptionEntity.veterinarian. */
    @PodamExclude
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "veterinarian")
    private List<AdoptionEntity> adoptions = new ArrayList<>();

    /**
     * Disponible mientras no acompañe una adopción activa (en proceso o en convivencia).
     * Se calcula: al terminar la adopción el veterinario vuelve a estar disponible solo.
     */
    @JsonIgnore
    public boolean isAvailable() {
        return adoptions.stream().noneMatch(AdoptionEntity::isActive);
    }

    @PodamExclude
    @JsonIgnore
    @ManyToOne
    private ShelterEntity shelter;
}


