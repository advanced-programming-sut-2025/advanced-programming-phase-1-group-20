package models.enums;

import models.*;
import models.Items.ArtisanItem;
import models.Items.CookingItem;
import models.enums.Types.ForagingType;

import java.util.HashMap;

public enum Markets {
    BLACKS_SMITH(creatBlackSmith()),
    JOJA_MART(createJojaMarket()),
    PIERRE_GENERAL_STORE(createPierreGeneralStore()),
    CARPENTERS_SHOP(createCarpentersShop()),
    FISH_SHOP(createFishShop()),
    MARNIE_SHOP(createMarnieShop()),
    STARDROP_SALOON(createStarDropSaloon());




    private final Market market;
    Markets(Market market) {
        this.market = market;
    }
    public Market createMarket() {
        return this.market;
    }

    private static Market creatBlackSmith() {
        HashMap<Item, Integer> items = new HashMap<>();
        items.put((Item)new Foraging(ForagingType.Copper), -1);
        items.put((Item)new Foraging(ForagingType.Iron), -1);
        items.put((Item)new Foraging(ForagingType.Coal), -1);
        items.put((Item)new Foraging(ForagingType.Gold), -1);
        // tools needed: i need them like items so it would be easy to handle
//        items.put(new Foraging(ForagingsType.Co), 1);
//        items.put(new Item("Iron Tool" , 5000), 1);
//        items.put(new Item("Gold Tool" , 10000), 1);
//        items.put(new Item("Iridium Tool" , 25000), 1);
//        items.put(new Item("Cooper Trash Can" , 1000), 1);
//        items.put(new Item("Steel Trash Can" , 2500), 1);
//        items.put(new Item("Gold Trash Can" , 2500), 1);
//        items.put(new Item("Iridium Trash Can" , 2500), 1);
        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[] {};
        return new Market(items , startHour,endHour,menu);
    }

    private static Market createJojaMarket() {
        HashMap<Item, Integer> items = new HashMap<>();

        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[] {};
        return new Market(items , startHour,endHour,menu);
    }
    private static Market createPierreGeneralStore() {
        HashMap<Item, Integer> items = new HashMap<>();
        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[] {};
        return new Market(items , startHour,endHour,menu);
    }
    private static Market createCarpentersShop() {
        HashMap<Item, Integer> items = new HashMap<>();

        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[] {};
        return new Market(items , startHour,endHour,menu);
    }

    private static Market createStarDropSaloon() {
        HashMap<Item, Integer> items = new HashMap<>();

        Item beer = (Item) new ArtisanItem(ArtisanItems.BEER);
        beer.setPrice(400);
        items.put(beer,-1);
        Item coffee = (Item) new ArtisanItem(ArtisanItems.COFFEE);
        coffee.setPrice(300);
        items.put(coffee,-1);
        Item pizza = (Item) new CookingItem(CookingItems.pizza);
        pizza.setPrice(600);
        items.put(pizza,-1);
        Item spaghetti = (Item) new CookingItem(CookingItems.spaghetti);
        spaghetti.setPrice(240);
        items.put(spaghetti,-1);
        Item bread = new CookingItem(CookingItems.bread);
        bread.setPrice(120);
        items.put(bread,-1);
        Item salad = (Item) new CookingItem(CookingItems.Salad);
        salad.setPrice(220);
        items.put(salad,-1);
        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[] {};
        return new Market(items , startHour,endHour,menu);
    }

    private static Market createMarnieShop() {
        HashMap<Item, Integer> items = new HashMap<>();

        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[] {};
        return new Market(items , startHour,endHour,menu);
    }

    private static Market createFishShop() {
        HashMap<Item, Integer> items = new HashMap<>();

        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[] {};
        return new Market(items , startHour,endHour,menu);
    }


}
