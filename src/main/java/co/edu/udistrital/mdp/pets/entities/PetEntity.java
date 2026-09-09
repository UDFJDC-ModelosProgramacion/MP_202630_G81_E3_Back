package co.edu.udistrital.mdp.pets.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class PetEntity extends BaseEntity {

    private String name;

    private String status;

    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "shelter_id", nullable = false)
    private ShelterEntity shelter;

    @PodamExclude
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VaccinationRegistrationEntity> vaccinationRegistrations = new ArrayList<>();

    @PodamExclude
    @OneToOne(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private BreedEntity breed;
}
