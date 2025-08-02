package org.example.client.controllers;

import com.badlogic.gdx.graphics.Texture;
import org.example.client.controllers.auth.LoginMenuController;
import org.example.client.controllers.auth.LoginRegisterMenuController;
import org.example.client.controllers.auth.SignUpMenuController;
import org.example.client.views.menu.LoginMenuScreen;
import org.example.client.views.menu.SignUpMenuScreen;
import org.example.client.views.menu.LoginRegisterMenuScreen;
import org.example.common.models.App;
import org.example.common.models.MapDetails.Building;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.MapDetails.GameMap;
import org.example.common.models.common.Location;
import org.example.common.models.Player.Player;
import org.example.common.models.entities.Game;
import org.example.common.models.entities.User;
import org.example.common.models.enums.PlayerEnums.Gender;
import org.example.common.models.enums.Types.TileType;
import org.example.utils.AssetManager;
import org.example.client.views.GameView;
import org.example.client.views.menu.WelcomeMenuScreen;

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
        getGame().setScreen(new LoginMenuScreen(new LoginMenuController(),
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
        User user2 = new User("guest user2" , "1234" , "guest@gmail.com" , "guest2" , Gender.Male);
        Player player2 = new Player(user2);
        User user3 = new User("guest user3" , "1234" , "guest@gmail.com" , "guest3" , Gender.Male);
        Player player3 = new Player(user3);
        User user4 = new User("guest user4" , "1234" , "guest@gmail.com" , "guest4" , Gender.Male);
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
        player1.setEnergyUnlimited();
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

        // Position players in their farms using global coordinates (like in multiplayer mode)
        for (Player player : players) {
            if (player != null && player.getCurrentFarm() != null) {
                Farm farm = player.getCurrentFarm();
                int farmIndex = farm.getFarmIndex();

                // Get the farm's building location
                Building building = farm.getBuilding();
                int houseCenterX = building.getX() + building.getWidth() / 2;
                int houseCenterY = building.getY() + building.getHeight() / 2;

                // Calculate global coordinates based on farm index
                int globalStartX = 0, globalStartY = 0;
                switch (farmIndex) {
                    case 0: // Top-Left
                        globalStartX = 0;
                        globalStartY = 0;
                        break;
                    case 1: // Bottom-Left
                        globalStartX = 0;
                        globalStartY = 78;
                        break;
                    case 2: // Top-Right
                        globalStartX = 156;
                        globalStartY = 0;
                        break;
                    case 3: // Bottom-Right
                        globalStartX = 156;
                        globalStartY = 78;
                        break;
                }

                // Position player near the house in global coordinates
                int playerStartX = globalStartX + houseCenterX;
                int playerStartY = globalStartY + houseCenterY - 3; // 3 tiles below house center

                // Ensure player is within farm boundaries
                if (playerStartY < globalStartY) {
                    playerStartY = globalStartY + houseCenterY + 3;
                }
                if (playerStartX < globalStartX) {
                    playerStartX = globalStartX + houseCenterX + 3;
                }
                if (playerStartX >= globalStartX + Farm.width) {
                    playerStartX = globalStartX + houseCenterX - 3;
                }
                if (playerStartY >= globalStartY + Farm.height) {
                    playerStartY = globalStartY + houseCenterY - 3;
                }

                // Create global location and set player position
                Location globalLocation = new Location(playerStartX, playerStartY, TileType.Dirt);
                player.setLocation(globalLocation);
                player.setIsInVillage(false);

                System.out.println("DEBUG: Positioned player " + player.getUser().getUsername() +
                    " at global coordinates (" + playerStartX + ", " + playerStartY + ") in farm " + farmIndex);
            }
        }

        map.updateTilesFromRegions();

        GameView gameMenuScreen = new GameView(new GameMenuController(player1) , player1 , game , assetManager.getSkin() ,user1);
        getGame().setScreen(gameMenuScreen);
    }
}
