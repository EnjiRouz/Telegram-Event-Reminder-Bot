package main;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventReminderBot extends TelegramLongPollingBot{

    //@Value("${EventReminderBot.botUsername}")
    private String botUsername = "event_registration_reminder_bot";
    private String botToken = "764767249:AAESgCyWEc05tNNfJXZgol6UFNo1ZBgMI2A";

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
     * Выполнение команды ботом из списка возможных
     * @param receivedMessage   полученное ботом сообщение
     * @param command            название обрабатываемой команды
     **/
    private void executeCommand(Message receivedMessage, String command) {
        RegistrationForm userData= new RegistrationForm();
        SendMessage messageToSend=sendMsg(receivedMessage, "Hello ^.^");
        switch (command) {
            case "/start":
            case "/menu":
                showMenu(messageToSend,"Show New Events","Help");
                break;

            case "/shownewevents":
            case "Show New Events":
            case "Get Back To New Events":
                if (loadEventList().isEmpty()){
                    messageToSend=sendMsg(receivedMessage, "Sorry, but I don't have any "+
                            "information on the nearest events yet :(\n" +
                            "But I promise to find it out ^.^");
                }
                else {
                    messageToSend=sendMsg(receivedMessage, "Here you are ^.^");
                    showEventList(messageToSend, loadEventList());
                }
                break;

            case "Register For The Event":
                // TODO описать логику для работы с БД по ПОЛУЧЕНИЮ данных по форме регистрации
                messageToSend=sendMsg(receivedMessage, "I need to know your name to register you for"+
                        " the event ^.^\nEnter your name like the example below:\nJohn Doe");
                showMenu(messageToSend,"Confirm Name");
                break;

            case "Confirm Name":
                // TODO описать логику для работы с БД по ОТПРАВКЕ данных из форме регистрации
                userData.setName(receivedMessage.getText());
                userData.setTgChatId(receivedMessage.getChatId().toString());
                userData.setTgUsername(receivedMessage.getChat().getUserName());
                if(userData.getName().isEmpty()){
                    messageToSend=sendMsg(receivedMessage, "You wrote your name wrong!"+
                            "\nI need to know your name to register you for"+
                            " the event ^.^\nEnter your name like the example below:\nJohn Doe");
                    showMenu(messageToSend,"Confirm Name");
                }else {
                    messageToSend = sendMsg(receivedMessage, "Okay, now I need to know your email"+
                            " to register you for the event ^.^\n"+
                            "Enter your email like the example below:\njohn.doe@mail.com");
                    showMenu(messageToSend, "Confirm Email");
                }
                break;

            case "Confirm Email":
                userData.setEmail(receivedMessage.getText());
                if(userData.getEmail().isEmpty()){
                    messageToSend=sendMsg(receivedMessage, "You wrote your email wrong!"+
                            "\nI need to know your email to register you for"+
                            " the event ^.^\nEnter your email like the example below:\njohn.doe@mail.com");
                    showMenu(messageToSend,"Confirm Name");
                }else {
                    messageToSend = sendMsg(receivedMessage, "Okay, we've done! ^.^\n"+
                            "Do you want me to remind you about the event?");
                    showMenu(messageToSend,"Remind Me","Don't Remind Me");
                }
                break;

            case "Remind Me":
                // TODO реализовать функцию напоминания о мероприятии (надо учитывать часовой пояс)
                userData.setSendNotification(true);
                userData.sendRegistrationForm();
                messageToSend = sendMsg(receivedMessage, "Okay, I'll send you a reminder ^.^");
                showMenu(messageToSend,"Show New Events","Help");
                break;

            case "Don't Remind Me":
                userData.setSendNotification(false);
                userData.sendRegistrationForm();
                messageToSend=sendMsg(receivedMessage, "Okay, I won't remind you about the event ^.^");
                showMenu(messageToSend,"Show New Events","Help");
                break;

            case "Help":
                messageToSend=sendMsg(receivedMessage, "1. Select the event you want to attend.\n"+
                        "2. Enter the data required for registration.\n"+
                        "3. Confirm that you want to be notified.");
                break;
            default:
                messageToSend=sendMsg(receivedMessage, "I don't know how to answer that yet ^.^");
                for (String eventName : loadEventList()) {
                    if (command.equals(eventName)) {
                        messageToSend=sendMsg(receivedMessage, "I found some information ^.^");
                        showMenu(messageToSend,"Register For The Event","Get Back To New Events","Help");
                        // TODO реализовать отображение данных о выбраннх мероприятиях (загрузить из БД)
                        break;
                    }
                }
                break;
        }
        try {
            execute(messageToSend);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    /**
     * Создание меню c заданными кнопками
     * @param messageToSend     сообщение, которое будет отправлено
     * @param options           названия кнопок, которые будут созданы
     **/
    private void showMenu (SendMessage messageToSend, String... options){
        somthing(messageToSend, options);
    }

    private void somthing(SendMessage messageToSend, String[] options) {
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
     * Загрузка списка мероприятий из БД
     * @return      список мероприятий
     * */
    private List<String> loadEventList(){
        // TODO написать логику по работе с БД, с помощью которой мы получим количество мероприятий
        List<String> events=new ArrayList<>();
        events.add("Event 1");
        events.add("Event 2");
        events.add("Event 3");
        events.add("Event 4");
        return events;
    }

    /**
     * Вывод списка доступных для регистрации мероприятий
     * @param events    список названий мероприятий
     **/
    private void showEventList (SendMessage messageToSend, List<String> events){
        ReplyKeyboardMarkup keyboardMarkup=new ReplyKeyboardMarkup();
        messageToSend.setReplyMarkup(keyboardMarkup);
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRows=new ArrayList<>();
        KeyboardRow firstKeyBoardRow= new KeyboardRow();

        for (String eventName : events) {
            firstKeyBoardRow.add(new KeyboardButton(eventName));
        }

        keyboardRows.add(firstKeyBoardRow);
        keyboardMarkup.setKeyboard(keyboardRows);
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
