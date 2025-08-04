// main/java/org/example/client/views/AnimalInteractionDialog.java

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

import java.util.function.Consumer;

public class AnimalInteractionDialog extends Dialog {

    private final Animal animal;
    private final AnimalController controller;
    private final Consumer<Result> resultCallback;

    public AnimalInteractionDialog(String title, Skin skin, Animal animal, AnimalController controller, Consumer<Result> resultCallback) {
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
                resultCallback.accept(result);
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
                resultCallback.accept(result);
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
                resultCallback.accept(result);
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
                resultCallback.accept(result);
                hide();
            }
        });
        getContentTable().add(sellButton).width(200).pad(5).row();


        // Cancel Button
        button("Cancel");
    }
}
