package org.example.server.controllers;

import org.example.client.views.MainMenuScreen;
import org.example.common.models.App;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Result;
import org.example.common.models.entities.Game;
import org.example.common.models.entities.User;
import org.example.utils.AutoLoginUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainMenuController implements Controller {
    private MainMenuScreen view;
    private User user;

    public MainMenuController(User user) {
        this.user = user;
    }

    public void setView(MainMenuScreen view) {
        this.view = view;
    }

    @Override
    public void setupListeners() {}


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

        if (users.size() > 4) {
            return Result.error("Too many users specified (maximum 4 including creator)");
        }

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
        //TODO : change this
//        appView.navigateMenu(new LoginRegisterMenu(appView));
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
