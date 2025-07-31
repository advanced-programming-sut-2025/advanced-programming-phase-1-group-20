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
    private static final float MAP_ZOOM = 13.5f; // Zoom out to show entire map

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

        setupUI();
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

        // Center camera on the entire map
        GameMap gameMap = game.getGameMap();
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

        batch.end();

        // Render UI
        stage.act(delta);
        stage.draw();
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
    }
}
