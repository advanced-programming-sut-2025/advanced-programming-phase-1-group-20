package org.example.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class GameAssetManager {
    private static GameAssetManager gameAssetManager;
    private final Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
    private final Texture[] welcomeMenuImages = new Texture[20];

    public static GameAssetManager getGameAssetManager(){
        if (gameAssetManager == null) {
            gameAssetManager = new GameAssetManager();
        }
        return gameAssetManager;
    }

    public void loadWelcomeMenuTextures() {
        for (int i = 0; i < 20; i++) {
            welcomeMenuImages[i] = new Texture(Gdx.files.internal("WelcomeMenu/" + (i + 1) + ".png"));
        }
    }

    public Texture getWelcomeMenuTexture(int index) {
        return welcomeMenuImages[index - 1];
    }

    public Skin getSkin() {
        return skin;
    }


}
