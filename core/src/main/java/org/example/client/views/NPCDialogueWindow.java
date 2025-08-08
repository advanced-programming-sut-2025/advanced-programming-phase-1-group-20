package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import org.example.client.controllers.NPCSpriteController;
import org.example.common.models.App;
import org.example.common.models.entities.NPC;
import org.example.common.models.entities.NPCFriendship;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Date;

import java.util.List;

public class NPCDialogueWindow implements Disposable {
    private Window window;
    private NPC npc;
    private Player player;
    private NPCSpriteController npcSpriteController;
    private Skin skin;
    private Stage stage;

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

    public NPCDialogueWindow(NPC npc, Player player, NPCSpriteController npcSpriteController, Skin skin, Stage stage) {
        this.npc = npc;
        this.player = player;
        this.npcSpriteController = npcSpriteController;
        this.skin = skin;
        this.stage = stage;

        // Get chat history from friendship
        NPCFriendship friendship = npc.getFriendship(player);
        this.chatHistory = friendship.getChatHistory();

        createWindow();
    }

    private void createWindow() {
        // Create main window with custom styling
        window = new Window("", skin);
        window.setModal(true);
        window.setMovable(false);
        window.setResizable(false);

        // Set window size and position
        float windowWidth = 900;
        float windowHeight = 650;
        float windowX = (Gdx.graphics.getWidth() - windowWidth) / 2;
        float windowY = (Gdx.graphics.getHeight() - windowHeight) / 2;
        window.setBounds(windowX, windowY, windowWidth, windowHeight);

        // Create main table with padding
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(15);

        // Header section with NPC info and face
        Table headerSection = createHeaderSection();
        mainTable.add(headerSection).expandX().fillX().row();

        // Separator line
        mainTable.add().expandX().fillX().height(2).pad(10, 0, 10, 0).row();

        // Chat section
        Table chatSection = createChatSection();
        mainTable.add(chatSection).expand().fill().row();

        // Input section
        Table inputSection = createInputSection();
        mainTable.add(inputSection).expandX().fillX().padTop(10);

        window.add(mainTable);

        // Add initial greeting
        addMessage(npc.getName() + ": " + getInitialGreeting(), false);
    }

    private Table createHeaderSection() {
        Table headerSection = new Table();
        headerSection.pad(5);

        // Left side - NPC info
        Table infoTable = new Table();
        infoTable.align(Align.left);

        // NPC name with larger font
        npcNameLabel = new Label(npc.getName(), skin);
        npcNameLabel.setFontScale(1.5f);
        npcNameLabel.setColor(Color.BLACK);

        // Friendship info
        NPCFriendship friendship = npc.getFriendship(player);
        int friendshipLevel = friendship.getLevel();
        int friendshipPoints = friendship.getPoints();
        friendshipLabel = new Label("Friendship Level: " + friendshipLevel + " (Points: " + friendshipPoints + ")", skin);
        friendshipLabel.setColor(Color.BLUE);
        friendshipLabel.setFontScale(1.1f);

        // Description
        if (npc.getDescription() != null && !npc.getDescription().isEmpty()) {
            descriptionLabel = new Label(npc.getDescription(), skin);
            descriptionLabel.setWrap(true);
            descriptionLabel.setColor(Color.GRAY);
            descriptionLabel.setFontScale(0.9f);
        }

        infoTable.add(npcNameLabel).row();
        infoTable.add(friendshipLabel).padTop(5).row();
        if (descriptionLabel != null) {
            infoTable.add(descriptionLabel).width(500).padTop(8).row();
        }

        headerSection.add(infoTable).expandX().fillX().padRight(15);

        // Right side - NPC face image
        npcFaceImage = createNPCFaceImage();
        headerSection.add(npcFaceImage).size(120, 120).padLeft(10);

        // Close button in top-right corner
        closeButton = new TextButton("✕", skin);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                close();
            }
        });
        headerSection.add(closeButton).size(30, 30).padLeft(10);

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
        chatSection.pad(5);

        // Chat area label
        Label chatLabel = new Label("Chat with " + npc.getName(), skin);
        chatLabel.setFontScale(1.2f);
        chatLabel.setColor(Color.BLACK);
        chatSection.add(chatLabel).expandX().fillX().row();

        // Chat messages area with border
        chatTable = new Table();
        chatTable.align(Align.topLeft);
        chatTable.pad(10);

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
        inputSection.pad(5);

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

        inputSection.add(inputField).expandX().fillX().padRight(10);
        inputSection.add(sendButton).width(100).height(40);

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
        Date currentDate = App.getGame().getDate();
        NPCFriendship friendship = npc.getFriendship(player);
        return friendship.talk(currentDate);
    }

    private void addMessage(String message, boolean isPlayer) {
        // Create message container
        Table messageContainer = new Table();
        messageContainer.pad(5, 0, 5, 0);

        // Create message label with better styling
        Label messageLabel = new Label(message, skin);
        messageLabel.setWrap(true);
        messageLabel.setFontScale(1.0f);

        // Color coding for messages
        if (isPlayer) {
            messageLabel.setColor(Color.BLUE);
            messageContainer.setBackground(skin.getDrawable("white"));
            messageContainer.pad(8, 12, 8, 12);
        } else {
            messageLabel.setColor(Color.BLACK);
            messageContainer.setBackground(skin.getDrawable("white"));
            messageContainer.pad(8, 12, 8, 12);
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

    public void show() {
        stage.addActor(window);
        stage.setKeyboardFocus(inputField);
    }

    public void close() {
        window.remove();
    }

    @Override
    public void dispose() {
        if (window != null) {
            window.remove();
        }
    }
}
