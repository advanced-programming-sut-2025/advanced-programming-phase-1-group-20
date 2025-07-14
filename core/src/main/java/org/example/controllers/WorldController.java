package org.example.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.example.Main;
import org.example.models.MapDetails.Farm;
import org.example.models.App;
import org.example.models.enums.Types.TileType;
import org.example.models.common.Location;
import org.example.models.Items.*;

import java.util.HashMap;
import java.util.Map;

public class WorldController {
    private PlayerController playerController;
    private Farm farm;
    private OrthographicCamera camera;

    // Texture caching for performance
    private Map<String, Texture> textureCache;

    // Tile size in pixels (adjust based on your game's scale)
    private static final int TILE_SIZE = 60; // Changed to match your coordinate system better

    public WorldController(PlayerController playerController, Farm farm, OrthographicCamera camera) {
        this.playerController = playerController;
        this.farm = farm;
        this.camera = camera;
        this.textureCache = new HashMap<>();

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
        loadTexture("lake", "content/flooring/Flooring_26.png");
        loadTexture("stone", "content/Crafting/Stone.png");
        loadTexture("iron_ore", "content/Crafting/Iron_Ore.png");
        loadTexture("gold_ore", "content/Crafting/Gold_Ore.png");
        loadTexture("crop", "content/Crafting/Common_Mushroom.png");
        loadTexture("plowed", "content/plowed.png");
        loadTexture("path", "content/path.png");
        loadTexture("shipping_bin", "content/Buildings/Shipping_Bin.png");

        // Load building textures (these are larger sprites)
        loadTexture("barn", "content/buildings/barn.png");
        loadTexture("coop", "content/buildings/Coop.png");
        loadTexture("house", "content/buildings/house.png");

        // Load tree textures for all seasons
        for (String season : seasons) {
            String treePath = "content/TreeTile/" + season + ".png";
            if (loadTexture("tree_" + season.toLowerCase(), treePath)) {
                Gdx.app.log("WorldController", "Loaded tree texture for " + season);
            }
        }

        // Additional textures for missing tile types
        loadTexture("branch", "content/Crafting/Stone.png"); // Placeholder
        loadTexture("quarry", "content/Crafting/Stone.png"); // Placeholder
        loadTexture("greenhouse", "content/buildings/house.png"); // Placeholder
        loadTexture("constructed_greenhouse", "content/buildings/house.png"); // Placeholder

        Gdx.app.log("WorldController", "Finished preloading textures. Cache size: " + textureCache.size());
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
        camera.position.set(playerController.getPlayer().getPosX(), playerController.getPlayer().getPosY(), 0);
        camera.update();
        Main.getBatch().setProjectionMatrix(camera.combined);

        // Debug: Log that we're trying to render
        Gdx.app.log("WorldController", "Starting render cycle");

        // Render the map tile by tile instead of using farm.getBackgroundSprite()
        renderFarmTiles();

        // Render the player
        playerController.getPlayer().getPlayerSprite().draw(Main.getBatch());

        Gdx.app.log("WorldController", "Render cycle complete");
    }

    private void renderFarmTiles() {
        // Debug: Check if farm exists
        if (farm == null) {
            Gdx.app.error("WorldController", "Farm is null!");
            return;
        }

        // Get current season for seasonal textures
        String currentSeason;
        try {
            currentSeason = App.getGame().getDate().getSeason().toString().toLowerCase();
        } catch (Exception e) {
            Gdx.app.error("WorldController", "Failed to get season, using spring as default");
            currentSeason = "spring";
        }

        Gdx.app.log("WorldController", "Rendering farm tiles for season: " + currentSeason);
        Gdx.app.log("WorldController", "Camera position: (" + camera.position.x + "," + camera.position.y + ")");
        Gdx.app.log("WorldController", "Player position: (" + playerController.getPlayer().getPosX() + "," + playerController.getPlayer().getPosY() + ")");

        // SIMPLIFIED: Just render the whole farm for now (we'll optimize later)
        int tilesRendered = 0;

        // Render all tiles (we'll add frustum culling back later)
        for (int x = 0; x < Farm.width; x++) {
            for (int y = 0; y < Farm.height; y++) {
                renderTile(x, y, currentSeason);
                tilesRendered++;
            }
        }

        Gdx.app.log("WorldController", "Rendered " + tilesRendered + " tiles total");
    }

    private void renderTile(int x, int y, String season) {
        // Get the location data from farm
        Location location = farm.getItem(x, y);
        if (location == null) {
            if (x == 25 && y == 25) { // Only log for center tile to avoid spam
                Gdx.app.error("WorldController", "Location is null at center " + x + "," + y);
            }
            return;
        }

        // CENTER THE MAP: Offset world coordinates so map center aligns with player
        float playerX = playerController.getPlayer().getPosX();
        float playerY = playerController.getPlayer().getPosY();

        // Calculate offset so center of map (25,25) aligns with player position
        float mapCenterOffsetX = playerX - (25 * TILE_SIZE);
        float mapCenterOffsetY = playerY - (25 * TILE_SIZE);

        float worldX = mapCenterOffsetX + (x * TILE_SIZE);
        float worldY = mapCenterOffsetY + (y * TILE_SIZE);

        // Debug: Log center tile info only
        if (x == 25 && y == 25) {
            Gdx.app.log("WorldController", "Rendering center tile at world coords (" + worldX + "," + worldY + ") with tile type: " + location.getTile());
            Gdx.app.log("WorldController", "Map offset: (" + mapCenterOffsetX + "," + mapCenterOffsetY + ")");
        }

        // Layer 1: Render grass base layer (seasonal) for most tiles
        TileType tileType = location.getTile();
        if (shouldRenderGrass(tileType)) {
            Texture grassTexture = getTexture("grass_" + season);
            if (grassTexture != null) {
                Main.getBatch().draw(grassTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
                if (x == 25 && y == 25) {
                    Gdx.app.log("WorldController", "Drew grass texture at center");
                }
            } else {
                if (x == 25 && y == 25) {
                    Gdx.app.error("WorldController", "Grass texture not found for season: " + season);
                }
            }
        }

        // Layer 2: Render tile-specific texture based on tile type
        Texture tileTexture = getTileTexture(tileType, season, location);
        if (tileTexture != null) {
            // For large buildings, handle them specially
            if (isLargeBuilding(tileType)) {
                renderLargeBuildingTile(x, y, tileType, tileTexture);
            } else {
                Main.getBatch().draw(tileTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
                if (x == 25 && y == 25) {
                    Gdx.app.log("WorldController", "Drew tile texture for type: " + tileType);
                }
            }
        }

        // Layer 3: Render items on top of tiles if any
        Item item = location.getItem();
        if (item != null) {
            renderItemOnTile(x, y, item, season);
            if (x == 25 && y == 25) {
                Gdx.app.log("WorldController", "Drew item: " + item.getClass().getSimpleName());
            }
        }
    }

    private boolean shouldRenderGrass(TileType tileType) {
        // Grass should be rendered under most tiles except water/lake
        return tileType != TileType.LAKE && tileType != TileType.WATER;
    }

    private Texture getTileTexture(TileType tileType, String season, Location location) {
        if (tileType == null) return null;

        switch (tileType) {
            case LAKE:
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
            case GREENHOUSE:
                return getTexture("greenhouse");
            case CONSTRUCTED_GREENHOUSE:
                return getTexture("constructed_greenhouse");
            case BUILDING:
                return getTexture("house");
            case BARN:
                return getTexture("barn");
            case COOP:
                return getTexture("coop");
            default:
                return null; // Grass will show through
        }
    }

    private boolean isLargeBuilding(TileType tileType) {
        return tileType == TileType.BUILDING || tileType == TileType.BARN ||
            tileType == TileType.COOP || tileType == TileType.GREENHOUSE ||
            tileType == TileType.CONSTRUCTED_GREENHOUSE;
    }

    private void renderLargeBuildingTile(int x, int y, TileType tileType, Texture texture) {
        // For large buildings, we need to determine if this is the corner tile
        // and render the full building sprite from that corner

        float worldX = x * TILE_SIZE;
        float worldY = y * TILE_SIZE;

        // For now, render each tile individually
        // You might want to optimize this by tracking which building tiles have been rendered
        Main.getBatch().draw(texture, worldX, worldY, TILE_SIZE, TILE_SIZE);
    }

    private void renderItemOnTile(int x, int y, Item item, String season) {
        // CENTER THE MAP: Use same offset calculation as main tile rendering
        float playerX = playerController.getPlayer().getPosX();
        float playerY = playerController.getPlayer().getPosY();

        float mapCenterOffsetX = playerX - (25 * TILE_SIZE);
        float mapCenterOffsetY = playerY - (25 * TILE_SIZE);

        float worldX = mapCenterOffsetX + (x * TILE_SIZE);
        float worldY = mapCenterOffsetY + (y * TILE_SIZE);

        // Handle different item types
        if (item instanceof Tree) {
            Tree tree = (Tree) item;
            Texture treeTexture = getTexture("tree_" + season);
            if (treeTexture != null) {
                Main.getBatch().draw(treeTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
            }
        } else if (item instanceof Crop) {
            Crop crop = (Crop) item;
            Texture cropTexture = getTexture("crop");
            if (cropTexture != null) {
                Main.getBatch().draw(cropTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
            }
        } else if (item instanceof Plant) {
            Plant plant = (Plant) item;
            // You might want different textures for different plant types/stages
            Texture plantTexture = getTexture("crop"); // Using crop texture as placeholder
            if (plantTexture != null) {
                Main.getBatch().draw(plantTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
            }
        } else if (item instanceof Mineral) {
            Mineral mineral = (Mineral) item;
            // You might want different textures based on mineral type
            Texture mineralTexture = getTexture("stone");
            if (mineralTexture != null) {
                Main.getBatch().draw(mineralTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
            }
        } else if (item instanceof ShippingBin) {
            Texture binTexture = getTexture("shipping_bin");
            if (binTexture != null) {
                Main.getBatch().draw(binTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
            }
        }
        // Add more item types as needed
    }

    // Clean up resources
    public void dispose() {
        for (Texture texture : textureCache.values()) {
            texture.dispose();
        }
        textureCache.clear();
    }
}
