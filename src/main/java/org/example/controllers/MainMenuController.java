package org.example.controllers;

import org.example.models.Result;
import org.example.models.User;
import org.example.models.enums.commands.MainMenuCommands;
import org.example.views.AppView;

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
            // TODO: implement the commands
        }
    }

    //implementing methods
}
