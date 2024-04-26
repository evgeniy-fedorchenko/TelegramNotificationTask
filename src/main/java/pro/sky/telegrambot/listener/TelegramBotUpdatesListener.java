package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.enums.AnswerMessage;
import pro.sky.telegrambot.exceptions.InvalidInputMessageException;
import pro.sky.telegrambot.services.GenerateAnswerService;

import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;
    private final GenerateAnswerService answerService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, GenerateAnswerService answerService) {
        this.telegramBot = telegramBot;
        this.answerService = answerService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
        logger.debug("Setup listener. Application stand up");
    }

    @PreDestroy
    public void destroy() {
        logger.debug("Application is now destroyed");
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

        SendMessage sendMessage;
        try {

            sendMessage = update.message().text() == null
                    ? answerService.reactNullText(update.message().chat().id())
                    : answerService.reactNotNullText(update.message());

        } catch (InvalidInputMessageException ex) {
            logger.error(ex.getMessage(), ex);
            sendMessage = new SendMessage(update.message().chat().id(), AnswerMessage.ANSWER_MESSAGE_TO_NOT_NULL_CORRECT_TEXT.getAnswer());
        } catch (RuntimeException ex) {
            logger.error("Unknown exception", ex);
            sendMessage = new SendMessage(update.message().chat().id(), AnswerMessage.ANSWER_MESSAGE_UNKNOWN_EXCEPTION.getAnswer());
        }
        telegramBot.execute(sendMessage);
    }
}
