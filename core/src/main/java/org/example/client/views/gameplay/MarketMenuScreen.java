package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.client.controllers.MarketController;
import org.example.common.models.Items.Item;
import org.example.common.models.Market;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Result;
import org.example.common.models.enums.Seasons;

import java.util.HashMap;
import java.util.Map;

public class MarketMenuScreen implements Screen, Disposable {

    private Stage stage;
    private Skin skin;
    private Market market;
    private Player player;
    private Seasons currentSeason;
    private Screen previousScreen;

    private MarketController controller;

    // UI Elements
    private Table rootTable;
    private Table itemDisplayTable; // Table within the scroll pane for items
    private ScrollPane scrollPane;
    private Label moneyLabel;
    private TextField quantityField;
    private Dialog buyConfirmationDialog; // Declare buyConfirmationDialog here
    private Dialog errorDialog; // Declare errorDialog here


    private HashMap<Item, Double> currentDisplayStock;

    public MarketMenuScreen(Market market, Player player , Skin skin , Screen previousScreen, Seasons currentSeason) {
        this.market = market;
        this.player = player;
        this.currentSeason = currentSeason;
        this.previousScreen = previousScreen;

        controller = new MarketController(player, market);

        this.market.initializeTotalStock(currentSeason);
        this.currentDisplayStock = market.getPermanentStock();

        stage = new Stage(new ScreenViewport());
        this.skin = skin;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        createUI();
        updateMoneyLabel();
        displayItems(currentDisplayStock);
    }

    private void createUI() {
        rootTable = new Table(skin);
        rootTable.setFillParent(true);


//        Texture backgroundTexture = new Texture(Gdx.files.internal("content/MarketMenus/JojaMart.png"));
//
//        Image background = new Image(backgroundTexture);
//        background.setFillParent(true);
//        Drawable backgroundDrawable = background.getDrawable();
//        rootTable.setBackground(backgroundDrawable);

        // --- Top Bar (Player Money, Market Name) ---
        Table topBar = new Table(skin);
        topBar.add(new Label("Market: " + market.getName(), skin)).expandX().left().padLeft(10);
        moneyLabel = new Label("Money: $" + player.getMoney(), skin);
        topBar.add(moneyLabel).expandX().right().padRight(10);
        rootTable.add(topBar).growX().row();

        // --- Filter Buttons ---
        Table filterButtons = new Table(skin);
        TextButton permanentBtn = new TextButton("Permanent Stock", skin);
        permanentBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentDisplayStock = market.getPermanentStock();
                displayItems(currentDisplayStock);
            }
        });
        filterButtons.add(permanentBtn).pad(5);

        TextButton seasonBtn = new TextButton(currentSeason.name() + " Stock", skin);
        seasonBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                switch (currentSeason) {
                    case SPRING: currentDisplayStock = market.getSpringStock(); break;
                    case SUMMER: currentDisplayStock = market.getSummerStock(); break;
                    case AUTUMN: currentDisplayStock = market.getAutumnStock(); break;
                    case WINTER: currentDisplayStock = market.getWinterStock(); break;
                }
                displayItems(currentDisplayStock);
            }
        });
        filterButtons.add(seasonBtn).pad(5);

        TextButton allStockBtn = new TextButton("All Available Stock", skin);
        allStockBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // This means permanent + the *currently active* seasonal stock for purchase.
                // market.totalStock already contains permanent + current season's stock after initializeTotalStock()
                currentDisplayStock = market.getTotalStock(); // Use totalStock as it's the "buyable" stock
                displayItems(currentDisplayStock);
            }
        });
        filterButtons.add(allStockBtn).pad(5);
        rootTable.add(filterButtons).padTop(10).row();

        // --- Item Display Area (Scrollable) ---
        itemDisplayTable = new Table(skin);
        // itemDisplayTable.setDebug(true); // Uncomment to see item cells
        scrollPane = new ScrollPane(itemDisplayTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollbarsVisible(true);
        rootTable.add(scrollPane).expand().fill().pad(10).row();

        // --- Bottom Bar (Exit Button) ---
        TextButton exitButton = new TextButton("Exit Market", skin);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getGame().setScreen(previousScreen);
            }
        });
        rootTable.add(exitButton).width(200).height(50).padBottom(10);

        stage.addActor(rootTable);
    }

    private void displayItems(HashMap<Item, Double> stockMap) {
        itemDisplayTable.clearChildren(); // Clear previous items

        if (stockMap.isEmpty()) {
            itemDisplayTable.add(new Label("No items in this category.", skin)).pad(20).center();
            return;
        }

        // Add a header row
        itemDisplayTable.add(new Label("Item Name", skin, "default")).expandX().pad(5);
        itemDisplayTable.add(new Label("Price", skin, "default")).width(80).pad(5);
        itemDisplayTable.add(new Label("Stock", skin, "default")).width(80).pad(5);
        itemDisplayTable.add(new Label("", skin)).width(100).pad(5).row(); // Empty cell for Buy button column

        for (Map.Entry<Item, Double> entry : stockMap.entrySet()) {
            Item item = entry.getKey();
            double stock = entry.getValue();

            // Item Name (can be multiline if description is long)
            Label nameLabel = new Label(item.getName(), skin);
            nameLabel.setWrap(true);
            itemDisplayTable.add(nameLabel).expandX().fillX().pad(5).align(Align.left);

            // Price
            itemDisplayTable.add(new Label("$" + item.getPrice(), skin)).width(80).pad(5);

            // Stock
            itemDisplayTable.add(new Label(String.valueOf((int) stock), skin)).width(80).pad(5); // Assuming integer stock display

            // Buy Button
            TextButton buyButton = new TextButton("Buy", skin);
            if (stock <= 0) {
                buyButton.setText("Out of Stock");
                buyButton.setDisabled(true); // Disable if out of stock
                buyButton.setColor(0.5f, 0.5f, 0.5f, 1f); // Grey out
            }
            buyButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showBuyConfirmation(item);
                }
            });
            itemDisplayTable.add(buyButton).width(100).pad(5).row(); // New row for each item
        }
    }

    private void showBuyConfirmation(final Item item) {
        // Initialize dialog if not already
        if (buyConfirmationDialog == null) {
            buyConfirmationDialog = new Dialog("Buy Item", skin);
            buyConfirmationDialog.button("Cancel", false); // Cancel button
            buyConfirmationDialog.pad(20);
            // It's good practice to set a modal background for dialogs
            buyConfirmationDialog.setModal(true);
            buyConfirmationDialog.setMovable(false); // Optional: prevent dialog from being dragged
        } else {
            buyConfirmationDialog.clearChildren(); // Clear previous content if reused
            buyConfirmationDialog.getTitleLabel().setText("Buy Item");
            buyConfirmationDialog.button("Cancel", false); // Re-add cancel button
        }

        // Add item name and quantity input
        Table contentTable = new Table(skin);
        contentTable.add(new Label("Item: " + item.getName(), skin)).colspan(2).row();
        contentTable.add(new Label("Price: $" + item.getPrice(), skin)).colspan(2).row();
        contentTable.add(new Label("Enter Quantity:", skin)).padTop(10);

        quantityField = new TextField("1", skin); // Default quantity to 1
        quantityField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter()); // Only allow digits
        contentTable.add(quantityField).width(100).padTop(10).row();

        TextButton confirmButton = new TextButton("Confirm Buy", skin);
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    double quantity = Double.parseDouble(quantityField.getText());
                    String productName = item.getName();
                    String quantityString = String.valueOf(quantity);
                    String[] args = new String[]{productName, quantityString};
                    Result result = controller.purchase(args);
                    if(!result.success()){
                        showErrorDialog("error purchasing" , result.message());
                    }
                } catch (NumberFormatException e) {
                    showErrorDialog("Invalid Input", "Please enter a valid number for quantity.");
                }
            }
        });
        contentTable.add(confirmButton).colspan(2).padTop(20);

        buyConfirmationDialog.getContentTable().add(contentTable).expand().fill();
        buyConfirmationDialog.show(stage);

        // --- CORRECTED: Manually center the dialog ---
        buyConfirmationDialog.pack(); // Pack to get preferred size
        buyConfirmationDialog.setPosition(
            Math.round((stage.getWidth() - buyConfirmationDialog.getWidth()) / 2),
            Math.round((stage.getHeight() - buyConfirmationDialog.getHeight()) / 2)
        );
        // --- END CORRECTION ---
    }

    private void updateMoneyLabel() {
        moneyLabel.setText("Money: $" + player.getMoney());
    }

    private void showErrorDialog(String title, String message) {
        // Initialize dialog if not already
        if (errorDialog == null) {
            errorDialog = new Dialog(title, skin);
            errorDialog.button("OK"); // Default OK button
            errorDialog.setModal(true);
            errorDialog.setMovable(false); // Optional: prevent dialog from being dragged
        } else {
            errorDialog.getTitleLabel().setText(title); // Update title
            errorDialog.clearChildren(); // Clear existing content
            errorDialog.text(message); // Set new message
            errorDialog.button("OK"); // Re-add OK button
        }

        errorDialog.show(stage);

        errorDialog.pack(); // Pack to get preferred size
        errorDialog.setPosition(
            Math.round((stage.getWidth() - errorDialog.getWidth()) / 2),
            Math.round((stage.getHeight() - errorDialog.getHeight()) / 2)
        );

    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Cap delta time for stability
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
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
