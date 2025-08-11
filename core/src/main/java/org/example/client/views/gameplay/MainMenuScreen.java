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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.common.models.Player.Player;

public class MainMenuScreen implements Screen {

    private static final float SCALE_NORMAL = 1.0f;
    private static final float SCALE_HOVER = 1.05f;
    private static final float ANIMATION_DURATION = 0.1f;
    private static final float FADE_DURATION = 0.5f;

    private Stage stage;
    private Skin skin;
    private Player player;
    private Screen previousScreen;

    public MainMenuScreen(Player player, Skin skin, Screen previousScreen) {
        this.player = player;
        this.skin = skin;
        this.previousScreen = previousScreen;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        ImageButton inventoryBtn = createImageButton("content/Titles/inventory.png");
        ImageButton skillsBtn = createImageButton("content/Titles/skills.png");
        ImageButton socialBtn = createImageButton("content/Titles/social.png");
        ImageButton mapBtn = createImageButton("content/Titles/map.png");
        ImageButton backBtn = createImageButton("content/Titles/back.png");

        addHoverAnimation(inventoryBtn);
        addHoverAnimation(skillsBtn);
        addHoverAnimation(socialBtn);
        addHoverAnimation(mapBtn);
        addHoverAnimation(backBtn);

        inventoryBtn.addListener(event -> {
            if (!inventoryBtn.isPressed()) return false;
            Main.getGame().setScreen(new InventoryScreen(player, skin, this));
            return true;
        });

        skillsBtn.addListener(event -> {
            if (!skillsBtn.isPressed()) return false;
            Main.getGame().setScreen(new SkillsScreen(player, skin, this));
            return true;
        });

        socialBtn.addListener(event -> {
            if (!socialBtn.isPressed()) return false;
            Main.getGame().setScreen(new SocialScreen(player, skin, this));
            return true;
        });

        mapBtn.addListener(event -> {
            if (!mapBtn.isPressed()) return false;
            Main.getGame().setScreen(new MapScreen(player, skin, this));
            return true;
        });

        backBtn.addListener(event -> {
            if (!backBtn.isPressed()) return false;
            Main.getGame().setScreen(previousScreen);
            return true;
        });

        table.add(inventoryBtn).pad(10).row();
        table.add(skillsBtn).pad(10).row();
        table.add(socialBtn).pad(10).row();
        table.add(mapBtn).pad(10).row();
        table.add(backBtn).pad(20).row();

        table.setColor(1, 1, 1, 0);
        table.addAction(Actions.fadeIn(FADE_DURATION));

        stage.addActor(table);
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

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY);
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
    }
}
