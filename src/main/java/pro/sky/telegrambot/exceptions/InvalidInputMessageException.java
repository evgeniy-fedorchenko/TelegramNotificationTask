package pro.sky.telegrambot.exceptions;

public class InvalidInputMessageException extends RuntimeException {
    public InvalidInputMessageException(String message) {
        super(message);
    }
}
