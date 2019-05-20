package main.repository;

import main.entity.Event;
import main.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParticipantsRepository extends JpaRepository<Participant, UUID> {

    List<Participant> findParticipantsByEvent(Event event);

    Participant findFirstByTgChatId(String chatId);
}
