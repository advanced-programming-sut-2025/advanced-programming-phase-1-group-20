package org.example.client.views.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Array;
import org.example.common.models.App;
import org.example.common.models.common.Date;
import org.example.common.models.common.Location;
import org.example.common.models.entities.Game;

public class LightningSystem implements Disposable {
    private static final float LIGHTNING_DURATION = 0.4f; // Increased duration for more dramatic effect
    private static final float LIGHTNING_FADE_TIME = 0.15f; // Slower fade for better visibility
    private static final float LIGHTNING_INTENSITY = 1.5f; // Increased intensity
    private static final float LIGHTNING_CHANCE = 0.8f; // 80% chance per second during storm
    private static final float LIGHTNING_COOLDOWN = 0.2f; // Faster lightning strikes
    private static final float THOR_CHANCE = 0.3f; // 30% chance that lightning affects the map
    private static final int MAX_LIGHTNING_STRIKES = 3; // Multiple lightning strikes

    private Texture lightningTexture;
    private float lightningTimer = 0f;
    private float lightningAlpha = 0f;
    private boolean isLightningActive = false;
    private Color lightningColor;
    private float screenWidth;
    private float screenHeight;
    private float cooldownTimer = 0f;
    private int lightningCount = 0;
    
    // Enhanced lightning system
    private Array<LightningStrike> activeStrikes;
    private float stormIntensity = 0f;
    private boolean isStormActive = false;
    private float stormTimer = 0f;
    private float nextStrikeTimer = 0f;
    private int totalStrikes = 0;

    public LightningSystem() {
        createLightningTexture();
        lightningColor = new Color(1f, 1f, 1f, 0f);
        activeStrikes = new Array<>();
    }

    private void createLightningTexture() {
        // Create a larger, more detailed lightning texture
        Pixmap pixmap = new Pixmap(128, 128, Pixmap.Format.RGBA8888);
        
        // Create a lightning bolt pattern
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        
        // Add some variation to make it look more like lightning
        for (int i = 0; i < 20; i++) {
            int x = MathUtils.random(0, 127);
            int y = MathUtils.random(0, 127);
            int size = MathUtils.random(5, 15);
            pixmap.setColor(new Color(0.9f, 0.95f, 1f, 0.8f));
            pixmap.fillCircle(x, y, size);
        }
        
        lightningTexture = new Texture(pixmap);
        lightningTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();
    }

    public void update(float deltaTime) {
        lightningTimer += deltaTime;
        cooldownTimer += deltaTime;
        stormTimer += deltaTime;
        nextStrikeTimer += deltaTime;

        // Update active lightning strikes
        for (int i = activeStrikes.size - 1; i >= 0; i--) {
            LightningStrike strike = activeStrikes.get(i);
            strike.update(deltaTime);
            
            if (strike.isFinished()) {
                activeStrikes.removeIndex(i);
            }
        }

        if (isStormActive) {
            // During storm, create multiple lightning strikes
            if (nextStrikeTimer >= LIGHTNING_COOLDOWN && activeStrikes.size < MAX_LIGHTNING_STRIKES) {
                createLightningStrike();
                nextStrikeTimer = 0f;
            }
            
            // End storm after some time
            if (stormTimer > 8f) { // Longer storm duration
                isStormActive = false;
                stormIntensity = 0f;
                stormTimer = 0f;
            }
        } else if (cooldownTimer >= LIGHTNING_COOLDOWN) {
            // Check if we should trigger lightning (during storm weather)
            if (MathUtils.random() < LIGHTNING_CHANCE * deltaTime) {
                triggerLightning();
            }
        }

        // Update lightning color
        lightningColor.a = lightningAlpha * LIGHTNING_INTENSITY;
    }

    // Method to check if storm weather is active and trigger lightning accordingly
    public void updateForWeather(org.example.common.models.enums.Weather weather, float deltaTime) {
        if (weather == org.example.common.models.enums.Weather.STORMY) {
            // During storm weather, increase lightning chance
            if (MathUtils.random() < 0.3f * deltaTime) { // 30% chance per second during storm
                triggerLightning();
            }
        }
    }

    private void createLightningStrike() {
        LightningStrike strike = new LightningStrike();
        strike.duration = MathUtils.random(0.3f, 0.8f); // Longer duration for more dramatic effect
        strike.intensity = MathUtils.random(1.0f, 1.5f); // Higher intensity
        strike.x = MathUtils.random(0, screenWidth);
        strike.y = MathUtils.random(screenHeight * 0.5f, screenHeight); // Cover more of the screen
        strike.width = MathUtils.random(100, 300); // Wider lightning
        strike.height = MathUtils.random(200, 500); // Taller lightning
        
        activeStrikes.add(strike);
        totalStrikes++;
        
        // Sometimes lightning affects the map (Thor's wrath!)
        if (MathUtils.random() < THOR_CHANCE) {
            triggerThorEffect();
        }
    }

    public void triggerLightning() {
        isLightningActive = true;
        lightningAlpha = 1f;
        lightningTimer = 0f;
        cooldownTimer = 0f;
        lightningCount++;
        
        // Start a storm sequence
        isStormActive = true;
        stormIntensity = 1f;
        stormTimer = 0f;
        nextStrikeTimer = 0f;
        
        // Create initial lightning strike
        createLightningStrike();
        
        // Add some randomness to lightning intensity
        float intensityVariation = MathUtils.random(0.7f, 1.0f);
        lightningColor.a = lightningAlpha * LIGHTNING_INTENSITY * intensityVariation;
    }

    private void triggerThorEffect() {
        try {
            // Get current game and player
            org.example.common.models.entities.Game game = App.getGame();
            if (game != null && game.getCurrentPlayer() != null) {
                // Get player's current location
                Location playerLocation = game.getCurrentPlayer().getLocation();
                if (playerLocation != null) {
                    // Get current date to call cheatThor
                    Date currentDate = game.getDate();
                    if (currentDate != null) {
                        // Call cheatThor with player's location
                        currentDate.cheatThor(playerLocation);
                    }
                }
            }
        } catch (Exception e) {
            // Silent error handling
        }
    }

    public void render(SpriteBatch batch) {
        if (activeStrikes.size > 0 || lightningAlpha > 0) {
            // Store original OpenGL state
            boolean wasBlendingEnabled = Gdx.gl.glIsEnabled(Gdx.gl.GL_BLEND);
            
            // Enable blending for lightning effect
            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            // Use additive blending for more dramatic lightning effect
            Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE);
            
            Color originalColor = batch.getColor().cpy();

            // Render each active lightning strike
            for (LightningStrike strike : activeStrikes) {
                if (strike.alpha > 0) {
                    Color lightningTint = new Color(0.9f, 0.95f, 1f, strike.alpha * strike.intensity);
                    batch.setColor(lightningTint);
                    
                    // Draw lightning bolt
                    batch.draw(lightningTexture, 
                        strike.x - strike.width/2, strike.y - strike.height/2, 
                        strike.width, strike.height);
                }
            }

            // Render overall screen flash - more dramatic
            if (lightningAlpha > 0) {
                Color screenFlash = new Color(0.8f, 0.85f, 1f, lightningAlpha * 0.5f);
                batch.setColor(screenFlash);
                batch.draw(lightningTexture, 0, 0, screenWidth, screenHeight);
            }

            // Restore original batch color
            batch.setColor(originalColor);
            
            // Restore original OpenGL state
            if (wasBlendingEnabled) {
                Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            } else {
                Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
            }
            // Restore normal blending
            Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    public void setScreenDimensions(float width, float height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    public boolean isLightningActive() {
        return isLightningActive || activeStrikes.size > 0;
    }

    public float getLightningIntensity() {
        return lightningAlpha * LIGHTNING_INTENSITY;
    }

    public int getLightningCount() {
        return lightningCount;
    }

    public int getTotalStrikes() {
        return totalStrikes;
    }

    public boolean isStormActive() {
        return isStormActive;
    }

    @Override
    public void dispose() {
        if (lightningTexture != null) {
            lightningTexture.dispose();
        }
        activeStrikes.clear();
    }

    private static class LightningStrike {
        public float x, y, width, height;
        public float duration;
        public float intensity;
        public float alpha = 1f;
        public float timer = 0f;
        
        public void update(float deltaTime) {
            timer += deltaTime;
            float progress = timer / duration;
            
            if (progress < 0.2f) {
                // Initial flash
                alpha = 1f;
            } else if (progress < 0.4f) {
                // Fade out
                alpha = 1f - (progress - 0.2f) / 0.2f;
            } else if (progress < 0.6f) {
                // Second flash
                alpha = 0.7f;
            } else if (progress < 0.8f) {
                // Fade out again
                alpha = 0.7f - (progress - 0.6f) / 0.2f * 0.7f;
            } else {
                // Final fade out
                alpha = 0f;
            }
        }
        
        public boolean isFinished() {
            return timer >= duration;
        }
    }
}
