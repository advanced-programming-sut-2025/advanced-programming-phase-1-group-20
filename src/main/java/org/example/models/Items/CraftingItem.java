package org.example.models.Items;


import org.example.models.App;
import org.example.models.Player.Backpack;
import org.example.models.enums.Types.*;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CraftingItem extends Item {
    private CraftingType type;


    public CraftingItem(CraftingType type) {
        super(type.getName(), type.getBaseSellPrice());
        this.type = type;

    }

    public String getIngredients() {
        return type.getIngredients();
    }


    public String getSource() {
        return type.getSource();
    }


    public boolean canCraft(Backpack inventory) {
        Map<Item, Integer> items = inventory.getInventory();
        String[] parts = type.getIngredients().split("\\+");
        for (String part : parts) {
            part = part.trim();
            String[] itemData = part.split(" ", 2);

            int requiredItem = Integer.parseInt(itemData[0]);
            String itemName = itemData[1];
            itemName = itemName.trim();
            Item item = App.getItem(itemName);
            if (!items.containsKey(item) || requiredItem > items.get(item)) {
                return false;
            }
        }
        return true;
    }

    public ArtisanItem createArtisan(String items) {
        if (type.getName().equals("Bee House")) {
            return new ArtisanItem(ArtisanType.Honey);
        }
        else if (type.getName().equals("Cheese Press")) {
            String[] regexes = new String[]{"^\\s*1\\s+Milk\\s*$", "^\\s*1\\s+Large\\s+Milk\\s*$",
                    "^\\s*1\\s+Goat\\s+Milk\\s*$", "^\\s*1\\s+Large\\s+Goat\\s+Milk\\s*$"};
            if (items.matches(regexes[0])) {
                return new ArtisanItem(ArtisanType.Cheese);
            } else if(items.matches(regexes[1])){
                ArtisanItem artisanItem = new ArtisanItem(ArtisanType.Cheese);
                artisanItem.setBaseSellPrice(345);
                return artisanItem;
            } else if(items.matches(regexes[2])){
                return new ArtisanItem(ArtisanType.GoatCheese);
            }  else if (items.matches(regexes[3])) {
                ArtisanItem artisanItem = new ArtisanItem(ArtisanType.GoatCheese);
                int price = 600;
                artisanItem.setBaseSellPrice(price);
                return artisanItem;
            }
        }
        else if (type.getName().equals("Keg")) {
            String[] regexes = new String[]{"^\\s*1\\s+Wheat\\s*$", "^\\s*1\\s+Rice\\s*$", // beer and vinegar
                    "^\\s*1\\s+Honey\\s*$", "^\\s*1\\s+Hops\\s*$", //Mead , Pale Ale
                    "^\\s*5\\s+Coffee Bean\\s*$", // Coffee
                    //juices : from 5 to 26
                    "^\\s*1\\s+Amaranth\\s*$", "^\\s*1\\s+Artichoke\\s*$",
                    "^\\s*1\\s+Beet\\s*$", "^\\s*1\\s+Bok Choy\\s*$", "^\\s*1\\s+Broccoli\\s*$",
                    "^\\s*1\\s+Carrot\\s*$", "^\\s*1\\s+Cauliflower\\s*$", "^\\s*1\\s+Corn\\s*$",
                    "^\\s*1\\s+Eggplant\\s*$", "^\\s*1\\s+Garlic\\s*$", "^\\s*1\\s+Green Bean\\s*$",
                    "^\\s*1\\s+Kale\\s*$", "^\\s*1\\s+Parsnip\\s*$", "^\\s*1\\s+Potato\\s*$",
                    "^\\s*1\\s+Pumpkin\\s*$", "^\\s*1\\s+Radish\\s*$", "^\\s*1\\s+Red Cabbage\\s*$",
                    "^\\s*1\\s+Summer Squash\\s*$", "^\\s*1\\s+Taro Root\\s*$",
                    "^\\s*1\\s+Tea Leaves\\s*$", "^\\s*1\\s+Tomato\\s*$", "^\\s*1\\s+Unmilled Rice\\s*$",
                    //Wine : from 27 to 52
                    "^\\s*1\\s+Ancient Fruit\\s*$", "^\\s*1\\s+Apple\\s*$", "^\\s*1\\s+Apricot\\s*$",
                    "^\\s*1\\s+Banana\\s*$", "^\\s*1\\s+Blackberry\\s*$", "^\\s*1\\s+Blueberry\\s*$",
                    "^\\s*1\\s+Cactus Fruit\\s*$", "^\\s*1\\s+Cherry\\s*$", "^\\s*1\\s+Coconut\\s*$",
                    "^\\s*1\\s+Cranberries\\s*$", "^\\s*1\\s+Crystal Fruit\\s*$", "^\\s*1\\s+Grape\\s*$",
                    "^\\s*1\\s+Hot Pepper\\s*$", "^\\s*1\\s+Mango\\s*$", "^\\s*1\\s+Melon\\s*$",
                    "^\\s*1\\s+Orange\\s*$", "^\\s*1\\s+Peach\\s*$", "^\\s*1\\s+Pineapple\\s*$",
                    "^\\s*1\\s+Pomegranate\\s*$", "^\\s*1\\s+Powdermelon\\s*$", "^\\s*1\\s+Rhubarb\\s*$",
                    "^\\s*1\\s+Salmonberry\\s*$", "^\\s*1\\s+Spice Berry\\s*$", "^\\s*1\\s+Starfruit\\s*$",
                    "^\\s*1\\s+Strawberry\\s*$", "^\\s*1\\s+Wild Plum\\s*$"};
            if (items.matches(regexes[0])) {
                return new ArtisanItem(ArtisanType.Beer);
            } else if (items.matches(regexes[1])) {
                return new ArtisanItem(ArtisanType.Vinegar);
            } else if (items.matches(regexes[2])) {
                return new ArtisanItem(ArtisanType.Mead);
            } else if (items.matches(regexes[3])) {
                return new ArtisanItem(ArtisanType.PaleAle);
            } else if (items.matches(regexes[4])) {
                return new ArtisanItem(ArtisanType.Coffee);
            }
            for (int i = 4; i < 27; i++) {
                Pattern pattern = Pattern.compile(regexes[i]);
                Matcher matcher = pattern.matcher(items);

                if (matcher.matches()) {
                    String name = matcher.group(1);
                    PlantType type = PlantType.fromName(name);
                    int energy = type.getEnergy() * 2;
                    //TODO : base Sell Price must change later
                    int baseSellPrice = (int) (type.getBaseSellPrice() * 2.25);
                    ArtisanItem artisanItem = new ArtisanItem(ArtisanType.Juice);
                    artisanItem.setEnergy(energy);
                    artisanItem.setBaseSellPrice(baseSellPrice);
                    return artisanItem;
                }
            }

            for (int i = 26; i < 53; i++) {
                Pattern pattern = Pattern.compile(regexes[i]);
                Matcher matcher = pattern.matcher(items);

                if (matcher.matches()) {
                    String name = matcher.group(1);
                    PlantType type = PlantType.fromName(name);
                    int energy = (int) (type.getEnergy() * 1.75);
                    //TODO : base Sell Price must change later
                    int baseSellPrice = (int) (type.getBaseSellPrice() * 3);
                    ArtisanItem artisanItem = new ArtisanItem(ArtisanType.Wine);
                    artisanItem.setEnergy(energy);
                    artisanItem.setBaseSellPrice(baseSellPrice);
                    return artisanItem;
                }
            }
        }
        else if (type.getName().equals("Dehydrator")) {
            String[] regexes = new String[]{ //Raisins
                    "^\\s*5\\s+Grape\\s*$",
                    //Dried Mushrooms
                    "^\\s*5\\s+Chanterelle\\s*$", "^\\s*5\\s+Common Mushroom\\s*$", "^\\s*5\\s+Morel\\s*$",
                    "^\\s*5\\s+Red Mushroom\\s*$", "^\\s*5\\s+Purple Mushroom\\s*$",
                    // Dried Fruit
                    "^\\s*5\\s+Ancient Fruit\\s*$", "^\\s*5\\s+Apple\\s*$", "^\\s*5\\s+Apricot\\s*$",
                    "^\\s*5\\s+Banana\\s*$", "^\\s*5\\s+Blackberry\\s*$", "^\\s*5\\s+Blueberry\\s*$",
                    "^\\s*5\\s+Cactus Fruit\\s*$", "^\\s*5\\s+Cherry\\s*$", "^\\s*5\\s+Coconut\\s*$",
                    "^\\s*5\\s+Cranberries\\s*$", "^\\s*5\\s+Crystal Fruit\\s*$",
                    "^\\s*5\\s+Hot Pepper\\s*$", "^\\s*5\\s+Mango\\s*$", "^\\s*5\\s+Melon\\s*$",
                    "^\\s*5\\s+Orange\\s*$", "^\\s*5\\s+Peach\\s*$", "^\\s*5\\s+Pineapple\\s*$",
                    "^\\s*5\\s+Pomegranate\\s*$", "^\\s*5\\s+Powdermelon\\s*$", "^\\s*5\\s+Rhubarb\\s*$",
                    "^\\s*5\\s+Salmonberry\\s*$", "^\\s*5\\s+Spice Berry\\s*$", "^\\s*5\\s+Starfruit\\s*$",
                    "^\\s*5\\s+Strawberry\\s*$", "^\\s*5\\s+Wild Plum\\s*$",
            };
            if (items.matches(regexes[0])) {
                return new ArtisanItem(ArtisanType.Raisins);
            }
            for (int i = 1; i < 6; i++) {
                Pattern pattern = Pattern.compile(regexes[i]);
                Matcher matcher = pattern.matcher(items);

                if (matcher.matches()) {
                    CropType cropType = CropType.fromName(matcher.group(1));
                    int price = (int) (cropType.getBaseSellPrice() * 7.5) + 25;
                    ArtisanItem artisanItem = new ArtisanItem(ArtisanType.DriedMushrooms);
                    artisanItem.setBaseSellPrice(price);
                    return artisanItem;
                }
            }
            for (int i = 6; i < 31; i++) {
                Pattern pattern = Pattern.compile(regexes[i]);
                Matcher matcher = pattern.matcher(items);
                if (matcher.matches()) {
                    String name = matcher.group(1);
                    PlantType type = PlantType.fromName(name);
                    int baseSellPrice = (int) (type.getBaseSellPrice() * 7.5) + 25;
                    ArtisanItem artisanItem = new ArtisanItem(ArtisanType.DriedFruit);
                    artisanItem.setBaseSellPrice(baseSellPrice);
                    return artisanItem;
                }
            }
        }
        else if (type.getName().equals("Charcoal Klin")) {
            String regex = "^\\s*10\\s+Wood\\s*$";
            if (items.matches(regex)) {
                return new ArtisanItem(ArtisanType.Coal);
            }
        }
        else if (type.getName().equals("Preserves Jar")) {
            String[] regexes = new String[]{
                    //Vegetables : (Pickles)
                    "^\\s*1\\s+Amaranth\\s*$", "^\\s*1\\s+Artichoke\\s*$",
                    "^\\s*1\\s+Beet\\s*$", "^\\s*1\\s+Bok Choy\\s*$", "^\\s*1\\s+Broccoli\\s*$",
                    "^\\s*1\\s+Carrot\\s*$", "^\\s*1\\s+Cauliflower\\s*$", "^\\s*1\\s+Corn\\s*$",
                    "^\\s*1\\s+Eggplant\\s*$", "^\\s*1\\s+Garlic\\s*$", "^\\s*1\\s+Green Bean\\s*$",
                    "^\\s*1\\s+Kale\\s*$", "^\\s*1\\s+Parsnip\\s*$", "^\\s*1\\s+Potato\\s*$",
                    "^\\s*1\\s+Pumpkin\\s*$", "^\\s*1\\s+Radish\\s*$", "^\\s*1\\s+Red Cabbage\\s*$",
                    "^\\s*1\\s+Summer Squash\\s*$", "^\\s*1\\s+Taro Root\\s*$",
                    "^\\s*1\\s+Tea Leaves\\s*$", "^\\s*1\\s+Tomato\\s*$", "^\\s*1\\s+Unmilled Rice\\s*$",
                    //Fruits : (Jelly)
                    "^\\s*1\\s+Ancient Fruit\\s*$", "^\\s*1\\s+Apple\\s*$", "^\\s*1\\s+Apricot\\s*$",
                    "^\\s*1\\s+Banana\\s*$", "^\\s*1\\s+Blackberry\\s*$", "^\\s*1\\s+Blueberry\\s*$",
                    "^\\s*1\\s+Cactus Fruit\\s*$", "^\\s*1\\s+Cherry\\s*$", "^\\s*1\\s+Coconut\\s*$",
                    "^\\s*1\\s+Cranberries\\s*$", "^\\s*1\\s+Crystal Fruit\\s*$", "^\\s*1\\s+Grape\\s*$",
                    "^\\s*1\\s+Hot Pepper\\s*$", "^\\s*1\\s+Mango\\s*$", "^\\s*1\\s+Melon\\s*$",
                    "^\\s*1\\s+Orange\\s*$", "^\\s*1\\s+Peach\\s*$", "^\\s*1\\s+Pineapple\\s*$",
                    "^\\s*1\\s+Pomegranate\\s*$", "^\\s*1\\s+Powdermelon\\s*$", "^\\s*1\\s+Rhubarb\\s*$",
                    "^\\s*1\\s+Salmonberry\\s*$", "^\\s*1\\s+Spice Berry\\s*$", "^\\s*1\\s+Starfruit\\s*$",
                    "^\\s*1\\s+Strawberry\\s*$", "^\\s*1\\s+Wild Plum\\s*$"};
            for (int i = 0; i < 23; i++) {
                Pattern pattern = Pattern.compile(regexes[i]);
                Matcher matcher = pattern.matcher(items);
                if (matcher.matches()) {
                    String name = matcher.group(1);
                    PlantType plantType = PlantType.fromName(name);
                    int energy = (int) (plantType.getEnergy() * 1.75);
                    int baseSellPrice = (int) (plantType.getBaseSellPrice() * 2) + 50;
                    ArtisanItem artisanItem = new ArtisanItem(ArtisanType.Pickles);
                    artisanItem.setBaseSellPrice(baseSellPrice);
                    artisanItem.setEnergy(energy);
                    return artisanItem;
                }
            }
            for (int i = 23; i < 49; i++) {
                Pattern pattern = Pattern.compile(regexes[i]);
                Matcher matcher = pattern.matcher(items);
                if (matcher.matches()) {
                    String name = matcher.group(1);
                    PlantType plantType = PlantType.fromName(name);
                    int energy = (int) (plantType.getEnergy() * 2);
                    int baseSellPrice = (int) (plantType.getBaseSellPrice() * 2) + 50;
                    ArtisanItem artisanItem = new ArtisanItem(ArtisanType.Jelly);
                    artisanItem.setBaseSellPrice(baseSellPrice);
                    artisanItem.setEnergy(energy);
                    return artisanItem;
                }
            }
        }
        else if (type.getName().equals("Fish Smoker")) {
            //TODO : when fishes are ready.
        }
        else if (type.getName().equals("Furnace")) {
            String[] regexes = new String[]{
                    "^\\s*1\\s+Iron\\s+1\\s+Coal\\s*$", "^\\s*1\\s+Copper\\s+1\\s+Coal\\s*$",
                    "^\\s*1\\s+Gold\\s+1\\s+Coal\\s*$", "^\\s*1\\s+Iridium\\s+1\\s+Coal\\s*$"};
            if (items.matches(regexes[0])) {
                ArtisanItem artisanItem = new ArtisanItem(ArtisanType.IronBar);
                MineralType type = MineralType.Iron;
                int baseSellPrice = (int) (type.getBaseSellPrice() * 10);
                artisanItem.setBaseSellPrice(baseSellPrice);
                return artisanItem;
            } else if (items.matches(regexes[1])) {
                ArtisanItem artisanItem = new ArtisanItem(ArtisanType.CopperBar);
                MineralType type = MineralType.Copper;
                int baseSellPrice = (int) (type.getBaseSellPrice() * 10);
                artisanItem.setBaseSellPrice(baseSellPrice);
                return artisanItem;
            } else if (items.matches(regexes[2])) {
                ArtisanItem artisanItem = new ArtisanItem(ArtisanType.GoldBar);
                MineralType type = MineralType.Gold;
                int baseSellPrice = (int) (type.getBaseSellPrice() * 10);
                artisanItem.setBaseSellPrice(baseSellPrice);
                return artisanItem;
            } else if (items.matches(regexes[3])) {
                ArtisanItem artisanItem = new ArtisanItem(ArtisanType.IridiumBar);
                MineralType type = MineralType.Iridium;
                int baseSellPrice = (int) (type.getBaseSellPrice() * 10);
                artisanItem.setBaseSellPrice(baseSellPrice);
                return artisanItem;
            }
        }
        else if(type.getName().equals("Loom")) {
            String regex = "^\\s*1\\s+Wool\\s*$";
            if (items.matches(regex)) {
                return new ArtisanItem(ArtisanType.Cloth);
            }
        }
        else if(type.getName().equals("Mayonnaise Machine")) {
            String[] regexes = new String[]{"^\\s*1\\s+Egg\\s*$" , "^\\s*1\\s+Large Egg\\s*$",
            "^\\s*1\\s+Duck Egg\\s*$" , "^\\s*1\\s+Dinosaur Egg\\s*$"};
            if (items.matches(regexes[0])) {
                return new ArtisanItem(ArtisanType.Mayonnaise);
            }else if(items.matches(regexes[1])) {
                ArtisanItem artisanItem = new ArtisanItem(ArtisanType.Mayonnaise);
                int price = 237;
                artisanItem.setBaseSellPrice(price);
                return artisanItem;
            } else if (items.matches(regexes[2])) {
                return new ArtisanItem(ArtisanType.DuckMayonnaise);
            } else if (items.matches(regexes[3])) {
                return new ArtisanItem(ArtisanType.DinosaurMayonnaise);
            }
        }
        else if(type.getName().equals("Oil Maker")) {
            String[] regexes = new String[]{"^\\s*1\\s+Truffle\\s*$" , "^\\s*1\\s+Corn\\s*$",
                                "^\\s*1\\s+Sunflower Seeds\\s*$" , "^\\s*1\\s+Sunflower\\s*$"};
            if (items.matches(regexes[0])) {
                return new ArtisanItem(ArtisanType.TruffleOil);
            }else if(items.matches(regexes[1])) {
                return new ArtisanItem(ArtisanType.Oil);
            }else if(items.matches(regexes[2])) {
                ArtisanItem artisanItem = new ArtisanItem(ArtisanType.Oil);
                int proccessingTime = 2*24;
                artisanItem.setProccessingTime(proccessingTime);
                return artisanItem;
            }else if(items.matches(regexes[3])) {
                ArtisanItem artisanItem = new ArtisanItem(ArtisanType.Oil);
                int proccessingTime = 1;
                artisanItem.setProccessingTime(proccessingTime);
                return artisanItem;
            }
        }
        return null;
    }


    @Override
    public void showInfo() {
        type.showInfo();
    }
}
