package org.example.controllers;

import com.badlogic.gdx.graphics.Texture;
import org.example.Main;
import org.example.models.App;
import org.example.models.MapDetails.Farm;
import org.example.models.MapDetails.GameMap;
import org.example.models.Player.Player;
import org.example.models.entities.Game;
import org.example.models.entities.User;
import org.example.models.enums.PlayerEnums.Gender;
import org.example.utils.GameAssetManager;
import org.example.views.GameView;
import org.example.views.LoginRegisterMenuScreen;
import org.example.views.WelcomeMenuScreen;

import java.util.ArrayList;
import java.util.List;

public class WelcomeMenuController {
    private WelcomeMenuScreen screen;
    private final GameAssetManager assetManager;

    private int currentImageIndex = 0;
    private float timeSinceLastChange = 0;
    private static final float IMAGE_CHANGE_INTERVAL = 0.1f;

    public WelcomeMenuController() {
        this.assetManager = GameAssetManager.getGameAssetManager();
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
        Main.getGame().getScreen().dispose();
        Main.getGame().setScreen(new LoginRegisterMenuScreen(new LoginRegisterMenuController(),
            GameAssetManager.getGameAssetManager().getSkin()));
    }

    public void handleSignUpButton() {
        Main.getGame().getScreen().dispose();
        LoginRegisterMenuScreen registerScreen = new LoginRegisterMenuScreen(new LoginRegisterMenuController(),
            GameAssetManager.getGameAssetManager().getSkin());
        Main.getGame().setScreen(registerScreen);
        registerScreen.showRegisterFormDirectly();
    }

    public void handleTryGameButton() {
        Main.getGame().getScreen().dispose();

        User user1 = new User("guest user1" , "1234" , "guest@gmail.com" , "guest" , Gender.Male);
        Player player1 = new Player(user1);
        User user2 = new User("guest user1" , "1234" , "guest@gmail.com" , "guest" , Gender.Male);
        Player player2 = new Player(user2);
        User user3 = new User("guest user1" , "1234" , "guest@gmail.com" , "guest" , Gender.Male);
        Player player3 = new Player(user3);
        User user4 = new User("guest user1" , "1234" , "guest@gmail.com" , "guest" , Gender.Male);
        Player player4 = new Player(user4);

        List<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        players.add(player3);
        players.add(player4);

        Game game = new Game(players, player1);
        App.setGame(game);

        GameMap map = new GameMap();
        Farm farm1 = new Farm("guest farm" , player1 , true , 0);
        player1.setCurrentFarm(farm1);
        map.addFarm(farm1);
        Farm farm2 = new Farm("guest farm" , player2 , false , 1);
        player2.setCurrentFarm(farm2);
        map.addFarm(farm2);
        Farm farm3 = new Farm("guest farm" , player3 , true , 2);
        player3.setCurrentFarm(farm3);
        map.addFarm(farm3);
        Farm farm4 = new Farm("guest farm" , player4 , false , 3);
        player4.setCurrentFarm(farm4);
        map.addFarm(farm4);

        game.setGameMap(map);
        GameView gameMenuScreen = new GameView(new GameMenuController(player1) , player1 , game , assetManager.getSkin() ,user1);
        Main.getGame().setScreen(gameMenuScreen);
    }
}
