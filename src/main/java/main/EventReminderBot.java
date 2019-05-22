package main;

import main.entity.Event;
import main.entity.Participant;
import main.serivce.EventService;
import main.serivce.ParticipantsService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@EnableScheduling
@Component
public class EventReminderBot extends TelegramLongPollingBot {
    private String botUsername = "event_registration_reminder_bot";
    private String botToken = "764767249:AAESgCyWEc05tNNfJXZgol6UFNo1ZBgMI2A";

    private final EventService eventService;
    private final ParticipantsService participantsService;

    private int registrationStage;
    private Participant userData= new Participant();

    public EventReminderBot(EventService eventService, ParticipantsService participantsService) {
        this.eventService = eventService;
        this.participantsService = participantsService;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public String getBotUsername() {
        return botUsername;
    }

    /**
     * Алгоритм поведения бота при получении обновлений
     * @param update    параметр, используемый для получения
     *                  и проверки различных типов обновлений
     **/
    public void onUpdateReceived(Update update) {
        Message message;
        String command;
        if (update.hasMessage()) {
            message = update.getMessage();
            command=message.getText();
            executeCommand(message, command);
        } else if(update.hasCallbackQuery()){
            try {
                execute(new SendMessage().setText(
                        update.getCallbackQuery().getData())
                        .setChatId(update.getCallbackQuery().getMessage().getChatId()));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Отправление напоминаний тем, кто его запрашивал при регистрации
     **/
    @Scheduled(fixedRate = 60000)
    public void remindAboutEvent() {
        LocalDateTime eventTime,reminderTime,now;
        Event event;
        List<Participant> receiversQueue;

        for (String eventName : loadEventList()) {
            event=eventService.findAllByName(eventName).get(0);
            eventTime=event.getDateTime().withSecond(0).withNano(0);
            reminderTime=eventTime.minusMinutes(119);
            now=LocalDateTime.now().atZone(ZoneId.of("Asia/Karachi")).toLocalDateTime().withSecond(0).withNano(0);

            if (reminderTime.isAfter(now))
                System.out.println("Event time is "+eventTime+"  reminder should be sent at "+reminderTime);

            if (reminderTime.equals(now)) {
                receiversQueue=participantsService.findParticipantsOfEvent(event);
                if(!receiversQueue.isEmpty()) {
                    for (Participant receiver : receiversQueue) {
                        if (receiver.isSendNotification()) {
                            sendReminderMsg(receiver.getTgChatId(), eventName + " is today at "
                                    + eventTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                        }
                    }
                }else System.out.println("receiversQueue.isEmpty()");
            }
        }
    }

    /**
     * Выполнение команды ботом из списка возможных
     * @param receivedMessage   полученное ботом сообщение
     * @param command            название обрабатываемой команды
     **/
    private void executeCommand(Message receivedMessage, String command) {
        SendMessage messageToSend=sendMsg(receivedMessage, "Hello ^.^");
        messageToSend = findCommand(receivedMessage, command, messageToSend);
        try {
            execute(messageToSend);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param receivedMessage       полученное ботом сообщение
     * @param command               искомая команда
     * @param messageToSend         сообщение, которое будет отправлено
     **/
    private SendMessage findCommand(Message receivedMessage, String command,SendMessage messageToSend) {
        switch (command) {
            case "/start":
            case "/menu":
                registrationStage=0;
                showMenu(messageToSend,"Show New Events","Help");
                break;

            case "/shownewevents":
            case "Show New Events":
            case "Get Back To New Events":
                registrationStage=0;
                messageToSend = showEventsNamesIfPossible(receivedMessage);
                break;

            case "Register For The Event":
                messageToSend=sendMsg(receivedMessage, "I need to know your name to register you for"+
                        " the event ^.^\nEnter your name like the example below:\nJohn Doe");
                showMenu(messageToSend,"Get Back To New Events","Help");
                registrationStage=1;
                break;

            case "Remind Me":
                // TODO реализовать функцию напоминания о мероприятии (надо учитывать часовой пояс)
                messageToSend = isRemindingApplied(receivedMessage, true,
                        "Okay, I'll send you a reminder ^.^");
                break;

            case "Don't Remind Me":
                messageToSend = isRemindingApplied(receivedMessage, false,
                        "Okay, I won't remind you about the event ^.^");
                break;

            case "Help":
                messageToSend = showInstruction(receivedMessage);
                break;
            default:
                messageToSend=sendMsg(receivedMessage, "I don't know how to answer that yet ^.^");
                for (String eventName : loadEventList()) {
                    if (command.equals(eventName)) {
                        userData.setEvent(eventService.findAllByName(eventName).get(0));
                        userData.setForm(eventService.findAllByName(eventName).get(0).getForm());
                        messageToSend = showEventInfoIfPossible(receivedMessage, eventName);
                        break;
                    }
                }
                if(registrationStage < 3 && registrationStage>0)
                    messageToSend = getUserRegistrationData(receivedMessage, messageToSend);
                break;
        }
        return messageToSend;
    }

    /**
     * Загрузка информации о мероприятии (описания) и отправление ее пользователю
     * @param receivedMessage       полученное ботом сообщение
     * */
    private SendMessage showEventInfoIfPossible(Message receivedMessage,String eventName) {
        SendMessage messageToSend;
        messageToSend=sendMsg(receivedMessage, eventService.findAllByName(eventName).get(0).getDescription());
        showMenu(messageToSend,"Register For The Event","Get Back To New Events","Help");
        return messageToSend;
    }

    /**
     * Получение ФИО и email от пользователя
     * @param receivedMessage       полученное ботом сообщение
     * @param messageToSend         сообщение, которое будет отправлено
     **/
    private SendMessage getUserRegistrationData(Message receivedMessage, SendMessage messageToSend) {
        switch (registrationStage) {
            case 1:
                userData.setName(receivedMessage.getText());
                if (userData.getName().isEmpty()) {
                    messageToSend = sendMsg(receivedMessage, "You wrote your name wrong!" +
                            "\nI need to know your name to register you for" +
                            " the event ^.^\nEnter your name like the example below:\nJohn Doe");
                    registrationStage = 1;
                } else {
                    messageToSend = sendMsg(receivedMessage, "Okay, now I need to know your email" +
                            " to register you for the event ^.^\n" +
                            "Enter your email like the example below:\njohn.doe@mail.com");
                    registrationStage = 2;
                }
                break;
            case 2:
                userData.setEmail(receivedMessage.getText());
                if (userData.getEmail().isEmpty()) {
                    messageToSend = sendMsg(receivedMessage, "You wrote your email wrong!" +
                            "\nI need to know your email to register you for" +
                            " the event ^.^\nEnter your email like the example below:\njohn.doe@mail.com");
                    registrationStage = 2;
                } else {
                    messageToSend = sendMsg(receivedMessage, "Okay, we've done! ^.^\n" +
                            "Do you want me to remind you about the event?");
                    showMenu(messageToSend, "Remind Me", "Don't Remind Me");
                    registrationStage = 3;
                }
                break;
        }
        return messageToSend;
    }

    /**
     * Отправка формы в БД и подтверждение отвпраления напоминания пользователю
     * @param receivedMessage   полученное ботом сообщение
     * @param isApplied         опция отправления уведомления (true - напоминание будет отправлено)
     * @param textToSend        сообщение бота в ответ на действие пользователя
     **/
    private SendMessage isRemindingApplied(Message receivedMessage, boolean isApplied, String textToSend) {
        SendMessage messageToSend;
        if (registrationStage == 3) {
            userData.setSendNotification(isApplied);
            userData.setTgChatId(receivedMessage.getChatId().toString());
            userData.setTgUsername(receivedMessage.getChat().getUserName());
            participantsService.createParticipant(userData);
            messageToSend = sendMsg(receivedMessage, textToSend);
        } else {
            messageToSend = sendMsg(receivedMessage, "You're smart, but I'm well trained ^.^");
        }
        showMenu(messageToSend, "Show New Events", "Help");
        registrationStage = 0;
        return messageToSend;
    }

    /**
     * Создание меню с выбором мероприятий или отправка сообщения об их отсутствии
     * @param receivedMessage   полученное ботом сообщение
     **/
    private SendMessage showEventsNamesIfPossible(Message receivedMessage) {
        SendMessage messageToSend;
        if (loadEventList().isEmpty()){
            messageToSend=sendMsg(receivedMessage, "Sorry, but I don't have any "+
                    "information on the nearest events yet :(\n" +
                    "But I promise to find it out ^.^");
        }
        else {
            messageToSend=sendMsg(receivedMessage, "Here you are ^.^");
            showMenu(messageToSend, loadEventList().toArray(new String[0]));
        }
        return messageToSend;
    }

    /**
     * Загрузка списка мероприятий из БД
     * @return      список мероприятий
     * */
    private List<String> loadEventList(){
        List<String> events;
        events= eventService.findAll().stream().map(Event::getName).collect(Collectors.toList());
        return events;
    }

    /**
     * Создание меню c заданными кнопками
     * @param messageToSend     сообщение, которое будет отправлено
     * @param options           названия кнопок, которые будут созданы
     **/
    private void showMenu (SendMessage messageToSend, String... options){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        messageToSend.setReplyMarkup(keyboardMarkup);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow firstKeyBoardRow = new KeyboardRow();

        for (String option : options) {
            firstKeyBoardRow.add(new KeyboardButton(option));
        }

        keyboardRows.add(firstKeyBoardRow);
        keyboardMarkup.setKeyboard(keyboardRows);
    }

    /**
     * Отправка в качестве ответного сообщения инструкции по использованию бота
     * @param receivedMessage   полученное ботом сообщение
     **/
    private SendMessage showInstruction(Message receivedMessage) {
        SendMessage messageToSend;
        messageToSend=sendMsg(receivedMessage,
                "1. Select the event you want to attend.\n"+
                        "2. Enter the data required for registration.\n"+
                        "3. Confirm that you want to be notified.");
        return messageToSend;
    }

    /**
     * Отправка напоминания в чат
     * @param chatId            чат, в который нужно отправить сообщение
     * @param textToSend        текст, которй должен быть отправлен
     **/
    private void sendReminderMsg(String chatId, String textToSend) {
        SendMessage messageToSend=new SendMessage();
        messageToSend.enableMarkdown(true);
        messageToSend.setChatId(chatId);
        messageToSend.setText(textToSend);
        try {
            execute(messageToSend);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Отправка сообщения в чат
     * @param receivedMessage   полученное ботом сообщение
     * @param textToSend        текст, которй должен быть отправлен
     * @return                  сообщение, которое будет отправлено
     **/
    private SendMessage sendMsg(Message receivedMessage, String textToSend) {
        SendMessage messageToSend=new SendMessage();
        messageToSend.enableMarkdown(true);
        messageToSend.setChatId(receivedMessage.getChatId().toString());
        messageToSend.setReplyToMessageId(receivedMessage.getMessageId());
        messageToSend.setText(textToSend);
        return messageToSend;
    }
}
