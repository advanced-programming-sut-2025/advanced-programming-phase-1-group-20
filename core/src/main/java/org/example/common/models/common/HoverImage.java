package org.example.common.models.common;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * An Image that displays a tooltip Actor when hovered over.
 * The tooltip is added directly to the stage to appear on top of other UI elements.
 */
public class HoverImage extends Image {

    // The actor to be displayed as a tooltip. Can be any actor, not just an Image.
    private final Actor tooltip;

    /**
     * @param defaultTexture The texture for the main image that triggers the hover effect.
     * @param tooltipTexture The texture for the tooltip pop-up.
     */
    public HoverImage(Texture defaultTexture, Texture tooltipTexture) {
        this(defaultTexture, tooltipTexture, 120f);
    }

    /**
     * @param defaultTexture The texture for the main image that triggers the hover effect.
     * @param tooltipTexture The texture for the tooltip pop-up.
     * @param maxSize The maximum size (width or height) for the tooltip in pixels.
     */
    public HoverImage(Texture defaultTexture, Texture tooltipTexture, float maxSize) {
        super(defaultTexture);

        // This is the tooltip actor that will pop up.
        this.tooltip = new Image(tooltipTexture);
        this.tooltip.setVisible(false); // The tooltip is hidden by default.
        
        // Scale the tooltip to a reasonable size
        float scale = Math.min(maxSize / tooltipTexture.getWidth(), maxSize / tooltipTexture.getHeight());
        this.tooltip.setSize(tooltipTexture.getWidth() * scale, tooltipTexture.getHeight() * scale);

        addListener(new InputListener() {
            /**
             * Called when the mouse enters the bounds of this actor.
             */
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                // We only care about mouse hover, not touch events.
                if (pointer != -1) return;

                Stage stage = getStage();
                if (stage == null) return;

                // Add the tooltip to the stage so it can be drawn anywhere, on top of everything.
                stage.addActor(tooltip);
                tooltip.setVisible(true);

                // Position the tooltip near the cursor. The 15px offset prevents it from being directly under the pointer.
                positionTooltip(event.getStageX(), event.getStageY());
            }

            /**
             * Called when the mouse exits the bounds of this actor.
             */
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer != -1) return;
                // Hide and remove the tooltip. Removing it is cleaner than just hiding it.
                tooltip.setVisible(false);
                tooltip.remove();
            }

            /**
             * Called when the mouse moves within the bounds of this actor.
             */
            @Override
            public boolean mouseMoved(InputEvent event, float x, float y) {
                // If the tooltip is visible, update its position to follow the mouse.
                if (tooltip.isVisible()) {
                    positionTooltip(event.getStageX(), event.getStageY());
                }
                return true;
            }

            /**
             * Positions the tooltip and ensures it stays within the screen bounds.
             */
            private void positionTooltip(float stageX, float stageY) {
                Stage stage = getStage();
                if (stage == null) return;

                // Position tooltip to the right and above the cursor
                tooltip.setPosition(stageX + 15f, stageY + 15f);

                // If it goes off the right edge of the screen, move it to the left of the cursor.
                if (tooltip.getRight() > stage.getWidth()) {
                    tooltip.setX(stageX - tooltip.getWidth() - 15f);
                }
                // If it goes off the top edge of the screen, move it below the cursor.
                if (tooltip.getTop() > stage.getHeight()) {
                    tooltip.setY(stageY - tooltip.getHeight() - 15f);
                }
            }
        });
    }
}
