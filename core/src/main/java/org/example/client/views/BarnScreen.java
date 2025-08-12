package org.example.client.views;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.client.controllers.AnimalSpriteController;
import org.example.client.controllers.gameplay.AnimalController;
import org.example.common.models.Barn;
import org.example.common.models.common.Result;
import org.example.common.models.entities.animal.BarnAnimal;
import org.example.common.models.Items.Item;

import java.util.HashMap;
import java.util.Map;

public class BarnScreen implements Screen {
    private Game game;
    private Screen previousScreen;
    private Stage stage;
    private Skin skin;
    private Barn barn;
    private AnimalController controller;
    private final Map<String, AnimalSpriteController> spriteControllers;
    private final Map<String, Texture> productTextures;

    public BarnScreen(Game game, Screen previousScreen, Barn barn, Skin skin, AnimalController controller) {
        this.game = game;
        this.previousScreen = previousScreen;
        this.barn = barn;
        this.skin = skin;
        this.controller = controller;
        this.stage = new Stage(new ScreenViewport());
        this.spriteControllers = new HashMap<>();
        this.productTextures = new HashMap<>();
        loadProductTextures();
    }

    private void loadProductTextures() {
        // Load product textures for barn animals
        String[] products = {"Milk", "Big_Milk", "Goat_Milk", "Big_Goat_Milk", "Wool", "Truffle"};
        for (String product : products) {
            try {
                productTextures.put(product, new Texture("content/Animals/animal_goods/" + product + ".png"));
            } catch (Exception e) {
                System.err.println("Failed to load product texture: " + product);
            }
        }
    }

    private AnimalSpriteController getSpriteController(String animalName) {
        if (!spriteControllers.containsKey(animalName)) {
            try {
                spriteControllers.put(animalName, new AnimalSpriteController(animalName));
            } catch (Exception e) {
                System.err.println("Failed to create sprite controller for: " + animalName);
                return null;
            }
        }
        return spriteControllers.get(animalName);
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
        mainTable.add(new Label(barn.getName() + " (Capacity: " + barn.getAnimalCount() + "/" + barn.getCapacity() + ")", skin)).colspan(3).pad(20).row();

        // List of animals
        if (barn.getAnimals().isEmpty()) {
            mainTable.add(new Label("This barn is empty.", skin)).row();
        } else {
            for (final BarnAnimal animal : barn.getAnimals()) {
                // Animal image (moveRight(0) frame)
                AnimalSpriteController spriteController = getSpriteController(animal.getName());
                if (spriteController != null) {
                    var animalFrame = spriteController.getRightFrame(0);
                    if (animalFrame != null) {
                        Image animalImage = new Image(animalFrame);
                        animalImage.setSize(48, 48);
                        mainTable.add(animalImage).pad(5);
                    } else {
                        mainTable.add(new Label("", skin)).pad(5);
                    }
                } else {
                    mainTable.add(new Label("", skin)).pad(5);
                }

                // Animal info
                Table animalInfo = new Table(skin);
                animalInfo.add(new Label(animal.getName() + " (" + animal.getType() + ")", skin)).row();
                
                // Check if animal has product ready
                Item product = animal.getProduct();
                if (product != null) {
                    // Show product image
                    Texture productTexture = productTextures.get(product.getName());
                    if (productTexture != null) {
                        Image productImage = new Image(productTexture);
                        productImage.setSize(32, 32);
                        animalInfo.add(productImage).pad(5);
                    }
                    
                    // Add collect button
                    TextButton collectButton = new TextButton("Collect", skin);
                    collectButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            Result result = controller.collectProduce(new String[]{animal.getName()});
                            System.out.println(result.message());
                            buildUI(); // Rebuild UI to reflect changes
                        }
                    });
                    animalInfo.add(collectButton).pad(5);
                }
                
                mainTable.add(animalInfo).pad(10);

                // Action buttons
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
        mainTable.add(new Label("Press ESC to go back", skin)).colspan(3);
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
        // Dispose sprite controllers
        for (AnimalSpriteController controller : spriteControllers.values()) {
            if (controller != null) {
                controller.dispose();
            }
        }
        spriteControllers.clear();
        
        // Dispose product textures
        for (Texture texture : productTextures.values()) {
            texture.dispose();
        }
        productTextures.clear();
        
        stage.dispose();
    }
}
