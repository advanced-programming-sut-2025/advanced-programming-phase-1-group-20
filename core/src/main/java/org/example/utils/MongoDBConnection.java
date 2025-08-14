package org.example.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class MongoDBConnection {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "stardew_valley_db";
    private static final String USERS_COLLECTION = "users";
    private static final String GAMES_COLLECTION = "games";

    private MongoDBConnection() {
    }

    public static synchronized MongoDatabase getDatabase() {
        if (database == null) {
            if (mongoClient == null) {
                try {
                    mongoClient = MongoClients.create(CONNECTION_STRING);
                    System.out.println("Connected to MongoDB successfully!");
                } catch (Exception e) {
                    System.err.println("Failed to connect to MongoDB: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            if (mongoClient != null) {
                database = mongoClient.getDatabase(DATABASE_NAME);
            }
        }
        return database;
    }

    public static MongoCollection<Document> getUsersCollection() {
        return getDatabase().getCollection(USERS_COLLECTION);
    }

    public static MongoCollection<Document> getGamesCollection() {
        return getDatabase().getCollection(GAMES_COLLECTION);
    }

    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
            System.out.println("MongoDB connection closed.");
        }
    }
}
