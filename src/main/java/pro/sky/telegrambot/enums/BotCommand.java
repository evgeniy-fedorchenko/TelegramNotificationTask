package pro.sky.telegrambot.enums;

public enum BotCommand {

    start("/start"),
    help("/help");


    private final String commandName;

    BotCommand(String commandName) {
        this.commandName = commandName;
    }
    public String getCommandName() {
        return commandName;
    }
}
