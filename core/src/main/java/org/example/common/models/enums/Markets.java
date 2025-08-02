package org.example.common.models.enums;

import org.example.common.models.Items.*;
import org.example.common.models.MapDetails.Tile;
import org.example.common.models.Product;
import org.example.common.models.enums.Types.*;
import org.example.common.models.Market;
import org.example.common.models.entities.animal.BarnAnimal;
import org.example.common.models.entities.animal.CoopAnimal;
import org.example.common.models.enums.PlayerEnums.Skills;
import org.example.common.models.enums.Types.ToolFunctionality;

import java.util.ArrayList;
import java.util.List;

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
        int x = 35;  // Center-left of village (78/2 - 4)
        int y = 130;  // Upper area of village
        TileType tileType = TileType.BlackSmith;
        List<Product> items = new ArrayList<>();
        List<Product> springItems = new ArrayList<>();
        List<Product> summerItems = new ArrayList<>();
        List<Product> autumnItems = new ArrayList<>();
        List<Product> winterItems = new ArrayList<>();

        Item cooper = new Mineral(MineralType.Copper);
        cooper.setPrice(75);
        items.add(new Product(cooper, Double.POSITIVE_INFINITY));

        Item iron = new Mineral(MineralType.Iron);
        iron.setPrice(150);
        items.add(new Product(iron, Double.POSITIVE_INFINITY));

        Item coal = new Mineral(MineralType.Coal);
        coal.setPrice(150);
        items.add(new Product(coal, Double.POSITIVE_INFINITY));

        Item gold = new Mineral(MineralType.Gold);
        gold.setPrice(400);
        items.add(new Product(gold, Double.POSITIVE_INFINITY));

        // Tool upgrade service (no physical items, handled by dialog)
        Item toolUpgradeService = new Item("Tool Upgrade Service", 0, "Upgrade your tools to better materials");
        items.add(new Product(toolUpgradeService, Double.POSITIVE_INFINITY));

        // Trash cans with proper Tool functionality
        Item cooperTrashCan = new Tool("Copper Trash Can", 1000, "content/Tools/Trash_Can_Copper.png", 
            "A copper trash can for disposing of items.",
            Tool.ToolType.TRASH_CAN, Tool.ToolMaterial.COPPER, 0, null, ToolFunctionality.TRASH_CAN);
        items.add(new Product(cooperTrashCan, 1.0));

        Item ironTrashCan = new Tool("Iron Trash Can", 2500, "content/Tools/Trash_Can_Steel.png", 
            "An iron trash can for disposing of items.",
            Tool.ToolType.TRASH_CAN, Tool.ToolMaterial.IRON, 0, null, ToolFunctionality.TRASH_CAN);
        items.add(new Product(ironTrashCan, 1.0));

        Item goldTrashCan = new Tool("Gold Trash Can", 5000, "content/Tools/Trash_Can_Gold.png", 
            "A gold trash can for disposing of items.",
            Tool.ToolType.TRASH_CAN, Tool.ToolMaterial.GOLD, 0, null, ToolFunctionality.TRASH_CAN);
        items.add(new Product(goldTrashCan, 1.0));

        Item iridiumTrashCan = new Tool("Iridium Trash Can", 12500, "content/Tools/Trash_Can_Iridium.png", 
            "An iridium trash can for disposing of items.",
            Tool.ToolType.TRASH_CAN, Tool.ToolMaterial.IRIDIUM, 0, null, ToolFunctionality.TRASH_CAN);
        items.add(new Product(iridiumTrashCan, 1.0));


        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(x , y, items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu , "Black Smith" , tileType);
    }

    private static Market createJojaMarket() {
        int x = 40;  // Center-right of village (78/2 + 1)
        int y = 130;  // Upper area of village
        TileType tileType = TileType.JojaMart;
        List<Product> items = new ArrayList<>();
        List<Product> springItems = new ArrayList<>();
        List<Product> summerItems = new ArrayList<>();
        List<Product> autumnItems = new ArrayList<>();
        List<Product> winterItems = new ArrayList<>();


        //Permanent Stock
        Item jojaCola = new Item("Joja Cola" , 75 , "content/Concessions/Joja_Cola_%28large%29.png" , "The flagship product of Joja corporation.");
        items.add(new Product(jojaCola, Double.POSITIVE_INFINITY));

        Item ancientSeed = new Seed(SeedType.AncientSeeds);
        ancientSeed.setPrice(500);
        items.add(new Product(ancientSeed, 1.0));

        Item grassStarter = new Item("Grass Starter" , 125 , "content/Grass_Starter.png" , "Place this on your farm to start a new patch of grass.");
        items.add(new Product(grassStarter, Double.POSITIVE_INFINITY));


        Item sugar = new Item("sugar", 125, "content/Crops/Sugar.png" ,"Adds sweetness to pastries and candies. Too much can be unhealthy.");
        items.add(new Product(sugar, Double.POSITIVE_INFINITY));

        Item wheatFlour = new Item("wheat flour", 125, "content/Farming/Wheat_Flour.png" ,"A common cooking ingredient made from crushed wheat seeds.");
        items.add(new Product(wheatFlour, Double.POSITIVE_INFINITY));

        Item rice = new Item("rice", 250, "content/Crops/Rice.png" , "A basic grain often served under vegetables.");
        items.add(new Product(rice, Double.POSITIVE_INFINITY));

        //Spring Stock
        Item parsnipSeeds = new Plant(PlantType.Parsnip);
        parsnipSeeds.setPrice(25);
        springItems.add(new Product(parsnipSeeds, 5.0));

        Item beanStarter = new Seed(SeedType.BeanStarter);
        beanStarter.setPrice(75);
        springItems.add(new Product(beanStarter, 5.0));

        Item cauliflowerSeeds = new Seed(SeedType.CauliflowerSeeds);
        cauliflowerSeeds.setPrice(100);
        springItems.add(new Product(cauliflowerSeeds, 5.0));

        Item potatoSeeds = new Seed(SeedType.PotatoSeeds);
        potatoSeeds.setPrice(62);
        springItems.add(new Product(potatoSeeds, 5.0));

        Item strawberrySeeds = new Seed(SeedType.StrawberrySeeds);
        strawberrySeeds.setPrice(100);
        springItems.add(new Product(strawberrySeeds, 5.0));

        Item tulipBulb = new Seed(SeedType.TulipBulb);
        tulipBulb.setPrice(25);
        springItems.add(new Product(tulipBulb, 5.0));

        Item kaleSeeds = new Seed(SeedType.KaleSeeds);
        kaleSeeds.setPrice(87);
        springItems.add(new Product(kaleSeeds, 5.0));

        Item coffeeBeansSpring = new Seed(SeedType.CoffeeBean);
        coffeeBeansSpring.setPrice(200);
        springItems.add(new Product(coffeeBeansSpring, 1.0));

        Item carrotSeeds = new Seed(SeedType.CarrotSeeds);
        carrotSeeds.setPrice(5);
        springItems.add(new Product(carrotSeeds, 10.0));

        Item rhubarbSeeds = new Seed(SeedType.RhubarbSeeds);
        rhubarbSeeds.setPrice(100);
        springItems.add(new Product(rhubarbSeeds, 5.0));

        Item jazzSeeds = new Seed(SeedType.JazzSeeds);
        jazzSeeds.setPrice(37);
        springItems.add(new Product(jazzSeeds, 5.0));

        //Summer Stock

        Item tomatoSeeds = new Seed(SeedType.TomatoSeeds);
        tomatoSeeds.setPrice(62);
        summerItems.add(new Product(tomatoSeeds, 5.0));

        Item pepperSeeds = new Seed(SeedType.PepperSeeds);
        pepperSeeds.setPrice(50);
        summerItems.add(new Product(pepperSeeds, 5.0));

        Item wheatSeedsSummer = new Seed(SeedType.WheatSeeds);
        wheatSeedsSummer.setPrice(12);
        summerItems.add(new Product(wheatSeedsSummer, 10.0));

        Item SummerSquashSeeds = new Seed(SeedType.SummerSquashSeeds);
        SummerSquashSeeds.setPrice(10);
        summerItems.add(new Product(SummerSquashSeeds, 10.0));

        Item radishSeeds = new Seed(SeedType.RadishSeeds);
        radishSeeds.setPrice(50);
        summerItems.add(new Product(radishSeeds, 5.0));

        Item melonSeeds = new Seed(SeedType.MelonSeeds);
        melonSeeds.setPrice(100);
        summerItems.add(new Product(melonSeeds, 5.0));

        Item hopsStarter = new Seed(SeedType.HopsStarter);
        hopsStarter.setPrice(75);
        summerItems.add(new Product(hopsStarter, 5.0));

        Item poppySeeds = new Seed(SeedType.PoppySeeds);
        poppySeeds.setPrice(125);
        summerItems.add(new Product(poppySeeds, 5.0));

        Item spangleSeeds = new Seed(SeedType.SpangleSeeds);
        spangleSeeds.setPrice(62);
        summerItems.add(new Product(spangleSeeds, 5.0));

        Item starfruitSeeds = new Seed(SeedType.StarfruitSeeds);
        starfruitSeeds.setPrice(400);
        summerItems.add(new Product(starfruitSeeds, 5.0));

        Item coffeeBeansSummer = new Seed(SeedType.CoffeeBean);
        coffeeBeansSummer.setPrice(200);
        summerItems.add(new Product(coffeeBeansSummer, 1.0));

        Item sunflowerSeedsSummer = new Seed(SeedType.SunflowerSeeds);
        sunflowerSeedsSummer.setPrice(125);
        summerItems.add(new Product(sunflowerSeedsSummer, 5.0));


        //Fall Stock
        Item cornSeeds = new Seed(SeedType.CornSeeds);
        cornSeeds.setPrice(187);
        autumnItems.add(new Product(cornSeeds, 5.0));

        Item eggplantSeeds = new Seed(SeedType.EggplantSeeds);
        eggplantSeeds.setPrice(25);
        autumnItems.add(new Product(eggplantSeeds, 5.0));

        Item pumpkinSeeds = new Seed(SeedType.PumpkinSeeds);
        pumpkinSeeds.setPrice(125);
        autumnItems.add(new Product(pumpkinSeeds, 5.0));

        Item broccoliSeeds = new Seed(SeedType.BroccoliSeeds);
        broccoliSeeds.setPrice(15);
        autumnItems.add(new Product(broccoliSeeds, 5.0));

        Item amaranthSeeds = new Seed(SeedType.AmaranthSeeds);
        amaranthSeeds.setPrice(87);
        autumnItems.add(new Product(amaranthSeeds, 5.0));

        Item grapeStarter = new Seed(SeedType.GrapeStarter);
        grapeStarter.setPrice(75);
        autumnItems.add(new Product(grapeStarter, 5.0));

        Item beetSeeds = new Seed(SeedType.BeetSeeds);
        beetSeeds.setPrice(20);
        autumnItems.add(new Product(beetSeeds, 5.0));

        Item yamSeeds = new Seed(SeedType.YamSeeds);
        yamSeeds.setPrice(75);
        autumnItems.add(new Product(yamSeeds, 5.0));

        Item bokChoySeeds = new Seed(SeedType.BokChoySeeds);
        bokChoySeeds.setPrice(62);
        autumnItems.add(new Product(bokChoySeeds, 5.0));

        Item cranberrySeeds = new Seed(SeedType.CranberrySeeds);
        cranberrySeeds.setPrice(300);
        autumnItems.add(new Product(cranberrySeeds, 5.0));

        Item sunflowerSeedsAutumn = new Seed(SeedType.SunflowerSeeds);
        sunflowerSeedsAutumn.setPrice(125);
        autumnItems.add(new Product(sunflowerSeedsAutumn, 5.0));

        Item fairySeeds = new Seed(SeedType.FairySeeds);
        fairySeeds.setPrice(250);
        autumnItems.add(new Product(fairySeeds, 5.0));

        Item rareSeed = new Seed(SeedType.RareSeed);
        rareSeed.setPrice(1000);
        autumnItems.add(new Product(rareSeed, 1.0));

        Item wheatSeedsAutumn = new Seed(SeedType.WheatSeeds);
        wheatSeedsAutumn.setPrice(12);
        autumnItems.add(new Product(wheatSeedsAutumn, 5.0));

        //Winter Stock
        Item powdermelonSeeds = new Seed(SeedType.PowdermelonSeeds);
        powdermelonSeeds.setPrice(20);
        winterItems.add(new Product(powdermelonSeeds, 5.0));

        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(x,y,items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu , "Joja Market" , tileType);
    }

    private static Market createPierreGeneralStore() {
        int x = 30;  // Left side of village center
        int y = 125;  // Upper area of village
        TileType tileType = TileType.PIERRE_GENERAL_STORE;
        List<Product> items = new ArrayList<>();
        List<Product> springItems = new ArrayList<>();
        List<Product> summerItems = new ArrayList<>();
        List<Product> autumnItems = new ArrayList<>();
        List<Product> winterItems = new ArrayList<>();

        // permanent stock
        Item rice = new Item("Rice", 200, "content/Crops/Rice.png" , "A basic grain often served under vegetables.");
        items.add(new Product(rice, Double.POSITIVE_INFINITY));

        Item bouquet = new Item("Bouquet" , 100 , "content/Bouquet.png" , "is a carefully arranged and often tied bundle of flowers");
        items.add(new Product(bouquet, 2.0));

        Item wheatFlour = new Item("Wheat Flour", 100, "content/Farming/Wheat_Flour.png" , "A common cooking ingredient made from crushed wheat seeds.");
        items.add(new Product(wheatFlour, Double.POSITIVE_INFINITY));

        Item weddingRing = new Item("Wedding Ring" , 10_000 , "content/Crafting/Wedding_Ring.png" , "It's used to ask for another farmer's hand in marriage. " +
            "(Unlocked after reaching level 3 friendship with a player)");
        items.add(new Product(weddingRing, 2.0));

        Item dehydrator = new CraftingItem(CraftingType.Dehydrator);
        dehydrator.setPrice(10_000);
        items.add(new Product(dehydrator, 1.0));

        Item grassStarter = new Item("Grass Starter" , 1000 , "content/Grass_Starter.png" , "A recipe to make Grass Starter");
        items.add(new Product(grassStarter, 1.0));


        Item sugar = new Item("Sugar", 100, "content/Crops/Sugar.png" , "Adds sweetness to pastries and candies. Too much can be unhealthy.");
        items.add(new Product(sugar, Double.POSITIVE_INFINITY));

        // Backpack upgrades
        Item largePack = new Item("Large Pack", 2000, "content/Tools/36_Backpack.png", "Upgrade your backpack to hold 24 items.");
        items.add(new Product(largePack, 1.0));

        Item deluxePack = new Item("Deluxe Pack", 10000, "content/Tools/36_Backpack.png", "Upgrade your backpack to hold unlimited items.");
        items.add(new Product(deluxePack, 1.0));

        Item soil = new Item("Deluxe Retaining Soil" , 150 , "content/Crafting/Deluxe_Retaining_Soil.png" ,"This soil has a 100% chance of staying watered overnight. Mix into tilled soil.");
        items.add(new Product(soil, Double.POSITIVE_INFINITY));

        Item speedGrow = new Item("Speed-Gro" , 100 , "content/Crafting/Speed-Gro.png" , "Makes the plants grow 1 day earlier.");
        items.add(new Product(speedGrow, Double.POSITIVE_INFINITY));

        Item oil = new ArtisanItem(ArtisanType.Oil);
        oil.setPrice(200);
        items.add(new Product(oil, Double.POSITIVE_INFINITY));

        Item vinegar = new ArtisanItem(ArtisanType.Vinegar);
        vinegar.setPrice(100);
        items.add(new Product(vinegar, Double.POSITIVE_INFINITY));


        Item appleSapling = new Tree(TreeType.AppleTree);
        appleSapling.setPrice(4_000);
        items.add(new Product(appleSapling, Double.POSITIVE_INFINITY));

        Item apricotSapling = new Tree(TreeType.ApricotTree);
        apricotSapling.setPrice(2_000);
        items.add(new Product(apricotSapling, Double.POSITIVE_INFINITY));

        Item cherrySapling = new Tree(TreeType.CherryTree);
        cherrySapling.setPrice(3_400);
        items.add(new Product(cherrySapling, Double.POSITIVE_INFINITY));

        Item orangeSapling = new Tree(TreeType.OrangeTree);
        orangeSapling.setPrice(4_000);
        items.add(new Product(orangeSapling, Double.POSITIVE_INFINITY));

        Item peachSapling = new Tree(TreeType.PeachTree);
        peachSapling.setPrice(6_000);
        items.add(new Product(peachSapling, Double.POSITIVE_INFINITY));

        Item pomegranateSapling = new Tree(TreeType.PomegranateTree);
        pomegranateSapling.setPrice(4_000);
        items.add(new Product(pomegranateSapling, Double.POSITIVE_INFINITY));



        Item basicRetainingSoil = new Item("Basic Retaining Soil" , 100 , "content/Crafting/Basic_Retaining_Soil.png" , "This soil has a chance of staying watered overnight. Mix into tilled soil.");
        items.add(new Product(basicRetainingSoil, Double.POSITIVE_INFINITY));

        Item qualityRetainingSoil = new Item("Quality Retaining Soil" , 150 , "content/Crafting/Quality_Retaining_Soil.png" ,"This soil has a good chance of staying watered overnight. Mix into tilled soil.");
        items.add(new Product(qualityRetainingSoil, Double.POSITIVE_INFINITY));


        //Spring stock
        Item parsnipSeeds = new Plant(PlantType.Parsnip);
        parsnipSeeds.setPrice(20);
        springItems.add(new Product(parsnipSeeds, 5.0));
        parsnipSeeds.setPrice(30);
        summerItems.add(new Product(parsnipSeeds, 5.0));
        autumnItems.add(new Product(parsnipSeeds, 5.0));
        winterItems.add(new Product(parsnipSeeds, 5.0));

        Item beanStarter = new Seed(SeedType.BeanStarter);
        beanStarter.setPrice(60);
        springItems.add(new Product(beanStarter, 5.0));
        beanStarter.setPrice(90);
        summerItems.add(new Product(beanStarter, 5.0));
        autumnItems.add(new Product(beanStarter, 5.0));
        winterItems.add(new Product(beanStarter, 5.0));

        Item cauliflowerSeeds = new Seed(SeedType.CauliflowerSeeds);
        cauliflowerSeeds.setPrice(80);
        springItems.add(new Product(cauliflowerSeeds, 5.0));
        cauliflowerSeeds.setPrice(120);
        summerItems.add(new Product(cauliflowerSeeds, 5.0));
        autumnItems.add(new Product(cauliflowerSeeds, 5.0));
        winterItems.add(new Product(cauliflowerSeeds, 5.0));

        Item potatoSeeds = new Seed(SeedType.PotatoSeeds);
        potatoSeeds.setPrice(50);
        springItems.add(new Product(potatoSeeds, 5.0));
        potatoSeeds.setPrice(75);
        summerItems.add(new Product(potatoSeeds, 5.0));
        autumnItems.add(new Product(potatoSeeds, 5.0));
        winterItems.add(new Product(potatoSeeds, 5.0));

        Item tulipBulb = new Seed(SeedType.TulipBulb);
        tulipBulb.setPrice(20);
        springItems.add(new Product(tulipBulb, 5.0));
        tulipBulb.setPrice(30);
        summerItems.add(new Product(tulipBulb, 5.0));
        autumnItems.add(new Product(tulipBulb, 5.0));
        winterItems.add(new Product(tulipBulb, 5.0));

        Item kaleSeeds = new Seed(SeedType.KaleSeeds);
        kaleSeeds.setPrice(70);
        springItems.add(new Product(kaleSeeds, 5.0));
        kaleSeeds.setPrice(105);
        summerItems.add(new Product(kaleSeeds, 5.0));
        autumnItems.add(new Product(kaleSeeds, 5.0));
        winterItems.add(new Product(kaleSeeds, 5.0));

        Item jazzSeeds = new Seed(SeedType.JazzSeeds);
        jazzSeeds.setPrice(30);
        springItems.add(new Product(jazzSeeds, 5.0));
        jazzSeeds.setPrice(45);
        summerItems.add(new Product(jazzSeeds, 5.0));
        autumnItems.add(new Product(jazzSeeds, 5.0));
        winterItems.add(new Product(jazzSeeds, 5.0));

        Item garlicSeeds = new Seed(SeedType.GarlicSeeds);
        garlicSeeds.setPrice(40);
        springItems.add(new Product(garlicSeeds, 5.0));
        garlicSeeds.setPrice(60);
        summerItems.add(new Product(garlicSeeds, 5.0));
        autumnItems.add(new Product(garlicSeeds, 5.0));
        winterItems.add(new Product(garlicSeeds, 5.0));

        Item riceShoot = new Seed(SeedType.RiceShoot);
        riceShoot.setPrice(40);
        springItems.add(new Product(riceShoot, 5.0));
        riceShoot.setPrice(60);
        summerItems.add(new Product(riceShoot, 5.0));
        autumnItems.add(new Product(riceShoot, 5.0));
        winterItems.add(new Product(riceShoot, 5.0));

        //Summer Stock:
        Item melonSeed = new Seed(SeedType.MelonSeeds);
        melonSeed.setPrice(80);
        summerItems.add(new Product(melonSeed, 5.0));
        melonSeed.setPrice(120);
        springItems.add(new Product(melonSeed, 5.0));
        autumnItems.add(new Product(melonSeed, 5.0));
        winterItems.add(new Product(melonSeed, 5.0));

        Item tomatoSeed = new Seed(SeedType.TomatoSeeds);
        tomatoSeed.setPrice(50);
        summerItems.add(new Product(tomatoSeed, 5.0));
        tomatoSeed.setPrice(75);
        springItems.add(new Product(tomatoSeed, 5.0));
        autumnItems.add(new Product(tomatoSeed, 5.0));
        winterItems.add(new Product(tomatoSeed, 5.0));

        Item BlueberrySeed = new Seed(SeedType.BlueberrySeeds);
        BlueberrySeed.setPrice(80);
        summerItems.add(new Product(BlueberrySeed, 5.0));
        BlueberrySeed.setPrice(120);
        springItems.add(new Product(BlueberrySeed, 5.0));
        autumnItems.add(new Product(BlueberrySeed, 5.0));
        winterItems.add(new Product(BlueberrySeed, 5.0));

        Item pepperSeed = new Seed(SeedType.PepperSeeds);
        pepperSeed.setPrice(40);
        summerItems.add(new Product(pepperSeed, 5.0));
        pepperSeed.setPrice(60);
        springItems.add(new Product(pepperSeed, 5.0));
        autumnItems.add(new Product(pepperSeed, 5.0));
        winterItems.add(new Product(pepperSeed, 5.0));

        Item wheatSeed = new Seed(SeedType.WheatSeeds);
        wheatSeed.setPrice(10);
        summerItems.add(new Product(wheatSeed, 5.0));
        wheatSeed.setPrice(15);
        springItems.add(new Product(wheatSeed, 5.0));
        autumnItems.add(new Product(wheatSeed, 5.0));
        winterItems.add(new Product(wheatSeed, 5.0));

        Item radishSeed = new Seed(SeedType.RadishSeeds);
        radishSeed.setPrice(40);
        summerItems.add(new Product(radishSeed, 5.0));
        radishSeed.setPrice(60);
        springItems.add(new Product(radishSeed, 5.0));
        autumnItems.add(new Product(radishSeed, 5.0));
        winterItems.add(new Product(radishSeed, 5.0));

        Item poppySeed = new Seed(SeedType.PoppySeeds);
        poppySeed.setPrice(100);
        summerItems.add(new Product(poppySeed, 5.0));
        poppySeed.setPrice(150);
        springItems.add(new Product(poppySeed, 5.0));
        autumnItems.add(new Product(poppySeed, 5.0));
        winterItems.add(new Product(poppySeed, 5.0));

        Item spangleSeed = new Seed(SeedType.SpangleSeeds);
        spangleSeed.setPrice(50);
        summerItems.add(new Product(spangleSeed, 5.0));
        spangleSeed.setPrice(75);
        springItems.add(new Product(spangleSeed, 5.0));
        autumnItems.add(new Product(spangleSeed, 5.0));
        winterItems.add(new Product(spangleSeed, 5.0));

        Item hopStarter = new Item("Hops Starter" , 60 ,  "content/Plants/Hops_Starter.png" ,"Plant these in the summer. Takes 11 days to grow, but keeps producing after that. Grows on a trellis.");
        summerItems.add(new Product(hopStarter, 5.0));
        hopStarter.setPrice(90);
        springItems.add(new Product(hopStarter, 5.0));
        autumnItems.add(new Product(hopStarter, 5.0));
        winterItems.add(new Product(hopStarter, 5.0));

        Item cornSeeds = new Seed(SeedType.CornSeeds);
        cornSeeds.setPrice(150);
        summerItems.add(new Product(cornSeeds, 5.0));
        cornSeeds.setPrice(225);
        springItems.add(new Product(cornSeeds, 5.0));
        autumnItems.add(new Product(cornSeeds, 5.0));
        winterItems.add(new Product(cornSeeds, 5.0));

        Item sunflowerSeeds = new Seed(SeedType.SunflowerSeeds);
        sunflowerSeeds.setPrice(200);
        summerItems.add(new Product(sunflowerSeeds, 5.0));
        sunflowerSeeds.setPrice(300);
        springItems.add(new Product(sunflowerSeeds, 5.0));
        autumnItems.add(new Product(sunflowerSeeds, 5.0));
        winterItems.add(new Product(sunflowerSeeds, 5.0));

        Item redCabbageSeeds = new Seed(SeedType.RedCabbageSeeds);
        redCabbageSeeds.setPrice(100);
        summerItems.add(new Product(redCabbageSeeds, 5.0));
        redCabbageSeeds.setPrice(150);
        springItems.add(new Product(redCabbageSeeds, 5.0));
        autumnItems.add(new Product(redCabbageSeeds, 5.0));
        winterItems.add(new Product(redCabbageSeeds, 5.0));

        //Autumn Stock:
        Item eggPlantSeed = new Seed(SeedType.EggplantSeeds);
        eggPlantSeed.setPrice(20);
        autumnItems.add(new Product(eggPlantSeed, 5.0));
        eggPlantSeed.setPrice(30);
        springItems.add(new Product(eggPlantSeed, 5.0));
        summerItems.add(new Product(eggPlantSeed, 5.0));
        winterItems.add(new Product(eggPlantSeed, 5.0));

        Item pumpkinSeed = new Seed(SeedType.PumpkinSeeds);
        pumpkinSeed.setPrice(100);
        autumnItems.add(new Product(pumpkinSeed, 5.0));
        pumpkinSeed.setPrice(150);
        springItems.add(new Product(pumpkinSeed, 5.0));
        summerItems.add(new Product(pumpkinSeed, 5.0));
        winterItems.add(new Product(pumpkinSeed, 5.0));

        Item bokChoySeed = new Seed(SeedType.BokChoySeeds);
        bokChoySeed.setPrice(50);
        autumnItems.add(new Product(bokChoySeed, 5.0));
        bokChoySeed.setPrice(75);
        springItems.add(new Product(bokChoySeed, 5.0));
        summerItems.add(new Product(bokChoySeed, 5.0));
        winterItems.add(new Product(bokChoySeed, 5.0));

        Item yamSeed = new Seed(SeedType.YamSeeds);
        yamSeed.setPrice(60);
        autumnItems.add(new Product(yamSeed, 5.0));
        yamSeed.setPrice(90);
        springItems.add(new Product(yamSeed, 5.0));
        summerItems.add(new Product(yamSeed, 5.0));
        winterItems.add(new Product(yamSeed, 5.0));

        Item cranberrySeed = new Seed(SeedType.CranberrySeeds);
        cranberrySeed.setPrice(240);
        autumnItems.add(new Product(cranberrySeed, 5.0));
        cranberrySeed.setPrice(360);
        springItems.add(new Product(cranberrySeed, 5.0));
        summerItems.add(new Product(cranberrySeed, 5.0));
        winterItems.add(new Product(cranberrySeed, 5.0));

        Item fairySeed = new Seed(SeedType.FairySeeds);
        fairySeed.setPrice(200);
        autumnItems.add(new Product(fairySeed, 5.0));
        fairySeed.setPrice(300);
        springItems.add(new Product(fairySeed, 5.0));
        summerItems.add(new Product(fairySeed, 5.0));
        winterItems.add(new Product(fairySeed, 5.0));

        Item amaranthSeed = new Seed(SeedType.AmaranthSeeds);
        amaranthSeed.setPrice(70);
        autumnItems.add(new Product(amaranthSeed, 5.0));
        amaranthSeed.setPrice(105);
        springItems.add(new Product(amaranthSeed, 5.0));
        summerItems.add(new Product(amaranthSeed, 5.0));
        winterItems.add(new Product(amaranthSeed, 5.0));


        Item grapeStarter = new Seed(SeedType.GrapeStarter);
        grapeStarter.setPrice(60);
        autumnItems.add(new Product(grapeStarter, 5.0));
        grapeStarter.setPrice(90);
        springItems.add(new Product(grapeStarter, 5.0));
        summerItems.add(new Product(grapeStarter, 5.0));
        winterItems.add(new Product(grapeStarter, 5.0));

        Item artichokeSeed = new Seed(SeedType.ArtichokeSeeds);
        artichokeSeed.setPrice(30);
        autumnItems.add(new Product(artichokeSeed, 5.0));
        artichokeSeed.setPrice(45);
        springItems.add(new Product(artichokeSeed, 5.0));
        summerItems.add(new Product(artichokeSeed, 5.0));
        winterItems.add(new Product(artichokeSeed, 5.0));

        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(x,y,items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu, "Pierre General Store" , tileType);
    }

    private static Market createCarpentersShop() {
        int x = 45;  // Right side of village center
        int y = 125;  // Upper area of village
        TileType tileType = TileType.CARPENTERS_SHOP;
        List<Product> items = new ArrayList<>();
        List<Product> springItems = new ArrayList<>();
        List<Product> summerItems = new ArrayList<>();
        List<Product> autumnItems = new ArrayList<>();
        List<Product> winterItems = new ArrayList<>();


        Item wood = new Mineral(MineralType.Wood);
        items.add(new Product(wood, Double.POSITIVE_INFINITY));

        Item stone = new Mineral(MineralType.Stone);
        items.add(new Product(stone, Double.POSITIVE_INFINITY));

        Item barn = new Item("Barn" , 6_000 , "content/Buildings/Barn.png");
        items.add(new Product(barn,1.0));
        Item bigBarn = new Item("Big Barn" , 12_000 , "content/Buildings/Big Barn.png");
        items.add(new Product(bigBarn,1.0));
        Item deluxeBarn = new Item("Deluxe Barn" , 25_000 , "content/Buildings/Deluxe Barn.png");
        items.add(new Product(deluxeBarn,1.0));


        Item coop = new Item("Coop" , 4_000 , "content/Buildings/Coop.png");
        items.add(new Product(coop,1.0));
        Item bigCoop = new Item("Big Coop" , 10_000 , "content/Buildings/Big Coop.png");
        items.add(new Product(bigCoop,1.0));
        Item deluxeCoop = new Item("Deluxe Coop" , 20_000 , "content/Buildings/Deluxe Coop.png");
        items.add(new Product(deluxeCoop,1.0));

        Item well = new Item("Well" , 1_000 , "content/Buildings/Well.png");
        items.add(new Product(well,1.0));
        Item shippingBin = new ShippingBin();
        items.add(new Product(shippingBin,Double.POSITIVE_INFINITY));

        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(x,y,items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu , "Carpenters Shop" , tileType);
    }

    private static Market createMarnieShop() {
        int x = 25;  // Left side of village center
        int y = 135; // Upper area of village
        TileType tileType = TileType.MARNIE_SHOP;
        List<Product> items = new ArrayList<>();
        List<Product> springItems = new ArrayList<>();
        List<Product> summerItems = new ArrayList<>();
        List<Product> autumnItems = new ArrayList<>();
        List<Product> winterItems = new ArrayList<>();

        Item shears = new Tool("Shears", 1000, "content/Tools/shears/Shears.png", "Use this to collect wool from sheep", 
            Tool.ToolType.SHEARS, Tool.ToolMaterial.BASIC, 4, Skills.FARMING, ToolFunctionality.SHEARS);
        items.add(new Product(shears, 1.0));

        Item milkPail = new Tool("Milk Pail", 1000, "content/Tools/Milk_Pail.png", "Gather milk from your animals.", 
            Tool.ToolType.MILK_PAIL, Tool.ToolMaterial.BASIC, 4, Skills.FARMING, ToolFunctionality.MILK_PAIL);
        items.add(new Product(milkPail, 1.0));

        Item chicken = new CoopAnimal(CoopAnimalTypes.CHICKEN, "Chicken");
        chicken.setPrice(800);
        items.add(new Product(chicken, 2.0));

        Item cow = new BarnAnimal(BarnAnimalTypes.COW , "Cow");
        cow.setPrice(1_500);
        items.add(new Product(cow, 2.0));

        Item goat = new BarnAnimal( BarnAnimalTypes.GOAT , "Goat");
        goat.setPrice(4_000);
        items.add(new Product(goat, 2.0));

        Item duck = new CoopAnimal(CoopAnimalTypes.DUCK, "Duck");
        duck.setPrice(1_200);
        items.add(new Product(duck, 2.0));

        Item sheep = new BarnAnimal( BarnAnimalTypes.SHEEP, "Sheep");
        sheep.setPrice(8_000);
        items.add(new Product(sheep, 2.0));

        Item rabbit = new CoopAnimal(CoopAnimalTypes.RABBIT, "Rabbit");
        rabbit.setPrice(8_500);
        items.add(new Product(rabbit, 2.0));

        Item dinosaur = new CoopAnimal(CoopAnimalTypes.DINOSAUR, "Dinosaur");
        dinosaur.setPrice(14_000);
        items.add(new Product(dinosaur, 2.0));

        Item pig = new BarnAnimal(BarnAnimalTypes.PIG, "Pig");
        pig.setPrice(16_000);
        items.add(new Product(pig, 2.0));


        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(x,y,items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu , "Marnie Shop" , tileType);
    }

    private static Market createStarDropSaloon() {
        int x = 50;  // Right side of village center
        int y = 135; // Upper area of village
        TileType tileType = TileType.STARDROP_SALOON;
        List<Product> items = new ArrayList<>();
        List<Product> springItems = new ArrayList<>();
        List<Product> summerItems = new ArrayList<>();
        List<Product> autumnItems = new ArrayList<>();
        List<Product> winterItems = new ArrayList<>();


        Item beer = new ArtisanItem(ArtisanType.Beer);
        beer.setPrice(400);
        items.add(new Product(beer, Double.POSITIVE_INFINITY));

        Item salad = new CookingItem(CookingType.Salad).getFood();
        salad.setPrice(220);
        items.add(new Product(salad, Double.POSITIVE_INFINITY));

        Item bread = new CookingItem(CookingType.Bread).getFood();
        bread.setPrice(120);
        items.add(new Product(bread, Double.POSITIVE_INFINITY));

        Item spaghetti = new CookingItem(CookingType.Spaghetti).getFood();
        spaghetti.setPrice(240);
        items.add(new Product(spaghetti, Double.POSITIVE_INFINITY));

        Item pizza = new CookingItem(CookingType.Pizza).getFood();
        pizza.setPrice(600);
        items.add(new Product(pizza, Double.POSITIVE_INFINITY));


        Item coffee = new ArtisanItem(ArtisanType.Coffee);
        coffee.setPrice(300);
        items.add(new Product(coffee, Double.POSITIVE_INFINITY));


        Item hashBrownsRecipe = new CookingItem(CookingType.HashBrowns);
        hashBrownsRecipe.setPrice(50);
        items.add(new Product(hashBrownsRecipe, 1.0));

        Item omeletRecipe = new CookingItem(CookingType.Omelet);
        omeletRecipe.setPrice(100);
        items.add(new Product(omeletRecipe, 1.0));

        Item pancakesRecipe = new CookingItem(CookingType.Pancakes);
        pancakesRecipe.setPrice(100);
        items.add(new Product(pancakesRecipe, 1.0));

        Item breadRecipe = new CookingItem(CookingType.Bread);
        breadRecipe.setPrice(100);
        items.add(new Product(breadRecipe, 1.0));

        Item tortillaRecipe = new CookingItem(CookingType.Tortilla);
        tortillaRecipe.setPrice(100);
        items.add(new Product(tortillaRecipe, 1.0));

        Item makiRoll = new CookingItem(CookingType.MakiRoll);
        makiRoll.setPrice(300);
        items.add(new Product(makiRoll, 1.0));

        Item tripleShotEspresso = new CookingItem(CookingType.TripleShotEspresso);
        tripleShotEspresso.setPrice(5_000);
        items.add(new Product(tripleShotEspresso, 1.0));

        Item pizzaRecipe = new CookingItem(CookingType.Pizza);
        pizzaRecipe.setPrice(150);
        items.add(new Product(pizzaRecipe, 1.0));

        Item cookieRecipe = new CookingItem(CookingType.Cookie);
        cookieRecipe.setPrice(300);
        items.add(new Product(cookieRecipe, 1.0));

        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(x,y,items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu , "Star Drop Saloon" , tileType);
    }

    private static Market createFishShop() {
        int x = 37;  // Center of village
        int y = 140; // Upper area of village
        TileType tileType = TileType.FISH_SHOP;
        List<Product> items = new ArrayList<>();
        List<Product> springItems = new ArrayList<>();
        List<Product> summerItems = new ArrayList<>();
        List<Product> autumnItems = new ArrayList<>();
        List<Product> winterItems = new ArrayList<>();

        Item fishSmoker = new CraftingItem(CraftingType.FishSmoker);
        fishSmoker.setPrice(10_000);
        items.add(new Product(fishSmoker, 1.0));

        Item troutSoup = new Item("Trout Soup", 250, "content/Recipe/Trout_Soup.png" , "Pretty salty.");
        items.add(new Product(troutSoup, 1.0));

        // Fishing rods with proper Tool functionality
        Item trainingRod = new Tool("Training Rod", 25, "content/Tools/Fishing_Pole/Training_Rod.png", 
            "It's a lot easier to use than other rods, but can only catch basic fish.",
            Tool.ToolType.FISHING_ROD, Tool.ToolMaterial.BASIC, 8, Skills.FISHING, ToolFunctionality.FISHING_ROD);
        items.add(new Product(trainingRod, 1.0));

        Item bambooPole = new Tool("Bamboo Pole", 500, "content/Tools/Fishing_Pole/Bamboo_Pole.png", 
            "Use in the water to catch fish.",
            Tool.ToolType.FISHING_ROD, Tool.ToolMaterial.BASIC, 8, Skills.FISHING, ToolFunctionality.FISHING_ROD);
        items.add(new Product(bambooPole, 1.0));

        Item fiberglassRod = new Tool("Fiberglass Rod", 1800, "content/Tools/Fishing_Pole/Fiberglass_Rod.png", 
            "Use in the water to catch fish.",
            Tool.ToolType.FISHING_ROD, Tool.ToolMaterial.COPPER, 6, Skills.FISHING, ToolFunctionality.FISHING_ROD);
        items.add(new Product(fiberglassRod, 1.0));

        Item iridiumRod = new Tool("Iridium Rod", 7500, "content/Tools/Fishing_Pole/Advanced_Iridium_Rod.png", 
            "Use in the water to catch fish.",
            Tool.ToolType.FISHING_ROD, Tool.ToolMaterial.IRIDIUM, 4, Skills.FISHING, ToolFunctionality.FISHING_ROD);
        items.add(new Product(iridiumRod, 1.0));

        int startHour = 9;
        int endHour = 16;
        String[] menu = new String[]{};
        return new Market(x,y,items, springItems, summerItems, autumnItems, winterItems, startHour, endHour, menu , "Fish Shop" , tileType);
    }

    public Market createMarket() {
        return this.market;
    }
}
