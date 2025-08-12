package org.example.client.views;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import org.example.client.controllers.gameplay.AnimalController;
import org.example.common.models.Coop;
import org.example.common.models.entities.animal.CoopAnimal;
import org.example.common.models.common.Result;

/**
 * A screen to display the contents and options for a specific Coop.
 */
public class CoopScreen extends Table {
    private Coop coop;
    private Skin skin;
    private AnimalController controller; // Controller for animal actions

    public CoopScreen(Coop coop, Skin skin, AnimalController controller) {
        super(skin);
        this.coop = coop;
        this.skin = skin;
        this.controller = controller;
        this.setFillParent(true);
        buildUI();
    }

    private void buildUI() {
        // Title
        add(new Label(coop.getName() + " (Capacity: " + coop.getAnimalCount() + "/" + coop.getCapacity() + ")", skin, "title")).colspan(2).pad(20);
        row();

        // List of animals
        if (coop.getAnimals().isEmpty()) {
            add(new Label("This coop is empty.", skin));
        } else {
            for (final CoopAnimal animal : coop.getAnimals()) {
                add(new Label(animal.getName() + " (" + animal.getType() + ")", skin)).pad(10);

                Table buttonGroup = new Table(skin);

                // Pet Button
                TextButton petButton = new TextButton("Pet", skin);
                petButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        Result result = controller.petAnimal(new String[]{animal.getName()});
                        // You can show the result message in a toast or label
                        System.out.println(result.message());
                        update();
                    }
                });
                buttonGroup.add(petButton).pad(5);


                // Release/Return Button
                String releaseButtonText = animal.isOutSide() ? "Bring Inside" : "Release Outside";
                TextButton releaseButton = new TextButton(releaseButtonText, skin);
                releaseButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        Result result;
                        if (animal.isOutSide()) {
                            result = controller.returnAnimalToHome(new String[]{animal.getName()});
                        } else {
                            // You would need a method to release the animal
                            // For now, we just toggle the state directly
                            animal.setOutSide(true);
                            result = Result.success(animal.getName() + " was released outside.");
                        }
                        System.out.println(result.message());
                        update();
                    }
                });
                buttonGroup.add(releaseButton).pad(5);

                add(buttonGroup);
                row();
            }
        }
    }

    public void update() {
        clear();
        buildUI();
    }
}
