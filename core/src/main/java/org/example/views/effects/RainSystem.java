package org.example.views.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import org.example.models.enums.Weather;
import org.example.utils.AssetManager;

public class RainSystem {
    private final Array<RainDrop> rainDrops;
    private final Texture[] rainTextures;
    private OrthographicCamera camera;

    private boolean isRaining;
    private float rainIntensity;
    private float rainSpawnTimer;
    private Weather currentWeather;

    // Rain configuration constants
    private static final float RAIN_SPAWN_INTERVAL = 0.008f; // Spawn very frequently
    private static final int MAX_RAIN_DROPS = 500; // Many drops for full coverage
    private static final float RAIN_DROP_MIN_SPEED = 300f;
    private static final float RAIN_DROP_MAX_SPEED = 700f;
    private static final float RAIN_DROP_DIAGONAL_SPEED = -50f;
    private static final float RAIN_DROP_DIAGONAL_VARIANCE = 80f;

    public RainSystem(OrthographicCamera camera) {
        this.camera = camera;
        this.rainDrops = new Array<>();
        this.rainIntensity = 1.0f;
        this.rainSpawnTimer = 0f;
        this.isRaining = false;

        // Load rain textures from AssetManager
        this.rainTextures = new Texture[2];

        try {
            AssetManager assetManager = AssetManager.getAssetManager();
            this.rainTextures[0] = assetManager.getRain1Texture();
            this.rainTextures[1] = assetManager.getRain2Texture();
        } catch (Exception e) {
            System.err.println("ERROR loading rain textures: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Alternative constructor for backwards compatibility
    public RainSystem(float screenWidth, float screenHeight) {
        this.rainDrops = new Array<>();
        this.rainIntensity = 1.0f;
        this.rainSpawnTimer = 0f;
        this.isRaining = false;

        // Load rain textures from AssetManager
        this.rainTextures = new Texture[2];

        try {
            AssetManager assetManager = AssetManager.getAssetManager();
            this.rainTextures[0] = assetManager.getRain1Texture();
            this.rainTextures[1] = assetManager.getRain2Texture();
        } catch (Exception e) {
            System.err.println("ERROR loading rain textures: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }

    public void update(float deltaTime, Weather weather, Color lightingColor) {
        this.currentWeather = weather;
        this.isRaining = (weather == Weather.RAINY || weather == Weather.STORMY);

        adjustRainIntensity(weather);

        if (isRaining) {
            spawnRainDrops(deltaTime);
        }

        updateRainDrops(deltaTime, lightingColor);
        removeOffScreenDrops();
    }

    private void adjustRainIntensity(Weather weather) {
        switch (weather) {
            case RAINY:
                this.rainIntensity = 1.2f;
                break;
            case STORMY:
                this.rainIntensity = 1.8f;
                break;
            default:
                this.rainIntensity = 0f;
                break;
        }
    }

    private void spawnRainDrops(float deltaTime) {
        rainSpawnTimer += deltaTime;

        float adjustedSpawnInterval = RAIN_SPAWN_INTERVAL / rainIntensity;
        int maxDrops = (int) (MAX_RAIN_DROPS * rainIntensity);

        if (rainSpawnTimer >= adjustedSpawnInterval && rainDrops.size < maxDrops) {
            spawnRainDrop();
            rainSpawnTimer = 0f;
        }
    }

    private void spawnRainDrop() {
        if (rainTextures[0] == null || rainTextures[1] == null) {
            return;
        }

        RainDrop drop = new RainDrop();

        // Get camera bounds for the current view
        float cameraLeft, cameraRight, cameraTop, cameraBottom;

        if (camera != null) {
            // Use camera bounds to cover the entire visible area
            cameraLeft = camera.position.x - camera.viewportWidth / 2;
            cameraRight = camera.position.x + camera.viewportWidth / 2;
            cameraTop = camera.position.y + camera.viewportHeight / 2;
            cameraBottom = camera.position.y - camera.viewportHeight / 2;
        } else {
            // Fallback to large area if no camera
            cameraLeft = -1000f;
            cameraRight = 1000f;
            cameraTop = 1000f;
            cameraBottom = -1000f;
        }

        // Spawn across the ENTIRE camera view + extra margin for diagonal movement
        float spawnMargin = 800f; // Large margin to ensure full coverage
        drop.position.x = MathUtils.random(cameraLeft - spawnMargin, cameraRight + spawnMargin);
        drop.position.y = cameraTop + 300f; // Start well above the camera view

        // Set velocity with variation
        float diagonalSpeed = RAIN_DROP_DIAGONAL_SPEED +
            MathUtils.random(-RAIN_DROP_DIAGONAL_VARIANCE, RAIN_DROP_DIAGONAL_VARIANCE);
        float verticalSpeed = MathUtils.random(RAIN_DROP_MIN_SPEED, RAIN_DROP_MAX_SPEED) * rainIntensity;

        drop.velocity.x = diagonalSpeed;
        drop.velocity.y = -verticalSpeed;

        // Random texture selection
        int randomTextureIndex = MathUtils.random(0, 1);
        drop.texture = rainTextures[randomTextureIndex];

        // Set drop properties for visibility
        drop.alpha = MathUtils.random(0.6f, 1.0f);
        drop.scale = MathUtils.random(1.0f, 2.5f);
        drop.rotation = MathUtils.random(-15f, 15f);

        // Wind effects
        if (currentWeather == Weather.STORMY) {
            drop.windEffect = MathUtils.random(-60f, 60f);
        } else {
            drop.windEffect = MathUtils.random(-15f, 15f);
        }

        rainDrops.add(drop);
    }

    private void updateRainDrops(float deltaTime, Color lightingColor) {
        for (RainDrop drop : rainDrops) {
            // Update position
            drop.position.x += drop.velocity.x * deltaTime;
            drop.position.y += drop.velocity.y * deltaTime;

            // Apply wind effect
            drop.position.x += drop.windEffect * deltaTime;

            // Update wind effect over time
            if (currentWeather == Weather.STORMY) {
                drop.windEffect += MathUtils.random(-30f, 30f) * deltaTime;
                drop.windEffect = MathUtils.clamp(drop.windEffect, -100f, 100f);
            } else {
                drop.windEffect += MathUtils.random(-5f, 5f) * deltaTime;
                drop.windEffect = MathUtils.clamp(drop.windEffect, -25f, 25f);
            }

            // Apply gravity
            drop.velocity.y -= 100f * deltaTime;

            // Fade out drops as they fall (optional)
            drop.alpha = Math.max(0.3f, drop.alpha - 0.1f * deltaTime);
        }
    }

    private void removeOffScreenDrops() {
        for (int i = rainDrops.size - 1; i >= 0; i--) {
            RainDrop drop = rainDrops.get(i);

            // Get camera bounds for removal
            float cameraLeft, cameraRight, cameraTop, cameraBottom;

            if (camera != null) {
                cameraLeft = camera.position.x - camera.viewportWidth / 2;
                cameraRight = camera.position.x + camera.viewportWidth / 2;
                cameraTop = camera.position.y + camera.viewportHeight / 2;
                cameraBottom = camera.position.y - camera.viewportHeight / 2;
            } else {
                // Fallback removal bounds
                cameraLeft = -1000f;
                cameraRight = 1000f;
                cameraTop = 1000f;
                cameraBottom = -1000f;
            }

            // Remove drops that are well outside the camera view
            float removalMargin = 1000f;
            if (drop.position.x < cameraLeft - removalMargin ||
                drop.position.x > cameraRight + removalMargin ||
                drop.position.y < cameraBottom - removalMargin ||
                drop.alpha <= 0.1f) {
                rainDrops.removeIndex(i);
            }
        }
    }

    public void render(SpriteBatch batch, Color lightingColor) {
        if (!isRaining || rainDrops.size == 0) return;

        // Store original batch color
        Color originalColor = batch.getColor().cpy();

        for (RainDrop drop : rainDrops) {
            if (drop.texture == null) continue;

            // Only render drops that are visible in the camera view
            if (camera != null) {
                float cameraLeft = camera.position.x - camera.viewportWidth / 2 - 100f;
                float cameraRight = camera.position.x + camera.viewportWidth / 2 + 100f;
                float cameraTop = camera.position.y + camera.viewportHeight / 2 + 100f;
                float cameraBottom = camera.position.y - camera.viewportHeight / 2 - 100f;

                // Skip drops outside camera view
                if (drop.position.x < cameraLeft || drop.position.x > cameraRight ||
                    drop.position.y < cameraBottom || drop.position.y > cameraTop) {
                    continue;
                }
            }

            // Create a highly visible rain color
            Color rainColor = new Color(0.7f, 0.8f, 1.0f, drop.alpha);

            // Apply lighting influence while maintaining visibility
            rainColor.r = Math.max(0.5f, rainColor.r * lightingColor.r);
            rainColor.g = Math.max(0.6f, rainColor.g * lightingColor.g);
            rainColor.b = Math.max(0.8f, rainColor.b * lightingColor.b);

            // Ensure minimum alpha for visibility
            rainColor.a = Math.max(0.5f, drop.alpha);

            batch.setColor(rainColor);

            // Calculate render dimensions
            float width = drop.texture.getWidth() * drop.scale;
            float height = drop.texture.getHeight() * drop.scale;

            // Render the raindrop
            batch.draw(drop.texture,
                drop.position.x - width / 2, drop.position.y - height / 2,
                width / 2, height / 2,  // origin for rotation
                width, height,          // size
                1f, 1f,                 // scale (already applied to width/height)
                drop.rotation,          // rotation
                0, 0,                   // source position
                drop.texture.getWidth(), drop.texture.getHeight(), // source size
                false, false            // flip
            );
        }

        // Restore original color
        batch.setColor(originalColor);
    }

    public void dispose() {
        rainDrops.clear();
    }

    public void resize(float newWidth, float newHeight) {
        // Clear existing drops when resizing
        rainDrops.clear();
    }

    public boolean isRaining() {
        return isRaining;
    }

    public int getRainDropCount() {
        return rainDrops.size;
    }

    public float getRainIntensity() {
        return rainIntensity;
    }

    private static class RainDrop {
        public final Vector2 position = new Vector2();
        public final Vector2 velocity = new Vector2();
        public Texture texture;
        public float alpha;
        public float scale;
        public float rotation;
        public float windEffect;
    }
}
