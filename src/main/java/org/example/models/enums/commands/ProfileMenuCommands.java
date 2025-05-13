package org.example.models.enums.commands;

import java.util.regex.Pattern;

public enum ProfileMenuCommands implements Command {
    ChangeUsername(Pattern.compile("^change\\s+username\\s+-u\\s+(\\S+)$")),
    ChangeNickname(Pattern.compile("^change\\s+nickname\\s+-n\\s+(\\S+)$")),
    ChangePassword(Pattern.compile("^change\\s+password\\s+-p\\s+(\\S+)\\s+-o(\\S+)$")),
    ChangeEmail(Pattern.compile("^change\\s+email\\s+-e\\s+(\\S+)$")),
    GoToMain(Pattern.compile("^go\\s+to\\s+main$")),
    ShowUserInfo(Pattern.compile("^show\\s+user\\s+info$")),
    Logout(Pattern.compile("^logout$")),
    ShowCurrentMenu(Pattern.compile("^show current menu$")),
    ChangeMenu(Pattern.compile("^menu enter (?<menuName>.*)$")),
    None(null);

    private final Pattern pattern;

    ProfileMenuCommands(Pattern pattern) {
        this.pattern = pattern;
    }

    public static ProfileMenuCommands getCommand(String input) {
        input = input.trim();
        for (ProfileMenuCommands command : values()) {
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
        return this.pattern.matcher(input).matches();
    }
}
