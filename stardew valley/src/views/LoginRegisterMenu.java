package views;

import controllers.LoginRegisterMenuController;
import models.App;
import models.User;

public class LoginRegisterMenu implements AppMenu {
    private AppView appView;
    private User user;
    private LoginRegisterMenuController controller;
    public LoginRegisterMenu(AppView appView) {
        this.appView = appView;
        user = null;
        controller = new LoginRegisterMenuController(appView , user);
    }
    @Override
    public void updateMenu(String input) {
        controller.update(input);
    }
}
