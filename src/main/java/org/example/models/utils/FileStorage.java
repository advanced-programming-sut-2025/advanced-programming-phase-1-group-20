package org.example.models.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.models.Items.*;
import org.example.models.Items.Plant;
import org.example.models.entities.User;
import org.example.models.enums.Types.*;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileStorage {
    private static final String USER_DATA_FILE = "users.json";
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


    //loading items in game.

    public static List<Item> loadItems(){
        List<Item> items = new ArrayList<>();

        for(CookingType type : CookingType.values()) {
            items.add(new CookingItem(type));
        }

        for(CraftingType type : CraftingType.values()) {
            items.add(new CraftingItem(type));
        }

        for(CropType type : CropType.values()) {
            items.add(new Crop(type));
        }

        for(MineralType type : MineralType.values()) {
            items.add(new Mineral(type));
        }

        for(PlantType type : PlantType.values()) {
            items.add(new Plant(type));
        }

        for(SeedType type : SeedType.values()) {
            items.add(new Seed(type));
        }

        for(TreeType type : TreeType.values()) {
            items.add(new Tree(type));
        }


        return items;
    }
}