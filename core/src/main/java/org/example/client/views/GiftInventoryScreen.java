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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.common.models.Items.Item;
import org.example.common.models.Player.Backpack;
import org.example.common.models.Player.Player;
import org.example.common.models.entities.FriendShip;

import java.util.Map;

public class GiftInventoryScreen implements Screen {
    private Stage stage;
    private Table mainTable;
    private Table inventoryTable;
    private Player currentPlayer;
    private Player friendToGift;
    private Skin skin;
    private Screen previousScreen;
    private ScrollPane scrollPane;

    // Gift selection state
    private Item selectedItem;
    private int selectedQuantity = 1;
    private Label selectedItemLabel;
    private TextField quantityField;

    public GiftInventoryScreen(Player currentPlayer, Skin skin, Screen previousScreen, Player friendToGift) {
        this.currentPlayer = currentPlayer;
        this.skin = skin;
        this.previousScreen = previousScreen;
        this.friendToGift = friendToGift;

        stage = new Stage(new ScreenViewport());
        createUI();
    }

    private void createUI() {
        // Main container
        mainTable = new Table();
        mainTable.setFillParent(true);

        // Background
        try {
            Image background = new Image(new Texture(Gdx.files.internal("content/crafting_background.png")));
            background.setFillParent(true);
            stage.addActor(background);
        } catch (Exception e) {
            System.err.println("Failed to load background image: " + e.getMessage());
        }

        // Title
        String friendName = friendToGift.getUser() != null ? friendToGift.getUser().getUsername() : "Unknown Player";
        Label titleLabel = new Label("Select Gift for " + friendName, skin);
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

        // Get player's inventory
        Backpack backpack = currentPlayer.getBackpack();
        Map<Item, Integer> inventory = backpack.getInventory();

        if (inventory.isEmpty()) {
            Label emptyLabel = new Label("Your inventory is empty. Nothing to gift!", skin);
            emptyLabel.setColor(Color.LIGHT_GRAY);
            inventoryTable.add(emptyLabel).pad(20);
        } else {
            // Create grid layout for items (3 columns)
            int itemCount = 0;
            for (Map.Entry<Item, Integer> entry : inventory.entrySet()) {
                Item item = entry.getKey();
                int quantity = entry.getValue();

                Table itemSlot = createItemSlot(item, quantity);
                inventoryTable.add(itemSlot).size(120, 120).pad(5);

                itemCount++;
                if (itemCount % 3 == 0) {
                    inventoryTable.row();
                }
            }
        }

        // Create scroll pane for inventory
        scrollPane = new ScrollPane(inventoryTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        mainTable.add(scrollPane).expand().fill().pad(20).row();
    }

    private Table createItemSlot(Item item, int availableQuantity) {
        Table slot = new Table();
        slot.setBackground(new Texture("content/ui/empty_slot.png") != null ?
                          new Image(new Texture("content/ui/empty_slot.png")).getDrawable() : null);

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

        // Click listener
        slot.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectItem(item, availableQuantity);
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

        Label selectionTitle = new Label("Selected Item:", skin);
        selectionTitle.setFontScale(1.2f);
        selectionTitle.setColor(Color.YELLOW);
        selectionPanel.add(selectionTitle).padTop(10).padBottom(10).row();

        selectedItemLabel = new Label("None selected", skin);
        selectedItemLabel.setColor(Color.WHITE);
        selectionPanel.add(selectedItemLabel).padBottom(10).row();

        // Quantity selection
        Table quantityTable = new Table();
        Label quantityLabel = new Label("Quantity:", skin);
        quantityLabel.setColor(Color.WHITE);
        quantityTable.add(quantityLabel).padRight(10);

        quantityField = new TextField("1", skin);
        quantityField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        quantityField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateQuantityFromField();
            }
        });
        quantityTable.add(quantityField).width(100);

        selectionPanel.add(quantityTable).padBottom(15);

        mainTable.add(selectionPanel).fillX().padLeft(20).padRight(20).row();
    }

    private void updateSelectionDisplay() {
        if (selectedItem != null) {
            selectedItemLabel.setText(selectedItem.getName());
            quantityField.setText(String.valueOf(selectedQuantity));
        } else {
            selectedItemLabel.setText("None selected");
            quantityField.setText("1");
        }
    }

    private void updateQuantityFromField() {
        try {
            int newQuantity = Integer.parseInt(quantityField.getText());
            if (newQuantity > 0 && selectedItem != null) {
                // Check if player has enough of this item
                int availableQuantity = currentPlayer.getBackpack().getNumberOfItem(selectedItem.getName());
                selectedQuantity = Math.min(newQuantity, availableQuantity);

                // Update field if we capped the quantity
                if (selectedQuantity != newQuantity) {
                    quantityField.setText(String.valueOf(selectedQuantity));
                }
            }
        } catch (NumberFormatException e) {
            quantityField.setText(String.valueOf(selectedQuantity));
        }
    }

    private void createActionButtons() {
        Table buttonTable = new Table();

        // Send Gift button
        TextButton sendButton = new TextButton("Send Gift", skin);
        sendButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sendGift();
            }
        });

        // Cancel button
        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                goBack();
            }
        });

        buttonTable.add(sendButton).pad(10).width(120).height(40);
        buttonTable.add(cancelButton).pad(10).width(120).height(40);

        mainTable.add(buttonTable).padBottom(20);
    }

    private void sendGift() {
        if (selectedItem == null) {
            showErrorDialog("Please select an item to gift!");
            return;
        }

        if (selectedQuantity <= 0) {
            showErrorDialog("Please select a valid quantity!");
            return;
        }

        // Check if player has enough of the item
        int availableQuantity = currentPlayer.getBackpack().getNumberOfItem(selectedItem.getName());
        if (availableQuantity < selectedQuantity) {
            showErrorDialog("You don't have enough of this item!");
            return;
        }

        // Get friendship and attempt to send gift
        FriendShip friendship = currentPlayer.getFriendship(friendToGift);

        // Check friendship level requirement
        if (friendship.getLevel() < FriendShip.LEVEL_1) {
            showErrorDialog("You need friendship level 1 or higher to send gifts!");
            return;
        }

        // Send the gift
        if (friendship.gift(selectedItem, currentPlayer, selectedQuantity)) {
            showSuccessDialog("Gift sent successfully!");

            // Send notification to recipient (if online)
            sendGiftNotification();

            // Go back to friends window
            goBack();
        } else {
            showErrorDialog("Failed to send gift. You may have already gifted today or players are not adjacent.");
        }
    }

    private void sendGiftNotification() {
        String friendName = friendToGift.getUser() != null ? friendToGift.getUser().getUsername() : "Unknown Player";
        String senderName = currentPlayer.getUser() != null ? currentPlayer.getUser().getUsername() : "Unknown Player";

        // Create gift notification
        org.example.common.network.events.GiftNotification notification =
            new org.example.common.network.events.GiftNotification(
                senderName,
                selectedItem.getName(),
                selectedQuantity,
                senderName,
                friendName
            );

        // For now, just log the gift - in a full multiplayer setup, this would be sent through the network
        System.out.println("Gift notification: " + notification.getDisplayMessage());

        // TODO: In a multiplayer environment, send this notification through the network client
//         if (App.getGame().isMultiplayer){
//             NetworkClient.getInstance().sendNotification(notification);
//         }
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
    public void show() {
        Gdx.input.setInputProcessor(stage);
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
