package org.example.client.views.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import org.example.client.Main;
import org.example.client.controllers.gameplay.GreenhouseController;
import org.example.client.controllers.gameplay.PlayerController;
import org.example.client.controllers.gameplay.WorldController;
import org.example.common.models.MapDetails.GreenHouse;

public class GreenhouseScreen implements Screen {

    private final GreenhouseController greenhouseController;
    private final PlayerController playerController;
    private final OrthographicCamera camera;
    private static final int TILE_SIZE = 60; // Make sure this matches the controller

    public GreenhouseScreen(PlayerController playerController, GreenHouse greenHouse, WorldController worldController) {
        this.playerController = playerController;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // --- NEW CODE TO CENTER THE VIEW ---
        // Calculate the center of the greenhouse map in pixels
        float mapCenterX = (GreenHouse.getWidth() * TILE_SIZE) / 2f;
        float mapCenterY = (GreenHouse.getHeight() * TILE_SIZE) / 2f;

        // Set the camera's position to the center of the map
        this.camera.position.set(mapCenterX, mapCenterY, 0);
        this.camera.update(); // Apply the position change
        // --- END OF NEW CODE ---

        // Set player's starting position inside the greenhouse
        float startX = mapCenterX; // Start player in the center as well
        float startY = TILE_SIZE; // Near the bottom edge
        playerController.getPlayer().setPosX(startX);
        playerController.getPlayer().setPosY(startY);

        this.greenhouseController = new GreenhouseController(playerController, greenHouse, camera, worldController.getTextureCache());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 1. Update logic
        greenhouseController.updateCamera();

        // 2. Render background
        greenhouseController.renderBackground();

        // 3. Render all sprites
        Main.getBatch().begin();
        greenhouseController.renderGameObjects();
        playerController.update();
        Main.getBatch().end();

        // 4. Handle input
        greenhouseController.handleInput();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        // Re-center the view on resize if the aspect ratio changes things
        float mapCenterX = (GreenHouse.getWidth() * TILE_SIZE) / 2f;
        float mapCenterY = (GreenHouse.getHeight() * TILE_SIZE) / 2f;
        camera.position.set(mapCenterX, mapCenterY, 0);
        camera.update();
    }

    // ... rest of the file is the same
    @Override
    public void show() { }

    @Override
    public void hide() { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void dispose() {
        greenhouseController.dispose();
    }
}
