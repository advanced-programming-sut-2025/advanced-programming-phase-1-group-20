package org.example.client.views;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import org.example.client.controllers.gameplay.AnimalController;
import org.example.common.models.common.Result;
import org.example.common.models.entities.animal.Animal;
import org.example.common.models.entities.animal.BarnAnimal;
import org.example.common.models.entities.animal.CoopAnimal;

import java.util.function.BiConsumer;

public class AnimalInteractionDialog extends Dialog {

    private final Animal animal;
    private final AnimalController controller;
    private final BiConsumer<Result, EffectType> resultCallback;

    /**
     * Enum to identify the type of interaction for creating visual effects.
     */
    public enum EffectType {
        PET, FEED, RETURN_HOME
    }

    public AnimalInteractionDialog(String title, Skin skin, Animal animal, AnimalController controller, BiConsumer<Result, EffectType> resultCallback) {
        super(title, skin);
        this.animal = animal;
        this.controller = controller;
        this.resultCallback = resultCallback;

        setModal(true);
        setMovable(false);

        // Pet Button
        TextButton petButton = new TextButton("Pet", skin);
        petButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Result result = controller.petAnimal(new String[]{animal.getName()});
                resultCallback.accept(result, EffectType.PET); // Send PET effect type
                hide();
            }
        });
        getContentTable().add(petButton).width(200).pad(5).row();

        // Feed Button
        TextButton feedButton = new TextButton("Feed with Hay", skin);
        feedButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Result result = controller.feedHay(new String[]{animal.getName()});
                resultCallback.accept(result, EffectType.FEED); // Send FEED effect type
                hide();
            }
        });
        getContentTable().add(feedButton).width(200).pad(5).row();

        // Collect Product Button
        TextButton collectButton = new TextButton("Collect Product", skin);
        boolean canProduce = (animal instanceof BarnAnimal && ((BarnAnimal) animal).canProduce()) ||
            (animal instanceof CoopAnimal && ((CoopAnimal) animal).getProduct() != null);
        collectButton.setDisabled(!canProduce);
        collectButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Result result = controller.collectProduce(new String[]{animal.getName()});
                resultCallback.accept(result, null); // No effect
                hide();
            }
        });
        getContentTable().add(collectButton).width(200).pad(5).row();


        // Sell Button
        TextButton sellButton = new TextButton("Sell", skin);
        sellButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Result result = controller.sellAnimal(new String[]{animal.getName()});
                resultCallback.accept(result, null); // No effect
                hide();
            }
        });
        getContentTable().add(sellButton).width(200).pad(5).row();

        // --- NEW BUTTON ---
        // Return to Home Button (only if the animal is outside)
        if (animal.isOutSide()) {
            TextButton returnHomeButton = new TextButton("Return to Home", skin);
            returnHomeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // Assuming you have a method in your controller to handle this
                    Result result = controller.returnAnimalToHome(new String[]{animal.getName()});
                    resultCallback.accept(result, EffectType.RETURN_HOME);
                    hide();
                }
            });
            getContentTable().add(returnHomeButton).width(200).pad(5).row();
        }


        // Cancel Button
        button("Cancel");
    }
}
