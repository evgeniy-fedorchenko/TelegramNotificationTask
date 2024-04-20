package pro.sky.telegrambot.services;

import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;

@Service
public class GenerateAnswerServiceImpl implements GenerateAnswerService {

    private final NotificationTaskService notificationTaskService;

    public GenerateAnswerServiceImpl(NotificationTaskService notificationTaskService) {
        this.notificationTaskService = notificationTaskService;
    }

    @Override
    public SendMessage reactNullText(Long chatId) {
        // todo Извиняемся, что не понимам текст, возвращаем ответ
        return null;
    }

    @Override
    public SendMessage reactNotNullText(Long chatId, String inputMessageText) {
        // todo собираем сущность NotificationTask и передаем ее в метод сохранения в NotificationTaskService
        return null;
    }
}
