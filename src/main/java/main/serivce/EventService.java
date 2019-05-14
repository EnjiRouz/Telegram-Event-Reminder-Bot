package main.serivce;

import main.entity.Event;
import org.springframework.stereotype.Service;
import main.repository.EventsRepository;

import java.util.List;

@Service
public class EventService {

    private final EventsRepository eventsRepository;

    public EventService(EventsRepository eventsRepository) {
        this.eventsRepository = eventsRepository;
    }

    public Event createEvent(Event event) {
        return eventsRepository.save(event);
    }

    public List<Event> findAll() {
        return eventsRepository.findAll();
    }

    public List<Event> findAllByName(String name) {
        return eventsRepository.findAllByName(name);
    }
}
