package org.example.controllers;

import org.example.models.Date;
import org.example.models.GameMap;
import org.example.models.Player.Player;
import org.example.models.Result;
import org.example.models.enums.commands.GameMenuCommands;
import org.example.views.AppView;

public class GameMenuController implements Controller {
    private AppView appView;
    private Player player;
    private Date gameClock;
    private GameMap gMap;

    public GameMenuController(AppView appView, Player player) {
        this.appView = appView;
        this.player = player;
        this.gameClock = new Date();
        this.gMap = new GameMap();
    }

    @Override
    public void update(String input) {
        GameMenuCommands command = GameMenuCommands.getCommand(input);
        String[] args = command.parseInput(input);
        switch (command) {
            //implementing methods:
            case ShowTime -> showTime();
            case None -> Result.error("Invalid command");
        }
    }

    //implementing methods:
    public void showTime() {
        gameClock.displayTime();
    }

    public void showDate() {
        gameClock.displayDate();
    }

    public void showDateTime() {
        gameClock.displayClock();
    }

    //TODO: implement needed methods -> Kasra -> time and tools

}
