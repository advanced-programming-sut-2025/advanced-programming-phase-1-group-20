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
import org.example.common.models.App;
import org.example.common.models.Player.Player;
import org.example.common.models.entities.Quest;
import org.example.common.models.entities.QuestManager;
import org.example.common.models.Items.Item;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class NPCQuestsScreen implements Screen {
    private Stage stage;
    private Skin skin;
    private GameView gameView;
    private BitmapFont customFont;
    private ScrollPane questScrollPane;
    private Table questsTable;

    public NPCQuestsScreen(Skin skin, GameView gameView) {
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
        
        Label titleLabel = new Label("NPC Quests", titleStyle);
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
        QuestManager questManager = QuestManager.getInstance();
        questManager.updateQuestsForPlayer(player, App.getGame().getDate());
        
        // Group quests by NPC
        Map<String, List<Quest>> questsByNPC = new HashMap<>();
        List<Quest> allQuests = questManager.getActiveQuestsForPlayer(player);
        
        for (Quest quest : allQuests) {
            String npcName = quest.getNpc().getName();
            questsByNPC.computeIfAbsent(npcName, k -> new ArrayList<>()).add(quest);
        }

        if (questsByNPC.isEmpty()) {
            Label.LabelStyle noQuestsStyle = new Label.LabelStyle();
            noQuestsStyle.font = customFont;
            noQuestsStyle.fontColor = Color.LIGHT_GRAY;
            noQuestsStyle.font.getData().setScale(0.8f);
            
            Label noQuestsLabel = new Label("You don't have any active quests at the moment.\nTalk to NPCs to discover new quests!", noQuestsStyle);
            noQuestsLabel.setAlignment(1);
            questsTable.add(noQuestsLabel).width(750).height(100).pad(20).row();
        } else {
            int questIndex = 1;
            for (Map.Entry<String, List<Quest>> entry : questsByNPC.entrySet()) {
                String npcName = entry.getKey();
                List<Quest> npcQuests = entry.getValue();
                
                // Add NPC section header
                addNPCSectionHeader(npcName);
                
                // Add quests for this NPC
                for (Quest quest : npcQuests) {
                    addQuestToTable(quest, questIndex++);
                }
            }
        }
    }

    private void addNPCSectionHeader(String npcName) {
        Table npcHeaderTable = new Table();
        npcHeaderTable.setBackground(skin.newDrawable("white", new Color(0.4f, 0.4f, 0.4f, 0.9f)));
        
        // Try to load NPC portrait
        Image npcPortrait = createNPCPortrait(npcName);
        npcHeaderTable.add(npcPortrait).size(60, 60).pad(10).left();
        
        Label.LabelStyle npcNameStyle = new Label.LabelStyle();
        npcNameStyle.font = customFont;
        npcNameStyle.fontColor = Color.WHITE;
        npcNameStyle.font.getData().setScale(1.0f);
        
        Label npcNameLabel = new Label(npcName + "'s Quests", npcNameStyle);
        npcHeaderTable.add(npcNameLabel).left().pad(10).expandX().fillX();
        
        questsTable.add(npcHeaderTable).width(750).pad(5).row();
    }

    private Image createNPCPortrait(String npcName) {
        try {
            // Create a simple colored rectangle with NPC name for now
            // In a full implementation, we would need to pass the NPC object to get the face sprite
            com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(60, 60, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            pixmap.setColor(0.3f, 0.3f, 0.3f, 1f);
            pixmap.fill();
            
            // Add a simple text representation of the NPC name
            pixmap.setColor(Color.WHITE);
            // Note: This is a simplified approach - in practice we'd use the NPC sprite controller
            
            Texture fallbackTexture = new Texture(pixmap);
            pixmap.dispose();
            
            return new Image(fallbackTexture);
        } catch (Exception e) {
            // Fallback to a colored rectangle
            com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(60, 60, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            pixmap.setColor(0.3f, 0.3f, 0.3f, 1f);
            pixmap.fill();
            
            Texture fallbackTexture = new Texture(pixmap);
            pixmap.dispose();
            
            return new Image(fallbackTexture);
        }
    }

    private void addQuestToTable(Quest quest, int questNumber) {
        Table questContainer = new Table();
        questContainer.setBackground(skin.newDrawable("white", new Color(0.3f, 0.3f, 0.3f, 0.9f)));
        
                // Quest header with status
        Table headerTable = new Table();
        headerTable.setBackground(skin.newDrawable("white", new Color(0.3f, 0.3f, 0.1f, 0.8f)));

        Label.LabelStyle headerStyle = new Label.LabelStyle();
        headerStyle.font = customFont;
        headerStyle.fontColor = Color.YELLOW;
        headerStyle.font.getData().setScale(0.9f);

        Label questHeader = new Label(questNumber + ". " + quest.getTitle(), headerStyle);
        headerTable.add(questHeader).left().expandX().fillX().pad(10, 15, 10, 15);

        // Quest status indicator
        Label.LabelStyle statusStyle = new Label.LabelStyle();
        statusStyle.font = customFont;
        statusStyle.font.getData().setScale(0.7f);

        String statusText;
        Color statusColor;
        if (quest.isCompleted()) {
            statusText = "✓ COMPLETED";
            statusColor = Color.GREEN;
        } else if (quest.isActive()) {
            if (quest.hasRequiredItems(App.getGame().getCurrentPlayer())) {
                statusText = "🎯 READY";
                statusColor = Color.GREEN;
            } else {
                statusText = "⏳ IN PROGRESS";
                statusColor = Color.ORANGE;
            }
        } else {
            statusText = "🔒 LOCKED";
            statusColor = Color.RED;
        }
        
        statusStyle.fontColor = statusColor;
        Label statusLabel = new Label(statusText, statusStyle);
        headerTable.add(statusLabel).right().pad(10, 15, 10, 15);

        questContainer.add(headerTable).pad(10, 15, 5, 15).row();
        
        // Quest description
        Table descTable = new Table();
        descTable.setBackground(skin.newDrawable("white", new Color(0.1f, 0.1f, 0.2f, 0.5f)));
        
        Label.LabelStyle descStyle = new Label.LabelStyle();
        descStyle.font = customFont;
        descStyle.fontColor = Color.WHITE;
        descStyle.font.getData().setScale(0.7f);
        
        Label questDesc = new Label(quest.getDescription(), descStyle);
        questDesc.setWrap(true);
        descTable.add(questDesc).left().pad(10, 15, 10, 15).width(700);
        questContainer.add(descTable).left().pad(5, 15, 10, 15).row();
        
        // Requirements with progress
        Table reqTable = new Table();
        reqTable.setBackground(skin.newDrawable("white", new Color(0.2f, 0.2f, 0.2f, 0.5f)));
        
        Label.LabelStyle reqHeaderStyle = new Label.LabelStyle();
        reqHeaderStyle.font = customFont;
        reqHeaderStyle.fontColor = Color.ORANGE;
        reqHeaderStyle.font.getData().setScale(0.7f);
        
        Label reqHeaderLabel = new Label("Requirements:", reqHeaderStyle);
        reqTable.add(reqHeaderLabel).left().pad(5, 10, 5, 10).row();
        
        Player currentPlayer = App.getGame().getCurrentPlayer();
        boolean allRequirementsMet = true;
        
        for (Map.Entry<Item, Integer> requirement : quest.getRequirements().entrySet()) {
            Item requiredItem = requirement.getKey();
            int requiredQuantity = requirement.getValue();
            
            // Check player's inventory
            int playerQuantity = 0;
            for (Map.Entry<Item, Integer> playerItem : currentPlayer.getBackpack().getInventory().entrySet()) {
                if (playerItem.getKey().getName().equalsIgnoreCase(requiredItem.getName())) {
                    playerQuantity = playerItem.getValue();
                    break;
                }
            }
            
            Table reqItemTable = new Table();
            
            Label.LabelStyle reqItemStyle = new Label.LabelStyle();
            reqItemStyle.font = customFont;
            reqItemStyle.font.getData().setScale(0.6f);
            
            if (playerQuantity >= requiredQuantity) {
                reqItemStyle.fontColor = Color.GREEN;
                String reqText = "✓ " + requiredQuantity + " " + requiredItem.getName();
                Label reqItemLabel = new Label(reqText, reqItemStyle);
                reqItemTable.add(reqItemLabel).left().pad(2, 10, 2, 10);
            } else {
                reqItemStyle.fontColor = Color.RED;
                String reqText = "✗ " + requiredQuantity + " " + requiredItem.getName() + " (Have: " + playerQuantity + ")";
                Label reqItemLabel = new Label(reqText, reqItemStyle);
                reqItemTable.add(reqItemLabel).left().pad(2, 10, 2, 10);
                allRequirementsMet = false;
            }
            
            reqTable.add(reqItemTable).left().pad(2, 20, 2, 10).row();
        }
        
        questContainer.add(reqTable).left().pad(5, 15, 5, 15).row();
        
        // Rewards
        Table rewardTable = new Table();
        rewardTable.setBackground(skin.newDrawable("white", new Color(0.1f, 0.3f, 0.1f, 0.5f)));
        
        Label.LabelStyle rewardHeaderStyle = new Label.LabelStyle();
        rewardHeaderStyle.font = customFont;
        rewardHeaderStyle.fontColor = Color.GREEN;
        rewardHeaderStyle.font.getData().setScale(0.7f);
        
        Label rewardHeaderLabel = new Label("Rewards:", rewardHeaderStyle);
        rewardTable.add(rewardHeaderLabel).left().pad(5, 10, 5, 10).row();
        
        Table rewardItemsTable = new Table();
        
        if (quest.getGoldReward() > 0) {
            Label.LabelStyle goldStyle = new Label.LabelStyle();
            goldStyle.font = customFont;
            goldStyle.fontColor = Color.YELLOW;
            goldStyle.font.getData().setScale(0.6f);
            
            String goldText = "💰 " + quest.getGoldReward() + " gold";
            Label goldLabel = new Label(goldText, goldStyle);
            rewardItemsTable.add(goldLabel).left().pad(2, 10, 2, 10);
        }
        
        if (quest.getItemReward() != null) {
            Label.LabelStyle itemStyle = new Label.LabelStyle();
            itemStyle.font = customFont;
            itemStyle.fontColor = Color.CYAN;
            itemStyle.font.getData().setScale(0.6f);
            
            String itemText = "📦 " + quest.getItemRewardQuantity() + " " + quest.getItemReward().getName();
            Label itemLabel = new Label(itemText, itemStyle);
            rewardItemsTable.add(itemLabel).left().pad(2, 10, 2, 10);
        }
        
        rewardTable.add(rewardItemsTable).left().pad(2, 20, 2, 10).row();
        questContainer.add(rewardTable).left().pad(5, 15, 10, 15).row();
        
        // Complete button
        TextButton completeButton = new TextButton("Complete Quest", skin);
        completeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                completeQuest(quest);
            }
        });
        
        if (!quest.isActive() || quest.isCompleted() || !allRequirementsMet) {
            completeButton.setDisabled(true);
            completeButton.getLabel().setColor(Color.GRAY);
        }
        
        questContainer.add(completeButton).width(150).height(40).pad(10, 15, 15, 15).row();
        questsTable.add(questContainer).width(750).pad(10).row();
    }

    private void completeQuest(Quest quest) {
        Player player = App.getGame().getCurrentPlayer();
        QuestManager questManager = QuestManager.getInstance();
        
        boolean success = questManager.completeQuest(player, quest.getId());
        
        if (success) {
            showQuestCompletionDialog(quest, true);
            loadQuests();
        } else {
            showQuestCompletionDialog(quest, false);
        }
    }

    private void showQuestCompletionDialog(Quest quest, boolean success) {
        Dialog dialog = new Dialog("Quest Result", skin);
        
        Label.LabelStyle dialogStyle = new Label.LabelStyle();
        dialogStyle.font = customFont;
        dialogStyle.fontColor = success ? Color.GREEN : Color.RED;
        dialogStyle.font.getData().setScale(0.8f);
        
        String message = success ? 
            "Quest completed successfully!\nYou received your rewards." :
            "Cannot complete quest.\nMake sure you have all required items.";
        
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

