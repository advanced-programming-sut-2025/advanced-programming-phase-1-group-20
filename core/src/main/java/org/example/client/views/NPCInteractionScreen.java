package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.client.controllers.NPCSpriteController;
import org.example.common.models.App;
import org.example.common.models.entities.NPC;
import org.example.common.models.entities.NPCFriendship;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Date;

public class NPCInteractionScreen implements Screen {
    private Stage stage;
    private NPC npc;
    private Player player;
    private NPCSpriteController npcSpriteController;
    private Skin skin;
    private Screen previousScreen;
    private BitmapFont customFont;

    // UI Components
    private Image npcPortraitImage;
    private Label npcNameLabel;
    private Label friendshipLabel;
    private Label descriptionLabel;

    public NPCInteractionScreen(NPC npc, Player player, NPCSpriteController npcSpriteController, Skin skin, Screen previousScreen) {
        this.npc = npc;
        this.player = player;
        this.npcSpriteController = npcSpriteController;
        this.skin = skin;
        this.previousScreen = previousScreen;

        // Load custom font
        try {
            customFont = new BitmapFont(Gdx.files.internal("content/fonts/new.fnt"));
        } catch (Exception e) {
            System.err.println("Failed to load custom font: " + e.getMessage());
            customFont = skin.getFont("default-font");
        }

        // Initialize stage
        stage = new Stage(new ScreenViewport());
        createUI();
    }

    private void createUI() {
        // Main container
        Table mainContainer = new Table();
        mainContainer.setFillParent(true);
        mainContainer.setBackground(skin.newDrawable("white", new Color(0.1f, 0.1f, 0.1f, 0.95f)));
        mainContainer.pad(20);

        // Header section with NPC info
        Table headerSection = createHeaderSection();
        mainContainer.add(headerSection).expandX().fillX().row();

        // Separator
        mainContainer.add().expandX().fillX().height(3).pad(15, 0, 15, 0).row();

        // Interaction buttons section
        Table interactionSection = createInteractionSection();
        mainContainer.add(interactionSection).expandX().fillX().row();

        // Close button
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                close();
            }
        });
        mainContainer.add(closeButton).width(120).height(40).padTop(20).row();

        stage.addActor(mainContainer);
        Gdx.input.setInputProcessor(stage);
    }

    private Table createHeaderSection() {
        Table headerTable = new Table();
        headerTable.setBackground(skin.newDrawable("white", new Color(0.2f, 0.2f, 0.2f, 0.8f)));

        // NPC Portrait
        npcPortraitImage = createNPCPortrait();
        headerTable.add(npcPortraitImage).size(120, 120).pad(10).left();

        // NPC Info
        Table infoTable = new Table();

        // NPC Name
        Label.LabelStyle nameStyle = new Label.LabelStyle();
        nameStyle.font = customFont;
        nameStyle.fontColor = Color.WHITE;
        nameStyle.font.getData().setScale(1.2f);
        
        npcNameLabel = new Label(npc.getName(), nameStyle);
        infoTable.add(npcNameLabel).left().pad(5).row();

        // Friendship Level
        NPCFriendship friendship = npc.getFriendship(player);
        int friendshipLevel = friendship.getLevel();
        int friendshipPoints = friendship.getPoints();
        
        Label.LabelStyle friendshipStyle = new Label.LabelStyle();
        friendshipStyle.font = customFont;
        friendshipStyle.fontColor = Color.YELLOW;
        friendshipStyle.font.getData().setScale(0.9f);
        
        friendshipLabel = new Label("Friendship: Level " + friendshipLevel + " (" + friendshipPoints + " points)", friendshipStyle);
        infoTable.add(friendshipLabel).left().pad(5).row();

        // NPC Description
        Label.LabelStyle descStyle = new Label.LabelStyle();
        descStyle.font = customFont;
        descStyle.fontColor = Color.LIGHT_GRAY;
        descStyle.font.getData().setScale(0.7f);
        
        String description = npc.getCharacter() + " - " + npc.getJobs();
        descriptionLabel = new Label(description, descStyle);
        infoTable.add(descriptionLabel).left().pad(5).row();

        headerTable.add(infoTable).expandX().fillX().pad(10);
        
        return headerTable;
    }

    private Image createNPCPortrait() {
        try {
            // Use NPC sprite controller to get the face frame
            if (npcSpriteController != null) {
                com.badlogic.gdx.graphics.g2d.TextureRegion faceFrame = npcSpriteController.getCurrentFrame(npc, 0f);
                if (faceFrame != null) {
                    return new Image(faceFrame);
                }
            }
            
            // Fallback to a colored rectangle with NPC name
            System.err.println("Failed to load NPC face for " + npc.getName());
            
            com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(120, 120, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            pixmap.setColor(0.3f, 0.3f, 0.3f, 1f);
            pixmap.fill();
            
            Texture fallbackTexture = new Texture(pixmap);
            pixmap.dispose();
            
            return new Image(fallbackTexture);
        } catch (Exception e) {
            // Fallback to a colored rectangle with NPC name
            System.err.println("Failed to load NPC face for " + npc.getName() + ": " + e.getMessage());
            
            com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(120, 120, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            pixmap.setColor(0.3f, 0.3f, 0.3f, 1f);
            pixmap.fill();
            
            Texture fallbackTexture = new Texture(pixmap);
            pixmap.dispose();
            
            return new Image(fallbackTexture);
        }
    }

    private Table createInteractionSection() {
        Table interactionTable = new Table();
        interactionTable.setBackground(skin.newDrawable("white", new Color(0.2f, 0.2f, 0.2f, 0.8f)));

        // Title
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = customFont;
        titleStyle.fontColor = Color.WHITE;
        titleStyle.font.getData().setScale(1.0f);
        
        Label titleLabel = new Label("Interaction Options", titleStyle);
        interactionTable.add(titleLabel).pad(10).row();

        // Interaction buttons
        Table buttonsTable = new Table();

        // Talk to NPC button
        TextButton talkButton = new TextButton("Talk to " + npc.getName(), skin);
        talkButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                openDialogue();
            }
        });
        buttonsTable.add(talkButton).width(200).height(50).pad(5).row();

        // Give Gift button
        TextButton giftButton = new TextButton("Give Gift", skin);
        giftButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                openGiftMenu();
            }
        });
        buttonsTable.add(giftButton).width(200).height(50).pad(5).row();

        // View Quests button
        TextButton questsButton = new TextButton("View Quests", skin);
        questsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                openQuestsMenu();
            }
        });
        buttonsTable.add(questsButton).width(200).height(50).pad(5).row();

        interactionTable.add(buttonsTable).pad(10);
        
        return interactionTable;
    }

    private void openDialogue() {
        NPCDialogueScreen dialogueScreen = new NPCDialogueScreen(npc, player, npcSpriteController, skin, this);
        Main.getGame().setScreen(dialogueScreen);
    }

    private void openGiftMenu() {
        NPCGiftInventoryScreen giftInventoryScreen = new NPCGiftInventoryScreen(player, skin, this, npc);
        Main.getGame().setScreen(giftInventoryScreen);
    }

    private void openQuestsMenu() {
        NPCQuestsScreen questsScreen = new NPCQuestsScreen(skin, (GameView) previousScreen);
        Main.getGame().setScreen(questsScreen);
    }

    private void close() {
        Main.getGame().setScreen(previousScreen);
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
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
