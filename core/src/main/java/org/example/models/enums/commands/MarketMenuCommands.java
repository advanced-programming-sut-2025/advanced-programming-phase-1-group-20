package org.example.models.enums.commands;

import java.util.regex.Pattern;

public enum MarketMenuCommands implements Command {
    ShowAllProducts(Pattern.compile("\\s*show\\s+all\\s+products\\s*")),
    ShowAllAvailableProducts(Pattern.compile("\\s*Show\\s+all\\s+available\\s+products\\s*")),
    Purchase(Pattern.compile("\\s*Purchase\\s+(?<productName>.+)\\s+-n\\s+(?<count>\\d+)\\s*")),
    CheatAddDollars(Pattern.compile("\\s*cheat\\s+add\\s+-n\\s+(?<count>\\d+)\\s*")),
    ToolUpgrade(Pattern.compile("^tools\\s+upgrade\\s+(?<toolName>.+)$")),
    ShowCurrentMenu(Pattern.compile("^show current menu$")),
    Build(Pattern.compile("^build\\s+-a\\s+(Barn|Big Barn|Deluxe Barn|Coop|Big Coop|Deluxe Coop|Well|Shipping Bin)\\s+-l\\s+(\\d+)\\s*,\\s*(\\d+)$")),

    CheatGetOut(Pattern.compile("^get\\s+out$")),
    None(null);

    private final Pattern pattern;

    MarketMenuCommands(Pattern pattern) {
        this.pattern = pattern;
    }

    public static MarketMenuCommands getCommand(String input) {
        for (MarketMenuCommands command : MarketMenuCommands.values()) {
            if (command != None && command.matches(input)) {
                return command;
            }

        }
        return None;
    }

    @Override
    public Pattern getPattern() {
        return this.pattern;
    }

    @Override
    public boolean matches(String input) {
        return pattern.matcher(input).matches();
    }
}
