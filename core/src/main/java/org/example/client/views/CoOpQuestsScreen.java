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
import org.example.common.models.App;
import org.example.common.models.Player.Player;
import org.example.common.models.entities.CoopQuest;
import org.example.common.models.entities.CoopQuestManager;
import org.example.common.models.Items.Item;

import java.util.List;
import java.util.Map;

public class CoOpQuestsScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private GameView gameView;
    private BitmapFont customFont;
    private ScrollPane questScrollPane;
    private Table questsTable;

    public CoOpQuestsScreen(Skin skin, GameView gameView) {
        this.skin = skin;
        this.gameView = gameView;
        this.stage = new Stage(new ScreenViewport());
        
        try {
            customFont = new BitmapFont(Gdx.files.internal("content/fonts/new.fnt"));
        } catch (Exception e) {
            customFont = skin.getFont("default-font");
        }
        
        initializeUI();
    }

    private void initializeUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setBackground(skin.newDrawable("white", new Color(0.1f, 0.1f, 0.1f, 0.9f)));

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = customFont;
        titleStyle.fontColor = Color.WHITE;
        titleStyle.font.getData().setScale(1.2f);
        
        Label titleLabel = new Label("Co-op Quests", titleStyle);
        mainTable.add(titleLabel).padTop(20).padBottom(20).row();

        questsTable = new Table();
        questsTable.setBackground(skin.newDrawable("white", new Color(0.2f, 0.2f, 0.2f, 0.8f)));
        
        questScrollPane = new ScrollPane(questsTable, skin);
        questScrollPane.setFadeScrollBars(false);
        questScrollPane.setScrollBarPositions(false, true);
        
        mainTable.add(questScrollPane).width(800).height(500).pad(20).row();

        TextButton backButton = new TextButton("Back to Quest Menu", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                goBackToQuestMenu();
            }
        });
        
        mainTable.add(backButton).width(200).height(50).pad(20).row();

        stage.addActor(mainTable);
        loadQuests();
        Gdx.input.setInputProcessor(stage);
    }

    private void loadQuests() {
        questsTable.clear();
        
        Player player = App.getGame().getCurrentPlayer();
        CoopQuestManager coopQuestManager = CoopQuestManager.getInstance();
        
        // Update quests for the current player
        coopQuestManager.updateCoopQuestsForPlayer(player, App.getGame().getDate());
        
        // Get different types of quests
        List<CoopQuest> activeQuests = coopQuestManager.getActiveQuestsForPlayer(player);
        List<CoopQuest> availableQuests = coopQuestManager.getAvailableCoopQuests();
        
        // Show active quests section
        if (!activeQuests.isEmpty()) {
            addSectionHeader("YOUR ACTIVE CO-OP QUESTS", Color.GREEN);
            
            int questIndex = 1;
            for (CoopQuest quest : activeQuests) {
                addActiveQuestToTable(quest, questIndex++);
            }
        }
        
        // Show available quests section
        if (!availableQuests.isEmpty()) {
            addSectionHeader("AVAILABLE CO-OP QUESTS TO JOIN", Color.YELLOW);
            
            for (CoopQuest quest : availableQuests) {
                addAvailableQuestToTable(quest);
            }
        }
        
        // Show message if no quests at all
        if (activeQuests.isEmpty() && availableQuests.isEmpty()) {
            Label.LabelStyle noQuestsStyle = new Label.LabelStyle();
            noQuestsStyle.font = customFont;
            noQuestsStyle.fontColor = Color.LIGHT_GRAY;
            noQuestsStyle.font.getData().setScale(0.8f);
            
            Label noQuestsLabel = new Label("No co-op quests available at the moment.\nCheck back later for new challenges!", noQuestsStyle);
            noQuestsLabel.setAlignment(1);
            questsTable.add(noQuestsLabel).width(750).height(100).pad(20).row();
        }
    }

    private void addSectionHeader(String title, Color color) {
        Table sectionHeaderTable = new Table();
        sectionHeaderTable.setBackground(skin.newDrawable("white", color));
        
        Label.LabelStyle sectionHeaderStyle = new Label.LabelStyle();
        sectionHeaderStyle.font = customFont;
        sectionHeaderStyle.fontColor = Color.WHITE;
        sectionHeaderStyle.font.getData().setScale(1.1f);
        
        Label sectionHeaderLabel = new Label(title, sectionHeaderStyle);
        sectionHeaderTable.add(sectionHeaderLabel).pad(15).center();
        
        questsTable.add(sectionHeaderTable).width(750).pad(5).row();
    }

    private void addActiveQuestToTable(CoopQuest quest, int questIndex) {
        Table questTable = new Table();
        questTable.setBackground(skin.newDrawable("white", new Color(0.3f, 0.3f, 0.3f, 0.9f)));
        
        // Quest title and description
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = customFont;
        titleStyle.fontColor = Color.WHITE;
        titleStyle.font.getData().setScale(1.0f);
        
        Label.LabelStyle descStyle = new Label.LabelStyle();
        descStyle.font = customFont;
        descStyle.fontColor = Color.LIGHT_GRAY;
        descStyle.font.getData().setScale(0.8f);
        
        Label titleLabel = new Label(questIndex + ". " + quest.getTitle(), titleStyle);
        Label descLabel = new Label(quest.getDescription(), descStyle);
        descLabel.setWrap(true);
        
        // Quest details
        String details = String.format("Players: %d/%d | Progress: %.1f%% (%d/%d) | Time Remaining: %d days",
                quest.getCurrentPlayerCount(), quest.getMaxPlayers(),
                quest.getCompletionPercentage(),
                quest.getTotalProgress(), getTotalRequiredProgress(quest),
                quest.getRemainingDays());
        
        Label detailsLabel = new Label(details, descStyle);
        detailsLabel.setColor(Color.CYAN);
        
        // Your progress
        String yourProgress = String.format("Your Progress: %d", quest.getPlayerProgress(App.getGame().getCurrentPlayer()));
        Label progressLabel = new Label(yourProgress, descStyle);
        progressLabel.setColor(Color.GREEN);
        
        // Requirements
        StringBuilder reqText = new StringBuilder("Requirements: ");
        for (Map.Entry<Item, Integer> requirement : quest.getRequirements().entrySet()) {
            reqText.append(requirement.getValue()).append(" ").append(requirement.getKey().getName()).append(", ");
        }
        if (reqText.length() > 12) {
            reqText.setLength(reqText.length() - 2); // Remove last comma and space
        }
        Label reqLabel = new Label(reqText.toString(), descStyle);
        reqLabel.setColor(Color.ORANGE);
        
        // Rewards
        StringBuilder rewardText = new StringBuilder("Rewards: ");
        if (quest.getGoldReward() > 0) {
            rewardText.append(quest.getGoldReward()).append(" gold");
            if (quest.getItemReward() != null) {
                rewardText.append(", ");
            }
        }
        if (quest.getItemReward() != null) {
            rewardText.append(quest.getItemRewardQuantity()).append(" ").append(quest.getItemReward().getName());
        }
        Label rewardLabel = new Label(rewardText.toString(), descStyle);
        rewardLabel.setColor(Color.GOLD);
        
        // Layout
        questTable.add(titleLabel).left().pad(10, 15, 5, 15).row();
        questTable.add(descLabel).left().pad(5, 15, 10, 15).width(700).row();
        questTable.add(detailsLabel).left().pad(5, 15, 5, 15).row();
        questTable.add(progressLabel).left().pad(5, 15, 5, 15).row();
        questTable.add(reqLabel).left().pad(5, 15, 5, 15).row();
        questTable.add(rewardLabel).left().pad(5, 15, 15, 15).row();
        
        questsTable.add(questTable).width(750).pad(5).row();
    }

    private void addAvailableQuestToTable(CoopQuest quest) {
        Table questTable = new Table();
        questTable.setBackground(skin.newDrawable("white", new Color(0.3f, 0.3f, 0.3f, 0.9f)));
        
        // Quest title and description
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = customFont;
        titleStyle.fontColor = Color.WHITE;
        titleStyle.font.getData().setScale(1.0f);
        
        Label.LabelStyle descStyle = new Label.LabelStyle();
        descStyle.font = customFont;
        descStyle.fontColor = Color.LIGHT_GRAY;
        descStyle.font.getData().setScale(0.8f);
        
        Label titleLabel = new Label("[ID: " + quest.getId() + "] " + quest.getTitle(), titleStyle);
        Label descLabel = new Label(quest.getDescription(), descStyle);
        descLabel.setWrap(true);
        
        // Quest details
        String details = String.format("Players: %d/%d | Time Limit: %d days",
                quest.getCurrentPlayerCount(), quest.getMaxPlayers(), quest.getTimeLimitDays());
        
        Label detailsLabel = new Label(details, descStyle);
        detailsLabel.setColor(Color.CYAN);
        
        // Requirements
        StringBuilder reqText = new StringBuilder("Requirements: ");
        for (Map.Entry<Item, Integer> requirement : quest.getRequirements().entrySet()) {
            reqText.append(requirement.getValue()).append(" ").append(requirement.getKey().getName()).append(", ");
        }
        if (reqText.length() > 12) {
            reqText.setLength(reqText.length() - 2); // Remove last comma and space
        }
        Label reqLabel = new Label(reqText.toString(), descStyle);
        reqLabel.setColor(Color.ORANGE);
        
        // Rewards
        StringBuilder rewardText = new StringBuilder("Rewards: ");
        if (quest.getGoldReward() > 0) {
            rewardText.append(quest.getGoldReward()).append(" gold");
            if (quest.getItemReward() != null) {
                rewardText.append(", ");
            }
        }
        if (quest.getItemReward() != null) {
            rewardText.append(quest.getItemRewardQuantity()).append(" ").append(quest.getItemReward().getName());
        }
        Label rewardLabel = new Label(rewardText.toString(), descStyle);
        rewardLabel.setColor(Color.GOLD);
        
        // Join button
        TextButton joinButton = new TextButton("Join Quest", skin);
        joinButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                joinQuest(quest);
            }
        });
        
        // Layout
        questTable.add(titleLabel).left().pad(10, 15, 5, 15).row();
        questTable.add(descLabel).left().pad(5, 15, 10, 15).width(700).row();
        questTable.add(detailsLabel).left().pad(5, 15, 5, 15).row();
        questTable.add(reqLabel).left().pad(5, 15, 5, 15).row();
        questTable.add(rewardLabel).left().pad(5, 15, 10, 15).row();
        
        // Add join button on the right side
        Table buttonTable = new Table();
        buttonTable.add().expandX();
        buttonTable.add(joinButton).width(120).height(35).padRight(15);
        questTable.add(buttonTable).fillX().row();
        
        questsTable.add(questTable).width(750).pad(5).row();
    }

    private int getTotalRequiredProgress(CoopQuest quest) {
        int total = 0;
        for (Map.Entry<Item, Integer> requirement : quest.getRequirements().entrySet()) {
            total += requirement.getValue();
        }
        return total;
    }

    private void joinQuest(CoopQuest quest) {
        Player player = App.getGame().getCurrentPlayer();
        CoopQuestManager coopQuestManager = CoopQuestManager.getInstance();

        // Pre-check: show dialog if player already has 3 active co-op quests
        if (coopQuestManager.getActiveQuestsForPlayer(player).size() >= 3) {
            showJoinQuestDialog(quest, false, "You already have 3 active co-op quests. Complete one before joining another.");
            return;
        }

        boolean success = coopQuestManager.joinCoopQuest(player, quest.getId(), App.getGame().getDate());

        if (success) {
            showJoinQuestDialog(quest, true, "Successfully joined the co-op quest!\nYou can now contribute items to help complete it.");
            loadQuests(); // Refresh the quest list
        } else {
            showJoinQuestDialog(quest, false, "Cannot join quest. The quest may be full or no longer available.");
        }
    }

    private void showJoinQuestDialog(CoopQuest quest, boolean success, String message) {
        Dialog dialog = new Dialog("Join Co-op Quest", skin);

        Label.LabelStyle dialogStyle = new Label.LabelStyle();
        dialogStyle.font = customFont;
        dialogStyle.fontColor = success ? Color.GREEN : Color.RED;
        dialogStyle.font.getData().setScale(0.8f);

        Label messageLabel = new Label(message, dialogStyle);
        dialog.text(messageLabel);

        TextButton okButton = new TextButton("OK", skin);
        dialog.button(okButton);

        dialog.show(stage);
    }

    private void goBackToQuestMenu() {
        QuestMenuScreen questMenuScreen = new QuestMenuScreen(skin, gameView);
        Main.getGame().setScreen(questMenuScreen);
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
    }
}
