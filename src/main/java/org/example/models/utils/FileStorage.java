package org.example.models.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.models.App;
import org.example.models.Items.*;
import org.example.models.entities.User;
import org.example.models.enums.Types.*;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileStorage {
    private static final String USER_DATA_FILE = "src/main/DataBase/users.json";
    private static final String GAME_DATA_FILE = "src/main/DataBase/games.json";

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static boolean saveUsers(Map<String, User> users) {
        try (FileWriter writer = new FileWriter(USER_DATA_FILE)) {
            gson.toJson(users, writer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Map<String, User> loadUsers() {
        File file = new File(USER_DATA_FILE);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (FileReader reader = new FileReader(file)) {
            Type userMapType = new TypeToken<Map<String, User>>() {
            }.getType();
            Map<String, User> users = gson.fromJson(reader, userMapType);
            return users != null ? users : new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static void saveGames() throws IOException {
        File file = new File(GAME_DATA_FILE);
        try (FileWriter writer = new FileWriter(GAME_DATA_FILE)) {
            gson.toJson(App.getAllGames(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static


    public static List<Item> loadItems() {
        List<Item> items = new ArrayList<>();

        //adding plants
        for (PlantType type : PlantType.values()) {
            Item item = new Plant(type);
            items.add(item);
        }

        //adding crops
        for (CropType type : CropType.values()) {
            Item item = new Crop(type);
            items.add(item);
        }

        //adding minerals
        for (MineralType type : MineralType.values()) {
            Item item = new Mineral(type);
            items.add(item);
        }

        //adding seeds
        for (SeedType type : SeedType.values()) {
            Item item = new Seed(type);
            items.add(item);
        }

        //adding trees
        for (TreeType type : TreeType.values()) {
            Item item = new Tree(type);
            items.add(item);
        }

        //adding cookings
        for (CookingType type : CookingType.values()) {
            Item item = new CookingItem(type);
            items.add(item);
        }

        //adding crafting
        for (CraftingType type : CraftingType.values()) {
            Item item = new CraftingItem(type);
            items.add(item);
        }


        return items;
    }
}