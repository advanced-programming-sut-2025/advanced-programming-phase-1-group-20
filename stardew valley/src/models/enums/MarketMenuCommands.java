package models.enums;

import java.util.regex.Pattern;

public enum MarketMenuCommands implements Command {
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
}
