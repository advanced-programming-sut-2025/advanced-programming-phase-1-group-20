package org.example.models.enums;


import org.example.models.App;
import org.example.models.Items.Item;
import org.example.models.Market;


import java.util.HashMap;

public enum Markets {
    BLACKS_SMITH(creatBlackSmith());
//    JOJA_MART(createJojaMarket()),
//    PIERRE_GENERAL_STORE(createPierreGeneralStore()),
//    CARPENTERS_SHOP(createCarpentersShop()),
//    FISH_SHOP(createFishShop()),
//    MARNIE_SHOP(createMarnieShop()),
//    STARDROP_SALOON(createStarDropSaloon());


    private final Market market;

    Markets(Market market) {
        this.market = market;
    }

    private static Market creatBlackSmith() {
        HashMap<Item, Integer> items = new HashMap<>();

//        items.put((Item) new Foraging(ForagingType.Copper), -1);
        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(items, startHour, endHour, menu);
    }

    private static Market createJojaMarket() {
        HashMap<Item, Integer> items = new HashMap<>();

        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(items, startHour, endHour, menu);
    }

    private static Market createPierreGeneralStore() {
        HashMap<Item, Integer> items = new HashMap<>();
        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(items, startHour, endHour, menu);
    }

    private static Market createCarpentersShop() {
        HashMap<Item, Integer> items = new HashMap<>();

        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(items, startHour, endHour, menu);
    }

    private static Market createMarnieShop() {
        HashMap<Item, Integer> items = new HashMap<>();

        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(items, startHour, endHour, menu);
    }

    private static Market createFishShop() {
        HashMap<Item, Integer> items = new HashMap<>();

        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(items, startHour, endHour, menu);
    }

    public Market createMarket() {
        return this.market;
    }


}
