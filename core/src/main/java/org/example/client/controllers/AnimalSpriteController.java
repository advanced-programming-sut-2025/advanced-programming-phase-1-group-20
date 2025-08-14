// main/java/org/example/client/controllers/AnimalSpriteController.java
package org.example.client.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import org.example.common.models.entities.animal.Animal;
import org.example.common.models.enums.Types.CoopAnimalTypes;

import java.util.HashMap;
import java.util.Map;

public class AnimalSpriteController implements Disposable {
    private static final float FRAME_DURATION = 0.2f;

    private static class AnimSet {
        Texture texture;
        Animation<TextureRegion> moveUp;
        Animation<TextureRegion> moveDown;
        Animation<TextureRegion> moveLeft;
        Animation<TextureRegion> moveRight;
        Animation<TextureRegion> doSomething;
    }

    private final String baseAnimalName;
    private final Map<String, AnimSet> spriteNameToAnims;

    public AnimalSpriteController(String animalName) {
        this.baseAnimalName = animalName;
        this.spriteNameToAnims = new HashMap<>();
        // Preload base sprite
        loadAnimSetIfAbsent(animalName);
    }

    private void loadAnimSetIfAbsent(String spriteName) {
        if (spriteNameToAnims.containsKey(spriteName)) return;

        Texture textureSheet = new Texture(Gdx.files.internal("content/Animals/" + spriteName + ".png"));
        CoopAnimalTypes coopAnimalType = CoopAnimalTypes.fromName(spriteName);
        int numOfHeight = (coopAnimalType != null) ? 7 : 5;
        int frameH = textureSheet.getHeight() / numOfHeight;
        int frameW = textureSheet.getWidth() / 4;

        TextureRegion[][] grid = TextureRegion.split(textureSheet, frameW, frameH);

        AnimSet set = new AnimSet();
        set.texture = textureSheet;
        set.moveDown = buildAnim(grid[0]);
        set.moveRight = buildAnim(grid[1]);
        set.moveUp = buildAnim(grid[2]);
        set.moveLeft = buildAnim(grid[3]);
        set.doSomething = buildAnim(grid.length > 5 ? grid[5] : grid[0]);

        spriteNameToAnims.put(spriteName, set);
    }

    private static Animation<TextureRegion> buildAnim(TextureRegion[] row) {
        Array<TextureRegion> frames = new Array<>(3);
        for (int i = 0; i < 3; i++) {
            frames.add(row[i]);
        }
        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP_PINGPONG);
    }

    public TextureRegion getCurrentFrame(Animal animal, float stateTime) {
        String spriteName = animal.getSpriteName();
        if (spriteName == null || spriteName.isEmpty()) spriteName = baseAnimalName;
        loadAnimSetIfAbsent(spriteName);
        AnimSet set = spriteNameToAnims.get(spriteName);

        Animation<TextureRegion> currentAnimation;
        switch (animal.getFacing()) {
            case UP:
                currentAnimation = set.moveUp;
                break;
            case DOWN:
                currentAnimation = set.moveDown;
                break;
            case LEFT:
                currentAnimation = set.moveLeft;
                break;
            case RIGHT:
                currentAnimation = set.moveRight;
                break;
            default:
                currentAnimation = set.doSomething;
                break;
        }

        return currentAnimation.getKeyFrame(stateTime, true);
    }

    public TextureRegion getRightFrameForAnimal(Animal animal, int frameIndex) {
        String spriteName = animal.getSpriteName();
        if (spriteName == null || spriteName.isEmpty()) spriteName = baseAnimalName;
        loadAnimSetIfAbsent(spriteName);
        AnimSet set = spriteNameToAnims.get(spriteName);
        if (frameIndex >= 0 && frameIndex < 3) {
            Object[] frames = set.moveRight.getKeyFrames();
            if (frames != null && frameIndex < frames.length) {
                return (TextureRegion) frames[frameIndex];
            }
        }
        return null;
    }

    @Override
    public void dispose() {
        for (AnimSet set : spriteNameToAnims.values()) {
            if (set.texture != null) set.texture.dispose();
        }
        spriteNameToAnims.clear();
    }
}
