package com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.support;

import com.michaelszymczak.sample.tddrefalgo.apps.marketmaker.MarketMakingModule;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.AckMessage;
import com.michaelszymczak.sample.tddrefalgo.protocols.pricing.ImmutableQuotePricingMessage;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class CommandLines implements Command {

    private final List<Command> commands;

    private CommandLines(List<Command> commands) {
        this.commands = commands;
    }

    public static CommandLines parseAll(final String commandDelimiter, String message) {
        return new CommandLines(stream(message.split(commandDelimiter, -1))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(CommandLines::parseOne)
                .collect(Collectors.toList()));
    }

    private static Command parseOne(String commandLine) {
        List<String> split = stream(commandLine.split("/", -1))
                .map(String::trim)
                .collect(Collectors.toList());
        return getCommand(split.get(0), split);
    }

    private static Command getCommand(String commandType, List<String> split) {
        switch (commandType) {
            case "Q": {
                if (split.size() != 5) {
                    throw new IllegalArgumentException(split.toString());
                }
                return marketMakingModule -> marketMakingModule.onMessage(new ImmutableQuotePricingMessage(
                        split.get(1),
                        Integer.parseInt(split.get(2)),
                        Long.parseLong(split.get(3)),
                        Long.parseLong(split.get(4))
                ));
            }
            case "A": {
                if (split.size() != 1) {
                    throw new IllegalArgumentException(split.toString());
                }
                return marketMakingModule -> marketMakingModule.onMessage(AckMessage.ACK_MESSAGE);
            }
            default:
                throw new IllegalArgumentException(split.toString());
        }

    }

    @Override
    public void executeAgainst(MarketMakingModule marketMakingModule) {
        commands.forEach(command -> command.executeAgainst(marketMakingModule));
    }
}
