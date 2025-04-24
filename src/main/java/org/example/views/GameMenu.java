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
            if (command instanceof GameMenuCommands gameCommand) {

                switch (gameCommand) {
                    case ShowMap:
                        displayMap();
                        break;
                    case ShowInventory:
                        displayInventory();
                        break;
                    case ShowDateTime:
                    case ShowTime:
                    case ShowDate:
                    case DayOfWeek:
                    case ShowSeason:
                    case ShowWeather:
                    case ShowWeatherForecast:

                        break;
                    default:
                        System.out.println(result.message());
                        break;
                }
            } else {
                System.out.println(result.message());
            }
        } else {
            System.out.println("Error: " + result.message());
        }
    }

    private void displayMap() {
        // TODO: Implement map display
    }

    private void displayInventory() {
        System.out.println("Inventory: ");
        // TODO: Implement  inventory display
    }
}