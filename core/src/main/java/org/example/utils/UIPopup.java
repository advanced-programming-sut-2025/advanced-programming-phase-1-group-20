package org.example.utils;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class UIPopup {
    private final Stage stage;
    private final Skin skin;

    public UIPopup(Stage stage, Skin skin) {
        this.stage = stage;
        this.skin = skin;
    }

    public void showDialog(String message, String promptType) {
        Dialog dialog = new Dialog(promptType, skin) {
            @Override
            protected void result(Object object) {
            }
        };

        dialog.text(message);
        dialog.button("Confirm");

        dialog.show(stage);
    }
}
