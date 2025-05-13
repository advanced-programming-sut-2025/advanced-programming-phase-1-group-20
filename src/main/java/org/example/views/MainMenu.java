package org.example.views;

import org.example.controllers.MainMenuController;
import org.example.models.App;
import org.example.models.common.Result;
import org.example.models.entities.User;
import org.example.models.enums.commands.MainMenuCommands;

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

        if (command == MainMenuCommands.NewGame) {
            if (result.success()) {
                System.out.println("~~map selection phase~~");
            }

            appView.navigateMenu(new GameMenu(appView, App.getLoggedInUser(), App.getGame().getCurrentPlayer()));
        } else if (command == MainMenuCommands.ChangeMenu) {
            if (result.success()) {
                appView.navigateMenu(new ProfileMenu(appView, App.getLoggedInUser()));
            }
        }

        if (result.success()) {
            System.out.println(result.message());
        } else {
            System.out.println("Error: " + result.message());
        }
    }
}
