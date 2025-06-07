package org.example.views;


import org.example.controllers.MarketController;
import org.example.models.Market;
import org.example.models.Player.Player;
import org.example.models.common.Result;
import org.example.models.enums.commands.MarketMenuCommands;

public class MarketMenu implements AppMenu {
    private AppView appView;
    private MarketController controller;
    private Player player;
    private Market market;


    public MarketMenu(AppView appView, Player player, Market market) {
        this.appView = appView;
        this.player = player;
        this.market = market;
        this.controller = new MarketController(appView, player, market);
    }

    @Override
    public void updateMenu(String input) {
        controller.update(input);
    }

    @Override
    public void handleResult(Result result, Object command) {
        if (result == null) return;

        if (result.success()) {
            if (command instanceof MarketMenuCommands) {
                System.out.println(result.message());
            }

        } else {
            System.out.println("Error: " + result.message());
        }
    }


}
