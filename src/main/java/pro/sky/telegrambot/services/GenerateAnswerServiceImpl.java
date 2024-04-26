package pro.sky.telegrambot.services;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entities.NotificationTask;
import pro.sky.telegrambot.exceptions.DatabaseException;
import pro.sky.telegrambot.exceptions.InvalidInputMessageException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pro.sky.telegrambot.enums.AnswerMessage.*;
import static pro.sky.telegrambot.enums.BotCommand.help;
import static pro.sky.telegrambot.enums.BotCommand.start;

@Service
public class GenerateAnswerServiceImpl implements GenerateAnswerService {

    private final Logger logger = LoggerFactory.getLogger(GenerateAnswerServiceImpl.class);
    private final Map<String, String> botCommand = new HashMap<>(Map.of(
            start.getCommandName(), ANSWER_MESSAGE_TO_START_COMMAND.getAnswer(),
            help.getCommandName(), ANSWER_MESSAGE_TO_HELP_COMMAND.getAnswer()
    ));
    private final NotificationTaskService notificationTaskService;

    public GenerateAnswerServiceImpl(NotificationTaskService notificationTaskService) {
        this.notificationTaskService = notificationTaskService;
    }

    @Override
    public SendMessage reactNullText(Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId, ANSWER_MESSAGE_TO_NULL_TEXT.getAnswer());
        logger.info("Detected update without text from chat{chatId: {}}. Processed", chatId);
        return sendMessage;
    }


    /**
     * @param message Корректный объект {@link Message}. Объект проверяется на соответвие командам бота и возвращается
     *                ответ на команду. В противном случае парсится на объект {@link NotificationTask} и сохраняется в БД.
     *                Если не получается спарсить - возвращаем {@link SendMessage} со стандартным ответом
     * @return {@link SendMessage} Ответное сообщение для юзера на его chatId
     * @throws DatabaseException если сообщение споткнулось об констрейнты базы данных, но такого не должно
     *                           произойти, т.к. валидация на входе должна все отловить
     */
    @Override
    public SendMessage reactNotNullText(Message message) throws DatabaseException {
        Long chatId = message.chat().id();
        String inputMessageText = message.text();

        if (botCommand.containsKey(inputMessageText)) {
            return new SendMessage(chatId, botCommand.get(inputMessageText));
        }
//        Optional<String> commandOpt = checkBotCommand(inputMessageText);
//        if (commandOpt.isPresent()) {
//            return new SendMessage(chatId, commandOpt.get());
//        }

        Pair<LocalDateTime, String> notificationData;
        try {
            notificationData = purseNotificationData(inputMessageText);
        } catch (InvalidInputMessageException ex) {
            logger.error(ex.getMessage());
            return new SendMessage(chatId, ANSWER_MESSAGE_TO_NOT_NULL_INCORRECT_TEXT.getAnswer());
        }

        new Thread(() -> notificationTaskService.saveNewTask(message, notificationData));
        return new SendMessage(chatId, ANSWER_MESSAGE_TO_NOT_NULL_CORRECT_TEXT.getAnswer());
    }

//    private Optional<String> checkBotCommand(String inputMessageText) {
//        return switch (inputMessageText) {
//            // todo сделать Enum из команд
//            case "/start" -> Optional.of(ANSWER_MESSAGE_TO_START_COMMAND.getAnswer());
//            case "/help" -> Optional.of(ANSWER_MESSAGE_TO_HELP_COMMAND.getAnswer());
//            default -> Optional.empty();
//        };
//    }


    private Pair<LocalDateTime, String> purseNotificationData(String inputMessageText) throws InvalidInputMessageException {
        /* Regexp: 1ая группа - число - от 1го до 31 с возможным ведущим нулем у однозначных чисел; точка
                   2ая группа - месяц - от 1го  до 12 с возможным ведущим нулем у однозначных номером месяца; точка
                   3ая группа - год - две или четыре цифры, проверка на уже наступившую дату будет позже; пробел
                   4ая группа - часы - от 0 до 23 с возможным ведущим нулем у однознвчных часов; двоеточие
                   5ая группа - минуты - от 0 до 59 в возможным ведущим нулем у однозначных минут; пробел
                   6ая группа - текст - абсолютно любой текст длинной от 1 до 1000 символов
           P.S.: Пример с платформы недостаточно точен, например он принимает вот такую дату: 315..:5202776960 */
        Pattern pattern = Pattern.compile(
                "^(0?[^0]|[1-2]\\d|3[0-1])\\.(0?[^0]|1[0-2])\\.(\\d{2}|\\d{4}) (0?\\d|1\\d|2[0-3]):(0?\\d|[1-5]\\d) (.*)$"
        );
        Matcher matcher = pattern.matcher(inputMessageText);

        if (!matcher.matches()) {
            throw new InvalidInputMessageException("Input message is not matched of pattern. Input message: " + inputMessageText);
        }
        String date = matcher.group(1) + "." + matcher.group(2) + "." + matcher.group(3);
        String time = matcher.group(4) + ":" + matcher.group(5);
        String item = matcher.group(6).length() > 1000 ? matcher.group(6).substring(0, 1000) : matcher.group(6);

        LocalDateTime dateTime = LocalDateTime.parse(date + " " + time, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new InvalidInputMessageException("DateTime of input message is before current dateTime. Input message: " + inputMessageText);
        }
        return Pair.of(dateTime, item);
    }
}
