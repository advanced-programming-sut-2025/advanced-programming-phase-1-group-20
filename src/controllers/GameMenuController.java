package controllers;

import models.Date;
import models.GameMap;
import models.Player.Player;
import models.Result;
import models.enums.commands.GameMenuCommands;
import views.AppView;

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

    
}
