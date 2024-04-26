package pro.sky.telegrambot.services;

import com.pengrad.telegrambot.model.Message;
import org.springframework.data.util.Pair;
import pro.sky.telegrambot.entities.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationTaskService {


    void saveNewTask(Message message, Pair<LocalDateTime, String> notificationData);

    void checkActualTasks();

    void sendActualTasks(List<NotificationTask> notificationTasks);
}
