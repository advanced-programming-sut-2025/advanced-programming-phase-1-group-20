package org.example.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class GameAssetManager {
    private static GameAssetManager gameAssetManager;
    private final Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
    private final Texture signUpTitleTexture = new Texture("Titles/signUp.png");
    private final Texture loginTitleTexture = new Texture("Titles/login.png");
    private final Texture newTitleTexture = new Texture("Titles/new.png");
    private final Texture loadTitleTexture = new Texture("Titles/load.png");
    private final Texture exitTitleTexture = new Texture("Titles/exit.png");
    private final Texture backTitleTexture = new Texture("Titles/back.png");
    private final Texture developedByTitleTexture = new Texture("Titles/developedBy.png");
    private final Texture[] welcomeMenuImages = new Texture[20];

    public static GameAssetManager getGameAssetManager() {
        if (gameAssetManager == null) {
            gameAssetManager = new GameAssetManager();
        }
        return gameAssetManager;
    }

    public Texture getSignUpTexture() {
        return signUpTitleTexture;
    }

    public Texture getLoginTexture() {
        return loginTitleTexture;
    }

    public Texture getExitTexture() {
        return exitTitleTexture;
    }

    GameAssetManager() {
        loadWelcomeMenuTextures();
    }

    public void loadWelcomeMenuTextures() {
        for (int i = 0; i < 20; i++) {
            welcomeMenuImages[i] = new Texture(Gdx.files.internal("WelcomeMenu/" + (i + 1) + ".png"));
        }
    }

    public Texture getWelcomeMenuTexture(int index) {
        return welcomeMenuImages[index];
    }

    public int getWelcomeMenuImagesCount() {
        return welcomeMenuImages.length;
    }

    public Skin getSkin() {
        return skin;
    }

    public void disposeWelcomeMenuTextures() {
        for (Texture texture : welcomeMenuImages) {
            if (texture != null) {
                texture.dispose();
            }
        }
    }
}
