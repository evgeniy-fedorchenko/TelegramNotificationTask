package pro.sky.telegrambot.enums;

/**
 * Перечисление заготовленных ответов для пользователя. Ответы для команд (/start, /help и т.д.) тоже находятся тут<br>
 * Полный список констант:<br>
 * {@code ANSWER_MESSAGE_TO_NULL_TEXT} - если юзер не прислал текст<br>
 * {@code ANSWER_MESSAGE_TO_NOT_NULL_INCORRECT_TEXT} - если текст не парсится<br>
 * {@code ANSWER_MESSAGE_TO_NOT_NULL_CORRECT_TEXT} - ответ, что все таска успешно сохранена<br>
 * {@code START_ANSWER_MESSAGE_FOR_ACTUAL_TASK} - ШАБЛОН для уведомления, требуется .formatted()<br>
 * {@code ANSWER_MESSAGE_TO_START_COMMAND} - ответ на команду /start<br>
 * {@code ANSWER_MESSAGE_TO_HELP_COMMAND} - ответ на команду /help<br>
 * {@code ANSWER_MESSAGE_UNKNOWN_EXCEPTION} - ответ, если непредвиденная ошибка в т.ч. DatabaseException<br>
 */
public enum AnswerMessage {

    ANSWER_MESSAGE_TO_NULL_TEXT(
            "I'm sorry, but I don't understand something. Can you please tell me what you said in a text message?"
    ),

    ANSWER_MESSAGE_TO_NOT_NULL_CORRECT_TEXT(
            "Cool, I'll send you a notification when it's time."
    ),

    ANSWER_MESSAGE_TO_NOT_NULL_INCORRECT_TEXT(
            """
                    I'm sorry, but you're talking too hard. Try using this format instead
                    (you can use any language you want):
                                        
                        dd.MM.yyyy HH:mm notification-text
                                        
                    For Example:
                                        
                        21.04.2024 09:00 To celebrate my dad a happy birthday. Picking up his present from Ozon
                    """
    ),


    /**
     * Для метода .getAnswer() требуется {@code .formatted()} два аргумента: <b>%s</b> - username, <b>%s</b> - notificationText
     * */
    START_ANSWER_MESSAGE_FOR_ACTUAL_TASK(
            """
                    Hi, %s!
                    A while back, you asked me to remind you
                    __________
                                        
                    %s
                    __________
                    """
    ),

    ANSWER_MESSAGE_TO_START_COMMAND(
            """
                    What's up guy, I'm Notik :)
                    I'm here to help you remember the important stuff
                    Just let me know what it is you don't want to forget, and I'll make sure you get a reminder when it's time!
                    To get in touch with me, just use the following format:
                                        
                        dd.MM.yyyy HH:mm notification-text
                                        
                    For more information using /help
                    """
    ),

    ANSWER_MESSAGE_TO_HELP_COMMAND(
            """
                    Apparently, you've been having some problems since you came here? :D
                    Ha-ha, in fact, it's pretty simple. To make sure we understand each other, please use this message format:
                                        
                        dd.MM.yyyy HH:mm notification-text
                                        
                    See, there are three parts separated by spaces: the date, the time and the notification text. Let me explain a bit more:
                    1. At first, you put the date you want a reminder for. Type <dayNumber - dot - monthNumber - dot - yearNumber> like this:
                            day.month.year
                    2. Then, after a space, you can just write the time that you want me to send you a notification, type <hoursCount - colon - minutesCount> like this:
                            hours:minutes
                    3. Finally, you put a space and type in the message you want to get on that date and time
                       Everything that comes next, I'll just consider it the text of a future notification, Including spaces, different characters, and all that jazz
                       But keep in mind, I won't remember more than about a thousand characters .)
                                        
                    If you have any questions or wishes, feel free to reach out to my developer. You can find him here -> @AzorAhai777
                    Good luck, bro
                    """
    ), ANSWER_MESSAGE_UNKNOWN_EXCEPTION(
            """
                    Sorry, man
                    There seems to be some kind of mix-up, but I'm not sure what's going on here
                    Can we try again?
                    :(
                    """
    );


    private final String answer;

    AnswerMessage(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }
}
