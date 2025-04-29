package org.example.views;


import org.example.controllers.MarketController;
import org.example.models.App;
import org.example.models.Market;
import org.example.models.Player.Player;
import org.example.models.common.Result;
import org.example.models.enums.commands.MarketMenuCommands;

public class MarketMenu implements AppMenu {
    private AppView appView;
    private App app;
    private MarketController controller;
    private Player player;
    private Market market;


    public MarketMenu(AppView appView , App app, Player player , Market market) {
        this.appView = appView;
        this.app = app;
        this.player = player;
        this.controller = new MarketController(appView , app , player , market);
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
