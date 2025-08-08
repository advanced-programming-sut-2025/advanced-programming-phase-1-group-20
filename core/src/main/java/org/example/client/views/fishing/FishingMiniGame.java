package org.example.client.views.fishing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.client.views.GameView;
import org.example.client.controllers.fishing.FishingController;
import org.example.utils.AssetManager;
import org.example.common.models.enums.Types.Quality;
import org.example.common.models.enums.Types.FishType;

import java.util.HashMap;

public class FishingMiniGame implements Screen, InputProcessor {
    private final float BASE_SPEED_FACTOR = 3;
    private final float MAX_PROGRESS = 10;
    private final float MIN_PROGRESS = 0;
    private final float MAX_BOBBER_POS = 325;
    private final float MIN_BOBBER_POS = 0;

    // Progress bar constants
    private final float VERTICAL_PROGRESS_BAR_WIDTH = 15;
    private final float VERTICAL_PROGRESS_BAR_HEIGHT = 445;

    private boolean isGameOngoing = true;
    private boolean isVictorious;
    private Stage stage;
    private Texture backgroundTexture;

    private Image waterLane;

    private Rectangle fishHitbox;
    private Image fishImage;

    private float fishPosition = 0;
    private float fishVelocity = 0;
    private float fishAcceleration = 0;

    private Rectangle bobberHitbox;
    private Image bobberImage;
    private float bobberPosition = 0;
    private float bobberVelocity = 0;
    private float bobberAcceleration = 0;

    private float catchingProgress = 0.01f;
    private boolean isCatchPerfect = true;

    private final GameView gameView;
    private final String poleName;

    private float BOBBER_BASE_X;
    public float BOBBER_BASE_Y;

    private FishType caughtFishType = FishType.HERRING;
    private int caughtFishQuantity = 2;
    private Quality caughtFishQuality = Quality.Normal;
    private int xpGained = 5;

    public FishingMiniGame(GameView gameView, String poleName) {
        this.gameView = gameView;
        this.poleName = poleName;
        getAnglingResults();
        initializeStage();
        FishingController.initializeFishBehavior(caughtFishType.isLegendary());

        // Initialize progress with a visible starting value
        catchingProgress = 1.0f; // Start with some progress visible
    }

    private void getAnglingResults() {
        HashMap<String, Object> results = FishingController.generateFishingResults(poleName);
        caughtFishType = (FishType) results.get("type");
        caughtFishQuality = (Quality) results.get("quality");
        caughtFishQuantity = (int) results.get("quantity");
        xpGained = (int) results.get("xp");
    }

    private void initializeStage() {
        stage = new Stage(new ScreenViewport());

        // Create background using the miniGameBackground from AssetManager
        Table background = new Table();
        background.setFillParent(true);
        background.setBackground(new com.badlogic.gdx.scenes.scene2d.utils.Drawable() {
            @Override
            public void draw(com.badlogic.gdx.graphics.g2d.Batch batch, float x, float y, float width, float height) {
                // Use the miniGameBackground texture from AssetManager
                Texture backgroundTexture = AssetManager.getAssetManager().getMiniGameBackground();
                batch.draw(backgroundTexture, x, y, width, height);
            }

            @Override
            public float getLeftWidth() { return 0; }
            @Override
            public void setLeftWidth(float leftWidth) {}
            @Override
            public float getRightWidth() { return 0; }
            @Override
            public void setRightWidth(float rightWidth) {}
            @Override
            public float getTopHeight() { return 0; }
            @Override
            public void setTopHeight(float topHeight) {}
            @Override
            public float getBottomHeight() { return 0; }
            @Override
            public void setBottomHeight(float bottomHeight) {}
            @Override
            public float getMinWidth() { return 0; }
            @Override
            public void setMinWidth(float minWidth) {}
            @Override
            public float getMinHeight() { return 0; }
            @Override
            public void setMinHeight(float minHeight) {}
        });
        stage.addActor(background);

        waterLane = new Image(AssetManager.getAssetManager().getFishingWaterLaneTexture());
        waterLane.setPosition(stage.getWidth() / 2 - waterLane.getWidth() / 2, stage.getHeight() / 2 - waterLane.getHeight() / 2);
        stage.addActor(waterLane);

        bobberImage = new Image(AssetManager.getAssetManager().getFishingSafezoneTexture());

        BOBBER_BASE_X = stage.getWidth() / 2 - waterLane.getWidth() / 2 + 85;
        BOBBER_BASE_Y = stage.getHeight() / 2 - waterLane.getHeight() / 2 + 20;
        bobberImage.setPosition(BOBBER_BASE_X, BOBBER_BASE_Y);

        bobberHitbox = new Rectangle(BOBBER_BASE_X, BOBBER_BASE_Y, bobberImage.getWidth(), bobberImage.getHeight());
        stage.addActor(bobberImage);

        if (caughtFishType.isLegendary()) {
            fishImage = new Image(AssetManager.getAssetManager().getFishingLegendaryFishIconTexture());
        } else {
            fishImage = new Image(AssetManager.getAssetManager().getFishingFishIconTexture());
        }
        fishImage.setPosition(BOBBER_BASE_X, BOBBER_BASE_Y);
        stage.addActor(fishImage);

        fishHitbox = new Rectangle(BOBBER_BASE_X, BOBBER_BASE_Y, fishImage.getWidth(), fishImage.getHeight());

        // Create instruction label with new text
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.fontColor = Color.WHITE;
        labelStyle.font = new BitmapFont();

        Label label = new Label("Master the waters! Use up/down arrows to guide your lure. Press Q to abandon.", labelStyle);
        stage.addActor(label);
        label.setPosition(stage.getWidth() / 2 - label.getWidth() / 2, stage.getHeight() / 2 + waterLane.getHeight() / 2 + 50);
    }

    @Override
    public boolean keyDown(int keycode) {
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
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (isGameOngoing) {
            handlePlayerInput();
            handleBobberKinematics(delta);
            FishingController.processFishAI(this, delta);
            incrementProgress(delta);
            checkForWinOrLoss();
        }

        stage.act(delta);
        stage.draw();

        // Draw the vertical progress bar directly using batch
        if (isGameOngoing) {
            drawVerticalProgressBar();
        }
    }

    private void drawVerticalProgressBar() {
        // Store original projection matrix
        Matrix4 originalProjection = Main.getBatch().getProjectionMatrix();

        // Set up orthographic projection for UI rendering
        Main.getBatch().setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        Main.getBatch().begin();

        // Create a white texture for rendering
        Pixmap whitePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(Color.WHITE);
        whitePixmap.fill();
        Texture whiteTexture = new Texture(whitePixmap);
        whitePixmap.dispose();

        // Position bar near the fishing lane (water lane)
        float waterLaneX = stage.getWidth() / 2 - waterLane.getWidth() / 2;
        float waterLaneY = stage.getHeight() / 2 - waterLane.getHeight() / 2;
        float barX = waterLaneX + waterLane.getWidth() - 43;
        float barY = waterLaneY + waterLane.getHeight() / 2 - VERTICAL_PROGRESS_BAR_HEIGHT / 2; // Center vertically with water lane

        // Calculate progress percentage
        float progressPercentage = Math.max(0, Math.min(1, catchingProgress / MAX_PROGRESS));
        float barHeight = VERTICAL_PROGRESS_BAR_HEIGHT * progressPercentage;

        // Draw background (empty bar)
        Main.getBatch().setColor(Color.DARK_GRAY);
        Main.getBatch().draw(whiteTexture, barX, barY, VERTICAL_PROGRESS_BAR_WIDTH, VERTICAL_PROGRESS_BAR_HEIGHT);

        // Draw filled portion from bottom up
        if (barHeight > 0) {
            // Dynamic color based on progress level
            Color progressColor;
            if (progressPercentage > 0.7f) {
                // Green for high progress
                progressColor = Color.GREEN;
            } else if (progressPercentage > 0.3f) {
                // Yellow for medium progress
                progressColor = Color.YELLOW;
            } else {
                // Red for low progress
                progressColor = Color.RED;
            }
            Main.getBatch().setColor(progressColor);
            Main.getBatch().draw(whiteTexture, barX, barY, VERTICAL_PROGRESS_BAR_WIDTH, barHeight);
        }

        // Draw border
        Main.getBatch().setColor(Color.WHITE);
        Main.getBatch().draw(whiteTexture, barX, barY, VERTICAL_PROGRESS_BAR_WIDTH, 2); // Bottom border
        Main.getBatch().draw(whiteTexture, barX, barY + VERTICAL_PROGRESS_BAR_HEIGHT - 2, VERTICAL_PROGRESS_BAR_WIDTH, 2); // Top border
        Main.getBatch().draw(whiteTexture, barX, barY, 2, VERTICAL_PROGRESS_BAR_HEIGHT); // Left border
        Main.getBatch().draw(whiteTexture, barX + VERTICAL_PROGRESS_BAR_WIDTH - 2, barY, 2, VERTICAL_PROGRESS_BAR_HEIGHT); // Right border

        // Reset color and end batch
        Main.getBatch().setColor(Color.WHITE);
        Main.getBatch().end();

        // Restore original projection matrix
        Main.getBatch().setProjectionMatrix(originalProjection);

        // Dispose of the white texture
        whiteTexture.dispose();
    }

    private void checkForWinOrLoss() {
        if (catchingProgress == MAX_PROGRESS) {
            isGameOngoing = false;
            isVictorious = true;
        } else if (catchingProgress == MIN_PROGRESS) {
            isGameOngoing = false;
            isVictorious = false;
        }

        if (!isGameOngoing) {
            stage.dispose();

            if (isVictorious) {
                if (isCatchPerfect) {
                    if (caughtFishQuality.ordinal() >= 2) {
                        caughtFishQuality = Quality.values()[Math.min(caughtFishQuality.ordinal() + 1, Quality.values().length - 1)];
                    }
                    xpGained = (int) (xpGained * 2.4);
                }
                notifyServerOfVictory(xpGained, caughtFishType, caughtFishQuality, caughtFishQuantity);
            }
            constructEndStage();
        }
    }

    private void constructEndStage() {
        stage = new Stage(new ScreenViewport());

        // Create background using the miniGameBackground from AssetManager
        Table background = new Table();
        background.setFillParent(true);
        background.setBackground(new com.badlogic.gdx.scenes.scene2d.utils.Drawable() {
            @Override
            public void draw(com.badlogic.gdx.graphics.g2d.Batch batch, float x, float y, float width, float height) {
                // Use the miniGameBackground texture from AssetManager
                Texture backgroundTexture = AssetManager.getAssetManager().getMiniGameBackground();
                batch.draw(backgroundTexture, x, y, width, height);
            }

            @Override
            public float getLeftWidth() { return 0; }
            @Override
            public void setLeftWidth(float leftWidth) {}
            @Override
            public float getRightWidth() { return 0; }
            @Override
            public void setRightWidth(float rightWidth) {}
            @Override
            public float getTopHeight() { return 0; }
            @Override
            public void setTopHeight(float topHeight) {}
            @Override
            public float getBottomHeight() { return 0; }
            @Override
            public void setBottomHeight(float bottomHeight) {}
            @Override
            public float getMinWidth() { return 0; }
            @Override
            public void setMinWidth(float minWidth) {}
            @Override
            public float getMinHeight() { return 0; }
            @Override
            public void setMinHeight(float minHeight) {}
        });
        stage.addActor(background);

        Table table = new Table();
        table.setFillParent(true);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.fontColor = Color.WHITE;
        labelStyle.font = new BitmapFont();

        String resultText;
        if (isVictorious) {
            resultText = "SPLENDID CATCH!";
            resultText += "\nYou've mastered the waters!";
            resultText += "\n\nRESULTS:";
            resultText += "\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
            resultText += "\nFish: " + caughtFishType.getName();
            resultText += "\nQuality: " + getQualityDescription(caughtFishQuality);
            resultText += "\nBase Value: " + caughtFishType.getBasePrice() + "g";
            resultText += "\nQuantity: " + caughtFishQuantity;
            resultText += "\nDescription: " + caughtFishType.getDescription();
            resultText += "\nSeason: " + caughtFishType.getSeasons()[0];
            resultText += "\nExperience gained: " + xpGained + " points";
            resultText += "\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
            resultText += "\nFish added to your inventory!";
        } else {
            resultText = "The fish got away this time...";
            resultText += "\nDon't give up, angler!";
            resultText += "\nTry again with better timing!";
        }

        Label label = new Label(resultText, labelStyle);
        Label label1 = new Label(isCatchPerfect ? "~Flawless technique!~" : "Decent effort, keep practicing!", labelStyle);

        // Create fish image display for successful catches
        Image fishDisplayImage = null;
        if (isVictorious) {
            // Load the fish image using the AssetManager
            Texture fishTexture = AssetManager.getAssetManager().getFishTexture(caughtFishType.getImageFilePath());
            fishDisplayImage = new Image(fishTexture);
            fishDisplayImage.setSize(200, 200); // Make it larger and more visible
        }

        Skin buttonSkin = new Skin();
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = new BitmapFont();
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.GRAY;
        buttonStyle.overFontColor = Color.YELLOW;

        buttonStyle.up = new com.badlogic.gdx.scenes.scene2d.utils.Drawable() {
            @Override
            public void draw(com.badlogic.gdx.graphics.g2d.Batch batch, float x, float y, float width, float height) {
                batch.setColor(0.4f, 0.4f, 0.4f, 1f);
                com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
                pixmap.setColor(0.4f, 0.4f, 0.4f, 1f);
                pixmap.fill();
                com.badlogic.gdx.graphics.Texture texture = new com.badlogic.gdx.graphics.Texture(pixmap);
                batch.draw(texture, x, y, width, height);
                texture.dispose();
                pixmap.dispose();
                batch.setColor(1, 1, 1, 1);
            }

            @Override
            public float getLeftWidth() { return 0; }
            @Override
            public void setLeftWidth(float leftWidth) {}
            @Override
            public float getRightWidth() { return 0; }
            @Override
            public void setRightWidth(float rightWidth) {}
            @Override
            public float getTopHeight() { return 0; }
            @Override
            public void setTopHeight(float topHeight) {}
            @Override
            public float getBottomHeight() { return 0; }
            @Override
            public void setBottomHeight(float bottomHeight) {}
            @Override
            public float getMinWidth() { return 0; }
            @Override
            public void setMinWidth(float minWidth) {}
            @Override
            public float getMinHeight() { return 0; }
            @Override
            public void setMinHeight(float minHeight) {}
        };

        buttonSkin.add("default", buttonStyle);
        TextButton backButton = new TextButton("Return to Farm", buttonSkin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                Main.getGame().setScreen(gameView);
            }
        });

        table.center();
        table.add(label);
        if (isVictorious && fishDisplayImage != null) {
            table.row();
            // Add a label above the fish image
            Label fishImageLabel = new Label("CAUGHT FISH:", labelStyle);
            table.add(fishImageLabel).pad(10);
            table.row();
            table.add(fishDisplayImage).pad(20);
        }
        if (isVictorious) {
            table.row();
            table.add(label1);
        }
        table.row();
        table.add(backButton);
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    private String getQualityDescription(Quality quality) {
        switch (quality) {
            case Normal: return "Common";
            case Silver: return "Uncommon";
            case Golden: return "Rare";
            case Iridium: return "Legendary";
            default: return "Unknown";
        }
    }

    private void notifyServerOfVictory(int xpGained, FishType caughtFishType, Quality caughtFishQuality, int caughtFishQuantity) {
        // Add fish to player's inventory
        try {
            org.example.common.models.Player.Player player = org.example.common.models.App.getGame().getCurrentPlayer();

            // Convert quality enum to integer for Fish constructor
            int qualityInt = switch (caughtFishQuality) {
                case Normal -> 0;
                case Silver -> 1;
                case Golden -> 2;
                case Iridium -> 3;
            };

            // Get current season
            org.example.common.models.enums.Seasons currentSeason = org.example.common.models.App.getGame().getDate().getSeason();

            // Create fish object and add to inventory
            org.example.common.models.entities.animal.Fish fish = new org.example.common.models.entities.animal.Fish(
                caughtFishType, qualityInt, currentSeason
            );

            // Add the fish to player's inventory
            for (int i = 0; i < caughtFishQuantity; i++) {
                player.addItem(fish);
            }

            // Increase player's fishing skill XP
            // Note: Skills are managed through the updateUnit() method
            for (int i = 0; i < xpGained; i++) {
                player.getSkills().get(3).updateUnit(); // Fishing skill is at index 3
            }

            System.out.println("🎣 Fish added to inventory: " + caughtFishType.getName() + " x" + caughtFishQuantity);
            System.out.println("⭐ Quality: " + caughtFishQuality);
            System.out.println("🎯 XP gained: " + xpGained);

        } catch (Exception e) {
            System.err.println("Error adding fish to inventory: " + e.getMessage());
        }
    }

    private void incrementProgress(float delta) {
        if (fishHitbox.overlaps(bobberHitbox)) {
            // Fish is in the green zone - progress increases
            catchingProgress += delta * 2.0f; // Faster progress when fish is caught
            // Make bobber glow when fish is caught
            bobberImage.setColor(0.2f, 1.0f, 0.2f, 1.0f); // Bright green glow
        } else {
            // Fish is outside the green zone - progress decreases
            catchingProgress -= delta * 1.5f; // Slower decrease to give player a chance
            isCatchPerfect = false;
            // Return bobber to normal color
            bobberImage.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }

        // Clamp progress between min and max
        catchingProgress = MathUtils.clamp(catchingProgress, MIN_PROGRESS, MAX_PROGRESS);
    }

    private void handlePlayerInput() {
        final float ALPHA = 10;
        final float BETA = 0.15f;

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            bobberAcceleration = ALPHA - BETA * bobberVelocity;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            bobberAcceleration = -ALPHA - BETA * bobberVelocity;
        } else if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            dispose();
            Main.getGame().setScreen(gameView);
        } else {
            bobberAcceleration = -BETA * bobberVelocity;
        }
    }

    private void handleBobberKinematics(float delta) {
        final float DELTA_X = 0.5f;

        bobberPosition += delta * bobberVelocity * BASE_SPEED_FACTOR;
        bobberPosition = MathUtils.clamp(bobberPosition, MIN_BOBBER_POS, MAX_BOBBER_POS);

        if (bobberPosition == MAX_BOBBER_POS || bobberPosition == MIN_BOBBER_POS) {
            bobberVelocity = -bobberVelocity * 0.8f;

            if (bobberPosition == MIN_BOBBER_POS) {
                bobberPosition = DELTA_X + MIN_BOBBER_POS;
            } else {
                bobberPosition = -DELTA_X + MAX_BOBBER_POS;
            }
        }

        bobberVelocity += delta * bobberAcceleration * BASE_SPEED_FACTOR;

        bobberImage.setPosition(BOBBER_BASE_X, BOBBER_BASE_Y + bobberPosition);
        bobberHitbox.setPosition(BOBBER_BASE_X, BOBBER_BASE_Y + bobberPosition);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // Not needed for this implementation
    }

    @Override
    public void resume() {
        // Not needed for this implementation
    }

    @Override
    public void hide() {
        // Not needed for this implementation
    }

    @Override
    public void dispose() {
        stage.dispose();
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }

    // Getters and setters for fish properties
    public float getFishAcceleration() {
        return fishAcceleration;
    }

    public void setFishAcceleration(float fishAcceleration) {
        this.fishAcceleration = fishAcceleration;
    }

    public void incrementFishAcceleration(float amount) {
        fishAcceleration += amount;
    }

    public float getFishVelocity() {
        return fishVelocity;
    }

    public void setFishVelocity(float fishVelocity) {
        this.fishVelocity = fishVelocity;
    }

    public void incrementFishVelocity(float amount) {
        fishVelocity += amount;
    }

    public float getFishPosition() {
        return fishPosition;
    }

    public void setFishPosition(float fishPosition) {
        this.fishPosition = fishPosition;
    }

    public void incrementFishPosition(float amount) {
        fishPosition += amount;
    }

    public Image getFishImage() {
        return fishImage;
    }

    public Rectangle getFishHitbox() {
        return fishHitbox;
    }
}
