package co.edu.udistrital.mdp.pets.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.udistrital.mdp.pets.entities.EventCalendarEntity;

public interface EventCalendarRepository extends JpaRepository<EventCalendarEntity, Long> {
}