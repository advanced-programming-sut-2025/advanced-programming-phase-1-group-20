package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.client.network.NetworkClient;
import org.example.common.models.App;
import org.example.common.models.Player.Player;

import java.util.ArrayList;
import java.util.List;

public class VoteMenuScreen implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final Screen previousScreen;
    private final NetworkClient networkClient;

    private final Table rootTable;
    private final SelectBox<String> playersSelect;
    private final TextButton kickButton;
    private final TextButton terminateButton;
    private final TextButton yesButton;
    private final TextButton noButton;
    private final TextButton backButton;
    private final Label infoLabel;

    public VoteMenuScreen(Screen previousScreen, Skin skin) {
        this.stage = new Stage(new ScreenViewport());
        this.skin = skin;
        this.previousScreen = previousScreen;
        this.networkClient = NetworkClient.getInstance();

        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.pad(20);

        Label title = new Label("Voting", skin);
        title.setColor(Color.BLACK);
        title.setFontScale(1.6f);
        rootTable.add(title).expandX().fillX().padBottom(10).row();

        // Kick section
        Table kickTable = new Table();
        kickTable.align(Align.left);
        kickTable.add(new Label("Kick a player:", skin)).padRight(10);
        playersSelect = new SelectBox<>(skin);
        playersSelect.setItems(getPlayerUsernames());
        kickTable.add(playersSelect).width(240).padRight(10);
        kickButton = new TextButton("Start Kick Vote", skin);
        kickTable.add(kickButton).width(180);
        rootTable.add(kickTable).expandX().fillX().padBottom(12).row();

        // Terminate section
        Table termTable = new Table();
        termTable.align(Align.left);
        terminateButton = new TextButton("Start Terminate Vote", skin);
        termTable.add(terminateButton).width(220);
        rootTable.add(termTable).expandX().fillX().padBottom(12).row();

        // Cast vote section
        Table castTable = new Table();
        castTable.align(Align.left);
        castTable.add(new Label("Cast your vote:", skin)).padRight(10);
        yesButton = new TextButton("Yes", skin);
        noButton = new TextButton("No", skin);
        castTable.add(yesButton).width(100).padRight(8);
        castTable.add(noButton).width(100);
        rootTable.add(castTable).expandX().fillX().padBottom(12).row();

        // Info + back
        Table bottom = new Table();
        bottom.align(Align.left);
        infoLabel = new Label("", skin);
        infoLabel.setColor(Color.BLACK);
        bottom.add(infoLabel).expandX().left();
        backButton = new TextButton("Back", skin);
        bottom.add(backButton).width(100).right();
        rootTable.add(bottom).expandX().fillX().row();

        stage.addActor(rootTable);

        // Listeners
        kickButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String target = playersSelect.getSelected();
                if (target != null && !target.isEmpty()) {
                    networkClient.startVoteKick(target);
                    infoLabel.setText("Kick vote requested for: " + target);
                }
            }
        });

        terminateButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                networkClient.startVoteTerminate();
                infoLabel.setText("Terminate vote requested.");
            }
        });

        yesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                networkClient.castVote(true);
                infoLabel.setText("Voted YES.");
            }
        });

        noButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                networkClient.castVote(false);
                infoLabel.setText("Voted NO.");
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (previousScreen != null) {
                    Main.getGame().setScreen(previousScreen);
                }
            }
        });
    }

    private String[] getPlayerUsernames() {
        List<String> names = new ArrayList<>();
        if (App.getGame() != null && App.getGame().getPlayers() != null) {
            for (Player p : App.getGame().getPlayers()) {
                if (p != null && p.getUser() != null && p.getUser().getUsername() != null) {
                    names.add(p.getUser().getUsername());
                }
            }
        }
        return names.toArray(new String[0]);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // keep network flowing
        networkClient.update();
        Gdx.gl.glClearColor(0.94f, 0.94f, 0.94f, 1f);
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




