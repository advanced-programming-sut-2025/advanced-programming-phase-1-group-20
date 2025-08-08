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
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.common.models.Items.Food;
import org.example.common.models.Items.Fruit;
import org.example.common.models.Items.Item;
import org.example.common.models.Player.Backpack;
import org.example.common.models.Player.Player;
import org.example.common.models.Player.Refrigerator;

import java.util.Map;

public class RefrigeratorScreen implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final Player player;
    private final Screen previousScreen;
    private final Refrigerator refrigerator;
    private final Backpack backpack;
    private final DragAndDrop dnd;

    private Table refrigeratorGrid;
    private Table inventoryGrid;

    // Constants for inventory layout
    private static final int REFRIGERATOR_COLS = 9;
    private static final int REFRIGERATOR_ROWS = 3;
    private static final int INVENTORY_COLS = 9;
    private static final int INVENTORY_ROWS = 1; // Like a hotbar
    private static final float SLOT_SIZE = 64f;
    private static final float SLOT_PAD = 8f;

    private static class ItemPayload {
        Item item;
        String sourceContainer; // "refrigerator" or "backpack"
    }

    public RefrigeratorScreen(Player player, Skin skin, Screen previousScreen) {
        this.player = player;
        this.skin = skin;
        this.previousScreen = previousScreen;
        this.refrigerator = player.getCurrentFarm().getBuilding().getRefrigerator();
        this.backpack = player.getBackpack();

        stage = new Stage(new ScreenViewport());
        dnd = new DragAndDrop();
        dnd.setDragActorPosition(SLOT_SIZE / 2, -SLOT_SIZE / 2); // Center the drag actor on the cursor

        setupUI();
    }

    private void setupUI() {
        // Main container table that holds everything
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.pad(20);

        // Add a general background for the whole screen
        try {
            Texture screenBgTexture = new Texture(Gdx.files.internal("content/crafting_background.png"));
            Image screenBg = new Image(screenBgTexture);
            screenBg.setFillParent(true);
            stage.addActor(screenBg);
        } catch (Exception e) {
            System.err.println("Failed to load screen background: " + e.getMessage());
        }

        // --- Refrigerator Section ---
        Label refrigeratorLabel = new Label("Refrigerator", skin);
        Stack refrigeratorStack = createInventorySection(REFRIGERATOR_ROWS, REFRIGERATOR_COLS, "refrigerator");
        refrigeratorGrid = (Table) refrigeratorStack.getChild(1); // Get the grid table from the stack

        // --- Inventory Section ---
        Label inventoryLabel = new Label("Inventory", skin);
        Stack inventoryStack = createInventorySection(INVENTORY_ROWS, INVENTORY_COLS, "backpack");
        inventoryGrid = (Table) inventoryStack.getChild(1); // Get the grid table from the stack

        // Layout the sections vertically
        rootTable.add(refrigeratorLabel).padBottom(10).row();
        rootTable.add(refrigeratorStack).padBottom(20).row();
        rootTable.add(inventoryLabel).padBottom(10).row();
        rootTable.add(inventoryStack);

        stage.addActor(rootTable);

        // Initial population of items
        populateAllGrids();
    }

    /**
     * Creates a visual section (background + grid) for an inventory.
     */
    private Stack createInventorySection(int rows, int cols, String containerName) {
        Stack stack = new Stack();
        try {
            // Background image for the grid
            Texture inventoryBgTexture = new Texture(Gdx.files.internal("inventory.png"));
            Image inventoryBg = new Image(inventoryBgTexture);
            stack.add(inventoryBg);
        } catch (Exception e) {
            System.err.println("Could not load inventory background 'inventory.jpg': " + e.getMessage());
        }

        // The grid for items
        Table grid = new Table();
        grid.pad(20); // Add some padding to align with the background image
        stack.add(grid);

        return stack;
    }


    private void populateAllGrids() {
        dnd.clear(); // Clear all previous drag/drop sources and targets
        populateGrid(refrigeratorGrid, refrigerator.getItems(), "refrigerator", REFRIGERATOR_ROWS * REFRIGERATOR_COLS);
        populateGrid(inventoryGrid, backpack.getInventory(), "backpack", INVENTORY_ROWS * INVENTORY_COLS);
    }


    private void populateGrid(Table grid, Map<Item, Integer> items, String containerName, int totalSlots) {
        grid.clear();
        int colCount = (containerName.equals("refrigerator")) ? REFRIGERATOR_COLS : INVENTORY_COLS;
        int currentSlot = 0;

        // Add slots with items
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            Item item = entry.getKey();
            int quantity = entry.getValue();
            Container<Stack> itemSlot = createItemSlot(item, quantity, containerName);
            grid.add(itemSlot).size(SLOT_SIZE, SLOT_SIZE).pad(SLOT_PAD);
            if (++currentSlot % colCount == 0) {
                grid.row();
            }
        }

        // Add empty slots to fill the rest of the grid
        while (currentSlot < totalSlots) {
            Container<Actor> emptySlot = createEmptySlot(containerName);
            grid.add(emptySlot).size(SLOT_SIZE, SLOT_SIZE).pad(SLOT_PAD);
            if (++currentSlot % colCount == 0) {
                grid.row();
            }
        }
    }

    private Container<Stack> createItemSlot(Item item, int quantity, String sourceContainer) {
        // Visual representation: Image + Quantity Label
        Image itemImage = new Image(new Texture(Gdx.files.internal(item.getImageFilepath())));
        Label quantityLabel = new Label(String.valueOf(quantity), skin);
        quantityLabel.setColor(Color.WHITE);

        Stack itemStack = new Stack();
        itemStack.add(itemImage);
        itemStack.add(quantityLabel);

        Container<Stack> container = new Container<>(itemStack);
        container.setTouchable(Touchable.enabled);

        // --- Drag and Drop Source (remains the same) ---
        dnd.addSource(new Source(container) {
            @Override
            public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                Payload payload = new Payload();
                ItemPayload itemPayload = new ItemPayload();
                itemPayload.item = item;
                itemPayload.sourceContainer = sourceContainer;
                payload.setObject(itemPayload);

                Image dragActor = new Image(new Texture(Gdx.files.internal(item.getImageFilepath())));
                dragActor.setSize(SLOT_SIZE, SLOT_SIZE);
                payload.setDragActor(dragActor);

                container.getActor().setColor(1, 1, 1, 0.4f);
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
                container.getActor().setColor(Color.WHITE);
            }
        });

        // --- NEW: Add ClickListener for Shift + Right Click ---
        container.addListener(new ClickListener(Input.Buttons.RIGHT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Check if the Shift key is held down
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {

                    // Determine source and destination
                    Map<Item, Integer> sourceMap;
                    Map<Item, Integer> targetMap;
                    int targetCapacity;

                    if (sourceContainer.equals("refrigerator")) {
                        sourceMap = refrigerator.getItems();
                        targetMap = backpack.getInventory();
                        targetCapacity = INVENTORY_ROWS * INVENTORY_COLS;
                    } else { // Source is "backpack"
                        sourceMap = backpack.getInventory();
                        targetMap = refrigerator.getItems();
                        targetCapacity = REFRIGERATOR_ROWS * REFRIGERATOR_COLS;
                    }

                    // Check if the destination has space
                    if (targetMap.size() >= targetCapacity) {
                        System.out.println("Destination is full!"); // Optional: log a message
                        return; // Do nothing if the destination is full
                    }

                    if (targetMap == refrigerator.getItems() && !(item instanceof Food || item instanceof Fruit)) {
                        System.out.println("Only food and fruit can be placed in the refrigerator.");
                        return;
                    }

                    // Perform the move
                    int itemQuantity = sourceMap.get(item);
                    sourceMap.remove(item);
                    targetMap.put(item, itemQuantity);

                    // Refresh the UI
                    populateAllGrids();
                }
            }
        });

        return container;
    }

    /**
     * Creates an empty, invisible slot that acts as a drop target.
     */
    private Container<Actor> createEmptySlot(String targetContainer) {
        Container<Actor> emptyContainer = new Container<>();
        emptyContainer.setTouchable(Touchable.enabled);

        // --- Drag and Drop Target ---
        dnd.addTarget(new Target(emptyContainer) {
            @Override
            public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
                // Highlight the slot to show it's a valid drop location
                getActor().setColor(Color.YELLOW);
                return true;
            }

            @Override
            public void reset(Source source, Payload payload) {
                // Remove highlight when the dragged item moves away
                getActor().setColor(Color.WHITE);
            }

            @Override
            public void drop(Source source, Payload payload, float x, float y, int pointer) {
                ItemPayload itemPayload = (ItemPayload) payload.getObject();
                Item draggedItem = itemPayload.item;
                String sourceName = itemPayload.sourceContainer;

                // Determine source and target maps
                Map<Item, Integer> sourceMap = sourceName.equals("refrigerator") ? refrigerator.getItems() : backpack.getInventory();
                Map<Item, Integer> targetMap = targetContainer.equals("refrigerator") ? refrigerator.getItems() : backpack.getInventory();

                if (targetMap == refrigerator.getItems() && !(draggedItem instanceof Food || draggedItem instanceof Fruit)) {
                    System.out.println("Only food and fruit can be placed in the refrigerator.");
                    return;
                }

                // Move the item
                int quantity = sourceMap.get(draggedItem);
                sourceMap.remove(draggedItem);
                targetMap.put(draggedItem, quantity);

                // Refresh the entire UI to reflect the change
                populateAllGrids();
            }

        });

        return emptyContainer;
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

        // Press ESC to go back to the previous screen
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
