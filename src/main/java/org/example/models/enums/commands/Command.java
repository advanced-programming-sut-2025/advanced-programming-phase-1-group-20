package org.example.models.enums.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Command {
    Pattern getPattern();

    boolean matches(String input);

    default String[] parseInput(String input) {
        Pattern pattern = getPattern();
        if (pattern == null) return null;
        Matcher matcher = pattern.matcher(input);
        if (matcher.matches()) {
            String[] inputs = new String[matcher.groupCount()];
            for (int i = 0; i < matcher.groupCount(); i++) {
                inputs[i] = matcher.group(i + 1);
            }
            return inputs;
        }
        return null;
    }
}
