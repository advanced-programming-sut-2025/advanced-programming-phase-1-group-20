package models.enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ProfileMenuCommands implements Command {
    ChangeUsername("^change\\s+username\\s+-u\\s+(\\S+)$"),
    ChangeNickname("^change\\s+nickname\\s+-n\\s+(\\S+)$"),
    ChangePassword("^change\\s+password\\s+-p\\s+(\\S+)\\s+-o(\\S+)$"),
    ChangeEmail("^change\\s+email\\s+-e\\s+(\\S+)$");

    private final String regex;

    ProfileMenuCommands(String regex) {
        this.regex = regex;
    }


    @Override
    public String getRegex() {
        return regex;
    }

    @Override
    public boolean matches(String input) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
