package org.example.controllers;

import org.example.models.App;
import org.example.models.Player.Player;
import org.example.models.common.Result;
import org.example.models.entities.Game;
import org.example.models.entities.User;
import org.example.models.enums.commands.MainMenuCommands;
import org.example.views.AppView;

import java.util.List;

public class MainMenuController implements Controller {
    AppView appView;
    User user;

    public MainMenuController(AppView appView, User user) {
        this.appView = appView;
        this.user = user;
    }

    @Override
    public Result update(String input) {
        MainMenuCommands command = MainMenuCommands.getCommand(input);
        String[] args = command.parseInput(input);
        Result result = null;
        switch (command) {
            //implementing cases:
            case None -> result = Result.error("invalid command");
            case NewGame -> result = newGame(args);
        }
        return Result.success("Command executed successfully");
    }

    //implementing methods
    public Result newGame(String[] args) {
        User user1 = App.getUser(args[0]);
        User user2 = App.getUser(args[1]);
        User user3 = App.getUser(args[2]);

        if (user1 == null || user2 == null || user3 == null) {
            return Result.error("invalid users");
        }

        List<User> users = List.of(user1, user2, user3);
        for (User user : users) {
            if (App.isUserInGame(user)) {
                return Result.error(user.getUsername() + " is already in a game");
            }
        }

        List<Player> players = List.of(
                new Player(user1),
                new Player(user2),
                new Player(user3)
        );
        Game newGame = new Game(players);

        App.setGame(newGame);
        return Result.success("game initialized successfully");
    }
}
