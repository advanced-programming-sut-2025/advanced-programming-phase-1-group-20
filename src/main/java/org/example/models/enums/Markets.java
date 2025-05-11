package org.example.models.enums;

import org.example.models.Items.*;
import org.example.models.Market;
import org.example.models.enums.Types.*;

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
        return new Market(items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu);
    }

    private static Market createJojaMarket() {
        HashMap<Item, Double> items = new HashMap<>();
        HashMap<Item, Double> springItems = new HashMap<>();
        HashMap<Item, Double> summerItems = new HashMap<>();
        HashMap<Item, Double> autumnItems = new HashMap<>();
        HashMap<Item, Double> winterItems = new HashMap<>();


        //Permanent Stock
        Item jojaCola = new Item("Joja Cola" , 75 , "The flagship product of Joja corporation.");
        items.put(jojaCola, Double.POSITIVE_INFINITY);

        Item ancientSeed = new Seed(SeedType.AncientSeeds);
        ancientSeed.setPrice(500);
        items.put(ancientSeed, 1.0);

        Item grassStarter = new Item("Grass Starter" , 125 , "Place this on your farm to start a new patch of grass.");
        items.put(grassStarter, Double.POSITIVE_INFINITY);

        Item sugar = new Item("sugar", 125, "Adds sweetness to pastries and candies. Too much can be unhealthy.");
        items.put(sugar, Double.POSITIVE_INFINITY);

        Item wheatFlour = new Item("wheat flour", 125, "A common cooking ingredient made from crushed wheat seeds.");
        items.put(wheatFlour, Double.POSITIVE_INFINITY);

        Item rice = new Item("rice", 250, "A basic grain often served under vegetables.");
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

        Item rareSeed = new Seed(SeedType.RareSeed);
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
        return new Market(items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu);
    }

    private static Market createPierreGeneralStore() {
        HashMap<Item, Double> items = new HashMap<>();
        HashMap<Item, Double> springItems = new HashMap<>();
        HashMap<Item, Double> summerItems = new HashMap<>();
        HashMap<Item, Double> autumnItems = new HashMap<>();
        HashMap<Item, Double> winterItems = new HashMap<>();

        // permanent stock
        Item rice = new Item("Rice", 200, "A basic grain often served under vegetables.");
        items.put(rice, Double.POSITIVE_INFINITY);

        Item wheatFlour = new Item("Wheat Flour", 100, "A common cooking ingredient made from crushed wheat seeds.");
        items.put(wheatFlour, Double.POSITIVE_INFINITY);


        //TODO : need to be added:
        //Wedding Ring
        //Dehydrator (Recipe)
        //Grass Starter (Recipe)

        Item sugar = new Item("Sugar", 100, "Adds sweetness to pastries and candies. Too much can be unhealthy.");
        items.put(sugar, Double.POSITIVE_INFINITY);

        Item oil = new ArtisanItem(ArtisanType.Oil);
        oil.setPrice(200);
        items.put(oil, Double.POSITIVE_INFINITY);

        Item vinegar = new ArtisanItem(ArtisanType.Vinegar);
        vinegar.setPrice(100);
        items.put(vinegar, Double.POSITIVE_INFINITY);

        //TODO : need to be added:
        //Deluxe Retaining Soil
        //Grass Starter
        //Speed-Gro waiting for kasra.

        Item appleSapling = new Item("Apple Sapling", 4000, "Takes 28 days to produce a mature Apple tree. Bears fruit in the fall. Only grows if the 8 surrounding \"tiles\" are empty.");
        items.put(appleSapling, Double.POSITIVE_INFINITY);

        Item apricotSapling = new Item("Apricot Sapling", 2000, "Takes 28 days to produce a mature Apricot tree. Bears fruit in the spring. Only grows if the 8 surrounding \"tiles\" are empty.");
        items.put(apricotSapling, Double.POSITIVE_INFINITY);

        Item cherrySapling = new Item("Cherry Sapling", 3_400, "Takes 28 days to produce a mature Cherry tree. Bears fruit in the spring. Only grows if the 8 surrounding \"tiles\" are empty.");
        items.put(cherrySapling, Double.POSITIVE_INFINITY);

        Item orangeSapling = new Item("Orange Sapling", 4_000, "Takes 28 days to produce a mature Orange tree. Bears fruit in the summer. Only grows if the 8 surrounding \"tiles\" are empty.");
        items.put(orangeSapling, Double.POSITIVE_INFINITY);

        Item peachSapling = new Item("Peach Sapling", 6_000, "Takes 28 days to produce a mature Peach tree. Bears fruit in the summer. Only grows if the 8 surrounding \"tiles\" are empty.");
        items.put(peachSapling, Double.POSITIVE_INFINITY);

        Item pomegranateSapling = new Item("Pomegranate Sapling", 6_000, "Takes 28 days to produce a mature Pomegranate tree. Bears fruit in the fall. Only grows if the 8 surrounding \"tiles\" are empty.");
        items.put(pomegranateSapling, Double.POSITIVE_INFINITY);

        //TODO: need to be added:
        //Basic Retaining Soil
        //Quality Retaining Soil

        Item basicRetainingSoil = new Item("Basic Retaining Soil" , 100 , "This soil has a chance of staying watered overnight. Mix into tilled soil.");
        items.put(basicRetainingSoil, Double.POSITIVE_INFINITY);

        Item qualityRetainingSoil = new Item("Quality Retaining Soil" , 150 , "This soil has a good chance of staying watered overnight. Mix into tilled soil.");
        items.put(qualityRetainingSoil, Double.POSITIVE_INFINITY);


        //Spring stock
        Item parsnipSeeds = new Plant(PlantType.Parsnip);
        parsnipSeeds.setPrice(20);
        springItems.put(parsnipSeeds, 5.0);
        parsnipSeeds.setPrice(30);
        summerItems.put(parsnipSeeds, 5.0);
        autumnItems.put(parsnipSeeds, 5.0);
        winterItems.put(parsnipSeeds, 5.0);

        Item beanStarter = new Seed(SeedType.BeanStarter);
        beanStarter.setPrice(60);
        springItems.put(beanStarter, 5.0);
        beanStarter.setPrice(90);
        summerItems.put(beanStarter, 5.0);
        autumnItems.put(beanStarter, 5.0);
        winterItems.put(beanStarter, 5.0);

        Item cauliflowerSeeds = new Seed(SeedType.CauliflowerSeeds);
        cauliflowerSeeds.setPrice(80);
        springItems.put(cauliflowerSeeds, 5.0);
        cauliflowerSeeds.setPrice(120);
        summerItems.put(cauliflowerSeeds, 5.0);
        autumnItems.put(cauliflowerSeeds, 5.0);
        winterItems.put(cauliflowerSeeds, 5.0);

        Item potatoSeeds = new Seed(SeedType.PotatoSeeds);
        potatoSeeds.setPrice(50);
        springItems.put(potatoSeeds, 5.0);
        potatoSeeds.setPrice(75);
        summerItems.put(potatoSeeds, 5.0);
        autumnItems.put(potatoSeeds, 5.0);
        winterItems.put(potatoSeeds, 5.0);

        Item tulipBulb = new Seed(SeedType.TulipBulb);
        tulipBulb.setPrice(20);
        springItems.put(tulipBulb, 5.0);
        tulipBulb.setPrice(30);
        summerItems.put(tulipBulb, 5.0);
        autumnItems.put(tulipBulb, 5.0);
        winterItems.put(tulipBulb, 5.0);

        Item kaleSeeds = new Seed(SeedType.KaleSeeds);
        kaleSeeds.setPrice(70);
        springItems.put(kaleSeeds, 5.0);
        kaleSeeds.setPrice(105);
        summerItems.put(kaleSeeds, 5.0);
        autumnItems.put(kaleSeeds, 5.0);
        winterItems.put(kaleSeeds, 5.0);

        Item jazzSeeds = new Seed(SeedType.JazzSeeds);
        jazzSeeds.setPrice(30);
        springItems.put(jazzSeeds, 5.0);
        jazzSeeds.setPrice(45);
        summerItems.put(jazzSeeds, 5.0);
        autumnItems.put(jazzSeeds, 5.0);
        winterItems.put(jazzSeeds, 5.0);

        Item garlicSeeds = new Seed(SeedType.GarlicSeeds);
        garlicSeeds.setPrice(40);
        springItems.put(garlicSeeds , 5.0);
        garlicSeeds.setPrice(60);
        summerItems.put(garlicSeeds, 5.0);
        autumnItems.put(garlicSeeds, 5.0);
        winterItems.put(garlicSeeds, 5.0);

        Item riceShoot = new Seed(SeedType.RiceShoot);
        riceShoot.setPrice(40);
        springItems.put(riceShoot , 5.0);
        riceShoot.setPrice(60);
        summerItems.put(riceShoot , 5.0);
        autumnItems.put(riceShoot,5.0);
        winterItems.put(riceShoot , 5.0);

        //Summer Stock:
        Item melonSeed = new Seed(SeedType.MelonSeeds);
        melonSeed.setPrice(80);
        summerItems.put(melonSeed, 5.0);
        melonSeed.setPrice(120);
        springItems.put(melonSeed, 5.0);
        autumnItems.put(melonSeed, 5.0);
        winterItems.put(melonSeed, 5.0);

        Item tomatoSeed = new Seed(SeedType.TomatoSeeds);
        tomatoSeed.setPrice(50);
        summerItems.put(tomatoSeed, 5.0);
        tomatoSeed.setPrice(75);
        springItems.put(tomatoSeed, 5.0);
        autumnItems.put(tomatoSeed, 5.0);
        winterItems.put(tomatoSeed, 5.0);

        Item BlueberrySeed = new Seed(SeedType.BlueberrySeeds);
        BlueberrySeed.setPrice(80);
        summerItems.put(BlueberrySeed, 5.0);
        BlueberrySeed.setPrice(120);
        springItems.put(BlueberrySeed, 5.0);
        autumnItems.put(BlueberrySeed, 5.0);
        winterItems.put(BlueberrySeed, 5.0);

        Item pepperSeed = new Seed(SeedType.PepperSeeds);
        pepperSeed.setPrice(40);
        summerItems.put(pepperSeed, 5.0);
        pepperSeed.setPrice(60);
        springItems.put(pepperSeed, 5.0);
        autumnItems.put(pepperSeed, 5.0);
        winterItems.put(pepperSeed, 5.0);

        Item wheatSeed = new Seed(SeedType.WheatSeeds);
        wheatSeed.setPrice(10);
        summerItems.put(wheatSeed, 5.0);
        wheatSeed.setPrice(15);
        springItems.put(wheatSeed, 5.0);
        autumnItems.put(wheatSeed, 5.0);
        winterItems.put(wheatSeed, 5.0);

        Item radishSeed = new Seed(SeedType.RadishSeeds);
        radishSeed.setPrice(40);
        summerItems.put(radishSeed, 5.0);
        radishSeed.setPrice(60);
        springItems.put(radishSeed, 5.0);
        autumnItems.put(radishSeed, 5.0);
        winterItems.put(radishSeed, 5.0);

        Item poppySeed = new Seed(SeedType.PoppySeeds);
        poppySeed.setPrice(100);
        summerItems.put(poppySeed, 5.0);
        poppySeed.setPrice(150);
        springItems.put(poppySeed, 5.0);
        autumnItems.put(poppySeed, 5.0);
        winterItems.put(poppySeed, 5.0);

        Item spangleSeed = new Seed(SeedType.SpangleSeeds);
        spangleSeed.setPrice(50);
        summerItems.put(spangleSeed, 5.0);
        spangleSeed.setPrice(75);
        springItems.put(spangleSeed, 5.0);
        autumnItems.put(spangleSeed, 5.0);
        winterItems.put(spangleSeed, 5.0);

        Item hopStarter = new Item("Hops Starter" , 60 , "Plant these in the summer. Takes 11 days to grow, but keeps producing after that. Grows on a trellis.");
        summerItems.put(hopStarter, 5.0);
        hopStarter.setPrice(90);
        springItems.put(hopStarter, 5.0);
        autumnItems.put(hopStarter, 5.0);
        winterItems.put(hopStarter, 5.0);

        Item cornSeeds = new Seed(SeedType.CornSeeds);
        cornSeeds.setPrice(150);
        summerItems.put(cornSeeds, 5.0);
        cornSeeds.setPrice(225);
        springItems.put(cornSeeds, 5.0);
        autumnItems.put(cornSeeds, 5.0);
        winterItems.put(cornSeeds, 5.0);

        Item sunflowerSeeds = new Seed(SeedType.SunflowerSeeds);
        sunflowerSeeds.setPrice(200);
        summerItems.put(sunflowerSeeds, 5.0);
        sunflowerSeeds.setPrice(300);
        springItems.put(sunflowerSeeds, 5.0);
        autumnItems.put(sunflowerSeeds, 5.0);
        winterItems.put(sunflowerSeeds, 5.0);

        Item redCabbageSeeds = new Seed(SeedType.RedCabbageSeeds);
        redCabbageSeeds.setPrice(100);
        summerItems.put(redCabbageSeeds, 5.0);
        redCabbageSeeds.setPrice(150);
        springItems.put(redCabbageSeeds, 5.0);
        autumnItems.put(redCabbageSeeds, 5.0);
        winterItems.put(redCabbageSeeds, 5.0);

        //Autumn Stock:
        Item eggPlantSeed = new Seed(SeedType.EggplantSeeds);
        eggPlantSeed.setPrice(20);
        autumnItems.put(eggPlantSeed, 5.0);
        eggPlantSeed.setPrice(30);
        springItems.put(eggPlantSeed, 5.0);
        summerItems.put(eggPlantSeed, 5.0);
        winterItems.put(eggPlantSeed, 5.0);

        Item pumpkinSeed = new Seed(SeedType.PumpkinSeeds);
        pumpkinSeed.setPrice(100);
        autumnItems.put(pumpkinSeed, 5.0);
        pumpkinSeed.setPrice(150);
        springItems.put(pumpkinSeed, 5.0);
        summerItems.put(pumpkinSeed, 5.0);
        winterItems.put(pumpkinSeed, 5.0);

        Item bokChoySeed = new Seed(SeedType.BokChoySeeds);
        bokChoySeed.setPrice(50);
        autumnItems.put(bokChoySeed, 5.0);
        bokChoySeed.setPrice(75);
        springItems.put(bokChoySeed, 5.0);
        summerItems.put(bokChoySeed, 5.0);
        winterItems.put(bokChoySeed, 5.0);

        Item yamSeed = new Seed(SeedType.YamSeeds);
        yamSeed.setPrice(60);
        autumnItems.put(yamSeed, 5.0);
        yamSeed.setPrice(90);
        springItems.put(yamSeed, 5.0);
        summerItems.put(yamSeed, 5.0);
        winterItems.put(yamSeed, 5.0);

        Item cranberrySeed = new Seed(SeedType.CranberrySeeds);
        cranberrySeed.setPrice(240);
        autumnItems.put(cranberrySeed, 5.0);
        cranberrySeed.setPrice(360);
        springItems.put(cranberrySeed, 5.0);
        summerItems.put(cranberrySeed, 5.0);
        winterItems.put(cranberrySeed, 5.0);

        Item fairySeed = new Seed(SeedType.FairySeeds);
        fairySeed.setPrice(200);
        autumnItems.put(fairySeed , 5.0);
        fairySeed.setPrice(300);
        springItems.put(fairySeed , 5.0);
        summerItems.put(fairySeed , 5.0);
        winterItems.put(fairySeed, 5.0);

        Item amaranthSeed = new Seed(SeedType.AmaranthSeeds);
        amaranthSeed.setPrice(70);
        autumnItems.put(amaranthSeed, 5.0);
        amaranthSeed.setPrice(105);
        springItems.put(amaranthSeed, 5.0);
        summerItems.put(amaranthSeed, 5.0);
        winterItems.put(amaranthSeed, 5.0);


        Item grapeStarter = new Seed(SeedType.GrapeStarter);
        grapeStarter.setPrice(60);
        autumnItems.put(grapeStarter, 5.0);
        grapeStarter.setPrice(90);
        springItems.put(grapeStarter, 5.0);
        summerItems.put(grapeStarter, 5.0);
        winterItems.put(grapeStarter, 5.0);

        Item artichokeSeed = new Seed(SeedType.ArtichokeSeeds);
        artichokeSeed.setPrice(30);
        autumnItems.put(artichokeSeed, 5.0);
        artichokeSeed.setPrice(45);
        springItems.put(artichokeSeed, 5.0);
        summerItems.put(artichokeSeed, 5.0);
        winterItems.put(artichokeSeed, 5.0);

        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu);
    }

    private static Market createCarpentersShop() {
        HashMap<Item, Double> items = new HashMap<>();
        HashMap<Item, Double> springItems = new HashMap<>();
        HashMap<Item, Double> summerItems = new HashMap<>();
        HashMap<Item, Double> autumnItems = new HashMap<>();
        HashMap<Item, Double> winterItems = new HashMap<>();

        Item wood = new Item("Wood", 10, "A sturdy, yet flexible plant material with a wide variety of uses.");
        items.put(wood, Double.POSITIVE_INFINITY);

        Item stone = new Item("Stone", 20, "A common material with many uses in crafting and building.");
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
        return new Market(items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu);
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
        return new Market(items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu);
    }

    private static Market createStarDropSaloon() {
        HashMap<Item, Double> items = new HashMap<>();
        HashMap<Item, Double> springItems = new HashMap<>();
        HashMap<Item, Double> summerItems = new HashMap<>();
        HashMap<Item, Double> autumnItems = new HashMap<>();
        HashMap<Item, Double> winterItems = new HashMap<>();


        Item beer = new ArtisanItem(ArtisanType.Beer);
        beer.setPrice(400);
        items.put(beer, Double.POSITIVE_INFINITY);

        Item salad = new CookingItem(CookingType.Salad);
        salad.setPrice(220);
        items.put(salad, Double.POSITIVE_INFINITY);

        Item bread = new CookingItem(CookingType.Bread);
        bread.setPrice(120);
        items.put(bread, Double.POSITIVE_INFINITY);

        Item spaghetti = new CookingItem(CookingType.Spaghetti);
        spaghetti.setPrice(240);
        items.put(spaghetti, Double.POSITIVE_INFINITY);

        Item pizza = new CookingItem(CookingType.Pizza);
        pizza.setPrice(600);
        items.put(pizza, Double.POSITIVE_INFINITY);


        Item coffee = new ArtisanItem(ArtisanType.Coffee);
        coffee.setPrice(300);
        items.put(coffee, Double.POSITIVE_INFINITY);

        //TODO : checking recipes.
        Item hashBrownsRecipe = new CookingItem(CookingType.HashBrowns);
        hashBrownsRecipe.setPrice(50);
        items.put(hashBrownsRecipe, 1.0);

        Item omeletRecipe = new CookingItem(CookingType.Omelet);
        omeletRecipe.setPrice(100);
        items.put(omeletRecipe, 1.0);

        Item pancakesRecipe = new CookingItem(CookingType.Pancakes);
        pancakesRecipe.setPrice(100);
        items.put(pancakesRecipe, 1.0);

        Item breadRecipe = new CookingItem(CookingType.Bread);
        breadRecipe.setPrice(100);
        items.put(breadRecipe, 1.0);

        Item tortillaRecipe = new CookingItem(CookingType.Tortilla);
        tortillaRecipe.setPrice(100);
        items.put(tortillaRecipe, 1.0);

        Item makiRoll = new CookingItem(CookingType.MakiRoll);
        makiRoll.setPrice(300);
        items.put(makiRoll, 1.0);

        Item tripleShotEspresso = new CookingItem(CookingType.TripleShotEspresso);
        tripleShotEspresso.setPrice(5_000);
        items.put(tripleShotEspresso, 1.0);

        Item pizzaRecipe = new CookingItem(CookingType.Pizza);
        pizzaRecipe.setPrice(150);
        items.put(pizzaRecipe, 1.0);

        Item cookieRecipe = new CookingItem(CookingType.Cookie);
        cookieRecipe.setPrice(300);
        items.put(cookieRecipe, 1.0);




        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu);
    }

    private static Market createFishShop() {
        HashMap<Item, Double> items = new HashMap<>();
        HashMap<Item, Double> springItems = new HashMap<>();
        HashMap<Item, Double> summerItems = new HashMap<>();
        HashMap<Item, Double> autumnItems = new HashMap<>();
        HashMap<Item, Double> winterItems = new HashMap<>();

        Item fishSmoker = new CraftingItem(CraftingType.FishSmoker);
        fishSmoker.setPrice(10_000);
        items.put(fishSmoker, 1.0);

//        Item rod = new Tool(Tool.ToolType.FISHING_ROD);


        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu);
    }

    public Market createMarket() {
        return this.market;
    }


}
