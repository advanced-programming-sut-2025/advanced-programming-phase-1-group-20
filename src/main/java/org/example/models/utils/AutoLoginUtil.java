package org.example.models.utils;

import org.example.models.App;
import org.example.models.entities.User;
import org.example.views.AppView;
import org.example.views.ProfileMenu;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AutoLoginUtil {
    private static final String AUTO_LOGIN_FILE = "autologin.txt";


    public static void saveAutoLogin(String username) {
        try (FileWriter writer = new FileWriter(AUTO_LOGIN_FILE)) {
            writer.write(username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearAutoLogin() {
        File file = new File(AUTO_LOGIN_FILE);
        if (file.exists()) {
            file.delete();
        }
    }


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
                appView.navigateMenu(new ProfileMenu(appView, user));
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}