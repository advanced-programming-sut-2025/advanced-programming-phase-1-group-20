package views;

import controllers.GameMenuController;
import models.Player;
import models.User;

public class GameMenu implements AppMenu{
    private AppView appView;
    private GameMenuController controller;
    private User user;
    private Player player;
    public GameMenu(AppView appView , User user , Player player) {
        this.appView = appView;
        this.user = user;
        this.player = player;
        controller = new GameMenuController(appView, player);
    }
    @Override
    public void updateMenu(String input) {
        controller.update(input);
    }
}
