package org.example.views;

import org.example.controllers.GameMenuController;
import org.example.models.Player.Player;
import org.example.models.common.Result;
import org.example.models.entities.User;
import org.example.models.enums.commands.GameMenuCommands;

public class GameMenu implements AppMenu {
    private AppView appView;
    private GameMenuController controller;
    private User user;
    private Player player;

    public GameMenu(AppView appView, User user, Player player) {
        this.appView = appView;
        this.user = user;
        this.player = player;
        controller = new GameMenuController(appView, player);
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

            if (command instanceof GameMenuCommands) {
                GameMenuCommands gameCommand = (GameMenuCommands) command;
                switch (gameCommand) {
                    case ShowMap:

                        break;
                    case ShowInventory:

                        break;
                }
            }
        } else {
            System.out.println("Error: " + result.message());
        }
    }
}
