package org.example.views;

import org.example.controllers.ProfileMenuController;
import org.example.models.common.Result;
import org.example.models.entities.User;
import org.example.models.enums.commands.ProfileMenuCommands;

public class ProfileMenu implements AppMenu {
    private AppView appView;
    private User user;
    private ProfileMenuController controller;

    public ProfileMenu(AppView appView, User user) {
        this.appView = appView;
        this.user = user;
        controller = new ProfileMenuController(appView, user);
    }

    @Override
    public void updateMenu(String input) {
        controller.update(input);
    }

    @Override
    public void handleResult(Result result, Object command) {
        if (result == null) return;

        if (command == ProfileMenuCommands.ChangeMenu) {
            if (result.success()) {
                appView.navigateMenu(new MainMenu(appView, user));
            }
        }

        if (result.success()) {
            System.out.println(result.message());
        } else {
            System.out.println("Error: " + result.message());
        }
    }
}
