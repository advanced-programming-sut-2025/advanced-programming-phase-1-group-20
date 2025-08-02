package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import org.example.client.controllers.GameMenuController;
import org.example.common.models.Items.Item;
import org.example.common.models.Player.Player;
import org.example.common.models.App;
import org.example.common.models.common.Result;

import java.util.Map;

public class GreenhouseRepairDialog extends Dialog {
    private final Player player;
    private final GameMenuController controller;
    private final Skin skin;

    // Required materials for greenhouse repair
    private static final int REQUIRED_WOOD = 500;
    private static final int REQUIRED_STONE = 1000;

    public GreenhouseRepairDialog(Player player, GameMenuController controller, Skin skin) {
        super("Greenhouse Repair", skin);
        this.player = player;
        this.controller = controller;
        this.skin = skin;

        initializeDialog();
    }

    private void initializeDialog() {
        // Set dialog size and position
        setSize(400, 300);
        setPosition(Gdx.graphics.getWidth() / 2f - getWidth() / 2f,
                   Gdx.graphics.getHeight() / 2f - getHeight() / 2f);

        // Create main content table
        Table contentTable = new Table();
        contentTable.setFillParent(true);
        contentTable.pad(20);

        // Title
        Label titleLabel = new Label("Greenhouse Repair Requirements", skin);
        titleLabel.setAlignment(Align.center);
        titleLabel.setFontScale(1.2f);
        contentTable.add(titleLabel).colspan(2).padBottom(20).row();

        // Materials section
        contentTable.add(createMaterialsSection()).colspan(2).padBottom(20).row();

        // Status section
        contentTable.add(createStatusSection()).colspan(2).padBottom(20).row();

        // Create buttons
        Table buttonTable = new Table();

        TextButton repairButton = new TextButton("Repair Greenhouse", skin);
        repairButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                repairGreenhouse();
            }
        });

        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Remove the dialog from the stage
                if (getStage() != null) {
                    remove();
                }
            }
        });

        buttonTable.add(repairButton).padRight(10);
        buttonTable.add(cancelButton);

        contentTable.add(buttonTable).colspan(2);

        getContentTable().add(contentTable);

        // Add key listener for Escape key
        addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    if (getStage() != null) {
                        remove();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private Table createMaterialsSection() {
        Table materialsTable = new Table();
        materialsTable.pad(10);

        // Header
        Label headerLabel = new Label("Required Materials:", skin);
        headerLabel.setFontScale(1.1f);
        materialsTable.add(headerLabel).colspan(3).padBottom(10).row();

        // Wood requirement
        Item woodItem = App.getItem("Wood");
        int woodCount = getItemCount("Wood");
        boolean hasEnoughWood = woodCount >= REQUIRED_WOOD;

        Label woodLabel = new Label("Wood:", skin);
        Label woodRequiredLabel = new Label(REQUIRED_WOOD + "", skin);
        Label woodOwnedLabel = new Label(woodCount + "", skin);

        if (hasEnoughWood) {
            woodOwnedLabel.setColor(0, 1, 0, 1); // Green
        } else {
            woodOwnedLabel.setColor(1, 0, 0, 1); // Red
        }

        materialsTable.add(woodLabel).padRight(10);
        materialsTable.add(woodRequiredLabel).padRight(10);
        materialsTable.add(woodOwnedLabel).row();

        Item stoneItem = App.getItem("Stone");
        int stoneCount = getItemCount("Stone");
        boolean hasEnoughStone = stoneCount >= REQUIRED_STONE;

        Label stoneLabel = new Label("Stone:", skin);
        Label stoneRequiredLabel = new Label(REQUIRED_STONE + "", skin);
        Label stoneOwnedLabel = new Label(stoneCount + "", skin);

        if (hasEnoughStone) {
            stoneOwnedLabel.setColor(0, 1, 0, 1); // Green
        } else {
            stoneOwnedLabel.setColor(1, 0, 0, 1); // Red
        }

        materialsTable.add(stoneLabel).padRight(10);
        materialsTable.add(stoneRequiredLabel).padRight(10);
        materialsTable.add(stoneOwnedLabel);

        return materialsTable;
    }

    private Table createStatusSection() {
        Table statusTable = new Table();
        statusTable.pad(10);

        boolean canRepair = canRepairGreenhouse();
        String statusText = canRepair ?
            "You have enough materials to repair the greenhouse!" :
            "You need more materials to repair the greenhouse.";

        Label statusLabel = new Label(statusText, skin);
        statusLabel.setAlignment(Align.center);
        statusLabel.setColor(canRepair ? 0 : 1, canRepair ? 1 : 0, 0, 1);

        statusTable.add(statusLabel);

        return statusTable;
    }

    private int getItemCount(String itemName) {
        for (Map.Entry<Item, Integer> entry : player.getBackpack().getInventory().entrySet()) {
            if (entry.getKey().getName().equalsIgnoreCase(itemName)) {
                return entry.getValue();
            }
        }
        return 0;
    }

    private boolean canRepairGreenhouse() {
        int woodCount = getItemCount("Wood");
        int stoneCount = getItemCount("Stone");

        return woodCount >= REQUIRED_WOOD && stoneCount >= REQUIRED_STONE;
    }

    private void repairGreenhouse() {
        if (!canRepairGreenhouse()) {
            // Show error message
            Dialog errorDialog = new Dialog("Cannot Repair", skin);
            errorDialog.getContentTable().add(new Label("You don't have enough materials to repair the greenhouse.", skin));
            errorDialog.button("OK");
            errorDialog.show(getStage());
            return;
        }

        // Perform the repair
        Result result = controller.greenhouseBuild(player);

        if (result.success()) {
            // Show success message
            Dialog successDialog = new Dialog("Success", skin);
            successDialog.getContentTable().add(new Label("Greenhouse repaired successfully!", skin));
            successDialog.button("OK");
            successDialog.show(getStage());

            // Close this dialog
            hide();
        } else {
            // Show error message
            Dialog errorDialog = new Dialog("Error", skin);
            errorDialog.getContentTable().add(new Label("Failed to repair greenhouse: " + result.message(), skin));
            errorDialog.button("OK");
            errorDialog.show(getStage());
        }
    }
}
