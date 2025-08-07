package org.example.client.views.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.client.controllers.HouseMenuController;
import org.example.common.models.Items.CraftingItem;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Result;
import org.example.common.models.enums.Types.CraftingType;
import org.example.common.models.common.HoverImage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CraftingScreen implements Screen, Disposable {
    private final Stage stage;
    private final Player player;
    private final Skin skin;
    private final Screen previousScreen;

    // Tables for layout
    private final Table mainTable;
    private final Table craftingTable;

    private boolean canClose = false;

    // Maps to store textures and the new progress bars
    private final Map<CraftingType, Texture> defaultCraftingTextures = new HashMap<>();
    private final Map<CraftingType, Texture> hoverCraftingTextures = new HashMap<>();
    private final Map<CraftingType, ProgressBar> progressBars = new HashMap<>(); // Map for progress bars

    private final Texture backgroundTexture;

    private Dialog errorDialog;
    private HouseMenuController controller;

    public CraftingScreen(Player player, Skin skin, Screen previousScreen) {
        this.player = player;
        this.skin = skin;
        this.previousScreen = previousScreen;
        stage = new Stage(new ScreenViewport());

        backgroundTexture = new Texture("content/crafting_background.png");
        Image backgroundImage = new Image(backgroundTexture);

        Stack stack = new Stack();
        stack.setFillParent(true);
        stage.addActor(stack);

        stack.add(backgroundImage);

        mainTable = new Table();
        mainTable.center();
        stack.add(mainTable);

        craftingTable = new Table();
        mainTable.add(craftingTable);

        loadCraftingItemsUI();

        controller = new HouseMenuController(player, player.getCurrentFarm().getBuilding());
    }

    /**
     * Creates the UI elements for each crafting item, including the image and a progress bar below it.
     */
    public void loadCraftingItemsUI() {
        int itemsInRow = 0;
        for (CraftingType craftingType : CraftingType.values()) {
            VerticalGroup itemGroup = new VerticalGroup();
            itemGroup.space(5);

            Texture defaultTex;
            if (player.craftingExists(craftingType.getName())) {
                defaultTex = new Texture("content/CraftingItems/" + craftingType.getImageFilepath() + ".png");
            } else {
                defaultTex = new Texture("content/CraftingItems/" + craftingType.getImageFilepath() + "_Locked" + ".png");
            }
            defaultCraftingTextures.put(craftingType, defaultTex);

            Texture hoverTex = new Texture("content/CraftingItems/" + craftingType.getImageFilepath() + "_hover" + ".png");
            hoverCraftingTextures.put(craftingType, hoverTex);

            HoverImage image = new HoverImage(defaultTex, hoverTex, 240);
            image.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (event.getButton() == Input.Buttons.LEFT) {
                        String[] args = new String[]{craftingType.getName()};
                        Result result = controller.craftItem(args);
                        if (!result.success()) {
                            showErrorDialog("Error Crafting", result.message());
                        }
                    } else if (event.getButton() == Input.Buttons.RIGHT) {
                        CraftingItem itemInstance = (CraftingItem) player.getBackpack().getItem(craftingType.getName());
                        if (itemInstance != null) {
                            showArtisanActionDialog(itemInstance);
                        } else {
                            showErrorDialog("Item Not Owned", "You need to craft or own this item first.");
                        }
                    }
                }
            });

            ProgressBar progressBar = new ProgressBar(0, 1, 0.01f, false, skin);
            progressBars.put(craftingType, progressBar);

            itemGroup.addActor(image);
            itemGroup.addActor(progressBar);

            craftingTable.add(itemGroup).pad(10);

            itemsInRow++;
            if (itemsInRow >= 8) {
                craftingTable.row();
                itemsInRow = 0;
            }
        }
    }

    private void updateProgressBars() {
        // This method now iterates through the player's backpack to find placed crafting items
        for (Map.Entry<CraftingType, ProgressBar> entry : progressBars.entrySet()) {
            CraftingType type = entry.getKey();
            ProgressBar bar = entry.getValue();

            Optional<CraftingItem> matchingItemOpt = player.getBackpack().getInventory().keySet().stream()
                .filter(item -> item instanceof CraftingItem)
                .map(item -> (CraftingItem) item)
                .filter(item -> item.getType() == type && item.getProccessingItem() != null)
                .findFirst();

            if (matchingItemOpt.isPresent()) {
                bar.setValue((float) matchingItemOpt.get().getProgressBar());
            } else {
                bar.setValue(0);
            }
        }
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        canClose = false;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);

        updateProgressBars();

        stage.act(delta);
        stage.draw();

        if (!Gdx.input.isKeyPressed(Input.Keys.B)) {
            canClose = true;
        }
        if (canClose && Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            Main.getGame().setScreen(previousScreen);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();

        for (Texture texture : defaultCraftingTextures.values()) {
            texture.dispose();
        }
        for (Texture texture : hoverCraftingTextures.values()) {
            texture.dispose();
        }
    }


    private void showErrorDialog(String title, String message) {
        if (errorDialog == null) {
            errorDialog = new Dialog("", skin, "dialog");
            errorDialog.setModal(true);
            errorDialog.setMovable(false);
        }
        errorDialog.getTitleLabel().setText(title);
        errorDialog.getContentTable().clear();
        errorDialog.getButtonTable().clear();

        Label messageLabel = new Label(message, skin);
        messageLabel.setWrap(true);
        errorDialog.getContentTable().add(messageLabel).width(300).pad(20);

        TextButton okButton = new TextButton("OK", skin);
        errorDialog.button(okButton);
        errorDialog.key(Input.Keys.ENTER, true);

        errorDialog.show(stage);
        errorDialog.pack();
        errorDialog.setPosition(
            Math.round((stage.getWidth() - errorDialog.getWidth()) / 2f),
            Math.round((stage.getHeight() - errorDialog.getHeight()) / 2f)
        );
    }

    private void showArtisanActionDialog(CraftingItem item) {
        Dialog actionDialog = new Dialog("Manage " + item.getName(), skin, "dialog");

        // Collect Button
        if (item.getFinishedItem() != null) {
            TextButton collectButton = new TextButton("Collect Product", skin);
            collectButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Result result = controller.artisanGet(new String[]{item.getName()});
                    if (!result.success()) {
                        showErrorDialog("Error", result.message());
                    } else {
                        showErrorDialog("Success", result.message()); // Using same dialog for success
                    }
                    actionDialog.hide();
                }
            });
            actionDialog.getContentTable().add(collectButton).pad(10).row();
        }

        // Processing Buttons
        if (item.getProccessingItem() != null) {
            TextButton cancelButton = new TextButton("Cancel Process", skin);
            cancelButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Result result = controller.artisanCancel(new String[]{item.getName()});
                    showErrorDialog("Info", result.message());
                    actionDialog.hide();
                }
            });
            actionDialog.getContentTable().add(cancelButton).pad(10).row();

            TextButton fastFinishButton = new TextButton("Finish Quickly (Cheat)", skin);
            fastFinishButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Result result = controller.artisanFastFinish(new String[]{item.getName()});
                    showErrorDialog("Info", result.message());
                    actionDialog.hide();
                }
            });
            actionDialog.getContentTable().add(fastFinishButton).pad(10).row();
        }

        // Start New Process Button
        if (item.getProccessingItem() == null && item.getFinishedItem() == null) {
            TextButton startButton = new TextButton("Start New Process", skin);
            startButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    actionDialog.hide();
                    showArtisanNameDialog(item.getType());
                }
            });
            actionDialog.getContentTable().add(startButton).pad(10).row();
        }

        actionDialog.button("Close");
        actionDialog.show(stage);
    }

    private void showArtisanNameDialog(CraftingType craftingType) {
        Dialog nameDialog = new Dialog("Enter Artisan Name", skin, "dialog");

        Label promptLabel = new Label("Please enter a name for the artisan item:", skin);
        nameDialog.getContentTable().add(promptLabel).pad(10).colspan(2).row();

        final TextField nameField = new TextField("", skin);
        nameDialog.getContentTable().add(nameField).width(250).pad(10).colspan(2);

        TextButton submitButton = new TextButton("Submit", skin);
        submitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Main.getGame().setScreen(new ArtisanCreateScreen(player, skin, previousScreen, new CraftingItem(craftingType)));
            }
        });

        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                nameDialog.hide();
            }
        });

        nameDialog.getButtonTable().add(submitButton).pad(10);
        nameDialog.getButtonTable().add(cancelButton).pad(10);

        nameDialog.show(stage);
        nameDialog.pack();
        nameDialog.setPosition(
            Math.round((stage.getWidth() - nameDialog.getWidth()) / 2f),
            Math.round((stage.getHeight() - nameDialog.getHeight()) / 2f)
        );
    }
}
