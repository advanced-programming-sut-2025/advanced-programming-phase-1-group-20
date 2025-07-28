package org.example.client.views.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.client.controllers.HouseMenuController;
import org.example.common.models.Player.Player;
import org.example.common.models.common.HoverImage;
import org.example.common.models.common.Result;
import org.example.common.models.enums.Types.CookingType;
import org.example.common.models.enums.Types.CraftingType;

import java.util.HashMap;
import java.util.Map;

public class CookingScreen implements Screen, Disposable {
    private final Stage stage;
    private final Player player;
    private final Skin skin;
    private final Screen previousScreen;


    // Tables for layout
    private final Table mainTable;
    private final Table cookingTable;

    private boolean canClose = false;

    // Maps to store textures and the new progress bars
    private final Map<CookingType, Texture> defaultCookingTextures = new HashMap<>();
    private final Map<CookingType, Texture> hoverCookingTextures = new HashMap<>();
//    private final Map<CookingType, ProgressBar> progressBars = new HashMap<>(); // Map for progress bars;

    private final Texture backgroundTexture;

    private Dialog errorDialog;
    private HouseMenuController controller;


    public CookingScreen(Player player, Skin skin, Screen previousScreen) {
        this.player = player;
        this.skin = skin;
        this.previousScreen = previousScreen;
        stage = new Stage(new ScreenViewport());

        backgroundTexture = new Texture("content/crafting_background.png");
        Image backgroundImage = new Image(backgroundTexture);

        Stack stack = new Stack();
        stack.setFillParent(true);
        stage.addActor(stack);

        stack.add(backgroundImage);

        mainTable = new Table();
        mainTable.center();
        stack.add(mainTable);

        cookingTable = new Table();
        mainTable.add(cookingTable);

        loadCookingItemsUI();

        controller = new HouseMenuController(player, player.getCurrentFarm().getBuilding());
    }

    public void loadCookingItemsUI() {
        int itemsInRow = 0;
        for (CookingType cookingType : CookingType.values()) {
            VerticalGroup itemGroup = new VerticalGroup();
            itemGroup.space(5);

            Texture defaultTex;
            if (player.cookingExists(cookingType.getName())) {
                defaultTex = new Texture("content/CookingItems/" + cookingType.getImageFilepath() + ".png");
            } else {
                defaultTex = new Texture("content/CookingItems/" + cookingType.getImageFilepath() + "_Locked" + ".png");
            }
            defaultCookingTextures.put(cookingType, defaultTex);

            Texture hoverTex = new Texture("content/CookingItems/" + cookingType.getImageFilepath() + "_hover" + ".png");
            hoverCookingTextures.put(cookingType, hoverTex);

            HoverImage image = new HoverImage(defaultTex, hoverTex, 240f);
            image.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (event.getButton() == Input.Buttons.LEFT) {

                    }
                    else if (event.getButton() == Input.Buttons.RIGHT) {

                    }
                }
            });

//            ProgressBar progressBar = new ProgressBar(0, 1, 0.01f, false, skin);
//            progressBars.put(cookingType, progressBar);

            itemGroup.addActor(image);
//            itemGroup.addActor(progressBar);

            cookingTable.add(itemGroup).pad(10);

            itemsInRow++;
            if (itemsInRow >= 8) {
                cookingTable.row();
                itemsInRow = 0;
            }
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        canClose = false;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);


        stage.act(delta);
        stage.draw();

        if (!Gdx.input.isKeyPressed(Input.Keys.C)) {
            canClose = true;
        }
        if (canClose && Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            Main.getGame().setScreen(previousScreen);
        }
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
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();

        for (Texture texture : defaultCookingTextures.values()) {
            texture.dispose();
        }
        for (Texture texture : hoverCookingTextures.values()) {
            texture.dispose();
        }
    }
}
