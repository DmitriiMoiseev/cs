package ru.dmitry.server.commands;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class CommandParser {

    public static Map<String, Command> commands = Map.of("/help", new HelpCommand())

    public CommandParser() {
    }

    public static Command parse(String string) {
        Objects.requireNonNull(string);
        String[] args = string.split(" ", 3);
        String commandName = args[0];
        return commands.get(commandName);
    }

}
