package org.example.client.controllers;

import org.example.common.models.App;
import org.example.common.models.Barn;
import org.example.common.models.Coop;
import org.example.common.models.Items.Item;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.Market;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Location;
import org.example.common.models.common.Result;
import org.example.common.models.entities.animal.Animal;
import org.example.common.models.entities.animal.BarnAnimal;
import org.example.common.models.entities.animal.CoopAnimal;
import org.example.common.models.enums.Types.CoopAnimalTypes;
import org.example.common.models.enums.Types.BarnAnimalTypes;
import org.example.common.models.enums.Types.BarnTypes;
import org.example.common.models.enums.Types.Cages;


public class MarketController implements Controller {
    private Player player;
    private Market market;

    public MarketController(Player player, Market market) {
        this.player = player;
        this.market = market;
        market.initializeTotalStock(App.getGame().getDate().getSeason());
    }

    @Override
    public void setupListeners() {}

    private void showAllProducts() {
        market.showAllProducts();
    }

    private void showAllAvailableProducts() {
        market.showAvailableProducts(App.getGame().getDate().getSeason());
    }

    public Result purchase(String[] args) {
        String productName = args[0];
        double count = Double.parseDouble(args[1]);
        Item item = market.getItem(productName);

        if (item == null) {
            return Result.error("There is no such item as" + productName);
        }
        Animal animal = getAnimalByName(item.getName());
        Farm farm = App.getGame().getGameMap().getFarmByPlayer(player);

        if (!market.containsItem(item, count)) {
            return Result.error("Item not in stock");
        }

        if (!market.checkItem(player, item, count)) {
            return Result.error("You don't have enough resources for this product");
        }

        if (market.getName().equals("Marnie Shop")) {
            if (!(item.getName().equals("Shears") || item.getName().equals("Milk Pail"))) {
                if (animal instanceof BarnAnimal) {
                    Barn barn = farm.getBarnByAnimal((BarnAnimal) animal);
                    if (barn == null) {
                        return Result.error("there is no farm for add animal");
                    }
                    barn.addAnimal((BarnAnimal) animal);
                }
                else if (animal instanceof CoopAnimal) {
                    Coop coop = farm.getCoopByAnimal((CoopAnimal) animal);
                    if (coop == null) {
                        return Result.error("there is no coop for add animal");
                    }
                    coop.addAnimal((CoopAnimal) animal);
                }
            }
        }
        market.checkOut(player, item, count);
        player.getBackpack().add(item, (int) count);


        return Result.success("Item purchased successfully");
    }

    private Animal getAnimalByName(String name) {
        Animal animal = null;
        switch (name) {
            case "Pig"-> {
                animal = new BarnAnimal(BarnAnimalTypes.PIG , "Pig");
            }
            case "Sheep"-> {
                animal = new BarnAnimal(BarnAnimalTypes.SHEEP , "Sheep");
            }
            case "Cow" -> {
                animal = new BarnAnimal(BarnAnimalTypes.COW , "Cow");
            }
            case "Goat" -> {
                animal = new BarnAnimal(BarnAnimalTypes.GOAT , "Goat");
            }
            case "Chicken" -> {
                animal = new CoopAnimal(CoopAnimalTypes.CHICKEN, "Chicken");
            }
            case "Duck" -> {
                animal = new CoopAnimal(CoopAnimalTypes.DUCK, "Duck");
            }
            case "Rabbit" -> {
                animal = new CoopAnimal(CoopAnimalTypes.RABBIT, "Rabbit");
            }
            case "Dinosaur" -> {
                animal = new CoopAnimal(CoopAnimalTypes.DINOSAUR, "Dinosaur");
            }
        }
        return animal;
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
            boolean success = player.upgradeTool(toolName);

            if (success) {
                return Result.success("Tool " + toolName + " upgraded successfully");
            } else {
                return Result.error("Failed to upgrade tool " + toolName);
            }
        }
        return Result.error("You are not in Black Smith!");
    }

    public Result build(String[] args) {
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
        //TODO : change this
//        appView.navigateMenu(new GameMenu(appView, player.getUser(), player));
    }

}
