package org.example.client.views;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import org.example.client.controllers.gameplay.AnimalController;
import org.example.common.models.Barn;
import org.example.common.models.entities.animal.BarnAnimal;
import org.example.common.models.common.Result;


/**
 * A screen to display the contents and options for a specific Barn.
 */
public class BarnScreen extends Table {
    private Barn barn;
    private Skin skin;
    private AnimalController controller; // Controller for animal actions

    public BarnScreen(Barn barn, Skin skin, AnimalController controller) {
        super(skin);
        this.barn = barn;
        this.skin = skin;
        this.controller = controller;
        this.setFillParent(true);
        buildUI();
    }

    private void buildUI() {
        // Title
        add(new Label(barn.getName() + " (Capacity: " + barn.getAnimalCount() + "/" + barn.getCapacity() + ")", skin, "title")).colspan(2).pad(20);
        row();

        // List of animals
        if (barn.getAnimals().isEmpty()) {
            add(new Label("This barn is empty.", skin));
        } else {
            for (final BarnAnimal animal : barn.getAnimals()) {
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
