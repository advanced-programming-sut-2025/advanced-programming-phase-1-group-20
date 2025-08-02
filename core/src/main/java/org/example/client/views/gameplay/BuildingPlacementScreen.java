package org.example.client.views.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.example.client.Main;
import org.example.common.models.Items.*;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Result;
import org.example.common.models.common.Location;
import org.example.common.models.enums.Types.TileType;
import org.example.common.models.Barn;
import org.example.common.models.Coop;
import org.example.common.models.MapDetails.Building;
import org.example.common.models.App;
import org.example.common.models.enums.Types.BarnTypes;
import org.example.common.models.enums.Types.Cages;

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

public class BuildingPlacementScreen implements Screen, Disposable {
    private Stage stage;
    private Skin skin;
    private Player player;
    private Farm farm;
    private Item buildingItem;
    private Screen previousScreen;

    // Rendering
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private Map<String, Texture> textureCache;

    // Building anchor collections (like WorldController)
    private List<Location> greenhouseAnchors;
    private List<Location> houseAnchors;
    private List<Location> barnAnchors;
    private List<Location> coopAnchors;

    // UI
    private Table uiTable;
    private Label instructionLabel;
    private TextButton cancelButton;
    private TextButton confirmButton;

    // Building placement state
    private boolean isPlacing = false;
    private int previewX = -1;
    private int previewY = -1;
    private boolean canPlace = false;

    // Constants
    private static final int TILE_SIZE = 30; // Smaller tiles to show full farm

    public BuildingPlacementScreen(Player player, Item buildingItem, Screen previousScreen, Skin skin) {
        this.player = player;
        this.buildingItem = buildingItem;
        this.previousScreen = previousScreen;
        this.skin = skin;
        this.farm = player.getCurrentFarm();

        // Initialize camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(1200, 800, camera); // Larger viewport to show full farm
        batch = new SpriteBatch();
        textureCache = new HashMap<>();

        // Initialize building anchor collections
        this.greenhouseAnchors = new ArrayList<>();
        this.houseAnchors = new ArrayList<>();
        this.barnAnchors = new ArrayList<>();
        this.coopAnchors = new ArrayList<>();

        // Preload textures
        preloadTextures();

        stage = new Stage(viewport);
    }

    private void preloadTextures() {
        // Load grass textures for all seasons
        loadTexture("grass_spring", "content/grass/spring.png");
        loadTexture("grass_summer", "content/grass/summer.png");
        loadTexture("grass_fall", "content/grass/fall.png");
        loadTexture("grass_winter", "content/grass/winter.png");

        // Load building textures
        loadTexture("barn", "content/Buildings/Barn.png");
        loadTexture("coop", "content/Buildings/Coop.png");
        loadTexture("house", "content/Buildings/House.png");
        loadTexture("greenhouse", "content/Buildings/GreenHouse/UnConstructed.png");
        loadTexture("constructed_greenhouse", "content/Buildings/GreenHouse/Constructed.png");

        // Load tree textures
        loadTexture("tree_spring", "content/Trees/Apple_Stage_1.png");
        loadTexture("tree_summer", "content/Trees/Apple_Stage_1.png");
        loadTexture("tree_fall", "content/Trees/Apple_Stage_1.png");
        loadTexture("tree_winter", "content/Trees/Apple_Stage_1.png");

        // Load crop textures
        loadTexture("crop_spring", "content/Crops/Wheat_Stage_1.png");
        loadTexture("crop_summer", "content/Crops/Wheat_Stage_1.png");
        loadTexture("crop_fall", "content/Crops/Wheat_Stage_1.png");
        loadTexture("crop_winter", "content/Crops/Wheat_Stage_1.png");

        // Load mineral textures
        loadTexture("stone", "content/Rock/mainStone.png");
        loadTexture("iron_ore", "content/Minerals/Iron_Ore.png");
        loadTexture("gold_ore", "content/Minerals/Gold_Ore.png");
        loadTexture("diamond_ore", "content/Minerals/Diamond_Ore.png");

        // Load building preview texture
        if (buildingItem.getImageFilepath() != null) {
            loadTexture("building_preview", buildingItem.getImageFilepath());
        }
    }

    private boolean loadTexture(String key, String path) {
        try {
            Texture texture = new Texture(path);
            textureCache.put(key, texture);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to load texture: " + path + " - " + e.getMessage());
            return false;
        }
    }

    private Texture getTexture(String key) {
        return textureCache.get(key);
    }

    private String getCurrentSeason() {
        return App.getGame().getDate().getSeason().name().toLowerCase();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                return stage.keyDown(keycode);
            }

            @Override
            public boolean keyUp(int keycode) {
                return stage.keyUp(keycode);
            }

            @Override
            public boolean keyTyped(char character) {
                return stage.keyTyped(character);
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Convert screen coordinates to world coordinates
                Vector3 worldCoords = viewport.unproject(new Vector3(screenX, screenY, 0));

                // Convert to tile coordinates
                int tileX = (int) (worldCoords.x / TILE_SIZE);
                int tileY = (int) (worldCoords.y / TILE_SIZE);

                // Check if click is within farm bounds
                if (tileX >= 0 && tileX < Farm.width && tileY >= 0 && tileY < Farm.height) {
                    previewX = tileX;
                    previewY = tileY;
                    isPlacing = true;

                    // Check if placement is valid based on building type
                    String buildingName = buildingItem.getName().toLowerCase();
                    int buildingWidth = 2;
                    int buildingHeight = 2;
                    if (buildingName.contains("barn")) {
                        buildingWidth = 4;
                        buildingHeight = 3;
                    } else if (buildingName.contains("coop")) {
                        buildingWidth = 3;
                        buildingHeight = 3;
                    }
                    canPlace = farm.canBuild(tileX, tileY, buildingWidth, buildingHeight);
                    confirmButton.setDisabled(!canPlace);

                    updateInstructionLabel();

                    return true;
                }

                return stage.touchDown(screenX, screenY, pointer, button);
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return stage.touchUp(screenX, screenY, pointer, button);
            }

            @Override
            public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
                return stage.touchCancelled(screenX, screenY, pointer, button);
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return stage.touchDragged(screenX, screenY, pointer);
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return stage.mouseMoved(screenX, screenY);
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                return stage.scrolled(amountX, amountY);
            }
        });
        createUI();
    }

    private void createUI() {
        uiTable = new Table(skin);
        uiTable.setFillParent(true);

        // Top instruction label
        instructionLabel = new Label("Click on the farm to place your " + buildingItem.getName(), skin);
        instructionLabel.setWrap(true);
        uiTable.add(instructionLabel).expandX().center().padTop(10).row();

        // Bottom buttons
        Table buttonTable = new Table(skin);

        cancelButton = new TextButton("Cancel", skin);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getGame().setScreen(previousScreen);
            }
        });

        confirmButton = new TextButton("Confirm Placement", skin);
        confirmButton.setDisabled(true);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                placeBuilding();
            }
        });

        buttonTable.add(cancelButton).pad(5);
        buttonTable.add(confirmButton).pad(5);
        uiTable.add(buttonTable).expandX().center().padBottom(10);

        stage.addActor(uiTable);
    }

    private void placeBuilding() {
        if (previewX == -1 || previewY == -1 || !canPlace) {
            return;
        }

        // Handle building placement directly
        try {
            String buildingName = buildingItem.getName();
            Location location = farm.getItem(previewX, previewY);

            if (location == null) {
                showErrorDialog("Invalid Location", "Selected location is invalid.");
                return;
            }

            // Check if it's a barn or coop
            if (buildingName.toLowerCase().contains("barn")) {
                Barn barn = getBarnByName(buildingName, location);
                if (barn != null && farm.canBuild(barn.getX(), barn.getY(), barn.getWidth(), barn.getHeight())) {
                    farm.addBarn(barn);
                    Main.getGame().setScreen(previousScreen);
                    return;
                }
            } else if (buildingName.toLowerCase().contains("coop")) {
                Coop coop = getCoopByName(buildingName, location);
                if (coop != null && farm.canBuild(coop.getX(), coop.getY(), coop.getWidth(), coop.getHeight())) {
                    farm.addCoop(coop);
                    Main.getGame().setScreen(previousScreen);
                    return;
                }
            }

            showErrorDialog("Placement Failed", "Cannot place building at this location.");

        } catch (Exception e) {
            showErrorDialog("Error", "An error occurred while placing the building: " + e.getMessage());
        }
    }

    private Barn getBarnByName(String buildingName, Location location) {
        switch (buildingName) {
            case "Barn" -> {
                return new Barn(BarnTypes.NORMAL_BARN, location, buildingName);
            }
            case "Big Barn" -> {
                return new Barn(BarnTypes.BIG_BARN, location, buildingName);
            }
            case "Deluxe Barn" -> {
                return new Barn(BarnTypes.DELUXE_BARN, location, buildingName);
            }
            default -> {
                return null;
            }
        }
    }

    private Coop getCoopByName(String buildingName, Location location) {
        switch (buildingName) {
            case "Coop" -> {
                return new Coop(Cages.NORMAL_COOP, location, buildingName);
            }
            case "Big Coop" -> {
                return new Coop(Cages.BIG_CAGE, location, buildingName);
            }
            case "Deluxe Coop" -> {
                return new Coop(Cages.DELUXE_CAGE, location, buildingName);
            }
            default -> {
                return null;
            }
        }
    }

    private void showErrorDialog(String title, String message) {
        Dialog errorDialog = new Dialog(title, skin);
        errorDialog.text(message);
        errorDialog.button("OK");
        errorDialog.show(stage);
    }

    private void updateInstructionLabel() {
        if (previewX != -1 && previewY != -1) {
            if (canPlace) {
                instructionLabel.setText("Placement valid at (" + previewX + ", " + previewY + "). Click Confirm to place.");
            } else {
                instructionLabel.setText("Invalid placement at (" + previewX + ", " + previewY + "). Choose another location.");
            }
        } else {
            instructionLabel.setText("Click on the farm to place your " + buildingItem.getName());
        }
    }

    // Collect building tiles like WorldController
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

    // Detect building anchors like WorldController
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

    private void detectGreenhouseAnchor(Location location, int x, int y, Set<String> greenhouseTiles) {
        String tileKey = x + "," + y;
        if (greenhouseTiles.contains(tileKey) && !greenhouseAnchors.contains(location)) {
            greenhouseAnchors.add(location);
        }
    }

    private void detectHouseAnchor(Location location, int x, int y, Set<String> houseTiles) {
        String tileKey = x + "," + y;
        if (houseTiles.contains(tileKey) && !houseAnchors.contains(location)) {
            houseAnchors.add(location);
        }
    }

    private void detectBarnAnchor(Location location, int x, int y, Set<String> barnTiles) {
        String tileKey = x + "," + y;
        if (barnTiles.contains(tileKey) && !barnAnchors.contains(location)) {
            barnAnchors.add(location);
        }
    }

    private void detectCoopAnchor(Location location, int x, int y, Set<String> coopTiles) {
        String tileKey = x + "," + y;
        if (coopTiles.contains(tileKey) && !coopAnchors.contains(location)) {
            coopAnchors.add(location);
        }
    }

    private void renderFarmTiles() {
        if (farm == null) {
            Gdx.app.error("BuildingPlacementScreen", "Farm is null!");
            return;
        }

        String currentSeason = getCurrentSeason();

        // Collect building tiles
        Set<String> greenhouseTiles = new HashSet<>();
        Set<String> houseTiles = new HashSet<>();
        Set<String> barnTiles = new HashSet<>();
        Set<String> coopTiles = new HashSet<>();

        collectBuildingTiles(greenhouseTiles, houseTiles, barnTiles, coopTiles);

        // Clear previous anchors
        greenhouseAnchors.clear();
        houseAnchors.clear();
        barnAnchors.clear();
        coopAnchors.clear();

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
                        batch.draw(grassTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
                    }
                }

                // Render tree item if present
                Item item = location.getItem();
                if (item instanceof org.example.common.models.Items.Tree) {
                    renderItemOnTile(x, y, item, currentSeason);
                }

                // Then draw tile-specific texture (like lake, stone, etc.)
                Texture tileTexture = getTileSpecificTexture(tileType, currentSeason);
                if (tileTexture != null) {
                    batch.draw(tileTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
                }

                // Detect anchor for building
                detectBuildingAnchors(location, x, y, tileType, greenhouseTiles, houseTiles, barnTiles, coopTiles);

                // Render non-large building items
                if (!isLargeBuilding(tileType)) {
                    if (!(item instanceof org.example.common.models.Items.Tree) && item != null) {
                        renderItemOnTile(x, y, item, currentSeason);
                    }
                }
            }
        }

        // Render buildings
        renderBuildings();
    }

    private boolean shouldRenderGrass(TileType tileType) {
        return tileType == TileType.Dirt;
    }

    private Texture getTileSpecificTexture(TileType tileType, String season) {
        return switch (tileType) {
            case LAKE -> getTexture("content/map_elements/" + "lake1" + ".png");
            case STONE -> getTexture("content/Rock/mainStone.png");
            default -> null;
        };
    }

    private boolean isLargeBuilding(TileType tileType) {
        return tileType == TileType.BARN || tileType == TileType.COOP;
    }

    private void renderItemOnTile(int x, int y, Item item, String season) {
        if (item == null) return;

        float worldX = x * TILE_SIZE;
        float worldY = y * TILE_SIZE;

        if (item instanceof org.example.common.models.Items.Tree) {
            renderTreeItem(worldX, worldY, season, (org.example.common.models.Items.Tree) item);
        } else if (item instanceof Crop) {
            renderCropItem(worldX, worldY, (Crop) item);
        } else if (item instanceof Plant) {
            renderPlantItem(worldX, worldY, (Plant) item);
        } else if (item instanceof Mineral) {
            renderMineralItem(worldX, worldY, (Mineral) item);
        } else {
            try {
                Texture itemTexture = new Texture(item.getImageFilepath());
                batch.draw(itemTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
            } catch (Exception e) {
                // If texture loading fails, skip rendering this item
            }
        }
    }

    private void renderTreeItem(float worldX, float worldY, String season, org.example.common.models.Items.Tree tree) {
        Texture treeTexture = getTexture("tree_" + season);
        if (treeTexture != null) {
            batch.draw(treeTexture, worldX, worldY, TILE_SIZE * 2, TILE_SIZE * 2);
        }
    }

    private void renderCropItem(float worldX, float worldY, Crop crop) {
        Texture cropTexture = getTexture("crop_spring"); // Use spring as default
        if (cropTexture != null) {
            batch.draw(cropTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
        }
    }

    private void renderPlantItem(float worldX, float worldY, Plant plant) {
        try {
            Texture plantTexture = new Texture(plant.getImageFilepath());
            batch.draw(plantTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
        } catch (Exception e) {
            // If texture loading fails, skip rendering this item
        }
    }

    private void renderMineralItem(float worldX, float worldY, Mineral mineral) {
        String mineralType = mineral.getName().toLowerCase();
        Texture mineralTexture = null;

        if (mineralType.contains("iron")) {
            mineralTexture = getTexture("iron_ore");
        } else if (mineralType.contains("gold")) {
            mineralTexture = getTexture("gold_ore");
        } else if (mineralType.contains("diamond")) {
            mineralTexture = getTexture("diamond_ore");
        } else {
            mineralTexture = getTexture("stone");
        }

        if (mineralTexture != null) {
            batch.draw(mineralTexture, worldX, worldY, TILE_SIZE, TILE_SIZE);
        }
    }

    private void renderBuildings() {
        // Render existing buildings
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
            batch.draw(texture, drawX, drawY, 7 * TILE_SIZE, 6 * TILE_SIZE);
        }
    }

    private void renderHouseAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        float drawX = x * TILE_SIZE;
        float drawY = y * TILE_SIZE;

        Texture texture = getTexture("house");
        if (texture != null) {
            batch.draw(texture, drawX, drawY, 5 * TILE_SIZE, 6 * TILE_SIZE);
        }
    }

    private void renderBarnAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        float drawX = x * TILE_SIZE;
        float drawY = y * TILE_SIZE;

        Texture texture = getTexture("barn");
        if (texture != null) {
            batch.draw(texture, drawX, drawY, 4 * TILE_SIZE, 3 * TILE_SIZE);
        }
    }

    private void renderCoopAtAnchor(Location anchor) {
        int x = anchor.getX();
        int y = anchor.getY();

        float drawX = x * TILE_SIZE;
        float drawY = y * TILE_SIZE;

        Texture texture = getTexture("coop");
        if (texture != null) {
            batch.draw(texture, drawX, drawY, 3 * TILE_SIZE, 3 * TILE_SIZE);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Set up camera and batch to show full farm
        float farmWidth = Farm.width * TILE_SIZE;
        float farmHeight = Farm.height * TILE_SIZE;
        camera.position.set(farmWidth / 2, farmHeight / 2, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Render farm
        batch.begin();
        renderFarmTiles();

        // Render building preview if placing
        if (isPlacing && previewX != -1 && previewY != -1) {
            Texture previewTexture = getTexture("building_preview");
            if (previewTexture != null) {
                float alpha = canPlace ? 0.7f : 0.3f;
                batch.setColor(1, 1, 1, alpha);
                // Adjust preview size based on building type
                int previewWidth = 2 * TILE_SIZE;
                int previewHeight = 2 * TILE_SIZE;
                if (buildingItem.getName().toLowerCase().contains("barn")) {
                    previewWidth = 4 * TILE_SIZE;
                    previewHeight = 3 * TILE_SIZE;
                } else if (buildingItem.getName().toLowerCase().contains("coop")) {
                    previewWidth = 3 * TILE_SIZE;
                    previewHeight = 3 * TILE_SIZE;
                }
                batch.draw(previewTexture, previewX * TILE_SIZE, previewY * TILE_SIZE, previewWidth, previewHeight);
                batch.setColor(1, 1, 1, 1);
            }
        }
        batch.end();

        // Render UI
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        for (Texture texture : textureCache.values()) {
            if (texture != null) {
                texture.dispose();
            }
        }
    }
}
