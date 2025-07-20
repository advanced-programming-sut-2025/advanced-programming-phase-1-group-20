package org.example.common.Lobby;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class LobbySettings {
    private boolean isPrivate;      // public/private access
    private boolean isVisible;      // visible/invisible in list
    private String password;        // password for private lobbies
    private int maxPlayers;         // maximum players (default 4)
    private String gameMode;        // game mode identifier

    // Default constructor
    public LobbySettings() {
        this.isPrivate = false;
        this.isVisible = true;
        this.password = null;
        this.maxPlayers = 4;
        this.gameMode = "standard";
    }

    @JsonCreator
    public LobbySettings(
        @JsonProperty("isPrivate") boolean isPrivate,
        @JsonProperty("isVisible") boolean isVisible,
        @JsonProperty("password") String password,
        @JsonProperty("maxPlayers") int maxPlayers,
        @JsonProperty("gameMode") String gameMode
    ) {
        this.isPrivate = isPrivate;
        this.isVisible = isVisible;
        this.password = password;
        this.maxPlayers = maxPlayers > 0 ? maxPlayers : 4;
        this.gameMode = gameMode != null ? gameMode : "standard";
    }

    // Convenience constructor for common settings
    public LobbySettings(boolean isPrivate, boolean isVisible, String password) {
        this();
        this.isPrivate = isPrivate;
        this.isVisible = isVisible;
        this.password = password;
    }

    // Validation methods
    public boolean requiresPassword() {
        return isPrivate && password != null && !password.trim().isEmpty();
    }

    public boolean isValidPassword(String inputPassword) {
        if (!isPrivate) return true;
        if (password == null || password.trim().isEmpty()) return true;
        return Objects.equals(password, inputPassword);
    }

    // Getters and Setters
    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }

    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean isVisible) { this.isVisible = isVisible; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers > 0 ? maxPlayers : 4;
    }

    public String getGameMode() { return gameMode; }
    public void setGameMode(String gameMode) { this.gameMode = gameMode; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LobbySettings that = (LobbySettings) o;
        return isPrivate == that.isPrivate &&
                isVisible == that.isVisible &&
                maxPlayers == that.maxPlayers &&
                Objects.equals(password, that.password) &&
                Objects.equals(gameMode, that.gameMode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isPrivate, isVisible, password, maxPlayers, gameMode);
    }

    @Override
    public String toString() {
        return "LobbySettings{" +
                "isPrivate=" + isPrivate +
                ", isVisible=" + isVisible +
                ", hasPassword=" + (password != null && !password.isEmpty()) +
                ", maxPlayers=" + maxPlayers +
                ", gameMode='" + gameMode + '\'' +
                '}';
    }
}
