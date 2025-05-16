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
import org.example.views.GameMenu;

public class MarketController implements Controller {
    private AppView appView;
    private Player player;
    private GameMap gMap;
    private Market market;

    public MarketController(AppView appView, Player player, Market market) {
        this.appView = appView;
        this.player = player;
//        this.gMap = new GameMap(100, 100, player);
        // طول و عرض همینطوری گذاشته شده!
        this.market = market;
        market.initializeTotalStock(App.getGame().getDate().getSeason());
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
            case CheatAddDollars -> result = cheatAddDollars(args);
            case ToolUpgrade -> result = upgradeTool(args);
            case ShowCurrentMenu -> result = Result.success("Market menu");
            case Build -> result = build(args);

            case CheatGetOut -> getOut();
            case None -> result = Result.error("Invalid input");
        }

        appView.handleResult(result, command);
        return result;
    }

    private void showAllProducts() {
        market.showAllProducts();
    }

    private void showAllAvailableProducts() {
        market.showAvailableProducts(App.getGame().getDate().getSeason());
    }

    private Result purchase(String[] args) {
        String productName = args[0];
        double count = Double.parseDouble(args[1]);
        Item item = market.getItem(productName);

        if (item == null) {
            return Result.error("There is no such item as" + productName);
        }

        if (!market.containsItem(item, count)) {
            return Result.error("Item not in stock");
        }

        if (!market.checkItem(player, item, count)) {
            return Result.error("You don't have enough resources for this product");
        }
        market.checkOut(player, item, count);
        player.getBackpack().add(item, (int) count);


        return Result.success("Item purchased successfully");
    }


    private Result cheatAddDollars(String[] args) {
        int amount = Integer.parseInt(args[0]);
        player.increaseMoney(amount);
        return Result.success("Cheat successfully executed amount: " + amount + " dollars");
    }

    private Result upgradeTool(String[] args) {
        if (args == null || args.length < 1) {
            return Result.error("Tool name not specified");
        }

        if (market.getName().equalsIgnoreCase("Black Smith")) {
            String toolName = args[0];
            boolean success = player.upgradeTool(toolName, market);

            if (success) {
                return Result.success("Tool " + toolName + " upgraded successfully");
            } else {
                return Result.error("Failed to upgrade tool " + toolName);
            }
        }
        return Result.error("You are not in Black Smith!");
    }

    private Result build(String[] args) {
        String buildingName = args[0];
        return Result.success("build successfully");
    }

    private void getOut() {
        System.out.println("You are out of market");
        appView.navigateMenu(new GameMenu(appView , player.getUser() , player));
    }

}
