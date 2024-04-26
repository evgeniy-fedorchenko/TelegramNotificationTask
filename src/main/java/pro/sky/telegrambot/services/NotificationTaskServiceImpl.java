package pro.sky.telegrambot.services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.entities.NotificationTask;
import pro.sky.telegrambot.exceptions.DatabaseException;
import pro.sky.telegrambot.repositories.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static pro.sky.telegrambot.enums.AnswerMessage.START_ANSWER_MESSAGE_FOR_ACTUAL_TASK;

@Service
public class NotificationTaskServiceImpl implements NotificationTaskService {

    Logger logger = LoggerFactory.getLogger(NotificationTaskServiceImpl.class);

    private final TelegramBot bot;
    private final NotificationTaskRepository repository;

    public NotificationTaskServiceImpl(TelegramBot bot, NotificationTaskRepository repository) {
        this.bot = bot;
        this.repository = repository;
    }

    /**
     * Метод конструирует объект {@link NotificationTask} и сохраняет его в БД
     * @param message Объект, для получения значений полей <b>chatId</b> и <b>username</b>
     * @param notificationData Объект для получения значений полей <b>notificationText</b> и <b>notificationDateTime</b>
     * @throws DatabaseException Если полученый объект нарушает констрейнты БД, но такого не должно роизойти,
     * т.к. валидация на входе должна все отловить
     */
    @Override
    public void saveNewTask(Message message, Pair<LocalDateTime, String> notificationData) throws DatabaseException {
        NotificationTask newTask = new NotificationTask();

        newTask.setChatId(message.chat().id());
        newTask.setNotificationText(notificationData.getSecond());
        newTask.setNotificationDateTime(notificationData.getFirst());
        newTask.setUsername(message.chat().username());

        try {
            repository.save(newTask);
        } catch (DataAccessException ex) {
            logger.error("Exception while saving new task: " + newTask, ex);
            throw new DatabaseException(ex);
        }
        logger.debug("New NotificationTask created: {}", newTask);
    }

    @Override
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    @Transactional(readOnly = true)
    public void checkActualTasks() {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<NotificationTask> actualTasks = repository.findAllByNotificationDateTimeEquals(currentTime);

        if (!actualTasks.isEmpty()) {
            sendActualTasks(actualTasks);
            logger.info("Received {} actual tasks for sending", actualTasks.size());
        }
    }

    /**
     * Метод для отправки уведомлений своим пользователям. Отправленные уведомления удаляются из базы
     * Если увемление не было отправлено - оно остается  в базе
     * Но если в течении 30 минут не получилось его отправить (это 30 попыток отправки) - оно удаляется из базы
     * @param actualTasks Список уведомлений, которые необходимо отправить сейчас
     */
    @Override
    public void sendActualTasks(List<NotificationTask> actualTasks) {
        actualTasks.forEach(task -> {
            String textForSend = START_ANSWER_MESSAGE_FOR_ACTUAL_TASK.getAnswer().formatted(
                    task.getUsername(),
                    task.getNotificationText()
            );
            if (bot.execute(new SendMessage(task.getChatId(), textForSend)).isOk()) {
                repository.deleteById(task.getId());
                logger.info("Notification for {} was successful sent and deleted", task);

            } else if (task.getNotificationDateTime().isBefore(LocalDateTime.now().plusMinutes(30))) {
                logger.warn("Notification {} has not been sent", task);

            } else {
                logger.info("Notification for {} has not been sent at 30 times. Deleted", task);
            }
        });
    }
}
