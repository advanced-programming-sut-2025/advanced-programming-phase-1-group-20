package org.example.client.controllers.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.example.client.Main;
import org.example.client.controllers.GameMenuController;
import org.example.client.views.GreenhouseRepairDialog;
import org.example.client.views.gameplay.GreenhouseScreen;
import org.example.client.views.gameplay.MarketMenuScreen;
import org.example.client.views.gameplay.RefrigeratorScreen;
import org.example.common.models.Items.*;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.MapDetails.Village;
import org.example.common.models.MapDetails.Building;
import org.example.common.models.App;
import org.example.common.models.Market;
import org.example.common.models.enums.Seasons;
import org.example.common.models.enums.Types.*;
import org.example.common.models.common.Location;
import org.example.common.models.MapDetails.GameMap;
import org.example.common.models.entities.Game;
import org.example.common.models.Player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class WorldController {
    private PlayerController playerController;
    private Farm farm;
    private OrthographicCamera camera;
    private Skin skin;
    private GameMenuController controller;

    private Map<String, Texture> textureCache;
    private Set<String> renderedBuildings;

    // Building anchor collections
    private List<Location> greenhouseAnchors;
    private List<Location> houseAnchors;
    private List<Location> barnAnchors;
    private List<Location> coopAnchors;
    private List<Location> goldClockAnchors;
    private List<Location> npcHouseAnchors;

    //Markets
    private List<Location> blacksmith;
    private List<Location> jojaMart;
    private List<Location> pierreGeneralStore;
    private List<Location> carpentersShop;
    private List<Location> fishShop;
    private List<Location> marnieShop;
    private List<Location> starDropSaloon;

    private static final int TILE_SIZE = 60;

    // Building dimensions in tiles
    private static final int GREENHOUSE_TILES_W = 7;
    private static final int GREENHOUSE_TILES_H = 6;
    private static final int HOUSE_TILES_W = 5;
    private static final int HOUSE_TILES_H = 6;
    private static final int BARN_TILES_W = 4;
    private static final int BARN_TILES_H = 3;
    private static final int COOP_TILES_W = 3;
    private static final int COOP_TILES_H = 3;
    private static final int GOLD_CLOCK_TILES_W = 3;
    private static final int GOLD_CLOCK_TILES_H = 5;

    // Tree rendering size multiplier
    private static final float TREE_SIZE_MULTIPLIER = 2f;

    public WorldController(PlayerController playerController, Farm farm, OrthographicCamera camera , Skin skin , GameMenuController controller) {
        this.playerController = playerController;
        this.farm = farm;
        this.camera = camera;
        this.skin = skin;
        this.textureCache = new HashMap<>();
        this.renderedBuildings = new HashSet<>();
        this.controller = controller;

        // Initialize building anchor collections
        this.greenhouseAnchors = new ArrayList<>();
        this.houseAnchors = new ArrayList<>();
        this.barnAnchors = new ArrayList<>();
        this.coopAnchors = new ArrayList<>();
        this.goldClockAnchors = new ArrayList<>();
        this.npcHouseAnchors = new ArrayList<>();

        // Initialize markets anchor collections
        this.blacksmith = new ArrayList<>();
        this.jojaMart = new ArrayList<>();
        this.pierreGeneralStore = new ArrayList<>();
        this.carpentersShop = new ArrayList<>();
        this.fishShop = new ArrayList<>();
        this.marnieShop = new ArrayList<>();
        this.starDropSaloon = new ArrayList<>();


        // Pre-load all textures
        preloadTextures();
    }

    private void preloadTextures() {
        Gdx.app.log("WorldController", "Starting to preload textures...");

        // Pre-load grass textures for all seasons
        String[] seasons = {"Spring", "Summer", "Fall", "Winter"};
        for (String season : seasons) {
            String grassPath = "content/grass/" + season + ".png";
            if (loadTexture("grass_" + season.toLowerCase(), grassPath)) {
                Gdx.app.log("WorldController", "Loaded grass texture for " + season);
            }
        }

        // Pre-load tile textures
        loadTexture("lake", "content/flooring/lake.png");
        loadTexture("stone", "content/Resource/Stone.png");
        loadTexture("iron_ore", "content/Crafting/Iron_Ore.png");
        loadTexture("gold_ore", "content/Crafting/Gold_Ore.png");
        loadTexture("plowed", "content/map_elements/shoveled.png");
        loadTexture("path", "content/path.png");
        loadTexture("shipping_bin", "content/Buildings/Shipping_Bin.png");

        // Building textures (larger sprites)
        loadTexture("barn", "content/buildings/barn.png");
        loadTexture("coop", "content/buildings/Coop.png");
        loadTexture("house", "content/buildings/house.png");

        // Greenhouse textures
        loadTexture("greenhouse", "content/Buildings/GreenHouse/UnConstructed.png");
        loadTexture("constructed_greenhouse", "content/Buildings/GreenHouse/Constructed.png");


        // Markets textures
        loadTexture("blacksmith", "content/map_elements/Blacksmith.png");
        loadTexture("joja_mart", "content/map_elements/Jojamart.png");
        loadTexture("pierre_store" , "content/map_elements/Pierres_shop.png");
        loadTexture("carpenters_shop" , "content/map_elements/Carpenter's_Shop.png");
        loadTexture("fish_shop" , "content/map_elements/Fish_Shop.png");
        loadTexture("marnie_shop" , "content/map_elements/Ranch.png");
        loadTexture("stardrop_saloon" , "content/map_elements/Saloon.png");

        // Clock texture
        loadTexture("gold_clock", "content/Buildings/Gold_Clock.png");

        loadTexture("fence", "content/Fence/Iron_Fence.png");
        preloadArtisans();
        preloadCrafting();
        preloadCooking();
        preloadPlants();
        preloadTrees();
        preloadCrops();
        preloadMinerals();

        loadTexture("branch", "content/Crafting/Stone.png");
        loadTexture("quarry", "content/Crafting/Stone.png");

        Gdx.app.log("WorldController", "Finished preloading textures. Cache size: " + textureCache.size());
    }


    public void preloadCrafting(){
        for(CraftingType craftingType : CraftingType.values()) {
            String key = craftingType.getImageFilepath();
            String path = "content/CraftingItems/" + key + ".png";
            loadTexture(key , path);
        }
    }

    public void preloadArtisans(){
        for(ArtisanType artisanType : ArtisanType.values()) {
            String key = artisanType.getImageFilepath();
            String path = "content/ArtisanItems/" + key + ".png";
            loadTexture(key , path);
        }
    }


    public void preloadCooking(){
        for(CookingType cookingType : CookingType.values()) {
            String key = cookingType.getImageFilepath();
            String path = "content/CookingItems/" + key + ".png";
            loadTexture(key , path);
        }
    }

    public void preloadSeeds(){

    }

    public void preloadPlants(){
        for(PlantType plantType : PlantType.values()) {
            int stage = plantType.getStage().length;
            for(int i = 1 ; i < stage + 1; i++) {
                String key = plantType.getImageFilePath() + "_" + i + ".png";
                String plantPath = "content/Plants/" + plantType.getImageFilePath() + "_" + "Stage_" + i + ".png";
                loadTexture(key, plantPath);
            }
            if(plantType.isGiantable()){
                String giantKey = plantType.getImageFilePath() + "_Giant";
                String giantPath = "content/Plants/Giant_" + plantType.getImageFilePath() + ".png";
                loadTexture(giantKey , giantPath);
            }
        }
    }

    public void preloadTrees(){
        for(TreeType treeType : TreeType.values()) {
            for(int i = 1 ; i < 5 ; i++){
                String key = treeType.getImageFilePath() + "_" + i;
                String treePath = "content/Trees/" + treeType.getImageFilePath() + "_" + "Stage_" + i + ".png";
                loadTexture(key , treePath);
            }
            if(treeType.getSeasons().length == 4){
                String keySeasons = treeType.getImageFilePath() + "_" + 5;
                String treePath = "content/Trees/" + treeType.getImageFilePath() + "_" + "Stage_" + 5 + ".png";
                loadTexture(keySeasons , treePath);
            }
        }
    }

    public void preloadCrops() {
        for(CropType cropType : CropType.values()) {
            String key = cropType.getImageFilePath();
            String cropPath = "content/Crops/" + cropType.getImageFilePath() + ".png";
            loadTexture(key, cropPath);
        }
    }

    public void preloadMinerals(){
        for(MineralType mineralType : MineralType.values()) {
            String key = mineralType.getImageFilepath();
            String mineralPath = "content/Minerals/" + key + "_Ore.png";
            loadTexture(key , mineralPath);
        }
    }

    private boolean loadTexture(String key, String path) {
        try {
            Texture texture = new Texture(path);
            textureCache.put(key, texture);
            Gdx.app.log("WorldController", "Successfully loaded: " + path);
            return true;
        } catch (Exception e) {
            Gdx.app.error("WorldController", "Failed to load texture: " + path + " - " + e.getMessage());
            return false;
        }
    }

    private Texture getTexture(String key) {
        return textureCache.get(key);
    }

    public void update() {
        // Check if full map is visible - if so, don't update camera position
        // The camera position is managed by the full map view
        boolean isFullMapVisible = false;
        try {
            // Try to get the full map visibility state from GameView
            if (controller != null && controller.getView() != null) {
                // Use reflection to access the private field
                java.lang.reflect.Field field = controller.getView().getClass().getDeclaredField("isFullMapVisible");
                field.setAccessible(true);
                isFullMapVisible = (Boolean) field.get(controller.getView());
            }
        } catch (Exception e) {
            // If we can't access the field, assume full map is not visible
            isFullMapVisible = false;
        }

        // Only update camera position if full map is not visible
        if (!isFullMapVisible) {
            float playerX = playerController.getPlayer().getPosX();
            float playerY = playerController.getPlayer().getPosY();

            float mapWidth, mapHeight;
            float mapOffsetX, mapOffsetY;

            if (playerController.getPlayer().getIsInVillage()) {
                // For village, use global village bounds
                mapWidth = Village.width * TILE_SIZE;
                mapHeight = Village.height * TILE_SIZE;
                mapOffsetX = GameMap.VILLAGE_X * TILE_SIZE;
                mapOffsetY = GameMap.VILLAGE_Y * TILE_SIZE;
            } else {
                // For farm, use farm bounds
                mapWidth = Farm.width * TILE_SIZE;
                mapHeight = Farm.height * TILE_SIZE;
                mapOffsetX = 0;
                mapOffsetY = 0;
            }

            float halfCameraViewWidth = camera.viewportWidth * camera.zoom / 2;
            float halfCameraViewHeight = camera.viewportHeight * camera.zoom / 2;

            float cameraX = playerX;
            float minCameraX = mapOffsetX + halfCameraViewWidth;
            float maxCameraX = mapOffsetX + mapWidth - halfCameraViewWidth;
            cameraX = Math.max(minCameraX, Math.min(cameraX, maxCameraX));

            float cameraY = playerY;
            float minCameraY = mapOffsetY + halfCameraViewHeight;
            float maxCameraY = mapOffsetY + mapHeight - halfCameraViewHeight;
            cameraY = Math.max(minCameraY, Math.min(cameraY, maxCameraY));

            camera.position.set(cameraX, cameraY, 0);
            camera.update();
        }

        Main.getBatch().setProjectionMatrix(camera.combined);

        renderedBuildings.clear();
        greenhouseAnchors.clear();
        houseAnchors.clear();
        barnAnchors.clear();
        coopAnchors.clear();
        goldClockAnchors.clear();
        npcHouseAnchors.clear();

        blacksmith.clear();
        jojaMart.clear();
        pierreGeneralStore.clear();
        carpentersShop.clear();
        fishShop.clear();
        marnieShop.clear();
        starDropSaloon.clear();


        if (playerController.getPlayer().getIsInVillage()) {
            renderVillageTiles();
        } else {
            renderFarmTiles();
        }
        renderMarkets();
        renderBuildings();

        // Render all players in the game, not just the current player
        renderAllPlayers();
    }

    private void renderFarmTiles() {
        if (farm == null) {
            Gdx.app.error("WorldController", "Farm is null!");
            return;
        }

        String currentSeason = getCurrentSeason();

        Set<String> greenhouseTiles = new HashSet<>();
        Set<String> houseTiles = new HashSet<>();
        Set<String> barnTiles = new HashSet<>();
        Set<String> coopTiles = new HashSet<>();

        collectBuildingTiles(greenhouseTiles, houseTiles, barnTiles, coopTiles);

        for (int x = 0; x < Farm.width; x++) {
            for (int y = 0; y < Farm.height; y++) {
                Location location = farm.getItem(x, y);
                if (location == null) continue;

                float worldX = x * TILE_SIZE;
                float worldY = y * TILE_SIZE;

                TileType tileType = location.getTile();

                // Draw grass first
                if (shouldRenderGrass(tileType)) {
                    Texture grassTexture = getTexture("grass_" + currentSeason);
                    if (grassTexture != null) {
                        Main.getBatch().draw(grassTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
                    }
                }

                // Render tree item if present
                Item item = location.getItem();
                if (item instanceof Tree) {
                    renderItemOnTile(x, y, item, currentSeason);
                }

                // Then draw tile-specific texture (like lake, stone, etc.)
                Texture tileTexture = getTileSpecificTexture(tileType, currentSeason);
                if (tileTexture != null) {
                    Main.getBatch().draw(tileTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
                }

                // Detect anchor for building
                detectBuildingAnchors(location, x, y, tileType, greenhouseTiles, houseTiles, barnTiles, coopTiles);

                // Render non-large building items
                if (!isLargeBuilding(tileType)) {
                    if (!(item instanceof Tree) && item != null) {
                        renderItemOnTile(x, y, item, currentSeason);
                    }
                }
            }
        }
    }

    private void renderVillageTiles() {
        Village village = App.getGame().getGameMap().getVillage();
        if (village == null) {
            Gdx.app.error("WorldController", "Village is null!");
            return;
        }


        String currentSeason = getCurrentSeason();

        Set<String> blackSmithTiles = new HashSet<>();
        Set<String> jojaMartTiles = new HashSet<>();
        Set<String> pierreGeneralStoreTiles = new HashSet<>();
        Set<String> carpentersTiles = new HashSet<>();
        Set<String> fishShopTiles = new HashSet<>();
        Set<String> marnieShopTiles = new HashSet<>();
        Set<String> starDropSaloonTiles = new HashSet<>();
        Set<String> goldClockTiles = new HashSet<>();
        Set<String> npcHouseTiles = new HashSet<>();


        collectMarketTiles(blackSmithTiles , jojaMartTiles , pierreGeneralStoreTiles ,carpentersTiles , fishShopTiles , marnieShopTiles , starDropSaloonTiles);
        collectClockTiles(goldClockTiles);
        collectNPCHouseTiles(npcHouseTiles);



        for (int x = 0; x < Village.width; x++) {
            for (int y = 0; y < Village.height; y++) {
                Location location = village.getItem(x, y);
                if (location == null) continue;

                // Use global coordinates for village tiles
                float worldX = (GameMap.VILLAGE_X + x) * TILE_SIZE;
                float worldY = (GameMap.VILLAGE_Y + y) * TILE_SIZE;

                TileType tileType = location.getTile();

                // Draw grass first
                if (shouldRenderGrass(tileType)) {
                    Texture grassTexture = getTexture("grass_" + currentSeason);
                    if (grassTexture != null) {
                        Main.getBatch().draw(grassTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
                    }
                }

                // Then draw tile-specific texture (like path, building, etc.)
                Texture tileTexture = getTileSpecificTexture(tileType, currentSeason);
                if (tileTexture != null) {
                    Main.getBatch().draw(tileTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
                }

                detectMarketAnchors(location , x , y ,tileType ,  blackSmithTiles , jojaMartTiles , pierreGeneralStoreTiles ,carpentersTiles , fishShopTiles , marnieShopTiles , starDropSaloonTiles);
                detectClockAnchors(location, x, y, goldClockTiles);
                detectNPCHouseAnchors(location, x, y, npcHouseTiles);

                // Render items if present
                Item item = location.getItem();
                if (item != null) {
                    renderVillageItemOnTile(x, y, item, currentSeason);
                }
            }
        }
    }

    private void renderVillageItemOnTile(int x, int y, Item item, String season) {
        // Use global coordinates for village items
        float worldX = (GameMap.VILLAGE_X + x) * TILE_SIZE;
        float worldY = (GameMap.VILLAGE_Y + y) * TILE_SIZE;

        if (item instanceof Tree tree) {
            renderTreeItem(worldX, worldY, season , tree);
        } else if (item instanceof Crop crop) {
            renderCropItem(worldX, worldY , crop);
        } else if (item instanceof Plant plant) {
            renderPlantItem(worldX, worldY , plant);
        } else if (item instanceof Mineral mineral) {
            renderMineralItem(worldX, worldY , mineral);
        } else if (item instanceof ShippingBin) {
            renderShippingBinItem(worldX, worldY);
        }
    }

    private String getCurrentSeason() {
        try {
            return App.getGame().getDate().getSeason().toString().toLowerCase();
        } catch (Exception e) {
            Gdx.app.error("WorldController", "Failed to get season, using spring as default");
            return "spring";
        }
    }

    private void collectBuildingTiles(Set<String> greenhouseTiles, Set<String> houseTiles,
                                      Set<String> barnTiles, Set<String> coopTiles) {
        for (int x = 0; x < Farm.width; x++) {
            for (int y = 0; y < Farm.height; y++) {
                Location location = farm.getItem(x, y);
                if (location != null) {
                    TileType tileType = location.getTile();
                    String tileKey = x + "," + y;

                    switch (tileType) {
                        case GREENHOUSE:
                        case CONSTRUCTED_GREENHOUSE:
                            greenhouseTiles.add(tileKey);
                            break;
                        case BUILDING:
                            houseTiles.add(tileKey);
                            break;
                        case BARN:
                            barnTiles.add(tileKey);
                            break;
                        case COOP:
                            coopTiles.add(tileKey);
                            break;
                    }
                }
            }
        }
    }

    private void collectMarketTiles(Set<String> blackSmith , Set<String> jojaMart , Set<String> pierreGeneralStore , Set<String> carpentersTiles , Set<String> fishShopTiles , Set<String> marnieShopTiles , Set<String> starDropSaloonTiles ) {
        Village village = App.getGame().getGameMap().getVillage();
        for (int x = 0; x < Village.width; x++) {
            for (int y = 0; y < Village.height; y++) {
                Location location = village.getItem(x, y);
                if (location != null) {
                    TileType tileType = location.getTile();
                    String tileKey = x + "," + y;

                    switch (tileType) {
                        case BlackSmith:
                            blackSmith.add(tileKey);
                            break;
                        case JojaMart:
                            jojaMart.add(tileKey);
                            break;
                        case PIERRE_GENERAL_STORE:
                            pierreGeneralStore.add(tileKey);
                            break;
                        case CARPENTERS_SHOP :
                            carpentersTiles.add(tileKey);
                            break;
                        case FISH_SHOP :
                            fishShopTiles.add(tileKey);
                            break;
                        case MARNIE_SHOP:
                            marnieShopTiles.add(tileKey);
                            break;
                        case STARDROP_SALOON :
                            starDropSaloonTiles.add(tileKey);
                            break;
                    }
                }
            }
        }
    }

    private void collectClockTiles(Set<String> goldClockTiles) {
        Village village = App.getGame().getGameMap().getVillage();
        for (int x = 0; x < Village.width; x++) {
            for (int y = 0; y < Village.height; y++) {
                Location location = village.getItem(x, y);
                if (location != null && location.getType() != null && location.getType().equals("gold_clock")) {
                    String tileKey = x + "," + y;
                    goldClockTiles.add(tileKey);
                }
            }
        }
    }

    private void collectNPCHouseTiles(Set<String> npcHouseTiles) {
        Village village = App.getGame().getGameMap().getVillage();
        for (int x = 0; x < Village.width; x++) {
            for (int y = 0; y < Village.height; y++) {
                Location location = village.getItem(x, y);
                if (location != null && location.getType() != null && location.getType().equals("npc_house")) {
                    String tileKey = x + "," + y;
                    npcHouseTiles.add(tileKey);
                }
            }
        }
    }

    private void detectBuildingAnchors(Location location, int x, int y, TileType tileType,
                                       Set<String> greenhouseTiles, Set<String> houseTiles,
                                       Set<String> barnTiles, Set<String> coopTiles) {
        switch (tileType) {
            case GREENHOUSE:
            case CONSTRUCTED_GREENHOUSE:
                detectGreenhouseAnchor(location, x, y, greenhouseTiles);
                break;
            case BUILDING:
                detectHouseAnchor(location, x, y, houseTiles);
                break;
            case BARN:
                detectBarnAnchor(location, x, y, barnTiles);
                break;
            case COOP:
                detectCoopAnchor(location, x, y, coopTiles);
                break;
        }
    }

    private void detectMarketAnchors(Location location, int x, int y, TileType tileType , Set<String> blackSmith , Set<String> jojaMart , Set<String> pierreGeneralStore , Set<String> carpentersTiles , Set<String> fishShopTiles , Set<String> marnieShopTiles , Set<String> starDropSaloonTiles) {
        switch (tileType) {
            case BlackSmith:
                detectBlackSmith(location, x, y, blackSmith);
                break;
            case JojaMart:
                detectJojaMart(location, x, y, jojaMart);
                break;
            case PIERRE_GENERAL_STORE:
                detectPierre(location, x, y, pierreGeneralStore);
                break;
            case CARPENTERS_SHOP:
                detectCarpenters(location , x , y , carpentersTiles);
                break;
            case FISH_SHOP:
                detectFishShop(location, x, y, fishShopTiles);
                break;
            case MARNIE_SHOP:
                detectMarnieShop(location, x, y, marnieShopTiles);
                break;
            case STARDROP_SALOON:
                detectStarDropSaloon(location, x, y, starDropSaloonTiles);
                break;
        }
    }

    private void detectClockAnchors(Location location, int x, int y, Set<String> goldClockTiles) {
        String tileKey = x + "," + y;
        if (goldClockTiles.contains(tileKey)) {
            detectGoldClockAnchor(location, x, y, goldClockTiles);
        }
    }

    private void detectNPCHouseAnchors(Location location, int x, int y, Set<String> npcHouseTiles) {
        String tileKey = x + "," + y;
        if (npcHouseTiles.contains(tileKey)) {
            detectNPCHouseAnchor(location, x, y, npcHouseTiles);
        }
    }

    private boolean shouldRenderGrass(TileType tileType) {
        return tileType != TileType.LAKE && tileType != TileType.WATER;
    }

    private Texture getTileSpecificTexture(TileType tileType, String season) {
        if (tileType == null) return null;

        switch (tileType) {
            case LAKE:
                return getTexture("lake");
            case WATER:
                return getTexture("lake");
            case STONE:
                return getTexture("stone");
            case IRON_ORE:
                return getTexture("iron_ore");
            case GOLD_ORE:
                return getTexture("gold_ore");
            case IRIDIUM_STONE:
                return getTexture("iridium_stone");
            case JEWEL_STONE:
                return getTexture("jewel_stone");
            case GOLD_STONE:
                return getTexture("gold_stone");
            case TREE:
                return getTexture("tree_" + season);
            case CROP:
                return getTexture("crop");
            case PLOWED:
                return getTexture("plowed");
            case PATH:
                return getTexture("path");
            case SHIPPING_BIN:
                return getTexture("shipping_bin");
            case BRANCH:
                return getTexture("branch");
            case QUARRY:
                return getTexture("quarry");
            case FENCE:
                return getTexture("fence");
            case GREENHOUSE:
            case CONSTRUCTED_GREENHOUSE:
            case BUILDING:
            case BARN:
            case COOP:
                // For building tiles, don't render any tile-specific texture
                // The building sprite will be rendered separately
                return null;
            default:
                return null;
        }
    }

    private void detectGreenhouseAnchor(Location location, int x, int y, Set<String> greenhouseTiles) {
        boolean hasLeft = greenhouseTiles.contains((x - 1) + "," + y);
        boolean hasBelow = greenhouseTiles.contains(x + "," + (y - 1));

        if (!hasLeft && !hasBelow) {
            greenhouseAnchors.add(location);
        }
    }

    private void detectHouseAnchor(Location location, int x, int y, Set<String> houseTiles) {
        boolean hasLeft = houseTiles.contains((x - 1) + "," + y);
        boolean hasBelow = houseTiles.contains(x + "," + (y - 1)); // Changed from hasAbove to hasBelow for bottom-left anchor

        if (!hasLeft && !hasBelow) {
            houseAnchors.add(location);
        }
    }

    private void detectBarnAnchor(Location location, int x, int y, Set<String> barnTiles) {
        boolean hasLeft = barnTiles.contains((x - 1) + "," + y);
        boolean hasBelow = barnTiles.contains(x + "," + (y - 1));

        if (!hasLeft && !hasBelow) {
            barnAnchors.add(location);
        }
    }

    private void detectCoopAnchor(Location location, int x, int y, Set<String> coopTiles) {
        boolean hasLeft = coopTiles.contains((x - 1) + "," + y);
        boolean hasBelow = coopTiles.contains(x + "," + (y - 1));

        if (!hasLeft && !hasBelow) {
            coopAnchors.add(location);
        }
    }
    private void detectBlackSmith(Location location , int x, int y, Set<String> marketTiles) {
        boolean hasLeft = marketTiles.contains((x - 1) + "," + y);
        boolean hasBelow = marketTiles.contains(x + "," + (y - 1));

        if (!hasLeft && !hasBelow) {
            blacksmith.add(location);
        }
    }

    private void detectJojaMart(Location location , int x, int y, Set<String> marketTiles) {
        boolean hasLeft = marketTiles.contains((x - 1) + "," + y);
        boolean hasBelow = marketTiles.contains(x + "," + (y - 1));

        if (!hasLeft && !hasBelow) {
            jojaMart.add(location);
        }
    }

    private void detectPierre(Location location , int x, int y, Set<String> marketTiles) {
        boolean hasLeft = marketTiles.contains((x - 1) + "," + y);
        boolean hasBelow = marketTiles.contains(x + "," + (y - 1));

        if (!hasLeft && !hasBelow) {
            pierreGeneralStore.add(location);
        }
    }


    private void detectCarpenters(Location location , int x, int y, Set<String> marketTiles) {
        boolean hasLeft = marketTiles.contains((x - 1) + "," + y);
        boolean hasBelow = marketTiles.contains(x + "," + (y - 1));

        if (!hasLeft && !hasBelow) {
            carpentersShop.add(location);
        }
    }

    private void detectFishShop(Location location , int x, int y, Set<String> marketTiles) {
        boolean hasLeft = marketTiles.contains((x - 1) + "," + y);
        boolean hasBelow = marketTiles.contains(x + "," + (y - 1));

        if (!hasLeft && !hasBelow) {
            fishShop.add(location);
        }
    }

    private void detectMarnieShop(Location location , int x, int y, Set<String> marketTiles) {
        boolean hasLeft = marketTiles.contains((x - 1) + "," + y);
        boolean hasBelow = marketTiles.contains(x + "," + (y - 1));

        if (!hasLeft && !hasBelow) {
            marnieShop.add(location);
        }
    }

    private void detectStarDropSaloon(Location location , int x, int y, Set<String> marketTiles) {
        boolean hasLeft = marketTiles.contains((x - 1) + "," + y);
        boolean hasBelow = marketTiles.contains(x + "," + (y - 1));

        if (!hasLeft && !hasBelow) {
            starDropSaloon.add(location);
        }
    }

    private void detectGoldClockAnchor(Location location, int x, int y, Set<String> goldClockTiles) {
        // For 3x3 clock, detect the top-left corner
        boolean hasLeft = goldClockTiles.contains((x - 1) + "," + y);
        boolean hasBelow = goldClockTiles.contains(x + "," + (y - 1));

        if (!hasLeft && !hasBelow) {
            goldClockAnchors.add(location);
        }
    }

    private void detectNPCHouseAnchor(Location location, int x, int y, Set<String> npcHouseTiles) {
        // For 5x5 NPC houses, detect the top-left corner
        boolean hasLeft = npcHouseTiles.contains((x - 1) + "," + y);
        boolean hasBelow = npcHouseTiles.contains(x + "," + (y - 1));

        if (!hasLeft && !hasBelow) {
            npcHouseAnchors.add(location);
        }
    }

    private void renderBuildings() {
        for (Location anchor : greenhouseAnchors) {
            renderGreenhouseAtAnchor(anchor);
        }

        for (Location anchor : houseAnchors) {
            renderHouseAtAnchor(anchor);
        }

        for (Location anchor : barnAnchors) {
            renderBarnAtAnchor(anchor);
        }

        for (Location anchor : coopAnchors) {
            renderCoopAtAnchor(anchor);
        }

        for (Location anchor : goldClockAnchors) {
            renderGoldClockAtAnchor(anchor);
        }

        for (Location anchor : npcHouseAnchors) {
            renderNPCHouseAtAnchor(anchor);
        }
    }

    private void renderMarkets() {
        if (playerController.getPlayer().getIsInVillage()) {
            // Use global coordinates for village markets
            for(Location location : blacksmith){
                renderVillageBlackSmithAtAnchor(location);
            }

            for(Location location : jojaMart){
                renderVillageJojaMartAtAnchor(location);
            }

            for(Location location : pierreGeneralStore){
                renderVillagePierreAtAnchor(location);
            }

            for(Location market : carpentersShop) {
                renderVillageCarpentersShopAtAnchor(market);
            }

            for(Location location : fishShop){
                renderVillageFishShopAtAnchor(location);
            }

            for(Location location : marnieShop){
                renderVillageMarnieShopAtAnchor(location);
            }

            for (Location location : starDropSaloon) {
                renderVillageStarDropSaloonAtAnchor(location);
            }
        } else {
            // Use local coordinates for farm markets
            for(Location location : blacksmith){
                renderBlackSmithAtAnchor(location);
            }

            for(Location location : jojaMart){
                renderJojaMartAtAnchor(location);
            }

            for(Location location : pierreGeneralStore){
                renderPierreAtAnchor(location);
            }

            for(Location market : carpentersShop) {
                renderCarpentersShopAtAnchor(market);
            }

            for(Location location : fishShop){
                renderFishShopAtAnchor(location);
            }

            for(Location location : marnieShop){
                renderMarnieShopAtAnchor(location);
            }

            for (Location location : starDropSaloon) {
                renderStarDropSaloonAtAnchor(location);
            }
        }
    }

    private void renderGreenhouseAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        float drawX = x * TILE_SIZE;
        float drawY = y * TILE_SIZE;

        TileType tileType = anchor.getTile();
        Texture texture = (tileType == TileType.CONSTRUCTED_GREENHOUSE) ?
            getTexture("constructed_greenhouse") : getTexture("greenhouse");

        if (texture != null) {
            Main.getBatch().draw(texture, drawX, drawY,
                TILE_SIZE * GREENHOUSE_TILES_W, TILE_SIZE * GREENHOUSE_TILES_H);
        }
    }

    private void renderHouseAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        float drawX = x * TILE_SIZE;
        float drawY = y * TILE_SIZE;

        Texture texture = getTexture("house");
        if (texture != null) {
            Main.getBatch().draw(texture, drawX, drawY,
                TILE_SIZE * HOUSE_TILES_W, TILE_SIZE * HOUSE_TILES_H);
        }
    }

    private void renderBarnAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();


        float drawX = x * TILE_SIZE;
        float drawY = y * TILE_SIZE;

        Texture texture = getTexture("barn");
        if (texture != null) {
            Main.getBatch().draw(texture, drawX, drawY,
                TILE_SIZE * BARN_TILES_W, TILE_SIZE * BARN_TILES_H);
        }
    }

    private void renderCoopAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        float drawX = x * TILE_SIZE;
        float drawY = y * TILE_SIZE;

        Texture texture = getTexture("coop");
        if (texture != null) {
            Main.getBatch().draw(texture, drawX, drawY,
                TILE_SIZE * COOP_TILES_W, TILE_SIZE * COOP_TILES_H);
        }
    }

    private void renderGoldClockAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        // Use global coordinates for village clock
        float drawX = (GameMap.VILLAGE_X + x) * TILE_SIZE;
        float drawY = (GameMap.VILLAGE_Y + y) * TILE_SIZE;

        Texture texture = getTexture("gold_clock");
        if (texture != null) {
            Main.getBatch().draw(texture, drawX, drawY,
                TILE_SIZE * GOLD_CLOCK_TILES_W, TILE_SIZE * GOLD_CLOCK_TILES_H);
        }
    }

    private void renderNPCHouseAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        // Use global coordinates for village NPC houses
        float drawX = (GameMap.VILLAGE_X + x) * TILE_SIZE;
        float drawY = (GameMap.VILLAGE_Y + y) * TILE_SIZE;

        // Find the building at this location to get the sprite path
        Village village = App.getGame().getGameMap().getVillage();
        Building building = null;
        for (Building b : village.getBuildings()) {
            if (b.getType().equals("npc_house") && b.contains(x, y)) {
                building = b;
                break;
            }
        }

        if (building != null && building.getSpritePath() != null) {
            // Load texture from the sprite path
            Texture texture = new Texture(building.getSpritePath());
            if (texture != null) {
                Main.getBatch().draw(texture, drawX, drawY,
                    TILE_SIZE * HOUSE_TILES_W, TILE_SIZE * HOUSE_TILES_H);
            }
        } else {
            // Fallback to a default house texture
            Texture texture = getTexture("house");
            if (texture != null) {
                Main.getBatch().draw(texture, drawX, drawY,
                    TILE_SIZE * HOUSE_TILES_W, TILE_SIZE * HOUSE_TILES_H);
            }
        }
    }
    private void renderBlackSmithAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        float drawX = x * TILE_SIZE;
        float drawY = y * TILE_SIZE;

        Texture texture = getTexture("blacksmith");
        if (texture != null) {
            Main.getBatch().draw(texture , drawX , drawY
                , TILE_SIZE * HOUSE_TILES_W, TILE_SIZE * HOUSE_TILES_H);
        }
    }

    private void renderJojaMartAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        float drawX = x * TILE_SIZE;
        float drawY = y * TILE_SIZE;

        Texture texture = getTexture("joja_mart");
        if (texture != null) {
            Main.getBatch().draw(texture , drawX , drawY
                , TILE_SIZE * HOUSE_TILES_W, TILE_SIZE * HOUSE_TILES_H);
        }
    }

    private void renderPierreAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        float drawX = x * TILE_SIZE;
        float drawY = y * TILE_SIZE;

        Texture texture = getTexture("pierre_store");
        if (texture != null) {
            Main.getBatch().draw(texture , drawX , drawY
                , TILE_SIZE * HOUSE_TILES_W, TILE_SIZE * HOUSE_TILES_H);
        }
    }

    private void renderCarpentersShopAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        float drawX = x * TILE_SIZE;
        float drawY = y * TILE_SIZE;

        Texture texture = getTexture("carpenters_shop");
        if (texture != null) {
            Main.getBatch().draw(texture , drawX , drawY
                , TILE_SIZE * HOUSE_TILES_W, TILE_SIZE * HOUSE_TILES_H);
        }
    }

    private void renderFishShopAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        float drawX = x * TILE_SIZE;
        float drawY = y * TILE_SIZE;

        Texture texture = getTexture("fish_shop");
        if (texture != null) {
            Main.getBatch().draw(texture , drawX , drawY
                , TILE_SIZE * HOUSE_TILES_W, TILE_SIZE * HOUSE_TILES_H);
        }
    }

    private void renderMarnieShopAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        float drawX = x * TILE_SIZE;
        float drawY = y * TILE_SIZE;

        Texture texture = getTexture("marnie_shop");
        if (texture != null) {
            Main.getBatch().draw(texture , drawX , drawY
                , TILE_SIZE * HOUSE_TILES_W, TILE_SIZE * HOUSE_TILES_H);
        }
    }

    private void renderStarDropSaloonAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        float drawX = x * TILE_SIZE;
        float drawY = y * TILE_SIZE;

        Texture texture = getTexture("stardrop_saloon");
        if (texture != null) {
            Main.getBatch().draw(texture , drawX , drawY
                , TILE_SIZE * HOUSE_TILES_W, TILE_SIZE * HOUSE_TILES_H);
        }
    }

    // Village market rendering methods with global coordinates
    private void renderVillageBlackSmithAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        float drawX = (GameMap.VILLAGE_X + x) * TILE_SIZE;
        float drawY = (GameMap.VILLAGE_Y + y) * TILE_SIZE;

        Texture texture = getTexture("blacksmith");
        if (texture != null) {
            Main.getBatch().draw(texture , drawX , drawY
                , TILE_SIZE * HOUSE_TILES_W, TILE_SIZE * HOUSE_TILES_H);
        }
    }

    private void renderVillageJojaMartAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        float drawX = (GameMap.VILLAGE_X + x) * TILE_SIZE;
        float drawY = (GameMap.VILLAGE_Y + y) * TILE_SIZE;

        Texture texture = getTexture("joja_mart");
        if (texture != null) {
            Main.getBatch().draw(texture , drawX , drawY
                , TILE_SIZE * HOUSE_TILES_W, TILE_SIZE * HOUSE_TILES_H);
        }
    }

    private void renderVillagePierreAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        float drawX = (GameMap.VILLAGE_X + x) * TILE_SIZE;
        float drawY = (GameMap.VILLAGE_Y + y) * TILE_SIZE;

        Texture texture = getTexture("pierre_store");
        if (texture != null) {
            Main.getBatch().draw(texture , drawX , drawY
                , TILE_SIZE * HOUSE_TILES_W, TILE_SIZE * HOUSE_TILES_H);
        }
    }

    private void renderVillageCarpentersShopAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        float drawX = (GameMap.VILLAGE_X + x) * TILE_SIZE;
        float drawY = (GameMap.VILLAGE_Y + y) * TILE_SIZE;

        Texture texture = getTexture("carpenters_shop");
        if (texture != null) {
            Main.getBatch().draw(texture , drawX , drawY
                , TILE_SIZE * HOUSE_TILES_W, TILE_SIZE * HOUSE_TILES_H);
        }
    }

    private void renderVillageFishShopAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        float drawX = (GameMap.VILLAGE_X + x) * TILE_SIZE;
        float drawY = (GameMap.VILLAGE_Y + y) * TILE_SIZE;

        Texture texture = getTexture("fish_shop");
        if (texture != null) {
            Main.getBatch().draw(texture , drawX , drawY
                , TILE_SIZE * HOUSE_TILES_W, TILE_SIZE * HOUSE_TILES_H);
        }
    }

    private void renderVillageMarnieShopAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        float drawX = (GameMap.VILLAGE_X + x) * TILE_SIZE;
        float drawY = (GameMap.VILLAGE_Y + y) * TILE_SIZE;

        Texture texture = getTexture("marnie_shop");
        if (texture != null) {
            Main.getBatch().draw(texture , drawX , drawY
                , TILE_SIZE * HOUSE_TILES_W, TILE_SIZE * HOUSE_TILES_H);
        }
    }

    private void renderVillageStarDropSaloonAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        float drawX = (GameMap.VILLAGE_X + x) * TILE_SIZE;
        float drawY = (GameMap.VILLAGE_Y + y) * TILE_SIZE;

        Texture texture = getTexture("stardrop_saloon");
        if (texture != null) {
            Main.getBatch().draw(texture , drawX , drawY
                , TILE_SIZE * HOUSE_TILES_W, TILE_SIZE * HOUSE_TILES_H);
        }
    }

    private boolean isLargeBuilding(TileType tileType) {
        return tileType == TileType.BUILDING || tileType == TileType.BARN ||
            tileType == TileType.COOP || tileType == TileType.GREENHOUSE ||
            tileType == TileType.CONSTRUCTED_GREENHOUSE;
    }

    private void renderItemOnTile(int x, int y, Item item, String season) {
        float worldX = x * TILE_SIZE;
        float worldY = y * TILE_SIZE;

        if (item instanceof Tree tree) {
            renderTreeItem(worldX, worldY, season , tree);
        } else if (item instanceof Crop crop) {
            renderCropItem(worldX, worldY , crop);
        } else if (item instanceof Plant plant) {
            renderPlantItem(worldX, worldY , plant);
        } else if (item instanceof Mineral mineral) {
            renderMineralItem(worldX, worldY , mineral);
        } else if (item instanceof ShippingBin) {
            renderShippingBinItem(worldX, worldY);
        }
    }


    private void renderTreeItem(float worldX, float worldY, String season, Tree tree) {
        int stage = tree.getStage() + 1; // Stage number for file name (1-5)

        if (tree.getSeasons().length == 4 && stage == 5) {
            String key = tree.getImageFilepath() + "_" + stage; // e.g., "Mahogany_5"
            Texture fullTexture = getTexture(key);

            if (fullTexture != null) {

                final int FRAME_WIDTH = 105;
                final int FRAME_HEIGHT = 182;

                Seasons currentSeason = App.getGame().getDate().getSeason();
                int frameX = 0;

                switch (currentSeason) {
                    case SUMMER:
                        frameX = FRAME_WIDTH;
                        break;
                    case AUTUMN:
                        frameX = FRAME_WIDTH * 2;
                        break;
                    case WINTER:
                        frameX = FRAME_WIDTH * 3;
                        break;
                    case SPRING:
                    default:
                        frameX = 0;
                        break;
                }


                TextureRegion seasonalFrame = new TextureRegion(fullTexture, frameX, 0, FRAME_WIDTH, FRAME_HEIGHT);


                float treeSize = TILE_SIZE * TREE_SIZE_MULTIPLIER;
                float offsetX = (TILE_SIZE - treeSize) / 2;
                float offsetY = (TILE_SIZE - treeSize) / 2;
                Main.getBatch().draw(seasonalFrame, worldX + offsetX, worldY + offsetY, treeSize, treeSize);
            }
        } else {
            // Default rendering for all other trees and stages
            String key = tree.getImageFilepath() + "_" + stage;
            Texture treeTexture = getTexture(key);
            if (treeTexture != null) {
                float treeSize = TILE_SIZE * TREE_SIZE_MULTIPLIER;
                float offsetX = (TILE_SIZE - treeSize) / 2;
                float offsetY = (TILE_SIZE - treeSize) / 2;
                Main.getBatch().draw(treeTexture, worldX + offsetX, worldY + offsetY, treeSize, treeSize);
            }
        }
    }

    private void renderCropItem(float worldX, float worldY , Crop crop) {
        String key = crop.getImageFilepath();
        Texture cropTexture = getTexture(key);
        if (cropTexture != null) {
            Main.getBatch().draw(cropTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
        }
    }

    private void renderPlantItem(float worldX, float worldY , Plant plant) {
        String key;
        if(plant.getIsGiant()){
            key = plant.getImageFilepath() + "_Giant";
        }else{
            key = plant.getImageFilepath() + "_" + plant.getStage() + ".png";
        }
        Texture plantTexture = getTexture(key);
        if (plantTexture != null) {
            Main.getBatch().draw(plantTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
        }
    }

    private void renderMineralItem(float worldX, float worldY , Mineral mineral) {
        String key = mineral.getImageFilepath();
        Texture mineralTexture = getTexture(key);
        if (mineralTexture != null) {
            Main.getBatch().draw(mineralTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
        }
    }

    private void renderShippingBinItem(float worldX, float worldY) {
        Texture binTexture = getTexture("shipping_bin");
        if (binTexture != null) {
            Main.getBatch().draw(binTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
        }
    }

    public void dispose() {
        for (Texture texture : textureCache.values()) {
            texture.dispose();
        }
        textureCache.clear();
    }


    private void renderAllPlayers() {
        Game game = App.getGame();
        if (game == null || game.getPlayers() == null) {
            return;
        }

        // Check if full map is visible by checking the map visibility state
        boolean isFullMapVisible = false;
        if (controller != null && controller.getView() != null) {
            try {
                // Try to access the isMapVisible field from GameView
                java.lang.reflect.Field field = controller.getView().getClass().getDeclaredField("isMapVisible");
                field.setAccessible(true);
                isFullMapVisible = (Boolean) field.get(controller.getView());
            } catch (Exception e) {
                // If we can't access the field, assume full map is not visible
                isFullMapVisible = false;
            }
        }

        for (Player player : game.getPlayers()) {
            if (player != null) {
                if (isFullMapVisible) {
                    renderPlayerOnMinimap(player);
                } else {
                    // Normal rendering when minimap is not visible
                    // Only render other players (not the current player, as they're rendered separately)
                    if (player != playerController.getPlayer() && player.getIsInVillage() && playerController.getPlayer().getIsInVillage()) {
                        renderPlayerSprite(player);
                    }
                }
            }
        }
    }

    private void renderPlayerOnMinimap(Player player) {
        if (player.getCurrentFarm() == null) {
            return;
        }

        Farm farm = player.getCurrentFarm();
        int farmIndex = farm.getFarmIndex();

        // Calculate farm position on minimap (same as in GameView.renderMinimapTiles)
        float farmX, farmY;
        float scale = 0.5f; // Scale factor for minimap

        switch (farmIndex) {
            case 0: // Top-Left
                farmX = 120;
                farmY = 120; // Above village
                break;
            case 1: // Bottom-Left
                farmX = 120;
                farmY = 120 + 234 * scale; // Below village
                break;
            case 2: // Top-Right
                farmX = 120 + 78 * scale;
                farmY = 120; // Above village
                break;
            case 3: // Bottom-Right
                farmX = 120 + 78 * scale;
                farmY = 120 + 234 * scale; // Below village
                break;
            default:
                return;
        }

        // Calculate player's position within their farm
        float playerLocalX = player.getPosX() / TILE_SIZE; // Convert to tile coordinates
        float playerLocalY = player.getPosY() / TILE_SIZE;

        // Calculate player's position on the minimap
        float playerMinimapX = farmX + (playerLocalX * TILE_SIZE * scale);
        float playerMinimapY = farmY + (playerLocalY * TILE_SIZE * scale);

        // Store the original position to restore later
        float originalX = player.getPosX();
        float originalY = player.getPosY();

        // Temporarily update the player position for minimap rendering
        player.setPosX(playerMinimapX);
        player.setPosY(playerMinimapY);

        // Render the player sprite at the calculated minimap position
        renderPlayerSprite(player);

        // Restore the original position
        player.setPosX(originalX);
        player.setPosY(originalY);
    }

    private void renderPlayerSprite(Player player) {
        final int RENDER_W = 48;
        final int RENDER_H = 72;

        // Load individual sprite file for player
        Texture playerTexture;
        try {
            // Use the first frame of the down animation as the default sprite
            playerTexture = new Texture(Gdx.files.internal("sprites/player/down_1.png"));
        } catch (Exception e) {
            // Fallback to colored dot if sprite can't be loaded
            boolean isCurrentPlayer = (player == playerController.getPlayer());
            Main.getBatch().setColor(isCurrentPlayer ? Color.RED : Color.BLUE);
            Texture whiteTexture = new Texture("content/grass/spring.png");
            float dotSize = 30;
            Main.getBatch().draw(whiteTexture, player.getPosX() - dotSize/2, player.getPosY() - dotSize/2, dotSize, dotSize);
            whiteTexture.dispose();
            return;
        }

        // Determine if this is the current player
        boolean isCurrentPlayer = (player == playerController.getPlayer());

        // Set color based on whether it's the current player or not
        if (isCurrentPlayer) {
            Main.getBatch().setColor(Color.WHITE); // Current player gets normal colors
        } else {
            Main.getBatch().setColor(0.7f, 0.7f, 0.7f, 1f); // Other players get slightly dimmed
        }

        // Draw the player sprite
        Main.getBatch().draw(playerTexture, player.getPosX() - RENDER_W/2, player.getPosY() - RENDER_H/2, RENDER_W, RENDER_H);

        // Dispose the texture to prevent memory leaks
        playerTexture.dispose();
    }


    public void handleInput() {
        // 1. Check if the screen was just clicked or touched.
        if (Gdx.input.justTouched()) {

            // 2. Get the click coordinates in screen space.
            Vector3 touchPoint = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);

            // 3. Convert the screen coordinates to your game's world coordinates.
            camera.unproject(touchPoint);

            // Check if click was on another player
            Player clickedPlayer = checkPlayerClick(touchPoint);
            if (clickedPlayer != null && !clickedPlayer.equals(playerController.getPlayer())) {
                showFriendInteractionWindow(clickedPlayer);
                return;
            }

            // 4. Check if the click was on a house.
            // We loop through the anchors we found during rendering.
            if (playerController.getPlayer().getIsInVillage()) {
                Market[] markets = App.getGame().getGameMap().getVillage().getMarkets();
                if (checkVillageClicked(blacksmith, HOUSE_TILES_W, HOUSE_TILES_H, touchPoint)) {
                    Main.getGame().setScreen(new MarketMenuScreen(markets[0], playerController.getPlayer(), skin, controller.getView(), App.getGame().getDate().getSeason()));
                } else if (checkVillageClicked(jojaMart, HOUSE_TILES_W, HOUSE_TILES_H, touchPoint)) {
                    Main.getGame().setScreen(new MarketMenuScreen(markets[1], playerController.getPlayer(), skin, controller.getView(), App.getGame().getDate().getSeason()));
                } else if (checkVillageClicked(pierreGeneralStore, HOUSE_TILES_W, HOUSE_TILES_H, touchPoint)) {
                    Main.getGame().setScreen(new MarketMenuScreen(markets[2], playerController.getPlayer(), skin, controller.getView(), App.getGame().getDate().getSeason()));
                } else if (checkVillageClicked(carpentersShop, HOUSE_TILES_W, HOUSE_TILES_H, touchPoint)) {
                    Main.getGame().setScreen(new MarketMenuScreen(markets[3], playerController.getPlayer(), skin, controller.getView(), App.getGame().getDate().getSeason()));
                } else if (checkVillageClicked(fishShop, HOUSE_TILES_W, HOUSE_TILES_H, touchPoint)) {
                    Main.getGame().setScreen(new MarketMenuScreen(markets[4], playerController.getPlayer(), skin, controller.getView(), App.getGame().getDate().getSeason()));
                } else if (checkVillageClicked(marnieShop, HOUSE_TILES_W, HOUSE_TILES_H, touchPoint)) {
                    Main.getGame().setScreen(new MarketMenuScreen(markets[5], playerController.getPlayer(), skin, controller.getView(), App.getGame().getDate().getSeason()));
                } else if (checkVillageClicked(starDropSaloon, HOUSE_TILES_W, HOUSE_TILES_H, touchPoint)) {
                    Main.getGame().setScreen(new MarketMenuScreen(markets[6], playerController.getPlayer(), skin, controller.getView(), App.getGame().getDate().getSeason()));
                }
            } else {
                if (checkClicked(houseAnchors, HOUSE_TILES_W, HOUSE_TILES_H, touchPoint)) {
                    System.out.println("house clicked");
                    Main.getGame().setScreen(new RefrigeratorScreen(playerController.getPlayer(), skin, controller.getView()));
                } else if (checkClicked(coopAnchors, COOP_TILES_W, COOP_TILES_H, touchPoint)) {

                } else if (checkClicked(barnAnchors, BARN_TILES_W, BARN_TILES_H, touchPoint)) {

                } else if (checkClicked(greenhouseAnchors, GREENHOUSE_TILES_W, GREENHOUSE_TILES_H, touchPoint)) {
                    boolean isConstructed = farm.getGreenHouse().getIsConstructed();

                    if (isConstructed) {
                        Gdx.app.log("WorldController", "Entering constructed greenhouse.");
                        Main.getGame().setScreen(new GreenhouseScreen(playerController, farm.getGreenHouse(), this));
                    } else {
                        GreenhouseRepairDialog repairDialog = new GreenhouseRepairDialog(
                            playerController.getPlayer(), controller, skin);
                        repairDialog.show(controller.getView().getStage());
                    }
                }
            }
        }
    }

    public boolean checkClicked(List<Location> anchors , int tilesW , int tilesH , Vector3 touchPoint) {
        for (Location anchor : anchors) {
            float houseX = anchor.getX() * TILE_SIZE;
            float houseY = anchor.getY() * TILE_SIZE;
            float houseW = tilesW * TILE_SIZE;
            float houseH = tilesH * TILE_SIZE;

            // Create a rectangle representing the house's bounds.
            Rectangle houseRectangle = new Rectangle(houseX, houseY, houseW, houseH);

            if (houseRectangle.contains(touchPoint.x, touchPoint.y)) {

                Gdx.app.log("CLICKED", "You clicked the house at tile: " + anchor.getX() + ", " + anchor.getY());
                return true;
            }
        }
        return false;
    }

    public boolean checkVillageClicked(List<Location> anchors , int tilesW , int tilesH , Vector3 touchPoint) {
        for (Location anchor : anchors) {
            float houseX = (GameMap.VILLAGE_X + anchor.getX()) * TILE_SIZE;
            float houseY = (GameMap.VILLAGE_Y + anchor.getY()) * TILE_SIZE;
            float houseW = tilesW * TILE_SIZE;
            float houseH = tilesH * TILE_SIZE;

            Rectangle houseRectangle = new Rectangle(houseX, houseY, houseW, houseH);

            if (houseRectangle.contains(touchPoint.x, touchPoint.y)) {

                Gdx.app.log("CLICKED", "You clicked the village market at tile: " + anchor.getX() + ", " + anchor.getY());

                return true;
            }
        }
        return false;
    }

    //TODO  : temporarily for green house:

    public Map<String, Texture> getTextureCache() {
        return textureCache;
    }


    public void updatePlayerController() {
        Player currentPlayer = App.getGame().getCurrentPlayer();
        System.out.println("DEBUG: updatePlayerController called for player: " + (currentPlayer != null ? currentPlayer.getUser().getUsername() : "null"));

        if (currentPlayer != null && playerController != null) {
            // Create a new PlayerController for the current player
            Farm currentFarm = App.getGame().getGameMap().getFarmByPlayer(currentPlayer);
            System.out.println("DEBUG: Found farm for player: " + (currentFarm != null ? currentFarm.getName() + " (index: " + currentFarm.getFarmIndex() + ")" : "null"));

            if (currentFarm != null) {
                // Update the farm reference to the current player's farm
                this.farm = currentFarm;

                // Create new PlayerController for the current player
                playerController = new PlayerController(currentPlayer, currentFarm, skin);
                System.out.println("PlayerController updated to follow: " + currentPlayer.getUser().getUsername());
                System.out.println("Farm updated to: " + currentFarm.getName());

                // Force camera to update to the new player's position
                float playerX = currentPlayer.getPosX();
                float playerY = currentPlayer.getPosY();

                float mapWidth, mapHeight;
                float mapOffsetX, mapOffsetY;

                if (currentPlayer.getIsInVillage()) {
                    // For village, use global village bounds
                    mapWidth = Village.width * TILE_SIZE;
                    mapHeight = Village.height * TILE_SIZE;
                    mapOffsetX = GameMap.VILLAGE_X * TILE_SIZE;
                    mapOffsetY = GameMap.VILLAGE_Y * TILE_SIZE;
                } else {
                    // For farm, use global farm bounds based on farm index
                    Farm playerFarm = currentPlayer.getCurrentFarm();
                    if (playerFarm != null) {
                        int farmIndex = playerFarm.getFarmIndex();
                        switch (farmIndex) {
                            case 0: // Top-Left
                                mapOffsetX = 0;
                                mapOffsetY = 0;
                                break;
                            case 1: // Bottom-Left
                                mapOffsetX = 0;
                                mapOffsetY = 78 * TILE_SIZE;
                                break;
                            case 2: // Top-Right
                                mapOffsetX = 156 * TILE_SIZE;
                                mapOffsetY = 0;
                                break;
                            case 3: // Bottom-Right
                                mapOffsetX = 156 * TILE_SIZE;
                                mapOffsetY = 78 * TILE_SIZE;
                                break;
                            default:
                                mapOffsetX = 0;
                                mapOffsetY = 0;
                                break;
                        }
                    } else {
                        mapOffsetX = 0;
                        mapOffsetY = 0;
                    }
                    mapWidth = Farm.width * TILE_SIZE;
                    mapHeight = Farm.height * TILE_SIZE;
                }

                float halfCameraViewWidth = camera.viewportWidth * camera.zoom / 2;
                float halfCameraViewHeight = camera.viewportHeight * camera.zoom / 2;

                float cameraX = playerX;
                float minCameraX = mapOffsetX + halfCameraViewWidth;
                float maxCameraX = mapOffsetX + mapWidth - halfCameraViewWidth;
                cameraX = Math.max(minCameraX, Math.min(cameraX, maxCameraX));

                float cameraY = playerY;
                float minCameraY = mapOffsetY + halfCameraViewHeight;
                float maxCameraY = mapOffsetY + mapHeight - halfCameraViewHeight;
                cameraY = Math.max(minCameraY, Math.min(cameraY, maxCameraY));

                camera.position.set(cameraX, cameraY, 0);
                camera.update();

                System.out.println("Camera updated to follow player at: " + playerX + ", " + playerY);
                System.out.println("DEBUG: Camera position set to: (" + cameraX + ", " + cameraY + ")");
                System.out.println("DEBUG: Map bounds - width: " + mapWidth + ", height: " + mapHeight + ", offset: (" + mapOffsetX + ", " + mapOffsetY + ")");
            }
        }
    }

    private Player checkPlayerClick(Vector3 touchPoint) {
        Game game = App.getGame();
        if (game == null || game.getPlayers() == null) {
            return null;
        }

        for (Player player : game.getPlayers()) {
            if (player != null && !player.equals(playerController.getPlayer())) {
                // Check if click is within player bounds
                float playerX = player.getPosX();
                float playerY = player.getPosY();
                float playerWidth = 48; // Player render width
                float playerHeight = 96; // Player render height

                if (touchPoint.x >= playerX && touchPoint.x <= playerX + playerWidth &&
                    touchPoint.y >= playerY && touchPoint.y <= playerY + playerHeight) {
                    return player;
                }
            }
        }
        return null;
    }

    private void showFriendInteractionWindow(Player targetPlayer) {
        System.out.println("🤝 Opening friend interaction window for " + targetPlayer.getUser().getUsername());
        try {
            org.example.client.views.FriendInteractionWindow interactionWindow =
                new org.example.client.views.FriendInteractionWindow(
                    playerController.getPlayer(),
                    targetPlayer,
                    skin,
                    controller.getView()
                );
            Main.getGame().setScreen(interactionWindow);
        } catch (Exception e) {
            System.err.println("Error opening friend interaction window: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
