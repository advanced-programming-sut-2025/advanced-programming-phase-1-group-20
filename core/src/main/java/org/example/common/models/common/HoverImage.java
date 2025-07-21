// Create a new file: org/example/client/views/HoverImage.java
package org.example.common.models.common;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class HoverImage extends Image {
    private final Texture defaultTexture;
    private final Texture hoverTexture;

    public HoverImage(Texture defaultTexture, Texture hoverTexture) {
        super(defaultTexture);
        this.defaultTexture = defaultTexture;
        this.hoverTexture = hoverTexture;

        addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor fromActor) {
                if (pointer == -1) { // -1 indicates a hover event
                    setDrawable(new TextureRegionDrawable(HoverImage.this.hoverTexture));
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                if (pointer == -1) { // -1 indicates a hover event
                    setDrawable(new TextureRegionDrawable(HoverImage.this.defaultTexture));
                }
            }
        });
    }

    // You can add a dispose method here if you manage textures within this class
    // For now, it's fine if they are managed externally in CraftingScreen
}
