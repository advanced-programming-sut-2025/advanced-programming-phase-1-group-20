package org.example.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "stardew_valley_db";
    private static boolean connectionFailed = false;

    private MongoDBConnection() {
    }

    public static synchronized MongoDatabase getDatabase() {
        if (database == null && !connectionFailed) {
            if (mongoClient == null) {
                try {
                    System.out.println("Attempting to connect to MongoDB at: " + CONNECTION_STRING);
                    mongoClient = MongoClients.create(CONNECTION_STRING);
                    
                    // Test the connection
                    mongoClient.getDatabase("admin").runCommand(new org.bson.Document("ping", 1));
                    System.out.println("Connected to MongoDB successfully!");
                    
                } catch (Exception e) {
                    System.err.println("Failed to connect to MongoDB: " + e.getMessage());
                    System.err.println("Please ensure MongoDB is running on localhost:27017");
                    System.err.println("To install MongoDB:");
                    System.err.println("  macOS: brew install mongodb-community && brew services start mongodb-community");
                    System.err.println("  Windows: Download from mongodb.com and start the service");
                    System.err.println("  Linux: sudo apt install mongodb && sudo systemctl start mongodb");
                    System.err.println("Or use MongoDB Atlas (cloud) by updating MONGODB_URI in .env file");
                    connectionFailed = true;
                    e.printStackTrace();
                    return null;
                }
            }
            database = mongoClient.getDatabase(DATABASE_NAME);
        }
        return database;
    }

    public static boolean isConnected() {
        return mongoClient != null && !connectionFailed;
    }

    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
            connectionFailed = false;
            System.out.println("MongoDB connection closed.");
        }
    }
}
