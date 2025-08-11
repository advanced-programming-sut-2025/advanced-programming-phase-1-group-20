package org.example.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.example.common.models.enums.Types.CropType;
import org.example.common.models.enums.Types.TreeType;
import org.example.common.models.enums.Types.TileType;

import java.util.HashMap;
import java.util.Map;

public class AssetManager {
    private static AssetManager assetManager;
    private final Skin skin = new Skin(Gdx.files.internal("content/ui/uiskin.json"));

    // Texture cache for tiles and game assets
    private final Map<String, Texture> textureCache = new HashMap<>();
    private final Texture signUpTitleTexture = new Texture("content/Titles/signUp.png");
    private final Texture loginTitleTexture = new Texture("content/Titles/login.png");
    private final Texture newTitleTexture = new Texture("content/Titles/new.png");
    private final Texture loadTitleTexture = new Texture("content/Titles/load.png");
    private final Texture exitTitleTexture = new Texture("content/Titles/exit.png");
    private final Texture backTitleTexture = new Texture("content/Titles/back.png");
    private final Texture developedByTitleTexture = new Texture("content/Titles/developedBy.png");
    private final Texture registerTitleTexture = new Texture("content/Titles/register.png");
    private final Texture startGameTitleTexture = new Texture("content/Titles/startGame.png");
    private final Texture changeAvatarTitleTexture = new Texture("content/Titles/changeAvatar.png");
    private final Texture forgotPasswordTitleTexture = new Texture("content/Titles/forgotPassword.png");
    private final Texture playAsGuestTitleTexture = new Texture("content/Titles/playAsGuest.png");

    public Texture getChangeTitleTexture() {
        return changeTitleTexture;
    }

    private final Texture changeTitleTexture = new Texture("content/Titles/change.png");
    private final Texture profileMenuTitleTexture = new Texture("content/Titles/profileMenu.png");
    private final Texture settingMenuTitleTexture = new Texture("content/Titles/settingMenu.png");
    private final Texture singlePlayerTitleTexture = new Texture("content/Titles/singlePlayer.png");

    private final Texture miniGameBackground = new Texture("content/fishing_mini_game/background.jpg");

    public Texture getMiniGameBackground() {
        return miniGameBackground;
    }
    public Texture getMultiPlayerTitleTexture() {
        return multiPlayerTitleTexture;
    }

    public Texture getSinglePlayerTitleTexture() {
        return singlePlayerTitleTexture;
    }

    private final Texture multiPlayerTitleTexture = new Texture("content/Titles/multiPlayer.png");

    public Texture getForgotPasswordTitleTexture() {
        return forgotPasswordTitleTexture;
    }

    public Texture getStartGameTitleTexture() {
        return startGameTitleTexture;
    }

    public Texture getRegisterTitleTexture() {
        return registerTitleTexture;
    }

    public Texture getBackTitleTexture() {
        return backTitleTexture;
    }

    public Texture getExitTitleTexture() {
        return exitTitleTexture;
    }

    public Texture getLoadTitleTexture() {
        return loadTitleTexture;
    }

    public Texture getNewTitleTexture() {
        return newTitleTexture;
    }

    public Texture getLoginTitleTexture() {
        return loginTitleTexture;
    }

    public Texture getSignUpTitleTexture() {
        return signUpTitleTexture;
    }

    public Texture getProfileMenuTitleTexture() {
        return profileMenuTitleTexture;
    }

    public Texture getSettingMenuTitleTexture() {
        return settingMenuTitleTexture;
    }

    public Texture getLogoutTitleTexture() {
        return logoutTitleTexture;
    }

    private final Texture logoutTitleTexture = new Texture("content/Titles/logout.png");
    private final Texture[] welcomeMenuImages = new Texture[20];
    private final Texture[] signUpMenuImages = new Texture[20];
    private final Texture[] loginMenuImages = new Texture[20];
    private final Texture[] profileMenuImages = new Texture[20];
    private final Texture[] mainMenuImages = new Texture[20];

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

    // Fishing mini-game assets
    private final Texture fishingWaterLaneTexture = new Texture("content/fishing_mini_game/water_lane.png");
    private final Texture fishingFishIconTexture = new Texture("content/fishing_mini_game/fish_icon.png");
    private final Texture fishingLegendaryFishIconTexture = new Texture("content/fishing_mini_game/LegendaryFish_icon.png");
    private final Texture fishingSafezoneTexture = new Texture("content/fishing_mini_game/safezone.png");

    // Snow textures
    private final Texture[] snowTextures = new Texture[4];

    public static final float BACKGROUND_CHANGE_INTERVAL = 0.1f;

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
        loadSignUpMenuTextures();
        loadLoginMenuTextures();
        loadProfileMenuTextures();
        loadMainMenuTextures();
        for (int i = 1; i < 4; i++) {
            snowTextures[i] = new Texture(Gdx.files.internal("content/snow/" + i + ".png"));
        }

        // Initialize tile texture cache
        initializeTileTextureCache();
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

    public void loadProfileMenuTextures() {
        for (int i = 0; i < 20; i++) {
            profileMenuImages[i] = new Texture(Gdx.files.internal("Menu/" + (i + 1) + ".png"));
        }
    }

    public Texture getProfileMenuTexture(int index) {
        return profileMenuImages[index];
    }

    public int getProfileMenuImagesCount() {
        return profileMenuImages.length;
    }

    public void disposeProfileMenuTextures() {
        for (Texture texture : profileMenuImages) {
            if (texture != null) {
                texture.dispose();
            }
        }
    }

    public void loadMainMenuTextures() {
        for (int i = 0; i < 20; i++) {
            mainMenuImages[i] = new Texture(Gdx.files.internal("Menu/" + (i + 1) + ".png"));
        }
    }

    public Texture getMainMenuTexture(int index) {
        if (index >= 0 && index < mainMenuImages.length && mainMenuImages[index] != null) {
            return mainMenuImages[index];
        }
        // Return a fallback texture or the first available texture
        for (int i = 0; i < mainMenuImages.length; i++) {
            if (mainMenuImages[i] != null) {
                return mainMenuImages[i];
            }
        }
        // If no textures are loaded, return null (this will be handled by the caller)
        return null;
    }

    public int getMainMenuImagesCount() {
        return mainMenuImages.length;
    }

    public void disposeMainMenuTextures() {
        for (Texture texture : mainMenuImages) {
            if (texture != null) {
                texture.dispose();
            }
        }
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

    // Fishing mini-game asset getters
    public Texture getFishingWaterLaneTexture() {
        return fishingWaterLaneTexture;
    }

    public Texture getFishingFishIconTexture() {
        return fishingFishIconTexture;
    }

    public Texture getFishingLegendaryFishIconTexture() {
        return fishingLegendaryFishIconTexture;
    }

    public Texture getFishingSafezoneTexture() {
        return fishingSafezoneTexture;
    }

    public Texture getFishTexture(String fishImagePath) {
        try {
            Texture texture = new Texture(Gdx.files.internal(fishImagePath));
            return texture;
        } catch (Exception e) {
            Gdx.app.error("AssetManager", "Failed to load fish texture: " + fishImagePath + " - " + e.getMessage());
            // Return a fallback texture
            return fishingFishIconTexture;
        }
    }

    public Texture[] getSnowTextures() {
        return snowTextures;
    }

    public void disposeLoginMenuTextures() {
        for (Texture texture : loginMenuImages) {
            if (texture != null) {
                texture.dispose();
            }
        }
    }

    // Tile texture cache methods
    private void initializeTileTextureCache() {
        Gdx.app.log("AssetManager", "Starting to preload tile textures...");

        String[] seasons = {"Spring", "Summer", "Fall", "Winter"};
        for (String season : seasons) {
            String grassPath = "content/grass/" + season + ".png";
            if (loadTexture("grass_" + season.toLowerCase(), grassPath)) {
                Gdx.app.log("AssetManager", "Loaded grass texture for " + season);
            }
        }

        // Pre-load tile textures
        loadTexture("lake", "content/flooring/lake.png");
        loadTexture("stone", "content/Crafting/Stone.png");
        loadTexture("iron_ore", "content/Crafting/Iron_Ore.png");
        loadTexture("gold_ore", "content/Crafting/Gold_Ore.png");
        loadTexture("iridium_stone", "content/map_elements/iridium_stone.png");
        loadTexture("jewel_stone", "content/map_elements/jewel_stone.png");
        loadTexture("gold_stone", "content/map_elements/gold_stone.png");
        loadTexture("plowed", "content/map_elements/shoveled.png");
        loadTexture("path", "content/path.png");
        loadTexture("shipping_bin", "content/Buildings/Shipping_Bin.png");

        // Building textures (larger sprites)
        loadTexture("barn", "content/buildings/barn.png");
        loadTexture("coop", "content/buildings/Coop.png");
        loadTexture("house", "content/buildings/house.png");

        // Additional barn and coop textures
        loadTexture("big_barn", "content/Buildings/Big Barn.png");
        loadTexture("deluxe_barn", "content/Buildings/Deluxe Barn.png");
        loadTexture("big_coop", "content/Buildings/Big Coop.png");
        loadTexture("deluxe_coop", "content/Buildings/Deluxe Coop.png");

        // Greenhouse textures
        loadTexture("greenhouse", "content/Buildings/GreenHouse/UnConstructed.png");
        loadTexture("constructed_greenhouse", "content/Buildings/GreenHouse/Constructed.png");

        // Market textures for village
        loadTexture("blacksmith", "content/map_elements/Blacksmith.png");
        loadTexture("joja_mart", "content/map_elements/Jojamart.png");
        loadTexture("pierre_store", "content/map_elements/Pierres_shop.png");
        loadTexture("carpenters_shop", "content/map_elements/Carpenter's_Shop.png");
        loadTexture("fish_shop", "content/map_elements/Fish_Shop.png");
        loadTexture("marnie_shop", "content/map_elements/Ranch.png");
        loadTexture("stardrop_saloon", "content/map_elements/Saloon.png");

        // Village building textures
        loadTexture("gold_clock", "content/Buildings/Gold_Clock.png");
        loadTexture("town_hall", "content/Buildings/town_hall.png");
        loadTexture("mayor_house", "content/Buildings/mayor_house.png");
        loadTexture("fish_pond", "content/Buildings/fish_pond.png");
        loadTexture("museum", "content/Buildings/museum.png");
        loadTexture("npc_house", "content/map_elements/npc_house1.png"); // Default NPC house texture

        loadTexture("fence", "content/Fence/Iron_Fence.png");
        preloadTrees();
        preloadCrops();

        loadTexture("branch", "content/Crafting/Stone.png");
        loadTexture("quarry", "content/Crafting/Stone.png");

        Gdx.app.log("AssetManager", "Finished preloading tile textures. Cache size: " + textureCache.size());
    }

    private void preloadTrees() {
        for (TreeType treeType : TreeType.values()) {
            for (int i = 1; i < 5; i++) {
                String key = treeType.getImageFilePath() + "_" + i;
                String treePath = "content/Trees/" + treeType.getImageFilePath() + "_" + "Stage_" + i + ".png";
                loadTexture(key, treePath);
            }
        }
    }

    private void preloadCrops() {
        for (CropType cropType : CropType.values()) {
            String key = cropType.getImageFilePath();
            String cropPath = "content/Crops/" + cropType.getImageFilePath() + ".png";
            loadTexture(key, cropPath);
        }
    }

    private boolean loadTexture(String key, String path) {
        try {
            Texture texture = new Texture(path);
            textureCache.put(key, texture);
            Gdx.app.log("AssetManager", "Successfully loaded: " + path);
            return true;
        } catch (Exception e) {
            Gdx.app.error("AssetManager", "Failed to load texture: " + path + " - " + e.getMessage());
            return false;
        }
    }

    public Texture getTileTexture(String key) {
        return textureCache.get(key);
    }

    public Texture getTileTextureForType(String tileType, String season) {
        switch (tileType.toLowerCase()) {
            case "lake":
            case "water":
                return getTileTexture("lake");
            case "stone":
                return getTileTexture("stone");
            case "iron_ore":
                return getTileTexture("iron_ore");
            case "gold_ore":
                return getTileTexture("gold_ore");
            case "iridium_stone":
                return getTileTexture("iridium_stone");
            case "jewel_stone":
                return getTileTexture("jewel_stone");
            case "gold_stone":
                return getTileTexture("gold_stone");
            case "plowed":
                return getTileTexture("plowed");
            case "path":
                return getTileTexture("path");
            case "shipping_bin":
                return getTileTexture("shipping_bin");
            case "fence":
                return getTileTexture("fence");
            case "branch":
                return getTileTexture("branch");
            case "quarry":
                return getTileTexture("quarry");
            case "dirt":
            case "grass":
                return getTileTexture("grass_" + season.toLowerCase());
            case "tree":
                return getTileTexture("grass_" + season.toLowerCase()); // Trees are rendered separately
            case "crop":
                return getTileTexture("grass_" + season.toLowerCase()); // Crops are rendered separately
            case "barn":
            case "coop":
            case "building":
            case "greenhouse":
            case "constructed_greenhouse":
                return getTileTexture("grass_" + season.toLowerCase()); // Buildings are rendered separately
            case "village":
                return getTileTexture("grass_" + season.toLowerCase()); // Village tiles use grass
            case "market":
                return getTileTexture("grass_" + season.toLowerCase()); // Markets are rendered separately
            case "blacksmith":
                return getTileTexture("blacksmith");
            case "jojamart":
            case "joja_mart":
                return getTileTexture("joja_mart");
            case "pierre_general_store":
            case "pierre_store":
                return getTileTexture("pierre_store");
            case "carpenters_shop":
                return getTileTexture("carpenters_shop");
            case "fish_shop":
                return getTileTexture("fish_shop");
            case "marnie_shop":
                return getTileTexture("marnie_shop");
            case "stardrop_saloon":
                return getTileTexture("stardrop_saloon");
            case "gold_clock":
                return getTileTexture("gold_clock");
            case "npc_house":
                return getTileTexture("npc_house");
            case "sand":
                return getTileTexture("grass_" + season.toLowerCase()); // Use grass as fallback for sand
            default:
                return getTileTexture("grass_" + season.toLowerCase()); // Default to grass
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
        for (Texture texture : snowTextures) {
            if (texture != null) texture.dispose();
        }

        // Dispose tile texture cache
        for (Texture texture : textureCache.values()) {
            if (texture != null) texture.dispose();
        }
        textureCache.clear();

        if (skin != null) skin.dispose();
    }
}
