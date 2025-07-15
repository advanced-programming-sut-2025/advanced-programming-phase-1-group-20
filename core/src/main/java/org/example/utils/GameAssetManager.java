package org.example.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class GameAssetManager {
    private static GameAssetManager gameAssetManager;
    private final Skin skin = new Skin(Gdx.files.internal("content/ui/uiskin.json"));
    private final Texture signUpTitleTexture = new Texture("content/Titles/signUp.png");
    private final Texture loginTitleTexture = new Texture("content/Titles/login.png");
    private final Texture newTitleTexture = new Texture("content/Titles/new.png");
    private final Texture loadTitleTexture = new Texture("content/Titles/load.png");
    private final Texture exitTitleTexture = new Texture("content/Titles/exit.png");
    private final Texture backTitleTexture = new Texture("content/Titles/back.png");
    private final Texture developedByTitleTexture = new Texture("content/Titles/developedBy.png");
    private final Texture[] welcomeMenuImages = new Texture[20];
    private final Texture[] signUpMenuImages = new Texture[20];
    private final Texture[] loginMenuImages = new Texture[20];

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

    public Skin getSkin() {
        return skin;
    }

    public void loadWelcomeMenuTextures() {
        for (int i = 0; i < 20; i++) {
            welcomeMenuImages[i] = new Texture(Gdx.files.internal("content/WelcomeMenu/" + (i + 1) + ".png"));
        }
    }

    public Texture getWelcomeMenuTexture(int index) {
        return welcomeMenuImages[index];
    }

    public int getWelcomeMenuImagesCount() {
        return welcomeMenuImages.length;
    }

    public void disposeWelcomeMenuTextures() {
        for (Texture texture : welcomeMenuImages) {
            if (texture != null) {
                texture.dispose();
            }
        }
    }

    public void loadSignUpMenuTextures() {
        for (int i = 0; i < 20; i++) {
            signUpMenuImages[i] = new Texture(Gdx.files.internal("Menu/" + (i + 1) + ".png"));
        }
    }

    public Texture getSignUpMenuTexture(int index) {
        return signUpMenuImages[index];
    }

    public int getSignUpMenuImagesCount() {
        return signUpMenuImages.length;
    }

    public void disposeSignUpMenuTextures() {
        for (Texture texture : signUpMenuImages) {
            if (texture != null) {
                texture.dispose();
            }
        }
    }

    public void loadLoginMenuTextures() {
        for (int i = 0; i < 20; i++) {
            loginMenuImages[i] = new Texture(Gdx.files.internal("Menu/" + (i + 1) + ".png"));
        }
    }

    public Texture getLoginMenuTexture(int index) {
        return loginMenuImages[index];
    }

    public int getLoginMenuImagesCount() {
        return loginMenuImages.length;
    }

    public void disposeLoginMenuTextures() {
        for (Texture texture : loginMenuImages) {
            if (texture != null) {
                texture.dispose();
            }
        }
    }
}
