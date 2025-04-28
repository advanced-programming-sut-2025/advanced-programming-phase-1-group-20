package org.example.models.enums.commands;

import java.util.regex.Pattern;

public enum MarketMenuCommands implements Command {
    ShowAllProducts(Pattern.compile("\\s*Show\\s+all\\s+products\\s*")),
    ShowAllAvailableProducts(Pattern.compile("\\s*Show\\s+all\\s+available\\s+products\\s*")),
    Purchase(Pattern.compile("\\s*Purchase\\s+(?<productName>.+)\\s+-n\\s+(?<count>\\d+)\\s*")),
    CheatAddDollars(Pattern.compile("\\s*cheat\\s+add\\s+-n\\s+(?<count>\\d+)\\s*")),
    None(null);

    private final Pattern pattern;

    MarketMenuCommands(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public Pattern getPattern() {
        return this.pattern;
    }

    @Override
    public boolean matches(String input) {
        return pattern.matcher(input).matches();
    }

    public static MarketMenuCommands getCommand(String input) {
        for (MarketMenuCommands command : MarketMenuCommands.values()) {
            if (command != None && command.matches(input)) {
                return command;
            }

        }
        return None;
    }
}
