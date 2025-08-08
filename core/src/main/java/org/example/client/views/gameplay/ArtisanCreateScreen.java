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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.client.controllers.HouseMenuController;
import org.example.common.models.Items.CraftingItem;
import org.example.common.models.Items.Item;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Result;

import java.util.HashMap;
import java.util.Map;

public class ArtisanCreateScreen implements Screen {

    private final Stage stage;
    private final Skin skin;
    private final Player player;
    private final Screen previousScreen;
    private final CraftingItem artisanStation;
    private final HouseMenuController controller;
    private final DragAndDrop dnd;

    private final HashMap<Item, Integer> inputItems = new HashMap<>();

    // UI Tables for easy access
    private Table inputGrid;
    private Table inventoryGrid;
    private ProgressBar progressBar;
    private Label statusLabel;
    private TextButton startProcessButton, collectButton, cancelButton, fastFinishButton;

    // Constants from RefrigeratorScreen
    private static final float SLOT_SIZE = 64f;
    private static final float SLOT_PAD = 8f;

    private static class ItemPayload {
        Item item;
        String sourceContainer; // "input" or "backpack"
    }

    public ArtisanCreateScreen(Player player, Skin skin, Screen previousScreen, CraftingItem artisanStation) {
        this.player = player;
        this.skin = skin;
        this.previousScreen = previousScreen;
        this.artisanStation = artisanStation;
        this.controller = new HouseMenuController(player, player.getCurrentFarm().getBuilding());
        this.stage = new Stage(new ScreenViewport());
        this.dnd = new DragAndDrop();
        dnd.setDragActorPosition(SLOT_SIZE / 2, -SLOT_SIZE / 2);

        setupUI();
    }

    private void setupUI() {
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.pad(20);

        try {
            Image background = new Image(new Texture(Gdx.files.internal("content/crafting_background.png")));
            background.setFillParent(true);
            stage.addActor(background);
        } catch (Exception e) {
            System.err.println("Could not load background texture.");
        }

        stage.addActor(rootTable);

        // --- TOP SECTION: ARTISAN STATION (like Refrigerator) ---
        Label artisanLabel = new Label("Artisan Machine: " + artisanStation.getName(), skin);
        Stack artisanStack = createInventorySection(2, 9, "input"); // 2 rows, 9 cols for input
        inputGrid = (Table) artisanStack.getChild(1);

        // --- BOTTOM SECTION: PLAYER INVENTORY (like Refrigerator) ---
        Label inventoryLabel = new Label("Your Inventory", skin);
        Stack inventoryStack = createInventorySection(3, 9, "backpack"); // 3 rows for backpack
        inventoryGrid = (Table) inventoryStack.getChild(1);

        // --- MIDDLE SECTION: CONTROLS AND STATUS ---
        Table controlTable = new Table();
        statusLabel = new Label("Ready", skin);
        progressBar = new ProgressBar(0, 1, 0.01f, false, skin);
        startProcessButton = new TextButton("Start Process", skin);
        collectButton = new TextButton("Collect Product", skin);
        cancelButton = new TextButton("Cancel", skin);
        fastFinishButton = new TextButton("Finish Fast", skin);

        controlTable.add(statusLabel).padBottom(5).row();
        controlTable.add(progressBar).width(250).height(20).padBottom(10).row();

        Table buttonTable = new Table();
        buttonTable.add(startProcessButton).pad(5);
        buttonTable.add(collectButton).pad(5);
        buttonTable.add(cancelButton).pad(5);
        buttonTable.add(fastFinishButton).pad(5);
        controlTable.add(buttonTable);

        // Layout the sections vertically
        rootTable.add(artisanLabel).padBottom(10).row();
        rootTable.add(artisanStack).padBottom(20).row();
        rootTable.add(controlTable).padBottom(20).row();
        rootTable.add(inventoryLabel).padBottom(10).row();
        rootTable.add(inventoryStack);

        setupListeners();
        populateAllGrids();
        updateButtonStates();
    }

    private Stack createInventorySection(int rows, int cols, String containerName) {
        Stack stack = new Stack();
        try {
            Image inventoryBg = new Image(new Texture(Gdx.files.internal("inventory.png")));
            stack.add(inventoryBg);
        } catch (Exception e) {
            System.err.println("Could not load inventory.png background.");
        }

        Table grid = new Table();
        grid.pad(20);
        stack.add(grid);

        return stack;
    }

    // --- (The rest of the methods are almost identical to the previous version, with minor tweaks for the new layout) ---
    private void setupListeners() {
        startProcessButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Result result = controller.artisanUse(artisanStation, inputItems);
                if (result.success()) {
                    inputItems.clear(); // Clear input slots on success
                    populateAllGrids();
                    updateButtonStates();
                } else {
                    new Dialog("Error", skin).text(result.message()).button("OK").show(stage);
                }
            }
        });

        collectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Result result = controller.artisanGet(new String[]{artisanStation.getName()});
                new Dialog("Result", skin).text(result.message()).button("OK").show(stage);
                populateAllGrids();
                updateButtonStates();
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Return items to backpack before canceling
                for(Map.Entry<Item, Integer> entry : inputItems.entrySet()) {
                    player.getBackpack().add(entry.getKey(), entry.getValue());
                }
                inputItems.clear();

                Result result = controller.artisanCancel(new String[]{artisanStation.getName()});
                new Dialog("Result", skin).text(result.message()).button("OK").show(stage);
                populateAllGrids();
                updateButtonStates();
            }
        });

        fastFinishButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Result result = controller.artisanFastFinish(new String[]{artisanStation.getName()});
                new Dialog("Result", skin).text(result.message()).button("OK").show(stage);
                updateButtonStates();
            }
        });
    }

    private void updateButtonStates() {
        boolean isProcessing = artisanStation.getProccessingItem() != null;
        boolean isFinished = artisanStation.getFinishedItem() != null;
        boolean needsIngredients = !artisanStation.getType().getIngredients().getRecipe().isEmpty();

        startProcessButton.setDisabled(isProcessing || isFinished || (needsIngredients && inputItems.isEmpty()));
        collectButton.setDisabled(!isFinished);
        cancelButton.setDisabled(!isProcessing);
        fastFinishButton.setDisabled(!isProcessing);

        if (isFinished) {
            statusLabel.setText("Product Ready!");
            progressBar.setValue(1);
        } else if (isProcessing) {
            statusLabel.setText("Processing: " + artisanStation.getProccessingItem().getName());
            progressBar.setValue((float)artisanStation.getProgressBar());
        } else {
            if (needsIngredients) {
                statusLabel.setText("Ready - Drag items to input slots");
            } else {
                statusLabel.setText("Ready to start");
            }
            progressBar.setValue(0);
        }
    }


    private void populateAllGrids() {
        dnd.clear();
        populateGrid(inputGrid, inputItems, "input", 18); // 2 rows * 9 cols
        populateGrid(inventoryGrid, player.getBackpack().getInventory(), "backpack", 27); // 3 rows * 9 cols
    }

    private void populateGrid(Table grid, Map<Item, Integer> items, String containerName, int totalSlots) {
        grid.clear();
        int colCount = 9;
        int currentSlot = 0;

        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            grid.add(createItemSlot(entry.getKey(), entry.getValue(), containerName)).size(SLOT_SIZE).pad(SLOT_PAD);
            if (++currentSlot % colCount == 0) grid.row();
        }

        while (currentSlot < totalSlots) {
            grid.add(createEmptySlot(containerName)).size(SLOT_SIZE).pad(SLOT_PAD);
            if (++currentSlot % colCount == 0) grid.row();
        }
    }

    private Container<Stack> createItemSlot(Item item, int quantity, String sourceContainer) {
        Stack stack = new Stack();
        stack.add(new Image(new Texture(item.getImageFilepath())));
        Label quantityLabel = new Label(String.valueOf(quantity), skin);
        quantityLabel.setAlignment(Align.bottomRight);
        quantityLabel.setColor(Color.BLACK);
        stack.add(quantityLabel);

        Container<Stack> container = new Container<>(stack);
        container.setTouchable(Touchable.enabled);

        dnd.addSource(new Source(container) {
            public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                if (quantity <= 0) return null;
                Payload payload = new Payload();
                ItemPayload itemPayload = new ItemPayload();
                itemPayload.item = item;
                itemPayload.sourceContainer = sourceContainer;
                payload.setObject(itemPayload);
                payload.setDragActor(new Image(new Texture(item.getImageFilepath())));
                container.getActor().setColor(0.5f, 0.5f, 0.5f, 0.5f);
                return payload;
            }
            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
                container.getActor().setColor(Color.WHITE);
            }
        });

        return container;
    }

    private Container<Actor> createEmptySlot(String targetContainerName) {
        Container<Actor> emptyContainer = new Container<>();
        emptyContainer.setTouchable(Touchable.enabled);

        dnd.addTarget(new Target(emptyContainer) {
            public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                getActor().setColor(Color.GREEN);
                return true;
            }
            public void reset(Source source, Payload payload) {
                getActor().setColor(Color.WHITE);
            }
            public void drop(Source source, Payload payload, float x, float y, int pointer) {
                ItemPayload itemPayload = (ItemPayload) payload.getObject();
                Item droppedItem = itemPayload.item;
                String sourceName = itemPayload.sourceContainer;

                Map<Item, Integer> sourceMap = sourceName.equals("input") ? inputItems : player.getBackpack().getInventory();
                Map<Item, Integer> targetMap = targetContainerName.equals("input") ? inputItems : player.getBackpack().getInventory();

                sourceMap.compute(droppedItem, (k, v) -> (v == null || v <= 1) ? null : v - 1);
                targetMap.put(droppedItem, targetMap.getOrDefault(droppedItem, 0) + 1);

                populateAllGrids();
                updateButtonStates();
            }
        });
        return emptyContainer;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1);
        updateButtonStates();
        stage.act(delta);
        stage.draw();
        if (Gdx.input.isKeyJustPressed(Input.Keys.C) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            inputItems.forEach((item, quantity) -> player.getBackpack().add(item, quantity));
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
