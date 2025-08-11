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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.client.views.GameView;

public class QuestMenuScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private GameView gameView;
    private BitmapFont customFont;

    public QuestMenuScreen(Skin skin, GameView gameView) {
        this.skin = skin;
        this.gameView = gameView;
        this.stage = new Stage(new ScreenViewport());
        
        // Load custom font
        try {
            customFont = new BitmapFont(Gdx.files.internal("content/fonts/new.fnt"));
        } catch (Exception e) {
            System.err.println("Failed to load custom font: " + e.getMessage());
            customFont = skin.getFont("default-font");
        }
        
        initializeUI();
    }

    private void initializeUI() {
        // Main table
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setBackground(skin.newDrawable("white", new Color(0.1f, 0.1f, 0.1f, 0.9f)));

        // Title
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = customFont;
        titleStyle.fontColor = Color.WHITE;
        titleStyle.font.getData().setScale(1.2f);
        
        Label titleLabel = new Label("Quest Menu", titleStyle);
        mainTable.add(titleLabel).padTop(20).padBottom(40).row();

        // Quest options table
        Table questOptionsTable = new Table();
        questOptionsTable.setBackground(skin.newDrawable("white", new Color(0.2f, 0.2f, 0.2f, 0.8f)));

        // Co-op Quests button (disabled - coming soon)
        TextButton coOpQuestsButton = new TextButton("Co-op Quests (Coming Soon)", skin);
        coOpQuestsButton.setDisabled(true);
        coOpQuestsButton.getLabel().setColor(Color.GRAY);
        
        // Add a tooltip or description for co-op quests
        Label coOpDescription = new Label("Team up with other players for challenging quests!", skin);
        coOpDescription.setColor(Color.LIGHT_GRAY);
        
        questOptionsTable.add(coOpQuestsButton).width(300).height(60).pad(10).row();
        questOptionsTable.add(coOpDescription).padBottom(20).row();

        // NPC Quests button
        TextButton npcQuestsButton = new TextButton("NPC Quests", skin);
        npcQuestsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                openNPCQuests();
            }
        });
        
        // Add description for NPC quests
        Label npcDescription = new Label("Complete tasks for villagers to earn rewards!", skin);
        npcDescription.setColor(Color.LIGHT_GRAY);
        
        questOptionsTable.add(npcQuestsButton).width(300).height(60).pad(10).row();
        questOptionsTable.add(npcDescription).padBottom(20).row();

        // Back button
        TextButton backButton = new TextButton("Back to Game", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                goBackToGame();
            }
        });
        
        questOptionsTable.add(backButton).width(200).height(50).pad(20).row();

        mainTable.add(questOptionsTable).pad(20);
        stage.addActor(mainTable);

        // Set input processor
        Gdx.input.setInputProcessor(stage);
    }

    private void openNPCQuests() {
        // Create and show the NPC quests screen
        NPCQuestsScreen npcQuestsScreen = new NPCQuestsScreen(skin, gameView);
        Main.getGame().setScreen(npcQuestsScreen);
    }

    private void goBackToGame() {
        // Return to the game view
        Main.getGame().setScreen(gameView);
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
