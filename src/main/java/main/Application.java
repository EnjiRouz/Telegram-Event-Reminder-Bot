package main;

import main.entity.Event;
import main.entity.Form;
import main.serivce.EventService;
import main.serivce.ParticipantsService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.ApiContextInitializer;

import java.time.LocalDateTime;
import java.time.ZoneId;

@SpringBootApplication
public class Application {
    private final EventService eventService;
    private final ParticipantsService participantsService;

    public Application(EventService eventService, ParticipantsService participantsService) {
        this.eventService = eventService;
        this.participantsService = participantsService;
    }

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(Application.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void testJpaMethods() {
        Form form = new Form();
        form.setFields("shortString, longString, email");

        Event event = new Event();
        event.setName("Deadline");
        event.setOpen(true);
        event.setDescription("OMG! Today is the day when you should show me to everyone at 17:40 ^.^");
        event.setForm(form);
        event.setDateTime(LocalDateTime.now().atZone(ZoneId.of("Asia/Karachi")).toLocalDateTime().plusHours(2));

        eventService.createEvent(event);
    }

}