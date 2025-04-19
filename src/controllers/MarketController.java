package controllers;

import models.GameClock;
import models.GameMap;
import models.Player.Player;
import models.Result;
import models.enums.commands.MainMenuCommands;
import views.AppView;

public class MarketController implements Controller {
    private AppView appView;
    private Player player;
    private GameClock gameClock;
    private GameMap gMap;
    public MarketController(AppView appView, Player player) {
        this.appView = appView;
        this.player = player;
        this.gameClock = new GameClock();
        this.gMap = new GameMap();
    }

    @Override
    public void update(String input) {
        MainMenuCommands command = MainMenuCommands.getCommand(input);
        String[] args = command.parseInput(input);
        switch (command) {
            case None -> Result.error("Invalid input");
        }
    }
}
