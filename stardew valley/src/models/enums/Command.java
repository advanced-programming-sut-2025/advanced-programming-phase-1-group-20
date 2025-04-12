package models.enums;

public interface Command {
    String getRegex();
    boolean matches(String input);
}
