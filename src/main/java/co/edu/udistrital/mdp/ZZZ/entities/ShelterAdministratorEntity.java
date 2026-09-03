package co.edu.udistrital.mdp.ZZZ.entities;

import jakarta.persistence.Entity;


import lombok.Data;

@Data
@Entity
public class ShelterAdministratorEntity extends co.edu.udistrital.mdp.ZZZ.entities.BaseEntity{

    private String name;
    private String email;
    private String role;
}
