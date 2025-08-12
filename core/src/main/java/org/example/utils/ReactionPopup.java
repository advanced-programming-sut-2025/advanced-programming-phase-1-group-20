package org.example.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import org.example.utils.AssetManager;

import java.util.ArrayList;
import java.util.List;

public class ReactionPopup {
    private final Stage stage;
    private final Skin skin;
    private Dialog reactionDialog;
    private ScrollPane emojiScrollPane;
    private Table emojiTable;
    private TextField customReactionField;
    private List<String> predefinedReactions;
    private Array<Texture> emojiTextures;
    private ReactionSelectedListener listener;

    public interface ReactionSelectedListener {
        void onReactionSelected(String reaction);
    }

    public ReactionPopup(Stage stage, Skin skin) {
        this.stage = stage;
        this.skin = skin;
        this.emojiTextures = new Array<>();
        initializePredefinedReactions();
        loadEmojiTextures();
    }

    private void initializePredefinedReactions() {
        predefinedReactions = new ArrayList<>();
        predefinedReactions.add("👍");
        predefinedReactions.add("👎");
        predefinedReactions.add("❤️");
        predefinedReactions.add("😊");
        predefinedReactions.add("😂");
        predefinedReactions.add("😮");
        predefinedReactions.add("😢");
        predefinedReactions.add("😡");
        predefinedReactions.add("🎉");
        predefinedReactions.add("👏");
        predefinedReactions.add("🔥");
        predefinedReactions.add("💯");
        predefinedReactions.add("✨");
        predefinedReactions.add("🌟");
        predefinedReactions.add("💪");
    }

    private void loadEmojiTextures() {
        // Load emoji textures from Emojis000.png to Emojis195.png
        for (int i = 0; i <= 195; i++) {
            try {
                String emojiPath = String.format("content/Emoji/Emojis%03d.png", i);
                Texture emojiTexture = new Texture(Gdx.files.internal(emojiPath));
                emojiTextures.add(emojiTexture);
            } catch (Exception e) {
                // Skip missing emoji files
                System.out.println("Could not load emoji: Emojis" + String.format("%03d", i) + ".png");
            }
        }
    }

    public void showReactionDialog(ReactionSelectedListener listener) {
        this.listener = listener;

        reactionDialog = new Dialog("Select Reaction", skin) {
            @Override
            protected void result(Object object) {
                if (object != null && listener != null) {
                    listener.onReactionSelected(object.toString());
                }
            }
        };

        reactionDialog.setModal(true);
        reactionDialog.setMovable(true);
        reactionDialog.setResizable(true);

        // Create main content table
        Table contentTable = new Table();
        contentTable.pad(10);

        // Add predefined reactions
        contentTable.add(new Label("Quick Reactions:", skin)).colspan(3).padBottom(10).row();

        Table predefinedTable = new Table();
        int cols = 4;
        for (int i = 0; i < predefinedReactions.size(); i++) {
            String reaction = predefinedReactions.get(i);
            TextButton reactionButton = new TextButton(reaction, skin);
            reactionButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    reactionDialog.hide();
                    if (listener != null) {
                        listener.onReactionSelected(reaction);
                    }
                }
            });
            predefinedTable.add(reactionButton).size(50, 50).pad(2);
            if ((i + 1) % cols == 0) {
                predefinedTable.row();
            }
        }

        contentTable.add(predefinedTable).colspan(3).padBottom(10).row();

        // Add emoji scroll section
        contentTable.add(new Label("Emoji Gallery:", skin)).colspan(3).padBottom(10).row();

        emojiTable = new Table();
        createEmojiGrid();

        emojiScrollPane = new ScrollPane(emojiTable, skin);
        emojiScrollPane.setFadeScrollBars(false);
        emojiScrollPane.setScrollingDisabled(false, true);
        emojiScrollPane.setHeight(200);

        contentTable.add(emojiScrollPane).colspan(3).width(300).height(200).padBottom(10).row();

        // Add custom reaction field
        contentTable.add(new Label("Custom Reaction (max 10 chars):", skin)).colspan(3).padBottom(5).row();

        customReactionField = new TextField("", skin);
        customReactionField.setMaxLength(10);
        customReactionField.setMessageText("Enter custom reaction...");

        contentTable.add(customReactionField).colspan(2).width(200).padBottom(10);

        TextButton customButton = new TextButton("Send", skin);
        customButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String customReaction = customReactionField.getText().trim();
                if (!customReaction.isEmpty()) {
                    reactionDialog.hide();
                    if (listener != null) {
                        listener.onReactionSelected(customReaction);
                    }
                }
            }
        });
        contentTable.add(customButton).width(80).padBottom(10).row();

        // Add close button
        TextButton closeButton = new TextButton("Cancel", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                reactionDialog.hide();
            }
        });
        contentTable.add(closeButton).colspan(3).width(100).padTop(10);

        reactionDialog.getContentTable().add(contentTable);
        reactionDialog.show(stage);
    }

    private void createEmojiGrid() {
        emojiTable.clear();
        int cols = 6;
        int emojiSize = 40;

        for (int i = 0; i < emojiTextures.size; i++) {
            Texture emojiTexture = emojiTextures.get(i);
            Image emojiImage = new Image(emojiTexture);
            emojiImage.setSize(emojiSize, emojiSize);

            // Create clickable button for emoji
            Table emojiButton = new Table();
            emojiButton.setBackground(skin.newDrawable("white", 0.8f, 0.8f, 0.8f, 0.3f));
            emojiButton.add(emojiImage).size(emojiSize, emojiSize);

            final int emojiIndex = i;
            emojiButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    reactionDialog.hide();
                    if (listener != null) {
                        // Use emoji index as reaction identifier
                        listener.onReactionSelected("EMOJI_" + emojiIndex);
                    }
                }
            });

            emojiTable.add(emojiButton).size(emojiSize + 4, emojiSize + 4).pad(2);
            if ((i + 1) % cols == 0) {
                emojiTable.row();
            }
        }
    }

    public Texture getEmojiTexture(int index) {
        if (index >= 0 && index < emojiTextures.size) {
            return emojiTextures.get(index);
        }
        return null;
    }

    public void dispose() {
        for (Texture texture : emojiTextures) {
            texture.dispose();
        }
        emojiTextures.clear();
    }
}
