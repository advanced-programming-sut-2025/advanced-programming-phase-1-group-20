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

public class AnimalSpriteController implements Disposable {
    private final Texture textureSheet;
    private final int FRAME_W;
    private final int FRAME_H;
    private static final float FRAME_DURATION = 0.2f;

    // Animations
    private final Animation<TextureRegion> moveUp;
    private final Animation<TextureRegion> moveDown;
    private final Animation<TextureRegion> moveLeft;
    private final Animation<TextureRegion> moveRight;
    private final Animation<TextureRegion> doSomething; // Can be used for idle state

    public AnimalSpriteController(String animalName) {
        textureSheet = new Texture(Gdx.files.internal("content/Animals/" + animalName + ".png"));
        CoopAnimalTypes coopAnimalType = CoopAnimalTypes.fromName(animalName);
        int numOfHeight;
        if (coopAnimalType != null) {
            numOfHeight = 7;
        } else {
            numOfHeight = 5;
        }

        FRAME_H = textureSheet.getHeight() / numOfHeight;
        FRAME_W = textureSheet.getWidth() / 4;

        TextureRegion[][] grid = TextureRegion.split(textureSheet, FRAME_W, FRAME_H);
        moveDown = buildAnim(grid[0]);
        moveRight = buildAnim(grid[1]);
        moveUp = buildAnim(grid[2]);
        moveLeft = buildAnim(grid[3]);
        doSomething = buildAnim(grid.length > 5 ? grid[5] : grid[0]); // Fallback to moveDown if doSomething doesn't exist

    }

    private static Animation<TextureRegion> buildAnim(TextureRegion[] row) {
        Array<TextureRegion> frames = new Array<>(3);
        for (int i = 0; i < 3; i++) {
            frames.add(row[i]);
        }
        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP_PINGPONG);
    }

    public TextureRegion getCurrentFrame(Animal animal, float stateTime) {
        Animation<TextureRegion> currentAnimation;

        // Select animation based on facing direction
        switch (animal.getFacing()) {
            case UP:
                currentAnimation = moveUp;
                break;
            case DOWN:
                currentAnimation = moveDown;
                break;
            case LEFT:
                currentAnimation = moveLeft;
                break;
            case RIGHT:
                currentAnimation = moveRight;
                break;
            default:
                currentAnimation = doSomething;
                break;
        }

        // If the animal is not moving, show a standing frame (e.g., the second frame)
        if (!animal.isMoving()) {
            return currentAnimation.getKeyFrame(stateTime, true);
        }

        // If moving, return the animated frame
        return currentAnimation.getKeyFrame(stateTime, true);
    }

    public TextureRegion getRightFrame(int frameIndex) {
        if (frameIndex >= 0 && frameIndex < 3) {
            Object[] frames = moveRight.getKeyFrames();
            if (frames != null && frameIndex < frames.length) {
                return (TextureRegion) frames[frameIndex];
            }
        }
        return null;
    }

    @Override
    public void dispose() {
        if (textureSheet != null) {
            textureSheet.dispose();
        }
    }
}
