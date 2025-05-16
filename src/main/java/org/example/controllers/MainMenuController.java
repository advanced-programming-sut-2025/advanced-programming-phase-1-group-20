package org.example.controllers;

import org.example.models.App;
import org.example.models.Player.Player;
import org.example.models.common.Result;
import org.example.models.entities.Game;
import org.example.models.entities.User;
import org.example.models.enums.commands.MainMenuCommands;
import org.example.models.utils.AutoLoginUtil;
import org.example.views.AppView;
import org.example.views.LoginRegisterMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
            case None -> result = Result.error("invalid command");
            case NewGame -> result = newGame(args);
            case LoadGame -> result = loadGame();
            case ShowCurrentMenu -> result = Result.success("Main menu");
            case UserLogout -> result = logout();
            case ChangeMenu -> result = changeMenu(args);
        }

        appView.handleResult(result, command);
        return result;
    }


    // implementing methods
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
        users.add(App.getLoggedInUser());

        String[] cleaned = Arrays.stream(args).filter(Objects::nonNull).toArray(String[]::new);

        for (String username : cleaned) {
            String trimmedUsername = username.trim();
            if (!trimmedUsername.isEmpty()) {
                User user = App.getUser(trimmedUsername);
                if (user == null) {
                    return Result.error("Invalid username: " + trimmedUsername);
                }
                if (App.isUserInGame(user)) {
                    return Result.error(trimmedUsername + " is already in a game");
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
        App.getGame().getGameMap().getVillage().initializeNPCs();
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
        appView.navigateMenu(new LoginRegisterMenu(appView));
        return Result.success("logged out");
    }

    public Result changeMenu(String[] args) {
        if (args == null || args.length < 1) {
            return Result.error("No menu specified");
        }

        String menuName = args[0].toLowerCase();
        if (!menuName.equals("profile menu")) {
            return Result.error("Only profile menu is supported");
        }

        return Result.success("entered profile menu");
    }
}