package main.commands;

import main.serivce.EventService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface Command {
    SendMessage executeCommand(Message receivedMessage, EventService eventService);
}
