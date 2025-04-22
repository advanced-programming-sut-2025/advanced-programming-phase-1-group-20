package org.example.controllers;

import org.example.models.common.Date;
import org.example.models.common.GameMap;
import org.example.models.Player.Player;
import org.example.models.common.Result;
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
    public Result update(String input) {
        GameMenuCommands command = GameMenuCommands.getCommand(input);
        String[] args = command.parseInput(input);
        Result result = null;

        switch (command) {
            // time related commands
            case ShowTime -> showTime();
            case ShowDate -> showDate();
            case ShowDateTime -> showDateTime();
            case AdvanceTime -> advanceTime(args);
            case AdvanceDate -> advanceDate(args);

            // Map related commands


            // Player related commands

            // Farm related commands

            // saving related commands
            case SaveGame -> {

            }
            case AutoSave -> {

            }
            case None -> Result.error("Invalid command");
        }
        appView.handleResult(result, command);

        return result;
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

    public void advanceTime(String[] args) {
        int hours = Integer.parseInt(args[0]);
        gameClock.advanceTime(hours);
    }

    public void advanceDate(String[] args) {
        int days = Integer.parseInt(args[0]);
        gameClock.advanceDays(days);
    }


    //TODO: implement needed methods -> Kasra -> time and tools
    // TODO: implement showing map
}
