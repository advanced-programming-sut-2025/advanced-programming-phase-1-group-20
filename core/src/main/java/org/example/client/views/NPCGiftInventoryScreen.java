package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.common.models.App;
import org.example.common.models.Items.Item;
import org.example.common.models.Items.Tool;
import org.example.common.models.entities.NPC;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NPCGiftInventoryScreen implements Screen {
    private Stage stage;
    private ScrollPane scrollPane;
    private DragAndDrop dragAndDrop;
    private Table mainTable;
    private Table inventoryTable;
    private Player currentPlayer;
    private NPC npcToGift;
    private Skin skin;
    private Screen previousScreen;

    // Gift selection state
    private Item selectedItem;
    private int selectedQuantity = 1;
    private Label selectedItemLabel;
    private TextField quantityField;

    public NPCGiftInventoryScreen(Player currentPlayer, Skin skin, Screen previousScreen, NPC npcToGift) {
        this.currentPlayer = currentPlayer;
        this.skin = skin;
        this.previousScreen = previousScreen;
        this.npcToGift = npcToGift;

        stage = new Stage(new ScreenViewport());
        createUI();
    }

    private void createUI() {
        // Main container
        mainTable = new Table();
        mainTable.setFillParent(true);
        dragAndDrop = new DragAndDrop();

        // Background
        try {
            Image background = new Image(new Texture(Gdx.files.internal("content/crafting_background.png")));
            background.setFillParent(true);
            stage.addActor(background);
        } catch (Exception e) {
            System.err.println("Failed to load background image: " + e.getMessage());
        }

        // Title
        Label titleLabel = new Label("Select Gift for " + npcToGift.getName(), skin);
        titleLabel.setFontScale(1.8f);
        titleLabel.setColor(Color.GOLD);
        mainTable.add(titleLabel).padTop(20).padBottom(20).row();

        // Inventory display
        createInventoryDisplay();

        // Selection info panel
        createSelectionPanel();

        // Action buttons
        createActionButtons();

        stage.addActor(mainTable);
    }

    private void createInventoryDisplay() {
        inventoryTable = new Table();
        inventoryTable.top();

        // Get all items from player's backpack
        List<Item> allItems = new ArrayList<>();
        for (Map.Entry<Item, Integer> entry : currentPlayer.getBackpack().getInventory().entrySet()) {
            Item item = entry.getKey();
            if (!(item instanceof Tool)) { // Don't allow gifting tools
                allItems.add(item);
            }
        }

        if (allItems.isEmpty()) {
            Label noItemsLabel = new Label("No items available to gift!", skin);
            noItemsLabel.setColor(Color.RED);
            inventoryTable.add(noItemsLabel).pad(20);
        } else {
            // Create item slots
            int itemsPerRow = 5;
            int currentRow = 0;
            int currentCol = 0;

            for (Item item : allItems) {
                int availableQuantity = currentPlayer.getBackpack().getNumberOfItem(item.getName());
                Table itemSlot = createItemSlot(item, availableQuantity);

                inventoryTable.add(itemSlot).size(80, 100).pad(5);

                currentCol++;
                if (currentCol >= itemsPerRow) {
                    inventoryTable.row();
                    currentCol = 0;
                    currentRow++;
                }
            }
        }

        // Create scroll pane for inventory
        scrollPane = new ScrollPane(inventoryTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(false, true);

        mainTable.add(scrollPane).expand().fill().pad(20);
    }

    private Table createItemSlot(Item item, int availableQuantity) {
        Table slot = new Table();
        slot.setBackground(skin.newDrawable("white", new Color(0.2f, 0.2f, 0.2f, 0.8f)));

        // Item image placeholder (items don't have getImagePath method)
        Label placeholder = new Label(item.getName().substring(0, Math.min(3, item.getName().length())), skin);
        placeholder.setFontScale(1.5f);
        placeholder.setColor(Color.WHITE);
        slot.add(placeholder).size(60, 60).row();

        // Item name
        Label nameLabel = new Label(item.getName(), skin);
        nameLabel.setFontScale(0.7f);
        nameLabel.setColor(Color.WHITE);
        slot.add(nameLabel).row();

        // Quantity
        Label quantityLabel = new Label("x" + availableQuantity, skin);
        quantityLabel.setFontScale(0.6f);
        quantityLabel.setColor(Color.CYAN);
        slot.add(quantityLabel);

        // Check if this is a favorite item for the NPC
        if (npcToGift.isFavoriteItem(item)) {
            slot.setBackground(skin.newDrawable("white", new Color(0.8f, 0.6f, 0.2f, 0.8f))); // Gold background for favorites
            Label favoriteLabel = new Label("★", skin);
            favoriteLabel.setColor(Color.YELLOW);
            favoriteLabel.setFontScale(1.2f);
            slot.add(favoriteLabel).padTop(5);
        }

        dragAndDrop.addSource(new DragAndDrop.Source(slot) {
            public DragAndDrop.Payload dragStart(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer) {
                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setObject(item);

                Table dragActor = new Table();
                dragActor.setBackground(skin.newDrawable("white", new Color(0.4f, 0.4f, 0.4f, 0.7f)));
                Label dragLabel = new Label(item.getName(), skin);
                dragActor.add(dragLabel);
                payload.setDragActor(dragActor);

                slot.setVisible(false);

                return payload;
            }

            @Override
            public void dragStop(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, DragAndDrop.Payload payload, DragAndDrop.Target target) {
                slot.setVisible(true);
            }
        });

        return slot;
    }

    private void selectItem(Item item, int availableQuantity) {
        selectedItem = item;
        selectedQuantity = Math.min(1, availableQuantity); // Default to 1

        // Update selection display
        updateSelectionDisplay();
    }

    private void createSelectionPanel() {
        Table selectionPanel = new Table();
        selectionPanel.setBackground(skin.newDrawable("white", new Color(0.3f, 0.3f, 0.3f, 0.9f)));
        selectionPanel.pad(15);

        // Title
        Label titleLabel = new Label("Selected Gift", skin);
        titleLabel.setFontScale(1.2f);
        titleLabel.setColor(Color.WHITE);
        selectionPanel.add(titleLabel).padBottom(10).row();

        // Selected item info
        selectedItemLabel = new Label("Drag an item here", skin);
        selectedItemLabel.setColor(Color.LIGHT_GRAY);
        selectionPanel.add(selectedItemLabel).padBottom(10).row();

        // Quantity selection
        Label quantityLabel = new Label("Quantity:", skin);
        quantityLabel.setColor(Color.WHITE);
        selectionPanel.add(quantityLabel).left().padBottom(5).row();

        quantityField = new TextField("1", skin);
        quantityField.setMaxLength(3);
        quantityField.setTextFieldListener((textField, key) -> {
            try {
                int newQuantity = Integer.parseInt(textField.getText());
                if (newQuantity > 0 && selectedItem != null) {
                    int maxQuantity = currentPlayer.getBackpack().getNumberOfItem(selectedItem.getName());
                    selectedQuantity = Math.min(newQuantity, maxQuantity);
                    textField.setText(String.valueOf(selectedQuantity));
                }
            } catch (NumberFormatException e) {
                textField.setText("1");
                selectedQuantity = 1;
            }
        });
        selectionPanel.add(quantityField).width(100).left().padBottom(10).row();

        // Gift info
        if (selectedItem != null && npcToGift.isFavoriteItem(selectedItem)) {
            Label favoriteInfo = new Label("★ This is " + npcToGift.getName() + "'s favorite item!", skin);
            favoriteInfo.setColor(Color.YELLOW);
            favoriteInfo.setFontScale(0.9f);
            selectionPanel.add(favoriteInfo).padTop(5);
        }

        mainTable.add(selectionPanel).width(300).fillY().pad(20);

        // Define the selection panel as a drop target
        dragAndDrop.addTarget(new DragAndDrop.Target(selectionPanel) {
            final Color normalColor = new Color(0.3f, 0.3f, 0.3f, 0.9f);
            final Color hoverColor = new Color(0.4f, 0.5f, 0.4f, 0.9f);

            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                getActor().setColor(hoverColor);
                return true; // Accept the drop
            }

            @Override
            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                getActor().setColor(normalColor);
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                Item droppedItem = (Item) payload.getObject();
                if (droppedItem != null) {
                    int availableQuantity = currentPlayer.getBackpack().getNumberOfItem(droppedItem.getName());
                    selectItem(droppedItem, availableQuantity);
                }
            }
        });
    }

    private void createActionButtons() {
        Table buttonTable = new Table();

        // Give Gift button
        TextButton giveGiftButton = new TextButton("Give Gift", skin);
        giveGiftButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                giveGift();
            }
        });

        // Back button
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                goBack();
            }
        });

        buttonTable.add(giveGiftButton).width(120).height(40).pad(10);
        buttonTable.add(backButton).width(120).height(40).pad(10);

        mainTable.add(buttonTable).padBottom(20);
    }

    private void updateSelectionDisplay() {
        if (selectedItem != null) {
            String displayText = selectedItem.getName();
            if (npcToGift.isFavoriteItem(selectedItem)) {
                displayText += " ★ (Favorite!)";
            }
            selectedItemLabel.setText(displayText);
            selectedItemLabel.setColor(Color.WHITE);
        } else {
            selectedItemLabel.setText("Drag an item here");
            selectedItemLabel.setColor(Color.LIGHT_GRAY);
        }
        quantityField.setText(String.valueOf(selectedQuantity));
    }

    private void giveGift() {
        if (selectedItem == null) {
            showErrorDialog("No item selected!");
            return;
        }

        if (selectedQuantity <= 0) {
            showErrorDialog("Invalid quantity!");
            return;
        }

        // Check if player has enough of the item
        int availableQuantity = currentPlayer.getBackpack().getNumberOfItem(selectedItem.getName());
        if (availableQuantity < selectedQuantity) {
            showErrorDialog("You don't have enough " + selectedItem.getName() + "!");
            return;
        }

        // Use the Player's giftNPC method directly
        boolean success = currentPlayer.giftNPC(npcToGift, selectedItem);

        if (success) {
            String message = "You gave " + selectedItem.getName() + " to " + npcToGift.getName() + ".";
            if (npcToGift.isFavoriteItem(selectedItem)) {
                message += " They loved your gift!";
            } else {
                message += " They appreciated your gift.";
            }
            showSuccessDialog(message);
            // Refresh the inventory display
            refreshInventoryDisplay();
        } else {
            showErrorDialog("Failed to give gift to " + npcToGift.getName() + ".");
        }
    }

    private void refreshInventoryDisplay() {
        // This is a simplified refresh. For a more robust solution, you might update individual slots.
        // For now, we recreate the inventory display part.
        mainTable.getCells().get(1).setActor(null); // Clear the old scrollpane cell
        createInventoryDisplay();

        // Reset selection
        selectedItem = null;
        selectedQuantity = 1;
        updateSelectionDisplay();
    }

    private void showErrorDialog(String message) {
        Dialog errorDialog = new Dialog("Error", skin);
        errorDialog.text(message);
        errorDialog.button("OK");
        errorDialog.show(stage);
    }

    private void showSuccessDialog(String message) {
        Dialog successDialog = new Dialog("Success", skin);
        successDialog.text(message);
        successDialog.button("OK");
        successDialog.show(stage);
    }

    private void goBack() {
        Main.getGame().setScreen(previousScreen);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
