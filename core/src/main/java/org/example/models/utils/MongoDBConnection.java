package org.example.models.utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    // CONNECTION_STRING: آدرس MongoDB Compass شما. برای اتصال محلی، معمولاً "mongodb://localhost:27017" است.
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    // DATABASE_NAME: نام دیتابیسی که می‌خواهید در MongoDB Compass ایجاد کنید.
    private static final String DATABASE_NAME = "stardew_valley_db"; // نام دیتابیس شما

    private MongoDBConnection() {
        // Private constructor to prevent instantiation
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
                    // می‌توانید در اینجا برنامه را متوقف کنید یا روش‌های جایگزین برای مدیریت خطا پیاده‌سازی کنید.
                }
            }
            database = mongoClient.getDatabase(DATABASE_NAME);
        }
        return database;
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
