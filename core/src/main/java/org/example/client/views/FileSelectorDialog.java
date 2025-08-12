package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.io.File;
import java.util.Comparator;

public class FileSelectorDialog extends Dialog {
    private final Skin skin;
    private final Stage stage;
    private final String[] allowedExtensions;
    private final FileSelectorCallback callback;
    
    private Table contentTable;
    private Table fileTable;
    private ScrollPane scrollPane;
    private TextField pathField;
    private Label currentPathLabel;
    private FileHandle currentDirectory;
    private Array<FileHandle> currentFiles;
    private FileHandle selectedFile;

    public interface FileSelectorCallback {
        void onFileSelected(FileHandle file);
        void onCancelled();
    }

    public FileSelectorDialog(String title, Skin skin, Stage stage, String[] allowedExtensions, FileSelectorCallback callback) {
        super(title, skin);
        this.skin = skin;
        this.stage = stage;
        this.allowedExtensions = allowedExtensions;
        this.callback = callback;
        
        initializeDialog();
    }

    private void initializeDialog() {
        setSize(700, 500);
        setPosition(
            Gdx.graphics.getWidth() / 2f - getWidth() / 2f,
            Gdx.graphics.getHeight() / 2f - getHeight() / 2f
        );

        contentTable = new Table();
        contentTable.setFillParent(true);
        contentTable.pad(20);

        // Create UI components
        createPathSection();
        createFileListSection();
        createButtonSection();

        getContentTable().add(contentTable);
        
        // Initialize with user's home directory
        initializeWithHomeDirectory();
    }

    private void createPathSection() {
        // Current path label
        currentPathLabel = new Label("", skin);
        currentPathLabel.setColor(Color.WHITE);
        currentPathLabel.setFontScale(0.9f);
        contentTable.add(currentPathLabel).fillX().padBottom(10).row();

        // Path input field
        Table pathTable = new Table();
        Label pathLabel = new Label("Path:", skin);
        pathLabel.setColor(Color.LIGHT_GRAY);
        
        pathField = new TextField("", skin);
        pathField.setMessageText("Enter path or use navigation below");
        
        TextButton goButton = new TextButton("Go", skin);
        goButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                navigateToPath(pathField.getText());
            }
        });

        pathTable.add(pathLabel).width(50);
        pathTable.add(pathField).expandX().fillX().padLeft(10);
        pathTable.add(goButton).width(60).padLeft(10);
        
        contentTable.add(pathTable).fillX().padBottom(15).row();
    }

    private void createFileListSection() {
        // File list header
        Table headerTable = new Table();
        headerTable.setBackground(skin.newDrawable("white", new Color(0.2f, 0.2f, 0.3f, 0.8f)));
        
        Label nameHeader = new Label("Name", skin);
        nameHeader.setColor(Color.WHITE);
        nameHeader.setFontScale(0.9f);
        
        Label sizeHeader = new Label("Size", skin);
        sizeHeader.setColor(Color.WHITE);
        sizeHeader.setFontScale(0.9f);
        sizeHeader.setAlignment(Align.right);
        
        Label dateHeader = new Label("Modified", skin);
        dateHeader.setColor(Color.WHITE);
        dateHeader.setFontScale(0.9f);
        dateHeader.setAlignment(Align.right);
        
        headerTable.add(nameHeader).expandX().fillX().pad(10);
        headerTable.add(sizeHeader).width(80).pad(10);
        headerTable.add(dateHeader).width(120).pad(10);
        
        contentTable.add(headerTable).fillX().padBottom(5).row();

        // File list
        fileTable = new Table();
        scrollPane = new ScrollPane(fileTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(false, true);
        
        contentTable.add(scrollPane).expand().fill().padBottom(15).row();
    }

    private void createButtonSection() {
        Table buttonTable = new Table();
        
        // Navigation buttons
        TextButton homeButton = new TextButton("Home", skin);
        homeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                navigateToHome();
            }
        });
        
        TextButton upButton = new TextButton("Up", skin);
        upButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                navigateUp();
            }
        });
        
        TextButton refreshButton = new TextButton("Refresh", skin);
        refreshButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                refreshCurrentDirectory();
            }
        });

        // Action buttons
        TextButton selectButton = new TextButton("Select", skin);
        selectButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedFile != null) {
                    callback.onFileSelected(selectedFile);
                    hide();
                }
            }
        });
        
        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                callback.onCancelled();
                hide();
            }
        });

        // Layout buttons
        Table navTable = new Table();
        navTable.add(homeButton).width(80).height(30).pad(5);
        navTable.add(upButton).width(80).height(30).pad(5);
        navTable.add(refreshButton).width(80).height(30).pad(5);
        
        buttonTable.add(navTable).expandX().left();
        
        Table actionTable = new Table();
        actionTable.add(selectButton).width(100).height(35).pad(5);
        actionTable.add(cancelButton).width(100).height(35).pad(5);
        
        buttonTable.add(actionTable).expandX().right();
        
        contentTable.add(buttonTable).fillX();
    }

    private void initializeWithHomeDirectory() {
        String homePath = System.getProperty("user.home");
        FileHandle homeDir = Gdx.files.absolute(homePath);
        if (homeDir.exists() && homeDir.isDirectory()) {
            navigateToDirectory(homeDir);
        } else {
            // Fallback to current working directory
            navigateToDirectory(Gdx.files.absolute("."));
        }
    }

    private void navigateToHome() {
        String homePath = System.getProperty("user.home");
        FileHandle homeDir = Gdx.files.absolute(homePath);
        navigateToDirectory(homeDir);
    }

    private void navigateUp() {
        if (currentDirectory != null && currentDirectory.parent() != null) {
            navigateToDirectory(currentDirectory.parent());
        }
    }

    private void navigateToPath(String path) {
        if (path != null && !path.trim().isEmpty()) {
            FileHandle dir = Gdx.files.absolute(path.trim());
            if (dir.exists() && dir.isDirectory()) {
                navigateToDirectory(dir);
            } else {
                // Try to navigate to the parent directory of the file
                File file = new File(path.trim());
                if (file.exists()) {
                    FileHandle parent = Gdx.files.absolute(file.getParent());
                    if (parent.exists() && parent.isDirectory()) {
                        navigateToDirectory(parent);
                        // Select the file if it's in the allowed extensions
                        if (isAllowedFile(Gdx.files.absolute(path.trim()))) {
                            selectedFile = Gdx.files.absolute(path.trim());
                            updateFileSelection();
                        }
                    }
                }
            }
        }
    }

    private void navigateToDirectory(FileHandle directory) {
        currentDirectory = directory;
        currentPathLabel.setText("Current: " + directory.path());
        pathField.setText(directory.path());
        
        loadDirectoryContents();
        updateFileSelection();
    }

    private void refreshCurrentDirectory() {
        if (currentDirectory != null) {
            loadDirectoryContents();
        }
    }

    private void loadDirectoryContents() {
        fileTable.clear();
        currentFiles = new Array<>();
        
        if (currentDirectory == null || !currentDirectory.exists()) {
            return;
        }

        try {
            // Get all files and directories
            FileHandle[] children = currentDirectory.list();
            
            // Separate directories and files
            Array<FileHandle> directories = new Array<>();
            Array<FileHandle> files = new Array<>();
            
            for (FileHandle child : children) {
                if (child.isDirectory()) {
                    directories.add(child);
                } else if (isAllowedFile(child)) {
                    files.add(child);
                }
            }
            
            // Sort directories and files
            directories.sort(new FileHandleComparator());
            files.sort(new FileHandleComparator());
            
            // Add directories first
            for (FileHandle dir : directories) {
                addDirectoryRow(dir);
            }
            
            // Add files
            for (FileHandle file : files) {
                addFileRow(file);
            }
            
            currentFiles.addAll(directories);
            currentFiles.addAll(files);
            
        } catch (Exception e) {
            System.err.println("Error loading directory: " + e.getMessage());
            Label errorLabel = new Label("Error loading directory contents", skin);
            errorLabel.setColor(Color.RED);
            fileTable.add(errorLabel).pad(20);
        }
    }

    private void addDirectoryRow(FileHandle directory) {
        Table row = new Table();
        row.setBackground(skin.newDrawable("white", new Color(0.3f, 0.3f, 0.4f, 0.3f)));
        
        // Directory icon/name
        Label nameLabel = new Label("📁 " + directory.name(), skin);
        nameLabel.setColor(Color.CYAN);
        
        // Size (always 0 for directories)
        Label sizeLabel = new Label("--", skin);
        sizeLabel.setColor(Color.LIGHT_GRAY);
        sizeLabel.setAlignment(Align.right);
        
        // Date
        Label dateLabel = new Label("", skin);
        dateLabel.setColor(Color.LIGHT_GRAY);
        dateLabel.setAlignment(Align.right);
        
        row.add(nameLabel).expandX().fillX().pad(8);
        row.add(sizeLabel).width(80).pad(8);
        row.add(dateLabel).width(120).pad(8);
        
        row.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                navigateToDirectory(directory);
            }
        });
        
        fileTable.add(row).fillX().row();
    }

    private void addFileRow(FileHandle file) {
        Table row = new Table();
        
        // File icon/name
        Label nameLabel = new Label("🎵 " + file.name(), skin);
        nameLabel.setColor(Color.WHITE);
        
        // Size
        String sizeText = formatFileSize(file.length());
        Label sizeLabel = new Label(sizeText, skin);
        sizeLabel.setColor(Color.LIGHT_GRAY);
        sizeLabel.setAlignment(Align.right);
        
        // Date (simplified)
        Label dateLabel = new Label("", skin);
        dateLabel.setColor(Color.LIGHT_GRAY);
        dateLabel.setAlignment(Align.right);
        
        row.add(nameLabel).expandX().fillX().pad(8);
        row.add(sizeLabel).width(80).pad(8);
        row.add(dateLabel).width(120).pad(8);
        
        row.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                selectedFile = file;
                updateFileSelection();
            }
        });
        
        fileTable.add(row).fillX().row();
    }

    private void updateFileSelection() {
        // Update visual selection
        for (int i = 0; i < fileTable.getChildren().size; i++) {
            Actor child = fileTable.getChild(i);
            if (child instanceof Table) {
                Table row = (Table) child;
                if (i < currentFiles.size) {
                    FileHandle file = currentFiles.get(i);
                    if (file.equals(selectedFile)) {
                        row.setBackground(skin.newDrawable("white", new Color(0.2f, 0.5f, 0.8f, 0.5f)));
                    } else {
                        if (file.isDirectory()) {
                            row.setBackground(skin.newDrawable("white", new Color(0.3f, 0.3f, 0.4f, 0.3f)));
                        } else {
                            row.setBackground(skin.newDrawable("white", new Color(0, 0, 0, 0)));
                        }
                    }
                }
            }
        }
    }

    private boolean isAllowedFile(FileHandle file) {
        if (allowedExtensions == null || allowedExtensions.length == 0) {
            return true;
        }
        
        String fileName = file.name().toLowerCase();
        for (String ext : allowedExtensions) {
            if (fileName.endsWith(ext.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    private static class FileHandleComparator implements Comparator<FileHandle> {
        @Override
        public int compare(FileHandle f1, FileHandle f2) {
            // Directories first, then files
            if (f1.isDirectory() && !f2.isDirectory()) return -1;
            if (!f1.isDirectory() && f2.isDirectory()) return 1;
            
            // Then alphabetically
            return f1.name().compareToIgnoreCase(f2.name());
        }
    }

    public void show() {
        show(stage);
    }
}
