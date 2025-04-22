package org.example.views;

import org.example.models.App;
import org.example.models.entities.Game;
import org.example.models.utils.AutoLoginUtil;
//import org.example.models.utils.GameSaveManager;

import java.util.Scanner;

public class AppView {
    public AppMenu currentMenu;
    public boolean exit = false;
    public App appHandler;
    private Scanner scanner;
    private Game currentGame;

    public AppView() {
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


    public void setCurrentGame(Game game) {
        this.currentGame = game;
    }

    public Game getCurrentGame() {
        return this.currentGame;
    }


    private void saveAllData() {
        // Save user data
        App.saveData();

        // Save the current game if one exists
//        if (currentGame != null) {
//            currentGame.saveGame();
//        }
    }

    public void exit() {
        // Save all data before exiting
        saveAllData();
        this.exit = true;
    }


    public void autoSave() {
//        if (currentGame != null) {
//            currentGame.saveGame();
//        }
    }
}