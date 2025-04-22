package org.example.controllers;

import org.example.models.common.Date;
import org.example.models.GameMap;
import org.example.models.Player.Player;
import org.example.models.common.Result;
import org.example.models.enums.commands.MainMenuCommands;
import org.example.views.AppView;

public class MarketController implements Controller {
    private AppView appView;
    private Player player;
    private Date gameClock;
    private GameMap gMap;

    public MarketController(AppView appView, Player player) {
        this.appView = appView;
        this.player = player;
        this.gameClock = new Date();
        this.gMap = new GameMap();
    }

    @Override
    public Result update(String input) {
        MainMenuCommands command = MainMenuCommands.getCommand(input);
        String[] args = command.parseInput(input);
        switch (command) {
            case None -> Result.error("Invalid input");
        }
        return Result.success("Command executed successfully");
    }
}
