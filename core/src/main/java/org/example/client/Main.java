package org.example.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.example.client.controllers.WelcomeMenuController;
import org.example.common.models.App;
import org.example.utils.AssetManager;
import org.example.client.views.menu.WelcomeMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    private static Main game;
    private static SpriteBatch batch;

    @Override
    public void create() {
        game = this;
        batch = new SpriteBatch();

        Pixmap pixmap = new Pixmap(Gdx.files.internal("content/cursor.png"));
        int xHotspot = 0;
        int yHotspot = 0;

        Cursor customCursor = Gdx.graphics.newCursor(pixmap, xHotspot, yHotspot);
        Gdx.graphics.setCursor(customCursor);

        App.initialize();
        WelcomeMenuController welcomeMenuController = new WelcomeMenuController();
        WelcomeMenuScreen welcomeMenuScreen = new WelcomeMenuScreen(welcomeMenuController , AssetManager.getAssetManager().getSkin());
        welcomeMenuController.setScreen(welcomeMenuScreen);
        getGame().setScreen(welcomeMenuScreen);
    }

    @Override
    public void render() {
        super.render(); // Delegate to screen
    }

    @Override
    public void setScreen(com.badlogic.gdx.Screen screen) {
        System.out.println("DEBUG: Main.setScreen() called with screen: " + (screen != null ? screen.getClass().getSimpleName() : "null"));
        if (screen != null) {
            System.out.println("DEBUG: Screen class: " + screen.getClass().getName());
        }
        super.setScreen(screen);
        System.out.println("DEBUG: Main.setScreen() completed");
    }

    @Override
    public com.badlogic.gdx.Screen getScreen() {
        com.badlogic.gdx.Screen currentScreen = super.getScreen();
        System.out.println("DEBUG: Main.getScreen() called, returning: " + (currentScreen != null ? currentScreen.getClass().getSimpleName() : "null"));
        return currentScreen;
    }

    @Override
    public void dispose() {
        super.dispose(); // Let current screen handle its own cleanup
    }

    public static Main getGame() {
        return game;
    }

    public static SpriteBatch getBatch() {
        return batch;
    }

}
