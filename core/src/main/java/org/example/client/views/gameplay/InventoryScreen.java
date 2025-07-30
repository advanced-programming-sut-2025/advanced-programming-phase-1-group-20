package org.example.client.views.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import com.badlogic.gdx.graphics.Color;
import org.example.common.models.Player.Player;
import org.example.common.models.Items.Item;
import org.example.common.models.Player.Backpack;
import org.example.common.models.Items.Tool;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Simple class to mark equipped tool slots
class EquippedToolOverlay {
}

public class InventoryScreen implements Screen {
    private Stage stage;
    private Table inventoryTable;
    private Player player;
    private Skin skin;
    private Screen previousScreen;
    private boolean canClose = false;
    private Image inventoryBackgroundImage;
    private Image screenBackgroundImage;
    private TextButton upButton;
    private TextButton downButton;
    private Label pageLabel;

    // Inventory configuration
    private static final int SLOTS_PER_ROW = 9;
    private static final int ROWS_PER_PAGE = 3;
    private static final int SLOTS_PER_PAGE = SLOTS_PER_ROW * ROWS_PER_PAGE; // 27 slots
    private static final String INVENTORY_BACKGROUND = "inventory.png";
    private static final String SCREEN_BACKGROUND = "content/crafting_background.png";

    // Scrolling state
    private int currentPage = 0;
    private List<Item> allItems = new ArrayList<>();

    // Drag and drop state
    private Item draggedItem = null;
    private Image draggedItemImage = null;
    private float dragOffsetX = 0;
    private float dragOffsetY = 0;

    private static final List<String> TOOL_ORDER = Arrays.asList(
            "Basic Hoe", "Basic Pickaxe", "Basic Axe", "Basic Watering Can", "Scythe", "Initial Trash Can"
    );

    public InventoryScreen(Player player, Skin skin, Screen previousScreen) {
        this.player = player;
        this.skin = skin;
        this.previousScreen = previousScreen;
        stage = new Stage(new ScreenViewport());

        // Create screen background image
        try {
            Texture screenBackgroundTexture = new Texture(Gdx.files.internal(SCREEN_BACKGROUND));
            screenBackgroundImage = new Image(screenBackgroundTexture);
            screenBackgroundImage.setFillParent(true);
            stage.addActor(screenBackgroundImage);
        } catch (Exception e) {
            System.err.println("Failed to load screen background image: " + e.getMessage());
        }

        // Create main container
        Table mainContainer = new Table();
        mainContainer.setFillParent(true);
        mainContainer.center();

        // Create inventory background image (as a table/container)
        try {
            Texture backgroundTexture = new Texture(Gdx.files.internal(INVENTORY_BACKGROUND));
            inventoryBackgroundImage = new Image(backgroundTexture);
            // Set a reasonable size for the inventory table (not full screen)
            inventoryBackgroundImage.setSize(1000, 500);
        } catch (Exception e) {
            System.err.println("Failed to load inventory background image: " + e.getMessage());
        }

        // Create inventory table (3 rows x 9 columns) - positioned over the background image
        inventoryTable = new Table();
        inventoryTable.setSize(900, 300); // 9 * 100 = 900 width, 3 * 100 = 300 height

        // Create navigation buttons
        upButton = new TextButton("↑", skin);
        downButton = new TextButton("↓", skin);
        pageLabel = new Label("Page 1", skin);
        pageLabel.setColor(Color.WHITE);

        // Setup button listeners
        upButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (currentPage > 0) {
                    currentPage--;
                    updateInventoryDisplay();
                }
            }
        });

        downButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int maxPage = (allItems.size() - 1) / SLOTS_PER_PAGE;
                if (currentPage < maxPage) {
                    currentPage++;
                    updateInventoryDisplay();
                }
            }
        });

        // Layout the UI
        Table navigationTable = new Table();
        navigationTable.add(upButton).pad(5);
        navigationTable.add(pageLabel).pad(5);
        navigationTable.add(downButton).pad(5);

        // Create a container for the inventory background and items using Stack for proper layering
        Stack inventoryStack = new Stack();
        inventoryStack.add(inventoryBackgroundImage);
        inventoryStack.add(inventoryTable);

        // Center the inventory table within the stack
        inventoryTable.setPosition(50, 120); // Move items up by increasing Y position

        // Create a horizontal layout for inventory and trash can
        Table horizontalLayout = new Table();
        horizontalLayout.add(inventoryStack).size(1000, 500);

        // Create trash can area and make sure it's visible
        Table trashCanArea = createTrashCanArea();
        horizontalLayout.add(trashCanArea).size(200, 600).padLeft(20); // Increased height from 500 to 600

        mainContainer.add(horizontalLayout).padBottom(20).row();
        mainContainer.add(navigationTable);

        stage.addActor(mainContainer);

        // Initialize inventory
        prepareInventoryItems();
        updateInventoryDisplay();
    }

    private void prepareInventoryItems() {
        Backpack backpack = player.getBackpack();
        allItems.clear();

        // Add tools in specific order (excluding trash can)
        for (String toolName : TOOL_ORDER) {
            for (Item item : backpack.getInventory().keySet()) {
                if (item instanceof Tool && item.getName().equals(toolName)) {
                    allItems.add(item);
                    break;
                }
            }
        }

        // Add other items (excluding trash can)
        for (Item item : backpack.getInventory().keySet()) {
            if (!(item instanceof Tool) || !TOOL_ORDER.contains(item.getName())) {
                // Skip trash can items - they will be shown separately
                if (!item.getName().toLowerCase().contains("trash can")) {
                    allItems.add(item);
                }
            }
        }
    }

    private void updateInventoryDisplay() {
        inventoryTable.clear();

        // Calculate which items to show on current page
        int startIndex = currentPage * SLOTS_PER_PAGE;
        int endIndex = Math.min(startIndex + SLOTS_PER_PAGE, allItems.size());

        // Update page label
        int maxPage = (allItems.size() - 1) / SLOTS_PER_PAGE;
        pageLabel.setText("Page " + (currentPage + 1) + " / " + (maxPage + 1));

        // Update button states
        upButton.setDisabled(currentPage == 0);
        downButton.setDisabled(currentPage >= maxPage);

        // Create inventory grid (3 rows x 9 columns) - only for items that exist
        for (int row = 0; row < ROWS_PER_PAGE; row++) {
            for (int col = 0; col < SLOTS_PER_ROW; col++) {
                int itemIndex = startIndex + (row * SLOTS_PER_ROW) + col;
                if (itemIndex < allItems.size()) {
                    Table slot = createInventorySlot(itemIndex);
                    inventoryTable.add(slot).size(100, 100).pad(2);
                } else {
                    // Add empty space for missing slots (no placeholder)
                    inventoryTable.add().size(100, 100).pad(2);
                }
            }
            inventoryTable.row();
        }
    }

    private Table createInventorySlot(int itemIndex) {
        Table slot = new Table();

        Item item = allItems.get(itemIndex);
        Backpack backpack = player.getBackpack();

        try {
            // Create item image
            Texture texture = new Texture(Gdx.files.internal(item.getImageFilepath()));
            Image image = new Image(texture);

            // Make trash can items smaller
            if (item.getName().toLowerCase().contains("trash can")) {
                image.setSize(60, 60);
                slot.add(image).size(60, 60).padTop(-70).row();
            } else {
                image.setSize(80, 80);
                slot.add(image).size(80, 80).padTop(-70).row();
            }

            // Create count label
            int count = backpack.getInventory().get(item);
            Label countLabel = new Label(String.valueOf(count), skin);
            countLabel.setColor(Color.WHITE);
            countLabel.setFontScale(0.9f);

            slot.add(countLabel);

            // Highlight if it's the currently equipped tool
            if (item instanceof Tool && player.getCurrentTool() != null &&
                item.getName().equals(player.getCurrentTool().getName())) {
                // Create a shadow/overlay effect for the equipped tool
                // This will be rendered as an overlay on top of the tool
                slot.setUserObject(new EquippedToolOverlay());
            }

            // Add water level bar for watering cans
            if (item instanceof Tool && ((Tool) item).getType() == Tool.ToolType.WATERING_CAN) {
                Tool wateringCan = (Tool) item;
                float waterPercentage = wateringCan.getWaterPercentage();

                if (wateringCan.getCapacity() > 0) {
                    // Create water level bar
                    Table waterBar = new Table();
                    waterBar.setBackground(skin.newDrawable("white", new Color(0.2f, 0.6f, 1f, 0.8f)));
                    waterBar.setSize(75 * waterPercentage, 5);

                    slot.add(waterBar).size(75 * waterPercentage, 5).padTop(2);

                    // Add water level text
                    Label waterLabel = new Label(wateringCan.getWaterLevelString(), skin);
                    waterLabel.setColor(new Color(0.2f, 0.6f, 1f, 1f));
                    waterLabel.setFontScale(0.5f);
                    slot.add(waterLabel).padTop(1);
                }
            }

            // Add drag and drop functionality for non-tool items
            if (!(item instanceof Tool)) {
                slot.addListener(new DragListener() {
                    @Override
                    public void dragStart(InputEvent event, float x, float y, int pointer) {
                        // Start dragging the item
                        draggedItem = item;
                        try {
                            Texture texture = new Texture(Gdx.files.internal(item.getImageFilepath()));
                            draggedItemImage = new Image(texture);
                            draggedItemImage.setSize(60, 60);

                            // Calculate offset from mouse to image center
                            dragOffsetX = x - 30;
                            dragOffsetY = y - 30;

                            // Add the dragged image to stage
                            stage.addActor(draggedItemImage);
                        } catch (Exception e) {
                            System.err.println("Failed to create dragged item image: " + e.getMessage());
                        }
                    }

                    @Override
                    public void drag(InputEvent event, float x, float y, int pointer) {
                        // Update dragged item position
                        if (draggedItemImage != null) {
                            float worldX = event.getStageX() - dragOffsetX;
                            float worldY = event.getStageY() - dragOffsetY;
                            draggedItemImage.setPosition(worldX, worldY);
                        }
                    }

                    @Override
                    public void dragStop(InputEvent event, float x, float y, int pointer) {
                        // Handle drop
                        if (draggedItem != null) {
                            // Check if dropped on trash can
                            Actor hit = stage.hit(event.getStageX(), event.getStageY(), true);
                            if (hit != null && hit.getUserObject() != null && hit.getUserObject() instanceof Tool) {
                                Tool trashCan = (Tool) hit.getUserObject();
                                if (trashCan.getType() == Tool.ToolType.TRASH_CAN) {
                                    sellItemToTrashCan(draggedItem, trashCan);
                                }
                            }

                            // Clean up dragged item
                            if (draggedItemImage != null) {
                                draggedItemImage.remove();
                                draggedItemImage = null;
                            }
                            draggedItem = null;
                        }
                    }
                });
            }

            // Add click listener for tools
            if (item instanceof Tool) {
                slot.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        player.equipTool(item.getName());
                        Main.getGame().setScreen(previousScreen);
                    }
                });
            }

        } catch (Exception e) {
            // Fallback if image loading fails
            System.err.println("Failed to load image for item: " + item.getName() + " - " + e.getMessage());
            Label fallbackLabel = new Label(item.getName(), skin);
            fallbackLabel.setColor(Color.WHITE);
            fallbackLabel.setFontScale(0.8f);
            slot.add(fallbackLabel).size(80, 80).padTop(-10);

            int count = backpack.getInventory().get(item);
            Label countLabel = new Label(String.valueOf(count), skin);
            countLabel.setColor(Color.WHITE);
            countLabel.setFontScale(0.9f);
            slot.add(countLabel);
        }

        return slot;
    }

        private Table createTrashCanArea() {
        Table trashCanArea = new Table();
        trashCanArea.setBackground(skin.newDrawable("white", new Color(0.2f, 0.2f, 0.2f, 0.8f)));

        // Find the best trash can (highest return percentage)
        Backpack backpack = player.getBackpack();
        final Tool bestTrashCan = findBestTrashCan(backpack);
        final double bestReturnPercentage = bestTrashCan != null ? bestTrashCan.getReturnPercentage() : 0;

        System.out.println("Trash can search result: " + (bestTrashCan != null ? bestTrashCan.getName() : "null"));
        System.out.println("Backpack items: " + backpack.getInventory().keySet().stream()
            .map(Item::getName)
            .collect(java.util.stream.Collectors.joining(", ")));

        if (bestTrashCan != null) {
            System.out.println("Creating trash can display for: " + bestTrashCan.getName());
                            // Create trash can display
                Table trashCanSlot = new Table();
                trashCanSlot.setBackground(skin.newDrawable("white", new Color(0.3f, 0.3f, 0.3f, 0.9f)));
                trashCanSlot.setUserObject(bestTrashCan); // Set user object for drop detection

            try {
                                    // Create item image
                    Texture texture = new Texture(Gdx.files.internal(bestTrashCan.getImageFilepath()));
                    Image image = new Image(texture);
                    image.setSize(90, 120); // Made taller: 90 width, 120 height
                    trashCanSlot.add(image).size(90, 120).pad(15).row();

                                    // Add return percentage info
                    Label returnLabel = new Label(String.format("%.0f%% Return", bestReturnPercentage * 100), skin);
                    returnLabel.setColor(Color.YELLOW);
                    returnLabel.setFontScale(0.8f); // Increased font scale
                    trashCanSlot.add(returnLabel).padTop(5);

                // Add click listener to show trash dialog
                trashCanSlot.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        showTrashDialog(bestTrashCan);
                    }
                });

                // Add visual feedback when dragging over trash can
                trashCanSlot.addListener(new DragListener() {
                    @Override
                    public void dragStart(InputEvent event, float x, float y, int pointer) {
                        // Not used for drop target
                    }

                    @Override
                    public void drag(InputEvent event, float x, float y, int pointer) {
                        // Highlight trash can when dragging over it
                        if (draggedItem != null && !(draggedItem instanceof Tool)) {
                            trashCanSlot.setColor(Color.YELLOW);
                        }
                    }

                    @Override
                    public void dragStop(InputEvent event, float x, float y, int pointer) {
                        // Reset color
                        trashCanSlot.setColor(Color.WHITE);
                    }
                });

            } catch (Exception e) {
                // Fallback if image loading fails
                Label fallbackLabel = new Label("Trash Can", skin);
                fallbackLabel.setColor(Color.WHITE);
                fallbackLabel.setFontScale(0.6f);
                trashCanSlot.add(fallbackLabel).size(80, 80).pad(10);

                Label returnLabel = new Label(String.format("%.0f%% Return", bestReturnPercentage * 100), skin);
                returnLabel.setColor(Color.YELLOW);
                returnLabel.setFontScale(0.6f);
                trashCanSlot.add(returnLabel);
            }

                                trashCanArea.add(trashCanSlot).size(120, 150).pad(15); // Increased size and padding
        } else {
            // Fallback: try to find any trash can, even with 0% return rate
            System.out.println("No trash can found with best return rate, searching for any trash can...");
            for (Item item : backpack.getInventory().keySet()) {
                if (item instanceof Tool && ((Tool) item).getType() == Tool.ToolType.TRASH_CAN) {
                    Tool anyTrashCan = (Tool) item;
                    System.out.println("Found fallback trash can: " + anyTrashCan.getName());

                    // Create trash can display for the fallback
                    Table trashCanSlot = new Table();
                    trashCanSlot.setBackground(skin.newDrawable("white", new Color(0.3f, 0.3f, 0.3f, 0.9f)));
                    trashCanSlot.setUserObject(anyTrashCan); // Set user object for drop detection

                    try {
                        Texture texture = new Texture(Gdx.files.internal(anyTrashCan.getImageFilepath()));
                        Image image = new Image(texture);
                        image.setSize(90, 120); // Made taller: 90 width, 120 height
                        trashCanSlot.add(image).size(90, 120).pad(15).row();

                        Label returnLabel = new Label(String.format("%.0f%% Return", anyTrashCan.getReturnPercentage() * 100), skin);
                        returnLabel.setColor(Color.YELLOW);
                        returnLabel.setFontScale(0.6f);
                        trashCanSlot.add(returnLabel);

                        trashCanSlot.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                showTrashDialog(anyTrashCan);
                            }
                        });

                        // Add visual feedback when dragging over fallback trash can
                        trashCanSlot.addListener(new DragListener() {
                            @Override
                            public void dragStart(InputEvent event, float x, float y, int pointer) {
                                // Not used for drop target
                            }

                            @Override
                            public void drag(InputEvent event, float x, float y, int pointer) {
                                // Highlight trash can when dragging over it
                                if (draggedItem != null && !(draggedItem instanceof Tool)) {
                                    trashCanSlot.setColor(Color.YELLOW);
                                }
                            }

                            @Override
                            public void dragStop(InputEvent event, float x, float y, int pointer) {
                                // Reset color
                                trashCanSlot.setColor(Color.WHITE);
                            }
                        });

                    } catch (Exception e) {
                        Label fallbackLabel = new Label("Trash Can", skin);
                        fallbackLabel.setColor(Color.WHITE);
                        fallbackLabel.setFontScale(0.6f);
                        trashCanSlot.add(fallbackLabel).size(90, 120).pad(15); // Made taller: 90 width, 120 height

                        Label returnLabel = new Label(String.format("%.0f%% Return", anyTrashCan.getReturnPercentage() * 100), skin);
                        returnLabel.setColor(Color.YELLOW);
                        returnLabel.setFontScale(0.8f); // Increased font scale
                        trashCanSlot.add(returnLabel).padTop(5);
                    }

                    trashCanArea.add(trashCanSlot).size(120, 150).pad(15); // Increased size and padding
                    return trashCanArea;
                }
            }

            Label noTrashCanLabel = new Label("No Trash Can\nAvailable", skin);
            noTrashCanLabel.setColor(Color.GRAY);
            noTrashCanLabel.setFontScale(0.6f);
            trashCanArea.add(noTrashCanLabel).pad(20);
        }

        return trashCanArea;
    }

        private Tool findBestTrashCan(Backpack backpack) {
        Tool bestTrashCan = null;
        double bestReturnPercentage = 0;

        for (Item item : backpack.getInventory().keySet()) {
            System.out.println("Checking item: " + item.getName() + " (type: " + item.getClass().getSimpleName() + ")");
            if (item instanceof Tool) {
                Tool tool = (Tool) item;
                System.out.println("Tool type: " + tool.getType() + ", name: " + tool.getName());
                if (tool.getType() == Tool.ToolType.TRASH_CAN) {
                    System.out.println("Found trash can: " + tool.getName() + " with return percentage: " + tool.getReturnPercentage());
                    if (tool.getReturnPercentage() > bestReturnPercentage) {
                        bestTrashCan = tool;
                        bestReturnPercentage = tool.getReturnPercentage();
                    }
                }
            }
        }

        System.out.println("Best trash can found: " + (bestTrashCan != null ? bestTrashCan.getName() : "null"));
        return bestTrashCan;
    }

    private void showTrashDialog(Tool trashCan) {
        // Create a dialog to show trash can functionality
        Table dialog = new Table();
        dialog.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.9f)));
        dialog.setSize(400, 300);
        dialog.setPosition(Gdx.graphics.getWidth() / 2 - 200, Gdx.graphics.getHeight() / 2 - 150);

        // Add title
        Label titleLabel = new Label("Trash Can - " + trashCan.getName(), skin);
        titleLabel.setColor(Color.WHITE);
        titleLabel.setFontScale(1.2f);
        dialog.add(titleLabel).padBottom(20).row();

        // Add description
        Label descLabel = new Label("Return Rate: " + String.format("%.0f%%", trashCan.getReturnPercentage() * 100), skin);
        descLabel.setColor(Color.YELLOW);
        descLabel.setFontScale(0.8f);
        dialog.add(descLabel).padBottom(10).row();

        Label infoLabel = new Label("Drag items here to sell them\nfor money based on their quality.", skin);
        infoLabel.setColor(Color.WHITE);
        infoLabel.setFontScale(0.7f);
        dialog.add(infoLabel).padBottom(20).row();

        // Add close button
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.remove();
            }
        });
        dialog.add(closeButton);

        stage.addActor(dialog);
    }

    private void sellItemToTrashCan(Item item, Tool trashCan) {
        if (item == null || trashCan == null) return;

        Backpack backpack = player.getBackpack();
        int itemCount = backpack.getInventory().get(item);

        if (itemCount > 0) {
            // Calculate sell price based on item quality and trash can return percentage
            double returnPercentage = trashCan.getReturnPercentage();
            int sellPrice = (int) (item.getPrice() * item.getQuality().getPercentage() * returnPercentage);

            // Remove item from inventory
            backpack.remove(item, 1);

            // Add money to player
            player.increaseMoney(sellPrice);

            // Show confirmation dialog
            showSellConfirmation(item.getName(), sellPrice);

            // Refresh inventory display
            prepareInventoryItems();
            updateInventoryDisplay();
        }
    }

    private void showSellConfirmation(String itemName, int sellPrice) {
        Table dialog = new Table();
        dialog.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0.9f)));
        dialog.setSize(300, 150);
        dialog.setPosition(Gdx.graphics.getWidth() / 2 - 150, Gdx.graphics.getHeight() / 2 - 75);

        Label titleLabel = new Label("Item Sold!", skin);
        titleLabel.setColor(Color.GREEN);
        titleLabel.setFontScale(1.0f);
        dialog.add(titleLabel).padBottom(10).row();

        Label itemLabel = new Label(itemName + " sold for " + sellPrice + "g", skin);
        itemLabel.setColor(Color.WHITE);
        itemLabel.setFontScale(0.8f);
        dialog.add(itemLabel).padBottom(20).row();

        TextButton okButton = new TextButton("OK", skin);
        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.remove();
            }
        });
        dialog.add(okButton);

        stage.addActor(dialog);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        canClose = false;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        // Render equipped tool overlay
        renderEquippedToolOverlay();

        if (!Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            canClose = true;
        }
        if (canClose && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Main.getGame().setScreen(previousScreen);
        }

        // Handle keyboard navigation
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            if (currentPage > 0) {
                currentPage--;
                updateInventoryDisplay();
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            int maxPage = (allItems.size() - 1) / SLOTS_PER_PAGE;
            if (currentPage < maxPage) {
                currentPage++;
                updateInventoryDisplay();
            }
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
    private void renderEquippedToolOverlay() {
        // Find the equipped tool slot and render overlay
        for (int row = 0; row < ROWS_PER_PAGE; row++) {
            for (int col = 0; col < SLOTS_PER_ROW; col++) {
                int itemIndex = currentPage * SLOTS_PER_PAGE + (row * SLOTS_PER_ROW) + col;
                if (itemIndex < allItems.size()) {
                    Item item = allItems.get(itemIndex);
                    if (item instanceof Tool && player.getCurrentTool() != null &&
                        item.getName().equals(player.getCurrentTool().getName())) {

                        // Calculate position of this slot
                        float slotX = 50 + (col * 102); // 50 is inventory table X position, 102 is slot width + padding
                        float slotY = 120 + ((ROWS_PER_PAGE - 1 - row) * 102); // 120 is inventory table Y position, 102 is slot height + padding

                        // Begin batch for overlay rendering
                        Main.getBatch().begin();

                        // Draw a bright yellow border overlay (94x94 to match cell size)
                        Main.getBatch().setColor(new Color(1f, 1f, 0f, 0.8f)); // Bright yellow with transparency
                        Main.getBatch().draw(skin.getRegion("white"), slotX + 4, slotY + 4, 94, 94);

                        // Draw a darker border outline
                        Main.getBatch().setColor(new Color(1f, 0.8f, 0f, 1f)); // Darker yellow for border
                        Main.getBatch().draw(skin.getRegion("white"), slotX + 4, slotY + 4, 94, 2); // Top border
                        Main.getBatch().draw(skin.getRegion("white"), slotX + 4, slotY + 96, 94, 2); // Bottom border
                        Main.getBatch().draw(skin.getRegion("white"), slotX + 4, slotY + 4, 2, 94); // Left border
                        Main.getBatch().draw(skin.getRegion("white"), slotX + 96, slotY + 4, 2, 94); // Right border

                        // Reset color
                        Main.getBatch().setColor(Color.WHITE);
                        Main.getBatch().end();

                        return; // Only one equipped tool at a time
                    }
                }
            }
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
