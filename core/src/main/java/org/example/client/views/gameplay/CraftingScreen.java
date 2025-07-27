package org.example.client.views.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
import org.example.common.models.common.Result;
import org.example.common.models.enums.Types.CraftingType;
import org.example.common.models.common.HoverImage;

import java.util.HashMap;
import java.util.Map;

public class CraftingScreen implements Screen, Disposable {
    private final Stage stage;
    private final Player player;
    private final Skin skin;
    private final Screen previousScreen;

    // Tables for layout
    private final Table mainTable; // Parent table for the whole screen
    private final Table craftingTable; // Table for the top crafting textures

    private boolean canClose = false;

    // Change from List<Texture> to Map to store both default and hover
    private final Map<CraftingType, Texture> defaultCraftingTextures = new HashMap<>();
    private final Map<CraftingType, Texture> hoverCraftingTextures = new HashMap<>();

    private Dialog errorDialog; // Declare errorDialog here
    private HouseMenuController controller;


    public CraftingScreen(Player player, Skin skin, Screen previousScreen) {
        this.player = player;
        this.skin = skin;
        this.previousScreen = previousScreen;
        stage = new Stage(new ScreenViewport());

        // **1. Main table to structure the screen**
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();
        stage.addActor(mainTable);


        // **2. Crafting table for textures at the top**
        craftingTable = new Table();
        mainTable.add(craftingTable); // Add crafting table to the main layout, it will be centered.

        loadCraftingTextures();

        controller = new HouseMenuController(player , player.getCurrentFarm().getBuilding());
    }


    // Renamed to be more specific and modified
    public void loadCraftingTextures() {
        for(CraftingType craftingType : CraftingType.values()) {
            // Load default texture
            Texture defaultTex;
            if(player.craftingExists(craftingType.getName())){
                defaultTex = new Texture("content/CraftingItems/" + craftingType.getImageFilepath() + ".png");
            }else{
                defaultTex = new Texture("content/CraftingItems/" + craftingType.getImageFilepath() + "_Locked" + ".png");
            }
            defaultCraftingTextures.put(craftingType, defaultTex);

            // Load hover texture
            Texture hoverTex = new Texture("content/CraftingItems/" + craftingType.getImageFilepath() + "_hover" + ".png");
            hoverCraftingTextures.put(craftingType, hoverTex);

            // Create and add the HoverImage to the crafting table
            HoverImage image = new HoverImage(defaultTex, hoverTex);

            image.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String[] args = new String[]{craftingType.getName()};
                    Result result = controller.craftItem(args);
                    if(!result.success()){
                        showErrorDialog("error crafting" , result.message());
                    }
                }
            });
            craftingTable.add(image).pad(10);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        canClose = false;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();

        // Wait until key is released before allowing close
        if (!Gdx.input.isKeyPressed(Input.Keys.B)) {
            canClose = true;
        }
        if (canClose && Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            Main.getGame().setScreen(previousScreen);
        }
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
        // Dispose of all textures loaded directly in this screen
        for (Texture texture : defaultCraftingTextures.values()) {
            texture.dispose();
        }
        for (Texture texture : hoverCraftingTextures.values()) {
            texture.dispose();
        }
    }

    private void showErrorDialog(String title, String message) {
        // 1. فقط اگر دیالوگ وجود ندارد آن را بساز
        if (errorDialog == null) {
            errorDialog = new Dialog("", skin);
            errorDialog.setModal(true);
            errorDialog.setMovable(false);
        }

        // 2. عنوان را به‌روزرسانی کن
        errorDialog.getTitleLabel().setText(title);

        // 3. محتوا و دکمه‌های قبلی را پاک کن
        errorDialog.getContentTable().clear();
        errorDialog.getButtonTable().clear();

        // 4. محتوای جدید (پیام) را اضافه کن
        Label messageLabel = new Label(message, skin);
        errorDialog.getContentTable().add(messageLabel);

        // 5. یک دکمه "OK" جدید بساز و به آن Listener برای بستن دیالوگ اضافه کن
        TextButton okButton = new TextButton("OK", skin);
        okButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                errorDialog.hide(); // <-- این خط به صراحت دیالوگ را می‌بندد
            }
        });

        // 6. دکمه‌ای که ساختیم را به دیالوگ اضافه کن
        errorDialog.button(okButton);

        // 7. دیالوگ را نمایش بده و در مرکز صفحه قرار بده
        errorDialog.show(stage);
        errorDialog.pack();
        errorDialog.setPosition(
            Math.round((stage.getWidth() - errorDialog.getWidth()) / 2),
            Math.round((stage.getHeight() - errorDialog.getHeight()) / 2)
        );
    }}
