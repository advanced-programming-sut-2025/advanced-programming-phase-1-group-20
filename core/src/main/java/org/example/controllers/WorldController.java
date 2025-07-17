package org.example.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.example.Main;
import org.example.models.MapDetails.Farm;
import org.example.models.App;
import org.example.models.enums.Types.CropType;
import org.example.models.enums.Types.TileType;
import org.example.models.common.Location;
import org.example.models.Items.*;
import org.example.models.enums.Types.TreeType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class WorldController {
    private PlayerController playerController;
    private Farm farm;
    private OrthographicCamera camera;

    private Map<String, Texture> textureCache;
    private Set<String> renderedBuildings;

    // Building anchor collections
    private List<Location> greenhouseAnchors;
    private List<Location> houseAnchors;
    private List<Location> barnAnchors;
    private List<Location> coopAnchors;

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

    // Tree rendering size multiplier
    private static final float TREE_SIZE_MULTIPLIER = 2f;

    public WorldController(PlayerController playerController, Farm farm, OrthographicCamera camera) {
        this.playerController = playerController;
        this.farm = farm;
        this.camera = camera;
        this.textureCache = new HashMap<>();
        this.renderedBuildings = new HashSet<>();

        // Initialize building anchor collections
        this.greenhouseAnchors = new ArrayList<>();
        this.houseAnchors = new ArrayList<>();
        this.barnAnchors = new ArrayList<>();
        this.coopAnchors = new ArrayList<>();

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
        loadTexture("stone", "content/Crafting/Stone.png");
        loadTexture("iron_ore", "content/Crafting/Iron_Ore.png");
        loadTexture("gold_ore", "content/Crafting/Gold_Ore.png");
        loadTexture("plowed", "content/plowed.png");
        loadTexture("path", "content/path.png");
        loadTexture("shipping_bin", "content/Buildings/Shipping_Bin.png");

        // Building textures (larger sprites)
        loadTexture("barn", "content/buildings/barn.png");
        loadTexture("coop", "content/buildings/Coop.png");
        loadTexture("house", "content/buildings/house.png");

        // Greenhouse textures
        loadTexture("greenhouse", "content/Buildings/GreenHouse/UnConstructed.png");
        loadTexture("constructed_greenhouse", "content/Buildings/GreenHouse/Constructed.png");

        loadTexture("fence", "content/Fence/Iron_Fence.png");
        preloadTrees();
        preloadCrops();

        loadTexture("branch", "content/Crafting/Stone.png");
        loadTexture("quarry", "content/Crafting/Stone.png");

        Gdx.app.log("WorldController", "Finished preloading textures. Cache size: " + textureCache.size());
    }

    public void preloadTrees(){
        for(TreeType treeType : TreeType.values()) {
            for(int i = 1 ; i < 5 ; i++){
                String key = treeType.getImageFilePath() + "_" + i;
                String treePath = "content/Trees/" + treeType.getImageFilePath() + "_" + "Stage_" + i + ".png";
                loadTexture(key , treePath);
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
        float playerX = playerController.getPlayer().getPosX();
        float playerY = playerController.getPlayer().getPosY();

        float mapWidth = Farm.width * TILE_SIZE;
        float mapHeight = Farm.height * TILE_SIZE;


        float halfCameraViewWidth = camera.viewportWidth * camera.zoom / 2;
        float halfCameraViewHeight = camera.viewportHeight * camera.zoom / 2;

        float cameraX = playerX;
        float minCameraX = halfCameraViewWidth;
        float maxCameraX = mapWidth - halfCameraViewWidth;
        cameraX = Math.max(minCameraX, Math.min(cameraX, maxCameraX));


        float cameraY = playerY;
        float minCameraY = halfCameraViewHeight;
        float maxCameraY = mapHeight - halfCameraViewHeight;
        cameraY = Math.max(minCameraY, Math.min(cameraY, maxCameraY));

        camera.position.set(cameraX, cameraY, 0);
        camera.update();
        Main.getBatch().setProjectionMatrix(camera.combined);

        renderedBuildings.clear();
        greenhouseAnchors.clear();
        houseAnchors.clear();
        barnAnchors.clear();
        coopAnchors.clear();

        renderFarmTiles();
        renderBuildings();

        playerController.getPlayer().getPlayerSprite().draw(Main.getBatch());
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
        } else if (item instanceof Plant) {
            renderPlantItem(worldX, worldY);
        } else if (item instanceof Mineral) {
            renderMineralItem(worldX, worldY);
        } else if (item instanceof ShippingBin) {
            renderShippingBinItem(worldX, worldY);
        }
    }

    private void renderTreeItem(float worldX, float worldY, String season , Tree tree) {
        int stage = tree.getStage() + 1;
        String key = tree.getImageFilepath() + "_" + stage;
        Texture treeTexture = getTexture(key);
        if (treeTexture != null) {
            float treeSize = TILE_SIZE * TREE_SIZE_MULTIPLIER;
            float offsetX = (TILE_SIZE - treeSize) / 2; // Center the larger tree
            float offsetY = (TILE_SIZE - treeSize) / 2;

            Main.getBatch().draw(treeTexture, worldX + offsetX, worldY + offsetY, treeSize, treeSize);
        }
    }

    private void renderCropItem(float worldX, float worldY , Crop crop) {
        String key = crop.getImageFilepath();
        Texture cropTexture = getTexture(key);
        if (cropTexture != null) {
            Main.getBatch().draw(cropTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
        }
    }

    private void renderPlantItem(float worldX, float worldY) {
        Texture plantTexture = getTexture("crop");
        if (plantTexture != null) {
            Main.getBatch().draw(plantTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
        }
    }

    private void renderMineralItem(float worldX, float worldY) {
        Texture mineralTexture = getTexture("stone");
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
}
