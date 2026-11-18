package co.edu.udistrital.mdp.pets.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.udistrital.mdp.pets.entities.MessageEntity;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {

    List<MessageEntity> findByShelterIdOrderBySentAtAscIdAsc(Long shelterId);

    List<MessageEntity> findByShelterIdAndPetIdOrderBySentAtAscIdAsc(Long shelterId, Long petId);
}
