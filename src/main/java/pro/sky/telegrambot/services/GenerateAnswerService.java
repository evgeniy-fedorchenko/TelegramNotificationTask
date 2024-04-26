package pro.sky.telegrambot.services;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;

public interface GenerateAnswerService {

    SendMessage reactNullText(Long chatId);

    SendMessage reactNotNullText(Message message);
}
