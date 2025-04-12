package controllers;

import models.GameClock;
import models.GameMap;
import models.Player;
import models.Result;
import models.enums.GameMenuCommands;
import views.AppView;

public class GameMenuController implements Controller {
    private AppView appView;
    private Player player;
    private GameClock gameClock;
    private GameMap gMap;
    public GameMenuController(AppView appView, Player player) {
        this.appView = appView;
        this.player = player;
        this.gameClock = new GameClock();
        this.gMap = new GameMap();
    }
    @Override
    public void update(String input) {
        GameMenuCommands command = GameMenuCommands.getCommand(input);
        String[] args = command.parseInput(input);
        switch (command) {
            //implementing methods:
            case None -> Result.error("Invalid command");
        }
    }

    //implementing methods:

}
