package main.serivce;

import main.entity.Event;
import main.entity.Participant;
import main.repository.ParticipantsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ParticipantsService {
    private final ParticipantsRepository participantsRepository;

    public ParticipantsService(ParticipantsRepository participantsRepository) {
        this.participantsRepository = participantsRepository;
    }

    public Participant createParticipant(Participant participant) {
        if (participant.getUuid() == null) {
            participant.setUuid(UUID.randomUUID());
        }
        return participantsRepository.save(participant);
    }

    public Participant findParticipantById(UUID id) {
        var participant = participantsRepository.findById(id);
        if (participant.isEmpty()) {
            return null;
        }
        else {
            return participant.get();
        }
    }

    public List<Participant> findParticipantsOfEvent(Event event) {
        return participantsRepository.findParticipantsByEvent(event);
    }

    public Participant findParticipantByChatId(String chatId) {
        return participantsRepository.findFirstByTgChatId(chatId);
    }
}
