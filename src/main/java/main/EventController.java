package main;

import main.entity.Event;
import main.entity.Form;
import main.repository.EventsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import main.serivce.EventService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventController) {
        this.eventService = eventController;
    }

    @GetMapping("/events")
    public List<Event> findAll(Pageable pageable) {
        return eventService.findAll();
    }

    @PostMapping("/questions")
    public Event createEvent(@Valid @RequestBody Event event) {
        return eventService.createEvent(event);
    }
}
