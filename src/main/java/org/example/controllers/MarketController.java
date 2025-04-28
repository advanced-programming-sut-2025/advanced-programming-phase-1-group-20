package org.example.controllers;

import org.example.models.MapDetails.GameMap;
import org.example.models.Market;
import org.example.models.Player.Player;
import org.example.models.common.Date;
import org.example.models.common.Result;
import org.example.models.enums.commands.MainMenuCommands;
import org.example.models.enums.commands.MarketMenuCommands;
import org.example.views.AppView;

public class MarketController implements Controller {
    private AppView appView;
    private Player player;
    private Date gameClock;
    private GameMap gMap;
    private Market market;

    public MarketController(AppView appView, Player player , Market market) {
        this.appView = appView;
        this.player = player;
        this.gameClock = new Date();
        this.gMap = new GameMap();
        this.market = market;
    }

    @Override
    public Result update(String input) {
        MarketMenuCommands command = MarketMenuCommands.getCommand(input);
        String[] args = command.parseInput(input);
        switch (command) {
            case ShowAllProducts -> showAllProducts();
            case ShowAllAvailableProducts -> showAllAvailableProducts();
            case Purchase -> purchase(args);
            case CheatAddDollars -> cheatAddDollars(args);
            case None -> Result.error("Invalid input");
        }
        return Result.success("Command executed successfully");
    }

    private void showAllProducts() {
        market.showAllProducts();
    }

    private void showAllAvailableProducts() {
    }

    private void purchase(String[] args) {
    }

    private void cheatAddDollars(String[] args) {
    }
}
