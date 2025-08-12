package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.client.Main;
import org.example.client.network.NetworkClient;
import org.example.common.models.ChatMessage;
import org.example.common.models.ChatRoom;
import org.example.common.models.Message;
import org.example.common.models.Notification;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChatScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private Skin skin;
    
    // UI Components
    private Table mainTable;
    private Table chatTable;
    private Table inputTable;
    private Table roomTable;
    private Table userTable;
    
    // Chat components
    private TextArea chatArea;
    private TextField messageInput;
    private TextField recipientInput;
    private SelectBox<String> chatTypeSelect;
    private SelectBox<String> roomSelect;
    private SelectBox<String> userSelect;
    private Button sendButton;
    private Button createRoomButton;
    private Button joinRoomButton;
    private Button leaveRoomButton;
    private Button backButton;
    
    // Data
    private List<ChatMessage> publicMessages;
    private List<ChatMessage> privateMessages;
    private List<ChatRoom> availableRooms;
    private List<String> onlineUsers;
    private String currentChatType;
    private String currentRoomId;
    private String currentRecipient;
    private final Gson gson;
    
    // Colors
    private final Color backgroundColor = new Color(0.1f, 0.1f, 0.1f, 0.9f);
    private final Color chatBackgroundColor = new Color(0.15f, 0.15f, 0.15f, 1f);
    private final Color inputBackgroundColor = new Color(0.2f, 0.2f, 0.2f, 1f);

    public ChatScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        
        // Try to load skin, fallback to default if not available
        try {
            this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        } catch (Exception e) {
            System.err.println("Could not load ui/uiskin.json, using default skin");
            this.skin = new Skin();
        }
        
        this.gson = new Gson();
        
        // Initialize data
        this.publicMessages = new ArrayList<>();
        this.privateMessages = new ArrayList<>();
        this.availableRooms = new ArrayList<>();
        this.onlineUsers = new ArrayList<>();
        this.currentChatType = "public";
        
        setupUI();
        loadOnlineUsers();
        loadAvailableRooms();
    }

    private void setupUI() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20);
        
        // Create a simple chat interface
        Label titleLabel = new Label("Chat System", skin);
        titleLabel.setFontScale(1.5f);
        
        chatArea = new TextArea("Welcome to the chat system!\n\nThis is a basic chat interface.\n\nFeatures:\n- Public Chat\n- Private Messages\n- Chat Rooms\n- Notifications", skin);
        chatArea.setDisabled(true);
        chatArea.setPrefRows(15);
        
        messageInput = new TextField("", skin);
        messageInput.setMessageText("Type your message here...");
        
        sendButton = new TextButton("Send", skin);
        sendButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String message = messageInput.getText().trim();
                if (!message.isEmpty()) {
                    chatArea.setText(chatArea.getText() + "\nYou: " + message);
                    messageInput.setText("");
                }
            }
        });
        
        backButton = new TextButton("Back to Game", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Return to the previous screen
                dispose();
            }
        });
        
        // Layout
        mainTable.add(titleLabel).colspan(2).padBottom(20);
        mainTable.row();
        mainTable.add(chatArea).expand().fill().colspan(2);
        mainTable.row();
        mainTable.add(messageInput).expandX().fillX().padRight(10);
        mainTable.add(sendButton);
        mainTable.row();
        mainTable.add(backButton).colspan(2).padTop(20);
        
        stage.addActor(mainTable);
    }

    // Simplified chat area creation - removed complex functionality for now

    // Simplified input area creation - removed complex functionality for now

    // Simplified control panels - removed complex functionality for now

    // Simplified methods - removed complex functionality for now

    private void showCreateRoomDialog() {
        // Simple dialog for creating a room
        Dialog dialog = new Dialog("Create Chat Room", skin);
        TextField roomNameField = new TextField("", skin);
        roomNameField.setMessageText("Enter room name");
        
        dialog.getContentTable().add(roomNameField).expandX().fillX();
        
        TextButton createButton = new TextButton("Create", skin);
        TextButton cancelButton = new TextButton("Cancel", skin);
        
        createButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String roomName = roomNameField.getText().trim();
                if (!roomName.isEmpty()) {
                    createChatRoom(roomName);
                }
                dialog.hide();
            }
        });
        
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide();
            }
        });
        
        dialog.getButtonTable().add(createButton);
        dialog.getButtonTable().add(cancelButton);
        
        dialog.show(stage);
    }

    private void createChatRoom(String roomName) {
        Message message = new Message();
        message.setType(Message.Type.CHAT_ROOM_CREATE);
        message.putInBody("roomName", roomName);
        NetworkClient.getInstance().sendMessage(message);
    }

    private void joinSelectedRoom() {
        String selectedRoom = roomSelect.getSelected();
        if (selectedRoom != null && !selectedRoom.equals("No rooms available")) {
            // Extract room ID from the display string
            for (ChatRoom room : availableRooms) {
                if (room.getRoomName().equals(selectedRoom)) {
                    joinChatRoom(room.getRoomId());
                    break;
                }
            }
        }
    }

    private void joinChatRoom(String roomId) {
        Message message = new Message();
        message.setType(Message.Type.CHAT_ROOM_JOIN);
        message.putInBody("roomId", roomId);
        NetworkClient.getInstance().sendMessage(message);
        currentRoomId = roomId;
    }

    private void leaveCurrentRoom() {
        if (currentRoomId != null) {
            Message message = new Message();
            message.setType(Message.Type.CHAT_ROOM_LEAVE);
            message.putInBody("roomId", currentRoomId);
            NetworkClient.getInstance().sendMessage(message);
            currentRoomId = null;
        }
    }

    private void loadOnlineUsers() {
        Message message = new Message();
        message.setType(Message.Type.REQUEST_PLAYERS_LIST);
        NetworkClient.getInstance().sendMessage(message);
    }

    private void loadAvailableRooms() {
        // This would be implemented when the server sends room list updates
        updateRoomSelect();
    }

    private void loadPublicChatHistory() {
        Message message = new Message();
        message.setType(Message.Type.CHAT_HISTORY_REQUEST);
        message.putInBody("chatType", "public");
        NetworkClient.getInstance().sendMessage(message);
    }

    private void loadPrivateChatHistory() {
        if (currentRecipient != null) {
            Message message = new Message();
            message.setType(Message.Type.CHAT_HISTORY_REQUEST);
            message.putInBody("chatType", "private");
            message.putInBody("target", currentRecipient);
            NetworkClient.getInstance().sendMessage(message);
        }
    }

    private void loadRoomChatHistory() {
        if (currentRoomId != null) {
            // Room history is loaded when joining the room
            updateChatDisplay();
        }
    }

    public void handleChatMessage(ChatMessage chatMessage) {
        switch (chatMessage.getType()) {
            case PUBLIC:
                publicMessages.add(chatMessage);
                if (currentChatType.equals("publicchat")) {
                    updateChatDisplay();
                }
                break;
            case PRIVATE:
                privateMessages.add(chatMessage);
                if (currentChatType.equals("privatechat")) {
                    updateChatDisplay();
                }
                break;
            case ROOM:
                if (currentChatType.equals("roomchat") && chatMessage.getRoomId().equals(currentRoomId)) {
                    updateChatDisplay();
                }
                break;
        }
    }

    public void handleNotification(Notification notification) {
        showNotification(notification.getMessage());
    }

    private void showNotification(String message) {
        // Simple notification display
        System.out.println("Notification: " + message);
        // In a real implementation, this would show a popup or toast notification
    }

    private void updateChatDisplay() {
        StringBuilder sb = new StringBuilder();
        List<ChatMessage> messagesToShow = new ArrayList<>();
        
        switch (currentChatType) {
            case "publicchat":
                messagesToShow = publicMessages;
                break;
            case "privatechat":
                messagesToShow = privateMessages;
                break;
            case "roomchat":
                if (currentRoomId != null) {
                    for (ChatRoom room : availableRooms) {
                        if (room.getRoomId().equals(currentRoomId)) {
                            messagesToShow = room.getMessageHistory();
                            break;
                        }
                    }
                }
                break;
        }
        
        for (ChatMessage msg : messagesToShow) {
            sb.append(msg.toString()).append("\n");
        }
        
        chatArea.setText(sb.toString());
    }

    private void updateRoomSelect() {
        List<String> roomNames = new ArrayList<>();
        for (ChatRoom room : availableRooms) {
            roomNames.add(room.getRoomName() + " (" + room.getParticipantCount() + ")");
        }
        
        if (roomNames.isEmpty()) {
            roomNames.add("No rooms available");
        }
        
        roomSelect.setItems(roomNames.toArray(new String[0]));
    }

    public void updateOnlineUsers(List<String> users) {
        this.onlineUsers = users;
        userSelect.setItems(users.toArray(new String[0]));
    }

    public void updateAvailableRooms(List<ChatRoom> rooms) {
        this.availableRooms = rooms;
        updateRoomSelect();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
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
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        font.dispose();
    }
}
