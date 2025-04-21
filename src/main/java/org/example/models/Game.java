package org.example.models;

import org.example.models.Player.Player;

import java.util.List;

public class Game {
    private final List<Player> players;
    private Player currentPlayer;
    private Date date;

    public Game(List<Player> players) {
        this.players = players;
        this.currentPlayer = players.get(0); // Set the first player as the current player
        this.date = new Date();
    }

    public Date getDate() {
        return date;
    }
}
