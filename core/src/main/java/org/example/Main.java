package org.example;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.example.controllers.LoginRegisterMenuController;
import org.example.models.App;
import org.example.models.entities.User;
import org.example.utils.GameAssetManager;
import org.example.views.LoginRegisterMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    private static Main game;
    private static SpriteBatch batch;




    @Override
    public void create() {
        game = this;
        batch = new SpriteBatch();
        App.initialize();
        getGame().setScreen(new LoginRegisterMenuScreen(new LoginRegisterMenuController(null) , GameAssetManager.getGameAssetManager().getSkin()));

    }

    @Override
    public void render() {
        super.render(); // Delegate to screen
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
