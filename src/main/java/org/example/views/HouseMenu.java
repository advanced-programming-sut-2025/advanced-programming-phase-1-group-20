package org.example.views;

import org.example.controllers.HouseMenuController;
import org.example.controllers.MarketController;
import org.example.models.App;
import org.example.models.MapDetails.Building;
import org.example.models.MapDetails.GameMap;
import org.example.models.Market;
import org.example.models.Player.Player;
import org.example.models.common.Result;
import org.example.models.enums.commands.HouseMenuCommands;
import org.example.models.enums.commands.LoginRegisterMenuCommands;

public class HouseMenu implements AppMenu{
    private AppView appView;
    private App app;
    private HouseMenuController controller;
    private Player player;
    private Building house;
    private GameMap gameMap;

    public HouseMenu(AppView appView, App app, HouseMenuController controller, Player player , Building house, GameMap gameMap) {
        this.appView = appView;
        this.app = app;
        this.controller = controller;
        this.player = player;
        this.house = house;
        this.gameMap = gameMap;
    }




    @Override
    public void updateMenu(String input) {
        controller.update(input);
    }

    @Override
    public void handleResult(Result result, Object command) {
        if (result == null) return;

        if (result.success()) {
            if(command instanceof HouseMenuCommands) {
                System.out.println(result.message());
            }
        } else {
            System.out.println("Error: " + result.message());
        }
    }
}
