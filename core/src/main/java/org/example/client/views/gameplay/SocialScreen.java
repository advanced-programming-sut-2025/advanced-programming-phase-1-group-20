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
import org.example.common.models.entities.FriendShip;

import java.util.Map;

public class SocialScreen implements Screen {

    private static final float BUTTON_PADDING = 20f;
    private static final float FRIEND_ITEM_PADDING = 15f;
    private static final float LEVEL_BAR_WIDTH = 100f;
    private static final float LEVEL_BAR_HEIGHT = 10f;
    private static final float ICON_SIZE = 48f;
    private static final float TEXT_WIDTH = 150f;
    private static final float SCALE_NORMAL = 1.0f;
    private static final float SCALE_HOVER = 1.05f;
    private static final float ANIMATION_DURATION = 0.1f;
    private static final float FADE_DURATION = 0.5f;
    private static final float RIGHT_PADDING = 80f;
    private static final int MAX_FRIENDSHIP_LEVEL = 10;

    private Stage stage;
    private Skin skin;
    private Player currentPlayer;
    private Screen previousScreen;
    private Image background;
    private Texture backgroundTexture;

    public SocialScreen(Player player, Skin skin, Screen previousScreen) {
        this.currentPlayer = player;
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
        mainTable.top().padRight(RIGHT_PADDING);

        Label title = new Label("", skin);
        title.setColor(Color.GOLD);
        title.setFontScale(1.5f);
        mainTable.add(title).padTop(50).colspan(4).align(Align.center).row();

        if (currentPlayer.getFriendships() != null && !currentPlayer.getFriendships().isEmpty()) {
            for (Map.Entry<Player, FriendShip> entry : currentPlayer.getFriendships().entrySet()) {
                Player friend = entry.getKey();
                FriendShip friendship = entry.getValue();
                addFriendRow(mainTable, friend, friendship);
            }
        }
        else {
            Label noFriendsLabel = new Label("No friends yet", skin);
            noFriendsLabel.setColor(Color.LIGHT_GRAY);
            mainTable.add(noFriendsLabel).padTop(50).colspan(4).center().row();
        }

        Table bottomTable = new Table();
        bottomTable.setFillParent(true);
        bottomTable.bottom();

        ImageButton backBtn = createImageButton("assets/content/skill_icons/back.png");
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

    private void addFriendRow(Table table, Player friend, FriendShip friendship) {
        Table friendRow = new Table();

        ImageButton iconBtn = createImageButton("assets/content/skill_icons/social_icon.png");
        addHoverAnimation(iconBtn);
        friendRow.add(iconBtn).size(ICON_SIZE).pad(FRIEND_ITEM_PADDING);

        Label nameLabel = new Label(friend.getUser().getNickname(), skin);
        nameLabel.setColor(Color.WHITE);
        nameLabel.setFontScale(1.2f);
        friendRow.add(nameLabel).width(TEXT_WIDTH).pad(FRIEND_ITEM_PADDING).left();

        int level = friendship.getLevel();
        Table levelBars = new Table();
        for (int i = 1; i <= 10; i++) {
            String barTexture = (i <= level) ? "green" : "red";
            Image bar = new Image(new Texture(Gdx.files.internal("assets/content/skill_icons/" + barTexture + ".png")));
            levelBars.add(bar).size(LEVEL_BAR_WIDTH/10, LEVEL_BAR_HEIGHT).padRight(1);
        }

        Label levelLabel = new Label("Lv. " + level, skin);
        levelLabel.setColor(Color.LIGHT_GRAY);

        friendRow.add(levelBars).width(LEVEL_BAR_WIDTH).pad(FRIEND_ITEM_PADDING);
        friendRow.add(levelLabel).pad(FRIEND_ITEM_PADDING);

        table.add(friendRow).colspan(4).center().row();
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
