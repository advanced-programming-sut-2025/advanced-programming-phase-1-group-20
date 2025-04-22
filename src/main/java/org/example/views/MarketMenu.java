package org.example.views;


import org.example.controllers.MarketController;
import org.example.models.Player.Player;
import org.example.models.common.Result;
import org.example.models.enums.commands.MarketMenuCommands;

public class MarketMenu implements AppMenu {
    private AppView appView;
    private MarketController controller;
    private Player player;

    public MarketMenu(AppView appView, Player player) {
        this.appView = appView;
        this.player = player;
        this.controller = new MarketController(appView, player);
    }

    @Override
    public void updateMenu(String input) {
        controller.update(input);
    }

    @Override
    public void handleResult(Result result, Object command) {
        if (result == null) return;

        if (result.success()) {
            System.out.println(result.message());

            // Handle specific market commands if needed
            if (command instanceof MarketMenuCommands) {
                MarketMenuCommands marketCommand = (MarketMenuCommands) command;
                switch (marketCommand) {
                    // Handle specific cases like displaying inventory, prices, etc.
                }
            }
        } else {
            System.out.println("Error: " + result.message());
        }
    }
}
