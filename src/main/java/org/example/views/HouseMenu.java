package org.example.views;

import org.example.controllers.HouseMenuController;
import org.example.models.MapDetails.Building;
import org.example.models.Player.Player;
import org.example.models.common.Result;
import org.example.models.enums.commands.HouseMenuCommands;

public class HouseMenu implements AppMenu {
    private AppView appView;
    private HouseMenuController controller;
    private Player player;
    private Building house;

    public HouseMenu(AppView appView, Player player, Building house) {
        this.appView = appView;
        this.controller = controller;
        this.player = player;
        this.house = house;
        controller = new HouseMenuController(appView, player, house);
    }

    @Override
    public void updateMenu(String input) {
        controller.update(input);
    }

    @Override
    public void handleResult(Result result, Object command) {
        if (result == null) return;

        if (result.success()) {
            if (command instanceof HouseMenuCommands) {
                System.out.println(result.message());
            }
        } else {
            System.out.println("Error: " + result.message());
        }
    }
}
