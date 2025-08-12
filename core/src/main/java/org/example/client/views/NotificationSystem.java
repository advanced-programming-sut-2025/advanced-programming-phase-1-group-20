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
import com.badlogic.gdx.utils.Timer;
import org.example.common.models.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationSystem {
    private final Stage stage;
    private final Skin skin;
    private final BitmapFont font;
    private final List<NotificationToast> activeToasts;
    private final List<NotificationDialog> activeDialogs;
    private final float screenWidth;
    private final float screenHeight;
    
    // Configuration
    private static final float TOAST_DURATION = 3.0f; // seconds
    private static final float TOAST_FADE_DURATION = 0.5f; // seconds
    private static final float TOAST_WIDTH = 300;
    private static final float TOAST_HEIGHT = 80;
    private static final float TOAST_MARGIN = 10;
    private static final int MAX_TOASTS = 5;

    public NotificationSystem(Stage stage) {
        this.stage = stage;
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        this.font = new BitmapFont();
        this.activeToasts = new ArrayList<>();
        this.activeDialogs = new ArrayList<>();
        this.screenWidth = Gdx.graphics.getWidth();
        this.screenHeight = Gdx.graphics.getHeight();
    }

    public void showNotification(Notification notification) {
        switch (notification.getPriority()) {
            case URGENT:
            case HIGH:
                showDialog(notification);
                break;
            case NORMAL:
            case LOW:
                showToast(notification);
                break;
        }
    }

    public void showToast(Notification notification) {
        if (activeToasts.size() >= MAX_TOASTS) {
            // Remove oldest toast
            NotificationToast oldestToast = activeToasts.remove(0);
            oldestToast.remove();
        }

        NotificationToast toast = new NotificationToast(notification, skin);
        activeToasts.add(toast);
        
        // Position the toast
        float x = screenWidth - TOAST_WIDTH - TOAST_MARGIN;
        float y = screenHeight - TOAST_HEIGHT - TOAST_MARGIN - (activeToasts.size() - 1) * (TOAST_HEIGHT + TOAST_MARGIN);
        toast.setPosition(x, y);
        
        stage.addActor(toast);
        
        // Schedule removal
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                removeToast(toast);
            }
        }, TOAST_DURATION);
    }

    public void showDialog(Notification notification) {
        NotificationDialog dialog = new NotificationDialog(notification, skin);
        activeDialogs.add(dialog);
        dialog.show(stage);
    }

    private void removeToast(NotificationToast toast) {
        activeToasts.remove(toast);
        toast.remove();
        repositionToasts();
    }

    private void repositionToasts() {
        for (int i = 0; i < activeToasts.size(); i++) {
            NotificationToast toast = activeToasts.get(i);
            float x = screenWidth - TOAST_WIDTH - TOAST_MARGIN;
            float y = screenHeight - TOAST_HEIGHT - TOAST_MARGIN - i * (TOAST_HEIGHT + TOAST_MARGIN);
            toast.setPosition(x, y);
        }
    }

    public void update(float delta) {
        // Update any animations or timers if needed
    }

    public void dispose() {
        for (NotificationToast toast : activeToasts) {
            toast.remove();
        }
        activeToasts.clear();
        
        for (NotificationDialog dialog : activeDialogs) {
            dialog.hide();
        }
        activeDialogs.clear();
    }

    // Inner class for toast notifications
    private static class NotificationToast extends Table {
        private final Notification notification;
        private float alpha = 1.0f;
        private boolean fading = false;

        public NotificationToast(Notification notification, Skin skin) {
            this.notification = notification;
            
            setBackground(skin.newDrawable("white", getBackgroundColor()));
            setSize(TOAST_WIDTH, TOAST_HEIGHT);
            
            // Create content
            Table contentTable = new Table();
            
            Label titleLabel = new Label(notification.getTitle(), skin);
            titleLabel.setColor(getTitleColor());
            titleLabel.setFontScale(0.8f);
            
            Label messageLabel = new Label(notification.getMessage(), skin);
            messageLabel.setColor(Color.WHITE);
            messageLabel.setFontScale(0.7f);
            messageLabel.setWrap(true);
            
            Label timeLabel = new Label(notification.getTimestamp(), skin);
            timeLabel.setColor(Color.GRAY);
            timeLabel.setFontScale(0.6f);
            
            contentTable.add(titleLabel).expandX().fillX().pad(5);
            contentTable.row();
            contentTable.add(messageLabel).expandX().fillX().pad(5);
            contentTable.row();
            contentTable.add(timeLabel).expandX().fillX().pad(5);
            
            add(contentTable).expand().fill().pad(10);
        }

        private Color getBackgroundColor() {
            switch (notification.getType()) {
                case CHAT_MESSAGE:
                    return new Color(0.2f, 0.6f, 0.8f, 0.9f);
                case PRIVATE_MESSAGE:
                    return new Color(0.8f, 0.4f, 0.8f, 0.9f);
                case SYSTEM_MESSAGE:
                    return new Color(0.6f, 0.6f, 0.6f, 0.9f);
                case TRADE_REQUEST:
                    return new Color(0.8f, 0.8f, 0.2f, 0.9f);
                case GIFT_RECEIVED:
                    return new Color(0.2f, 0.8f, 0.4f, 0.9f);
                case QUEST_UPDATE:
                    return new Color(0.8f, 0.6f, 0.2f, 0.9f);
                case WEATHER_ALERT:
                    return new Color(0.8f, 0.2f, 0.2f, 0.9f);
                case FARM_UPDATE:
                    return new Color(0.4f, 0.8f, 0.4f, 0.9f);
                default:
                    return new Color(0.3f, 0.3f, 0.3f, 0.9f);
            }
        }

        private Color getTitleColor() {
            switch (notification.getPriority()) {
                case URGENT:
                    return Color.RED;
                case HIGH:
                    return Color.ORANGE;
                case NORMAL:
                    return Color.WHITE;
                case LOW:
                    return Color.LIGHT_GRAY;
                default:
                    return Color.WHITE;
            }
        }

        public void startFade() {
            fading = true;
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            
            if (fading) {
                alpha -= delta / TOAST_FADE_DURATION;
                if (alpha <= 0) {
                    remove();
                } else {
                    getColor().a = alpha;
                }
            }
        }
    }

    // Inner class for dialog notifications
    private static class NotificationDialog extends Dialog {
        private final Notification notification;

        public NotificationDialog(Notification notification, Skin skin) {
            super(notification.getTitle(), skin);
            this.notification = notification;
            
            setModal(true);
            setMovable(true);
            setResizable(true);
            
            // Create content
            Table contentTable = new Table();
            
            Label messageLabel = new Label(notification.getMessage(), skin);
            messageLabel.setWrap(true);
            messageLabel.setAlignment(Align.center);
            
            Label timeLabel = new Label(notification.getTimestamp(), skin);
            timeLabel.setColor(Color.GRAY);
            timeLabel.setAlignment(Align.center);
            
            Label typeLabel = new Label("Type: " + notification.getType().toString(), skin);
            typeLabel.setColor(Color.LIGHT_GRAY);
            typeLabel.setAlignment(Align.center);
            
            contentTable.add(messageLabel).expandX().fillX().pad(10);
            contentTable.row();
            contentTable.add(timeLabel).expandX().fillX().pad(5);
            contentTable.row();
            contentTable.add(typeLabel).expandX().fillX().pad(5);
            
            getContentTable().add(contentTable).expand().fill().pad(20);
            
            // Add buttons
            TextButton okButton = new TextButton("OK", skin);
            okButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    hide();
                }
            });
            
            getButtonTable().add(okButton).expandX().fillX().pad(10);
            
            // Set dialog size
            setSize(400, 200);
            
            // Center the dialog
            setPosition(
                (Gdx.graphics.getWidth() - getWidth()) / 2,
                (Gdx.graphics.getHeight() - getHeight()) / 2
            );
        }

        @Override
        public void hide() {
            super.hide();
            remove();
        }
    }

    // Utility methods for different notification types
    public void showChatNotification(String sender, String message) {
        Notification notification = new Notification(
            "New Chat Message",
            sender + ": " + message,
            Notification.NotificationType.CHAT_MESSAGE,
            Notification.NotificationPriority.LOW
        );
        notification.setSender(sender);
        showNotification(notification);
    }

    public void showPrivateMessageNotification(String sender, String message) {
        Notification notification = new Notification(
            "Private Message",
            sender + ": " + message,
            Notification.NotificationType.PRIVATE_MESSAGE,
            Notification.NotificationPriority.HIGH
        );
        notification.setSender(sender);
        showNotification(notification);
    }

    public void showSystemNotification(String title, String message) {
        Notification notification = new Notification(
            title,
            message,
            Notification.NotificationType.SYSTEM_MESSAGE,
            Notification.NotificationPriority.NORMAL
        );
        showNotification(notification);
    }

    public void showTradeRequestNotification(String sender) {
        Notification notification = new Notification(
            "Trade Request",
            sender + " wants to trade with you",
            Notification.NotificationType.TRADE_REQUEST,
            Notification.NotificationPriority.HIGH
        );
        notification.setSender(sender);
        showNotification(notification);
    }

    public void showGiftReceivedNotification(String sender, String itemName) {
        Notification notification = new Notification(
            "Gift Received",
            sender + " sent you: " + itemName,
            Notification.NotificationType.GIFT_RECEIVED,
            Notification.NotificationPriority.NORMAL
        );
        notification.setSender(sender);
        showNotification(notification);
    }

    public void showQuestUpdateNotification(String questName, String update) {
        Notification notification = new Notification(
            "Quest Update: " + questName,
            update,
            Notification.NotificationType.QUEST_UPDATE,
            Notification.NotificationPriority.NORMAL
        );
        showNotification(notification);
    }

    public void showWeatherAlertNotification(String weatherType) {
        Notification notification = new Notification(
            "Weather Alert",
            "Weather changed to: " + weatherType,
            Notification.NotificationType.WEATHER_ALERT,
            Notification.NotificationPriority.NORMAL
        );
        showNotification(notification);
    }

    public void showFarmUpdateNotification(String update) {
        Notification notification = new Notification(
            "Farm Update",
            update,
            Notification.NotificationType.FARM_UPDATE,
            Notification.NotificationPriority.LOW
        );
        showNotification(notification);
    }
}

