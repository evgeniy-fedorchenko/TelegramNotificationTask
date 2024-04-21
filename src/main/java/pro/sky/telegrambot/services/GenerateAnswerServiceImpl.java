package pro.sky.telegrambot.services;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entities.NotificationTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pro.sky.telegrambot.enums.AnswerMessage.*;

@Service
public class GenerateAnswerServiceImpl implements GenerateAnswerService {

    private final NotificationTaskService notificationTaskService;

    public GenerateAnswerServiceImpl(NotificationTaskService notificationTaskService) {
        this.notificationTaskService = notificationTaskService;
    }

    @Override
    public SendMessage reactNullText(Long chatId) {
        return new SendMessage(chatId, ANSWER_MESSAGE_TO_NULL_TEXT.getAnswer());
    }

    @Override
    public SendMessage reactNotNullText(Message message) {
        Long chatId = message.chat().id();
        String inputMessageText = message.text();

        Optional<String> commandOpt = checkBotCommand(inputMessageText);
        if (commandOpt.isPresent()) {
            return new SendMessage(chatId, commandOpt.get());
        }

        Optional<Pair<LocalDateTime, String>> notificationDataOpt = purseNotificationData(inputMessageText);
        if (notificationDataOpt.isEmpty()) {
            return new SendMessage(chatId, ANSWER_MESSAGE_TO_NULL_INCORRECT_TEXT.getAnswer());
        }

        new Thread(() -> {
            NotificationTask newTask = new NotificationTask();
            newTask.setChatId(chatId);
            newTask.setNotificationText(notificationDataOpt.get().getSecond());
            newTask.setNotificationDateTime(notificationDataOpt.get().getFirst());
            newTask.setUsername(message.chat().username());
            notificationTaskService.saveNewTask(newTask);
        }).start();

        return new SendMessage(chatId, ANSWER_MESSAGE_TO_NOT_NULL_CORRECT_TEXT.getAnswer());

    }

    private Optional<String> checkBotCommand(String inputMessageText) {
        return switch (inputMessageText) {
            // todo сделать Enum из команд
            case "/start" -> Optional.of(ANSWER_MESSAGE_TO_START_COMMAND.getAnswer());
            case "/help" -> Optional.of(ANSWER_MESSAGE_TO_HELP_COMMAND.getAnswer());
            default -> Optional.empty();
        };
    }

    private Optional<Pair<LocalDateTime, String>> purseNotificationData(String inputMessageText) {
        // todo протестить паттерн
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
            return Optional.empty();
        }
        String item = matcher.group(6);
        String dateTime = matcher.group(1) + matcher.group(2) + matcher.group(3) + matcher.group(4) + matcher.group(5).substring(0, 1000);
        LocalDateTime date = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

        return Optional.of(Pair.of(date, item));
    }
}
