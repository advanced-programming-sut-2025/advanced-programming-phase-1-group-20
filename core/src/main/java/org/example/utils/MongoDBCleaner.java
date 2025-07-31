package org.example.utils;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBCleaner {

    public static void main(String[] args) {
        System.out.println("=== MongoDB Database Cleaner ===");
        try {
            // Connect to MongoDB
            MongoDatabase database = MongoDBConnection.getDatabase();

            // Clear users collection
            clearUsersCollection(database);

            // Clear games collection
            clearGamesCollection(database);

            System.out.println("Database cleaned successfully!");

        } catch (Exception e) {
            System.err.println("Error cleaning database: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close connection
            MongoDBConnection.closeConnection();
        }
    }

    private static void clearUsersCollection(MongoDatabase database) {
        try {
            MongoCollection<Document> usersCollection = database.getCollection("users");
            long countBefore = usersCollection.countDocuments();

            if (countBefore > 0) {
                usersCollection.deleteMany(new Document());
                System.out.println("Deleted " + countBefore + " users from database");
            } else {
                System.out.println("No users found in database");
            }
        } catch (Exception e) {
            System.err.println("Error clearing users collection: " + e.getMessage());
        }
    }

    private static void clearGamesCollection(MongoDatabase database) {
        try {
            MongoCollection<Document> gamesCollection = database.getCollection("games");
            long countBefore = gamesCollection.countDocuments();

            if (countBefore > 0) {
                gamesCollection.deleteMany(new Document());
                System.out.println("Deleted " + countBefore + " games from database");
            } else {
                System.out.println("ℹNo games found in database");
            }
        } catch (Exception e) {
            System.err.println("Error clearing games collection: " + e.getMessage());
        }
    }

    public static void clearAllData() {
        try {
            MongoDatabase database = MongoDBConnection.getDatabase();
            clearUsersCollection(database);
            clearGamesCollection(database);
            System.out.println("All data cleared from MongoDB");
        } catch (Exception e) {
            System.err.println("Error clearing data: " + e.getMessage());
        }
    }
}
