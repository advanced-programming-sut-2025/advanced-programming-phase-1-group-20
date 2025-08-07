package org.example.client.views.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;

import org.example.client.controllers.HouseMenuController;
import org.example.common.models.Items.CraftingItem;
import org.example.common.models.Items.Food;
import org.example.common.models.Items.Fruit;
import org.example.common.models.Items.Item;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Result;

import java.util.HashMap;
import java.util.Map;

public class ArtisanCreatScreen implements Screen {

    private final Stage stage;
    private final Skin skin;
    private final Player player;
    private final Screen previousScreen;
    private final CraftingItem artisanStation;
    private final HouseMenuController controller;

    private final DragAndDrop dnd;
    private final HashMap<Item, Integer> inputItems = new HashMap<>();

    // UI Fields for better access
    private Table inventoryTable;
    private Table inputGrid;

    public ArtisanCreatScreen(Player player, Skin skin, Screen previousScreen, CraftingItem artisanStation) {
        this.player = player;
        this.skin = skin;
        this.previousScreen = previousScreen;
        this.artisanStation = artisanStation;
        this.controller = new HouseMenuController(player, player.getCurrentFarm().getBuilding());

        this.stage = new Stage(new ScreenViewport());
        this.dnd = new DragAndDrop();
        dnd.setDragActorPosition(32, -32); // Center the drag actor on the cursor

        setupUI();
    }

    private void setupUI() {
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.pad(20);
        stage.addActor(rootTable);

        // Background
        try {
            Image background = new Image(new Texture(Gdx.files.internal("content/crafting_background.png")));
            background.setFillParent(true);
            stage.addActor(background);
            rootTable.toFront(); // Ensure rootTable is on top of the background
        } catch (Exception e) {
            System.err.println("Failed to load background image.");
        }

        // Title
        rootTable.add(new Label("Using: " + artisanStation.getName(), skin)).colspan(2).padBottom(20).row();

        // Left Side: 2x2 Input Grid
        inputGrid = createInputGrid();
        rootTable.add(inputGrid).top().padRight(20);

        // Right Side: Inventory
        ScrollPane inventoryPane = createInventoryPane();
        inventoryTable = (Table) inventoryPane.getActor(); // Correctly get reference to the inner table
        rootTable.add(inventoryPane).expand().fill();
        rootTable.row();

        // Bottom: Action Buttons
        Table actionTable = new Table();
        TextButton processButton = new TextButton("Start Process", skin);
        processButton.addListener(event -> {
            if (event.toString().contains("touchDown")) {
                Result result = controller.artisanUse(artisanStation, inputItems);
                if (result.success()) {
                    Main.getGame().setScreen(previousScreen);
                } else {
                    new Dialog("Error", skin).text(result.message()).button("OK").show(stage);
                }
            }
            return true;
        });
        actionTable.add(processButton).pad(10);
        rootTable.add(actionTable).colspan(2).padTop(20);

        populateInventory();
    }

    private Table createInputGrid() {
        Table grid = new Table();
        for (int i = 0; i < 4; i++) {
            // FIX 2: Use Container<Actor> to hold any actor, not just Label.
            Container<Actor> slot = new Container<>();
            grid.add(slot).size(64, 64).pad(5);
            if (i == 1) grid.row();

            dnd.addTarget(new DragAndDrop.Target(slot) {
                public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    Item item = (Item) payload.getObject();
                    // Optional: Add logic here to check if the item is valid for this machine
                    getActor().setColor(Color.GREEN);
                    return true;
                }

                public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                    getActor().setColor(Color.WHITE);
                }

                public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                    Item item = (Item) payload.getObject();

                    // Add item to the input map (simplified: adds 1, removes 1 from backpack)
                    inputItems.put(item, inputItems.getOrDefault(item, 0) + 1);
                    player.getBackpack().remove(item, 1);

                    // FIX 2: This now works because the container is of type Actor.
                    slot.setActor(new Image(new Texture(item.getImageFilepath())));
                    populateInventory(); // Refresh inventory to show decreased quantity
                }
            });
        }
        return grid;
    }

    private ScrollPane createInventoryPane() {
        Table table = new Table();
        ScrollPane scrollPane = new ScrollPane(table, skin);
        scrollPane.setFadeScrollBars(false);
        return scrollPane;
    }

    private void populateInventory() {
        // FIX 1: Use the class field `inventoryTable` directly.
        inventoryTable.clear();
        inventoryTable.top();

        int col = 0;
        for (Map.Entry<Item, Integer> entry : player.getBackpack().getInventory().entrySet()) {
            Item item = entry.getKey();
            int quantity = entry.getValue();

            // Create a stack to overlay quantity on the image
            Stack itemStack = new Stack();
            itemStack.add(new Image(new Texture(item.getImageFilepath())));
            Label quantityLabel = new Label(String.valueOf(quantity), skin);
            quantityLabel.setAlignment(Align.bottomRight);
            quantityLabel.setColor(Color.BLACK);
            itemStack.add(quantityLabel);

            // Make the item draggable
            dnd.addSource(new DragAndDrop.Source(itemStack) {
                public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    if(quantity <= 0) return null; // Can't drag empty stacks

                    DragAndDrop.Payload payload = new DragAndDrop.Payload();
                    payload.setObject(item);

                    Image dragImage = new Image(new Texture(item.getImageFilepath()));
                    payload.setDragActor(dragImage);

                    // Visually decrease opacity of source
                    itemStack.setColor(0.5f, 0.5f, 0.5f, 0.5f);
                    return payload;
                }

                @Override
                public void dragStop(InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                    itemStack.setColor(Color.WHITE); // Restore original color
                }
            });

            inventoryTable.add(itemStack).size(64, 64).pad(5);
            if (++col % 4 == 0) inventoryTable.row();
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1);
        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.C) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Main.getGame().setScreen(previousScreen);
        }
    }

    @Override
    public void show() { Gdx.input.setInputProcessor(stage); }
    @Override
    public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() { stage.dispose(); }
}
