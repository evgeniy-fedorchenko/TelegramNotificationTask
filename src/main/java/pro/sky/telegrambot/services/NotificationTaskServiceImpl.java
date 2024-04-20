package pro.sky.telegrambot.services;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entities.NotificationTask;
import pro.sky.telegrambot.repositories.NotificationTaskRepository;

import java.util.Set;
import java.util.concurrent.TimeUnit;

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
        // todo сохраняем новую сущность в бд
    }

    @Override
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void checkActualTasks() {
        // todo опрашиваем бд раз в минуту, и если нашли актуальные таски - отправляем их
    }

    @Override
    public void sendActualTasks(Set<NotificationTask> notificationTasks) {
        // todo Отправялем нотификации
    }
}
