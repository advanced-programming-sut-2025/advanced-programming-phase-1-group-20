package org.example.client.controllers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import org.example.common.models.App;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.entities.animal.Animal;

import java.util.*;

public class AnimalsController implements Disposable {
    // A map to hold a sprite controller for each type of animal
    private final Map<String, AnimalSpriteController> spriteControllers;
    private float stateTime = 0f;
    private Random animalAiRandom = new Random();

    public AnimalsController() {
        this.spriteControllers = new HashMap<>();
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
        updateAnimalAI(deltaTime);
    }

    private void updateAnimalAI(float deltaTime) {
        if (App.getGame() == null || App.getGame().getGameMap() == null) return;

        List<Animal> allAnimals = new ArrayList<>();
        for (Farm farm : App.getGame().getGameMap().getFarms()) {
            farm.getBarns().forEach(barn -> allAnimals.addAll(barn.getAnimals()));
            farm.getCoops().forEach(coop -> allAnimals.addAll(coop.getAnimals()));
        }

        for (Animal animal : allAnimals) {
            if (!animal.isOutSide()) {
                continue; // Skip to the next animal if it's inside
            }


            // Update the state timer
            animal.setStateTimer(animal.getStateTimer() - deltaTime);

            // Time to change state
            if (animal.getStateTimer() <= 0) {
                // 50% chance to start moving, 50% to stay idle
                if (animalAiRandom.nextBoolean()) {
                    animal.setMoving(true);

                    // Pick a new target location within 5 tiles
                    double angle = animalAiRandom.nextDouble() * 2 * Math.PI;
                    double distance = animalAiRandom.nextDouble() * 5 * 120;

                    float targetX = (float) (animal.getPosX() + Math.cos(angle) * distance);
                    float targetY = (float) (animal.getPosY() + Math.sin(angle) * distance);

                    animal.setTargetX(targetX);
                    animal.setTargetY(targetY);
                }
                else {
                    animal.setMoving(false);
                }
                // Reset timer for next state change (e.g., 2 to 5 seconds)
                animal.setStateTimer(2 + animalAiRandom.nextFloat() * 3);
            }

            // If moving, update position towards target
            if (animal.isMoving()) {
                float currentX = animal.getPosX();
                float currentY = animal.getPosY();
                float targetX = animal.getTargetX();
                float targetY = animal.getTargetY();

                float dx = targetX - currentX;
                float dy = targetY - currentY;

                // Stop if close to the target
                if (Math.abs(dx) < 1 && Math.abs(dy) < 1) {
                    animal.setMoving(false);
                }
                else {
                    // Normalize direction vector
                    float length = (float) Math.sqrt(dx * dx + dy * dy);
                    float moveX = (dx / length) * animal.getSpeed() * deltaTime;
                    float moveY = (dy / length) * animal.getSpeed() * deltaTime;

                    animal.setPosX(currentX + moveX);
                    animal.setPosY(currentY + moveY);

                    // Update facing direction for animation
                    if (Math.abs(dx) > Math.abs(dy)) {
                        animal.setFacing(dx > 0 ? Animal.Direction.RIGHT : Animal.Direction.LEFT);
                    }
                    else {
                        animal.setFacing(dy > 0 ? Animal.Direction.UP : Animal.Direction.DOWN);
                    }
                }
            }
        }
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
            if (animal.isOutSide()) {
                renderAnimal(batch, animal);
            }
        }
        batch.setColor(Color.WHITE);
    }

    private void renderAnimal(SpriteBatch batch, Animal animal) {
        AnimalSpriteController spriteController = getSpriteController(animal.getName());
        if (spriteController == null) {
            return;
        }

        TextureRegion currentFrame = spriteController.getCurrentFrame(animal, stateTime);

        float renderWidth = 48;
        float renderHeight = 48;

        batch.draw(currentFrame, animal.getPosX(), animal.getPosY(), renderWidth, renderHeight);
    }

    /**
     * Retrieves or creates an AnimalSpriteController for a given animal name.
     * This ensures we only load textures once per animal type.
     * @param animalName The name of the animal (e.g., "Chicken", "Cow").
     * @return The corresponding AnimalSpriteController.
     */
    private AnimalSpriteController getSpriteController(String animalName) {
        // Check if a controller for this animal type already exists
        if (!spriteControllers.containsKey(animalName)) {
            try {
                // If not, create a new one and add it to the map
                spriteControllers.put(animalName, new AnimalSpriteController(animalName));
            }
            catch (Exception e) {
                System.err.println("Failed to create sprite controller for: " + animalName + " - " + e.getMessage());
                // Put a null marker to avoid trying again
                spriteControllers.put(animalName, null);
                return null;
            }
        }
        return spriteControllers.get(animalName);
    }

    @Override
    public void dispose() {
        spriteControllers.clear();
    }
}
