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
import org.example.client.network.ClientMessageHandler;
import org.example.client.network.NetworkClient;
import org.example.common.models.App;
import org.example.common.models.Player.Player;

import java.util.*;

public class ScoreboardScreen implements Screen, ClientMessageHandler.PlayerDataUpdateListener {
    private final Stage stage;
    private final Skin skin;
    private final NetworkClient networkClient;
    private final ClientMessageHandler messageHandler;
    private final Screen previousScreen;

    private final Table rootTable;
    private final Table scoreboardTable;
    private final SelectBox<String> criteriaSelect;
    private final TextButton backButton;

    // Cached last payload for re-rendering on criteria change
    private Map<String, Object> lastPlayersMap;

    public ScoreboardScreen(Screen previousScreen, Skin skin) {
        this.stage = new Stage(new ScreenViewport());
        this.skin = skin;
        this.previousScreen = previousScreen;
        this.networkClient = NetworkClient.getInstance();
        this.messageHandler = networkClient.getMessageHandler();

        // UI setup
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.pad(20);

        Label title = new Label("Live Scoreboard", skin);
        title.setColor(Color.BLACK);
        title.setFontScale(1.6f);
        rootTable.add(title).expandX().fillX().padBottom(10).row();

        Table controls = new Table();
        controls.align(Align.left);
        controls.add(new Label("Sort by:", skin)).padRight(10);
        criteriaSelect = new SelectBox<>(skin);
        criteriaSelect.setItems("money", "completedQuests", "totalSkillLevel");
        controls.add(criteriaSelect).width(220).padRight(20);
        backButton = new TextButton("Back", skin);
        controls.add(backButton).width(100);
        rootTable.add(controls).expandX().fillX().padBottom(10).row();

        scoreboardTable = new Table(skin);
        ScrollPane scrollPane = new ScrollPane(scoreboardTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(false, false);
        rootTable.add(scrollPane).expand().fill().row();

        stage.addActor(rootTable);

        // Listeners
        criteriaSelect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                renderScoreboard();
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Close scoreboard and resume previous screen (game view)
                if (previousScreen != null) {
                    Main.getGame().setScreen(previousScreen);
                }
            }
        });
    }

    private void renderScoreboard() {
        scoreboardTable.clear();

        // Header row
        scoreboardTable.add(labelBlack("#")).pad(5);
        scoreboardTable.add(labelBlack("Player")).pad(5).expandX().left();
        scoreboardTable.add(labelBlack("Money")).pad(5);
        scoreboardTable.add(labelBlack("Completed Quests")).pad(5);
        scoreboardTable.add(labelBlack("Total Skill Lv")).pad(5).row();

        java.util.List<Row> rows = computeRows();
        System.out.println("##[SB][UI][ROWS] count=" + rows.size());
        String criterion = criteriaSelect.getSelected();
        Comparator<Row> comparator;
        if ("completedQuests".equals(criterion)) {
            comparator = Comparator.comparingInt((Row r) -> r.completedQuests).reversed()
                .thenComparingInt(r -> r.money).reversed();
        } else if ("totalSkillLevel".equals(criterion)) {
            comparator = Comparator.comparingInt((Row r) -> r.totalSkillLevel).reversed()
                .thenComparingInt(r -> r.money).reversed();
        } else {
            comparator = Comparator.comparingInt((Row r) -> r.money).reversed();
        }
        rows.sort(comparator);

        int rank = 1;
        for (Row r : rows) {
            System.out.println("##[SB][UI][ROW] user=" + r.username + " money=" + r.money +
                " completed=" + r.completedQuests + " totalSkill=" + r.totalSkillLevel);
            scoreboardTable.add(labelBlack(String.valueOf(rank++))).pad(4);
            scoreboardTable.add(labelBlack(r.username)).pad(4).left();
            scoreboardTable.add(labelBlack(String.valueOf(r.money))).pad(4);
            scoreboardTable.add(labelBlack(String.valueOf(r.completedQuests))).pad(4);
            scoreboardTable.add(labelBlack(String.valueOf(r.totalSkillLevel))).pad(4).row();
        }
    }

    private Label labelBlack(String text) {
        Label l = new Label(text, skin);
        l.setColor(Color.BLACK);
        return l;
    }

    private java.util.List<Row> computeRows() {
        java.util.List<Row> rows = new java.util.ArrayList<>();

        // Prefer server payload (lastPlayersMap) for real-time data; fallback to local App.getGame()
        if (lastPlayersMap != null && !lastPlayersMap.isEmpty()) {
            for (Map.Entry<String, Object> e : lastPlayersMap.entrySet()) {
                String username = e.getKey();
                int money = 0;
                int completed = -1;
                int totalSkill = -1;
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> pdata = (Map<String, Object>) e.getValue();
                    Object mObj = pdata.get("money");
                    if (mObj instanceof Double) money = ((Double) mObj).intValue();
                    else if (mObj instanceof Integer) money = (Integer) mObj;

                    Object compObj = pdata.get("completedQuests");
                    if (compObj instanceof Double) completed = ((Double) compObj).intValue();
                    else if (compObj instanceof Integer) completed = (Integer) compObj;

                    Object tsObj = pdata.get("totalSkillLevel");
                    if (tsObj instanceof Double) totalSkill = ((Double) tsObj).intValue();
                    else if (tsObj instanceof Integer) totalSkill = (Integer) tsObj;
                } catch (Exception ignore) {}

                // Fallback using local model (skills/quests) if server did not provide them
                Player p = App.getGame() != null ? App.getGame().getPlayerByUsername(username) : null;
                if (p != null) {
                    if (totalSkill < 0) totalSkill = safeSumSkills(p);
                    if (completed < 0) completed = safeCompletedQuests(p);
                }
                rows.add(new Row(username, money, Math.max(0, completed), Math.max(0, totalSkill)));
            }
            return rows;
        }

        if (App.getGame() != null && App.getGame().getPlayers() != null) {
            for (Player p : App.getGame().getPlayers()) {
                if (p == null || p.getUser() == null) continue;
                rows.add(new Row(
                    p.getUser().getUsername(),
                    p.getMoney(),
                    safeCompletedQuests(p),
                    safeSumSkills(p)
                ));
            }
        }
        return rows;
    }

    private int safeSumSkills(Player p) {
        int total = 0;
        if (p.getSkills() != null) {
            for (org.example.common.models.Player.Skill s : p.getSkills()) {
                total += (s != null ? s.getLevel() : 0);
            }
        }
        return total;
    }

    private int safeCompletedQuests(Player p) {
        try {
            org.example.common.models.entities.QuestManager qm = org.example.common.models.entities.QuestManager.getInstance();
            java.util.List<org.example.common.models.entities.Quest> allForPlayer = qm.getAllQuestsForPlayer(p);
            int c = 0;
            for (org.example.common.models.entities.Quest q : allForPlayer) {
                if (q != null && q.isCompleted()) c++;
            }
            return c;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        // Register listener for live updates
        if (messageHandler != null) {
            messageHandler.setPlayerDataUpdateListener(this);
        }
        renderScoreboard();
    }

    @Override
    public void render(float delta) {
        // Process incoming network traffic frequently
        if (networkClient != null) networkClient.update();
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
        if (messageHandler != null) {
            // Avoid leaking listener; scoreboard is transient
            messageHandler.setPlayerDataUpdateListener(null);
        }
        stage.dispose();
    }

    @Override
    public void onPlayerDataUpdate(Map<String, Object> playersMap, Long timestamp) {
        // Cache latest payload and re-render on UI thread
        this.lastPlayersMap = playersMap != null ? new HashMap<>(playersMap) : null;
        if (playersMap != null) {
            System.out.println("##[SB][UI][RECV_DATA] keys=" + playersMap.keySet());
            try {
                Object meName = NetworkClient.getInstance().getAuthenticatedUser() != null ?
                    NetworkClient.getInstance().getAuthenticatedUser().getUsername() : null;
                if (meName != null && playersMap.containsKey(meName)) {
                    @SuppressWarnings("unchecked") Map<String, Object> pdata = (Map<String, Object>) playersMap.get(meName);
                    System.out.println("##[SB][UI][ME_DATA] user=" + meName + " money=" + pdata.get("money") +
                        " completedQuests=" + pdata.get("completedQuests") +
                        " totalSkillLevel=" + pdata.get("totalSkillLevel"));
                }
            } catch (Exception ignored) {}
        }
        Gdx.app.postRunnable(this::renderScoreboard);
    }

    private static class Row {
        final String username;
        final int money;
        final int completedQuests;
        final int totalSkillLevel;

        Row(String username, int money, int completedQuests, int totalSkillLevel) {
            this.username = username;
            this.money = money;
            this.completedQuests = completedQuests;
            this.totalSkillLevel = totalSkillLevel;
        }
    }
}


