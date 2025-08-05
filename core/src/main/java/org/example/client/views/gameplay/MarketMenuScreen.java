package org.example.client.views.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.client.controllers.MarketController;
import org.example.client.views.ToolUpgradeDialog;
// import org.example.client.views.gameplay.BuildingPlacementScreen; // <--- حذف شد، دیگر لازم نیست
import org.example.common.models.Items.Item;
import org.example.common.models.Market;
import org.example.common.models.Player.Player;
import org.example.common.models.Product;
import org.example.common.models.common.Result;
import org.example.common.models.enums.Seasons;

import java.util.List;
import java.util.ArrayList;
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
    private Table itemDisplayTable;
    private ScrollPane scrollPane;
    private Label moneyLabel;
    private TextField quantityField;
    private Dialog buyConfirmationDialog;
    private Dialog errorDialog;

    // This now holds a list of Product objects
    private List<Product> currentDisplayStock;

    // Texture cache for item images with fallback support
    private Map<String, Texture> itemTextureCache;
    private Texture fallbackTexture;
    private Texture backgroundTexture;

    public MarketMenuScreen(Market market, Player player, Skin skin, Screen previousScreen, Seasons currentSeason) {
        this.market = market;
        this.player = player;
        this.currentSeason = currentSeason;
        this.previousScreen = previousScreen;
        this.skin = skin;

        this.controller = new MarketController(player, market);

        // Initialize stocks for the current season
        this.market.initializeTotalStock(currentSeason);
        // Default view is the permanent stock
        this.currentDisplayStock = new ArrayList<>(market.getPermanentStock());

        this.stage = new Stage(new ScreenViewport());

        // Initialize texture cache and fallback
        this.itemTextureCache = new HashMap<>();
        initializeFallbackTexture();
    }

    private void initializeFallbackTexture() {
        try {
            fallbackTexture = new Texture("content/ui/empty_slot.png");
        } catch (Exception e) {
            System.err.println("Failed to load fallback texture: " + e.getMessage());
            fallbackTexture = null;
        }
    }

    private Texture getItemTexture(Item item) {
        String imagePath = item.getImageFilepath();
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return fallbackTexture;
        }

        if (itemTextureCache.containsKey(imagePath)) {
            return itemTextureCache.get(imagePath);
        }

        try {
            Texture texture = new Texture(imagePath);
            itemTextureCache.put(imagePath, texture);
            return texture;
        } catch (Exception e) {
            System.err.println("Failed to load texture for item '" + item.getName() + "' at path '" + imagePath + "': " + e.getMessage());
            itemTextureCache.put(imagePath, fallbackTexture);
            return fallbackTexture;
        }
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
        try {
            backgroundTexture = new Texture("content/crafting_background.png");
            rootTable.setBackground(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(backgroundTexture));
        } catch (Exception e) {
            System.err.println("Failed to load market background: " + e.getMessage());
        }

        Table topBar = new Table();
        topBar.add(new Label("Market: " + market.getName(), skin)).expandX().left().padLeft(10);
        moneyLabel = new Label("Money: $" + player.getMoney(), skin);
        topBar.add(moneyLabel).expandX().right().padRight(10);
        rootTable.add(topBar).growX().row();

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
                currentDisplayStock = market.getTotalStock();
                displayItems(currentDisplayStock);
            }
        });
        filterButtons.add(allStockBtn).pad(5);
        rootTable.add(filterButtons).padTop(10).row();

        itemDisplayTable = new Table(skin);
        scrollPane = new ScrollPane(itemDisplayTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollbarsVisible(true);
        rootTable.add(scrollPane).expand().fill().pad(10).row();

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

    private void displayItems(List<Product> stockList) {
        itemDisplayTable.clearChildren();
        if (stockList.isEmpty()) {
            itemDisplayTable.add(new Label("No items in this category.", skin)).pad(20).center();
            return;
        }

        itemDisplayTable.add(new Label("", skin)).width(50).pad(5);
        itemDisplayTable.add(new Label("Item", skin, "default")).expandX().pad(5);
        itemDisplayTable.add(new Label("Price", skin, "default")).width(80).pad(5);
        itemDisplayTable.add(new Label("Stock", skin, "default")).width(100).pad(5);
        itemDisplayTable.add(new Label("", skin)).width(100).pad(5).row();

        for (Product product : stockList) {
            Item item = product.getItem();
            double stock = product.getAmount();
            boolean isAvailable = stock > 0;

            Texture itemTexture = getItemTexture(item);
            Image itemImage = new Image(itemTexture);
            itemImage.setSize(40, 40);
            if (!isAvailable) itemImage.setColor(0.5f, 0.5f, 0.5f, 0.7f);
            itemDisplayTable.add(itemImage).width(50).height(50).pad(5).center();

            Label nameLabel = new Label(item.getName(), skin);
            nameLabel.setWrap(true);
            if (!isAvailable) nameLabel.setColor(0.5f, 0.5f, 0.5f, 0.7f);
            itemDisplayTable.add(nameLabel).expandX().fillX().pad(5).align(Align.left);

            Label priceLabel = new Label("$" + item.getPrice(), skin);
            if (!isAvailable) priceLabel.setColor(0.5f, 0.5f, 0.5f, 0.7f);
            itemDisplayTable.add(priceLabel).width(80).pad(5);

            String stockText = (stock == Double.POSITIVE_INFINITY) ? "Infinite" : String.valueOf((int) stock);
            Label stockLabel = new Label(stockText, skin);
            if (!isAvailable) stockLabel.setColor(0.5f, 0.5f, 0.5f, 0.7f);
            itemDisplayTable.add(stockLabel).width(100).pad(5);

            TextButton buyButton = new TextButton("Buy", skin);
            if (!isAvailable) {
                buyButton.setText("Out of Stock");
                buyButton.setDisabled(true);
            }
            buyButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (isAvailable) {
                        showBuyConfirmation(item);
                    }
                }
            });
            itemDisplayTable.add(buyButton).width(100).pad(5).row();
        }
    }

    private void showBuyConfirmation(final Item item) {
        if (market.getName().equals("Black Smith") && isToolUpgradeItem(item)) {
            showToolUpgradeDialog();
            return;
        }

        // *** CHANGE HERE: Check for building items and show the new build dialog ***
        if (market.getName().equalsIgnoreCase("Carpenters Shop") && isBuildingItem(item)) {
            showBuildDialog(item);
            return;
        }

        buyConfirmationDialog = new Dialog("Buy Item", skin, "dialog") {
            protected void result(Object object) {
                if (Boolean.TRUE.equals(object)) {
                    try {
                        double quantity = Double.parseDouble(quantityField.getText());
                        if (quantity <= 0) {
                            showErrorDialog("Invalid Quantity", "Please enter a quantity greater than zero.");
                            return;
                        }

                        String[] args = new String[]{item.getName(), String.valueOf(quantity)};
                        Result result = controller.purchase(args);

                        if (result.success()) {
                            updateMoneyLabel();
                            displayItems(currentDisplayStock);
                        } else {
                            showErrorDialog("Purchase Failed", result.message());
                        }
                    } catch (NumberFormatException e) {
                        showErrorDialog("Invalid Input", "Please enter a valid number.");
                    }
                }
            }
        };

        Table contentTable = buyConfirmationDialog.getContentTable();
        contentTable.clear();
        contentTable.pad(20);

        contentTable.add(new Label("Item: " + item.getName(), skin)).colspan(2).row();
        contentTable.add(new Label("Price: $" + item.getPrice(), skin)).colspan(2).padBottom(10).row();
        contentTable.add(new Label("Enter Quantity:", skin));

        quantityField = new TextField("1", skin);
        quantityField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        contentTable.add(quantityField).width(100).padLeft(5).row();

        buyConfirmationDialog.button("Confirm Buy", true);
        buyConfirmationDialog.button("Cancel", false);
        buyConfirmationDialog.key(com.badlogic.gdx.Input.Keys.ENTER, true);
        buyConfirmationDialog.key(com.badlogic.gdx.Input.Keys.ESCAPE, false);

        buyConfirmationDialog.show(stage);
        buyConfirmationDialog.pack();
        buyConfirmationDialog.setPosition(
            Math.round((stage.getWidth() - buyConfirmationDialog.getWidth()) / 2),
            Math.round((stage.getHeight() - buyConfirmationDialog.getHeight()) / 2)
        );
    }

    // *** NEW METHOD: Shows a dialog to get X, Y and call controller.build ***
    private void showBuildDialog(final Item item) {
        final Dialog buildDialog = new Dialog("Place Building", skin, "dialog");

        Table contentTable = buildDialog.getContentTable();
        contentTable.pad(20f);

        contentTable.add(new Label("Building: " + item.getName(), skin)).colspan(2).row();
        contentTable.add(new Label("Click 'Select Location' to choose placement on map", skin)).colspan(2).padBottom(10).row();

        TextButton selectLocationButton = new TextButton("Select Location", skin);
        TextButton cancelButton = new TextButton("Cancel", skin);

        selectLocationButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buildDialog.hide();

                String buildingType = item.getName().toLowerCase().contains("barn") ? "barn" : "coop";
                controller.getView().getWorldController().startBuildingPlacement(buildingType);

                controller.getView().setBuildingPlacementListener(new Runnable() {
                    @Override
                    public void run() {
                        buildDialog.hide();
                        updateMoneyLabel();
                        displayItems(currentDisplayStock);
                        showErrorDialog("Success", "Building placed successfully!");

                        controller.getView().setBuildingPlacementListener(null);
                    }
                });
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buildDialog.hide();
            }
        });

        buildDialog.getButtonTable().add(selectLocationButton).pad(10);
        buildDialog.getButtonTable().add(cancelButton).pad(10);

        buildDialog.show(stage);
        buildDialog.pack();
        buildDialog.setPosition(
            Math.round((stage.getWidth() - buildDialog.getWidth()) / 2f),
            Math.round((stage.getHeight() - buildDialog.getHeight()) / 2f)
        );
    }

    private void updateMoneyLabel() {
        moneyLabel.setText("Money: $" + player.getMoney());
    }

    private void showErrorDialog(String title, String message) {
        if (errorDialog == null) {
            errorDialog = new Dialog(title, skin, "dialog");
            errorDialog.button("OK");
            errorDialog.setModal(true);
            errorDialog.setMovable(false);
        }

        errorDialog.getTitleLabel().setText(title);
        Table content = errorDialog.getContentTable();
        content.clear();
        content.add(new Label(message, skin)).pad(20);

        errorDialog.show(stage);
        errorDialog.pack();
        errorDialog.setPosition(
            Math.round((stage.getWidth() - errorDialog.getWidth()) / 2),
            Math.round((stage.getHeight() - errorDialog.getHeight()) / 2)
        );
    }

    private boolean isToolUpgradeItem(Item item) {
        String itemName = item.getName().toLowerCase();
        return itemName.contains("tool upgrade service") || itemName.contains("tool") ||
            itemName.contains("cooper") || itemName.contains("iron") ||
            itemName.contains("gold") || itemName.contains("iridium");
    }

    // *** CHANGE HERE: Updated to include shipping bin ***
    private boolean isBuildingItem(Item item) {
        String itemName = item.getName().toLowerCase();
        return itemName.contains("barn") || itemName.contains("coop") || itemName.contains("well") || itemName.contains("shipping bin");
    }

    // *** METHOD REMOVED: handleBuildingPurchase is no longer needed ***
    /*
    private void handleBuildingPurchase(Item item) {
        // ... OLD CODE REMOVED ...
    }
    */

    private void showToolUpgradeDialog() {
        ToolUpgradeDialog upgradeDialog = new ToolUpgradeDialog(player, skin);
        upgradeDialog.show(stage);
        upgradeDialog.pack();
        upgradeDialog.setPosition(
            Math.round((stage.getWidth() - upgradeDialog.getWidth()) / 2),
            Math.round((stage.getHeight() - upgradeDialog.getHeight()) / 2)
        );
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
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
    public void hide() {
        if (buyConfirmationDialog != null) buyConfirmationDialog.hide();
        if (errorDialog != null) errorDialog.hide();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        for (Texture texture : itemTextureCache.values()) {
            if (texture != null) {
                texture.dispose();
            }
        }
        if (fallbackTexture != null) {
            fallbackTexture.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
