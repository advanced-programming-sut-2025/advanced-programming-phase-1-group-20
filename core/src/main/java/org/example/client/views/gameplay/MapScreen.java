package org.example.client.views.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.common.models.App;
import org.example.common.models.Player.Player;
import org.example.common.models.Items.Item;
import org.example.common.models.Items.Tree;
import org.example.common.models.Items.Crop;
import org.example.common.models.Items.Plant;
import org.example.common.models.Items.Mineral;
import org.example.common.models.Items.ShippingBin;
import org.example.common.models.MapDetails.Building;
import org.example.common.models.Barn;
import org.example.common.models.Coop;
import org.example.common.models.MapDetails.GreenHouse;
import org.example.common.models.MapDetails.Quarry;
import org.example.common.models.MapDetails.Lake;
import org.example.common.models.Market;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.MapDetails.Village;
import org.example.common.models.MapDetails.GameMap;
import org.example.common.models.common.Location;
import org.example.common.models.enums.Types.TileType;
import org.example.common.models.common.Date;
import org.example.common.models.entities.Game;
import org.example.common.models.enums.Seasons;
import org.example.utils.AssetManager;
import java.util.List;

public class MapScreen implements Screen, InputProcessor {
    private Stage stage;
    private Player player;
    private Skin skin;
    private Screen previousScreen;
    private Game game;

    // Camera for map view
    private OrthographicCamera camera;
    private SpriteBatch batch;

    // UI components
    private TextButton backButton;
    private Label titleLabel;
    private Label instructionsLabel;

    // Map rendering constants
    private static final int TILE_SIZE = 60;
    private static final float MAP_ZOOM = 12f; // Zoom out to show entire map

    // Nickname rendering
    private BitmapFont nicknameFont;
    private static final float NICKNAME_OFFSET_Y = 120f;
    private static final Color NICKNAME_TEXT_COLOR = Color.BLACK;

    public MapScreen(Player player, Skin skin, Screen previousScreen) {
        this.player = player;
        this.skin = skin;
        this.previousScreen = previousScreen;
        this.game = App.getGame();

        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = MAP_ZOOM;

        // Initialize batch
        batch = new SpriteBatch();

        // Initialize stage
        stage = new Stage(new ScreenViewport());

        // Initialize nickname font
        initializeNicknameFont();

        setupUI();
    }

    private void initializeNicknameFont() {
        nicknameFont = new BitmapFont();
        nicknameFont.getData().setScale(10.0f); // Make the font triple the size
        nicknameFont.setColor(NICKNAME_TEXT_COLOR);
    }

    private void setupUI() {
        // Create main table
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // Create title
        titleLabel = new Label("Game Map", skin);
        titleLabel.setFontScale(2.0f);
        titleLabel.setColor(Color.WHITE);

        // Create instructions
        instructionsLabel = new Label("Press ESC or click Back to return", skin);
        instructionsLabel.setColor(Color.WHITE);

        // Create back button
        backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getGame().setScreen(previousScreen);
            }
        });

        // Layout
        mainTable.top().left();
        mainTable.pad(20);
        mainTable.add(titleLabel).row();
        mainTable.add(instructionsLabel).padTop(10).row();
        mainTable.add(backButton).padTop(20).row();

        stage.addActor(mainTable);
    }

    @Override
    public void show() {
        // Set input processor to handle both stage and key events
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);  // Stage first (UI elements)
        multiplexer.addProcessor(this);   // MapScreen second (key events)
        Gdx.input.setInputProcessor(multiplexer);

        // Update the tiles array to ensure it's current
        GameMap gameMap = App.getGame().getGameMap();
        if (gameMap != null) {
            gameMap.updateTilesFromRegions();
        }

        // Center camera on the entire map
        if (gameMap != null) {
            float totalMapWidth = GameMap.TOTAL_WIDTH * TILE_SIZE;
            float totalMapHeight = GameMap.TOTAL_HEIGHT * TILE_SIZE;
            camera.position.set(totalMapWidth / 2, totalMapHeight / 2, 0);
            camera.update();
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.M)   {
            Main.getGame().setScreen(previousScreen);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public void render(float delta) {
        // Clear screen
        ScreenUtils.clear(0.2f, 0.4f, 0.2f, 1);

        // Update camera
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Begin rendering
        batch.begin();

        // Render the full map
        renderFullMap();

        batch.end();

        // Render UI
        stage.act(delta);
        stage.draw();
    }

    private void renderFullMap() {
        GameMap gameMap = App.getGame().getGameMap();
        if (gameMap == null) return;

        // Get the unified tiles array
        Location[][] tiles = gameMap.getTiles();
        if (tiles == null) return;

        String currentSeason = getCurrentSeason();

        int tilesRendered = 0;
        int nonNullTiles = 0;

        // Render all tiles from the unified array
        for (int x = 0; x < GameMap.TOTAL_WIDTH; x++) {
            for (int y = 0; y < GameMap.TOTAL_HEIGHT; y++) {
                Location location = tiles[x][y];
                if (location != null) {
                    nonNullTiles++;
                    float worldX = x * TILE_SIZE;
                    float worldY = y * TILE_SIZE;

                    TileType tileType = location.getTile();

                    // Draw grass first for appropriate tile types
                    if (shouldRenderGrass(tileType)) {
                        Texture grassTexture = AssetManager.getAssetManager().getTileTextureForType("grass", currentSeason);
                        if (grassTexture != null) {
                            batch.draw(grassTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
                            tilesRendered++;
                        }
                    }

                    // Draw tile-specific texture
                    Texture tileTexture = AssetManager.getAssetManager().getTileTextureForType(tileType.toString().toLowerCase(), currentSeason);
                    if (tileTexture != null) {
                        batch.draw(tileTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
                        tilesRendered++;
                    } else {
                        // Fallback to colored rectangle if texture not found
                        Color tileColor = getTileColor(tileType);
                        if (tileColor != null) {
                            batch.setColor(tileColor);
                            Texture whiteTexture = new Texture("content/grass/spring.png");
                            batch.draw(whiteTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
                            whiteTexture.dispose();
                            batch.setColor(Color.WHITE);
                            tilesRendered++;
                        }
                    }

                    // Render items on tiles
                    Item item = location.getItem();
                    if (item != null) {
                        renderItemOnTile(x, y, item, currentSeason);
                    }
                }
            }
        }

        renderBuildingsAndStructures();
        renderPlayersOnFullMap();
    }

    private void renderBuildingsAndStructures() {
        GameMap gameMap = App.getGame().getGameMap();
        if (gameMap == null) return;

        // Render buildings and structures for all farms
        for (Farm farm : gameMap.getFarms()) {
            if (farm == null) continue;

            // Render the main house
            Building house = farm.getBuilding();
            if (house != null) {
                renderBuilding(house, farm, TILE_SIZE);
            }

            // Render barns
            for (Barn barn : farm.getBarns()) {
                renderBarn(barn, farm, TILE_SIZE);
            }

            // Render coops
            for (Coop coop : farm.getCoops()) {
                renderCoop(coop, farm, TILE_SIZE);
            }

            // Render greenhouse
            GreenHouse greenhouse = farm.getGreenHouse();
            if (greenhouse != null) {
                renderGreenhouse(greenhouse, farm, TILE_SIZE);
            }

            // Render quarry
            Quarry quarry = farm.getQuarry();
            if (quarry != null) {
                renderQuarry(quarry, farm, TILE_SIZE);
            }

            // Render lakes
            for (Lake lake : farm.getLakes()) {
                renderLake(lake, farm, TILE_SIZE);
            }
        }

        // Render village buildings and markets
        Village village = gameMap.getVillage();
        if (village != null) {
            renderVillageBuildings(village, TILE_SIZE);
        }
    }

    private void renderVillageBuildings(Village village, int tileSize) {
        // Render markets
        Market[] markets = village.getMarkets();
        if (markets != null) {
            for (Market market : markets) {
                if (market != null) {
                    renderMarket(market, tileSize);
                }
            }
        }

        // Render other village buildings
        List<Building> buildings = village.getBuildings();
        if (buildings != null) {
            for (Building building : buildings) {
                if (building != null) {
                    renderVillageBuilding(building, tileSize);
                }
            }
        }
    }

    private void renderMarket(Market market, int tileSize) {
        String marketName = market.getName();
        String textureKey = getMarketTextureKey(marketName);
        Texture marketTexture = AssetManager.getAssetManager().getTileTexture(textureKey);

        if (marketTexture != null) {
            float worldX = (GameMap.VILLAGE_X + market.getX()) * tileSize;
            float worldY = (GameMap.VILLAGE_Y + market.getY()) * tileSize;
            batch.draw(marketTexture, worldX, worldY, tileSize * 2, tileSize * 2);
        }
    }

    private void renderVillageBuilding(Building building, int tileSize) {
        String buildingName = building.getName();
        String buildingType = building.getType();
        String textureKey = getVillageBuildingTextureKey(buildingName, buildingType);
        Texture buildingTexture = AssetManager.getAssetManager().getTileTexture(textureKey);

        if (buildingTexture != null) {
            float worldX = (GameMap.VILLAGE_X + building.getX()) * tileSize;
            float worldY = (GameMap.VILLAGE_Y + building.getY()) * tileSize;
            batch.draw(buildingTexture, worldX, worldY, tileSize * 2, tileSize * 2);
        }
    }

    private String getMarketTextureKey(String marketName) {
        return switch (marketName.toLowerCase()) {
            case "pierre's general store" -> "pierre_general_store";
            case "blacksmith" -> "blacksmith";
            case "carpenter's shop" -> "carpenters_shop";
            default -> "market";
        };
    }

    private String getVillageBuildingTextureKey(String buildingName, String buildingType) {
        return switch (buildingType.toLowerCase()) {
            case "house" -> "house";
            case "shop" -> "shop";
            case "public" -> "public_building";
            default -> "building";
        };
    }

    private void renderBuilding(Building building, Farm farm, int tileSize) {
        String buildingName = building.getName();
        String textureKey = "house"; // Default to house texture
        Texture buildingTexture = AssetManager.getAssetManager().getTileTexture(textureKey);

        if (buildingTexture != null) {
            int farmIndex = getFarmIndex(farm);
            float worldX = (getFarmStartX(farmIndex) + building.getX()) * tileSize;
            float worldY = (getFarmStartY(farmIndex) + building.getY()) * tileSize;
            batch.draw(buildingTexture, worldX, worldY, tileSize * 2, tileSize * 2);
        }
    }

    private void renderBarn(Barn barn, Farm farm, int tileSize) {
        String textureKey = "barn";
        Texture barnTexture = AssetManager.getAssetManager().getTileTexture(textureKey);

        if (barnTexture != null) {
            int farmIndex = getFarmIndex(farm);
            float worldX = (getFarmStartX(farmIndex) + barn.getX()) * tileSize;
            float worldY = (getFarmStartY(farmIndex) + barn.getY()) * tileSize;
            batch.draw(barnTexture, worldX, worldY, tileSize * 2, tileSize * 2);
        }
    }

    private void renderCoop(Coop coop, Farm farm, int tileSize) {
        String textureKey = "coop";
        Texture coopTexture = AssetManager.getAssetManager().getTileTexture(textureKey);

        if (coopTexture != null) {
            int farmIndex = getFarmIndex(farm);
            float worldX = (getFarmStartX(farmIndex) + coop.getX()) * tileSize;
            float worldY = (getFarmStartY(farmIndex) + coop.getY()) * tileSize;
            batch.draw(coopTexture, worldX, worldY, tileSize * 2, tileSize * 2);
        }
    }

    private void renderGreenhouse(GreenHouse greenhouse, Farm farm, int tileSize) {
        String textureKey = greenhouse.getIsConstructed() ? "greenhouse_constructed" : "greenhouse_unconstructed";
        Texture greenhouseTexture = AssetManager.getAssetManager().getTileTexture(textureKey);

        if (greenhouseTexture != null) {
            int farmIndex = getFarmIndex(farm);
            float worldX = (getFarmStartX(farmIndex) + greenhouse.getX()) * tileSize;
            float worldY = (getFarmStartY(farmIndex) + greenhouse.getY()) * tileSize;
            batch.draw(greenhouseTexture, worldX, worldY, tileSize * 2, tileSize * 2);
        }
    }

    private void renderQuarry(Quarry quarry, Farm farm, int tileSize) {
        String textureKey = "quarry";
        Texture quarryTexture = AssetManager.getAssetManager().getTileTexture(textureKey);

        if (quarryTexture != null) {
            int farmIndex = getFarmIndex(farm);
            float worldX = (getFarmStartX(farmIndex) + quarry.getX()) * tileSize;
            float worldY = (getFarmStartY(farmIndex) + quarry.getY()) * tileSize;
            batch.draw(quarryTexture, worldX, worldY, tileSize * 2, tileSize * 2);
        }
    }

    private void renderLake(Lake lake, Farm farm, int tileSize) {
        String textureKey = "lake";
        Texture lakeTexture = AssetManager.getAssetManager().getTileTexture(textureKey);

        if (lakeTexture != null) {
            int farmIndex = getFarmIndex(farm);
            float worldX = (getFarmStartX(farmIndex) + lake.getX()) * tileSize;
            float worldY = (getFarmStartY(farmIndex) + lake.getY()) * tileSize;
            batch.draw(lakeTexture, worldX, worldY, tileSize * 2, tileSize * 2);
        }
    }

    private int getFarmIndex(Farm farm) {
        GameMap gameMap = App.getGame().getGameMap();
        if (gameMap != null) {
            List<Farm> farms = gameMap.getFarms();
            for (int i = 0; i < farms.size(); i++) {
                if (farms.get(i) == farm) {
                    return i;
                }
            }
        }
        return 0;
    }

    private int getFarmStartX(int farmIndex) {
        return switch (farmIndex) {
            case 0 -> 0;      // Bottom-Left
            case 1 -> 0;      // Top-Left
            case 2 -> 156;     // Top-Right
            case 3 -> 156;     // Bottom-Right
            default -> 0;
        };
    }

    private int getFarmStartY(int farmIndex) {
        return switch (farmIndex) {
            case 0 -> 0;      // Bottom-Left
            case 1 -> 78;    // Top-Left
            case 2 -> 0;    // Top-Right
            case 3 -> 78;      // Bottom-Right
            default -> 0;
        };
    }

        private void renderPlayersOnFullMap() {
        GameMap gameMap = App.getGame().getGameMap();
        if (gameMap == null) return;



        // Render all players
        for (Player player : App.getGame().getPlayers()) {
            if (player == null) {
                continue;
            }

            // Get player's farm
            Farm playerFarm = player.getCurrentFarm();
            if (playerFarm == null) {
                continue;
            }

            // Get player's location and farm index
            Location playerLocation = player.getLocation();
            int farmIndex = getFarmIndex(playerFarm);

            if (player.getIsInVillage()) {
                float worldX = (GameMap.VILLAGE_X + playerLocation.getX()) * TILE_SIZE;
                float worldY = (GameMap.VILLAGE_Y + playerLocation.getY()) * TILE_SIZE;



                // Render player sprite using PlayerController-style gridding
                renderPlayerSprite(player, worldX, worldY);
                continue;
            }



            float worldX = (playerLocation.getX()) * TILE_SIZE;
            float worldY = (playerLocation.getY()) * TILE_SIZE;

            renderPlayerSprite(player, worldX, worldY);
        }

        // Reset color to white
        batch.setColor(Color.WHITE);
    }

    private void renderPlayerSprite(Player player, float worldX, float worldY) {
        // PlayerController constants for sprite rendering
        final int FRAME_W = 16;
        final int FRAME_H = 32;
        final int RENDER_W = 96;  // Doubled from 48
        final int RENDER_H = 192; // Doubled from 96

        // Get player's texture sheet
        Texture textureSheet = player.getTextureSheet();
        if (textureSheet == null) {
            // Fallback to colored dot
            Player currentPlayer = App.getGame().getCurrentPlayer();
            boolean isCurrentPlayer = (player == currentPlayer);
            batch.setColor(isCurrentPlayer ? Color.RED : Color.BLUE);
            Texture whiteTexture = new Texture("content/grass/spring.png");
            float dotSize = 30;
            batch.draw(whiteTexture, worldX - dotSize/2, worldY - dotSize/2, dotSize, dotSize);
            whiteTexture.dispose();
            return;
        }

        // Split the texture sheet into a grid (like PlayerController does)
        TextureRegion[][] grid = TextureRegion.split(textureSheet, FRAME_W, FRAME_H);

        TextureRegion playerFrame = grid[0][0];
        // Determine if this is the current player
        Player currentPlayer = App.getGame().getCurrentPlayer();
        boolean isCurrentPlayer = (player == currentPlayer);

        // Set color based on whether it's the current player or not
        if (isCurrentPlayer) {
            batch.setColor(Color.WHITE); // Current player gets normal colors
        } else {
            batch.setColor(0.7f, 0.7f, 0.7f, 1f); // Other players get slightly dimmed
        }

        // Draw the player sprite
        batch.draw(playerFrame, worldX - RENDER_W/2, worldY - RENDER_H/2, RENDER_W, RENDER_H);

        // Render nickname
        renderPlayerNickname(player, worldX, worldY);
    }

    private void renderPlayerNickname(Player player, float worldX, float worldY) {
        if (player == null || player.getUser() == null || nicknameFont == null) {
            return;
        }

        String nickname = player.getUser().getNickname();
        if (nickname == null || nickname.trim().isEmpty()) {
            nickname = player.getUser().getUsername(); // Fallback to username
        }

        if (nickname == null || nickname.trim().isEmpty()) {
            return; // No nickname to display
        }

        float nicknameWidth = nicknameFont.draw(batch, nickname, 0, 0).width;
        float nicknameX = worldX - (nicknameWidth / 2f); // Center above player
        float nicknameY = worldY + NICKNAME_OFFSET_Y;

        // Draw nickname text
        Color originalColor = batch.getColor().cpy();
        batch.setColor(NICKNAME_TEXT_COLOR);
        nicknameFont.draw(batch, nickname, nicknameX, nicknameY);

        // Reset batch color
        batch.setColor(originalColor);
    }

    private boolean shouldRenderGrass(TileType tileType) {
        return tileType == TileType.Dirt || tileType == TileType.PATH ||
            tileType == TileType.PLOWED || tileType == TileType.CROP;
    }

    private void renderItemOnTile(int x, int y, Item item, String season) {
        float worldX = x * TILE_SIZE;
        float worldY = y * TILE_SIZE;

        if (item instanceof Tree tree) {
            renderTreeItem(worldX, worldY, season, tree);
        } else if (item instanceof Crop crop) {
            renderCropItem(worldX, worldY, crop);
        } else if (item instanceof Plant) {
            renderPlantItem(worldX, worldY);
        } else if (item instanceof Mineral) {
            renderMineralItem(worldX, worldY);
        } else if (item instanceof ShippingBin) {
            renderShippingBinItem(worldX, worldY);
        }
    }

    private void renderTreeItem(float worldX, float worldY, String season, Tree tree) {
        int stage = tree.getStage() + 1;
        String key = tree.getImageFilepath() + "_" + stage;
        Texture treeTexture = AssetManager.getAssetManager().getTileTexture(key);
        if (treeTexture != null) {
            float treeSize = TILE_SIZE * 2f; // TILE_SIZE * TREE_SIZE_MULTIPLIER
            float offsetX = (TILE_SIZE - treeSize) / 2; // Center the larger tree
            float offsetY = (TILE_SIZE - treeSize) / 2;

            batch.draw(treeTexture, worldX + offsetX, worldY + offsetY, treeSize, treeSize);
        }
    }

    private void renderCropItem(float worldX, float worldY, Crop crop) {
        String key = crop.getImageFilepath();
        Texture cropTexture = AssetManager.getAssetManager().getTileTexture(key);
        if (cropTexture != null) {
            batch.draw(cropTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
        }
    }

    private void renderPlantItem(float worldX, float worldY) {
        Texture plantTexture = AssetManager.getAssetManager().getTileTexture("crop");
        if (plantTexture != null) {
            batch.draw(plantTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
        }
    }

    private void renderMineralItem(float worldX, float worldY) {
        Texture mineralTexture = AssetManager.getAssetManager().getTileTexture("stone");
        if (mineralTexture != null) {
            batch.draw(mineralTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
        }
    }

    private void renderShippingBinItem(float worldX, float worldY) {
        Texture binTexture = AssetManager.getAssetManager().getTileTexture("shipping_bin");
        if (binTexture != null) {
            batch.draw(binTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
        }
    }

    private Color getTileColor(TileType tileType) {
        switch (tileType) {
            case Dirt:
                return new Color(0.6f, 0.4f, 0.2f, 1f); // Brown
            case WATER:
                return new Color(0.2f, 0.4f, 0.8f, 1f); // Blue
            case STONE:
                return new Color(0.5f, 0.5f, 0.5f, 1f); // Gray
            case TREE:
                return new Color(0.4f, 0.3f, 0.2f, 1f); // Dark brown
            case VILLAGE:
                return new Color(0.7f, 0.5f, 0.3f, 1f); // Village brown
            case MARKET:
                return new Color(0.9f, 0.7f, 0.5f, 1f); // Market color
            case PATH:
                return new Color(0.8f, 0.6f, 0.4f, 1f); // Light brown
            case BUILDING:
                return new Color(0.8f, 0.6f, 0.4f, 1f); // Light brown
            case SAND:
                return new Color(0.9f, 0.8f, 0.6f, 1f); // Sand color
            case PLOWED:
                return new Color(0.5f, 0.3f, 0.1f, 1f); // Dark brown
            case CROP:
                return new Color(0.2f, 0.8f, 0.2f, 1f); // Green
            default:
                return new Color(0.3f, 0.3f, 0.3f, 1f); // Default gray
        }
    }

    private String getCurrentSeason() {
        Date gameDate = App.getGame().getCurrentDate();
        if (gameDate != null) {
            Seasons season = gameDate.getSeason();
            return season.toString().toLowerCase();
        }
        return "spring"; // Default to spring
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        camera.setToOrtho(false, width, height);
        camera.zoom = MAP_ZOOM;

        // Re-center camera after resize
        GameMap gameMap = game.getGameMap();
        if (gameMap != null) {
            float totalMapWidth = GameMap.TOTAL_WIDTH * TILE_SIZE;
            float totalMapHeight = GameMap.TOTAL_HEIGHT * TILE_SIZE;
            camera.position.set(totalMapWidth / 2, totalMapHeight / 2, 0);
        }
        camera.update();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        if (nicknameFont != null) {
            nicknameFont.dispose();
        }
    }

    // TODO: update whenever server sends a notification
    public void serverUpdate() {

    }
}
