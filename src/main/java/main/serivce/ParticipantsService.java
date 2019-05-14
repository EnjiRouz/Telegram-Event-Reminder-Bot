package main.serivce;

import main.entity.Participant;
import org.springframework.stereotype.Service;
import main.repository.ParticipantsRepository;

@Service
public class ParticipantsService {
    private final ParticipantsRepository participantsRepository;

    public ParticipantsService(ParticipantsRepository participantsRepository) {
        this.participantsRepository = participantsRepository;
    }

    public void  createParticipant(Participant participant) {
        participantsRepository.save(participant);
    }
}
