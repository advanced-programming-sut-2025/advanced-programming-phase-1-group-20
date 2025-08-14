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
    private final Screen previousScreen;
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
    private String currentChatType = "public"; // "public", "private"
    private String currentRecipient = null;
    private String currentRoomId = null;
    private List<String> onlinePlayers = new ArrayList<>();
    private List<ChatRoom> availableRooms = new ArrayList<>();

    // Chat history
    private List<ChatMessage> publicChatHistory = new ArrayList<>();
    private List<ChatMessage> privateChatHistory = new ArrayList<>();
    private List<ChatMessage> roomChatHistory = new ArrayList<>();

    public ChatScreen(Main game, NetworkClient networkClient, Screen previousScreen) {
        this.game = game;
        this.networkClient = networkClient;
        this.messageHandler = networkClient.getMessageHandler();
        this.stage = new Stage(new ScreenViewport());
        this.skin = org.example.utils.AssetManager.getAssetManager().getSkin();
        this.previousScreen = previousScreen;
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
        // Prevent recursive ChangeListener triggers when changing checked state programmatically
        publicChatButton.setProgrammaticChangeEvents(false);
        privateChatButton.setProgrammaticChangeEvents(false);
        // rooms removed
        backButton = new TextButton("Back to Game", skin);
        refreshPlayersButton = new TextButton("Refresh Players", skin);

        buttonTable.add(publicChatButton).pad(5);
        buttonTable.add(privateChatButton).pad(5);
        // rooms removed
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
                // Ensure public mode doesn't open selection dialog accidentally
            }
        });

        privateChatButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Always open selection dialog explicitly when switching to private
                currentChatType = "private";
                privateChatButton.setChecked(true);
                publicChatButton.setChecked(false);
                showPlayerSelectionDialog();
            }
        });

        // rooms removed

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Return to the screen that opened the chat (typically GameView)
                if (previousScreen != null) {
                    game.setScreen(previousScreen);
                } else {
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
                if ("private".equals(currentChatType)) {
                    showPlayerSelectionDialog();
                }
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
            public void onChatMessage(String sender, String message, long timestamp, String type, String recipient) {
                Gdx.app.postRunnable(() -> {
                    ChatMessage.ChatType chatType;
                    if ("PRIVATE".equalsIgnoreCase(type)) {
                        chatType = ChatMessage.ChatType.PRIVATE;
                    } else if ("ROOM".equalsIgnoreCase(type)) {
                        chatType = ChatMessage.ChatType.ROOM;
                    } else {
                        chatType = ChatMessage.ChatType.PUBLIC;
                    }

                    ChatMessage chatMessage = new ChatMessage(sender, message, chatType);
                    chatMessage.setTimestamp(String.valueOf(timestamp));
                    if (recipient != null) {
                        chatMessage.setRecipient(recipient);
                    }

                    // Lightweight @mention detection for public chat
                    if (chatType == ChatMessage.ChatType.PUBLIC) {
                        if (isMentionedForMe(chatMessage) && !isFromMe(chatMessage)) {
                            showMentionNotification(sender, message);
                        }
                    }

                    // Persist to appropriate history
                    if (chatType == ChatMessage.ChatType.PRIVATE) {
                        privateChatHistory.add(chatMessage);
                    } else {
                        publicChatHistory.add(chatMessage);
                    }

                    // Only update visible chat stream when it matches the current tab and, for private, the selected peer
                    if (chatType == ChatMessage.ChatType.PUBLIC && "public".equals(currentChatType)) {
                        addMessageToUI(chatMessage);
                    } else if (chatType == ChatMessage.ChatType.PRIVATE && "private".equals(currentChatType)) {
                        if (matchesCurrentPrivateConversation(chatMessage)) {
                            addMessageToUI(chatMessage);
                        }
                    }
                });
            }
        });
    }

    private boolean matchesCurrentPrivateConversation(ChatMessage message) {
        if (currentRecipient == null) return false;
        String me = getMyUsername();
        if (me == null) return false;
        // Conversation between me and currentRecipient
        boolean iSentToPeer = me.equals(message.getSender()) && currentRecipient.equals(message.getRecipient());
        boolean peerSentToMe = currentRecipient.equals(message.getSender()) && me.equals(message.getRecipient());
        return iSentToPeer || peerSentToMe;
    }

    private String getMyUsername() {
        return networkClient != null && networkClient.getAuthenticatedUser() != null
            ? networkClient.getAuthenticatedUser().getUsername()
            : null;
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

    }

    private void joinChatRoom(String roomId) {
        // rooms removed
    }

    private void sendMessage() {
        String message = messageInput.getText().trim();
        if (message.isEmpty()) {
            return;
        }

        if ("public".equals(currentChatType)) {
            networkClient.sendPublicChatMessage(message);
        } else if ("private".equals(currentChatType) && currentRecipient != null) {
            networkClient.sendPrivateChatMessage(currentRecipient, message);
        }

        messageInput.setText("");
    }

    private void updateChatDisplay() {
        // Clear current chat display
        Table messagesTable = (Table) chatScrollPane.getActor();
        messagesTable.clear();

        List<ChatMessage> messagesToShow;
        if ("private".equals(currentChatType)) {
            // Filter only messages of the active conversation
            messagesToShow = new ArrayList<>();
            for (ChatMessage msg : privateChatHistory) {
                if (matchesCurrentPrivateConversation(msg)) {
                    messagesToShow.add(msg);
                }
            }
        } else {
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

        // Debug
        System.out.println("**[CHAT][UI][ADD] type=" + message.getType() + " text=\"" + displayText + "\" currentTab=" + currentChatType + "**");

        // Color code based on message type
        if (message.getType() == ChatMessage.ChatType.PRIVATE) {
            messageLabel.setColor(Color.PURPLE);
        } else {
            messageLabel.setColor(Color.BLACK);
        }

        // Highlight if this message mentions me in public chat
        if (message.getType() == ChatMessage.ChatType.PUBLIC && isMentionedForMe(message)) {
            messageLabel.setColor(Color.SCARLET);
            try {
                messageContainer.setBackground(skin.newDrawable("white", new Color(1f, 1f, 0.7f, 0.6f)));
            } catch (Exception ignored) {}
        }

        messageContainer.add(messageLabel).expandX().fillX();
        messagesTable.add(messageContainer).expandX().fillX().row();

        // Scroll to bottom
        chatScrollPane.scrollTo(0, 0, 0, 0);
    }

    private boolean isFromMe(ChatMessage message) {
        String me = getMyUsername();
        return me != null && me.equals(message.getSender());
    }

    private boolean isMentionedForMe(ChatMessage message) {
        String me = getMyUsername();
        if (me == null) return false;
        String content = message.getContent();
        if (content == null) return false;
        String needle = "@" + me;
        return content.contains(needle);
    }

    private void showMentionNotification(String sender, String content) {
        String me = getMyUsername();
        if (me == null) return;

        String shortContent = content.length() > 60 ? content.substring(0, 57) + "..." : content;
        Label notificationLabel = new Label("@" + me + " mentioned by " + sender + ": " + shortContent, skin);
        notificationLabel.setColor(Color.ORANGE);
        notificationLabel.setFontScale(1.1f);

        float x = Gdx.graphics.getWidth() / 2f - 220f;
        float y = Gdx.graphics.getHeight() - 80f;
        notificationLabel.setPosition(x, y);

        stage.addActor(notificationLabel);

        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
            @Override
            public void run() {
                Gdx.app.postRunnable(() -> {
                    if (notificationLabel.getStage() != null) {
                        notificationLabel.remove();
                    }
                });
            }
        }, 2.0f);
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
        setupMessageHandling();
        switchToPublicChat();
        // Request online players list when screen is shown
        requestOnlinePlayersList();
    }

    @Override
    public void render(float delta) {
        // Ensure incoming network messages are processed while this screen is active
        if (networkClient != null) {
            networkClient.update();
        }

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
