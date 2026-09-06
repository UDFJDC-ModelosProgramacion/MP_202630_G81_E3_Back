package co.edu.udistrital.mdp.pet.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;

@Data
@Entity
public class AdopterEntity extends BaseEntity {

    private String name;
    private String phone;

    @PodamExclude
    @OneToMany(mappedBy = "adopter", cascade = CascadeType.PERSIST)
    private List<AdoptionEntity> adoptions = new ArrayList<>();
}
