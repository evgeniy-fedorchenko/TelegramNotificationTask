package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.services.GenerateAnswerService;

import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;
    private final GenerateAnswerService service;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, GenerateAnswerService service) {
        this.telegramBot = telegramBot;
        this.service = service;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing has BEGUN for update: {}", update);
            process(update);
            logger.info("Processing has ENDED for update: {}", update);
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void process(Update update) {
        if (update == null || update.message() == null) {
            return;
        }

        Long chatId = update.message().chat().id();
        String inputMessageText = update.message().text();

        SendMessage request = inputMessageText == null
                ? service.reactNullText(chatId)
                : service.reactNotNullText(chatId, inputMessageText);

        telegramBot.execute(request);
    }
}
