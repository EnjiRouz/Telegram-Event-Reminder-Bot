package main.commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

class Menu {
    /**
     * Создание меню c заданными кнопками
     * @param messageToSend     сообщение, которое будет отправлено
     * @param options           названия кнопок, которые будут созданы
     **/
    void showMenu (SendMessage messageToSend, String... options){
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
}
