package org.example.client.controllers;

import com.badlogic.gdx.graphics.Texture;
import org.example.client.controllers.auth.LoginRegisterMenuController;
import org.example.client.controllers.auth.SignUpMenuController;
import org.example.client.views.menu.SignUpMenuScreen;
import org.example.client.views.menu.LoginRegisterMenuScreen;
import org.example.common.models.App;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.MapDetails.GameMap;
import org.example.common.models.Player.Player;
import org.example.common.models.entities.Game;
import org.example.common.models.entities.User;
import org.example.common.models.enums.PlayerEnums.Gender;
import org.example.utils.AssetManager;
import org.example.client.views.GameView;
import org.example.client.views.menu.WelcomeMenuScreen;
import org.example.client.views.FarmSelectionScreen;

import java.util.ArrayList;
import java.util.List;

import static org.example.client.Main.getGame;

public class WelcomeMenuController {
    private WelcomeMenuScreen screen;
    private final AssetManager assetManager;

    private int currentImageIndex = 0;
    private float timeSinceLastChange = 0;
    private static final float IMAGE_CHANGE_INTERVAL = 0.1f;

    public WelcomeMenuController() {
        this.assetManager = AssetManager.getAssetManager();
    }

    public void setScreen(WelcomeMenuScreen screen) {
        this.screen = screen;
    }

    public void update(float delta) {
        timeSinceLastChange += delta;
        if (timeSinceLastChange >= IMAGE_CHANGE_INTERVAL) {
            timeSinceLastChange = 0;
            currentImageIndex = (currentImageIndex + 1) % assetManager.getWelcomeMenuImagesCount();
            updateBackground();
        }
    }

    private void updateBackground() {
        Texture currentTexture = assetManager.getWelcomeMenuTexture(currentImageIndex);
        screen.updateBackground(currentTexture);
    }

    public void handleLoginButton() {
        getGame().getScreen().dispose();
        getGame().setScreen(new LoginRegisterMenuScreen(new LoginRegisterMenuController(),
            AssetManager.getAssetManager().getSkin()));
    }

    public void handleSignUpButton() {
        getGame().getScreen().dispose();
        getGame().setScreen(new SignUpMenuScreen(new SignUpMenuController(),
            AssetManager.getAssetManager().getSkin()));
    }

    public void handleTryGameButton() {
        getGame().getScreen().dispose();

        User user1 = new User("guest user1" , "1234" , "guest@gmail.com" , "guest1" , Gender.Male);
        Player player1 = new Player(user1);
        User user2 = new User("guest user1" , "1234" , "guest@gmail.com" , "guest2" , Gender.Male);
        Player player2 = new Player(user2);
        User user3 = new User("guest user1" , "1234" , "guest@gmail.com" , "guest3" , Gender.Male);
        Player player3 = new Player(user3);
        User user4 = new User("guest user1" , "1234" , "guest@gmail.com" , "guest4" , Gender.Male);
        Player player4 = new Player(user4);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);

        Game game = new Game(players, player1);
        App.setGame(game);

        // Create GameMap but don't assign farms yet - wait for farm selection
        GameMap map = new GameMap();
        game.setGameMap(map);

        // Set the current user for the farm selection screen
        App.setLoggedInUser(user1);

        // Show farm selection screen instead of directly going to game
        FarmSelectionScreen farmSelectionScreen = new FarmSelectionScreen();
        getGame().setScreen(farmSelectionScreen);
    }
}
