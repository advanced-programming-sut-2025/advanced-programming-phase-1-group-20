package views;

import controllers.MainMenuController;
import models.App;
import models.User;

public class MainMenu implements AppMenu {
    private AppView appView;
    private User user;
    MainMenuController controller;
    public MainMenu(AppView appView, User user) {
        this.appView = appView;
        this.user = user;
        controller = new MainMenuController(appView, user);
    }
    @Override
    public void updateMenu(String input) {
        controller.update(input);
    }
}
