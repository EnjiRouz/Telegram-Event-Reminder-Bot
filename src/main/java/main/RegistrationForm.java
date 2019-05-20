package main;

public class RegistrationForm {

    private String name;
    private String email;
    private String tgUsername;
    private String tgChatId;
    private boolean sendNotification;

    void sendRegistrationForm(){
        // TODO написать логику отправки формы участника в БД для конкретного мероприятия
    }

    /**
     * Проверка ФИО на корректность (настколько это возможно на этапе сбора информации)
     * @param name  анализируемый c помощью регулярных выражений набор символов (текст)
     * @return      true, если name может быть корректным
     */
    private static boolean isNameValid(String name) {
        final String EMAIL_REGEX="^[\\w+\\s*]*\\w{2,40}$";
        return name.matches(EMAIL_REGEX);
    }

    /**
     * Проверка email на корректность (настколько это возможно на этапе сбора информации)
     * @param email  анализируемый c помощью регулярных выражений набор символов (email)
     * @return      true, если email может быть корректным
     */
    private static boolean isEmailValid(String email) {
        final String EMAIL_REGEX="^.+@.{1,40}$";
        return email.matches(EMAIL_REGEX);
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        if(isNameValid(name)) this.name = name;
    }

    String getEmail() {
        return email;
    }

    void setEmail(String email) {
        if(isEmailValid(email)) this.email = email;
    }

    public String getTgUsername() {
        return tgUsername;
    }

    void setTgUsername(String tgUsername) {
        this.tgUsername = tgUsername;
    }

    public String getTgChatId() {
        return tgChatId;
    }

    void setTgChatId(String tgChatId) {
        this.tgChatId = tgChatId;
    }

    public boolean isSendNotification() {
        return sendNotification;
    }

    void setSendNotification(boolean sendNotification) {
        this.sendNotification = sendNotification;
    }
}
