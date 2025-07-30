package org.example.client.controllers.gameplay;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.example.client.Main;
import org.example.common.models.Items.Plant;
import org.example.common.models.MapDetails.GreenHouse;
import org.example.common.models.Player.Player;

import java.util.List;
import java.util.Map;

public class GreenhouseController {

    private final PlayerController playerController;
    private final GreenHouse greenHouse;
    private final OrthographicCamera camera;
    private final Map<String, Texture> textureCache; // Uses the existing texture cache
    private final ShapeRenderer shapeRenderer; // For drawing the simple background

    private static final int TILE_SIZE = 60;

    /**
     * Constructor uses the existing texture cache from WorldController.
     */
    public GreenhouseController(PlayerController playerController, GreenHouse greenHouse, OrthographicCamera camera, Map<String, Texture> textureCache) {
        this.playerController = playerController;
        this.greenHouse = greenHouse;
        this.camera = camera;
        this.textureCache = textureCache; // No new loading!
        this.shapeRenderer = new ShapeRenderer();
    }

    /**
     * The main update loop, called by GreenhouseScreen.
     */
    public void update() {
        updateCamera();

        // Render the simple background color
        renderBackground();

        // Switch to SpriteBatch for rendering textures
        Main.getBatch().setProjectionMatrix(camera.combined);

        // Render plants and the player
        renderGreenhouseTilesAndPlants();
        renderPlayer();
    }

    /**
     * Renders a simple colored rectangle as the background.
     */
    private void renderBackground() {
        // Use ShapeRenderer to draw a solid color rectangle background
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.valueOf("3b2e21")); // A dark, earthy brown color
        shapeRenderer.rect(0, 0, GreenHouse.getWidth() * TILE_SIZE, GreenHouse.getHeight() * TILE_SIZE);
        shapeRenderer.end();
    }


    /**
     * Updates camera to follow the player within the greenhouse bounds.
     */
    private void updateCamera() {
        float playerX = playerController.getPlayer().getPosX();
        float playerY = playerController.getPlayer().getPosY();

        float mapWidth = GreenHouse.getWidth() * TILE_SIZE;
        float mapHeight = GreenHouse.getHeight() * TILE_SIZE;

        float halfCamWidth = camera.viewportWidth * camera.zoom / 2;
        float halfCamHeight = camera.viewportHeight * camera.zoom / 2;

        float camX = Math.max(halfCamWidth, Math.min(playerX, mapWidth - halfCamWidth));
        float camY = Math.max(halfCamHeight, Math.min(playerY, mapHeight - halfCamHeight));

        camera.position.set(camX, camY, 0);
        camera.update();
    }

    /**
     * Renders all the plants currently in the greenhouse.
     */
    private void renderGreenhouseTilesAndPlants() {
        // Draw the plowed ground texture for each plant spot
        Texture plowedTexture = textureCache.get("plowed");
        if (plowedTexture != null) {
            for (int y = 0; y < GreenHouse.getHeight(); y++) {
                for (int x = 0; x < GreenHouse.getWidth(); x++) {
                    Main.getBatch().draw(plowedTexture, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // Draw the plants on top of the plowed tiles
        List<Plant> plants = greenHouse.getGreenHousePlants();
        for (int i = 0; i < plants.size(); i++) {
            Plant plant = plants.get(i);

            // This grid layout assumes plants are added sequentially left-to-right, top-to-bottom
            float plantX = (i % GreenHouse.getWidth()) * TILE_SIZE;
            float plantY = (i / GreenHouse.getWidth()) * TILE_SIZE;

            // Get the correct texture key for the plant's current stage
            String key = plant.getImageFilepath() + "_Stage_" + plant.getStage();
            Texture plantTexture = textureCache.get(key);

            if (plantTexture != null) {
                Main.getBatch().draw(plantTexture, plantX, plantY, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    /**
     * Renders the player sprite.
     */
    private void renderPlayer() {
        Player player = playerController.getPlayer();
        if (player != null && player.getPlayerSprite() != null) {
            // The PlayerController should be updating the sprite's position
            player.getPlayerSprite().draw(Main.getBatch());
        }
    }

    /**
     * Handles user input within the greenhouse.
     */
    public void handleInput() {
        // This logic remains the same as your WorldController's input handling
    }

    /**
     * Disposes of the ShapeRenderer. Textures are disposed by WorldController.
     */
    public void dispose() {
        shapeRenderer.dispose();
    }

    public Map<String, Texture> getTextureCache() {
        return this.textureCache;
    }
}
