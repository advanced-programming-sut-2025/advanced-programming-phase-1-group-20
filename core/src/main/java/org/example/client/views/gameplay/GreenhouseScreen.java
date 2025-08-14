package org.example.client.views.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import org.example.client.Main;
import org.example.client.controllers.gameplay.GreenhouseController;
import org.example.client.controllers.gameplay.PlantController;
import org.example.client.controllers.gameplay.PlayerController;
import org.example.client.controllers.gameplay.WorldController;
import org.example.common.models.Items.Food;
import org.example.common.models.Items.Seed;
import org.example.common.models.MapDetails.GreenHouse;
import org.example.common.models.common.Result;

public class GreenhouseScreen implements Screen , InputProcessor {

    private final GreenhouseController greenhouseController;
    private final PlayerController playerController;
    private final OrthographicCamera camera;
    private final Screen previousScreen;
    private final GreenHouse greenhouse;
    private final PlantController plantController;
    private static final int TILE_SIZE = 60; // Make sure this matches the controller

    private float lastToolMouseX = 0;
    private float lastToolMouseY = 0;

    public GreenhouseScreen(PlayerController playerController, GreenHouse greenHouse, WorldController worldController , Screen previousScreen) {
        this.playerController = playerController;
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.previousScreen = previousScreen;
        this.plantController = new PlantController();

        // --- NEW CODE TO CENTER THE VIEW ---
        // Calculate the center of the greenhouse map in pixels
        float mapCenterX = (GreenHouse.getWidth() * TILE_SIZE) / 2f;
        float mapCenterY = (GreenHouse.getHeight() * TILE_SIZE) / 2f;

        // Set the camera's position to the center of the map
        this.camera.position.set(mapCenterX, mapCenterY, 0);
        this.camera.update(); // Apply the position change
        // --- END OF NEW CODE ---
        this.greenhouse = greenHouse;

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
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            playerController.getPlayer().setPosX(greenhouse.getX() * TILE_SIZE - 60);
            playerController.getPlayer().setPosY(greenhouse.getY() * TILE_SIZE - 60);
            Main.getGame().setScreen(previousScreen);
        }
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

    @Override
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT){
            float correctedScreenY = Gdx.graphics.getHeight() - screenY;
            Vector3 worldCoords = camera.unproject(new Vector3(screenX, correctedScreenY, 0));
            if(playerController.getPlayer().getCurrentItem() != null) {
                System.out.println("here");
                if(playerController.getPlayer().getCurrentItem() instanceof Seed seed){
                    float playerX = playerController.getPlayer().getPosX();
                    float playerY = playerController.getPlayer().getPosY();
                    float dx = worldCoords.x - playerX;
                    float dy = worldCoords.y - playerY;

                    lastToolMouseX = worldCoords.x;
                    lastToolMouseY = worldCoords.y;

                    double angle = Math.atan2(dy, dx);
                    String direction;
                    if (Math.abs(dx) > Math.abs(dy)) {
                        direction = dx > 0 ? "east" : "west";
                    } else {
                        direction = dy > 0 ? "north" : "south";
                    }
                    String[] args = new String[]{seed.getName() , direction};

                    Result result = plantController.plant(args);
                    System.out.println(result.message());
                }
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }
}
