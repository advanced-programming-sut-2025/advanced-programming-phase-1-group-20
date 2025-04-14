package controllers;

import models.Result;
import models.User;
import models.enums.commands.MainMenuCommands;
import views.AppView;

public class MainMenuController implements Controller {
    AppView appView;
    User user;
    public MainMenuController(AppView appView, User user) {
        this.appView = appView;
        this.user = user;
    }
    @Override
    public void update(String input) {
        MainMenuCommands command = MainMenuCommands.getCommand(input);
        String[] args = command.parseInput(input);
        switch (command) {
            //implementing cases:
            case None -> Result.error("Invalid input");
        }
    }

    //implementing methods
}
