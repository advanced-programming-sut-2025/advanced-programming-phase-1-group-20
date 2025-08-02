package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import org.example.common.models.Items.Tool;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Result;

import java.util.List;

public class ToolUpgradeDialog extends Dialog {
    private final Player player;
    private final Skin skin;
    private Table toolListTable;
    private Label infoLabel;
    private Label costLabel;
    private Label materialLabel;
    private TextButton upgradeButton;
    private Tool selectedTool;

    public ToolUpgradeDialog(Player player, Skin skin) {
        super("Tool Upgrade", skin);
        this.player = player;
        this.skin = skin;

        initializeDialog();
    }

    private void initializeDialog() {
        // Main layout
        Table contentTable = getContentTable();
        contentTable.clear();
        contentTable.pad(20);

        // Title
        contentTable.add(new Label("Select a tool to upgrade:", skin, "title")).colspan(2).padBottom(20).row();

        // Tool list
        toolListTable = new Table();
        toolListTable.setBackground(skin.getDrawable("button"));
        toolListTable.pad(10);

        // Info panel
        Table infoTable = new Table();
        infoTable.setBackground(skin.getDrawable("button"));
        infoTable.pad(10);

        infoLabel = new Label("Select a tool to see upgrade information", skin);
        costLabel = new Label("", skin);
        materialLabel = new Label("", skin);

        infoTable.add(new Label("Tool Information:", skin, "title")).colspan(2).padBottom(10).row();
        infoTable.add(infoLabel).colspan(2).padBottom(5).row();
        infoTable.add(costLabel).colspan(2).padBottom(5).row();
        infoTable.add(materialLabel).colspan(2).padBottom(5).row();

        // Upgrade button
        upgradeButton = new TextButton("Upgrade Tool", skin);
        upgradeButton.setDisabled(true);
        upgradeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                upgradeSelectedTool();
            }
        });

        infoTable.add(upgradeButton).colspan(2).padTop(10).row();

        // Layout
        contentTable.add(toolListTable).width(300).height(400).padRight(10);
        contentTable.add(infoTable).width(300).height(400);

        // Buttons
        button("Close", false);

        // Load tools
        loadAvailableTools();
    }

    private void loadAvailableTools() {
        toolListTable.clear();
        toolListTable.add(new Label("Available Tools:", skin, "title")).colspan(2).padBottom(10).row();

        List<Tool> tools = player.getAvailableTools();

        if (tools.isEmpty()) {
            toolListTable.add(new Label("No tools available for upgrade", skin)).colspan(2).pad(10);
            return;
        }

        for (Tool tool : tools) {
            // Tool button
            TextButton toolButton = new TextButton(tool.getName(), skin);
            toolButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectTool(tool);
                }
            });

            // Tool icon (if available)
            Table toolRow = new Table();
            try {
                Texture toolTexture = new Texture(tool.getImageFilepath());
                Image toolImage = new Image(toolTexture);
                toolImage.setSize(32, 32);
                toolRow.add(toolImage).size(32, 32).padRight(10);
            } catch (Exception e) {
                // Use fallback icon
                Label iconLabel = new Label("🔧", skin);
                toolRow.add(iconLabel).size(32, 32).padRight(10);
            }

            toolRow.add(toolButton).expandX().fillX();
            toolListTable.add(toolRow).expandX().fillX().padBottom(5).row();
        }
    }

    private void selectTool(Tool tool) {
        selectedTool = tool;
        updateToolInfo();
        upgradeButton.setDisabled(false);
    }

    private void updateToolInfo() {
        if (selectedTool == null) {
            infoLabel.setText("Select a tool to see upgrade information");
            costLabel.setText("");
            materialLabel.setText("");
            return;
        }

        // Current tool info
        String currentMaterial = selectedTool.getMaterial().name();
        infoLabel.setText("Current: " + selectedTool.getName() + "\nMaterial: " + currentMaterial);

        // Upgrade requirements
        String nextMaterial = getNextMaterial(currentMaterial);
        if (nextMaterial != null) {
            int cost = getUpgradeCost(selectedTool.getMaterial());
            int requiredBars = 5;
            String barType = getRequiredBarType(selectedTool.getMaterial());

            costLabel.setText("Upgrade Cost: $" + cost + "\nRequired: " + requiredBars + " " + barType + " Bars");
            materialLabel.setText("Upgrade to: " + nextMaterial);
        } else {
            costLabel.setText("Tool is already at maximum level");
            materialLabel.setText("");
        }
    }

    private String getNextMaterial(String currentMaterial) {
        return switch (currentMaterial) {
            case "BASIC" -> "COPPER";
            case "COPPER" -> "IRON";
            case "IRON" -> "GOLD";
            case "GOLD" -> "IRIDIUM";
            default -> null;
        };
    }

    private int getUpgradeCost(Tool.ToolMaterial material) {
        return switch (material) {
            case BASIC -> 1000;
            case COPPER -> 2500;
            case IRON -> 5000;
            case GOLD -> 12500;
            default -> 0;
        };
    }

    private String getRequiredBarType(Tool.ToolMaterial material) {
        return switch (material) {
            case BASIC -> "Copper";
            case COPPER -> "Iron";
            case IRON -> "Gold";
            case GOLD -> "Iridium";
            default -> "";
        };
    }

    private void upgradeSelectedTool() {
        if (selectedTool == null) {
            return;
        }

        // Check if player has enough money
        int cost = getUpgradeCost(selectedTool.getMaterial());
        if (player.getMoney() < cost) {
            showErrorDialog("Insufficient Funds", "You don't have enough money for this upgrade.");
            return;
        }

        // Check if player has required bars
        String barType = getRequiredBarType(selectedTool.getMaterial()) + " Bar";
        int requiredBars = 5;

        if (player.getBackpack().getItem(barType) == null ||
            player.getBackpack().getInventory().get(player.getBackpack().getItem(barType)) < requiredBars) {
            showErrorDialog("Insufficient Materials", "You need " + requiredBars + " " + barType + "s for this upgrade.");
            return;
        }

        // Perform upgrade
        boolean success = player.upgradeTool(selectedTool.getName());

        if (success) {
            showSuccessDialog("Upgrade Successful", "Your " + selectedTool.getName() + " has been upgraded!");
            // Refresh the tool list
            loadAvailableTools();
            selectedTool = null;
            updateToolInfo();
            upgradeButton.setDisabled(true);
        } else {
            showErrorDialog("Upgrade Failed", "Failed to upgrade the tool. Please try again.");
        }
    }

    private void showErrorDialog(String title, String message) {
        Dialog errorDialog = new Dialog(title, skin);
        errorDialog.text(message);
        errorDialog.button("OK");
        errorDialog.show(getStage());
    }

    private void showSuccessDialog(String title, String message) {
        Dialog successDialog = new Dialog(title, skin);
        successDialog.text(message);
        successDialog.button("OK");
        successDialog.show(getStage());
    }
}
