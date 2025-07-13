package org.example.models.utils;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.example.models.Items.*;
import org.example.models.entities.User;
import org.example.models.enums.PlayerEnums.Gender;
import org.example.models.enums.Types.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class FileStorage {
    private static MongoCollection<Document> usersCollection;

    private static void initializeCollection() {
        if (usersCollection == null) {
            usersCollection = MongoDBConnection.getDatabase().getCollection("users");
        }
    }

    public static boolean saveUsers(Map<String, User> users) {
        initializeCollection();
        try {
            for (User user : users.values()) {
                Document userDoc = new Document("username", user.getUsername())
                    .append("passwordHash", user.getPassword())
                    .append("email", user.getEmail())
                    .append("nickname", user.getNickname())
                    .append("gender", user.getGender().name())
                    .append("stayLoggedIn", user.isStayLoggedIn())
                    .append("securityQuestionIndex", user.getSecurityQuestionIndex())
                    .append("securityAnswer", user.getSecurityAnswer())
                    .append("mostEarnedMoney", user.getMostEarnedMoney())
                    .append("gamesPlayed", user.getGamesPlayed());

                // **** تغییر در اینجا: Inventory از List<Item> به List<Document> نگاشت می‌شود ****
                List<Document> inventoryList = new ArrayList<>();
                if (user.getInventory() != null) {
                    for (Item item : user.getInventory()) { // حالا ایتریت روی List<Item>
                        // این بخش باید با فیلدهای واقعی کلاس Item شما مطابقت داشته باشد.
                        // فرض می‌کنیم Item دارای getName(), getPrice(), getDescription() است.
                        inventoryList.add(new Document("name", item.getName())
                            .append("price", item.getPrice())
                            .append("description", item.getDescription())); // اضافه کردن سایر فیلدهای مورد نیاز
                    }
                }
                userDoc.append("inventory", inventoryList);


                usersCollection.replaceOne(
                    Filters.eq("username", user.getUsername()),
                    userDoc,
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

                // **** تغییر در اینجا: بارگذاری Inventory به عنوان List<Item> ****
                List<Item> inventoryItems = new ArrayList<>();
                List<Document> loadedInventoryList = doc.getList("inventory", Document.class);
                if (loadedInventoryList != null) {
                    for (Document itemDoc : loadedInventoryList) {
                        String itemName = itemDoc.getString("name");
                        Integer itemPrice = itemDoc.getInteger("price");
                        String imageFilePath = itemDoc.getString("imageFilePath");
                        String itemDescription = itemDoc.getString("description");

                        // اینجا فرض شده که Item دارای سازنده‌ای مانند new Item(name, price, description) است.
                        // شما باید این قسمت را با روش ساخت اشیاء Item در پروژه‌تان تطبیق دهید.
                        // ممکن است نیاز به استفاده از ItemBuilder.buildItem(name) یا سازنده دیگری داشته باشید.
                        Item loadedItem = new Item(itemName, itemPrice != null ? itemPrice : 0, imageFilePath , itemDescription);
                        inventoryItems.add(loadedItem);
                    }
                }
                user.setInventory(inventoryItems); // مطمئن شوید متد setInventory در User، یک List<Item> را قبول می‌کند

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
