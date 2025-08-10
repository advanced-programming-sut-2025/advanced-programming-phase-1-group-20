package org.example.utils;

import org.example.common.models.Items.*;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.MapDetails.GameMap;
import org.example.common.models.MapDetails.Village;
import org.example.common.models.Player.Backpack;
import org.example.common.models.Player.Player;
import org.example.common.models.Player.Skill;
import org.example.common.models.common.Date;
import org.example.common.models.common.Location;
import org.example.common.models.entities.Game;
import org.example.common.models.entities.User;
import org.example.common.models.enums.PlayerEnums.Gender;
import org.example.common.models.enums.Seasons;
import org.example.common.models.enums.Types.*;
import org.example.common.models.enums.Weather;

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
            dos.writeUTF(entry.getKey().getName());
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
            dos.writeUTF(player.getCurrentItem().getName());
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
            String itemName = dis.readUTF();
            int quantity = dis.readInt();
            player.getBackpack().add(ItemBuilder.build(itemName), quantity);
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
            String itemName = dis.readUTF();
            Item item = ItemBuilder.build(itemName);
            if(item != null) {
                player.equipItem(itemName);
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

        return farm;
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
            dos.writeUTF(location.getItem().getName());
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
            String itemName = dis.readUTF();
            location.setItem(ItemBuilder.build(itemName));
        }

        return location;
    }
}
