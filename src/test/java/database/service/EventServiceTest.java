package database.service;

import main.Application;
import main.entity.Event;
import main.entity.Form;
import main.repository.EventsRepository;
import main.serivce.EventService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@TestPropertySource(locations = "/test.properties")
public class EventServiceTest {

    @Resource
    private EventService eventService;

    @Resource
    private EventsRepository eventsRepository;

    private ArrayList<Event> testEvents;

    @Before
    public void setUp() {
        var date = LocalDateTime.now();
        eventsRepository.deleteAll();

        testEvents= new ArrayList<>(List.of(
                new Event(new Form(), "test event 1", "this is event for test", date),
                new Event(new Form(), "test event 2", "this is event for test", date),
                new Event(new Form(), "test event 3", "this is event for test", date)
        ));
    }

    @Test
    public void testCreateEvents() {
        var eventInEkaterinburg = testEvents.get(2);
        eventInEkaterinburg.setVenue("Ekaterinburg");
        testEvents.set(2, eventInEkaterinburg);

        for (var event : testEvents) {
            eventService.createEvent(event);
        }

        Assert.assertEquals(3, eventsRepository.findAll().size());
        Assert.assertEquals("test event 1", eventsRepository.findAllByName("test event 1").get(0).getName());
        Assert.assertEquals("Ekaterinburg", eventsRepository.findAllByName("test event 3").get(0).getVenue());
    }

    @Test
    public void testFindAllEvents() {

        for (var event : testEvents) {
            eventService.createEvent(event);
        }

        Assert.assertEquals(3, eventService.findAll().size());
        Assert.assertEquals(testEvents.get(0).getName(), eventService.findAll().get(0).getName());
        Assert.assertEquals(testEvents.get(1).getName(), eventService.findAll().get(1).getName());
        Assert.assertEquals(testEvents.get(2).getName(), eventService.findAll().get(2).getName());
    }

    @Test
    public void testFindAllByNameTest() {
        var eventWithDuplicateName = testEvents.get(2);
        eventWithDuplicateName.setName(testEvents.get(1).getName());
        testEvents.set(2, eventWithDuplicateName);

        for (var event : testEvents) {
            eventService.createEvent(event);
        }

        Assert.assertEquals(1, eventService.findAllByName("test event 1").size());
        Assert.assertEquals(testEvents.get(0).getName(), eventService.findAllByName("test event 1").get(0).getName());
        Assert.assertEquals(2, eventService.findAllByName("test event 2").size());
        Assert.assertEquals(testEvents.get(1).getName(), eventService.findAllByName("test event 2").get(1).getName());
    }
}
