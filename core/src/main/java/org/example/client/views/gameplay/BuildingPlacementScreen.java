package org.example.client.views.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.example.client.Main;
import org.example.client.controllers.MarketController;
import org.example.common.models.Items.Item;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Result;
import org.example.common.models.common.Location;

public class BuildingPlacementScreen implements Screen, Disposable {

    private Stage stage;
    private Skin skin;
    private Player player;
    private Farm farm;
    private Item buildingItem;
    private Screen previousScreen;
    private MarketController controller;

    // Rendering
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private Texture farmBackground;
    private Texture buildingPreviewTexture;
    
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

    public BuildingPlacementScreen(Player player, Item buildingItem, Screen previousScreen, Skin skin) {
        this.player = player;
        this.buildingItem = buildingItem;
        this.previousScreen = previousScreen;
        this.skin = skin;
        this.farm = player.getCurrentFarm();
        this.controller = new MarketController(player, null); // We don't need market for building placement

        // Initialize camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(800, 600, camera);
        batch = new SpriteBatch();

        // Load farm background
        try {
            farmBackground = new Texture(farm.getBackground());
        } catch (Exception e) {
            System.err.println("Failed to load farm background: " + e.getMessage());
            farmBackground = null;
        }

        // Load building preview texture
        try {
            buildingPreviewTexture = new Texture(buildingItem.getImageFilepath());
        } catch (Exception e) {
            System.err.println("Failed to load building preview: " + e.getMessage());
            buildingPreviewTexture = null;
        }

        stage = new Stage(viewport);
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
                float worldX = viewport.unproject(new com.badlogic.gdx.math.Vector3(screenX, screenY, 0)).x;
                float worldY = viewport.unproject(new com.badlogic.gdx.math.Vector3(screenX, screenY, 0)).y;
                
                // Convert to tile coordinates
                int tileX = (int) (worldX / 32);
                int tileY = (int) (worldY / 32);
                
                // Check if click is within farm bounds
                if (tileX >= 0 && tileX < Farm.width && tileY >= 0 && tileY < Farm.height) {
                    previewX = tileX;
                    previewY = tileY;
                    isPlacing = true;
                    
                    // Check if placement is valid
                    canPlace = farm.canBuild(tileX, tileY, 2, 2); // Assuming 2x2 building size
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

        // Create the building placement command
        String[] args = {buildingItem.getName(), String.valueOf(previewX), String.valueOf(previewY)};
        Result result = controller.build(args);

        if (result.success()) {
            // Return to previous screen
            Main.getGame().setScreen(previousScreen);
        } else {
            // Show error dialog
            Dialog errorDialog = new Dialog("Placement Failed", skin);
            errorDialog.text(result.message());
            errorDialog.button("OK");
            errorDialog.show(stage);
        }
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

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render farm background
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        if (farmBackground != null) {
            batch.draw(farmBackground, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        }

        // Render building preview if placing
        if (isPlacing && buildingPreviewTexture != null && previewX != -1 && previewY != -1) {
            float alpha = canPlace ? 0.7f : 0.3f;
            batch.setColor(1, 1, 1, alpha);
            batch.draw(buildingPreviewTexture, previewX * 32, previewY * 32, 64, 64); // Assuming 32x32 tiles
            batch.setColor(1, 1, 1, 1);
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
        if (farmBackground != null) {
            farmBackground.dispose();
        }
        if (buildingPreviewTexture != null) {
            buildingPreviewTexture.dispose();
        }
    }
} 