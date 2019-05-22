package database.service;


import main.Application;
import main.entity.Event;
import main.entity.Form;
import main.entity.Participant;
import main.repository.EventsRepository;
import main.repository.ParticipantsRepository;
import main.serivce.ParticipantsService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@TestPropertySource(locations = "/test.properties")
public class ParticipantsServiceTest {

    @Resource
    private ParticipantsService participantsService;

    @Resource
    private ParticipantsRepository participantsRepository;

    @Resource
    private EventsRepository eventsRepository;

    private ArrayList<Participant> testParticipants;

    @Before
    public void setUp() {
        var event = new Event();
        var form = new Form();
        event.setForm(form);
        eventsRepository.save(event);

        testParticipants = new ArrayList<>(
                List.of(
                        new Participant("John Doe", "", event, "john.doe@mail.com"),
                        new Participant("Lu Kas", "", event, "lukas@mail.com"),
                        new Participant("Enij Rouz", "", event, "enji.rouz@mail.com")
                )
        );
    }

    @After
    public void teaDown() {
        participantsRepository.deleteAll();
    }

    @Test
    public void testCreateParticipants() {
        var participant = testParticipants.get(0);
        participant.setSendNotification(true);
        testParticipants.set(0, participant);

        createParticipants();

        Assert.assertEquals(3, participantsRepository.findAll().size());
        Assert.assertEquals("Lu Kas", participantsRepository.findAll().get(1).getName());
        Assert.assertTrue(participantsRepository.findAll().get(0).isSendNotification());
    }

    @Test
    public void testAssigningUuidWhenCreatingEventWithoutId() {
        participantsService.createParticipant(testParticipants.get(0));

        Assert.assertNotNull(participantsRepository.findAll().get(0).getUuid());
    }

    @Test
    public void testSaveUuidWhenCreatingEventWithId() {
        var uuid = UUID.randomUUID();
        var participant = testParticipants.get(0);
        participant.setUuid(uuid);

        participantsService.createParticipant(participant);

        Assert.assertEquals(uuid, participantsRepository.findAll().get(0).getUuid());
    }

    @Test
    public void testFindParticipantById() {
        var uuid = UUID.randomUUID();
        var participant = testParticipants.get(1);
        participant.setUuid(uuid);

        createParticipants();

        Assert.assertNotNull(participantsService.findParticipantById(uuid));
        Assert.assertEquals(participant.getUuid(), participantsService.findParticipantById(uuid).getUuid());
    }

    @Test
    public void testFailedFindParticipantById() {
        createParticipants();

        Assert.assertNull(participantsService.findParticipantById(UUID.randomUUID()));
    }


    private void createParticipants() {
        for (var participant : testParticipants) {
            participantsService.createParticipant(participant);
        }
    }
}
