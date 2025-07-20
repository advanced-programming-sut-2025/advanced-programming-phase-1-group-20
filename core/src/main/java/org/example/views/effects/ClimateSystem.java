package org.example.views.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import org.example.common.models.enums.Weather;
import org.example.utils.AssetManager;

public class ClimateSystem {
    private final Array<RainDrop> rainDrops;
    private final Texture[] rainTextures;
    private OrthographicCamera camera;

    private boolean isRaining;
    private float rainIntensity;
    private float rainSpawnTimer;
    private Weather currentWeather;

    // Rain configuration constants
    private static final float RAIN_SPAWN_INTERVAL = 0.008f; // Spawn very frequently
    private static final int MAX_RAIN_DROPS = 1200; // Increased for denser rain
    private static final float RAIN_DROP_MIN_SPEED = 300f;
    private static final float RAIN_DROP_MAX_SPEED = 700f;
    private static final float RAIN_DROP_DIAGONAL_SPEED = -50f;
    private static final float RAIN_DROP_DIAGONAL_VARIANCE = 80f;
    private static final float SPAWN_MARGIN = 1600f; // Increased margin for wider coverage

    private Music rainMusic;
    private Music stormMusic;
    private boolean wasRaining = false;
    private Weather lastWeather = null;

    private final Array<SnowDrop> snowDrops = new Array<>();
    private final Texture[] snowTextures;
    private boolean isSnowing;
    private float snowSpawnTimer;
    private static final float SNOW_SPAWN_INTERVAL = 0.012f;
    private static final int MAX_SNOW_DROPS = 600;
    private static final float SNOW_DROP_MIN_SPEED = 60f;
    private static final float SNOW_DROP_MAX_SPEED = 120f;
    private static final float SNOW_SPAWN_MARGIN = 1600f;

    public ClimateSystem(OrthographicCamera camera) {
        this.camera = camera;
        this.rainDrops = new Array<>();
        this.rainIntensity = 1.0f;
        this.rainSpawnTimer = 0f;
        this.isRaining = false;
        this.wasRaining = false;
        this.lastWeather = null;
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
        // Load rain and storm music
        try {
            rainMusic = Gdx.audio.newMusic(Gdx.files.internal("content/effects/rain.mp3"));
            stormMusic = Gdx.audio.newMusic(Gdx.files.internal("content/effects/storm.mp3"));
            rainMusic.setLooping(true);
            stormMusic.setLooping(true);
        } catch (Exception e) {
            System.err.println("ERROR loading rain/storm music: " + e.getMessage());
        }
        AssetManager assetManager = AssetManager.getAssetManager();
        this.snowTextures = assetManager.getSnowTextures();
    }

    // Alternative constructor for backwards compatibility
    public ClimateSystem(float screenWidth, float screenHeight) {
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
        AssetManager assetManager = AssetManager.getAssetManager();
        this.snowTextures = assetManager.getSnowTextures();
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }

    public void update(float deltaTime, Weather weather, Color lightingColor) {
        this.currentWeather = weather;
        this.isRaining = (weather == Weather.RAINY || weather == Weather.STORMY);
        this.isSnowing = (weather == Weather.SNOWY);
        // Handle rain/storm sound
        handleRainSound(weather);
        adjustRainIntensity(weather);

        if (isRaining) {
            spawnRainDrops(deltaTime);
        }
        if (isSnowing) {
            spawnSnowDrops(deltaTime);
        }
        updateRainDrops(deltaTime, lightingColor);
        updateSnowDrops(deltaTime, lightingColor);
        removeOffScreenDrops();
        removeOffScreenSnowDrops();
        this.wasRaining = isRaining;
        this.lastWeather = weather;
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
        drop.position.x = MathUtils.random(cameraLeft - SPAWN_MARGIN, cameraRight + SPAWN_MARGIN);
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

    private void spawnSnowDrops(float deltaTime) {
        snowSpawnTimer += deltaTime;
        if (snowSpawnTimer >= SNOW_SPAWN_INTERVAL && snowDrops.size < MAX_SNOW_DROPS) {
            spawnSnowDrop();
            snowSpawnTimer = 0f;
        }
    }
    private void spawnSnowDrop() {
        if (snowTextures == null || snowTextures.length < 4) return;
        SnowDrop drop = new SnowDrop();
        float cameraLeft, cameraRight, cameraTop;
        if (camera != null) {
            cameraLeft = camera.position.x - camera.viewportWidth / 2;
            cameraRight = camera.position.x + camera.viewportWidth / 2;
            cameraTop = camera.position.y + camera.viewportHeight / 2;
        } else {
            cameraLeft = -1000f;
            cameraRight = 1000f;
            cameraTop = 1000f;
        }
        drop.position.x = MathUtils.random(cameraLeft - SNOW_SPAWN_MARGIN, cameraRight + SNOW_SPAWN_MARGIN);
        drop.position.y = cameraTop + 200f;
        float verticalSpeed = MathUtils.random(SNOW_DROP_MIN_SPEED, SNOW_DROP_MAX_SPEED);
        drop.velocity.x = MathUtils.random(-20f, 20f); // gentle horizontal drift
        drop.velocity.y = -verticalSpeed;
        int randomTextureIndex = MathUtils.random(0, 3);
        drop.texture = snowTextures[randomTextureIndex];
        drop.alpha = MathUtils.random(0.7f, 1.0f);
        // Special case for 1.png (index 1): make it much smaller
        if (randomTextureIndex == 1) {
            drop.scale = MathUtils.random(0.005f, 0.02f);
        } else {
            drop.scale = MathUtils.random(0.05f, 0.15f);
        }
        drop.rotation = MathUtils.random(-10f, 10f);
        drop.windEffect = MathUtils.random(-10f, 10f);
        snowDrops.add(drop);
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

    private void updateSnowDrops(float deltaTime, Color lightingColor) {
        for (SnowDrop drop : snowDrops) {
            drop.position.x += drop.velocity.x * deltaTime;
            drop.position.y += drop.velocity.y * deltaTime;
            drop.position.x += drop.windEffect * deltaTime;
            drop.windEffect += MathUtils.random(-2f, 2f) * deltaTime;
            drop.windEffect = MathUtils.clamp(drop.windEffect, -20f, 20f);
            drop.velocity.y -= 10f * deltaTime; // very light gravity
            drop.alpha = Math.max(0.3f, drop.alpha - 0.03f * deltaTime);
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

    private void removeOffScreenSnowDrops() {
        for (int i = snowDrops.size - 1; i >= 0; i--) {
            SnowDrop drop = snowDrops.get(i);
            float cameraLeft, cameraRight, cameraBottom;
            if (camera != null) {
                cameraLeft = camera.position.x - camera.viewportWidth / 2;
                cameraRight = camera.position.x + camera.viewportWidth / 2;
                cameraBottom = camera.position.y - camera.viewportHeight / 2;
            } else {
                cameraLeft = -1000f;
                cameraRight = 1000f;
                cameraBottom = -1000f;
            }
            float removalMargin = 1000f;
            if (drop.position.x < cameraLeft - removalMargin ||
                drop.position.x > cameraRight + removalMargin ||
                drop.position.y < cameraBottom - removalMargin ||
                drop.alpha <= 0.1f) {
                snowDrops.removeIndex(i);
            }
        }
    }

    public void render(SpriteBatch batch, Color lightingColor) {
        // Remove the early return so both rain and snow can render independently
        // Store original batch color
        Color originalColor = batch.getColor().cpy();

        if (isRaining && rainDrops.size > 0) {
            for (RainDrop drop : rainDrops) {
                if (drop.texture == null) continue;
                if (camera != null) {
                    float cameraLeft = camera.position.x - camera.viewportWidth / 2 - 100f;
                    float cameraRight = camera.position.x + camera.viewportWidth / 2 + 100f;
                    float cameraTop = camera.position.y + camera.viewportHeight / 2 + 100f;
                    float cameraBottom = camera.position.y - camera.viewportHeight / 2 - 100f;
                    if (drop.position.x < cameraLeft || drop.position.x > cameraRight ||
                        drop.position.y < cameraBottom || drop.position.y > cameraTop) {
                        continue;
                    }
                }
                Color rainColor = new Color(0.7f, 0.8f, 1.0f, drop.alpha);
                rainColor.r = Math.max(0.5f, rainColor.r * lightingColor.r);
                rainColor.g = Math.max(0.6f, rainColor.g * lightingColor.g);
                rainColor.b = Math.max(0.8f, rainColor.b * lightingColor.b);
                rainColor.a = Math.max(0.5f, drop.alpha);
                batch.setColor(rainColor);
                float width = drop.texture.getWidth() * drop.scale;
                float height = drop.texture.getHeight() * drop.scale;
                batch.draw(drop.texture,
                    drop.position.x - width / 2, drop.position.y - height / 2,
                    width / 2, height / 2,
                    width, height,
                    1f, 1f,
                    drop.rotation,
                    0, 0,
                    drop.texture.getWidth(), drop.texture.getHeight(),
                    false, false
                );
            }
        }

        if (isSnowing && snowDrops.size > 0) {
            for (SnowDrop drop : snowDrops) {
                if (drop.texture == null) continue;
                if (camera != null) {
                    float cameraLeft = camera.position.x - camera.viewportWidth / 2 - 100f;
                    float cameraRight = camera.position.x + camera.viewportWidth / 2 + 100f;
                    float cameraTop = camera.position.y + camera.viewportHeight / 2 + 100f;
                    float cameraBottom = camera.position.y - camera.viewportHeight / 2 - 100f;
                    if (drop.position.x < cameraLeft || drop.position.x > cameraRight ||
                        drop.position.y < cameraBottom || drop.position.y > cameraTop) {
                        continue;
                    }
                }
                Color snowColor = new Color(1.0f, 1.0f, 1.0f, drop.alpha);
                snowColor.r = Math.max(0.8f, snowColor.r * lightingColor.r);
                snowColor.g = Math.max(0.8f, snowColor.g * lightingColor.g);
                snowColor.b = Math.max(0.9f, snowColor.b * lightingColor.b);
                snowColor.a = Math.max(0.5f, drop.alpha);
                batch.setColor(snowColor);
                float width = drop.texture.getWidth() * drop.scale;
                float height = drop.texture.getHeight() * drop.scale;
                batch.draw(drop.texture,
                    drop.position.x - width / 2, drop.position.y - height / 2,
                    width / 2, height / 2,
                    width, height,
                    1f, 1f,
                    drop.rotation,
                    0, 0,
                    drop.texture.getWidth(), drop.texture.getHeight(),
                    false, false
                );
            }
        }
        // Restore original color
        batch.setColor(originalColor);
    }

    public void dispose() {
        rainDrops.clear();
        snowDrops.clear();
        if (rainMusic != null) rainMusic.dispose();
        if (stormMusic != null) stormMusic.dispose();
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

    private void handleRainSound(Weather weather) {
        boolean shouldRain = (weather == Weather.RAINY);
        boolean shouldStorm = (weather == Weather.STORMY);
        if ((shouldRain || shouldStorm) && !wasRaining) {
            // Start appropriate sound
            if (shouldRain && rainMusic != null) {
                rainMusic.play();
            } else if (shouldStorm && stormMusic != null) {
                stormMusic.play();
            }
        } else if (!shouldRain && !shouldStorm && wasRaining) {
            // Stop all rain sounds
            if (rainMusic != null) rainMusic.stop();
            if (stormMusic != null) stormMusic.stop();
        } else if (shouldRain && lastWeather == Weather.STORMY) {
            // Switch from storm to rain
            if (stormMusic != null) stormMusic.stop();
            if (rainMusic != null) rainMusic.play();
        } else if (shouldStorm && lastWeather == Weather.RAINY) {
            // Switch from rain to storm
            if (rainMusic != null) rainMusic.stop();
            if (stormMusic != null) stormMusic.play();
        }
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

    private static class SnowDrop {
        public final Vector2 position = new Vector2();
        public final Vector2 velocity = new Vector2();
        public Texture texture;
        public float alpha;
        public float scale;
        public float rotation;
        public float windEffect;
    }
}
