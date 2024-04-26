package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import pro.sky.telegrambot.repositories.NotificationTaskRepository;
import pro.sky.telegrambot.services.GenerateAnswerServiceImpl;
import pro.sky.telegrambot.services.NotificationTaskServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static pro.sky.telegrambot.enums.AnswerMessage.*;
import static pro.sky.telegrambot.listener.TestUtils.getNullTextUpdate;
import static pro.sky.telegrambot.listener.TestUtils.getUpdate;

@ExtendWith(MockitoExtension.class)
public class TelegramBotUpdatesListenerTests {

    @Mock
    private TelegramBot telegramBotMock;
    @Mock
    private GenerateAnswerServiceImpl answerServiceMock;
    @Mock
    private NotificationTaskServiceImpl notificationServiceMock;
    @Mock
    private NotificationTaskRepository repositoryMock;
    @InjectMocks
    private TelegramBotUpdatesListener out;

    @Captor
    private ArgumentCaptor<SendMessage> sendMessageCaptor;


    @Test
    void sendEmptyUpdatesList() {
        assertThat((out.process(Collections.emptyList()))).isEqualTo(-1);
        assertThat((out.process(Collections.singletonList(null)))).isEqualTo(-1);
        assertThat((out.process(Collections.singletonList(getNullTextUpdate())))).isEqualTo(-1);
    }

    private static Stream<Arguments> provideParamsForBotCommandsTest() {
        return Stream.of(
                Arguments.of("/start", ANSWER_MESSAGE_TO_START_COMMAND.getAnswer()),
                Arguments.of("/help", ANSWER_MESSAGE_TO_HELP_COMMAND.getAnswer())
        );
    }

    @ParameterizedTest
    @MethodSource("provideParamsForBotCommandsTest")
    void botCommandsTest(String command, String answerMessage) {

        Update update = getUpdate(command);
        when(answerServiceMock.reactNotNullText(update.message())).thenCallRealMethod();

        out.process(Collections.singletonList(update));
        verify(telegramBotMock).execute(sendMessageCaptor.capture());
        SendMessage actual = sendMessageCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        assertThat(actual.getParameters().get("text")).isEqualTo(answerMessage);

    }

    @Test
    void illegalMessageTextTest() {
        Update update = getUpdate("Illegal message text");
        when(answerServiceMock.reactNotNullText(update.message())).thenCallRealMethod();
        out.process(Collections.singletonList(update));
        verify(telegramBotMock).execute(sendMessageCaptor.capture());
        SendMessage actual = sendMessageCaptor.getValue();

        assertThat(actual.getParameters().get("chat_id")).isEqualTo(update.message().chat().id());
        assertThat(actual.getParameters().get("text")).isEqualTo(ANSWER_MESSAGE_TO_NOT_NULL_INCORRECT_TEXT.getAnswer());
    }

    @Test
    void positiveTest() {

        LocalDateTime targetDateTime = LocalDateTime.now().plusDays(1);
        String targetTaskText = "Correct task";
        Update update = getUpdate(targetDateTime + targetTaskText);

        when(answerServiceMock.reactNotNullText(update.message())).thenCallRealMethod();
        doNothing().when(notificationServiceMock).saveNewTask(update.message(), Pair.of(targetDateTime, targetTaskText));

        int actual = out.process(Collections.singletonList(update));

        verify(notificationServiceMock, times(1)).saveNewTask(update.message(), Pair.of(targetDateTime, targetTaskText));
        verify(answerServiceMock, times(1)).reactNotNullText(update.message());
        assertThat(actual).isEqualTo(-1);
    }
}
