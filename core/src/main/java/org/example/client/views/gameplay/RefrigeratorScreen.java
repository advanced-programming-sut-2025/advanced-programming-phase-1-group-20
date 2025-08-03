package org.example.client.views.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.common.models.Player.Player;
import org.example.common.models.Items.Item;
import org.example.common.models.Player.Backpack;
import org.example.common.models.Player.Refrigerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RefrigeratorScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private Player player;
    private Screen previousScreen;
    private Refrigerator refrigerator;
    private Backpack backpack;

    private Table refrigeratorTable;
    private Table inventoryTable;
    private DragAndDrop dnd;

    public RefrigeratorScreen(Player player, Skin skin, Screen previousScreen) {
        this.player = player;
        this.skin = skin;
        this.previousScreen = previousScreen;
        this.refrigerator = player.getCurrentFarm().getBuilding().getRefrigerator();
        this.backpack = player.getBackpack();

        stage = new Stage(new ScreenViewport());
        dnd = new DragAndDrop();

        setupUI();
    }

    private void setupUI() {
        Table mainContainer = new Table();
        mainContainer.setFillParent(true);
        mainContainer.center();

        // Background
        try {
            Texture backgroundTexture = new Texture(Gdx.files.internal("content/crafting_background.png"));
            Image backgroundImage = new Image(backgroundTexture);
            backgroundImage.setFillParent(true);
            stage.addActor(backgroundImage);
        } catch (Exception e) {
            System.err.println("Failed to load background image: " + e.getMessage());
        }

        // Refrigerator section
        refrigeratorTable = new Table();
        Label refrigeratorLabel = new Label("Refrigerator", skin);
        mainContainer.add(refrigeratorLabel).padBottom(10).row();
        mainContainer.add(refrigeratorTable).padBottom(20).row();

        // Inventory section
        inventoryTable = new Table();
        Label inventoryLabel = new Label("Inventory", skin);
        mainContainer.add(inventoryLabel).padBottom(10).row();
        mainContainer.add(inventoryTable).row();

        stage.addActor(mainContainer);

        populateGrids();
    }

    private void populateGrids() {
        populateGrid(refrigeratorTable, refrigerator.getItems(), "refrigerator");
        populateGrid(inventoryTable, backpack.getInventory(), "inventory");
    }

    private void populateGrid(Table table, Map<Item, Integer> items, String type) {
        table.clear();
        int i = 0;
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            Item item = entry.getKey();
            int quantity = entry.getValue();

            Image itemImage = new Image(new Texture(Gdx.files.internal(item.getImageFilepath())));
            Label quantityLabel = new Label(String.valueOf(quantity), skin);
            quantityLabel.setColor(Color.WHITE);

            Stack itemStack = new Stack();
            itemStack.add(itemImage);
            itemStack.add(quantityLabel);

            Container<Stack> container = new Container<>(itemStack);
            container.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
            container.setUserObject(item); // Store the item itself in the container

            dnd.addSource(new Source(container) {
                public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    Payload payload = new Payload();
                    payload.setObject(item);

                    Image dragImage = new Image(new Texture(Gdx.files.internal(item.getImageFilepath())));
                    payload.setDragActor(dragImage);

                    return payload;
                }
            });

            dnd.addTarget(new Target(container) {
                public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                    return true; // Can drop here
                }

                public void drop(Source source, Payload payload, float x, float y, int pointer) {
                    Item droppedItem = (Item) payload.getObject();
                    Item targetItem = (Item) getActor().getUserObject();

                    // Logic to swap or stack items can be added here
                }
            });

            table.add(container).size(64, 64).pad(5);
            if (++i % 9 == 0) {
                table.row();
            }
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Main.getGame().setScreen(previousScreen);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
