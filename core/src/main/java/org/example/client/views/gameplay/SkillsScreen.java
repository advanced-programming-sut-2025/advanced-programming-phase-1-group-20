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

public class SkillsScreen implements Screen {

    private static final float BUTTON_PADDING = 20f;
    private static final float SKILL_ITEM_PADDING = 30f;
    private static final float LEVEL_BAR_WIDTH = 100f;
    private static final float LEVEL_BAR_HEIGHT = 20f;
    private static final float ICON_SIZE = 64f;
    private static final float SCALE_NORMAL = 1.0f;
    private static final float SCALE_HOVER = 1.05f;
    private static final float ANIMATION_DURATION = 0.1f;
    private static final float FADE_DURATION = 0.5f;

    private Stage stage;
    private Skin skin;
    private Player player;
    private Screen previousScreen;
    private Image background;
    private Texture backgroundTexture;

    public SkillsScreen(Player player, Skin skin, Screen previousScreen) {
        this.player = player;
        this.skin = skin;
        this.previousScreen = previousScreen;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("assets/content/skill_icons/background.png"));
        background = new Image(backgroundTexture);
        background.setFillParent(true);
        stage.addActor(background);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().left();

        Label title = new Label("Skills", skin);
        title.setColor(Color.GOLD);
        title.setFontScale(1.5f);
        mainTable.add(title).padTop(50).padLeft(50).colspan(3).align(Align.left).row();

        String[] skillNames = {"farming", "fishing", "foraging", "mining"};
        for (String skillName : skillNames) {
            addSkillRow(mainTable, skillName);
        }

        ImageButton backBtn = createImageButton("assets/content/skill_icons/back.png");
        addHoverAnimation(backBtn);
        backBtn.addListener(event -> {
            if (!backBtn.isPressed()) return false;
            Main.getGame().setScreen(previousScreen);
            return true;
        });

        mainTable.add(backBtn).padTop(50).colspan(3).center().row();

        mainTable.setColor(1, 1, 1, 0);
        mainTable.addAction(Actions.fadeIn(FADE_DURATION));

        stage.addActor(mainTable);
    }

    private void addSkillRow(Table table, String skillName) {
        ImageButton iconBtn = createImageButton("assets/content/skill_icons/" + skillName + "_skill_icon.png");
        addHoverAnimation(iconBtn);
        table.add(iconBtn).size(ICON_SIZE).pad(SKILL_ITEM_PADDING).padLeft(50);

        Label nameLabel = new Label(capitalizeFirstLetter(skillName), skin);
        nameLabel.setColor(Color.WHITE);
        nameLabel.setFontScale(1.2f);
        table.add(nameLabel).width(150).pad(SKILL_ITEM_PADDING).left();

        Skill skill = player.getSkillByName(skillName);
        int level = (skill != null) ? skill.getLevel() : 0;

        Table levelBars = new Table();
        for (int i = 1; i <= 4; i++) {
            String barTexture = (i <= level) ? "green" : "red";
            Image bar = new Image(new Texture(Gdx.files.internal("assets/content/skill_icons/" + barTexture + ".png")));
            levelBars.add(bar).size(LEVEL_BAR_WIDTH/4, LEVEL_BAR_HEIGHT).padRight(5);
        }

        table.add(levelBars).width(LEVEL_BAR_WIDTH).pad(SKILL_ITEM_PADDING).right().row();
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
