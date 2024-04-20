package pro.sky.telegrambot.services;

import pro.sky.telegrambot.entities.NotificationTask;

import java.util.Set;

public interface NotificationTaskService {

    void saveNewTask(NotificationTask task);

    void checkActualTasks();

    void sendActualTasks(Set<NotificationTask> notificationTasks);
}
