package org.example.controllers;

import org.example.models.App;
import org.example.models.Player.Player;
import org.example.models.common.Result;
import org.example.models.entities.Game;
import org.example.models.entities.User;
import org.example.models.enums.commands.MainMenuCommands;
import org.example.views.AppView;
import org.example.views.GameMenu;
import org.example.views.LoginRegisterMenu;
import org.example.views.ProfileMenu;

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
        if (isMenuNavigationCommand(input)) {
            return processMenuNavigationCommand(input);
        }

        MainMenuCommands command = MainMenuCommands.getCommand(input);
        String[] args = command.parseInput(input);
        Result result = null;
        switch (command) {
            //implementing cases:
            case None -> result = Result.error("invalid command");
            case NewGame -> result = newGame(args);
            case LoadGame -> result = loadGame();
        }
        if (result == null) {
            result = Result.success("Command executed successfully");
        }
        return result;
    }

    private boolean isMenuNavigationCommand(String input) {
        return input.trim().startsWith("menu ") || input.trim().equals("show current menu");
    }

    private Result processMenuNavigationCommand(String input) {
        input = input.trim();

        if (input.equals("show current menu")) {
            return Result.success(appView.getCurrentMenuName());
        } else if (input.equals("menu exit")) {
            appView.navigateMenu(new LoginRegisterMenu(appView));
            return Result.success("Exited to login menu");
        } else if (input.startsWith("menu enter ")) {
            String menuName = input.substring("menu enter ".length()).trim().toLowerCase();

            if (menuName.equals("profile")) {
                appView.navigateMenu(new ProfileMenu(appView, user));
                return Result.success("Entered profile menu");
            } else if (menuName.equals("game")) {
                if (App.getGame() == null) {
                    return Result.error("No active game");
                }

                Player player = App.getGame().getPlayers().stream()
                        .filter(p -> p.getUser().equals(user))
                        .findFirst()
                        .orElse(null);

                if (player == null) {
                    return Result.error("User is not a player in the current game");
                }

                appView.navigateMenu(new GameMenu(appView, user, player));
                return Result.success("Entered game menu");
            } else {
                return Result.error("Cannot navigate from main menu to " + menuName + " menu");
            }
        }

        return Result.error("Invalid menu navigation command");
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
}
