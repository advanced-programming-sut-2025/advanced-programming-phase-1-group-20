package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.client.network.NetworkClient;
import org.example.client.network.ClientMessageHandler;
import org.example.common.models.ChatMessage;
import org.example.common.models.ChatRoom;
import org.example.common.models.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatScreen implements Screen, ClientMessageHandler.OnlinePlayersListener {
    private final Main game;
    private final Stage stage;
    private final Skin skin;
    private final NetworkClient networkClient;
    private final ClientMessageHandler messageHandler;

    // UI Components
    private Table mainTable;
    private Table chatArea;
    private ScrollPane chatScrollPane;
    private TextField messageInput;
    private TextButton sendButton;
    private TextButton publicChatButton;
    private TextButton privateChatButton;
    private TextButton roomsButton;
    private TextButton backButton;
    private TextButton refreshPlayersButton;

    // Chat state
    private String currentChatType = "public"; // "public", "private", "room"
    private String currentRecipient = null;
    private String currentRoomId = null;
    private List<String> onlinePlayers = new ArrayList<>();
    private List<ChatRoom> availableRooms = new ArrayList<>();

    // Chat history
    private List<ChatMessage> publicChatHistory = new ArrayList<>();
    private List<ChatMessage> privateChatHistory = new ArrayList<>();
    private List<ChatMessage> roomChatHistory = new ArrayList<>();

    public ChatScreen(Main game, NetworkClient networkClient) {
        this.game = game;
        this.networkClient = networkClient;
        this.messageHandler = networkClient.getMessageHandler();
        this.stage = new Stage(new ScreenViewport());
        this.skin = org.example.utils.AssetManager.getAssetManager().getSkin();

        setupUI();
        setupMessageHandling();
        setupOnlinePlayersListener();
    }

    private void setupUI() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20);

        // Title
        Label titleLabel = new Label("Chat System", skin);
        titleLabel.setFontScale(2.0f);
        titleLabel.setColor(Color.BLACK);
        mainTable.add(titleLabel).expandX().fillX().row();

        // Chat type buttons
        Table buttonTable = new Table();
        buttonTable.pad(10);

        publicChatButton = new TextButton("Public Chat", skin);
        privateChatButton = new TextButton("Private Chat", skin);
        roomsButton = new TextButton("Chat Rooms", skin);
        backButton = new TextButton("Back to Game", skin);
        refreshPlayersButton = new TextButton("Refresh Players", skin);

        buttonTable.add(publicChatButton).pad(5);
        buttonTable.add(privateChatButton).pad(5);
        buttonTable.add(roomsButton).pad(5);
        buttonTable.add(backButton).pad(5);
        buttonTable.add(refreshPlayersButton).pad(5);

        mainTable.add(buttonTable).expandX().fillX().row();

        // Chat area
        chatArea = new Table();
        chatArea.pad(10);
        chatArea.setBackground(skin.newDrawable("white", Color.LIGHT_GRAY));

        // Chat messages area
        Table messagesTable = new Table();
        messagesTable.align(Align.topLeft);
        messagesTable.pad(10);

        chatScrollPane = new ScrollPane(messagesTable, skin);
        chatScrollPane.setFadeScrollBars(false);
        chatScrollPane.setScrollBarPositions(false, true);
        chatScrollPane.setScrollingDisabled(false, false);

        chatArea.add(chatScrollPane).expand().fill().row();

        // Message input area
        Table inputTable = new Table();
        inputTable.pad(5);

        messageInput = new TextField("", skin);
        messageInput.setMessageText("Type your message here...");
        messageInput.setMaxLength(200);

        sendButton = new TextButton("Send", skin);

        inputTable.add(messageInput).expandX().fillX().padRight(10);
        inputTable.add(sendButton);

        chatArea.add(inputTable).expandX().fillX();

        mainTable.add(chatArea).expand().fill().row();

        stage.addActor(mainTable);

        // Add listeners
        setupListeners();
    }

    private void setupListeners() {
        publicChatButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                switchToPublicChat();
            }
        });

        privateChatButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showPlayerSelectionDialog();
            }
        });

        roomsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showRoomSelectionDialog();
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Go back to the previous screen or main menu
                Screen currentScreen = game.getScreen();
                if (currentScreen instanceof GameView) {
                    game.setScreen(currentScreen);
                } else {
                    // Fallback to main menu
                    game.setScreen(new org.example.client.views.menu.MainMenuScreen(
                        new org.example.client.controllers.menu.MainMenuController(),
                        org.example.utils.AssetManager.getAssetManager().getSkin()
                    ));
                }
            }
        });

        refreshPlayersButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                requestOnlinePlayersList();
            }
        });

        sendButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sendMessage();
            }
        });

        messageInput.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Handle enter key press
                if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
                    sendMessage();
                }
            }
        });
    }

    private void setupMessageHandling() {
        messageHandler.setChatListener(new ClientMessageHandler.ChatMessageListener() {
            @Override
            public void onChatMessage(String sender, String message, long timestamp) {
                Gdx.app.postRunnable(() -> {
                    ChatMessage chatMessage = new ChatMessage(sender, message, ChatMessage.ChatType.PUBLIC);
                    chatMessage.setTimestamp(String.valueOf(timestamp));
                    addMessageToUI(chatMessage);
                });
            }
        });
    }

    private void setupOnlinePlayersListener() {
        messageHandler.setOnlinePlayersListener(this);
        // Request online players list
        requestOnlinePlayersList();
    }

    private void requestOnlinePlayersList() {
        Message requestMessage = new Message();
        requestMessage.setType(Message.Type.REQUEST_PLAYERS_LIST);
        networkClient.sendMessage(requestMessage);
        System.out.println("[CHAT] Requested online players list");
    }

    private void switchToPublicChat() {
        currentChatType = "public";
        currentRecipient = null;
        currentRoomId = null;
        updateChatDisplay();
        publicChatButton.setChecked(true);
        privateChatButton.setChecked(false);
        roomsButton.setChecked(false);
    }

    private void showPlayerSelectionDialog() {
        // Create a dialog to select a player for private chat
        Dialog dialog = new Dialog("Select Player", skin);
        dialog.setModal(true);

        Table contentTable = new Table();
        contentTable.pad(20);

        Label label = new Label("Select a player to start private chat:", skin);
        contentTable.add(label).expandX().fillX().row();

        // Add online players
        if (onlinePlayers.isEmpty()) {
            Label noPlayersLabel = new Label("No players online", skin);
            noPlayersLabel.setColor(Color.GRAY);
            contentTable.add(noPlayersLabel).expandX().fillX().row();
        } else {
            for (String player : onlinePlayers) {
                TextButton playerButton = new TextButton(player, skin);
                playerButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        startPrivateChat(player);
                        dialog.hide();
                    }
                });
                contentTable.add(playerButton).expandX().fillX().row();
            }
        }

        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide();
            }
        });
        contentTable.add(cancelButton).expandX().fillX().row();

        dialog.getContentTable().add(contentTable);
        dialog.show(stage);
    }

    private void showRoomSelectionDialog() {
        Dialog dialog = new Dialog("Chat Rooms", skin);
        dialog.setModal(true);

        Table contentTable = new Table();
        contentTable.pad(20);

        Label label = new Label("Available Chat Rooms:", skin);
        contentTable.add(label).expandX().fillX().row();

        // Add existing rooms
        if (availableRooms.isEmpty()) {
            Label noRoomsLabel = new Label("No chat rooms available", skin);
            noRoomsLabel.setColor(Color.GRAY);
            contentTable.add(noRoomsLabel).expandX().fillX().row();
        } else {
            for (ChatRoom room : availableRooms) {
                TextButton roomButton = new TextButton(room.getRoomName() + " (" + room.getParticipantCount() + ")", skin);
                roomButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        joinChatRoom(room.getRoomId());
                        dialog.hide();
                    }
                });
                contentTable.add(roomButton).expandX().fillX().row();
            }
        }

        // Create new room button
        TextButton createRoomButton = new TextButton("Create New Room", skin);
        createRoomButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showCreateRoomDialog();
                dialog.hide();
            }
        });
        contentTable.add(createRoomButton).expandX().fillX().row();

        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide();
            }
        });
        contentTable.add(cancelButton).expandX().fillX().row();

        dialog.getContentTable().add(contentTable);
        dialog.show(stage);
    }

    private void showCreateRoomDialog() {
        Dialog dialog = new Dialog("Create Chat Room", skin);
        dialog.setModal(true);

        Table contentTable = new Table();
        contentTable.pad(20);

        Label label = new Label("Enter room name:", skin);
        contentTable.add(label).expandX().fillX().row();

        TextField roomNameInput = new TextField("", skin);
        contentTable.add(roomNameInput).expandX().fillX().row();

        Table buttonTable = new Table();
        TextButton createButton = new TextButton("Create", skin);
        TextButton cancelButton = new TextButton("Cancel", skin);

        createButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String roomName = roomNameInput.getText().trim();
                if (!roomName.isEmpty()) {
                    networkClient.createChatRoom(roomName);
                    dialog.hide();
                }
            }
        });

        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dialog.hide();
            }
        });

        buttonTable.add(createButton).pad(5);
        buttonTable.add(cancelButton).pad(5);
        contentTable.add(buttonTable).expandX().fillX().row();

        dialog.getContentTable().add(contentTable);
        dialog.show(stage);
    }

    private void startPrivateChat(String recipient) {
        currentChatType = "private";
        currentRecipient = recipient;
        currentRoomId = null;
        updateChatDisplay();
        publicChatButton.setChecked(false);
        privateChatButton.setChecked(true);
        roomsButton.setChecked(false);
    }

    private void joinChatRoom(String roomId) {
        currentChatType = "room";
        currentRecipient = null;
        currentRoomId = roomId;
        networkClient.joinChatRoom(roomId);
        updateChatDisplay();
        publicChatButton.setChecked(false);
        privateChatButton.setChecked(false);
        roomsButton.setChecked(true);
    }

    private void sendMessage() {
        String message = messageInput.getText().trim();
        if (message.isEmpty()) {
            return;
        }

        switch (currentChatType) {
            case "public":
                networkClient.sendPublicChatMessage(message);
                break;
            case "private":
                if (currentRecipient != null) {
                    networkClient.sendPrivateChatMessage(currentRecipient, message);
                }
                break;
            case "room":
                if (currentRoomId != null) {
                    networkClient.sendRoomChatMessage(currentRoomId, message);
                }
                break;
        }

        messageInput.setText("");
    }

    private void updateChatDisplay() {
        // Clear current chat display
        Table messagesTable = (Table) chatScrollPane.getActor();
        messagesTable.clear();

        List<ChatMessage> messagesToShow;
        switch (currentChatType) {
            case "public":
                messagesToShow = publicChatHistory;
                break;
            case "private":
                messagesToShow = privateChatHistory;
                break;
            case "room":
                messagesToShow = roomChatHistory;
                break;
            default:
                messagesToShow = publicChatHistory;
        }

        // Add messages to UI
        for (ChatMessage message : messagesToShow) {
            addMessageToUI(message);
        }

        // Scroll to bottom
        chatScrollPane.scrollTo(0, 0, 0, 0);
    }

    private void addMessageToUI(ChatMessage message) {
        Table messagesTable = (Table) chatScrollPane.getActor();

        // Create message container
        Table messageContainer = new Table();
        messageContainer.pad(5);

        // Create message label with timestamp
        String displayText = "[" + message.getTimestamp() + "] " + message.getSender() + ": " + message.getContent();
        Label messageLabel = new Label(displayText, skin);
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.left);

        // Color code based on message type
        switch (message.getType()) {
            case PRIVATE:
                messageLabel.setColor(Color.PURPLE);
                break;
            case ROOM:
                messageLabel.setColor(Color.BLUE);
                break;
            default:
                messageLabel.setColor(Color.BLACK);
        }

        messageContainer.add(messageLabel).expandX().fillX();
        messagesTable.add(messageContainer).expandX().fillX().row();

        // Scroll to bottom
        chatScrollPane.scrollTo(0, 0, 0, 0);
    }

    public void setOnlinePlayers(List<String> players) {
        this.onlinePlayers = new ArrayList<>(players);
        System.out.println("[CHAT] Set online players: " + players.size() + " players - " + players);
    }

    public void setAvailableRooms(List<ChatRoom> rooms) {
        this.availableRooms = new ArrayList<>(rooms);
    }

    // OnlinePlayersListener implementation
    @Override
    public void onOnlinePlayersUpdate(List<Object> players) {
        Gdx.app.postRunnable(() -> {
            List<String> playerNames = new ArrayList<>();
            for (Object player : players) {
                if (player instanceof String) {
                    playerNames.add((String) player);
                } else if (player instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> playerMap = (Map<String, Object>) player;
                    String username = (String) playerMap.get("username");
                    String status = (String) playerMap.get("status");
                    
                    // Only add players who are online (not in game, unless they're in the same game)
                    if (username != null && !"IN_GAME".equals(status)) {
                        playerNames.add(username);
                    }
                }
            }
            setOnlinePlayers(playerNames);
            System.out.println("[CHAT] Updated online players list: " + playerNames.size() + " players");
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        switchToPublicChat();
        // Request online players list when screen is shown
        requestOnlinePlayersList();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
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
    }
}
