package co.edu.udistrital.mdp.pets.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.udistrital.mdp.pets.entities.PetEntity;

public interface PetRepository extends JpaRepository<PetEntity, Long> {

    List<PetEntity> findByStatus(String status);

    List<PetEntity> findBySizeAndCompatibleWithChildrenAndCompatibleWithOtherPets(
            String size, Boolean compatibleWithChildren, Boolean compatibleWithOtherPets);

    List<PetEntity> findByActivityLevel(String activityLevel);

    List<PetEntity> findBySpaceRequirement(String spaceRequirement);

    List<PetEntity> findByBreedName(String breedName);
}