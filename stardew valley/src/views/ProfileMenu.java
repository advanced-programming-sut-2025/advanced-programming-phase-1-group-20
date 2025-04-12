package views;

import controllers.ProfileMenuController;
import models.App;
import models.User;

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
}
