package org.example.client.views.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import org.example.common.models.enums.Weather;

public class LightningSystem {
    private OrthographicCamera camera;
    private boolean isLightningActive;
    private float lightningTimer;
    private float lightningDuration;
    private float lightningIntensity;
    private boolean isThundering;

    // Lightning configuration
    private static final float LIGHTNING_CHANCE = 0.001f; // Chance per frame during stormy weather
    private static final float LIGHTNING_DURATION = 0.3f; // Duration of lightning flash
    private static final float LIGHTNING_FADE_TIME = 0.1f; // Time to fade out
    private static final float THUNDER_DELAY = 0.5f; // Delay before thunder sound of lightning

    // Animation frames
    private float frameTimer;
    private int currentFrame;
    private static final int TOTAL_FRAMES = 8; // Multiple frames for continuous animation
    private static final float FRAME_DURATION = 0.04f; // Faster frame rate for smoother animation

    // Sound effects
    private Sound thunderSound;
    private float thunderTimer;
    private boolean thunderPlayed;

    // Screen flash effect
    private Color flashColor;
    private float flashAlpha;
    private Texture whiteTexture;

    public LightningSystem(OrthographicCamera camera) {
        this.camera = camera;
        this.isLightningActive = false;
        this.lightningTimer = 0f;
        this.lightningDuration = LIGHTNING_DURATION;
        this.lightningIntensity = 0f;
        this.isThundering = false;
        this.frameTimer = 0f;
        this.currentFrame = 0;
        this.thunderTimer = 0f;
        this.thunderPlayed = false;
        this.flashColor = new Color(1f, 1f, 1f, 0f);
        this.flashAlpha = 0f;

        // Create white texture for flash effect
        createWhiteTexture();

        // Load thunder sound
        try {
            thunderSound = Gdx.audio.newSound(Gdx.files.internal("content/effects/storm.mp3"));
        } catch (Exception e) {
            System.err.println("ERROR loading thunder sound: " + e.getMessage());
        }
    }

    public void update(float deltaTime, Weather weather) {
        // Trigger lightning during stormy weather
        if (weather == Weather.STORMY && !isLightningActive) {
            if (MathUtils.random() < LIGHTNING_CHANCE) {
                triggerLightning();
            }
        }

        if (isLightningActive) {
            updateLightning(deltaTime);
        }

        if (isThundering) {
            updateThunder(deltaTime);
        }
    }

    public void triggerLightning() {
        isLightningActive = true;
        lightningTimer = 0f;
        lightningIntensity = 1f;
        currentFrame = 0;
        frameTimer = 0f;
        thunderTimer = 0f;
        thunderPlayed = false;
        flashAlpha = 0.8f;

        System.out.println("⚡ Lightning triggered!");
    }

    private void updateLightning(float deltaTime) {
        lightningTimer += deltaTime;
        frameTimer += deltaTime;

        // Update animation frame continuously
        if (frameTimer >= FRAME_DURATION) {
            currentFrame++;
            frameTimer = 0f;

            // Loop the animation frames for continuous effect
            if (currentFrame >= TOTAL_FRAMES) {
                currentFrame = 0;
            }
        }

        // Calculate lightning intensity based on frame animation
        if (lightningTimer < lightningDuration) {
            // Create dynamic lightning effect based on frame position
            float frameProgress = (float) currentFrame / TOTAL_FRAMES;

            // Create pulsing effect with multiple peaks
            float intensity = 0f;
            if (frameProgress < 0.25f) {
                // First flash
                intensity = MathUtils.sin(frameProgress * 4 * MathUtils.PI) * 0.8f + 0.2f;
            } else if (frameProgress < 0.5f) {
                // Second flash
                intensity = MathUtils.sin((frameProgress - 0.25f) * 4 * MathUtils.PI) * 0.6f + 0.4f;
            } else if (frameProgress < 0.75f) {
                // Third flash
                intensity = MathUtils.sin((frameProgress - 0.5f) * 4 * MathUtils.PI) * 0.4f + 0.6f;
            } else {
                // Final flash
                intensity = MathUtils.sin((frameProgress - 0.75f) * 4 * MathUtils.PI) * 0.2f + 0.8f;
            }

            lightningIntensity = intensity;
            flashAlpha = intensity * 0.9f;
        } else {
            // Lightning ended
            isLightningActive = false;
            lightningIntensity = 0f;
            flashAlpha = 0f;

            // Start thunder
            isThundering = true;
            thunderTimer = 0f;
        }
    }

    private void updateThunder(float deltaTime) {
        thunderTimer += deltaTime;

        if (thunderTimer >= THUNDER_DELAY && !thunderPlayed) {
            playThunderSound();
            thunderPlayed = true;
        }

        if (thunderTimer >= THUNDER_DELAY + 2f) { // Thunder effect duration
            isThundering = false;
        }
    }

    private void playThunderSound() {
        if (thunderSound != null) {
            thunderSound.play(0.7f); // Play at 70% volume
            System.out.println("🌩️ Thunder sound played!");
        }
    }

    public void render(SpriteBatch batch, Color lightingColor) {
        if (isLightningActive && flashAlpha > 0) {
            // Store original batch color
            Color originalColor = batch.getColor();

            // Create lightning flash effect
            Color lightningColor = new Color(1f, 1f, 1f, flashAlpha);
            batch.setColor(lightningColor);

            // Draw full screen flash
            if (camera != null) {
                float screenWidth = camera.viewportWidth;
                float screenHeight = camera.viewportHeight;
                float x = camera.position.x - screenWidth / 2;
                float y = camera.position.y - screenHeight / 2;

                // Draw white rectangle covering entire screen
                batch.draw(whiteTexture, x, y, screenWidth, screenHeight);
            }

            // Restore original color
            batch.setColor(originalColor);
        }
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }

    public boolean isLightningActive() {
        return isLightningActive;
    }

    public boolean isThundering() {
        return isThundering;
    }



    public float getLightningIntensity() {
        return lightningIntensity;
    }

    public void dispose() {
        if (thunderSound != null) {
            thunderSound.dispose();
        }
        if (whiteTexture != null) {
            whiteTexture.dispose();
        }
    }

    private void createWhiteTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whiteTexture = new Texture(pixmap);
        pixmap.dispose();
    }



    public int getCurrentFrame() {
        return currentFrame;
    }

    public int getTotalFrames() {
        return TOTAL_FRAMES;
    }
}
