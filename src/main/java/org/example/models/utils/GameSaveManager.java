package org.example.models.utils;

import java.io.File;

public class GameSaveManager {
    private static final String GAME_SAVE_FILE = "game_save.dat";


    public static boolean hasSavedGame() {
        File file = new File(GAME_SAVE_FILE);
        return file.exists();
    }

    public static boolean deleteSavedGame() {
        File file = new File(GAME_SAVE_FILE);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }
}