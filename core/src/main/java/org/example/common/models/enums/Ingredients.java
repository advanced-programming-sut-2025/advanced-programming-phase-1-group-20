package org.example.common.models.enums;

import org.example.common.models.Items.Item;
import org.example.common.models.Player.Backpack;
import org.example.common.models.enums.Types.ItemBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum Ingredients {
    // Crafting Recipes
    CherryBomb(Map.of("Copper", 4, "Coal", 1)),
    Bomb(Map.of("Iron", 4, "Coal", 1)),
    MegaBomb(Map.of("Gold", 4, "Coal", 1, "Iridium", 1)),
    Sprinkler(Map.of("Copper", 1, "Iron", 1)),
    QualitySprinkler(Map.of("Iron", 1, "Gold", 1)),
    IridiumSprinkler(Map.of("Gold", 1, "Iridium", 1)),
    CharcoalKiln(Map.of("Wood", 20, "Copper", 2)),
    Furnace(Map.of("Copper", 20, "Stone", 25)),
    Scarecrow(Map.of("Wood", 50, "Coal", 1, "Fiber", 20)),
    DeluxeScarecrow(Map.of("Wood", 50, "Iridium", 1, "Fiber", 40)),
    BeeHouse(Map.of("Wood", 40, "Coal", 8, "Iron", 1)),
    CheesePress(Map.of("Wood", 45, "Stone", 45, "Copper", 1)),
    Keg(Map.of("Wood", 30, "Copper", 1, "Iron", 1)),
    Loom(Map.of("Wood", 60, "Fiber", 30)),
    MayonnaiseMachine(Map.of("Wood", 15, "Stone", 15, "Copper", 1)),
    OilMaker(Map.of("Wood", 100, "Gold", 1, "Iron", 1)),
    PreservesJar(Map.of("Wood", 50, "Stone", 40, "Coal", 8)),
    Dehydrator(Map.of("Wood", 30, "Stone", 20, "Fiber", 30)),
    FishSmoker(Map.of("Wood", 50, "Iron", 3, "Coal", 10)),

    // Cooking Recipes
    FriedEgg(Map.of("Egg", 1)),
    BakedFish(Map.of("Sardine", 1, "Salmon", 1, "Wheat", 1)),
    Salad(Map.of("Leek", 1, "Dandelion", 1)),
    Omelet(Map.of("Egg", 1, "Milk", 1)),
    PumpkinPie(Map.of("Pumpkin", 1, "Wheat Flour", 1, "Milk", 1, "Sugar", 1)),
    Spaghetti(Map.of("Wheat Flour", 1, "Tomato", 1)),
    Pizza(Map.of("Wheat Flour", 1, "Tomato", 1, "Cheese", 1)),
    Tortilla(Map.of("Corn", 1)),
    MakiRoll(Map.of("Any Fish", 1, "Rice", 1, "Fiber", 1)),
    TripleShotEspresso(Map.of("Coffee", 3)),
    Cookie(Map.of("Wheat Flour", 1, "Sugar", 1, "Egg", 1)),
    Hashbrowns(Map.of("Potato", 1, "Oil", 1)),
    Pancakes(Map.of("Wheat Flour", 1, "Egg", 1)),
    FruitSalad(Map.of("Blueberry", 1, "Melon", 1, "Apricot", 1)),
    RedPlate(Map.of("Red Cabbage", 1, "Radish", 1)),
    Bread(Map.of("Wheat Flour", 1)),
    SalmonDinner(Map.of("Salmon", 1, "Amaranth", 1, "Kale", 1)),
    VegetableMedley(Map.of("Tomato", 1, "Beet", 1)),
    FarmersLunch(Map.of("Omelet", 1, "Parsnip", 1)),
    SurvivalBurger(Map.of("Bread", 1, "Carrot", 1, "Eggplant", 1)),
    DishOTheSea(Map.of("Sardines", 2, "Hashbrowns", 1)),
    SeafoamPudding(Map.of("Flounder", 1, "Midnight Carp", 1)),
    MinersTreat(Map.of("Carrot", 2, "Sugar", 1, "Milk", 1)),

    // Artisan Good Recipes
    // Note: For items with multiple options (e.g., "Any Fruit"), the complex checking
    // logic remains in your CraftingItem class. This enum stores the basic recipe.
    Honey(Map.of()), // Honey is a special case, often without direct ingredients
    Cheese(Map.of("Milk", 1)),
    GoatCheese(Map.of("Goat Milk", 1)),
    Beer(Map.of("Wheat", 1)),
    Vinegar(Map.of("Rice", 1)),
    Coffee(Map.of("Coffee Bean", 5)),
    Juice(Map.of("Any Vegetable", 1)),
    Mead(Map.of("Honey", 1)),
    PaleAle(Map.of("Hops", 1)),
    Wine(Map.of("Any Fruit", 1)),
    DriedMushrooms(Map.of("Any Mushroom", 5)),
    DriedFruit(Map.of("Any Fruit", 5)),
    Raisins(Map.of("Grape", 5)),
    Charcoal(Map.of("Wood", 10)),
    Pickles(Map.of("Any Vegetable", 1)),
    Jelly(Map.of("Any Fruit", 1)),
    SmokedFish(Map.of("Any Fish", 1, "Coal", 1)),
    IronBar(Map.of("Iron", 1, "Coal", 1)),
    CopperBar(Map.of("Copper", 1, "Coal", 1)),
    GoldBar(Map.of("Gold", 1, "Coal", 1)),
    IridiumBar(Map.of("Iridium", 1, "Coal", 1)),
    Cloth(Map.of("Wool", 1)),
    Mayonnaise(Map.of("Egg", 1)),
    DuckMayonnaise(Map.of("Duck Egg", 1)),
    DinosaurMayonnaise(Map.of("Dinosaur Egg", 1)),
    TruffleOil(Map.of("Truffle", 1)),
    Oil(Map.of("Corn", 1)),


    //Markets
    NoSpecialItem(Map.of()),
    Barn(Map.of("Wood", 350 , "Stone" , 150)),
    BigBarn(Map.of("Wood", 450, "Stone" ,200)),
    DeluxeBarn(Map.of("Wood" ,  550 , "Stone" ,300)),
    Coop(Map.of("Wood", 300 , "Stone" ,100)),
    BigCoop(Map.of("Wood" ,  400 , "Stone" ,150)),
    DeluxeCoop(Map.of("Wood" ,  500 , "Stone" ,200)),
    Well(Map.of("Stone" ,75)),
    ShippingBin(Map.of("Wood", 150)),
    ;

    private final Map<String, Integer> recipe;

    Ingredients(Map<String, Integer> recipe) {
        this.recipe = recipe;
    }

    public Map<String, Integer> getRecipe() {
        return recipe;
    }

    public boolean checkRecipe(Backpack backpack) {
        Map<String , Integer> itemsNeeded = new HashMap<>();
        if(!recipe.isEmpty()){
            for(Map.Entry<String, Integer> entry : this.recipe.entrySet()) {
                String itemName = entry.getKey();
                int requiredAmount = entry.getValue();
                if(itemName.equals("Any Fruit")) {
                    List<Item> fruits = ItemBuilder.getFruits();
                    String fruit = hasItem(fruits , backpack);
                    if(fruit == null) {
                        return false;
                    }else{
                        if (backpack.getNumberOfItem(fruit) < requiredAmount) {
                            return false;
                        }
                    }
                    itemsNeeded.put(fruit, requiredAmount);
                }else if(itemName.equals("Any Vegetable")) {
                    List<Item> vegetables = ItemBuilder.getVegetables();
                    String vegetable = hasItem(vegetables , backpack);
                    if(vegetable == null) {
                        return false;
                    }else {
                        if (backpack.getNumberOfItem(vegetable) < requiredAmount) {
                            return false;
                        }
                    }
                    itemsNeeded.put(vegetable, requiredAmount);
                }else if(itemName.equals("Any Mushroom")) {
                    List<Item> mushrooms = ItemBuilder.getMushrooms();
                    String mushroom = hasItem(mushrooms , backpack);
                    if(mushroom == null) {
                        return false;
                    }else {
                        if (backpack.getNumberOfItem(mushroom) < requiredAmount) {
                            return false;
                        }
                    }
                    itemsNeeded.put(mushroom, requiredAmount);
                }else if(itemName.equals("Any Fish")) {
                    List<Item> fishes = ItemBuilder.getFishes();
                    String fish = hasItem(fishes , backpack);
                    if(fish == null) {
                        return false;
                    }else{
                        if (backpack.getNumberOfItem(fish) < requiredAmount) {
                            return false;
                        }
                    }
                    itemsNeeded.put(fish, requiredAmount);
                }else {
                    if(!backpack.hasItems(List.of(itemName))) {
                        return false;
                    }else{
                        if (backpack.getNumberOfItem(itemName) < requiredAmount) {
                            return false;
                        }
                    }
                    itemsNeeded.put(itemName, requiredAmount);
                }
            }
        }


        if(!itemsNeeded.isEmpty()) {
            for(Map.Entry<String, Integer> entry : itemsNeeded.entrySet()) {
                String itemName = entry.getKey();
                int amountToRemove = entry.getValue();

                Item itemInstance = backpack.getItem(itemName); // Get an instance of the item to remove

                if (itemInstance != null) {
                    backpack.remove(itemInstance, amountToRemove);
                }
            }
        }
        return true;
    }


    private String hasItem(List<Item> items, Backpack backpack) {
        for(Item item : items) {
            if(backpack.hasItems(List.of(item.getName()))) {
                return item.getName();
            }
        }
        return null;
    }
}
