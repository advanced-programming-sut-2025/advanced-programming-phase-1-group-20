package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.common.models.Player.Player;
import org.example.common.models.Items.Item;
import org.example.common.models.Player.Backpack;
import org.example.common.models.Items.Tool;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryScreen implements Screen {
    private Stage stage;
    private Table table;
    private Player player;
    private Skin skin;
    private Screen previousScreen;
    private boolean canClose = false;

    private static final int SLOTS_PER_ROW = 12;
    private static final int TOTAL_SLOTS = 36;
    private static final String EMPTY_SLOT_IMAGE = "content/ui/empty_slot.png";

    private static final List<String> TOOL_ORDER = Arrays.asList(
            "Basic Hoe", "Basic Pickaxe", "Basic Axe", "Basic Watering Can", "Scythe", "Initial Trash Can"
    );

    public InventoryScreen(Player player, Skin skin, Screen previousScreen) {
        this.player = player;
        this.skin = skin;
        this.previousScreen = previousScreen;
        stage = new Stage(new ScreenViewport());

        // Inventory grid table
        table = new Table();
        table.center();
        // Set preferred size for the inventory grid (12 slots * 64px + 11*6px padding wide, 3 slots * 64px + 2*6px padding tall)
        int slotSize = 64;
        int slotPad = 6;
        int gridWidth = 12 * slotSize + 11 * slotPad;
        int gridHeight = 3 * slotSize + 2 * slotPad;
        table.setSize(gridWidth, gridHeight);

        // Parent table to center the inventory grid
        Table parent = new Table();
        parent.setFillParent(true);
        parent.center();
        parent.add(table).width(gridWidth).height(gridHeight);

        stage.addActor(parent);

        populateInventory();
    }

    private void populateInventory() {
        Backpack backpack = player.getBackpack();
        List<Item> tools = new ArrayList<>();
        List<Item> otherItems = new ArrayList<>();
        for (Item item : backpack.getInventory().keySet()) {
            if (item instanceof Tool) {
                tools.add(item);
            } else {
                otherItems.add(item);
            }
        }
        List<Item> slotItems = new ArrayList<>();
        for (String toolName : TOOL_ORDER) {
            Item found = null;
            for (Item tool : tools) {
                if (tool.getName().equals(toolName)) {
                    found = tool;
                    break;
                }
            }
            slotItems.add(found);
        }

        int slotsLeft = TOTAL_SLOTS - TOOL_ORDER.size();
        int added = 0;
        for (Item item : otherItems) {
            if (added >= slotsLeft) break;
            slotItems.add(item);
            added++;
        }
        while (slotItems.size() < TOTAL_SLOTS) {
            slotItems.add(null);
        }
        table.clear();
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            Item item = slotItems.get(i);
            Table cell = new Table();
            boolean isSelected = false;
            if (item != null) {
                Texture texture = new Texture(Gdx.files.internal(item.getImageFilepath()));
                Image image = new Image(texture);
                int count = backpack.getInventory().get(item);
                Label countLabel = new Label(String.valueOf(count), skin);
                countLabel.setColor(Color.WHITE);
                cell.add(image).size(64, 64).row();
                cell.add(countLabel);
                if (item instanceof Tool && player.getCurrentTool() != null && item.getName().equals(player.getCurrentTool().getName())) {
                    isSelected = true;
                }
                if (isSelected) {
                    cell.setColor(0.2f, 0.7f, 1f, 0.5f); // Light blue highlight
                    cell.setBackground(skin.newDrawable("white", new Color(0.2f, 0.7f, 1f, 0.3f)));
                }
                if (item instanceof Tool) {
                    cell.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            player.equipTool(item.getName());
                            Main.getGame().setScreen(previousScreen);
                        }
                    });
                }
            } else {
                Texture texture = new Texture(Gdx.files.internal(EMPTY_SLOT_IMAGE));
                Image image = new Image(texture);
                cell.add(image).size(64, 64);
            }
            table.add(cell).pad(6);
            if ((i + 1) % SLOTS_PER_ROW == 0) table.row();
        }
        Texture trashTexture = new Texture(Gdx.files.internal("content/Tools/Trash_Can_Steel.png"));
        Image trashImage = new Image(trashTexture);
        Table trashCell = new Table();
        trashCell.add(trashImage).size(64, 64);
        table.add(trashCell).pad(10);
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

        // Wait until Esc is released before allowing close
        if (!Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            canClose = true;
        }
        if (canClose && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
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
    }
}
