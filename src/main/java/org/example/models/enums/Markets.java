package org.example.models.enums;

import org.example.models.Items.*;
import org.example.models.Market;
import org.example.models.entities.animal.Animal;
import org.example.models.entities.animal.BarnAnimal;
import org.example.models.entities.animal.CoopAnimal;
import org.example.models.enums.PlayerEnums.Skills;
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
        int x = 4;
        int y = 0;
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

        //upgrading item;
        Item cooperTool = new Item("Cooper Tool" , 2_000);
        items.put(cooperTool, 1.0);

        Item ironTool = new Item("Iron Tool" , 5_000);
        items.put(ironTool, 1.0);

        Item goldTool = new Item("Gold Tool" , 10_000);
        items.put(goldTool, 1.0);

        Item iridiumTool = new Item("Irididium Tool" , 25_000);
        items.put(iridiumTool, 1.0);

        Item cooperTrashCan = new Item("Copper Trash Can" , 1_000);
        items.put(cooperTrashCan, 1.0);

        Item ironTrashCan = new Item("Iron Trash Can" , 2_500);
        items.put(ironTrashCan, 1.0);

        Item goldTrashCan = new Item("Gold Trash Can" , 5_000);
        items.put(goldTrashCan, 1.0);

        Item iridiumTrashCan = new Item("Iridium Trash Can" , 12_500);
        items.put(iridiumTrashCan, 1.0);


        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(x , y, items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu , "Black Smith");
    }

    private static Market createJojaMarket() {
        int x = 41;
        int y = 12;
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
        return new Market(x,y,items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu , "Joja Market");
    }

    private static Market createPierreGeneralStore() {
        int x = 15;
        int y = 13;
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




        Item weddingRing = new Item("Wedding Ring" , 10_000 , "It's used to ask for another farmer's hand in marriage. " +
                "(Unlocked after reaching level 3 friendship with a player)");
        items.put(weddingRing, 2.0);

        Item dehydrator = new CraftingItem(CraftingType.Dehydrator);
        dehydrator.setPrice(10_000);
        items.put(dehydrator, 1.0);

        Item grassStarter = new Item("Grass Starter" , 1000 , "A recipe to make Grass Starter");
        items.put(grassStarter, 1.0);

        Item sugar = new Item("Sugar", 100, "Adds sweetness to pastries and candies. Too much can be unhealthy.");
        items.put(sugar, Double.POSITIVE_INFINITY);

        Item soil = new Item("Deluxe Retaining Soil" , 150 , "This soil has a 100% chance of staying watered overnight. Mix into tilled soil.");
        items.put(soil, Double.POSITIVE_INFINITY);

        Item speedGrow = new Item("Speed-Gro" , 100 , "Makes the plants grow 1 day earlier.");
        items.put(speedGrow, Double.POSITIVE_INFINITY);

        Item oil = new ArtisanItem(ArtisanType.Oil);
        oil.setPrice(200);
        items.put(oil, Double.POSITIVE_INFINITY);

        Item vinegar = new ArtisanItem(ArtisanType.Vinegar);
        vinegar.setPrice(100);
        items.put(vinegar, Double.POSITIVE_INFINITY);


        Item appleSapling = new Tree(TreeType.AppleTree);
        appleSapling.setPrice(4_000);
        items.put(appleSapling, Double.POSITIVE_INFINITY);

        Item apricotSapling = new Tree(TreeType.ApricotTree);
        appleSapling.setPrice(2_000);
        items.put(apricotSapling, Double.POSITIVE_INFINITY);

        Item cherrySapling = new Tree(TreeType.CherryTree);
        cherrySapling.setPrice(3_400);
        items.put(cherrySapling, Double.POSITIVE_INFINITY);

        Item orangeSapling = new Tree(TreeType.OrangeTree);
        orangeSapling.setPrice(4_000);
        items.put(orangeSapling, Double.POSITIVE_INFINITY);

        Item peachSapling = new Tree(TreeType.PeachTree);
        peachSapling.setPrice(6_000);
        items.put(peachSapling, Double.POSITIVE_INFINITY);

        Item pomegranateSapling = new Tree(TreeType.PomegranateTree);
        pomegranateSapling.setPrice(4_000);
        items.put(pomegranateSapling, Double.POSITIVE_INFINITY);



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
        return new Market(x,y,items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu, "Pierre General Store");
    }

    private static Market createCarpentersShop() {
        int x = 25;
        int y = 18;
        HashMap<Item, Double> items = new HashMap<>();
        HashMap<Item, Double> springItems = new HashMap<>();
        HashMap<Item, Double> summerItems = new HashMap<>();
        HashMap<Item, Double> autumnItems = new HashMap<>();
        HashMap<Item, Double> winterItems = new HashMap<>();

        Item wood = new Item("Wood", 10, "A sturdy, yet flexible plant material with a wide variety of uses.");
        items.put(wood, Double.POSITIVE_INFINITY);

        Item stone = new Item("Stone", 20, "A common material with many uses in crafting and building.");
        items.put(stone, Double.POSITIVE_INFINITY);



        Item barn = new Item("Barn" , 6_000);
        items.put(barn,1.0);
        Item bigBarn = new Item("Big Barn" , 12_000);
        items.put(bigBarn,1.0);
        Item deluxeBarn = new Item("Deluxe Barn" , 25_000);
        items.put(deluxeBarn,1.0);


        Item coop = new Item("Coop" , 4_000);
        items.put(coop,1.0);
        Item bigCoop = new Item("Big Coop" , 10_000);
        items.put(bigCoop,1.0);
        Item deluxeCoop = new Item("Deluxe Coop" , 20_000);
        items.put(deluxeCoop,1.0);

        Item well = new Item("Well" , 1_000);
        items.put(well,1.0);
        Item shippingBin = new ShippingBin();
        items.put(shippingBin,Double.POSITIVE_INFINITY);

        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(x,y,items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu , "Carpenters Shop");
    }

    private static Market createMarnieShop() {
        int x = 35;
        int y = 30;
        HashMap<Item, Double> items = new HashMap<>();
        HashMap<Item, Double> springItems = new HashMap<>();
        HashMap<Item, Double> summerItems = new HashMap<>();
        HashMap<Item, Double> autumnItems = new HashMap<>();
        HashMap<Item, Double> winterItems = new HashMap<>();

        Item shears = new Tool("Shears" , 1_000 , "Use this to collect wool from sheep", Tool.ToolType.SHEARS , Tool.ToolMaterial.BASIC , 8 , Skills.FARMING );
        items.put(shears, 1.0);

        Item milkPail = new Tool("Milk Pail" , 1_000 , "Gather milk from your animals." , Tool.ToolType.MILK_PAIL , Tool.ToolMaterial.BASIC , 5 , Skills.FARMING );
        items.put(milkPail, 1.0);

        Item chicken = new CoopAnimal(org.example.models.enums.CoopAnimalTypes.CHICKEN, "Chicken");
        chicken.setPrice(800);
        items.put(chicken, 2.0);

        Item cow = new BarnAnimal(BarnAnimalTypes.COW , "Cow");
        cow.setPrice(1_500);
        items.put(cow, 2.0);

        Item goat = new BarnAnimal( BarnAnimalTypes.GOAT , "Goat");
        goat.setPrice(4_000);
        items.put(goat, 2.0);

        Item duck = new CoopAnimal(org.example.models.enums.CoopAnimalTypes.DUCK, "Duck");
        duck.setPrice(1_200);
        items.put(duck, 2.0);

        Item sheep = new BarnAnimal( BarnAnimalTypes.SHEEP, "Sheep");
        sheep.setPrice(8_000);
        items.put(sheep, 2.0);

        Item rabbit = new CoopAnimal(org.example.models.enums.CoopAnimalTypes.RABBIT, "Rabbit");
        rabbit.setPrice(8_500);
        items.put(rabbit, 2.0);

        Item dinosaur = new CoopAnimal(org.example.models.enums.CoopAnimalTypes.DINOSAUR, "Dinosaur");
        dinosaur.setPrice(14_000);
        items.put(dinosaur, 2.0);

        Item pig = new BarnAnimal(BarnAnimalTypes.PIG, "Pig");
        pig.setPrice(16_000);
        items.put(pig, 2.0);


        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(x,y,items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu , "Marnie Shop");
    }

    private static Market createStarDropSaloon() {
        int x = 1;
        int y = 31;
        HashMap<Item, Double> items = new HashMap<>();
        HashMap<Item, Double> springItems = new HashMap<>();
        HashMap<Item, Double> summerItems = new HashMap<>();
        HashMap<Item, Double> autumnItems = new HashMap<>();
        HashMap<Item, Double> winterItems = new HashMap<>();


        Item beer = new ArtisanItem(ArtisanType.Beer);
        beer.setPrice(400);
        items.put(beer, Double.POSITIVE_INFINITY);

        Item salad = new CookingItem(CookingType.Salad).getFood();
        salad.setPrice(220);
        items.put(salad, Double.POSITIVE_INFINITY);

        Item bread = new CookingItem(CookingType.Bread).getFood();
        bread.setPrice(120);
        items.put(bread, Double.POSITIVE_INFINITY);

        Item spaghetti = new CookingItem(CookingType.Spaghetti).getFood();
        spaghetti.setPrice(240);
        items.put(spaghetti, Double.POSITIVE_INFINITY);

        Item pizza = new CookingItem(CookingType.Pizza).getFood();
        pizza.setPrice(600);
        items.put(pizza, Double.POSITIVE_INFINITY);


        Item coffee = new ArtisanItem(ArtisanType.Coffee);
        coffee.setPrice(300);
        items.put(coffee, Double.POSITIVE_INFINITY);


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
        return new Market(x,y,items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu , "Star Drop Saloon");
    }

    private static Market createFishShop() {
        int x = 43;
        int y = 45;
        HashMap<Item, Double> items = new HashMap<>();
        HashMap<Item, Double> springItems = new HashMap<>();
        HashMap<Item, Double> summerItems = new HashMap<>();
        HashMap<Item, Double> autumnItems = new HashMap<>();
        HashMap<Item, Double> winterItems = new HashMap<>();

        Item fishSmoker = new CraftingItem(CraftingType.FishSmoker);
        fishSmoker.setPrice(10_000);
        items.put(fishSmoker, 1.0);

        Item troutSoup = new Item("Trout Soup", 250, "Pretty salty.");
        items.put(troutSoup, 1.0);

        Item bambooPole = new Item("Bamboo Pole", 500, "Use in the water to catch fish.");
        items.put(bambooPole, 1.0);

        Item trainingRod = new Item("Training Rod", 25, "It's a lot easier to use than other rods, but can only catch basic fish.");
        items.put(trainingRod, 1.0);

        Item fiberglassRod = new Item("Fiberglass Rod", 1800, "Use in the water to catch fish.");
        items.put(fiberglassRod, 1.0);

        Item iridiumRod = new Item("Iridium Rod", 7500, "Use in the water to catch fish.");
        items.put(iridiumRod, 1.0);

        //        Item rod = new Tool(Tool.ToolType.FISHING_ROD);


        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(x,y,items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu , "Fish Shop");
    }

    public Market createMarket() {
        return this.market;
    }


}
