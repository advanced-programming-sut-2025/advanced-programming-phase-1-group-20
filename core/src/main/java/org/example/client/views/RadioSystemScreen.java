package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.common.models.Player.Player;
import org.example.client.network.NetworkClient;
import org.example.common.models.Message;
import org.example.common.models.entities.User;
import org.example.client.radio.RadioSharedStore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RadioSystemScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private Screen previousScreen;
    private Player player;

    // UI Components
    private Table mainTable;
    private Table currentTrackTable;
    private Table playlistTable;
    private Table controlsTable;
    private Table uploadTable;
    private Table connectionTable;

    // Current track info
    private Label currentTrackLabel;
    private Label currentTimeLabel;
    private Label totalTimeLabel;
    private ProgressBar progressBar;

    // Playlist
    private ScrollPane playlistScrollPane;
    private Table playlistItemsTable;
    private List<AudioTrack> playlist;
    private int currentTrackIndex = -1;

    // Controls
    private TextButton playButton;
    private TextButton pauseButton;
    private TextButton stopButton;
    private TextButton previousButton;
    private TextButton nextButton;
    private Slider volumeSlider;

    // Upload section
    private TextButton uploadButton;
    private TextButton browseButton;
    private Label uploadStatusLabel;
    private TextField selectedFileField;

    // Connection section
    private TextField playerNameField;
    private TextButton connectButton;
    private TextButton disconnectButton;
    private Label connectionStatusLabel;
    private List<String> onlinePlayers;

    // Audio playback
    private Music currentMusic;
    private boolean isPlaying = false;
    private float currentVolume = 0.5f;

    // Connected player info
    private String connectedPlayerName = null;
    private Music connectedPlayerMusic = null;
    private boolean isConnectedToPlayer = false;

    // Network and file management
    private NetworkClient networkClient;
    private String uploadsDirectory;
    private String selectedFilePath = null;

    public RadioSystemScreen(Skin skin, Screen previousScreen, Player player) {
        this.skin = skin;
        this.previousScreen = previousScreen;
        this.player = player;
        this.playlist = new ArrayList<>();
        this.onlinePlayers = new ArrayList<>();
        this.networkClient = NetworkClient.getInstance();
        // Register as radio listener
        if (this.networkClient != null && this.networkClient.getMessageHandler() != null) {
            this.networkClient.getMessageHandler().setRadioListener(new org.example.client.network.ClientMessageHandler.RadioMessageListener() {
                @Override
                public void onRadioTrackUpdate(String trackName, String trackPath, String fromPlayer) {
                    System.out.println("**CLIENT RADIO RECV** TRACK_UPDATE from=" + fromPlayer + " name=" + trackName + " path=" + trackPath);
                    handleIncomingTrack(trackName, trackPath, fromPlayer);
                }

                @Override
                public void onRadioTrackUpload(String trackName, String trackPath, String fromPlayer) {
                    System.out.println("**CLIENT RADIO RECV** TRACK_UPLOAD from=" + fromPlayer + " name=" + trackName + " path=" + trackPath);
                    // Add to playlist but do not auto play
                    addTrackToPlaylist(trackName + " (from " + fromPlayer + ")", trackPath);
                    updatePlaylistDisplay();
                }

                @Override
                public void onRadioConnectRequest(String requestingPlayer, String targetPlayer) {
                    System.out.println("**CLIENT RADIO RECV** CONNECT_REQUEST from=" + requestingPlayer + " to=" + targetPlayer);
                    // Auto-accept if target is current player
                    if (player != null && player.getUser() != null && targetPlayer != null && targetPlayer.equals(player.getUser().getUsername())) {
                        Message resp = new Message();
                        resp.setType(Message.Type.RADIO_CONNECT_RESPONSE);
                        resp.putInBody("targetPlayer", requestingPlayer); // send back to requester
                        resp.putInBody("accepted", true);
                        networkClient.sendMessage(resp);
                        System.out.println("**CLIENT RADIO SEND** CONNECT_RESPONSE accepted=true to=" + requestingPlayer);
                    }
                }

                @Override
                public void onRadioConnectResponse(String respondingPlayer, String targetPlayer, boolean accepted) {
                    System.out.println("**CLIENT RADIO RECV** CONNECT_RESPONSE from=" + respondingPlayer + " to=" + targetPlayer + " accepted=" + accepted);
                    handleConnectionResponse(respondingPlayer, accepted);
                }

                @Override
                public void onRadioDisconnect(String disconnectingPlayer, String targetPlayer) {
                    System.out.println("**CLIENT RADIO RECV** DISCONNECT from=" + disconnectingPlayer + " to=" + targetPlayer);
                    if (connectedPlayerName != null && connectedPlayerName.equals(disconnectingPlayer)) {
                        isConnectedToPlayer = false;
                        connectedPlayerName = null;
                        connectionStatusLabel.setText("Disconnected by peer");
                        connectionStatusLabel.setColor(Color.LIGHT_GRAY);
                        if (connectedPlayerMusic != null) {
                            connectedPlayerMusic.stop();
                            connectedPlayerMusic.dispose();
                            connectedPlayerMusic = null;
                        }
                    }
                }
            });
            System.out.println("**CLIENT RADIO** listener registered for user=" + (player != null && player.getUser() != null ? player.getUser().getUsername() : "null"));
        }

        // Create uploads directory
        setupUploadsDirectory();

        initializeUI();
        loadDefaultTracks();
        loadUploadedTracks();
        requestOnlinePlayers();
    }

    private void setupUploadsDirectory() {
        // Create uploads directory in user's home directory
        String userHome = System.getProperty("user.home");
        uploadsDirectory = userHome + "/.stardew_radio_uploads/";

        File uploadsDir = new File(uploadsDirectory);
        if (!uploadsDir.exists()) {
            uploadsDir.mkdirs();
        }

        System.out.println("Uploads directory: " + uploadsDirectory);
    }

    private void initializeUI() {
        stage = new Stage(new ScreenViewport());

        // Create main table
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20);

        // Create sections
        createCurrentTrackSection();
        createControlsSection();
        createPlaylistSection();
        createUploadSection();
        createConnectionSection();

        // Layout main table
        layoutMainTable();

        stage.addActor(mainTable);
        setupEventListeners();
    }

    private void createCurrentTrackSection() {
        currentTrackTable = new Table();
        currentTrackTable.pad(10);
        currentTrackTable.setBackground(skin.newDrawable("white", new Color(0.1f, 0.1f, 0.1f, 0.8f)));

        Label titleLabel = new Label("RADIO SYSTEM", skin);
        titleLabel.setColor(Color.GOLD);
        titleLabel.setFontScale(1.5f);
        currentTrackTable.add(titleLabel).padBottom(20).row();

        currentTrackLabel = new Label("No track selected", skin);
        currentTrackLabel.setColor(Color.WHITE);
        currentTrackTable.add(currentTrackLabel).padBottom(10).row();

        progressBar = new ProgressBar(0, 100, 1, false, skin);
        progressBar.setValue(0);
        currentTrackTable.add(progressBar).width(400).padBottom(5).row();

        Table timeTable = new Table();
        currentTimeLabel = new Label("0:00", skin);
        totalTimeLabel = new Label("0:00", skin);
        timeTable.add(currentTimeLabel).expandX().left();
        timeTable.add(totalTimeLabel).expandX().right();
        currentTrackTable.add(timeTable).width(400).row();

        // Back button
        TextButton backButton = new TextButton("Back to Game", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                Main.getGame().setScreen(previousScreen);
            }
        });
        currentTrackTable.add(backButton).width(150).height(40).padTop(20);
    }

    private void createControlsSection() {
        controlsTable = new Table();
        controlsTable.pad(10);
        controlsTable.setBackground(skin.newDrawable("white", new Color(0.15f, 0.15f, 0.15f, 0.8f)));

        Label controlsLabel = new Label("Controls", skin);
        controlsLabel.setColor(Color.CYAN);
        controlsTable.add(controlsLabel).padBottom(10).row();

        // Control buttons
        Table buttonTable = new Table();

        previousButton = new TextButton("⏮", skin);
        playButton = new TextButton("▶", skin);
        pauseButton = new TextButton("⏸", skin);
        stopButton = new TextButton("⏹", skin);
        nextButton = new TextButton("⏭", skin);

        buttonTable.add(previousButton).width(50).height(50).pad(5);
        buttonTable.add(playButton).width(50).height(50).pad(5);
        buttonTable.add(pauseButton).width(50).height(50).pad(5);
        buttonTable.add(stopButton).width(50).height(50).pad(5);
        buttonTable.add(nextButton).width(50).height(50).pad(5);

        controlsTable.add(buttonTable).row();

        // Volume control
        Label volumeLabel = new Label("Volume", skin);
        controlsTable.add(volumeLabel).padTop(10).row();

        volumeSlider = new Slider(0, 1, 0.1f, false, skin);
        volumeSlider.setValue(currentVolume);
        controlsTable.add(volumeSlider).width(200).padTop(5);
    }

    private void createPlaylistSection() {
        playlistTable = new Table();
        playlistTable.pad(10);
        playlistTable.setBackground(skin.newDrawable("white", new Color(0.1f, 0.1f, 0.1f, 0.8f)));

        Label playlistLabel = new Label("Playlist", skin);
        playlistLabel.setColor(Color.GREEN);
        playlistTable.add(playlistLabel).padBottom(10).row();

        playlistItemsTable = new Table();
        playlistScrollPane = new ScrollPane(playlistItemsTable, skin);
        playlistScrollPane.setFadeScrollBars(false);
        playlistScrollPane.setScrollBarPositions(false, true);

        playlistTable.add(playlistScrollPane).width(300).height(200);
    }

    private void createUploadSection() {
        uploadTable = new Table();
        uploadTable.pad(10);
        uploadTable.setBackground(skin.newDrawable("white", new Color(0.15f, 0.15f, 0.15f, 0.8f)));

        Label uploadLabel = new Label("Upload Audio", skin);
        uploadLabel.setColor(Color.ORANGE);
        uploadTable.add(uploadLabel).padBottom(10).row();

        browseButton = new TextButton("Browse Files", skin);
        uploadButton = new TextButton("Upload", skin);
        uploadStatusLabel = new Label("", skin);
        uploadStatusLabel.setColor(Color.YELLOW);

        selectedFileField = new TextField("", skin);
        selectedFileField.setMessageText("No file selected");
        selectedFileField.setDisabled(true);

        // Add a simple file path input option
        TextButton manualPathButton = new TextButton("Enter Path", skin);
        manualPathButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showManualPathDialog();
            }
        });

        Table uploadButtonTable = new Table();
        uploadButtonTable.add(browseButton).width(100).height(40).pad(5);
        uploadButtonTable.add(manualPathButton).width(100).height(40).pad(5);
        uploadButtonTable.add(uploadButton).width(100).height(40).pad(5);

        uploadTable.add(selectedFileField).width(250).height(30).padBottom(5).row();
        uploadTable.add(uploadButtonTable).row();
        uploadTable.add(uploadStatusLabel).padTop(5);
    }

    private void showManualPathDialog() {
        Dialog pathDialog = new Dialog("Enter File Path", skin);
        pathDialog.setModal(true);

        Table dialogTable = new Table();
        dialogTable.pad(20);

        Label instructionLabel = new Label("Enter the full path to your audio file:", skin);
        instructionLabel.setColor(Color.WHITE);
        dialogTable.add(instructionLabel).padBottom(10).row();

        TextField pathField = new TextField("", skin);
        pathField.setMessageText("e.g., /Users/username/Music/song.ogg");
        pathField.setWidth(400);
        dialogTable.add(pathField).padBottom(10).row();

        Label infoLabel = new Label("Supported formats: OGG, MP3, WAV", skin);
        infoLabel.setColor(Color.LIGHT_GRAY);
        infoLabel.setFontScale(0.8f);
        dialogTable.add(infoLabel).padBottom(20).row();

        Table buttonTable = new Table();
        TextButton okButton = new TextButton("OK", skin);
        TextButton cancelButton = new TextButton("Cancel", skin);

        okButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String path = pathField.getText().trim();
                if (!path.isEmpty()) {
                    File file = new File(path);
                    if (file.exists() && isValidAudioFile(file)) {
                        selectedFilePath = file.getAbsolutePath();
                        selectedFileField.setText(file.getName());
                        uploadStatusLabel.setText("File selected: " + file.getName());
                        System.out.println("**CLIENT RADIO** manual path selected path=" + selectedFilePath + " exists=" + file.exists());
                        pathDialog.hide();
                        // Immediately upload after selection
                        uploadAudioFile();
                    } else {
                        uploadStatusLabel.setText("Invalid file or path");
                        System.out.println("**CLIENT RADIO WARN** manual path invalid path=" + path);
                    }
                }
            }
        });

        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                pathDialog.hide();
            }
        });

        buttonTable.add(okButton).width(80).height(30).pad(5);
        buttonTable.add(cancelButton).width(80).height(30).pad(5);

        dialogTable.add(buttonTable);
        pathDialog.add(dialogTable);
        pathDialog.show(stage);
    }

    private boolean isValidAudioFile(File file) {
        if (!file.exists() || !file.isFile()) {
            return false;
        }

        String name = file.getName().toLowerCase();
        return name.endsWith(".ogg") || name.endsWith(".mp3") || name.endsWith(".wav");
    }

    private void createConnectionSection() {
        connectionTable = new Table();
        connectionTable.pad(10);
        connectionTable.setBackground(skin.newDrawable("white", new Color(0.1f, 0.1f, 0.1f, 0.8f)));

        Label connectionLabel = new Label("Connect to Player", skin);
        connectionLabel.setColor(Color.MAGENTA);
        connectionTable.add(connectionLabel).padBottom(10).row();

        playerNameField = new TextField("", skin);
        playerNameField.setMessageText("Enter player name");
        connectionTable.add(playerNameField).width(200).height(40).padBottom(10).row();

        Table connectionButtonTable = new Table();
        connectButton = new TextButton("Connect", skin);
        disconnectButton = new TextButton("Disconnect", skin);

        connectionButtonTable.add(connectButton).width(100).height(40).pad(5);
        connectionButtonTable.add(disconnectButton).width(100).height(40).pad(5);

        connectionTable.add(connectionButtonTable).row();

        connectionStatusLabel = new Label("Not connected", skin);
        connectionStatusLabel.setColor(Color.LIGHT_GRAY);
        connectionTable.add(connectionStatusLabel).padTop(5);
    }

    private void layoutMainTable() {
        // Top row: Current track and controls
        Table topRow = new Table();
        topRow.add(currentTrackTable).expandX().fillY();
        topRow.add(controlsTable).width(300).fillY();

        // Bottom row: Playlist, Upload, and Connection
        Table bottomRow = new Table();
        bottomRow.add(playlistTable).width(300).height(250);
        bottomRow.add(uploadTable).width(300).height(250);
        bottomRow.add(connectionTable).width(300).height(250);

        mainTable.add(topRow).expandX().fillY().row();
        mainTable.add(bottomRow).expandX().fillY();
    }

    private void setupEventListeners() {
        // Control buttons
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playCurrentTrack();
            }
        });

        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                pauseCurrentTrack();
            }
        });

        stopButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stopCurrentTrack();
            }
        });

        previousButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playPreviousTrack();
            }
        });

        nextButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playNextTrack();
            }
        });

        // Volume slider
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                currentVolume = volumeSlider.getValue();
                if (currentMusic != null) {
                    currentMusic.setVolume(currentVolume);
                }
            }
        });

        // Upload buttons
        browseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                browseForAudioFile();
            }
        });

        uploadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                uploadAudioFile();
            }
        });

        // Connection buttons
        connectButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                connectToPlayer();
            }
        });

        disconnectButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                disconnectFromPlayer();
            }
        });
    }

    private void loadDefaultTracks() {
        // Add some default tracks if available
        try {
            // Try to load default music files
            FileHandle musicDir = Gdx.files.internal("content/music/");
            if (musicDir.exists()) {
                FileHandle[] musicFiles = musicDir.list(".ogg");
                for (FileHandle file : musicFiles) {
                    addTrackToPlaylist(file.name(), file.path());
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading default tracks: " + e.getMessage());
        }
    }

    private void loadUploadedTracks() {
        try {
            // Load any previously seen radio tracks from shared store (including peer uploads)
            try {
                java.util.List<RadioSharedStore.RadioTrackEntry> shared = RadioSharedStore.getAllTracks();
                for (RadioSharedStore.RadioTrackEntry e : shared) {
                    addTrackToPlaylist(e.name, e.path);
                }
            } catch (Exception ignore) {}

            File uploadsDir = new File(uploadsDirectory);
            if (uploadsDir.exists()) {
                File[] files = uploadsDir.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".ogg") ||
                        name.toLowerCase().endsWith(".mp3") ||
                        name.toLowerCase().endsWith(".wav"));

                if (files != null) {
                    for (File file : files) {
                        addTrackToPlaylist(file.getName(), file.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading uploaded tracks: " + e.getMessage());
        }

        // If no tracks loaded, add a placeholder
        if (playlist.isEmpty()) {
            addTrackToPlaylist("No tracks available", "");
        }

        updatePlaylistDisplay();
    }

    private void requestOnlinePlayers() {
        try {
            Message message = new Message();
            message.setType(Message.Type.REQUEST_PLAYERS_LIST);
            networkClient.sendMessage(message);
        } catch (Exception e) {
            System.err.println("Failed to request online players: " + e.getMessage());
        }
    }

    private void addTrackToPlaylist(String name, String path) {
        AudioTrack track = new AudioTrack(name, path);
        playlist.add(track);
    }

    private void updatePlaylistDisplay() {
        playlistItemsTable.clear();

        for (int i = 0; i < playlist.size(); i++) {
            final int index = i;
            AudioTrack track = playlist.get(i);

            TextButton trackButton = new TextButton(track.name, skin);
            if (i == currentTrackIndex) {
                trackButton.setColor(Color.YELLOW);
            }

            trackButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    playTrack(index);
                }
            });

            playlistItemsTable.add(trackButton).width(280).height(30).pad(2).row();
        }
    }

    private void playCurrentTrack() {
        if (currentTrackIndex >= 0 && currentTrackIndex < playlist.size()) {
            playTrack(currentTrackIndex);
        }
    }

    private void playTrack(int index) {
        if (index < 0 || index >= playlist.size()) return;

        // Stop current track
        stopCurrentTrack();

        currentTrackIndex = index;
        AudioTrack track = playlist.get(index);

        if (track.path.isEmpty()) {
            currentTrackLabel.setText("No track available");
            return;
        }

        try {
            // Try to load as internal file first, then as external file
            FileHandle fileHandle;
            if (track.path.startsWith("content/")) {
                fileHandle = Gdx.files.internal(track.path);
            } else {
                fileHandle = Gdx.files.absolute(track.path);
            }

            currentMusic = Gdx.audio.newMusic(fileHandle);
            currentMusic.setVolume(currentVolume);
            currentMusic.setLooping(true);
            currentMusic.play();
            isPlaying = true;

            currentTrackLabel.setText("Now playing: " + track.name);
            updatePlaylistDisplay();

            // Always broadcast so connected peers (and others) can receive updates. The client will filter.
            broadcastTrackToConnectedPlayers(track);
        } catch (Exception e) {
            System.err.println("Error playing track: " + e.getMessage());
            currentTrackLabel.setText("Error playing: " + track.name);
        }
    }

    private void broadcastTrackToConnectedPlayers(AudioTrack track) {
        try {
            Message message = new Message();
            message.setType(Message.Type.RADIO_TRACK_UPDATE);
            message.putInBody("trackName", track.name);
            message.putInBody("trackPath", track.path);
            String uname = (player != null && player.getUser() != null) ? player.getUser().getUsername() :
                (networkClient != null && networkClient.getAuthenticatedUser() != null ? networkClient.getAuthenticatedUser().getUsername() : null);
            message.putInBody("playerName", uname);
            System.out.println("**CLIENT RADIO SEND** TRACK_UPDATE name=" + track.name + " path=" + track.path + " from=" + uname);
            networkClient.sendMessage(message);
        } catch (Exception e) {
            System.err.println("**CLIENT RADIO ERR** Failed to broadcast track: " + e.getMessage());
        }
    }

    private void pauseCurrentTrack() {
        if (currentMusic != null && isPlaying) {
            currentMusic.pause();
            isPlaying = false;
        }
    }

    private void stopCurrentTrack() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
            currentMusic = null;
            isPlaying = false;
        }
    }

    private void playPreviousTrack() {
        if (playlist.isEmpty()) return;

        int newIndex = currentTrackIndex - 1;
        if (newIndex < 0) newIndex = playlist.size() - 1;
        playTrack(newIndex);
    }

    private void playNextTrack() {
        if (playlist.isEmpty()) return;

        int newIndex = currentTrackIndex + 1;
        if (newIndex >= playlist.size()) newIndex = 0;
        playTrack(newIndex);
    }

    private void browseForAudioFile() {
        // Use the new FileSelectorDialog
        String[] allowedExtensions = {".ogg", ".mp3", ".wav"};

        FileSelectorDialog fileSelector = new FileSelectorDialog(
            "Select Audio File",
            skin,
            stage,
            allowedExtensions,
            new FileSelectorDialog.FileSelectorCallback() {
                @Override
                public void onFileSelected(com.badlogic.gdx.files.FileHandle file) {
                    selectedFilePath = file.path();
                    selectedFileField.setText(file.name());
                    uploadStatusLabel.setText("File selected: " + file.name());
                    System.out.println("**CLIENT RADIO** file dialog selected path=" + selectedFilePath + " exists=" + new java.io.File(selectedFilePath).exists());
                    // Immediately upload after selection
                    uploadAudioFile();
                }

                @Override
                public void onCancelled() {
                    uploadStatusLabel.setText("File selection cancelled");
                }
            }
        );

        fileSelector.show();
    }



    private void uploadAudioFile() {
        if (selectedFilePath == null || selectedFilePath.isEmpty()) {
            uploadStatusLabel.setText("Please select a file first");
            return;
        }

        try {
            File sourceFile = new File(selectedFilePath);
            if (!sourceFile.exists()) {
                uploadStatusLabel.setText("Selected file does not exist");
                System.out.println("**CLIENT RADIO ERR** upload source missing path=" + selectedFilePath);
                return;
            }

            // Generate unique filename
            String fileName = sourceFile.getName();
            String extension = "";
            int lastDot = fileName.lastIndexOf('.');
            if (lastDot > 0) {
                extension = fileName.substring(lastDot);
                fileName = fileName.substring(0, lastDot);
            }

            String uniqueFileName = fileName + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            String destinationPath = uploadsDirectory + uniqueFileName;

            // Copy file to uploads directory
            Path source = Paths.get(selectedFilePath);
            Path destination = Paths.get(destinationPath);
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("**CLIENT RADIO** copied to uploads src=" + source + " dst=" + destination + ", size=" + sourceFile.length());

            // Add to playlist
            addTrackToPlaylist(fileName, destinationPath);
            updatePlaylistDisplay();
            System.out.println("**CLIENT RADIO** playlist added name=" + fileName + " path=" + destinationPath);

            // Broadcast to other players
            broadcastUploadedTrack(fileName, destinationPath);

            uploadStatusLabel.setText("Upload successful: " + fileName);
            selectedFilePath = null;
            selectedFileField.setText("");

        } catch (Exception e) {
            uploadStatusLabel.setText("Upload failed: " + e.getMessage());
            System.err.println("**CLIENT RADIO ERR** Upload error: " + e.getMessage());
        }
    }

    private void broadcastUploadedTrack(String trackName, String trackPath) {
        try {
            Message message = new Message();
            message.setType(Message.Type.RADIO_TRACK_UPLOAD);
            message.putInBody("trackName", trackName);
            message.putInBody("trackPath", trackPath);
            String uname = (player != null && player.getUser() != null) ? player.getUser().getUsername() :
                (networkClient != null && networkClient.getAuthenticatedUser() != null ? networkClient.getAuthenticatedUser().getUsername() : null);
            message.putInBody("playerName", uname);
            System.out.println("**CLIENT RADIO SEND** TRACK_UPLOAD name=" + trackName + " path=" + trackPath + " from=" + uname);
            networkClient.sendMessage(message);
        } catch (Exception e) {
            System.err.println("**CLIENT RADIO ERR** Failed to broadcast uploaded track: " + e.getMessage());
        }
    }

    private void connectToPlayer() {
        String playerName = playerNameField.getText().trim();
        if (playerName.isEmpty()) {
            connectionStatusLabel.setText("Please enter a player name");
            return;
        }

        try {
            Message message = new Message();
            message.setType(Message.Type.RADIO_CONNECT_REQUEST);
            message.putInBody("targetPlayer", playerName);
            message.putInBody("requestingPlayer", player.getUser().getUsername());
            System.out.println("**CLIENT RADIO SEND** CONNECT_REQUEST from=" + (player != null && player.getUser()!=null ? player.getUser().getUsername() : "null") + " -> to=" + playerName);
            networkClient.sendMessage(message);

            connectedPlayerName = playerName;
            connectionStatusLabel.setText("Connecting to: " + playerName);
            connectionStatusLabel.setColor(Color.YELLOW);

        } catch (Exception e) {
            connectionStatusLabel.setText("Connection failed: " + e.getMessage());
        }
    }

    private void disconnectFromPlayer() {
        if (connectedPlayerName != null) {
            try {
                Message message = new Message();
                message.setType(Message.Type.RADIO_DISCONNECT);
                message.putInBody("targetPlayer", connectedPlayerName);
                message.putInBody("disconnectingPlayer", player.getUser().getUsername());
                System.out.println("**CLIENT RADIO SEND** DISCONNECT from=" + (player != null && player.getUser()!=null ? player.getUser().getUsername() : "null") + " -> to=" + connectedPlayerName);
                networkClient.sendMessage(message);
            } catch (Exception e) {
                System.err.println("**CLIENT RADIO ERR** Failed to send disconnect message: " + e.getMessage());
            }

            connectedPlayerName = null;
            isConnectedToPlayer = false;
            connectionStatusLabel.setText("Disconnected");
            connectionStatusLabel.setColor(Color.LIGHT_GRAY);

            // Stop connected player's music if playing
            if (connectedPlayerMusic != null) {
                connectedPlayerMusic.stop();
                connectedPlayerMusic.dispose();
                connectedPlayerMusic = null;
            }
        }
    }

    public void handleIncomingTrack(String trackName, String trackPath, String fromPlayer) {
        System.out.println("**CLIENT RADIO** handleIncomingTrack from=" + fromPlayer + " name=" + trackName + " path=" + trackPath +
            " connectedTo=" + isConnectedToPlayer + " peer=" + connectedPlayerName);
        // Add the track to our playlist
        addTrackToPlaylist(trackName + " (from " + fromPlayer + ")", trackPath);
        updatePlaylistDisplay();

        // If we're connected to this player, play their track
        if (isConnectedToPlayer && connectedPlayerName.equals(fromPlayer)) {
            // Find the track in our playlist and play it
            for (int i = 0; i < playlist.size(); i++) {
                AudioTrack track = playlist.get(i);
                if (track.path.equals(trackPath)) {
                    System.out.println("**CLIENT RADIO** auto-playing peer track index=" + i);
                    playTrack(i);
                    break;
                }
            }
        }
    }

    public void handleConnectionResponse(String fromPlayer, boolean accepted) {
        if (accepted) {
            isConnectedToPlayer = true;
            connectionStatusLabel.setText("Connected to: " + fromPlayer);
            connectionStatusLabel.setColor(Color.GREEN);
        } else {
            connectionStatusLabel.setText("Connection rejected by: " + fromPlayer);
            connectionStatusLabel.setColor(Color.RED);
            connectedPlayerName = null;
        }
    }

    @Override
    public void render(float delta) {
        // Update progress bar and time labels
        if (currentMusic != null && isPlaying) {
            // Note: libGDX Music doesn't provide duration, so we'll show current position only
            int currentSeconds = (int) currentMusic.getPosition();
            currentTimeLabel.setText(formatTime(currentSeconds));
            totalTimeLabel.setText("--:--"); // Duration not available

            // Progress bar will show a simple animation instead
            float progress = (currentSeconds % 60) / 60.0f * 100;
            progressBar.setValue(progress);
        }

        // Clear screen
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1);

        // Update and draw stage
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%d:%02d", minutes, remainingSeconds);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // Pause music when screen is paused
        if (currentMusic != null && isPlaying) {
            currentMusic.pause();
        }
    }

    @Override
    public void resume() {
        // Resume music when screen is resumed
        if (currentMusic != null && !isPlaying) {
            currentMusic.play();
            isPlaying = true;
        }
    }

    @Override
    public void hide() {
        // Pause music when screen is hidden
        if (currentMusic != null && isPlaying) {
            currentMusic.pause();
        }
    }

    @Override
    public void dispose() {
        // Clean up audio resources
        if (currentMusic != null) {
            currentMusic.dispose();
        }
        if (connectedPlayerMusic != null) {
            connectedPlayerMusic.dispose();
        }
        if (stage != null) {
            stage.dispose();
        }
    }

    // Inner class to represent audio tracks
    private static class AudioTrack {
        String name;
        String path;

        AudioTrack(String name, String path) {
            this.name = name;
            this.path = path;
        }
    }
}
