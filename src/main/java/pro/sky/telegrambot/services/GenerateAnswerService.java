package pro.sky.telegrambot.services;

import com.pengrad.telegrambot.request.SendMessage;

public interface GenerateAnswerService {

    SendMessage reactNullText(Long chatId);

    SendMessage reactNotNullText(Long chatId, String inputMessageText);
}
