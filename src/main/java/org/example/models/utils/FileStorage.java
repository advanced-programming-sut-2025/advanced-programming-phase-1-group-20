package org.example.models.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.models.Items.*;
import org.example.models.Items.Plant;
import org.example.models.entities.User;

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
    public static List<Plant> loadPlants() {
        List<Plant> plants = new ArrayList<>();
        try(Reader reader = new FileReader("plants.json")){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            plants = gson.fromJson(reader, new TypeToken<List<Plant>>() {
            }.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return plants;
    }

    public static List<Crop> loadCrops() {
        List<Crop> crops = new ArrayList<>();
        try(Reader reader = new FileReader("crops.json")){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            crops = gson.fromJson(reader, new TypeToken<List<Crop>>() {
            }.getType());
        }catch (IOException e) {
            e.printStackTrace();
        }
        return crops;
    }

    public static List<Mineral> loadMinerals() {
        List<Mineral> minerals = new ArrayList<>();
        try(Reader reader = new FileReader("minerals.json")){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            minerals = gson.fromJson(reader,new TypeToken<List<Mineral>>() {
            }.getType());
        }catch (IOException e) {
            e.printStackTrace();
        }
        return minerals;
    }

    public static List<Seed> loadSeeds() {
        List<Seed> seeds = new ArrayList<>();
        try(Reader reader = new FileReader("seeds.json")){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            seeds = gson.fromJson(reader , new TypeToken<List<Seed>>() {
            }.getType());
        }catch (IOException e) {
            e.printStackTrace();
        }
        return seeds;
    }

    public static List<Tree> loadTrees() {
        List<Tree> trees = new ArrayList<>();
        try(Reader reader = new FileReader("trees.json")){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            trees = gson.fromJson(reader , new TypeToken<List<Tree>>() {
            }.getType());
        }catch (IOException e) {
            e.printStackTrace();
        }
        return trees;
    }

    public static List<CookingItem> loadCookingItems() {
        List<CookingItem> cookingItems = new ArrayList<>();
        try(Reader reader = new FileReader("cookings.json")){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            cookingItems = gson.fromJson(reader , new TypeToken<List<CookingItem>>() {
            }.getType());
        }catch (IOException e) {
            e.printStackTrace();
        }
        return cookingItems;
    }

    public static List<CraftingItem> loadCraftingItems() {
        List<CraftingItem> craftingItems = new ArrayList<>();
        try(Reader reader = new FileReader("craftings.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            craftingItems = gson.fromJson(reader , new TypeToken<List<CraftingItem>>() {
            }.getType());
        }catch (IOException e) {
            e.printStackTrace();
        }
        return craftingItems;
    }

//    public static List<Item> loadItems(){
//        List<Item> items = new ArrayList<>();
//
//        //adding plants
//        items.addAll(loadCrops());
//
//        //adding crops
//        items.addAll(loadCrops());
//
//        //adding minerals
//        items.addAll(loadMinerals());
//
//        //adding seeds
//        items.addAll(loadSeeds());
//
//        //adding trees
//        items.addAll(loadTrees());
//
//        //adding cookings
//        items.addAll(loadCookingItems());
//
//        //adding craftings
//        items.addAll(loadCraftingItems());
//
//
//        return items;
//    }
}