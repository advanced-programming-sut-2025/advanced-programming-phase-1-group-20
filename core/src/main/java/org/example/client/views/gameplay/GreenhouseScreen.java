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

    public GreenhouseScreen(PlayerController playerController, GreenHouse greenHouse, WorldController worldController) {
        this.playerController = playerController;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Set player's starting position inside the greenhouse
        // For example, near the entrance at the bottom-center
        float startX = (GreenHouse.getWidth() * 60) / 2f;
        float startY = 60f; // One tile up from the bottom edge
        playerController.getPlayer().setPosX(startX);
        playerController.getPlayer().setPosY(startY);

        // Create the controller, passing the existing texture cache from the WorldController
        this.greenhouseController = new GreenhouseController(playerController, greenHouse, camera, worldController.getTextureCache());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Player movement logic
        playerController.update();

        // Greenhouse rendering logic
        Main.getBatch().begin();
        greenhouseController.update(); // This now renders everything: background, tiles, plants, player
        Main.getBatch().end();

        // Input handling
        greenhouseController.handleInput();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    // Add a method in WorldController to expose the texture cache
    // public Map<String, Texture> getTextureCache() { return this.textureCache; }

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
