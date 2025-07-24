package org.example.client.views.fishing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import org.example.common.models.enums.Types.Quality;
import org.example.common.models.enums.Types.FishType;

public class FishingMiniGame implements Screen, InputProcessor {
    private final float BASE_SPEED_FACTOR = 3;
    private final float MAX_PROGRESS = 10;
    private final float MIN_PROGRESS = 0;
    private final float MAX_BOBBER_POS = 325;
    private final float MIN_BOBBER_POS = 0;

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
    private ProgressBar catchingProgressBar;

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
        FishingController.determineFishMotionType(caughtFishType.isLegendary());
    }

    private void getAnglingResults() {
        var query = FishingController.queryAnglingResult(poleName);
        caughtFishQuality = (Quality) query.get("quality");
        caughtFishType = (FishType) query.get("type");
        caughtFishQuantity = (Integer) query.get("quantity");
        xpGained = (Integer) query.get("xp");
    }

    private void initializeStage() {
        stage = new Stage(new ScreenViewport());

        // Use a simple colored background instead of texture
        Table background = new Table();
        background.setFillParent(true);
        background.setBackground(new com.badlogic.gdx.scenes.scene2d.utils.Drawable() {
            @Override
            public void draw(com.badlogic.gdx.graphics.g2d.Batch batch, float x, float y, float width, float height) {
                batch.setColor(0.2f, 0.4f, 0.8f, 1f);
                com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
                pixmap.setColor(0.2f, 0.4f, 0.8f, 1f);
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
        });
        stage.addActor(background);

        // Create water lane as a colored rectangle
        waterLane = new Image();
        waterLane.setSize(400, 200);
        waterLane.setPosition(stage.getWidth() / 2 - waterLane.getWidth() / 2, stage.getHeight() / 2 - waterLane.getHeight() / 2);
        waterLane.setColor(0.1f, 0.3f, 0.7f, 0.8f);
        stage.addActor(waterLane);

        // Create bobber as a colored circle
        bobberImage = new Image();
        bobberImage.setSize(20, 20);
        bobberImage.setColor(1f, 0.5f, 0f, 1f); // Orange color

        BOBBER_BASE_X = stage.getWidth() / 2 - waterLane.getWidth() / 2 + 85;
        BOBBER_BASE_Y = stage.getHeight() / 2 - waterLane.getHeight() / 2 + 20;
        bobberImage.setPosition(BOBBER_BASE_X, BOBBER_BASE_Y);

        bobberHitbox = new Rectangle(BOBBER_BASE_X, BOBBER_BASE_Y, bobberImage.getWidth(), bobberImage.getHeight());
        stage.addActor(bobberImage);

        // Create fish as a colored rectangle
        fishImage = new Image();
        fishImage.setSize(30, 15);
        fishImage.setColor(0.8f, 0.2f, 0.2f, 1f); // Red color
        fishImage.setPosition(BOBBER_BASE_X, BOBBER_BASE_Y);
        stage.addActor(fishImage);

        fishHitbox = new Rectangle(BOBBER_BASE_X, BOBBER_BASE_Y, fishImage.getWidth(), fishImage.getHeight());

        // Create instruction label with new text
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.fontColor = Color.WHITE;
        labelStyle.font = new BitmapFont();

        Label label = new Label("Master the waters! Use ↑↓ arrows to guide your lure. Press Q to abandon.", labelStyle);
        stage.addActor(label);
        label.setPosition(stage.getWidth() / 2 - label.getWidth() / 2, stage.getHeight() / 2 + waterLane.getHeight() / 2 + 50);

        // Create progress bar
        Skin progressSkin = new Skin();
        catchingProgressBar = new ProgressBar(MIN_PROGRESS, MAX_PROGRESS, 0.01f, true, progressSkin);
        catchingProgressBar.setSize(200, 20);
        catchingProgressBar.setPosition(waterLane.getX() + waterLane.getWidth() + 20, waterLane.getY() + waterLane.getHeight() / 2);
        stage.addActor(catchingProgressBar);
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
            FishingController.handleFishAI(this, delta);
            incrementProgress(delta);
            checkForWinOrLoss();
        }

        stage.act(delta);
        stage.draw();
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
        
        // Create background
        Table background = new Table();
        background.setFillParent(true);
        background.setBackground(new com.badlogic.gdx.scenes.scene2d.utils.Drawable() {
            @Override
            public void draw(com.badlogic.gdx.graphics.g2d.Batch batch, float x, float y, float width, float height) {
                batch.setColor(0.1f, 0.1f, 0.3f, 1f);
                com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
                pixmap.setColor(0.1f, 0.1f, 0.3f, 1f);
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
        });
        stage.addActor(background);

        Table table = new Table();
        table.setFillParent(true);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.fontColor = Color.WHITE;
        labelStyle.font = new BitmapFont();

        String resultText;
        if (isVictorious) {
            resultText = "🎣 Splendid catch! You've mastered the waters!";
            resultText += "\nExperience gained: " + xpGained + " points";
            resultText += "\nHarvested: " + caughtFishQuantity + "x " + caughtFishType.getName();
            resultText += "\nQuality: " + getQualityDescription(caughtFishQuality);
        } else {
            resultText = "🌊 The fish got away this time...";
            resultText += "\nDon't give up, angler!";
        }
        
        Label label = new Label(resultText, labelStyle);
        Label label1 = new Label(isCatchPerfect ? "✨ Flawless technique! ✨" : "Decent effort, keep practicing!", labelStyle);

        TextButton backButton = new TextButton("Return to Farm", new Skin());
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                Main.getGame().setScreen(gameView);
            }
        });

        table.center();
        table.add(label);
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
        // TODO: Implement server notification
    }

    private void incrementProgress(float delta) {
        if (fishHitbox.overlaps(bobberHitbox)) {
            catchingProgress += delta;
        } else {
            catchingProgress -= delta * 0.5f;
            isCatchPerfect = false;
        }
        catchingProgress = MathUtils.clamp(catchingProgress, MIN_PROGRESS, MAX_PROGRESS);
        catchingProgressBar.setValue(catchingProgress);
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