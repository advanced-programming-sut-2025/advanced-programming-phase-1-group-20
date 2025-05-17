package org.example.controllers;

import org.example.models.App;
import org.example.models.Barn;
import org.example.models.Coop;
import org.example.models.Items.Item;
import org.example.models.MapDetails.Farm;
import org.example.models.Market;
import org.example.models.Player.Player;
import org.example.models.common.Location;
import org.example.models.common.Result;
import org.example.models.enums.Types.BarnTypes;
import org.example.models.enums.Types.Cages;
import org.example.models.enums.commands.MarketMenuCommands;
import org.example.views.AppView;
import org.example.views.GameMenu;

public class MarketController implements Controller {
    private AppView appView;
    private Player player;
    private Market market;

    public MarketController(AppView appView, Player player, Market market) {
        this.appView = appView;
        this.player = player;
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
        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);
        Location location = App.getGame().getGameMap().getFarmByPlayer(player).getItem(x,y);
        if (!market.getName().equalsIgnoreCase("Carpenters Shop")) {
            return Result.error("You are not in Carpenters Shop!");
        }

        if(!buildBarn(buildingName)) {
            return Result.error("You can't build barn because you don't have enough resources!");
        }
        Barn barn = getBarnByName(buildingName, location);
        Coop coop = getCoopByName(buildingName, location);
        Farm farm = App.getGame().getGameMap().getFarmByPlayer(player);
        if (barn != null && coop == null) {
            if (!farm.canBuild(barn.getX(), barn.getY(), barn.getWidth(), barn.getHeight())) {
                return Result.error("You can't build barn because you don't have enough space!");
            }
            farm.addBarn(barn);
        }
        else if (coop != null && barn == null) {
            if (!farm.canBuild(coop.getX(), coop.getY(), coop.getWidth(), coop.getHeight())) {
                return Result.error("You can't build coop because you don't have enough space!");
            }
            farm.addCoop(coop);
        }

        return Result.success("build successfully");
    }

    private Barn getBarnByName(String buildingName, Location location) {
        switch (buildingName) {
            case "Barn" -> {
                Barn newBarn = new Barn(BarnTypes.NORMAL_BARN, location, buildingName);
                return newBarn;
            }
            case "Big Barn" -> {
                Barn newBarn = new Barn(BarnTypes.BIG_BARN, location, buildingName);
                return newBarn;
            }
            case "Deluxe Barn" -> {
                Barn newBarn = new Barn(BarnTypes.DELUXE_BARN, location, buildingName);
                return newBarn;
            }
            default -> {
                return null;
            }
        }
    }

    private Coop getCoopByName(String buildingName, Location location) {
        switch (buildingName) {
            case "Coop" -> {
                Coop newCoop = new Coop(Cages.NORMAL_COOP, location, buildingName);
                return newCoop;
            }
            case "Big Coop" -> {
                Coop newCoop = new Coop(Cages.BIG_CAGE, location, buildingName);
                return newCoop;
            }
            case "Deluxe Coop" -> {
                Coop newCoop = new Coop(Cages.DELUXE_CAGE, location, buildingName);
                return newCoop;
            }
            default -> {
                return null;
            }
        }
    }

    private boolean buildBarn(String buildingName) {
        if(buildingName.equalsIgnoreCase("Barn")) {
            int money = player.getMoney();
            int woodCount = player.getBackpack().getNumberOfItem("Wood");
            int stone = player.getBackpack().getNumberOfItem("Stone");
            //todo : important note this is not manteghi... backpack doesn't have this much space change this if you want in tahvil.
            Item wood = player.getBackpack().getItem("Wood");
            Item stoneItem = player.getBackpack().getItem("Stone");
            if(money > 6000 && woodCount > 350 && stone > 150) {
                player.getBackpack().remove(stoneItem , stone);
                player.getBackpack().remove(wood , woodCount);
                player.decreaseMoney(money);
                return true;
            }
        }else if(buildingName.equalsIgnoreCase("Big Barn")) {
            int money = player.getMoney();
            int woodCount = player.getBackpack().getNumberOfItem("Wood");
            int stone = player.getBackpack().getNumberOfItem("Stone");
            Item wood = player.getBackpack().getItem("Wood");
            Item stoneItem = player.getBackpack().getItem("Stone");
            if(money > 12_000 && woodCount > 450 && stone > 200) {
                player.getBackpack().remove(stoneItem , stone);
                player.getBackpack().remove(wood , woodCount);
                player.decreaseMoney(money);
                return true;
            }
        }else if(buildingName.equalsIgnoreCase("Deluxe Barn")) {
            int money = player.getMoney();
            int woodCount = player.getBackpack().getNumberOfItem("Wood");
            int stone = player.getBackpack().getNumberOfItem("Stone");
            Item wood = player.getBackpack().getItem("Wood");
            Item stoneItem = player.getBackpack().getItem("Stone");
            if(money > 25_000 && woodCount > 550 && stone > 300) {
                player.getBackpack().remove(stoneItem , stone);
                player.getBackpack().remove(wood , woodCount);
                player.decreaseMoney(money);
                return true;
            }
        }else if(buildingName.equalsIgnoreCase("Coop")) {
            int money = player.getMoney();
            int woodCount = player.getBackpack().getNumberOfItem("Wood");
            int stone = player.getBackpack().getNumberOfItem("Stone");
            Item wood = player.getBackpack().getItem("Wood");
            Item stoneItem = player.getBackpack().getItem("Stone");
            if(money > 4000 && woodCount > 300 && stone > 300) {
                player.getBackpack().remove(stoneItem , stone);
                player.getBackpack().remove(wood , woodCount);
                player.decreaseMoney(money);
                return true;
            }
        }else if(buildingName.equalsIgnoreCase("Big Coop")) {
            int money = player.getMoney();
            int woodCount = player.getBackpack().getNumberOfItem("Wood");
            int stone = player.getBackpack().getNumberOfItem("Stone");
            Item wood = player.getBackpack().getItem("Wood");
            Item stoneItem = player.getBackpack().getItem("Stone");
            if(money > 10_000 && woodCount > 400 && stone > 150) {
                player.getBackpack().remove(stoneItem , stone);
                player.getBackpack().remove(wood , woodCount);
                player.decreaseMoney(money);
                return true;
            }
        }else if(buildingName.equalsIgnoreCase("Deluxe Coop")) {
            int money = player.getMoney();
            int woodCount = player.getBackpack().getNumberOfItem("Wood");
            int stone = player.getBackpack().getNumberOfItem("Stone");
            Item wood = player.getBackpack().getItem("Wood");
            Item stoneItem = player.getBackpack().getItem("Stone");
            if(money > 20_000 && woodCount > 500 && stone > 200) {
                player.getBackpack().remove(stoneItem , stone);
                player.getBackpack().remove(wood , woodCount);
                player.decreaseMoney(money);
                return true;
            }
        }else if(buildingName.equalsIgnoreCase("Well")) {
            int money = player.getMoney();
            int stone = player.getBackpack().getNumberOfItem("Stone");
            Item stoneItem = player.getBackpack().getItem("Stone");
            if(money > 1000 && stone > 70) {
                player.getBackpack().remove(stoneItem , stone);
                player.decreaseMoney(money);
                return true;
            }
        }else if(buildingName.equalsIgnoreCase("Shipping Bin")) {
            int money = player.getMoney();
            Item wood = player.getBackpack().getItem("Wood");
            int woodCount = player.getBackpack().getNumberOfItem("Wood");
            if(money > 250 && woodCount > 150){
                player.getBackpack().remove(wood , woodCount);
                player.decreaseMoney(money);
                return true;
            }
        }
        return false;
    }

    private void getOut() {
        System.out.println("You are out of market");
        appView.navigateMenu(new GameMenu(appView, player.getUser(), player));
    }

}
