package main.commands;

import main.serivce.EventService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class StartBot implements Command {
    @Override
    public SendMessage executeCommand(Message receivedMessage, EventService eventService) {
        SendMessage messageToSend=new SendMessage();
        messageToSend.enableMarkdown(true);
        messageToSend.setChatId(receivedMessage.getChatId().toString());
        messageToSend.setReplyToMessageId(receivedMessage.getMessageId());
        messageToSend.setText("Hello ^.^");

        Menu mainMenu=new Menu();
        mainMenu.showMenu(messageToSend,"Show New Events","Help");
        return messageToSend;
    }
}
