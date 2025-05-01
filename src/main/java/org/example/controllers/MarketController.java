package org.example.controllers;

import org.example.models.App;
import org.example.models.Items.Item;
import org.example.models.MapDetails.GameMap;
import org.example.models.Market;
import org.example.models.Player.Player;
import org.example.models.common.Date;
import org.example.models.common.Result;
import org.example.models.enums.commands.MarketMenuCommands;
import org.example.views.AppView;

public class MarketController implements Controller {
    private AppView appView;
    private App app;
    private Player player;
    private Date gameClock;
    private GameMap gMap;
    private Market market;

    public MarketController(AppView appView, App app, Player player, Market market) {
        this.appView = appView;
        this.player = player;
        this.gameClock = new Date();
        this.gMap = new GameMap(100, 100, player);
        // طول و عرض همینطوری گذاشته شده!
        this.market = market;
        this.app = app;
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
        market.showAvailableProducts(gameClock.getSeason());
    }

    private void purchase(String[] args) {
        String productName = args[0];
        double count = Double.parseDouble(args[1]);
        Item item = App.getItem(productName);
        boolean flag = checkItem(item) && market.containsItem(item, count, gameClock.getSeason());
        if (flag) {
            for (int i = 0; i < count; i++) {
                player.getBackpack().add(item);
                //TODO : handling money.
            }
        }
    }

    private boolean checkItem(Item item) {
        if (item == null) {
            System.out.println("Item does not exist");
            return false;
        }
        return true;
    }

    private void cheatAddDollars(String[] args) {
        int amount = Integer.parseInt(args[0]);
        //TODO : handling money.
    }
}
