// main/java/org/example/client/controllers/AnimalSpriteController.java
package org.example.client.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import org.example.common.models.App;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.entities.animal.Animal;
import org.example.common.models.entities.animal.BarnAnimal;
import org.example.common.models.entities.animal.CoopAnimal;
import org.example.common.models.enums.Types.CoopAnimalTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class AnimalSpriteController implements Disposable {
    private final Map<String, Animation<TextureRegion>> animations;
    private final Texture textureSheet;
    private float stateTime = 0f;

    private static final float FRAME_DURATION = 0.2f;

    public AnimalSpriteController() {
        this.animations = new HashMap<>();
        // textureSheet is loaded dynamically for each animal type in render method
        this.textureSheet = null;
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
        // The actual animation logic will be handled in the render method
        // based on each animal's state.
    }

    public void render(SpriteBatch batch, Color lightingColor) {
        if (App.getGame() == null || App.getGame().getGameMap() == null) return;

        List<Animal> allAnimals = new ArrayList<>();
        for (Farm farm : App.getGame().getGameMap().getFarms()) {
            farm.getBarns().forEach(barn -> allAnimals.addAll(barn.getAnimals()));
            farm.getCoops().forEach(coop -> allAnimals.addAll(coop.getAnimals()));
        }

        batch.setColor(lightingColor);
        for (Animal animal : allAnimals) {
            if (animal.isMoving()) {
                // Simplified logic: determine direction based on target or velocity
                // For now, we'll just use a default "walk" animation
                renderAnimal(batch, animal, "walk");
            } else {
                renderAnimal(batch, animal, "idle");
            }
        }
        batch.setColor(Color.WHITE);
    }


    private void renderAnimal(SpriteBatch batch, Animal animal, String animationType) {
        String animalName = animal.getName();
        Animation<TextureRegion> animation = getAnimation(animalName, animationType);
        if (animation == null) {
            animation = createAndCacheAnimation(animalName, animationType);
        }

        if (animation != null) {
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
            float RENDER_W = 48; // Standard render width
            float RENDER_H = 48; // Standard render height
            batch.draw(currentFrame, animal.getPosX(), animal.getPosY(), RENDER_W, RENDER_H);
        }
    }


    private Animation<TextureRegion> getAnimation(String animalName, String animationType) {
        return animations.get(animalName + "_" + animationType);
    }

    private Animation<TextureRegion> createAndCacheAnimation(String animalName, String animationType) {
        try {
            Texture sheet = new Texture(Gdx.files.internal("content/Animals/" + animalName + ".png"));
            int FRAME_W = 16;
            int FRAME_H = 16;
            if(animalName.equals("Cow") || animalName.equals("Sheep") || animalName.equals("Pig") || animalName.equals("Goat")){
                FRAME_W = 32;
                FRAME_H = 32;
            }


            TextureRegion[][] grid = TextureRegion.split(sheet, FRAME_W, FRAME_H);

            // Define rows for different animations (this is an example, adjust for your sprite sheets)
            int row_index = 0; // Default to first row
            if (animationType.equals("walk")) {
                row_index = 0; // Assuming first row is walk/idle
            } else if (animationType.equals("idle")) {
                row_index = 1;
            }


            Array<TextureRegion> frames = new Array<>();
            for (int i = 0; i < grid[row_index].length; i++) {
                frames.add(grid[row_index][i]);
            }

            Animation<TextureRegion> animation = new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
            animations.put(animalName + "_" + animationType, animation);
            return animation;
        } catch (Exception e) {
            System.err.println("Failed to create animation for " + animalName + " (" + animationType + "): " + e.getMessage());
            return null;
        }
    }


    @Override
    public void dispose() {
        for (Animation<TextureRegion> animation : animations.values()) {
            for (TextureRegion frame : animation.getKeyFrames()) {
                frame.getTexture().dispose();
            }
        }
        animations.clear();
        if (textureSheet != null) {
            textureSheet.dispose();
        }
    }
}
