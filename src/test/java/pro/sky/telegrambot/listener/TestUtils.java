package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.utility.BotUtils;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;

@Component
public class TestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);

    static Update getUpdate(String messText) {

        String json;
        try {
            json = Files.readString(new ClassPathResource("update.json").getFile().toPath());
        } catch (IOException e) {
            LOGGER.error(() -> "Cannot read resource");
            throw new RuntimeException(e);
        }
        return BotUtils.fromJson(json.replace("%toReplace%", messText), Update.class);
    }

    static Update getNullTextUpdate() {
        try {
            String json = Files.readString(new ClassPathResource("update.json").getFile().toPath());
            return BotUtils.fromJson(json, Update.class);
        } catch (IOException e) {
            LOGGER.error(() -> "Cannot read resource");
            throw new RuntimeException(e);
        }
    }
}
