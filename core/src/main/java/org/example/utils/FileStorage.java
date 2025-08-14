package org.example.utils;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bson.types.Binary;
import org.example.common.models.Items.*;
import org.example.common.models.entities.Game;
import org.example.common.models.enums.Types.*;
import org.example.common.models.entities.User;
import org.example.common.models.enums.PlayerEnums.Gender;
// این import را برای استفاده از CustomSaveManager اضافه کنید
import org.example.utils.CustomSaveManager; // فرض می‌شود این مسیر صحیح است

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileStorage {
    private static MongoCollection<Document> usersCollection;
    private static MongoCollection<Document> gamesCollection;

    private static void initializeCollection() {
        if (usersCollection == null) {
            usersCollection = MongoDBConnection.getUsersCollection();
            gamesCollection = MongoDBConnection.getGamesCollection();
        }
    }


    public static boolean saveGame(Game game) {
        initializeCollection();
        if (game == null || game.getSaveName() == null || game.getSaveName().isEmpty()) {
            System.err.println("Game or save name is null or empty. Cannot save to MongoDB.");
            return false;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // داده‌های بازی را به صورت باینری در حافظه می‌نویسیم
            CustomSaveManager.saveGame(game, baos);
            byte[] gameData = baos.toByteArray();

            // داکیومنت را برای ذخیره در مونگو آماده می‌کنیم
            Document doc = new Document("savename", game.getSaveName())
                .append("gameData", new Binary(gameData));

            // داکیومنت را در دیتابیس ذخیره/جایگزین می‌کنیم
            gamesCollection.replaceOne(
                Filters.eq("savename", game.getSaveName()),
                doc,
                new ReplaceOptions().upsert(true)
            );
            System.out.println("Game '" + game.getSaveName() + "' saved to MongoDB successfully.");
            return true;
        } catch (IOException e) {
            System.err.println("Failed to serialize and save game '" + game.getSaveName() + "' to MongoDB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * داده‌های باینری بازی را از MongoDB بارگذاری می‌کند.
     */
    public static Game loadGame(String savename) {
        initializeCollection();
        try {
            Document doc = gamesCollection.find(Filters.eq("savename", savename)).first();
            if (doc != null) {
                Binary gameData = doc.get("gameData", Binary.class);
                byte[] bytes = gameData.getData();

                try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                    // داده‌های باینری را از حافظه می‌خوانیم و به آبجکت Game تبدیل می‌کنیم
                    Game game = CustomSaveManager.loadGame(bais);
                    System.out.println("Game '" + savename + "' loaded from MongoDB successfully.");
                    return game;
                }
            } else {
                System.out.println("No save data found in MongoDB for '" + savename + "'.");
                return null;
            }
        } catch (Exception e) {
            System.err.println("Failed to load and deserialize game '" + savename + "' from MongoDB: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * تمام بازی‌های ذخیره شده در MongoDB را بارگذاری می‌کند.
     */
    public static List<Game> loadAllGames() {
        initializeCollection();
        List<Game> games = new ArrayList<>();
        List<String> saveNames = listSavedGames(); // گرفتن لیست نام‌ها
        for (String saveName : saveNames) {
            Game game = loadGame(saveName); // بارگذاری هر بازی با نام آن
            if (game != null) {
                games.add(game);
            }
        }
        System.out.println("Loaded " + games.size() + " games from MongoDB.");
        return games;
    }

    /**
     * لیست نام تمام بازی‌های ذخیره شده در MongoDB را برمی‌گرداند.
     */
    public static List<String> listSavedGames() {
        initializeCollection();
        List<String> saveNames = new ArrayList<>();
        gamesCollection.find().projection(new Document("savename", 1).append("_id", 0))
            .forEach(doc -> saveNames.add(doc.getString("savename")));
        return saveNames;
    }

    /**
     * یک بازی ذخیره شده را از MongoDB حذف می‌کند.
     */
    public static boolean deleteGame(String savename) {
        initializeCollection();
        try {
            gamesCollection.deleteOne(Filters.eq("savename", savename));
            System.out.println("Game '" + savename + "' deleted from MongoDB successfully.");
            return true;
        } catch (Exception e) {
            System.err.println("Failed to delete game '" + savename + "' from MongoDB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public static boolean saveUsers(Map<String, User> users) {
        initializeCollection();
        try {
            for (User user : users.values()) {
                Document doc = new Document()
                    .append("username", user.getUsername())
                    .append("passwordHash", user.getPassword())
                    .append("email", user.getEmail())
                    .append("nickname", user.getNickname())
                    .append("gender", user.getGender().toString())
                    .append("stayLoggedIn", user.isStayLoggedIn())
                    .append("securityQuestionIndex", user.getSecurityQuestionIndex())
                    .append("securityAnswer", user.getSecurityAnswer())
                    .append("mostEarnedMoney", user.getMostEarnedMoney())
                    .append("gamesPlayed", user.getGamesPlayed())
                    .append("inventory", user.getInventory().stream()
                        .map(item -> new Document()
                            .append("name", item.getName())
                            .append("price", item.getPrice())
                            .append("imageFilePath", item.getImageFilepath())
                            .append("description", item.getDescription()))
                        .collect(Collectors.toList())
                    )
                    .append("games" , user.getGames());
                usersCollection.replaceOne(
                    Filters.eq("username", user.getUsername()),
                    doc,
                    new ReplaceOptions().upsert(true)
                );
            }
            System.out.println("Users saved to MongoDB successfully.");
            return true;
        } catch (Exception e) {
            System.err.println("Failed to save users to MongoDB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static Map<String, User> loadUsers() {
        initializeCollection();
        Map<String, User> users = new HashMap<>();
        try {
            for (Document doc : usersCollection.find()) {
                User user = new User();
                user.setUsername(doc.getString("username"));
                user.setPasswordHash(doc.getString("passwordHash"));
                user.setEmail(doc.getString("email"));
                user.setNickname(doc.getString("nickname"));
                user.setGender(Gender.valueOf(doc.getString("gender")));
                user.setStayLoggedIn(doc.getBoolean("stayLoggedIn", false));
                user.setSecurityQuestionIndex(doc.getInteger("securityQuestionIndex", 0));
                user.setSecurityAnswer(doc.getString("securityAnswer"));
                user.setMostEarnedMoney(doc.getInteger("mostEarnedMoney", 0));
                user.setGamesPlayed(doc.getInteger("gamesPlayed", 0));

                List<Item> inventoryItems = new ArrayList<>();
                List<Document> loadedInventoryList = doc.getList("inventory", Document.class);
                if (loadedInventoryList != null) {
                    for (Document itemDoc : loadedInventoryList) {
                        String itemName = itemDoc.getString("name");
                        Integer itemPrice = itemDoc.getInteger("price");
                        String imageFilePath = itemDoc.getString("imageFilePath");
                        String itemDescription = itemDoc.getString("description");

                        Item loadedItem = new Item(itemName, itemPrice != null ? itemPrice : 0, imageFilePath , itemDescription);
                        inventoryItems.add(loadedItem);
                    }
                }
                user.setInventory(inventoryItems);

                user.setGames(doc.getList("games", String.class , new ArrayList<>()));

                users.put(user.getUsername(), user);
            }
            System.out.println("Users loaded from MongoDB: " + users.size());
            return users;
        } catch (Exception e) {
            System.err.println("Failed to load users from MongoDB: " + e.getMessage());
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    // متد loadItems بدون تغییر باقی می‌ماند
    public static List<Item> loadItems() {
        List<Item> items = new ArrayList<>();
        for (PlantType type : PlantType.values()) {
            items.add(new Plant(type));
        }
        for (CropType type : CropType.values()) {
            items.add(new Crop(type));
        }
        for (MineralType type : MineralType.values()) {
            items.add(new Mineral(type));
        }
        for (SeedType type : SeedType.values()) {
            items.add(new Seed(type));
        }
        for (TreeType type : TreeType.values()) {
            items.add(new Tree(type));
        }
        for (CookingType type : CookingType.values()) {
            items.add(new CookingItem(type));
        }
        for (CraftingType type : CraftingType.values()) {
            items.add(new CraftingItem(type));
        }
        for (SpecialItemType type : SpecialItemType.values()) {
            items.add(new SpecialItem(type));
        }
        return items;
    }
}
