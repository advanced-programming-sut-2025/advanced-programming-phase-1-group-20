package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import org.example.client.Main;

public class ChatButton {
    private final Main game;
    private final Stage stage;
    private final Skin skin;
    private final TextButton chatButton;
    private final TextButton notificationButton;
    private final Label notificationCountLabel;
    private int notificationCount = 0;
    
    // Position and size
    private static final float BUTTON_WIDTH = 60;
    private static final float BUTTON_HEIGHT = 40;
    private static final float MARGIN = 10;

    public ChatButton(Main game, Stage stage) {
        this.game = game;
        this.stage = stage;
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        
        // Create chat button
        chatButton = new TextButton("Chat", skin);
        chatButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        chatButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                openChatScreen();
            }
        });
        
        // Create notification button with counter
        Table notificationTable = new Table();
        notificationCountLabel = new Label("0", skin);
        notificationCountLabel.setColor(Color.RED);
        notificationCountLabel.setFontScale(0.8f);
        
        notificationButton = new TextButton("!", skin);
        notificationButton.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        notificationButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                openNotificationCenter();
            }
        });
        
        notificationTable.add(notificationButton);
        notificationTable.add(notificationCountLabel).padLeft(-10).padTop(-10);
        
        // Position buttons in top-right corner
        positionButtons();
        
        // Add to stage
        stage.addActor(chatButton);
        stage.addActor(notificationTable);
    }

    private void positionButtons() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        
        // Position chat button
        chatButton.setPosition(
            screenWidth - BUTTON_WIDTH - MARGIN,
            screenHeight - BUTTON_HEIGHT - MARGIN
        );
        
        // Position notification button
        notificationButton.setPosition(
            screenWidth - BUTTON_WIDTH - MARGIN,
            screenHeight - BUTTON_HEIGHT * 2 - MARGIN * 2
        );
    }

    private void openChatScreen() {
        ChatScreen chatScreen = new ChatScreen(game);
        game.setScreen(chatScreen);
    }

    private void openNotificationCenter() {
        // For now, just clear notifications
        // In a full implementation, this would open a notification center
        clearNotifications();
    }

    public void addNotification() {
        notificationCount++;
        updateNotificationDisplay();
    }

    public void clearNotifications() {
        notificationCount = 0;
        updateNotificationDisplay();
    }

    private void updateNotificationDisplay() {
        if (notificationCount > 0) {
            notificationCountLabel.setText(String.valueOf(notificationCount));
            notificationCountLabel.setVisible(true);
            notificationButton.setColor(Color.RED);
        } else {
            notificationCountLabel.setVisible(false);
            notificationButton.setColor(Color.WHITE);
        }
    }

    public void resize(int width, int height) {
        positionButtons();
    }

    public void dispose() {
        // Buttons will be disposed with the stage
    }

    // Utility methods for different notification types
    public void notifyChatMessage(String sender) {
        addNotification();
    }

    public void notifyPrivateMessage(String sender) {
        addNotification();
    }

    public void notifySystemMessage() {
        addNotification();
    }

    public void notifyTradeRequest(String sender) {
        addNotification();
    }

    public void notifyGiftReceived(String sender) {
        addNotification();
    }

    public void notifyQuestUpdate() {
        addNotification();
    }

    public void notifyWeatherAlert() {
        addNotification();
    }

    public void notifyFarmUpdate() {
        addNotification();
    }
}

