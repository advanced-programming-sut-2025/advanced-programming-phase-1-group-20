package org.example.views;

import org.example.models.App;
import org.example.models.entities.Game;
import org.example.models.common.Result;
import org.example.models.utils.AutoLoginUtil;

import java.util.Scanner;

public class AppView {
    public AppMenu currentMenu;
    public boolean exit = false;
    public App appHandler;
    private Scanner scanner;
    private Game currentGame;

    public AppView() {
        // Initialize the App to load saved user data
        App.initialize();

        scanner = new Scanner(System.in);

        // Check for auto-login
        boolean autoLoginSuccessful = AutoLoginUtil.checkAndPerformAutoLogin(this);

        if (!autoLoginSuccessful) {
            this.currentMenu = new LoginRegisterMenu(this);
        }
    }

    public void appStart() {
        while (!exit) {
            // Get user input
            String input = scanner.nextLine();

            update(input);

            if (input.equalsIgnoreCase("exit")) {
                exit();
            }
        }
    }

    public void update(String input) {
        this.currentMenu.updateMenu(input);

    }

    public void navigateMenu(AppMenu menu) {
        this.currentMenu = menu;
    }

    public void handleResult(Result result, Object command) {
        currentMenu.handleResult(result, command);
    }

    public void setCurrentGame(Game game) {
        this.currentGame = game;
    }

    public Game getCurrentGame() {
        return this.currentGame;
    }

    private void saveAllData() {
        // Save user data
        App.saveData();

        // if (currentGame != null) {
        //     currentGame.saveGame();
        // }
    }

    public void exit() {
        saveAllData();
        this.exit = true;
    }

    public void autoSave() {
        // if (currentGame != null) {
        //     currentGame.saveGame();
        // }
    }
}