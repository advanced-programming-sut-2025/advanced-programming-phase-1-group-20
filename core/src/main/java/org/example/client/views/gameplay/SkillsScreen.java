package org.example.client.views.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.common.models.Player.Player;
import org.example.common.models.Player.Skill;

import java.util.HashMap;
import java.util.Map;

import java.util.HashMap;
import java.util.Map;

public class SkillsScreen implements Screen {

    private static final float BUTTON_PADDING = 20f;
    private static final float SKILL_ITEM_PADDING = 30f;
    private static final float LEVEL_BAR_WIDTH = 280f;
    private static final float LEVEL_BAR_HEIGHT = 80f;
    private static final float ICON_SIZE = 100f;
    private static final float TEXT_IMAGE_WIDTH = 200f;
    private static final float SCALE_NORMAL = 1.0f;
    private static final float SCALE_HOVER = 1.05f;
    private static final float ANIMATION_DURATION = 0.1f;
    private static final float FADE_DURATION = 0.5f;
    private static final float RIGHT_PADDING = 60f;

    private Stage stage;
    private Skin skin;
    private Player player;
    private Screen previousScreen;
    private Image background;
    private Texture backgroundTexture;
    
    // Skill descriptions map
    private final Map<String, String> skillDescriptions = new HashMap<>();
    private final Map<String, String> skillCapabilities = new HashMap<>();

    public SkillsScreen(Player player, Skin skin, Screen previousScreen) {
        this.player = player;
        this.skin = skin;
        this.previousScreen = previousScreen;

        initializeSkillDescriptions();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("content/skill_icons/background.png"));
        background = new Image(backgroundTexture);
        background.setFillParent(true);
        stage.addActor(background);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().padRight(RIGHT_PADDING);

        Label title = new Label("", skin);
        title.setColor(Color.GOLD);
        title.setFontScale(1.5f);
        mainTable.add(title).padTop(50).colspan(4).align(Align.center).row();

        String[] skillNames = {"farming", "fishing", "foraging", "mining"};
        for (String skillName : skillNames) {
            addSkillRow(mainTable, skillName);
        }

        Table bottomTable = new Table();
        bottomTable.setFillParent(true);
        bottomTable.bottom();

        ImageButton backBtn = createImageButton("content/skill_icons/back.png");
        addHoverAnimation(backBtn);
        backBtn.addListener(event -> {
            if (!backBtn.isPressed()) return false;
            Main.getGame().setScreen(previousScreen);
            return true;
        });

        bottomTable.add(backBtn).padBottom(50).center();

        mainTable.setColor(1, 1, 1, 0);
        mainTable.addAction(Actions.fadeIn(FADE_DURATION));

        stage.addActor(mainTable);
        stage.addActor(bottomTable);
    }

    private void initializeSkillDescriptions() {
        // Skill descriptions in English
        skillDescriptions.put("farming", "Farming");
        skillDescriptions.put("fishing", "Fishing");
        skillDescriptions.put("foraging", "Foraging");
        skillDescriptions.put("mining", "Mining");
        
        // Skill capabilities descriptions
        skillCapabilities.put("farming", 
            "• Plant and harvest crops\n" +
            "• Water fields and gardens\n" +
            "• Use farming tools effectively\n" +
            "• Improve crop quality\n" +
            "• Increase plant growth speed");
            
        skillCapabilities.put("fishing", 
            "• Catch different types of fish\n" +
            "• Use fishing traps and nets\n" +
            "• Identify rare fish species\n" +
            "• Improve fishing mini-game skills\n" +
            "• Access better fishing locations");
            
        skillCapabilities.put("foraging", 
            "• Collect wild plants and herbs\n" +
            "• Cut trees and branches\n" +
            "• Find mushrooms and berries\n" +
            "• Discover hidden resources\n" +
            "• Increased chance of rare items");
            
        skillCapabilities.put("mining", 
            "• Extract stones and minerals\n" +
            "• Break large rocks and boulders\n" +
            "• Find hidden mines and caves\n" +
            "• Access precious metals and gems\n" +
            "• Improve mining tool quality");
    }

    private void addSkillRow(Table table, String skillName) {
        Table skillRow = new Table();

        ImageButton iconBtn = createImageButton("content/skill_icons/" + capitalizeFirstLetter(skillName) + "_Skill_Icon.png");
        addHoverAnimation(iconBtn);
        
        // Add tooltip functionality to the icon button
        addTooltipToIcon(iconBtn, skillName);
        
        skillRow.add(iconBtn).size(ICON_SIZE).pad(SKILL_ITEM_PADDING);

        Image nameImage = new Image(new Texture(Gdx.files.internal("content/skill_icons/" + skillName + ".png")));
        skillRow.add(nameImage).width(TEXT_IMAGE_WIDTH).pad(SKILL_ITEM_PADDING);

        Skill skill = player.getSkillByName(skillName);
        int level = (skill != null) ? skill.getLevel() : 0;

        Table levelBars = new Table();
        for (int i = 1; i <= 4; i++) {
            String barTexture = (i <= level) ? "green" : "red";
            Image bar = new Image(new Texture(Gdx.files.internal("content/skill_icons/" + barTexture + ".png")));
            levelBars.add(bar).size(LEVEL_BAR_WIDTH/4, LEVEL_BAR_HEIGHT).padRight(2);
        }

        skillRow.add(levelBars).width(LEVEL_BAR_WIDTH).pad(SKILL_ITEM_PADDING);

        table.add(skillRow).colspan(4).center().row();
    }

    private void addTooltipToIcon(Actor icon, String skillName) {
        // Create tooltip content
        String tooltipText = skillDescriptions.get(skillName) + "\n\n" + skillCapabilities.get(skillName);
        
        // Create tooltip table with better background
        Table tooltip = new Table();
        
        // Create a solid background color instead of using texture
        tooltip.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("content/ui/uiskin.png")))));
        tooltip.setColor(0.05f, 0.05f, 0.1f, 0.98f); // Dark blue background with high opacity
        tooltip.pad(15); // More padding for better appearance
        
        // Add border effect by creating an inner table
        Table innerTable = new Table();
        innerTable.setColor(0.1f, 0.1f, 0.2f, 0.95f);
        innerTable.pad(10);
        
        // Add title
        Label titleLabel = new Label(skillDescriptions.get(skillName), skin);
        titleLabel.setColor(Color.GOLD);
        titleLabel.setFontScale(1.3f);
        titleLabel.setAlignment(Align.center);
        innerTable.add(titleLabel).row();
        
        // Add separator line
        Table separator = new Table();
        separator.setColor(Color.GOLD);
        separator.setHeight(2);
        innerTable.add(separator).width(200).padTop(5).padBottom(10).row();
        
        // Add capabilities
        Label capabilitiesLabel = new Label(skillCapabilities.get(skillName), skin);
        capabilitiesLabel.setColor(Color.WHITE);
        capabilitiesLabel.setFontScale(1.0f);
        capabilitiesLabel.setAlignment(Align.left);
        capabilitiesLabel.setWrap(true);
        innerTable.add(capabilitiesLabel).width(250).row();
        
        tooltip.add(innerTable);
        tooltip.setVisible(false);
        
        // Add tooltip to stage
        stage.addActor(tooltip);
        
        // Add hover listeners with delay
        icon.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            private float hoverTime = 0f;
            private boolean isHovering = false;
            private static final float HOVER_DELAY = 0.3f; // 300ms delay before showing
            private static final float HIDE_DELAY = 0.5f; // 500ms delay before hiding
            
            @Override
            public void enter(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer != -1) return; // Only for mouse, not touch
                
                isHovering = true;
                hoverTime = 0f;
                
                // Schedule tooltip to appear after delay
                icon.addAction(Actions.sequence(
                    Actions.delay(HOVER_DELAY),
                    Actions.run(() -> {
                        if (isHovering) {
                            tooltip.setVisible(true);
                            tooltip.addAction(Actions.alpha(0f));
                            tooltip.addAction(Actions.fadeIn(0.3f));
                            
                            // Position tooltip below the skill icon
                            float tooltipX = event.getStageX() + 20f;
                            float tooltipY = event.getStageY() - 80f; // Position below the icon
                            
                            // Adjust if tooltip goes off screen
                            if (tooltipX + tooltip.getWidth() > stage.getWidth()) {
                                tooltipX = event.getStageX() - tooltip.getWidth() - 20f;
                            }
                            if (tooltipY + tooltip.getHeight() > stage.getHeight()) { // If tooltip goes below screen, put it above
                                tooltipY = event.getStageY() + 120f;
                            }
                            
                            tooltip.setPosition(tooltipX, tooltipY);
                        }
                    })
                ));
            }

            @Override
            public void exit(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer != -1) return;
                
                isHovering = false;
                
                // Schedule tooltip to disappear after delay
                icon.addAction(Actions.sequence(
                    Actions.delay(HIDE_DELAY),
                    Actions.run(() -> {
                        if (!isHovering) {
                            tooltip.addAction(Actions.sequence(
                                Actions.fadeOut(0.3f),
                                Actions.run(() -> tooltip.setVisible(false))
                            ));
                        }
                    })
                ));
            }

            @Override
            public boolean mouseMoved(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                if (tooltip.isVisible()) {
                    float tooltipX = event.getStageX() + 20f;
                    float tooltipY = event.getStageY() - 80f; // Position below the icon
                    
                    // Adjust if tooltip goes off screen
                    if (tooltipX + tooltip.getWidth() > stage.getWidth()) {
                        tooltipX = event.getStageX() - tooltip.getWidth() - 20f;
                    }
                    if (tooltipY + tooltip.getHeight() > stage.getHeight()) { // If tooltip goes below screen, put it above
                        tooltipY = event.getStageY() + 120f;
                    }
                    
                    tooltip.setPosition(tooltipX, tooltipY);
                }
                return true;
            }
        });
    }

    private ImageButton createImageButton(String imagePath) {
        Texture buttonTexture = new Texture(Gdx.files.internal(imagePath));
        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        return new ImageButton(buttonDrawable);
    }

    private void addHoverAnimation(Actor button) {
        button.addAction(Actions.forever(
            Actions.sequence(
                Actions.scaleTo(SCALE_NORMAL, SCALE_NORMAL, ANIMATION_DURATION),
                Actions.scaleTo(SCALE_HOVER, SCALE_HOVER, ANIMATION_DURATION)
            )
        ));
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Main.getGame().setScreen(previousScreen);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
    }
}
