package org.example.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetManager {
    private static AssetManager assetManager;
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

    // weather texture
    private final Texture rainyTexture = new Texture("content/clock/weather/rainy.png");
    private final Texture snowyTexture = new Texture("content/clock/weather/snowy.png");
    private final Texture sunnyTexture = new Texture("content/clock/weather/sunny.png");
    private final Texture weddingTexture = new Texture("content/clock/weather/wedding.png");
    private final Texture stormyTexture = new Texture("content/clock/weather/stormy.png");

    // season textures
    private final Texture winterTexture = new Texture("content/clock/seasons/winter.png");
    private final Texture springTexture = new Texture("content/clock/seasons/spring.png");
    private final Texture summerTexture = new Texture("content/clock/seasons/summer.png");
    private final Texture fallTexture = new Texture("content/clock/seasons/fall.png");

    // Rain textures - NEW
    private final Texture rain1Texture = new Texture("content/rain/1.png");
    private final Texture rain2Texture = new Texture("content/rain/2.png");

    public static AssetManager getAssetManager() {
        if (assetManager == null) {
            assetManager = new AssetManager();
        }
        return assetManager;
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

    AssetManager() {
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

    public Texture getRainyTexture() {
        return rainyTexture;
    }

    public Texture getSnowyTexture() {
        return snowyTexture;
    }

    public Texture getSunnyTexture() {
        return sunnyTexture;
    }

    public Texture getWeddingTexture() {
        return weddingTexture;
    }

    public Texture getStormyTexture() {
        return stormyTexture;
    }

    public Texture getWinterTexture() {
        return winterTexture;
    }

    public Texture getSpringTexture() {
        return springTexture;
    }

    public Texture getSummerTexture() {
        return summerTexture;
    }

    public Texture getFallTexture() {
        return fallTexture;
    }

    // NEW Rain texture getters
    public Texture getRain1Texture() {
        return rain1Texture;
    }

    public Texture getRain2Texture() {
        return rain2Texture;
    }

    public void disposeLoginMenuTextures() {
        for (Texture texture : loginMenuImages) {
            if (texture != null) {
                texture.dispose();
            }
        }
    }

    public void dispose() {
        if (rain1Texture != null) rain1Texture.dispose();
        if (rain2Texture != null) rain2Texture.dispose();
        if (rainyTexture != null) rainyTexture.dispose();
        if (snowyTexture != null) snowyTexture.dispose();
        if (sunnyTexture != null) sunnyTexture.dispose();
        if (weddingTexture != null) weddingTexture.dispose();
        if (stormyTexture != null) stormyTexture.dispose();
        if (winterTexture != null) winterTexture.dispose();
        if (springTexture != null) springTexture.dispose();
        if (summerTexture != null) summerTexture.dispose();
        if (fallTexture != null) fallTexture.dispose();
        if (skin != null) skin.dispose();
    }
}
