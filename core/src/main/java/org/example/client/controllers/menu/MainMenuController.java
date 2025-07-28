package org.example.client.controllers.menu;

import com.badlogic.gdx.graphics.Texture;
import org.example.client.controllers.MultiplayerMenuController;
import org.example.client.controllers.ProfileMenuController;
import org.example.client.network.ClientMessageHandler;
import org.example.client.network.NetworkClient;
import org.example.client.views.MultiplayerMenuScreen;
import org.example.client.views.ProfileMenuScreen;
import org.example.client.views.menu.LoginMenuScreen;
import org.example.common.models.App;
import org.example.common.models.Message;
import org.example.client.controllers.auth.LoginMenuController;
import org.example.utils.AssetManager;
import org.example.client.views.menu.MainMenuScreen;

import java.util.List;

import static org.example.client.Main.getGame;

public class MainMenuController implements ClientMessageHandler.OnlinePlayersListener {
    private MainMenuScreen view;
    private int currentImageIndex = 0;
    private float timeSinceLastChange = 0;
    private static final float IMAGE_CHANGE_INTERVAL = 0.1f;
    private final NetworkClient networkClient;
    private final ClientMessageHandler messageHandler;

    public MainMenuController() {
        this.networkClient = NetworkClient.getInstance();
        this.messageHandler = networkClient.getMessageHandler();
        
        // Set up online players listener
        this.messageHandler.setOnlinePlayersListener(this);
    }

    public void setView(MainMenuScreen view) {
        this.view = view;
    }

    public void update(float delta) {
        timeSinceLastChange += delta;
        if (timeSinceLastChange >= IMAGE_CHANGE_INTERVAL) {
            timeSinceLastChange = 0;
            currentImageIndex = (currentImageIndex + 1) % AssetManager.getAssetManager().getMainMenuImagesCount();
            Texture newTexture = AssetManager.getAssetManager().getMainMenuTexture(currentImageIndex);
            view.updateBackground(newTexture);
        }
    }

    public void requestOnlinePlayersList() {
        try {
            Message message = new Message();
            message.setType(Message.Type.REQUEST_PLAYERS_LIST);
            networkClient.sendMessage(message);
        } catch (Exception e) {
            System.err.println("Failed to request online players list: " + e.getMessage());
        }
    }

    public void handleOnlinePlayersListUpdate(List<Object> playersList) {
        if (view != null && playersList != null) {
            view.updateOnlinePlayersList(playersList);
        }
    }

    // OnlinePlayersListener implementation
    @Override
    public void onOnlinePlayersUpdate(List<Object> players) {
        handleOnlinePlayersListUpdate(players);
    }

    public void handleSinglePlayer() {
        getGame().getScreen().dispose();
//        getGame().setScreen(new SinglePlayerMenuScreen(new SinglePlayerMenuController(),
//            AssetManager.getAssetManager().getSkin()));
    }

    public void handleMultiPlayer() {
        getGame().setScreen(new MultiplayerMenuScreen(new MultiplayerMenuController(),
            AssetManager.getAssetManager().getSkin()));
    }

    public void handleLoadGame() {
        getGame().getScreen().dispose();
//        getGame().setScreen(new LoadGameScreen(new LoadGameController(),
//            AssetManager.getAssetManager().getSkin()));
    }

    public void handleProfile() {
        getGame().getScreen().dispose();
        getGame().setScreen(new ProfileMenuScreen(new ProfileMenuController(),
            AssetManager.getAssetManager().getSkin()));
    }

    public void handleSettings() {
        getGame().getScreen().dispose();
//        getGame().setScreen(new SettingsScreen(new SettingsController(),
//            AssetManager.getAssetManager().getSkin()));
    }

    public void handleLogout() {
        App.logout();
        getGame().getScreen().dispose();
        getGame().setScreen(new LoginMenuScreen(new LoginMenuController(),
            AssetManager.getAssetManager().getSkin()));
    }
}
