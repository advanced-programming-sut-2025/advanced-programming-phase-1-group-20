package org.example.models.enums;

import org.example.models.Items.Item;
import org.example.models.Items.Mineral;
import org.example.models.Items.Plant;
import org.example.models.Items.Seed;
import org.example.models.Market;
import org.example.models.enums.Types.MineralType;
import org.example.models.enums.Types.PlantType;
import org.example.models.enums.Types.SeedType;


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

    private static Market creatBlackSmith() {
        HashMap<Item, Double> items = new HashMap<>();
        HashMap<Item, Double> springItems = new HashMap<>();
        HashMap<Item, Double> summerItems = new HashMap<>();
        HashMap<Item, Double> autumnItems = new HashMap<>();
        HashMap<Item, Double> winterItems = new HashMap<>();

        Item cooper = new Mineral(MineralType.Copper);
        cooper.setPrice(75);
        items.put(cooper, Double.POSITIVE_INFINITY);

        Item iron = new Mineral(MineralType.Iron);
        iron.setPrice(150);
        items.put(iron, Double.POSITIVE_INFINITY);

        Item coal = new Mineral(MineralType.Coal);
        coal.setPrice(150);
        items.put(coal, Double.POSITIVE_INFINITY);

        Item gold = new Mineral(MineralType.Gold);
        gold.setPrice(400);
        items.put(gold, Double.POSITIVE_INFINITY);

        //TODO : tools need to be added later

        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(items , springItems , summerItems , autumnItems , winterItems, startHour, endHour, menu);
    }

    private static Market createJojaMarket() {
        HashMap<Item, Double> items = new HashMap<>();
        HashMap<Item, Double> springItems = new HashMap<>();
        HashMap<Item, Double> summerItems = new HashMap<>();
        HashMap<Item, Double> autumnItems = new HashMap<>();
        HashMap<Item, Double> winterItems = new HashMap<>();


        //Permanent Stock
        Item ancientSeed = new Seed(SeedType.AncientSeeds);
        ancientSeed.setPrice(500);
        items.put(ancientSeed, 1.0);

        Item sugar = new Item("sugar" , 125 , "Adds sweetness to pastries and candies. Too much can be unhealthy.");
        items.put(sugar, Double.POSITIVE_INFINITY);

        Item wheatFlour = new Item("wheat flour" , 125 , "A common cooking ingredient made from crushed wheat seeds.");
        items.put(wheatFlour, Double.POSITIVE_INFINITY);

        Item rice = new Item("rice" , 250 , "A basic grain often served under vegetables.");
        items.put(rice, Double.POSITIVE_INFINITY);

        //Spring Stock
        Item parsnipSeeds = new Plant(PlantType.Parsnip);
        parsnipSeeds.setPrice(25);
        items.put(parsnipSeeds, 5.0);

        Item beanStarter = new Seed(SeedType.BeanStarter);
        beanStarter.setPrice(75);
        items.put(beanStarter, 5.0);

        Item cauliflowerSeeds = new Seed(SeedType.CauliflowerSeeds);
        cauliflowerSeeds.setPrice(100);
        items.put(cauliflowerSeeds, 5.0);

        Item potatoSeeds = new Seed(SeedType.PotatoSeeds);
        potatoSeeds.setPrice(62);
        items.put(potatoSeeds, 5.0);

        Item strawberrySeeds = new Seed(SeedType.StrawberrySeeds);
        strawberrySeeds.setPrice(100);
        items.put(strawberrySeeds, 5.0);

        Item tulipBulb = new Seed(SeedType.TulipBulb);
        tulipBulb.setPrice(25);
        items.put(tulipBulb, 5.0);

        Item kaleSeeds = new Seed(SeedType.KaleSeeds);
        kaleSeeds.setPrice(87);
        items.put(kaleSeeds, 5.0);

        Item coffeeBeansSpring = new Seed(SeedType.CoffeeBean);
        coffeeBeansSpring.setPrice(200);
        items.put(coffeeBeansSpring, 1.0);

        Item carrotSeeds = new Seed(SeedType.CarrotSeeds);
        carrotSeeds.setPrice(5);
        items.put(carrotSeeds, 10.0);

        Item rhubarbSeeds = new Seed(SeedType.RhubarbSeeds);
        rhubarbSeeds.setPrice(100);
        items.put(rhubarbSeeds, 5.0);

        Item jazzSeeds = new Seed(SeedType.JazzSeeds);
        jazzSeeds.setPrice(37);
        items.put(jazzSeeds, 5.0);

        //Summer Stock

        Item tomatoSeeds = new Seed(SeedType.TomatoSeeds);
        tomatoSeeds.setPrice(62);
        summerItems.put(tomatoSeeds, 5.0);

        Item pepperSeeds = new Seed(SeedType.PepperSeeds);
        pepperSeeds.setPrice(50);
        summerItems.put(pepperSeeds, 5.0);

        Item wheatSeedsSummer = new Seed(SeedType.WheatSeeds);
        wheatSeedsSummer.setPrice(12);
        summerItems.put(wheatSeedsSummer, 10.0);

        Item SummerSquashSeeds = new Seed(SeedType.SummerSquashSeeds);
        SummerSquashSeeds.setPrice(10);
        summerItems.put(SummerSquashSeeds, 10.0);

        Item radishSeeds = new Seed(SeedType.RadishSeeds);
        radishSeeds.setPrice(50);
        summerItems.put(radishSeeds, 5.0);

        Item melonSeeds = new Seed(SeedType.MelonSeeds);
        melonSeeds.setPrice(100);
        summerItems.put(melonSeeds, 5.0);

        Item hopsStarter = new Seed(SeedType.HopsStarter);
        hopsStarter.setPrice(75);
        summerItems.put(hopsStarter, 5.0);

        Item poppySeeds = new Seed(SeedType.PoppySeeds);
        poppySeeds.setPrice(125);
        summerItems.put(poppySeeds, 5.0);

        Item spangleSeeds = new Seed(SeedType.SpangleSeeds);
        spangleSeeds.setPrice(62);
        summerItems.put(spangleSeeds, 5.0);

        Item starfruitSeeds = new Seed(SeedType.StarfruitSeeds);
        starfruitSeeds.setPrice(400);
        summerItems.put(starfruitSeeds, 5.0);

        Item coffeeBeansSummer = new Seed(SeedType.CoffeeBean);
        coffeeBeansSummer.setPrice(200);
        summerItems.put(coffeeBeansSummer, 1.0);

        Item sunflowerSeedsSummer = new Seed(SeedType.SunflowerSeeds);
        sunflowerSeedsSummer.setPrice(125);
        summerItems.put(sunflowerSeedsSummer, 5.0);


        //Fall Stock
        Item cornSeeds = new Seed(SeedType.CornSeeds);
        cornSeeds.setPrice(187);
        autumnItems.put(cornSeeds, 5.0);

        Item eggplantSeeds = new Seed(SeedType.EggplantSeeds);
        eggplantSeeds.setPrice(25);
        autumnItems.put(eggplantSeeds, 5.0);

        Item pumpkinSeeds = new Seed(SeedType.PumpkinSeeds);
        pumpkinSeeds.setPrice(125);
        autumnItems.put(pumpkinSeeds, 5.0);

        Item broccoliSeeds = new Seed(SeedType.BroccoliSeeds);
        broccoliSeeds.setPrice(15);
        autumnItems.put(broccoliSeeds, 5.0);

        Item amaranthSeeds = new Seed(SeedType.AmaranthSeeds);
        amaranthSeeds.setPrice(87);
        autumnItems.put(amaranthSeeds, 5.0);

        Item grapeStarter = new Seed(SeedType.GrapeStarter);
        grapeStarter.setPrice(75);
        autumnItems.put(grapeStarter, 5.0);

        Item beetSeeds = new Seed(SeedType.BeetSeeds);
        beetSeeds.setPrice(20);
        autumnItems.put(beetSeeds, 5.0);

        Item yamSeeds = new Seed(SeedType.YamSeeds);
        yamSeeds.setPrice(75);
        autumnItems.put(yamSeeds, 5.0);

        Item bokChoySeeds = new Seed(SeedType.BokChoySeeds);
        bokChoySeeds.setPrice(62);
        autumnItems.put(bokChoySeeds, 5.0);

        Item cranberrySeeds = new Seed(SeedType.CranberrySeeds);
        cranberrySeeds.setPrice(300);
        autumnItems.put(cranberrySeeds, 5.0);

        Item sunflowerSeedsAutumn = new Seed(SeedType.SunflowerSeeds);
        sunflowerSeedsAutumn.setPrice(125);
        autumnItems.put(sunflowerSeedsAutumn, 5.0);

        Item fairySeeds = new Seed(SeedType.FairySeeds);
        fairySeeds.setPrice(250);
        autumnItems.put(fairySeeds, 5.0);

        Item rareSeed =  new Seed(SeedType.RareSeed);
        rareSeed.setPrice(1000);
        autumnItems.put(rareSeed, 1.0);

        Item wheatSeedsAutumn = new Seed(SeedType.WheatSeeds);
        wheatSeedsAutumn.setPrice(12);
        autumnItems.put(wheatSeedsAutumn, 5.0);

        //Winter Stock
        Item powdermelonSeeds = new Seed(SeedType.PowdermelonSeeds);
        powdermelonSeeds.setPrice(20);
        winterItems.put(powdermelonSeeds, 5.0);

        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(items , springItems , summerItems , autumnItems , winterItems, startHour, endHour, menu);
    }

    private static Market createPierreGeneralStore() {
        HashMap<Item, Double> items = new HashMap<>();
        HashMap<Item, Double> springItems = new HashMap<>();
        HashMap<Item, Double> summerItems = new HashMap<>();
        HashMap<Item, Double> autumnItems = new HashMap<>();
        HashMap<Item, Double> winterItems = new HashMap<>();

        // permanent stock
        Item rice = new Item("Rice" , 200 , "A basic grain often served under vegetables.");
        items.put(rice, Double.POSITIVE_INFINITY);

        Item wheatFlour = new Item("Wheat Flour" , 100 , "A common cooking ingredient made from crushed wheat seeds.");
        items.put(wheatFlour, Double.POSITIVE_INFINITY);


        //TODO : need to be added:
        //Wedding Ring
        //Dehydrator (Recipe)
        //Grass Starter (Recipe)

        Item sugar = new Item("Sugar" , 100 , "Adds sweetness to pastries and candies. Too much can be unhealthy.");
        items.put(sugar, Double.POSITIVE_INFINITY);

        Item oil = new Item("Oil" , 200 , "All purpose cooking oil.");
        items.put(oil, Double.POSITIVE_INFINITY);

        Item vinegar = new Item("Vinegar" , 100 , "An aged fermented liquid used in many cooking recipes.");
        items.put(vinegar, Double.POSITIVE_INFINITY);

        //TODO : need to be added:
        //Deluxe Retaining Soil
        //Grass Starter
        //Speed-Gro waiting for kasra.

        Item appleSapling = new Item("Apple Sapling" , 4000 , "Takes 28 days to produce a mature Apple tree. Bears fruit in the fall. Only grows if the 8 surrounding \"tiles\" are empty.");
        items.put(appleSapling, Double.POSITIVE_INFINITY);

        Item apricotSapling = new Item("Apricot Sapling" , 2000 , "Takes 28 days to produce a mature Apricot tree. Bears fruit in the spring. Only grows if the 8 surrounding \"tiles\" are empty.");
        items.put(apricotSapling, Double.POSITIVE_INFINITY);

        Item cherrySapling = new Item("Cherry Sapling" , 3_400 , "Takes 28 days to produce a mature Cherry tree. Bears fruit in the spring. Only grows if the 8 surrounding \"tiles\" are empty.");
        items.put(cherrySapling, Double.POSITIVE_INFINITY);

        Item orangeSapling = new Item("Orange Sapling" , 4_000 , "Takes 28 days to produce a mature Orange tree. Bears fruit in the summer. Only grows if the 8 surrounding \"tiles\" are empty.");
        items.put(orangeSapling, Double.POSITIVE_INFINITY);

        Item peachSapling = new Item("Peach Sapling" , 6_000 , "Takes 28 days to produce a mature Peach tree. Bears fruit in the summer. Only grows if the 8 surrounding \"tiles\" are empty.");
        items.put(peachSapling, Double.POSITIVE_INFINITY);

        Item pomegranateSapling = new Item("Pomegranate Sapling" , 6_000 , "Takes 28 days to produce a mature Pomegranate tree. Bears fruit in the fall. Only grows if the 8 surrounding \"tiles\" are empty.");
        items.put(pomegranateSapling, Double.POSITIVE_INFINITY);

        //TODO: need to be added:
        //Basic Retaining Soil
        //Quality Retaining Soil



        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(items , springItems , summerItems , autumnItems , winterItems, startHour, endHour, menu);
    }

    private static Market createCarpentersShop() {
        HashMap<Item, Double> items = new HashMap<>();
        HashMap<Item, Double> springItems = new HashMap<>();
        HashMap<Item, Double> summerItems = new HashMap<>();
        HashMap<Item, Double> autumnItems = new HashMap<>();
        HashMap<Item, Double> winterItems = new HashMap<>();

        Item wood = new Item("Wood" , 10 , "A sturdy, yet flexible plant material with a wide variety of uses.");
        items.put(wood, Double.POSITIVE_INFINITY);

        Item stone = new Item("Stone" , 20 , "A common material with many uses in crafting and building.");
        items.put(stone, Double.POSITIVE_INFINITY);


        //TODO: adding barns:
        //Barn
        //Big Barn
        //Deluxe Barn
        //Coop
        //Big Coop
        //Deluxe Coop
        //Well
        //Shipping Bin
        //i need Barns taha... fuck you



        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(items , springItems , summerItems , autumnItems , winterItems, startHour, endHour, menu);
    }

    private static Market createMarnieShop() {
        HashMap<Item, Double> items = new HashMap<>();
        HashMap<Item, Double> springItems = new HashMap<>();
        HashMap<Item, Double> summerItems = new HashMap<>();
        HashMap<Item, Double> autumnItems = new HashMap<>();
        HashMap<Item, Double> winterItems = new HashMap<>();




        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(items , springItems , summerItems , autumnItems , winterItems, startHour, endHour, menu);
    }

    private static Market createStarDropSaloon() {
        HashMap<Item, Double> items = new HashMap<>();
        HashMap<Item, Double> springItems = new HashMap<>();
        HashMap<Item, Double> summerItems = new HashMap<>();
        HashMap<Item, Double> autumnItems = new HashMap<>();
        HashMap<Item, Double> winterItems = new HashMap<>();




        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(items , springItems , summerItems , autumnItems , winterItems, startHour, endHour, menu);
    }

    private static Market createFishShop() {
        HashMap<Item, Double> items = new HashMap<>();
        HashMap<Item, Double> springItems = new HashMap<>();
        HashMap<Item, Double> summerItems = new HashMap<>();
        HashMap<Item, Double> autumnItems = new HashMap<>();
        HashMap<Item, Double> winterItems = new HashMap<>();




        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(items , springItems , summerItems , autumnItems , winterItems, startHour, endHour, menu);
    }

    public Market createMarket() {
        return this.market;
    }


}
