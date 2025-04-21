package org.example.views;

import org.example.models.App;

import java.util.Scanner;

public class AppView {
    public AppMenu currentMenu;
    public boolean exit = false;
    public App appHandler;
    private Scanner scanner;

    public AppView() {
        // Initialize the App to load saved data
        App.initialize();

        scanner = new Scanner(System.in);

        // Check for auto-login
        boolean autoLoginSuccessful = org.example.models.utils.AutoLoginUtil.checkAndPerformAutoLogin(this);

        // If auto-login fails, show the login/register menu
        if (!autoLoginSuccessful) {
            this.currentMenu = new LoginRegisterMenu(this);
        }
    }

    public void appStart() {
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            update(input);
            if (exit) {
                // Save data before exiting
                App.saveData();
                break;
            }
        }
    }

    public void update(String input) {
        this.currentMenu.updateMenu(input);
    }

    public void navigateMenu(AppMenu menu) {
        this.currentMenu = menu;
    }

    public void exit() {
        // Save data before exiting
        App.saveData();
        this.exit = true;
    }
}