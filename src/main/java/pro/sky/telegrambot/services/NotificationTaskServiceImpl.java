package pro.sky.telegrambot.services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entities.NotificationTask;
import pro.sky.telegrambot.repositories.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static pro.sky.telegrambot.enums.AnswerMessage.START_ANSWER_MESSAGE_FOR_ACTUAL_TASK;

@Service
public class NotificationTaskServiceImpl implements NotificationTaskService {

    private final TelegramBot bot;
    private final NotificationTaskRepository repository;

    public NotificationTaskServiceImpl(TelegramBot bot, NotificationTaskRepository repository) {
        this.bot = bot;
        this.repository = repository;
    }

    @Override
    public void saveNewTask(NotificationTask task) {
        repository.save(task);
    }

    @Override
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void checkActualTasks() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> actualTasks = repository.findAllByNotificationDateTimeEquals(currentTime);

        if (!actualTasks.isEmpty()) {
            sendActualTasks(actualTasks);
        }
    }

    @Override
    public void sendActualTasks(List<NotificationTask> actualTasks) {
        actualTasks.forEach(task -> {
            String textForSend = START_ANSWER_MESSAGE_FOR_ACTUAL_TASK.getAnswer().formatted(task.getNotificationText());
            bot.execute(new SendMessage(task.getChatId(), textForSend));
        });
    }
}
