package org.example.views;

import org.example.controllers.MainMenuController;
import org.example.models.common.Result;
import org.example.models.entities.User;

public class MainMenu implements AppMenu {
    MainMenuController controller;
    private AppView appView;
    private User user;

    public MainMenu(AppView appView, User user) {
        this.appView = appView;
        this.user = user;
        controller = new MainMenuController(appView, user);
    }

    @Override
    public void updateMenu(String input) {
        controller.update(input);
    }

    @Override
    public void handleResult(Result result, Object command) {
        if (result == null) return;

        if (result.success()) {
            System.out.println(result.message());
//            if (command instanceof MainMenuCommands) {
//                MainMenuCommands mainCommand = (MainMenuCommands) command;
//                switch (mainCommand) {
//
//                }
//            }
        } else {
            System.out.println("Error: " + result.message());
        }
    }
}
