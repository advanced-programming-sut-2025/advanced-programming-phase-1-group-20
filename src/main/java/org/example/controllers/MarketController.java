package org.example.controllers;

import org.example.models.App;
import org.example.models.Items.Item;
import org.example.models.MapDetails.GameMap;
import org.example.models.Market;
import org.example.models.Player.Player;
import org.example.models.common.Date;
import org.example.models.common.Result;
import org.example.models.enums.Types.ItemBuilder;
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
        // TODO: make the map more detailed
        // "Kasra" -> TODO: i wil add the colored map printing when this is handled
        this.market = market;
        market.initializeTotalStock(gameClock.getSeason());
        this.app = app;
    }

    @Override
    public Result update(String input) {
        MarketMenuCommands command = MarketMenuCommands.getCommand(input);
        String[] args = command.parseInput(input);
        Result result = null;

        switch (command) {
            case ShowAllProducts -> showAllProducts();
            case ShowAllAvailableProducts -> showAllAvailableProducts();
            case Purchase -> result = purchase(args);
            case CheatAddDollars -> cheatAddDollars(args);
            case None -> Result.error("Invalid input");
        }
        return result;
    }

    private void showAllProducts() {
        market.showAllProducts();
    }

    private void showAllAvailableProducts() {
        market.showAvailableProducts(gameClock.getSeason());
    }

    private Result purchase(String[] args) {
        String productName = args[0];
        double count = Double.parseDouble(args[1]);
        Item item = market.getItem(productName);

        if(item == null) {
            return Result.error("There is no such item as" + productName);
        }

        if(!market.containsItem(item , count)){
            return Result.error("Item not in stock");
        }

        if(!market.checkItem(player, item,count)) {
            return Result.error("You don't have enough resources for this product");
        }
        market.checkOut(player, item, count);
        player.getBackpack().add(item, (int) count);



        return Result.success("Item purchased successfully");
    }

    private void cheatAddDollars(String[] args) {
        int amount = Integer.parseInt(args[0]);
        player.increaseMoney(amount);
    }
}
