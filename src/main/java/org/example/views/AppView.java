package org.example.views;

import org.example.models.App;
import org.example.models.common.Result;
import org.example.models.entities.Game;
import org.example.models.utils.AutoLoginUtil;

import java.util.Scanner;

public class AppView {
    public AppMenu currentMenu;
    public boolean exit = false;
    private Scanner scanner;
    private Game currentGame;

    public AppView() {
        App.initialize();

        scanner = new Scanner(System.in);

        boolean autoLoginSuccessful = AutoLoginUtil.checkAndPerformAutoLogin(this);

        if (!autoLoginSuccessful) {
            this.currentMenu = new LoginRegisterMenu(this);
        }
    }

    public void appStart() {
        while (!exit) {
            System.out.print(getCurrentMenuName() + "> ");
            String input = scanner.nextLine();
            
            update(input);

            if (input.equalsIgnoreCase("exit")) {
                exit();
            }
        }
    }

    public void update(String input) {
        if (currentMenu != null) {
            this.currentMenu.updateMenu(input);
        } else {
            System.out.println("Error: No active menu controller");
            this.currentMenu = new LoginRegisterMenu(this);
        }
    }

    public String getCurrentMenuName() {
        return switch (currentMenu) {
            case LoginRegisterMenu loginRegisterMenu -> "login";
            case MainMenu mainMenu -> "main";
            case ProfileMenu profileMenu -> "profile";
            case GameMenu gameMenu -> "game";
            case null, default -> "unknown";
        };
    }

    public void navigateMenu(AppMenu menu) {
        this.currentMenu = menu;
    }

    public void handleResult(Result result, Object command) {
        currentMenu.handleResult(result, command);
    }

    public Game getCurrentGame() {
        return this.currentGame;
    }

    public void setCurrentGame(Game game) {
        this.currentGame = game;
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
