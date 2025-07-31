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
import org.example.common.models.Player.Player;
import org.example.common.models.entities.FriendShip;

public class FriendInteractionWindow implements Screen {
    private static final float WINDOW_WIDTH = 400;
    private static final float WINDOW_HEIGHT = 300;
    private static final Color BACKGROUND_COLOR = new Color(0.2f, 0.2f, 0.3f, 0.95f);

    private Stage stage;
    private Table mainTable;
    private Player currentPlayer;
    private Player targetPlayer;
    private Skin skin;
    private Screen previousScreen;
    private boolean isVisible;

    public FriendInteractionWindow(Player currentPlayer, Player targetPlayer, Skin skin, Screen previousScreen) {
        System.out.println("🤝 Creating FriendInteractionWindow for " + targetPlayer.getUser().getUsername());
        this.currentPlayer = currentPlayer;
        this.targetPlayer = targetPlayer;
        this.skin = skin;
        this.previousScreen = previousScreen;
        this.isVisible = false;

        try {
            stage = new Stage(new ScreenViewport());
            createUI();
            System.out.println("FriendInteractionWindow created successfully");
        } catch (Exception e) {
            System.err.println("Error creating FriendInteractionWindow: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private void createUI() {
        // Main container
        mainTable = new Table();
        mainTable.setFillParent(true);

        // Create the interaction window
        Table windowTable = new Table();
        windowTable.setBackground(new Image(createBackgroundTexture()).getDrawable());

        // Title with friend's name
        String friendName = targetPlayer.getUser() != null ? targetPlayer.getUser().getUsername() : "Unknown Player";
        Label titleLabel = new Label("Interact with " + friendName, skin);
        titleLabel.setFontScale(1.3f);
        titleLabel.setColor(Color.GOLD);
        windowTable.add(titleLabel).padTop(20).padBottom(30).row();

        // Get friendship level
        FriendShip friendship = currentPlayer.getFriendship(targetPlayer);
        String levelText = "Friendship Level: " + friendship.getLevel() + " (" + friendship.getXp() + "/" + friendship.getMaxXpForCurrentLevel() + " XP)";
        Label levelLabel = new Label(levelText, skin);
        levelLabel.setColor(Color.CYAN);
        windowTable.add(levelLabel).padBottom(20).row();

        // Create interaction buttons
        createInteractionButtons(windowTable, friendship);

        // Close button
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hideInteractionWindow();
            }
        });

        windowTable.add(closeButton).padTop(20).width(100).height(35);

        // Center the window on screen
        mainTable.center();
        mainTable.add(windowTable);

        stage.addActor(mainTable);
    }

    private void createInteractionButtons(Table windowTable, FriendShip friendship) {
        // Hug button
        TextButton hugButton = new TextButton("Hug", skin);
        hugButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                performHug(friendship);
            }
        });

        // Buy Flowers button
        TextButton flowersButton = new TextButton("Buy Flowers", skin);
        flowersButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                performBuyFlowers(friendship);
            }
        });

        // Marriage Proposal button
        TextButton marriageButton = new TextButton("Marriage Proposal", skin);
        marriageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                performMarriageProposal(friendship);
            }
        });

        // Add buttons to window
        windowTable.add(hugButton).width(200).height(40).pad(10).row();
        windowTable.add(flowersButton).width(200).height(40).pad(10).row();
        windowTable.add(marriageButton).width(200).height(40).pad(10).row();
    }

    private void performHug(FriendShip friendship) {
        if (!arePlayersNearEachOther()) {
            showErrorDialog("You need to be near " + targetPlayer.getUser().getUsername() + " to hug them.");
            return;
        }

        boolean success = currentPlayer.hugMob(targetPlayer);
        if (success) {
            showSuccessDialog("You hugged " + targetPlayer.getUser().getUsername() + "! Friendship increased by " + FriendShip.XP_HUG + " XP.");
        } else {
            showErrorDialog("You have already hugged " + targetPlayer.getUser().getUsername() + " today.");
        }
    }

    private void performBuyFlowers(FriendShip friendship) {
        if (!arePlayersNearEachOther()) {
            showErrorDialog("You need to be near " + targetPlayer.getUser().getUsername() + " to give them flowers.");
            return;
        }

        // Check if player has enough money (flowers cost 100 gold)
        if (currentPlayer.getMoney() < 100) {
            showErrorDialog("You need 100 gold to buy flowers.");
            return;
        }

        // Check if friendship level is high enough (level 1 or higher)
        if (friendship.getLevel() < FriendShip.LEVEL_1) {
            showErrorDialog("You need friendship level 1 or higher to give flowers.");
            return;
        }

        // Perform the flower giving action
        boolean success = currentPlayer.giveBouquetTo(targetPlayer);
        if (success) {
            currentPlayer.decreaseMoney(100); // Deduct money
            showSuccessDialog("You gave flowers to " + targetPlayer.getUser().getUsername() + "! Friendship increased!");
        } else {
            showErrorDialog("You have already given flowers to " + targetPlayer.getUser().getUsername() + " today.");
        }
    }

    private void performMarriageProposal(FriendShip friendship) {
        if (!arePlayersNearEachOther()) {
            showErrorDialog("You need to be near " + targetPlayer.getUser().getUsername() + " to propose marriage.");
            return;
        }

        // Check if friendship level is high enough (level 3 or higher)
        if (friendship.getLevel() < FriendShip.LEVEL_3) {
            showErrorDialog("You need friendship level 3 or higher to propose marriage.");
            return;
        }

        // Check if either player is already married
        if (currentPlayer.isMarriedTo(targetPlayer)) {
            showErrorDialog("You are already married to " + targetPlayer.getUser().getUsername() + "!");
            return;
        }

        if (currentPlayer.isMarried()) {
            showErrorDialog("You are already married to someone else.");
            return;
        }

        if (targetPlayer.isMarried()) {
            showErrorDialog(targetPlayer.getUser().getUsername() + " is already married to someone else.");
            return;
        }

        // Show marriage proposal dialog
        showMarriageProposalDialog(friendship);
    }

    private void showMarriageProposalDialog(FriendShip friendship) {
        Dialog proposalDialog = new Dialog("💍 Marriage Proposal", skin) {
            @Override
            protected void result(Object object) {
                // Handle dialog result
            }
        };

        String friendName = targetPlayer.getUser().getUsername();
        Label proposalLabel = new Label("Do you want to propose marriage to " + friendName + "?", skin);
        proposalLabel.setWrap(true);
        proposalDialog.getContentTable().add(proposalLabel).pad(20).row();

        // Yes button
        TextButton yesButton = new TextButton("Yes, Propose", skin);
        yesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                proposalDialog.hide();
                performMarriageProposalAction(friendship);
            }
        });

        // No button
        TextButton noButton = new TextButton("No, Cancel", skin);
        noButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                proposalDialog.hide();
            }
        });

        proposalDialog.getButtonTable().add(yesButton).pad(10);
        proposalDialog.getButtonTable().add(noButton).pad(10);
        proposalDialog.show(stage);
    }

    private void performMarriageProposalAction(FriendShip friendship) {
        boolean success = currentPlayer.proposeMarriageTo(targetPlayer);
        if (success) {
            showSuccessDialog("💍 Marriage proposal sent to " + targetPlayer.getUser().getUsername() + "!");
            // In a real implementation, you would show a dialog to the target player
            // asking them to accept or reject the proposal
        } else {
            showErrorDialog("Failed to send marriage proposal. You may have already proposed today.");
        }
    }

    private boolean arePlayersNearEachOther() {
        if (currentPlayer.getCurrentFarm() != targetPlayer.getCurrentFarm() &&
            !(currentPlayer.getIsInVillage() && targetPlayer.getIsInVillage())) {
            return false;
        }

        float distance = Math.abs(currentPlayer.getPosX() - targetPlayer.getPosX()) +
                       Math.abs(currentPlayer.getPosY() - targetPlayer.getPosY());

        return distance <= 120; // Within 2 tiles (60 pixels per tile)
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

    private Texture createBackgroundTexture() {
        // Create a simple colored texture for background
        try {
            return new Texture(Gdx.files.internal("content/crafting_background.png"));
        } catch (Exception e) {
            // Fallback - create a simple colored texture
            return createColorTexture(BACKGROUND_COLOR);
        }
    }

    private Texture createColorTexture(Color color) {
        // This would normally create a 1x1 pixel texture with the specified color
        // For now, return null and use table backgrounds without textures
        return null;
    }

    public void showInteractionWindow() {
        isVisible = true;
        Gdx.input.setInputProcessor(stage);
    }

    public void hideInteractionWindow() {
        isVisible = false;
        Main.getGame().setScreen(previousScreen);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0.8f);
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
    public void show() {
        showInteractionWindow();
    }

    @Override
    public void hide() {
        isVisible = false;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
