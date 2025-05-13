package org.example.controllers;

import org.example.models.App;
import org.example.models.Player.Player;
import org.example.models.common.Result;
import org.example.models.entities.Game;
import org.example.models.entities.User;
import org.example.models.enums.commands.MainMenuCommands;
import org.example.models.utils.AutoLoginUtil;
import org.example.views.AppView;

import java.util.ArrayList;
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
            case LoadGame -> result = loadGame();
            case ShowCurrentMenu -> result = Result.success("Main menu");
            case UserLogout -> result = logout();
        }
        if (result == null) {
            result = Result.success("Command executed successfully");
        }
        return result;
    }


    //implementing methods
    public Result loadGame() {
        Game game = App.loadCurrentGame();
        if (game == null) {
            return Result.error("No saved game found");
        }

        if (!game.isPlayerInGame(user)) {
            return Result.error("You are not a player in this game");
        }

        // Set the game creator to the current player
        Player player = game.getPlayers().stream()
                .filter(p -> p.getUser().equals(user))
                .findFirst()
                .orElse(null);

        if (player == null) {
            return Result.error("Player not found in game");
        }

        game.setGameCreator(player);

        App.setGame(game);

        return Result.success("Game loaded successfully");
    }

    public Result newGame(String[] args) {
        if (args == null || args.length < 1) {
            return Result.error("No usernames specified");
        }

        List<User> users = new ArrayList<>();
        users.add(this.user);

        for (String username : args) {
            if (username != null && !username.isEmpty()) {
                User user = App.getUser(username);
                if (user == null) {
                    return Result.error("Invalid username: " + username);
                }
                if (App.isUserInGame(user)) {
                    return Result.error(username + " is already in a game");
                }
                if (!users.contains(user)) {
                    users.add(user);
                }
            }
        }

        if (users.size() > 4) {
            return Result.error("Too many users specified (maximum 4 including creator)");
        }

        List<Player> players = new ArrayList<>();
        for (User user : users) {
            players.add(new Player(user));
        }

        Player creator = players.stream()
                .filter(p -> p.getUser().equals(this.user))
                .findFirst()
                .orElse(players.get(0));

        Game newGame = new Game(players, creator);

        App.setGame(newGame);
        return Result.success("New game created with " + users.size() + " players. Please select your map.");
    }

    public Result logout() {
        AutoLoginUtil.clearAutoLogin();

        User user = App.getLoggedInUser();
        if (user != null) {
            user.setStayLoggedIn(false);
            App.saveData();
        }

        App.setLoggedInUser(null);
        return Result.success("logged out");
    }

}
