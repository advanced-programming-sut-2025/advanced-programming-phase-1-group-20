package org.example.models.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.example.models.App;
import org.example.models.entities.Game;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GameSaveLoadManager {
    private static MongoCollection<Document> gamesCollection;
    private static final String GAME_DATA_FIELD = "gameDataJson";
    private static final String SAVE_NAME_FIELD = "saveName";
    private static final String TIMESTAMP_FIELD = "timestamp";

    private static final Gson gson = new GsonBuilder().create();

    private static void initializeCollection() {
        if (gamesCollection == null) {
            gamesCollection = MongoDBConnection.getDatabase().getCollection("games");
        }
    }

    public static void initialize() {
        initializeCollection();
        System.out.println("GameSaveLoadManager initialized for MongoDB.");
    }

    public static boolean saveCurrentGame() {
        if (App.getGame() != null) {
            Game game = App.getGame();
            game.setSaved(true);
            return saveGameInternal(game, "current_game", true);
        }
        return false;
    }

    public static boolean autosave() {
        if (App.getGame() != null) {
            return saveGameInternal(App.getGame(), "autosave", true);
        }
        return false;
    }

    public static boolean saveGameWithName(Game game, String customSaveName) {
        String cleanSaveName = customSaveName.replaceAll("[^a-zA-Z0-9-_]", "_");
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); // **** تغییر در اینجا: استفاده از Date ****
        String uniqueSaveName = cleanSaveName + "_" + timestamp;

        boolean saved = saveGameInternal(game, uniqueSaveName, false);
        if (saved) {
            if (App.getAllGames() != null && !App.getAllGames().contains(game)) {
                App.getAllGames().add(game);
            }
        }
        return saved;
    }

    private static boolean saveGameInternal(Game game, String identifier, boolean replaceExisting) {
        initializeCollection();
        try {
            String gameJson = gson.toJson(game);

            Document gameDoc = new Document(SAVE_NAME_FIELD, identifier)
                .append(GAME_DATA_FIELD, gameJson)
                .append(TIMESTAMP_FIELD, new Date()); // **** تغییر در اینجا: استفاده از Date ****

            if (replaceExisting) {
                gamesCollection.replaceOne(Filters.eq(SAVE_NAME_FIELD, identifier), gameDoc, new ReplaceOptions().upsert(true));
            } else {
                gamesCollection.insertOne(gameDoc);
            }

            System.out.println("Game saved successfully to MongoDB with identifier: " + identifier);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to save game to MongoDB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static Game loadCurrentGame() {
        return loadGame("current_game");
    }

    public static Game loadAutosave() {
        return loadGame("autosave");
    }

    public static Game loadGame(String identifier) {
        initializeCollection();
        try {
            Document gameDoc = gamesCollection.find(Filters.eq(SAVE_NAME_FIELD, identifier)).first();
            if (gameDoc != null) {
                String gameJson = gameDoc.getString(GAME_DATA_FIELD);
                Game game = gson.fromJson(gameJson, Game.class);
                App.setGame(game);
                System.out.println("Game loaded successfully from MongoDB: " + identifier);
                return game;
            }
        } catch (Exception e) {
            System.err.println("Failed to load game from MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> listSavedGames() {
        initializeCollection();
        List<String> saveNames = new ArrayList<>();
        try {
            for (Document doc : gamesCollection.find(
                Filters.and(
                    Filters.ne(SAVE_NAME_FIELD, "current_game"),
                    Filters.ne(SAVE_NAME_FIELD, "autosave")
                )
            ).sort(new Document(TIMESTAMP_FIELD, -1))) {
                saveNames.add(doc.getString(SAVE_NAME_FIELD));
            }
            System.out.println("Listed saved games from MongoDB: " + saveNames.size());
        } catch (Exception e) {
            System.err.println("Failed to list saved games from MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
        return saveNames;
    }

    public static void loadAllGames() {
        initializeCollection();
        if (App.getAllGames() != null) {
            App.getAllGames().clear();
        } else {
            App.setAllGames(new ArrayList<>());
        }
        try {
            for (Document doc : gamesCollection.find()) {
                if (!doc.getString(SAVE_NAME_FIELD).equals("current_game") &&
                    !doc.getString(SAVE_NAME_FIELD).equals("autosave")) {
                    Game game = loadGame(doc.getString(SAVE_NAME_FIELD));
                    if (game != null && !App.getAllGames().contains(game)) {
                        App.getAllGames().add(game);
                    }
                }
            }
            System.out.println("Loaded all unique games into App from MongoDB.");
        } catch (Exception e) {
            System.err.println("Failed to load all games into App from MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean deleteSavedGame(String identifier) {
        initializeCollection();
        try {
            long deletedCount = gamesCollection.deleteOne(Filters.eq(SAVE_NAME_FIELD, identifier)).getDeletedCount();
            if (deletedCount > 0) {
                System.out.println("Saved game '" + identifier + "' deleted successfully from MongoDB.");
                if (App.getAllGames() != null) {
                    App.getAllGames().removeIf(game -> game.getSaveName() != null && game.getSaveName().equals(identifier));
                }
                return true;
            } else {
                System.out.println("No game found to delete with identifier: " + identifier);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Failed to delete saved game from MongoDB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isGameSaved(Game game) {
        return game != null && game.isSaved();
    }
}
