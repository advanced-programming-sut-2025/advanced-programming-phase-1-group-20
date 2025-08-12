package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import org.example.utils.ReactionPopup;

import java.util.HashMap;
import java.util.Map;

public class ReactionDisplay implements Disposable {
    private final Stage stage;
    private final Map<String, Array<ReactionActor>> playerReactions;
    private final ReactionPopup reactionPopup;
    private final BitmapFont font;
    private final Skin skin;
    private final float DISPLAY_DURATION = 5.0f; // 5 seconds
    private final float FADE_DURATION = 1.0f; // 1 second fade
    private final float REACTION_HEIGHT = 50f; // Height above player head
    
    public ReactionDisplay(Stage stage, ReactionPopup reactionPopup, Skin skin) {
        this.stage = stage;
        this.reactionPopup = reactionPopup;
        this.skin = skin;
        this.playerReactions = new HashMap<>();
        this.font = new BitmapFont();
        this.font.getData().setScale(1.2f);
    }
    
    public void showReaction(String playerUsername, String reaction, Vector2 playerPosition) {
        // Remove existing reactions for this player
        removePlayerReactions(playerUsername);
        
        // Create new reaction actor
        ReactionActor reactionActor = createReactionActor(reaction, playerPosition);
        
        // Add to player's reaction list
        if (!playerReactions.containsKey(playerUsername)) {
            playerReactions.put(playerUsername, new Array<>());
        }
        playerReactions.get(playerUsername).add(reactionActor);
        
        // Add to stage
        stage.addActor(reactionActor);
        
        // Schedule removal after 5 seconds
        reactionActor.addAction(Actions.sequence(
            Actions.delay(DISPLAY_DURATION),
            Actions.fadeOut(FADE_DURATION),
            Actions.run(() -> {
                stage.getActors().removeValue(reactionActor, true);
                if (playerReactions.containsKey(playerUsername)) {
                    playerReactions.get(playerUsername).removeValue(reactionActor, true);
                }
            })
        ));
    }
    
            private ReactionActor createReactionActor(String reaction, Vector2 playerPosition) {
            ReactionActor actor = new ReactionActor(reaction, playerPosition, skin);
            
            // Position above player head
            actor.setPosition(playerPosition.x - actor.getWidth() / 2, 
                             playerPosition.y + REACTION_HEIGHT);
            
            return actor;
        }
    
    private void removePlayerReactions(String playerUsername) {
        if (playerReactions.containsKey(playerUsername)) {
            Array<ReactionActor> reactions = playerReactions.get(playerUsername);
            for (ReactionActor reaction : reactions) {
                stage.getActors().removeValue(reaction, true);
            }
            reactions.clear();
        }
    }
    
    public void updatePlayerPosition(String playerUsername, Vector2 newPosition) {
        if (playerReactions.containsKey(playerUsername)) {
            Array<ReactionActor> reactions = playerReactions.get(playerUsername);
            for (ReactionActor reaction : reactions) {
                reaction.setPosition(newPosition.x - reaction.getWidth() / 2,
                                   newPosition.y + REACTION_HEIGHT);
            }
        }
    }
    
    public void clearAllReactions() {
        for (Array<ReactionActor> reactions : playerReactions.values()) {
            for (ReactionActor reaction : reactions) {
                stage.getActors().removeValue(reaction, true);
            }
            reactions.clear();
        }
        playerReactions.clear();
    }
    
    @Override
    public void dispose() {
        font.dispose();
        clearAllReactions();
    }
    
    private class ReactionActor extends Table {
        private final String reaction;
        private final Vector2 originalPosition;
        private Image emojiImage;
        private Label textLabel;
        
        public ReactionActor(String reaction, Vector2 position, Skin skin) {
            this.reaction = reaction;
            this.originalPosition = position.cpy();
            
            setupReactionDisplay(skin);
        }
        
        private void setupReactionDisplay(Skin skin) {
            if (reaction.startsWith("EMOJI_")) {
                // Handle emoji reaction
                int emojiIndex = Integer.parseInt(reaction.substring(6));
                Texture emojiTexture = reactionPopup.getEmojiTexture(emojiIndex);
                if (emojiTexture != null) {
                    emojiImage = new Image(emojiTexture);
                    emojiImage.setSize(32, 32);
                    add(emojiImage).size(32, 32);
                }
            } else {
                // Handle text reaction
                textLabel = new Label(reaction, new Label.LabelStyle(font, Color.WHITE));
                textLabel.setColor(Color.YELLOW);
                add(textLabel);
            }
            
            // Add background for better visibility
            setBackground(skin.newDrawable("white", 0, 0, 0, 0.7f));
            
            pack();
        }
        
        public String getReaction() {
            return reaction;
        }
        
        public Vector2 getOriginalPosition() {
            return originalPosition.cpy();
        }
    }
}
