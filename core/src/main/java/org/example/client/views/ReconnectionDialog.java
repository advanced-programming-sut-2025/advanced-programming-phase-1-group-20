package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.Align;
import org.example.client.network.NetworkClient;
import org.example.utils.AssetManager;

public class ReconnectionDialog {
    private Dialog dialog;
    private Label statusLabel;
    private Label timeLabel;
    private TextButton cancelButton;
    private ProgressBar progressBar;
    private final NetworkClient networkClient;
    private boolean isVisible = false;

    public ReconnectionDialog(Stage stage, NetworkClient networkClient) {
        this.networkClient = networkClient;
        createDialog(stage);
    }

    private void createDialog(Stage stage) {
        Skin skin = AssetManager.getAssetManager().getSkin();

        dialog = new Dialog("Connection Lost", skin);
        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.setResizable(false);

        // Create content table
        Table contentTable = new Table();
        contentTable.pad(20);

        // Status label
        statusLabel = new Label("Attempting to reconnect...", skin);
        statusLabel.setAlignment(Align.center);
        statusLabel.setColor(Color.YELLOW);
        contentTable.add(statusLabel).expandX().fillX().row();

        // Time remaining label
        timeLabel = new Label("Time remaining: 2:00", skin);
        timeLabel.setAlignment(Align.center);
        timeLabel.setColor(Color.WHITE);
        contentTable.add(timeLabel).expandX().fillX().padTop(10).row();

        // Progress bar
        progressBar = new ProgressBar(0, 120, 1, false, skin);
        progressBar.setValue(120);
        contentTable.add(progressBar).expandX().fillX().padTop(10).row();

        // Cancel button
        cancelButton = new TextButton("Cancel Reconnection", skin);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cancelReconnection();
            }
        });
        contentTable.add(cancelButton).expandX().fillX().padTop(20).row();

        dialog.getContentTable().add(contentTable);

        // Center dialog on screen
        dialog.setPosition(
            (Gdx.graphics.getWidth() - dialog.getWidth()) / 2,
            (Gdx.graphics.getHeight() - dialog.getHeight()) / 2
        );
    }

    public void show() {
        if (!isVisible) {
            isVisible = true;
            dialog.show(dialog.getStage());
            startUpdateTimer();
        }
    }

    public void hide() {
        if (isVisible) {
            isVisible = false;
            dialog.hide();
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    private void startUpdateTimer() {
        // Update the dialog every second
        new Thread(() -> {
            while (isVisible && networkClient.isReconnecting()) {
                try {
                    Thread.sleep(1000);
                    Gdx.app.postRunnable(this::updateDialog);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    private void updateDialog() {
        if (!isVisible) return;

        long remainingTime = networkClient.getRemainingReconnectionTime();
        int minutes = (int) (remainingTime / 60);
        int seconds = (int) (remainingTime % 60);

        timeLabel.setText(String.format("Time remaining: %d:%02d", minutes, seconds));
        progressBar.setValue(remainingTime);

        // Update status based on remaining time
        if (remainingTime <= 30) {
            statusLabel.setText("Reconnection timeout approaching...");
            statusLabel.setColor(Color.RED);
        } else if (remainingTime <= 60) {
            statusLabel.setText("Still attempting to reconnect...");
            statusLabel.setColor(Color.ORANGE);
        } else {
            statusLabel.setText("Attempting to reconnect...");
            statusLabel.setColor(Color.YELLOW);
        }
    }

    private void cancelReconnection() {
        networkClient.cancelReconnection();
        hide();
    }

    public void setSuccess() {
        statusLabel.setText("Reconnection successful!");
        statusLabel.setColor(Color.GREEN);
        cancelButton.setText("Continue");
        cancelButton.clearListeners();
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
    }

    public void setFailed() {
        statusLabel.setText("Reconnection failed. Returning to main menu.");
        statusLabel.setColor(Color.RED);
        cancelButton.setText("OK");
        cancelButton.clearListeners();
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });
    }
}
