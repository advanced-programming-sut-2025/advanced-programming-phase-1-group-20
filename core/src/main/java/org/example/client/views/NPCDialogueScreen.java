package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.client.controllers.NPCSpriteController;
import org.example.common.models.entities.NPC;
import org.example.common.models.entities.NPCFriendship;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Date;

import java.util.List;

public class NPCDialogueScreen implements Screen, Disposable {
    private Stage stage;
    private NPC npc;
    private Player player;
    private NPCSpriteController npcSpriteController;
    private Skin skin;
    private Screen previousScreen;
    
    // UI Components
    private ScrollPane chatScrollPane;
    private Table chatTable;
    private TextField inputField;
    private TextButton sendButton;
    private TextButton closeButton;
    private Image npcFaceImage;
    private Label npcNameLabel;
    private Label friendshipLabel;
    private Label descriptionLabel;
    
    // Chat history
    private List<String> chatHistory;
    
    // Background
    private Texture backgroundTexture;
    
    public NPCDialogueScreen(NPC npc, Player player, NPCSpriteController npcSpriteController, Skin skin, Screen previousScreen) {
        this.npc = npc;
        this.player = player;
        this.npcSpriteController = npcSpriteController;
        this.skin = skin;
        this.previousScreen = previousScreen;
        
        // Get chat history from friendship
        NPCFriendship friendship = npc.getFriendship(player);
        this.chatHistory = friendship.getChatHistory();
        
        // Initialize stage
        stage = new Stage(new ScreenViewport());
        
        // Create background texture
        createBackgroundTexture();
        
        createUI();
    }
    
    private void createBackgroundTexture() {
        // Create a simple background texture using Pixmap
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(0.94f, 0.94f, 0.94f, 1f);
        pixmap.fill();
        backgroundTexture = new Texture(pixmap);
        pixmap.dispose();
    }
    
    private void createUI() {
        // Main container
        Table mainContainer = new Table();
        mainContainer.setFillParent(true);
        mainContainer.pad(20);
        
        // Header section
        Table headerSection = createHeaderSection();
        mainContainer.add(headerSection).expandX().fillX().row();
        
        // Separator
        mainContainer.add().expandX().fillX().height(3).pad(15, 0, 15, 0).row();
        
        // Chat section
        Table chatSection = createChatSection();
        mainContainer.add(chatSection).expand().fill().row();
        
        // Input section
        Table inputSection = createInputSection();
        mainContainer.add(inputSection).expandX().fillX().padTop(20);
        
        stage.addActor(mainContainer);
        
        // Add initial greeting
        addMessage(npc.getName() + ": " + getInitialGreeting(), false);
    }
    
    private Table createHeaderSection() {
        Table headerSection = new Table();
        headerSection.pad(10);
        
        // Left side - NPC info
        Table infoTable = new Table();
        infoTable.align(Align.left);
        
        // NPC name with larger font
        npcNameLabel = new Label(npc.getName(), skin);
        npcNameLabel.setFontScale(2.0f);
        npcNameLabel.setColor(Color.BLACK);
        
        // Friendship info
        NPCFriendship friendship = npc.getFriendship(player);
        int friendshipLevel = friendship.getLevel();
        int friendshipPoints = friendship.getPoints();
        friendshipLabel = new Label("Friendship Level: " + friendshipLevel + " (Points: " + friendshipPoints + ")", skin);
        friendshipLabel.setColor(Color.BLUE);
        friendshipLabel.setFontScale(1.3f);
        
        // Description
        if (npc.getDescription() != null && !npc.getDescription().isEmpty()) {
            descriptionLabel = new Label(npc.getDescription(), skin);
            descriptionLabel.setWrap(true);
            descriptionLabel.setColor(Color.GRAY);
            descriptionLabel.setFontScale(1.1f);
        }
        
        infoTable.add(npcNameLabel).row();
        infoTable.add(friendshipLabel).padTop(10).row();
        if (descriptionLabel != null) {
            infoTable.add(descriptionLabel).width(600).padTop(15).row();
        }
        
        headerSection.add(infoTable).expandX().fillX().padRight(30);
        
        // Right side - NPC face image
        npcFaceImage = createNPCFaceImage();
        headerSection.add(npcFaceImage).size(150, 150).padLeft(20);
        
        // Close button in top-right corner
        closeButton = new TextButton("✕", skin);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                close();
            }
        });
        headerSection.add(closeButton).size(40, 40).padLeft(20);
        
        return headerSection;
    }
    
    private Image createNPCFaceImage() {
        try {
            // Load the NPC's face asset directly
            String npcName = npc.getName();
            String facePath = "content/NPC/" + npcName + "/face_0.png";
            
            // Try to load the face texture
            Texture faceTexture = new Texture(facePath);
            if (faceTexture != null) {
                return new Image(faceTexture);
            }
        } catch (Exception e) {
            System.err.println("Could not load face asset for " + npc.getName() + ": " + e.getMessage());
        }
        
        // Fallback: try to get from sprite controller
        try {
            TextureRegion faceFrame = npcSpriteController.getCurrentFrame(npc, 0f);
            if (faceFrame != null) {
                // Create a face-focused region (top portion of the sprite)
                int faceWidth = faceFrame.getRegionWidth();
                int faceHeight = faceFrame.getRegionHeight() / 2; // Take top half for face
                TextureRegion faceRegion = new TextureRegion(faceFrame, 0, faceHeight, faceWidth, faceHeight);
                return new Image(new TextureRegionDrawable(faceRegion));
            }
        } catch (Exception e) {
            System.err.println("Could not get sprite frame for " + npc.getName() + ": " + e.getMessage());
        }
        
        // Final fallback: create a colored rectangle with NPC name
        return new Image(skin.getDrawable("white"));
    }
    
    private Table createChatSection() {
        Table chatSection = new Table();
        chatSection.pad(10);
        
        // Chat area label
        Label chatLabel = new Label("Chat with " + npc.getName(), skin);
        chatLabel.setFontScale(1.5f);
        chatLabel.setColor(Color.BLACK);
        chatSection.add(chatLabel).expandX().fillX().row();
        
        // Chat messages area with border
        chatTable = new Table();
        chatTable.align(Align.topLeft);
        chatTable.pad(15);
        
        // Create scroll pane for chat with custom styling
        chatScrollPane = new ScrollPane(chatTable, skin);
        chatScrollPane.setFadeScrollBars(false);
        chatScrollPane.setScrollBarPositions(false, true);
        chatScrollPane.setScrollingDisabled(false, false);
        
        chatSection.add(chatScrollPane).expand().fill().row();
        
        return chatSection;
    }
    
    private Table createInputSection() {
        Table inputSection = new Table();
        inputSection.pad(10);
        
        // Input field with better styling
        inputField = new TextField("", skin);
        inputField.setMessageText("Type your message...");
        inputField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Handle enter key
                if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
                    sendMessage();
                }
            }
        });
        
        // Send button with better styling
        sendButton = new TextButton("Send", skin);
        sendButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sendMessage();
            }
        });
        
        inputSection.add(inputField).expandX().fillX().padRight(15);
        inputSection.add(sendButton).width(120).height(50);
        
        return inputSection;
    }
    
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            // Add player message
            addMessage("You: " + message, true);
            inputField.setText("");
            
            // Get NPC response
            String npcResponse = getNPCResponse();
            addMessage(npc.getName() + ": " + npcResponse, false);
        }
    }
    
    private String getInitialGreeting() {
        Date currentDate = org.example.common.models.App.getGame().getDate();
        NPCFriendship friendship = npc.getFriendship(player);
        return friendship.talk(currentDate);
    }
    
    private String getNPCResponse() {
        Date currentDate = org.example.common.models.App.getGame().getDate();
        NPCFriendship friendship = npc.getFriendship(player);
        return friendship.talk(currentDate);
    }
    
    private void addMessage(String message, boolean isPlayer) {
        // Create message container
        Table messageContainer = new Table();
        messageContainer.pad(8, 0, 8, 0);
        
        // Create message label with better styling
        Label messageLabel = new Label(message, skin);
        messageLabel.setWrap(true);
        messageLabel.setFontScale(1.2f);
        
        // Color coding for messages
        if (isPlayer) {
            messageLabel.setColor(Color.BLUE);
            messageContainer.setBackground(skin.getDrawable("white"));
            messageContainer.pad(12, 16, 12, 16);
        } else {
            messageLabel.setColor(Color.BLACK);
            messageContainer.setBackground(skin.getDrawable("white"));
            messageContainer.pad(12, 16, 12, 16);
        }
        
        // Add message to container
        messageContainer.add(messageLabel).expandX().fillX();
        
        // Add to chat table with alignment
        if (isPlayer) {
            chatTable.add(messageContainer).expandX().fillX().align(Align.right).row();
        } else {
            chatTable.add(messageContainer).expandX().fillX().align(Align.left).row();
        }
        
        // Scroll to bottom
        chatScrollPane.scrollTo(0, 0, 0, 0);
        
        // Update chat history
        chatHistory.add(message);
    }
    
    public void close() {
        // Return to previous screen
        if (previousScreen != null) {
            Gdx.app.postRunnable(() -> {
                Main.getGame().setScreen(previousScreen);
            });
        }
    }
    
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.setKeyboardFocus(inputField);
    }
    
    @Override
    public void render(float delta) {
        // Clear screen with background color
        Gdx.gl.glClearColor(0.94f, 0.94f, 0.94f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Draw background
        stage.getBatch().begin();
        stage.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();
        
        // Render stage
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void pause() {
        // Not needed
    }
    
    @Override
    public void resume() {
        // Not needed
    }
    
    @Override
    public void hide() {
        // Not needed
    }
    
    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
