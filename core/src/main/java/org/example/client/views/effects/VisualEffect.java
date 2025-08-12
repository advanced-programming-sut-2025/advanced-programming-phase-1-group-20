package org.example.client.views.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;

public class VisualEffect {
    private final Texture texture;
    private final float x;
    private final float y;
    private final float duration;
    private float timer;
    private boolean finished = false;

    public VisualEffect(Texture texture, float x, float y, float duration) {
        this.texture = texture;
        // Adjust coordinates to center the effect on the animal
        this.x = x - texture.getWidth() / 2f;
        this.y = y;
        this.duration = duration;
        this.timer = 0f;
    }

    public void update(float delta) {
        if (finished) return;
        timer += delta;
        if (timer >= duration) {
            finished = true;
        }
    }

    public void draw(SpriteBatch batch) {
        if (finished) return;

        // Calculate alpha for fade-in and fade-out
        float halfDuration = duration / 2.0f;
        float alpha;
        if (timer < halfDuration) {
            // Fade in for the first half of the duration
            alpha = Interpolation.fade.apply(timer / halfDuration);
        } else {
            // Fade out for the second half
            alpha = Interpolation.fade.apply((duration - timer) / halfDuration);
        }

        // Apply the alpha and draw the texture
        Color oldColor = batch.getColor().cpy();
        batch.setColor(1, 1, 1, alpha);
        batch.draw(texture, this.x, this.y);
        batch.setColor(oldColor); // Reset to original color
    }

    public boolean isFinished() {
        return finished;
    }
}
