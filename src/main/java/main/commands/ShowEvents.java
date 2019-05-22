package main.commands;

import main.entity.Event;
import main.serivce.EventService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.stream.Collectors;

public class ShowEvents implements Command {

    @Override
    public SendMessage executeCommand(Message receivedMessage,EventService eventService) {
        SendMessage messageToSend=new SendMessage();
        messageToSend.enableMarkdown(true);
        messageToSend.setChatId(receivedMessage.getChatId().toString());
        messageToSend.setReplyToMessageId(receivedMessage.getMessageId());

        if (loadEventList(eventService).isEmpty()){
            messageToSend.setText("Sorry, but I don't have any "+
                    "information on the nearest events yet :(\n" +
                    "But I promise to find it out ^.^");
        }
        else {
            messageToSend.setText("Here you are ^.^");
            Menu mainMenu=new Menu();
            mainMenu.showMenu(messageToSend,loadEventList(eventService).toArray(new String[0]));
        }
        return messageToSend;
    }

    /**
     * Загрузка списка мероприятий из БД
     * @return      список мероприятий
     * */
    private List<String> loadEventList(EventService eventService){
        List<String> events;
        events= eventService.findAll().stream().map(Event::getName).collect(Collectors.toList());
        return events;
    }
}
