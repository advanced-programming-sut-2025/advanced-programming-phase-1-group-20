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

import java.util.Map;

public class FriendsWindow implements Screen {
    private static final float WINDOW_WIDTH = 600;
    private static final float WINDOW_HEIGHT = 400;
    private static final Color BACKGROUND_COLOR = new Color(0.2f, 0.2f, 0.3f, 0.9f);

    private Stage stage;
    private Table mainTable;
    private ScrollPane scrollPane;
    private Player currentPlayer;
    private Skin skin;
    private Screen previousScreen;
    private boolean isVisible;

    // Gift selection state
    private Player selectedFriend;
    private boolean isSelectingGiftItem;

    public FriendsWindow(Player currentPlayer, Skin skin, Screen previousScreen) {
        System.out.println("🏠 Creating FriendsWindow...");
        this.currentPlayer = currentPlayer;
        this.skin = skin;
        this.previousScreen = previousScreen;
        this.isVisible = false;

        try {
            stage = new Stage(new ScreenViewport());
            createUI();
            System.out.println("🏠 FriendsWindow created successfully");
        } catch (Exception e) {
            System.err.println("❌ Error creating FriendsWindow: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

        private void createUI() {
        // Main container
        mainTable = new Table();
        mainTable.setFillParent(true);

        // Create a more compact window
        Table windowTable = new Table();
        windowTable.setBackground(new Image(createBackgroundTexture()).getDrawable());

        // Title
        Label titleLabel = new Label("Friends", skin);
        titleLabel.setFontScale(1.5f);
        titleLabel.setColor(Color.GOLD);
        windowTable.add(titleLabel).padTop(15).padBottom(20).row();

        // Friends list container
        Table friendsListTable = new Table();
        friendsListTable.top();

                // Get all friendships for current player
        Map<Player, FriendShip> friendships = currentPlayer.getAllFriendships();

        if (friendships.isEmpty()) {
            Label noFriendsLabel = new Label("No friends yet. Play with other players to make friends!", skin);
            noFriendsLabel.setColor(Color.LIGHT_GRAY);
            noFriendsLabel.setWrap(true);
            friendsListTable.add(noFriendsLabel).width(400).pad(20);
        } else {
            // Add each friend to the list
            for (Map.Entry<Player, FriendShip> entry : friendships.entrySet()) {
                Player friend = entry.getKey();
                FriendShip friendship = entry.getValue();

                Table friendRow = createFriendRow(friend, friendship);
                friendsListTable.add(friendRow).fillX().padBottom(5).row();
            }
        }

        // Create scroll pane for friends list
        scrollPane = new ScrollPane(friendsListTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        // Calculate appropriate height based on friend count
        int friendCount = Math.max(1, friendships.size());
        float contentHeight = Math.min(300, friendCount * 60 + 100); // Max 300px, ~60px per friend

        windowTable.add(scrollPane).width(500).height(contentHeight).pad(15).row();

        // Close button
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hideFriendsWindow();
            }
        });

        windowTable.add(closeButton).pad(15).width(100).height(35);

        // Center the window on screen
        mainTable.center();
        mainTable.add(windowTable);

        stage.addActor(mainTable);
    }

    private Table createFriendRow(Player friend, FriendShip friendship) {
        Table row = new Table();
        // Friend name
        String friendName = friend.getUser() != null ? friend.getUser().getUsername() : "Unknown Player";
        Label nameLabel = new Label(friendName, skin);
        nameLabel.setFontScale(1.2f);
        nameLabel.setColor(Color.WHITE);

        // Friendship level display
        String levelText = "Level " + friendship.getLevel() + " (" + friendship.getXp() + "/" + friendship.getMaxXpForCurrentLevel() + " XP)";
        Label levelLabel = new Label(levelText, skin);
        levelLabel.setColor(Color.CYAN);

        // Gift button
        TextButton giftButton = new TextButton("Gift", skin);
        giftButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showGiftMenu(friend, friendship);
            }
        });

        // Layout
        row.add(nameLabel).left().padLeft(20).expandX();
        row.add(levelLabel).center().padRight(10);
        row.add(giftButton).right().padRight(20).width(80).height(30);

        return row;
    }

    private void showGiftMenu(Player friend, FriendShip friendship) {
        selectedFriend = friend;

        // Create gift menu dialog
        Dialog giftDialog = new Dialog("Gift Options", skin) {
            @Override
            protected void result(Object object) {
                // Handle dialog result
            }
        };

        // Send Gift button
        TextButton sendGiftButton = new TextButton("Send Gift", skin);
        sendGiftButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (friendship.getLevel() >= FriendShip.LEVEL_1) {
                    openInventoryForGiftSelection(friend);
                    giftDialog.hide();
                } else {
                    showErrorDialog("You need friendship level 1 or higher to send gifts!");
                }
            }
        });

        // View Gift History button
        TextButton historyButton = new TextButton("Gift History", skin);
        historyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showGiftHistory(friend, friendship);
                giftDialog.hide();
            }
        });

        // Cancel button
        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                giftDialog.hide();
            }
        });

        giftDialog.getContentTable().add(sendGiftButton).pad(10).row();
        giftDialog.getContentTable().add(historyButton).pad(10).row();
        giftDialog.getContentTable().add(cancelButton).pad(10).row();

        giftDialog.show(stage);
    }

    private void openInventoryForGiftSelection(Player friend) {
        // Switch to inventory screen for gift selection
        isSelectingGiftItem = true;
        selectedFriend = friend;

        // Create a special inventory screen for gift selection
        org.example.client.views.GiftInventoryScreen giftInventoryScreen = new org.example.client.views.GiftInventoryScreen(currentPlayer, skin, this, friend);
        Main.getGame().setScreen(giftInventoryScreen);
    }

    private void showGiftHistory(Player friend, FriendShip friendship) {
        Dialog historyDialog = new Dialog("Gift History with " + friend.getUser().getUsername(), skin);

        Table historyTable = new Table();
        historyTable.top();

        if (friendship.getGiftHistory().isEmpty()) {
            Label noHistoryLabel = new Label("No gifts exchanged yet.", skin);
            noHistoryLabel.setColor(Color.LIGHT_GRAY);
            historyTable.add(noHistoryLabel).pad(20);
        } else {
            // Add header
            Label headerLabel = new Label("Recent Gifts:", skin);
            headerLabel.setFontScale(1.2f);
            headerLabel.setColor(Color.GOLD);
            historyTable.add(headerLabel).padBottom(15).row();

            // Add each gift record
            for (int i = 0; i < friendship.getGiftHistory().size(); i++) {
                FriendShip.GiftRecord gift = friendship.getGiftHistory().get(i);
                Table giftRow = createGiftHistoryRow(gift, i, friendship);
                historyTable.add(giftRow).fillX().padBottom(5).row();
            }
        }

        ScrollPane historyScrollPane = new ScrollPane(historyTable, skin);
        historyScrollPane.setFadeScrollBars(false);

        historyDialog.getContentTable().add(historyScrollPane).size(400, 300).pad(20);

        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                historyDialog.hide();
            }
        });

        historyDialog.getButtonTable().add(closeButton).pad(10);
        historyDialog.show(stage);
    }

    private Table createGiftHistoryRow(FriendShip.GiftRecord gift, int giftIndex, FriendShip friendship) {
        Table row = new Table();

        String itemName = gift.getItem() != null ? gift.getItem().getName() : "Unknown Item";
        String senderName = gift.getSender() != null && gift.getSender().getUser() != null ?
                           gift.getSender().getUser().getUsername() : "Unknown";

        Label giftLabel = new Label(itemName + " x" + gift.getAmount() + " from " + senderName, skin);
        giftLabel.setColor(Color.WHITE);

        row.add(giftLabel).left().expandX().padRight(10);

        // Rating display/button
        if (gift.getRating() != null) {
            Label ratingLabel = new Label("★" + gift.getRating(), skin);
            ratingLabel.setColor(Color.YELLOW);
            row.add(ratingLabel).right();
        } else if (!gift.getSender().equals(currentPlayer)) {
            // Only allow rating gifts we received, not ones we sent
            TextButton rateButton = new TextButton("Rate", skin);
            rateButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    showRatingDialog(giftIndex, friendship);
                }
            });
            row.add(rateButton).right().width(60).height(25);
        }

        return row;
    }

    private void showRatingDialog(int giftIndex, FriendShip friendship) {
        Dialog ratingDialog = new Dialog("Rate Gift", skin);

        Label promptLabel = new Label("Rate this gift (1-5 stars):", skin);
        ratingDialog.getContentTable().add(promptLabel).padBottom(15).row();

        Table ratingTable = new Table();
        for (int i = 1; i <= 5; i++) {
            final int rating = i;
            TextButton starButton = new TextButton("★" + i, skin);
            starButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (friendship.rateGift(giftIndex, rating)) {
                        showSuccessDialog("Gift rated successfully! Friendship XP increased.");
                        ratingDialog.hide();
                        refreshFriendsList();
                    } else {
                        showErrorDialog("Failed to rate gift. It may already be rated.");
                        ratingDialog.hide();
                    }
                }
            });
            ratingTable.add(starButton).pad(5);
        }

        ratingDialog.getContentTable().add(ratingTable).row();

        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ratingDialog.hide();
            }
        });

        ratingDialog.getButtonTable().add(cancelButton).pad(10);
        ratingDialog.show(stage);
    }

    private void refreshFriendsList() {
        // Clear and recreate the UI to reflect updated friendship data
        stage.clear();
        createUI();
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

    private Texture createRowBackgroundTexture() {
        // Create a slightly lighter background for friend rows
        Color rowColor = new Color(0.3f, 0.3f, 0.4f, 0.8f);
        return createColorTexture(rowColor);
    }

    private Texture createColorTexture(Color color) {
        // This would normally create a 1x1 pixel texture with the specified color
        // For now, return null and use table backgrounds without textures
        return null;
    }

        public void showFriendsWindow() {
        isVisible = true;
        Gdx.input.setInputProcessor(stage);
    }

    public void hideFriendsWindow() {
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
        showFriendsWindow();
    }

    @Override
    public void hide() {
        // This is the Screen interface method - don't call setScreen here to avoid infinite loop
        isVisible = false;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
