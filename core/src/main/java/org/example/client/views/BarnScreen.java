package org.example.client.views;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.client.controllers.gameplay.AnimalController;
import org.example.common.models.Barn;
import org.example.common.models.common.Result;
import org.example.common.models.entities.animal.BarnAnimal;

public class BarnScreen implements Screen {
    private Game game;
    private Screen previousScreen;
    private Stage stage;
    private Skin skin;
    private Barn barn;
    private AnimalController controller;

    public BarnScreen(Game game, Screen previousScreen, Barn barn, Skin skin, AnimalController controller) {
        this.game = game;
        this.previousScreen = previousScreen;
        this.barn = barn;
        this.skin = skin;
        this.controller = controller;
        this.stage = new Stage(new ScreenViewport());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        buildUI();
    }

    private void buildUI() {
        stage.clear();
        Table mainTable = new Table(skin);
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Title
        mainTable.add(new Label(barn.getName() + " (Capacity: " + barn.getAnimalCount() + "/" + barn.getCapacity() + ")", skin)).colspan(2).pad(20).row();

        // List of animals
        if (barn.getAnimals().isEmpty()) {
            mainTable.add(new Label("This barn is empty.", skin)).row();
        } else {
            for (final BarnAnimal animal : barn.getAnimals()) {
                mainTable.add(new Label(animal.getName() + " (" + animal.getType() + ")", skin)).pad(10);

                Table buttonGroup = new Table(skin);

                // Pet Button
                TextButton petButton = new TextButton("Pet", skin);
                petButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        Result result = controller.petAnimal(new String[]{animal.getName()});
                        System.out.println(result.message());
                        buildUI(); // Rebuild UI to reflect changes
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
                            animal.setOutSide(true);
                            result = Result.success(animal.getName() + " was released outside.");
                        }
                        System.out.println(result.message());
                        buildUI(); // Rebuild UI to reflect changes
                    }
                });
                buttonGroup.add(releaseButton).pad(5);

                mainTable.add(buttonGroup).row();
            }
        }
        // Add a back button or instruction
        mainTable.row().padTop(30);
        mainTable.add(new Label("Press ESC to go back", skin)).colspan(2);
    }

    @Override
    public void render(float delta) {
        // Handle ESC key press to go back
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Main.getGame().setScreen(previousScreen);
        }

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
