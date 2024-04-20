package pro.sky.telegrambot.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "notification-task")
public class NotificationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private long chatId;

    @NotBlank
    private String notificationText;

    @Future
    private LocalDateTime notificationDateTime;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public LocalDateTime getNotificationDateTime() {
        return notificationDateTime;
    }

    public void setNotificationDateTime(LocalDateTime notificationDateTime) {
        this.notificationDateTime = notificationDateTime;
    }

    @Override
    public boolean equals(Object otherNotificationTask) {
        if (this == otherNotificationTask) {
            return true;
        }
        if (otherNotificationTask == null || getClass() != otherNotificationTask.getClass()) {
            return false;
        }
        NotificationTask that = (NotificationTask) otherNotificationTask;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "NotifTask: {ID:%d, chatId:%d, notificationText:'%s', notificationTime:'%s'}".formatted(
                id, chatId, notificationText.substring(0, 15), notificationDateTime
        );
    }
}
