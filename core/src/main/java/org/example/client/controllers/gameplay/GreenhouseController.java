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
    private final Map<String, Texture> textureCache;
    private final ShapeRenderer shapeRenderer;

    private static final int TILE_SIZE = 60;

    public GreenhouseController(PlayerController playerController, GreenHouse greenHouse, OrthographicCamera camera, Map<String, Texture> textureCache) {
        this.playerController = playerController;
        this.greenHouse = greenHouse;
        this.camera = camera;
        this.textureCache = textureCache;
        this.shapeRenderer = new ShapeRenderer();
    }

    public void update() {
        updateCamera();
        renderBackground();
        Main.getBatch().setProjectionMatrix(camera.combined);
        renderGameObjects();
    }

    public void renderBackground() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.valueOf("3b2e21"));
        shapeRenderer.rect(0, 0, GreenHouse.getWidth() * TILE_SIZE, GreenHouse.getHeight() * TILE_SIZE);
        shapeRenderer.end();
    }

    /**
     * Updates camera to follow the player within the greenhouse bounds.
     * NOW CORRECTLY HANDLES MAPS SMALLER THAN THE SCREEN.
     */
    public void updateCamera() {
        float playerX = playerController.getPlayer().getPosX();
        float playerY = playerController.getPlayer().getPosY();

        float mapWidth = GreenHouse.getWidth() * TILE_SIZE;
        float mapHeight = GreenHouse.getHeight() * TILE_SIZE;

        // --- THIS IS THE CORRECTED LOGIC ---
        // If the map is smaller than the screen, just center the camera on the map.
        if (mapWidth < camera.viewportWidth || mapHeight < camera.viewportHeight) {
            camera.position.set(mapWidth / 2f, mapHeight / 2f, 0);
        } else {
            // Otherwise, use the clamping logic to follow the player on a larger map.
            float halfCamWidth = camera.viewportWidth * camera.zoom / 2;
            float halfCamHeight = camera.viewportHeight * camera.zoom / 2;

            float camX = Math.max(halfCamWidth, Math.min(playerX, mapWidth - halfCamWidth));
            float camY = Math.max(halfCamHeight, Math.min(playerY, mapHeight - halfCamHeight));

            camera.position.set(camX, camY, 0);
        }

        camera.update();
    }

    /**
     * This method now ONLY renders the environment (tiles and plants).
     * The player is rendered separately by the PlayerController.
     */
    public void renderGameObjects() {
        Main.getBatch().setProjectionMatrix(camera.combined);

        Texture plowedTexture = textureCache.get("plowed");
        if (plowedTexture != null) {
            for (int y = 0; y < GreenHouse.getHeight(); y++) {
                for (int x = 0; x < GreenHouse.getWidth(); x++) {
                    Main.getBatch().draw(plowedTexture, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        List<Plant> plants = greenHouse.getGreenHousePlants();
        for (int i = 0; i < plants.size(); i++) {
            Plant plant = plants.get(i);
            float plantX = (i % GreenHouse.getWidth()) * TILE_SIZE;
            float plantY = (i / GreenHouse.getWidth()) * TILE_SIZE;
            String key = plant.getImageFilepath() + "_Stage_" + plant.getStage();
            Texture plantTexture = textureCache.get(key);
            if (plantTexture != null) {
                Main.getBatch().draw(plantTexture, plantX, plantY, TILE_SIZE, TILE_SIZE);
            }
        }
        // The call to renderPlayer() has been removed.
    }

    public void handleInput() {
        // Input logic here...
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
