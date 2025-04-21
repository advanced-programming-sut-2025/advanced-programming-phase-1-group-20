package org.example.models.utils;

import org.example.models.App;
import org.example.models.User;
import org.example.views.AppView;
import org.example.views.MainMenu;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility class to handle auto-login functionality
 */
public class AutoLoginUtil {
    private static final String AUTO_LOGIN_FILE = "autologin.txt";

    /**
     * Saves the username of the user who selected "stay logged in"
     *
     * @param username The username to save
     */
    public static void saveAutoLogin(String username) {
        try (FileWriter writer = new FileWriter(AUTO_LOGIN_FILE)) {
            writer.write(username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the auto-login file
     */
    public static void clearAutoLogin() {
        File file = new File(AUTO_LOGIN_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Checks if there's a user saved for auto-login and logs them in
     *
     * @param appView The AppView instance to navigate to the main menu
     * @return true if auto-login was successful, false otherwise
     */
    public static boolean checkAndPerformAutoLogin(AppView appView) {
        File file = new File(AUTO_LOGIN_FILE);
        if (!file.exists()) {
            return false;
        }

        try (FileReader reader = new FileReader(file)) {
            char[] buf = new char[1024];
            int len = reader.read(buf);
            if (len <= 0) {
                return false;
            }

            String username = new String(buf, 0, len).trim();
            User user = App.getUser(username);

            if (user != null && user.isStayLoggedIn()) {
                App.setLoggedInUser(user);
                appView.navigateMenu(new MainMenu(appView, user));
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}