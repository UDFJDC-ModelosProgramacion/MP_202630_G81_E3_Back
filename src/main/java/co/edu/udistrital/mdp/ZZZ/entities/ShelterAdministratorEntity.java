package co.edu.udistrital.mdp.ZZZ.entities;

import javax.persistence.Entity;

import lombok.Data;

@Data
@Entity
public class ShelterAdministratorEntity extends BaseEntity {

    private String name;
    private String email;
    private String role;
}
