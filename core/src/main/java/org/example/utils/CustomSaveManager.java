package org.example.utils;

import org.example.common.models.Items.*;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.MapDetails.GameMap;
import org.example.common.models.MapDetails.Village;
import org.example.common.models.MapDetails.Lake;
import org.example.common.models.Player.Backpack;
import org.example.common.models.Player.Player;
import org.example.common.models.Player.Skill;
import org.example.common.models.common.Date;
import org.example.common.models.common.Location;
import org.example.common.models.entities.Game;
import org.example.common.models.entities.User;
import org.example.common.models.entities.animal.Animal;
import org.example.common.models.entities.animal.BarnAnimal;
import org.example.common.models.entities.animal.CoopAnimal;
import org.example.common.models.enums.PlayerEnums.Gender;
import org.example.common.models.enums.Seasons;
import org.example.common.models.enums.Types.*;
import org.example.common.models.enums.Weather;
import org.example.common.models.MapDetails.Building;
import org.example.common.models.Player.Refrigerator;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomSaveManager {

    private static final int SAVE_FORMAT_VERSION = 1;

    public static void saveGame(Game game, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             DataOutputStream dos = new DataOutputStream(fos)) {

            // 1.writing header
            dos.writeInt(SAVE_FORMAT_VERSION);
            dos.writeLong(System.currentTimeMillis());

            // 2.writing game
            dos.writeUTF(game.getSaveName() != null ? game.getSaveName() : "");
            dos.writeBoolean(game.isMultiplayer());
            dos.writeInt(game.getCurrentPlayerIndex());
            dos.writeBoolean(game.isInFarmSelectionPhase());
            dos.writeBoolean(game.isSaved());

            // Save Date
            Date date = game.getDate();
            dos.writeInt(date.getDay());
            dos.writeInt(date.getSeason().ordinal());
            dos.writeInt(date.getHour());
            dos.writeInt(date.getMinutes());
            dos.writeInt(date.getWeatherToday().ordinal());
            dos.writeInt(date.getWeatherTomorrow().ordinal());


            // 3.writing players
            dos.writeInt(game.getPlayers().size());
            for (Player player : game.getPlayers()) {
                savePlayer(dos, player);
            }

            // 4.writing game map
            saveGameMap(dos, game.getGameMap());
        }
    }


    public static Game loadGame(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             DataInputStream dis = new DataInputStream(fis)) {

            // 1.reading header
            int version = dis.readInt();
            if (version != SAVE_FORMAT_VERSION) {
                throw new IOException("Unsupported save format version!");
            }
            long saveTimestamp = dis.readLong();

            // 2.reading game
            Game game = new Game();
            game.setSaveName(dis.readUTF());
            game.isMultiplayer = dis.readBoolean();
            game.setCurrentPlayerIndex(dis.readInt());
            game.setInFarmSelectionPhase(dis.readBoolean());
            game.setSaved(dis.readBoolean());

            // Load Date
            Date date = new Date();
            date.setDay(dis.readInt());
            date.setSeason(dis.readInt());
            date.setHour(dis.readInt());
            date.setMinute(dis.readInt());
            date.setWeatherToday(Weather.values()[dis.readInt()]);
            date.setWeatherTomorrow(Weather.values()[dis.readInt()]);
            game.setDate(date);


            // 3.reading players
            int playerCount = dis.readInt();
            List<Player> players = new ArrayList<>();
            for (int i = 0; i < playerCount; i++) {
                players.add(loadPlayer(dis));
            }
            game.setPlayers(players);
            game.setCurrentPlayer(players.get(game.getCurrentPlayerIndex()));


            // 4.reading game map
            game.setGameMap(loadGameMap(dis, players));


            return game;
        }
    }

    private static void savePlayer(DataOutputStream dos, Player player) throws IOException {
        // User
        dos.writeUTF(player.getUser().getUsername());
        dos.writeUTF(player.getUser().getNickname());
        dos.writeUTF(player.getUser().getEmail());
        dos.writeUTF(player.getUser().getPassword());
        dos.writeInt(player.getUser().getGender().ordinal());


        // Skills
        dos.writeInt(player.getSkills().size());
        for (Skill skill : player.getSkills()) {
            dos.writeUTF(skill.getName());
            dos.writeInt(skill.getLevel());
            dos.writeInt(skill.getUnits());
        }

        // Crafting Items
        dos.writeInt(player.getCraftingItems().size());
        for (CraftingItem item : player.getCraftingItems()) {
            dos.writeUTF(item.getName());
        }

        // Cooking Items
        dos.writeInt(player.getCookingItems().size());
        for (CookingItem item : player.getCookingItems()) {
            dos.writeUTF(item.getName());
        }

        // Backpack
        Backpack backpack = player.getBackpack();
        dos.writeInt(backpack.getInventory().size());
        for (Map.Entry<Item, Integer> entry : backpack.getInventory().entrySet()) {
            saveItem(dos, entry.getKey());
            dos.writeInt(entry.getValue());
        }
        dos.writeInt(backpack.getType().ordinal());

        // Player Stats
        dos.writeInt(player.getEnergy());
        dos.writeBoolean(player.isEnergyUnlimited());
        dos.writeBoolean(player.hasCollapsed());
        dos.writeInt(player.getMoney());
        dos.writeBoolean(player.isMarried());
        dos.writeFloat(player.getPosX());
        dos.writeFloat(player.getPosY());

        // Current Tool
        if (player.getCurrentTool() != null) {
            dos.writeBoolean(true);
            dos.writeUTF(player.getCurrentTool().getName());
        } else {
            dos.writeBoolean(false);
        }

        // Current Item
        if (player.getCurrentItem() != null) {
            dos.writeBoolean(true);
            saveItem(dos, player.getCurrentItem());
        } else {
            dos.writeBoolean(false);
        }
    }

    private static Player loadPlayer(DataInputStream dis) throws IOException {
        // User
        String username = dis.readUTF();
        String nickname = dis.readUTF();
        String email = dis.readUTF();
        String password = dis.readUTF();
        Gender gender = Gender.values()[dis.readInt()];
        User user = new User(username, password, email, nickname, gender);
        Player player = new Player(user);

        // Skills
        int skillCount = dis.readInt();
        for (int i = 0; i < skillCount; i++) {
            String skillName = dis.readUTF();
            int level = dis.readInt();
            int units = dis.readInt();
            player.getSkillByName(skillName).setLevel(level);
            player.getSkillByName(skillName).setUnits(units);
        }

        // Crafting Items
        int craftingItemCount = dis.readInt();
        for (int i = 0; i < craftingItemCount; i++) {
            String itemName = dis.readUTF();
            player.addCraftingItem((CraftingItem) ItemBuilder.build(itemName));
        }

        // Cooking Items
        int cookingItemCount = dis.readInt();
        for (int i = 0; i < cookingItemCount; i++) {
            String itemName = dis.readUTF();
            player.addCookingItem((CookingItem) ItemBuilder.build(itemName));
        }

        // Backpack
        int inventorySize = dis.readInt();
        for (int i = 0; i < inventorySize; i++) {
            Item item = loadItem(dis);
            int quantity = dis.readInt();
            player.getBackpack().add(item, quantity);
        }
        player.getBackpack().setType(Backpack.Type.values()[dis.readInt()]);


        // Player Stats
        player.setEnergy(dis.readInt());
        if (dis.readBoolean()) player.setEnergyUnlimited();
        player.setCollapsed(dis.readBoolean());
        player.setMoney(dis.readInt());
        if (dis.readBoolean()) player.marry(null); // Spouse will be linked later
        player.setPosX(dis.readFloat());
        player.setPosY(dis.readFloat());

        // Current Tool
        if (dis.readBoolean()) {
            String toolName = dis.readUTF();
            Item item = ItemBuilder.build(toolName);
            if(item != null) {
                player.equipTool(toolName);
            }
        }

        // Current Item
        if (dis.readBoolean()) {
            Item item = loadItem(dis);
            if(item != null) {
                player.setCurrentItem(item);
            }
        }


        return player;
    }

    private static void saveGameMap(DataOutputStream dos, GameMap gameMap) throws IOException {
        // Farms
        dos.writeInt(gameMap.getFarms().size());
        for (Farm farm : gameMap.getFarms()) {
            saveFarm(dos, farm);
        }

        // Village
        saveVillage(dos, gameMap.getVillage());
    }

    private static GameMap loadGameMap(DataInputStream dis, List<Player> players) throws IOException {
        GameMap gameMap = new GameMap();

        // Farms
        int farmCount = dis.readInt();
        for (int i = 0; i < farmCount; i++) {
            gameMap.addFarm(loadFarm(dis, players));
        }

        // Village
        gameMap.setVillage(loadVillage(dis));

        return gameMap;
    }

    private static void saveFarm(DataOutputStream dos, Farm farm) throws IOException {
        dos.writeUTF(farm.getName());
        dos.writeUTF(farm.getOwner().getUser().getUsername());
        dos.writeBoolean(farm.getFarmType());
        dos.writeInt(farm.getFarmIndex());

        // Tiles
        for (int i = 0; i < Farm.width; i++) {
            for (int j = 0; j < Farm.height; j++) {
                saveLocation(dos, farm.getItem(i, j));
            }
        }
        //Animals
        dos.writeInt(farm.getAnimals().size());
        for (Animal animal : farm.getAnimals()) {
            saveAnimal(dos, animal);
        }
        //Building
        dos.writeBoolean(farm.getBuilding() != null);
        if (farm.getBuilding() != null) {
            saveBuilding(dos, farm.getBuilding());
        }

        // Lakes
        dos.writeInt(farm.getLakes().size());
        for(Lake lake : farm.getLakes()){
            saveLake(dos, lake);
        }

    }

    private static void saveLake(DataOutputStream dos, Lake lake) throws IOException {
        dos.writeInt(lake.getX());
        dos.writeInt(lake.getY());
        dos.writeInt(lake.getWidth());
        dos.writeInt(lake.getHeight());
        dos.writeUTF(lake.getName());
        dos.writeInt(lake.getType().ordinal());

        boolean hasMask = lake.getMask() != null;
        dos.writeBoolean(hasMask);
        if (hasMask) {
            boolean[][] mask = lake.getMask();
            for (int i = 0; i < lake.getHeight(); i++) {
                for (int j = 0; j < lake.getWidth(); j++) {
                    dos.writeBoolean(mask[i][j]);
                }
            }
        }
    }


    private static void saveBuilding(DataOutputStream dos, Building building) throws IOException {
        Refrigerator fridge = building.getRefrigerator();
        dos.writeInt(fridge.getItems().size());
        for (Map.Entry<Item, Integer> entry : fridge.getItems().entrySet()) {
            saveItem(dos, entry.getKey());
            dos.writeInt(entry.getValue());
        }
    }


    private static Farm loadFarm(DataInputStream dis, List<Player> players) throws IOException {
        String name = dis.readUTF();
        String ownerUsername = dis.readUTF();
        boolean farmType = dis.readBoolean();
        int farmIndex = dis.readInt();

        Player owner = players.stream().filter(p -> p.getUser().getUsername().equals(ownerUsername)).findFirst().orElse(null);

        Farm farm = new Farm(name, owner, farmType, farmIndex);

        // Tiles
        for (int i = 0; i < Farm.width; i++) {
            for (int j = 0; j < Farm.height; j++) {
                farm.getTiles()[i][j] = loadLocation(dis);
            }
        }

        //Animals here
        int animalCount = dis.readInt();
        for (int i = 0; i < animalCount; i++) {
            farm.addAnimal(loadAnimal(dis));
        }


        //Building here
        if (dis.readBoolean()) {
            Refrigerator fridge = farm.getBuilding().getRefrigerator();
            int fridgeItemCount = dis.readInt();
            for (int i = 0; i < fridgeItemCount; i++) {
                Item item = loadItem(dis);
                int quantity = dis.readInt();
                fridge.putItem(item, quantity);
            }
        }



        //List<Lake> lakes here
        int lakeCount = dis.readInt();
        for(int i = 0; i < lakeCount; i++){
            farm.getLakes().add(loadLake(dis));
        }


        //GreenHouse here


        //Quarry here


        //List<Barn> barns here


        //List<Coop> here


        //List<ShippingBin> shippingBins here

        return farm;
    }

    private static Lake loadLake(DataInputStream dis) throws IOException {
        int x = dis.readInt();
        int y = dis.readInt();
        int width = dis.readInt();
        int height = dis.readInt();
        String name = dis.readUTF();
        Lake.LakeType type = Lake.LakeType.values()[dis.readInt()];

        boolean hasMask = dis.readBoolean();
        boolean[][] mask = null;
        if (hasMask) {
            mask = new boolean[height][width];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    mask[i][j] = dis.readBoolean();
                }
            }
        }
        return new Lake(x, y, width, height, name, type, mask);
    }



    private static void saveVillage(DataOutputStream dos, Village village) throws IOException {
        dos.writeUTF(village.getName());

        // Tiles
        for (int i = 0; i < Village.width; i++) {
            for (int j = 0; j < Village.height; j++) {
                saveLocation(dos, village.getItem(i, j));
            }
        }
    }

    private static Village loadVillage(DataInputStream dis) throws IOException {
        String name = dis.readUTF();
        Village village = new Village(name);

        // Tiles
        for (int i = 0; i < Village.width; i++) {
            for (int j = 0; j < Village.height; j++) {
                village.getTiles()[i][j] = loadLocation(dis);
            }
        }

        return village;
    }

    private static void saveLocation(DataOutputStream dos, Location location) throws IOException {
        dos.writeInt(location.getTile().ordinal());
        dos.writeUTF(location.getType());
        dos.writeBoolean(location.getShokhm());
        dos.writeBoolean(location.isScarecrowThere());

        if (location.getItem() != null) {
            dos.writeBoolean(true);
            saveItem(dos, location.getItem());
        } else {
            dos.writeBoolean(false);
        }
    }

    private static Location loadLocation(DataInputStream dis) throws IOException {
        TileType tileType = TileType.values()[dis.readInt()];
        String type = dis.readUTF();
        boolean shokhm = dis.readBoolean();
        boolean isScarecrowThere = dis.readBoolean();

        Location location = new Location(0, 0, tileType); // Coordinates will be set by the calling method
        location.setType(type);
        location.setShokhm(shokhm);
        location.setScarecrowThere(isScarecrowThere);

        if (dis.readBoolean()) {
            location.setItem(loadItem(dis));
        }

        return location;
    }
    private static void saveItem(DataOutputStream dos, Item item) throws IOException {
        dos.writeUTF(item.getName());
        if (item instanceof ArtisanItem) {
            saveArtisanItem(dos, (ArtisanItem) item);
        } else if (item instanceof CookingItem) {
            saveCookingItem(dos, (CookingItem) item);
        } else if (item instanceof CraftingItem) {
            saveCraftingItem(dos, (CraftingItem) item);
        } else if (item instanceof Food) {
            saveFood(dos, (Food) item);
        } else if (item instanceof Fruit) {
            saveFruit(dos, (Fruit) item);
        } else if (item instanceof Mineral) {
            saveMineral(dos, (Mineral) item);
        } else if (item instanceof Plant) {
            savePlant(dos, (Plant) item);
        } else if (item instanceof Seed) {
            saveSeed(dos, (Seed) item);
        } else if (item instanceof Tree) {
            saveTree(dos, (Tree) item);
        }
    }

    private static Item loadItem(DataInputStream dis) throws IOException {
        String itemName = dis.readUTF();
        Item item = ItemBuilder.build(itemName);
        if (item instanceof ArtisanItem) {
            return loadArtisanItem(dis, item);
        } else if (item instanceof CookingItem) {
            return loadCookingItem(dis, item);
        } else if (item instanceof CraftingItem) {
            return loadCraftingItem(dis, item);
        } else if (item instanceof Food) {
            return loadFood(dis, item);
        } else if (item instanceof Fruit) {
            return loadFruit(dis, item);
        } else if (item instanceof Mineral) {
            return loadMineral(dis, item);
        } else if (item instanceof Plant) {
            return loadPlant(dis, item);
        } else if (item instanceof Seed) {
            return loadSeed(dis, item);
        } else if (item instanceof Tree) {
            return loadTree(dis, item);
        }
        return item;
    }
    private static void saveArtisanItem(DataOutputStream dos, ArtisanItem item) throws IOException {
        dos.writeInt(item.getProccessingTimeFinal());
    }

    private static ArtisanItem loadArtisanItem(DataInputStream dis, Item baseItem) throws IOException {
        ArtisanItem item = (ArtisanItem) baseItem;
        item.setProccessingTimeFinal(dis.readInt());
        return item;
    }

    private static void saveCookingItem(DataOutputStream dos, CookingItem item) throws IOException {
        dos.writeUTF(item.getType().name());
    }

    private static CookingItem loadCookingItem(DataInputStream dis, Item baseItem) throws IOException {
        CookingItem item = (CookingItem) baseItem;
        // The type is already set by the ItemBuilder, no need to load it again
        return item;
    }

    private static void saveCraftingItem(DataOutputStream dos, CraftingItem item) throws IOException {
        dos.writeDouble(item.getProgressBar());
        dos.writeInt(item.getPosX());
        dos.writeInt(item.getPosY());

        if (item.getProccessingItem() != null) {
            dos.writeBoolean(true);
            saveArtisanItem(dos, (ArtisanItem) item.getProccessingItem());
        } else {
            dos.writeBoolean(false);
        }

        if (item.getFinishedItem() != null) {
            dos.writeBoolean(true);
            saveArtisanItem(dos, (ArtisanItem) item.getFinishedItem());
        } else {
            dos.writeBoolean(false);
        }
    }

    private static CraftingItem loadCraftingItem(DataInputStream dis, Item baseItem) throws IOException {
        CraftingItem item = (CraftingItem) baseItem;
        item.setProgressBar(dis.readDouble());
        item.setPosX(dis.readInt());
        item.setPosY(dis.readInt());

        if (dis.readBoolean()) {
            item.setProccessingItem((ArtisanItem) loadItem(dis));
        }

        if (dis.readBoolean()) {
            item.setFinishedItem((ArtisanItem) loadItem(dis));
        }

        return item;
    }

    private static void saveFood(DataOutputStream dos, Food item) throws IOException {
        dos.writeInt(item.getEnergy());
        dos.writeUTF(item.getBuffer());
    }

    private static Food loadFood(DataInputStream dis, Item baseItem) throws IOException {
        Food item = (Food) baseItem;
        item.setEnergy(dis.readInt());
        // item.setBuffer(dis.readUTF()); // The buffer is applied on consumption, no need to save it
        return item;
    }

    private static void saveFruit(DataOutputStream dos, Fruit item) throws IOException {
        dos.writeInt(item.getEnergy());
    }

    private static Fruit loadFruit(DataInputStream dis, Item baseItem) throws IOException {
        Fruit item = (Fruit) baseItem;
        item.setEnergy(dis.readInt());
        return item;
    }

    private static void saveMineral(DataOutputStream dos, Mineral item) throws IOException {
        dos.writeBoolean(item.isMined());
    }

    private static Mineral loadMineral(DataInputStream dis, Item baseItem) throws IOException {
        Mineral item = (Mineral) baseItem;
        item.setMined(dis.readBoolean());
        return item;
    }

    private static void savePlant(DataOutputStream dos, Plant item) throws IOException {
        dos.writeInt(item.getStage());
        dos.writeInt(item.getDaysCounter());
        dos.writeBoolean(item.getFinished());
        dos.writeBoolean(item.getMoisture());
        dos.writeInt(item.getMoistureCounter());
        dos.writeBoolean(item.getIsGiant());
        dos.writeBoolean(item.isMoistureGod());
    }

    private static Plant loadPlant(DataInputStream dis, Item baseItem) throws IOException {
        Plant item = (Plant) baseItem;
        item.setStage(dis.readInt());
        item.setDaysCounter(dis.readInt());
        item.setFinished(dis.readBoolean());
        item.setMoisture(dis.readBoolean());
        item.setMoistureCounter(dis.readInt());
        if (dis.readBoolean()) {
            item.isGiant(item.getStage());
        }
        item.setMoistureGod(dis.readBoolean());
        return item;
    }

    private static void saveSeed(DataOutputStream dos, Seed item) throws IOException {
        // The type is already saved in the item name, no need to save it again
    }

    private static Seed loadSeed(DataInputStream dis, Item baseItem) throws IOException {
        return (Seed) baseItem;
    }

    private static void saveTree(DataOutputStream dos, Tree item) throws IOException {
        dos.writeInt(item.getStages().length);
        for (int stage : item.getStages()) {
            dos.writeInt(stage);
        }
        dos.writeInt(item.getFruitCounter());
        dos.writeInt(item.getFruitCycle());
        dos.writeBoolean(item.isFruitFinished());
        dos.writeInt(item.getStage());
        dos.writeInt(item.getDaysCounter());
        dos.writeBoolean(item.getFinished());
        dos.writeBoolean(item.getMoisture());
        dos.writeInt(item.getMoistureCounter());
        dos.writeBoolean(item.isMoistureGod());
    }

    private static Tree loadTree(DataInputStream dis, Item baseItem) throws IOException {
        Tree item = (Tree) baseItem;
        int[] stages = new int[dis.readInt()];
        for (int i = 0; i < stages.length; i++) {
            stages[i] = dis.readInt();
        }
        item.setStages(stages);
        item.setFruitCounter(dis.readInt());
        item.setFruitCycle(dis.readInt());
        item.setFruitFinished(dis.readBoolean());
        item.setStage(dis.readInt());
        item.setDaysCounter(dis.readInt());
        item.setFinished(dis.readBoolean());
        item.setMoisture(dis.readBoolean());
        item.setMoistureCounter(dis.readInt());
        item.setMoistureGod(dis.readBoolean());
        return item;
    }

    private static void saveAnimal(DataOutputStream dos, Animal animal) throws IOException {
        dos.writeUTF(animal.getName());
        dos.writeInt(animal.getPrice());
        dos.writeFloat(animal.getPosX());
        dos.writeFloat(animal.getPosY());
        dos.writeFloat(animal.getSpeed());
        dos.writeBoolean(animal.isMoving());
        dos.writeFloat(animal.getTargetX());
        dos.writeFloat(animal.getTargetY());
        dos.writeFloat(animal.getStateTimer());
        dos.writeInt(animal.getFacing().ordinal());

        if (animal instanceof BarnAnimal) {
            dos.writeBoolean(true); // It's a BarnAnimal
            BarnAnimal barnAnimal = (BarnAnimal) animal;
            dos.writeInt(barnAnimal.getType().ordinal());
            dos.writeInt(barnAnimal.getHappinessLevel());
            dos.writeInt(barnAnimal.getDaysSinceLastProduction());
            dos.writeBoolean(barnAnimal.isHasBeenFed());
            dos.writeBoolean(barnAnimal.isHasBeenPetToday());
            dos.writeBoolean(barnAnimal.isOutside());
        } else if (animal instanceof CoopAnimal) {
            dos.writeBoolean(false); // It's a CoopAnimal
            CoopAnimal coopAnimal = (CoopAnimal) animal;
            dos.writeInt(coopAnimal.getCoopType().ordinal());
            dos.writeInt(coopAnimal.getHappinessLevel());
            dos.writeInt(coopAnimal.getDaysSinceLastProduction());
            dos.writeBoolean(coopAnimal.isPetToday());
            dos.writeBoolean(coopAnimal.isHasBeenFed());
            dos.writeBoolean(coopAnimal.isOutside());
        }
    }

    private static Animal loadAnimal(DataInputStream dis) throws IOException {
        String name = dis.readUTF();
        int price = dis.readInt();
        float posX = dis.readFloat();
        float posY = dis.readFloat();
        float speed = dis.readFloat();
        boolean isMoving = dis.readBoolean();
        float targetX = dis.readFloat();
        float targetY = dis.readFloat();
        float stateTimer = dis.readFloat();
        Animal.Direction facing = Animal.Direction.values()[dis.readInt()];

        boolean isBarnAnimal = dis.readBoolean();

        if (isBarnAnimal) {
            BarnAnimalTypes type = BarnAnimalTypes.values()[dis.readInt()];
            BarnAnimal barnAnimal = new BarnAnimal(type, name);
            barnAnimal.setPosX(posX);
            barnAnimal.setPosY(posY);
            barnAnimal.setSpeed(speed);
            barnAnimal.setMoving(isMoving);
            barnAnimal.setTargetX(targetX);
            barnAnimal.setTargetY(targetY);
            barnAnimal.setStateTimer(stateTimer);
            barnAnimal.setFacing(facing);
            barnAnimal.increaseHappiness(dis.readInt() - 50); // initial happiness is 50
            // barnAnimal.setDaysSinceLastProduction(dis.readInt()); // No setter
            barnAnimal.setHasBeenFed(dis.readBoolean());
            barnAnimal.setHasBeenPetToday(dis.readBoolean());
            barnAnimal.setOutside(dis.readBoolean());
            return barnAnimal;
        } else {
            CoopAnimalTypes type = CoopAnimalTypes.values()[dis.readInt()];
            CoopAnimal coopAnimal = new CoopAnimal(type, name);
            coopAnimal.setPosX(posX);
            coopAnimal.setPosY(posY);
            coopAnimal.setSpeed(speed);
            coopAnimal.setMoving(isMoving);
            coopAnimal.setTargetX(targetX);
            coopAnimal.setTargetY(targetY);
            coopAnimal.setStateTimer(stateTimer);
            coopAnimal.setFacing(facing);
            coopAnimal.increaseHappiness(dis.readInt() - 50);
            // coopAnimal.setDaysSinceLastProduction(dis.readInt());
            coopAnimal.setPetToday(dis.readBoolean());
            coopAnimal.setHasBeenFed(dis.readBoolean());
            coopAnimal.setOutside(dis.readBoolean());
            return coopAnimal;
        }
    }
}
