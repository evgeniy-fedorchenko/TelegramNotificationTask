package pro.sky.telegrambot.services;

import pro.sky.telegrambot.entities.NotificationTask;

import java.util.List;

public interface NotificationTaskService {

    void saveNewTask(NotificationTask task);

    void checkActualTasks();

    void sendActualTasks(List<NotificationTask> notificationTasks);
}
