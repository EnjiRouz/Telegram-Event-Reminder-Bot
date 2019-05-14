package main.repository;

import main.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ParticipantsRepository extends JpaRepository<Participant, UUID> {
}
