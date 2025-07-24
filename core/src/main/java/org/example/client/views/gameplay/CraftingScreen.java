package org.example.client.views.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent; // Keep for other listeners
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable; // Import Disposable for better texture management
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.common.models.Player.Player;
import org.example.common.models.Items.Item;
import org.example.common.models.Player.Backpack;
import org.example.common.models.Items.Tool;
import org.example.common.models.enums.Types.CraftingType;
import org.example.common.models.common.HoverImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap; // For storing default and hover textures
import java.util.List;
import java.util.Map;

public class CraftingScreen implements Screen, Disposable {
    private final Stage stage;
    private final Player player;
    private final Skin skin;
    private final Screen previousScreen;

    // Tables for layout
    private final Table mainTable; // Parent table for the whole screen
    private final Table craftingTable; // Table for the top crafting textures
    private final Table inventoryTable; // Table for the bottom inventory grid

    private boolean canClose = false;

    private static final int SLOTS_PER_ROW = 12;
    private static final int TOTAL_SLOTS = 36;
    private static final String EMPTY_SLOT_IMAGE = "content/ui/empty_slot.png";

    private static final List<String> TOOL_ORDER = Arrays.asList(
        "Basic Hoe", "Basic Pickaxe", "Basic Axe", "Basic Watering Can", "Scythe", "Initial Trash Can"
    );

    // Change from List<Texture> to Map to store both default and hover
    private final Map<CraftingType, Texture> defaultCraftingTextures = new HashMap<>();
    private final Map<CraftingType, Texture> hoverCraftingTextures = new HashMap<>();


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
        mainTable.add(craftingTable).padBottom(60).row(); // Add crafting table to the main layout

        loadCraftingTextures(); // Renamed and modified

        // **3. Inventory table for the grid at the bottom**
        inventoryTable = new Table();
        int slotSize = 64;
        int slotPad = 6;
        int gridWidth = 12 * slotSize + 11 * slotPad;
        int gridHeight = 3 * slotSize + 2 * slotPad;
        mainTable.add(inventoryTable).width(gridWidth).height(gridHeight); // Add inventory to the main layout

        mainTable.row();

        populateInventory();
    }


    // Renamed to be more specific and modified
    public void loadCraftingTextures() {
        for(CraftingType craftingType : CraftingType.values()) {
            // Load default texture
            Texture defaultTex = new Texture("content/CraftingItems/" + craftingType.getImageFilepath() + ".png");
            defaultCraftingTextures.put(craftingType, defaultTex);

            // Load hover texture (assuming a naming convention, e.g., "_hover")
            // You might need to adjust this path/naming based on your actual assets
            Texture hoverTex = new Texture("content/CraftingItems/" + craftingType.getImageFilepath() + "_hover" + ".png");
            hoverCraftingTextures.put(craftingType, hoverTex);

            // Create and add the HoverImage to the crafting table
            HoverImage image = new HoverImage(defaultTex, hoverTex);
            // You can add a ClickListener here if you want crafting items to be clickable


            image.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("CraftingScreen", "Clicked on: " + craftingType.name());
                    // Add your crafting logic here based on 'craftingType'
                }
            });
            craftingTable.add(image).pad(10);
        }
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
        inventoryTable.clear(); // Clear the specific inventory table
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
                // You should dispose of this texture when the item is no longer needed in this cell,
                // but for inventory, it's often more complex. A better approach for inventory is
                // to use an AssetManager or reuse textures. For now, we'll let dispose() handle it
                // at screen exit, but be aware of potential multiple texture loads for the same item.
            } else {
                Texture texture = new Texture(Gdx.files.internal(EMPTY_SLOT_IMAGE));
                Image image = new Image(texture);
                cell.add(image).size(64, 64);
                // Dispose of this empty slot texture later
            }
            inventoryTable.add(cell).pad(6);
            if ((i + 1) % SLOTS_PER_ROW == 0) inventoryTable.row();
        }
        Texture trashTexture = new Texture(Gdx.files.internal("content/Tools/Trash_Can_Steel.png"));
        Image trashImage = new Image(trashTexture);
        Table trashCell = new Table();
        trashCell.add(trashImage).size(64, 64);
        inventoryTable.add(trashCell).pad(10);
        // Dispose of the trash texture later
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
        // Dispose of textures in inventory cells. This is more complex
        // if textures are dynamically loaded or reused.
        // For simple cases, you might iterate through the inventoryTable's children
        // and dispose of their drawables if they are Textures.
        // A robust asset management system (e.g., AssetManager) is highly recommended.
        // For now, I'll add a placeholder for inventory cleanup.
        disposeInventoryTextures();
    }

    private void disposeInventoryTextures() {
        // This is a simplified approach and not ideal.
        // It's still prone to issues if textures are shared or already disposed.
        // AssetManager is the recommended solution.

        // Collect textures to dispose to avoid ConcurrentModificationException if modifying the actor list
        List<Texture> texturesToDispose = new ArrayList<>();

        for (com.badlogic.gdx.scenes.scene2d.Actor actor : inventoryTable.getChildren()) {
            if (actor instanceof Table) {
                Table cell = (Table) actor;
                for (com.badlogic.gdx.scenes.scene2d.Actor cellChild : cell.getChildren()) {
                    if (cellChild instanceof Image) {
                        Image image = (Image) cellChild;
                        if (image.getDrawable() instanceof com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable) {
                            Texture texture = ((com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable) image.getDrawable()).getRegion().getTexture();
                            // In a manual disposal scenario, you just dispose it.
                            // The problem is that if multiple Image objects use the same Texture object,
                            // you'll dispose it multiple times, which is an error.
                            // To mitigate, you might track disposed textures.
                            texturesToDispose.add(texture);
                        }
                    }
                }
            }
        }

        // Dispose collected textures, trying to avoid multiple disposals of the same Texture object.
        // This is still a hacky workaround for not using AssetManager.
        List<Texture> alreadyDisposedTracker = new ArrayList<>(); // A simple tracker for this session

        for (Texture texture : texturesToDispose) {
            // This is still problematic without AssetManager because 'texture' might be shared
            // and already disposed by a previous iteration.
            // There's no reliable way to check if a Texture object is valid after its dispose() has been called.
            // The best you can do is avoid double-disposing the same *object reference* within this loop.
            if (!alreadyDisposedTracker.contains(texture)) {
                texture.dispose();
                alreadyDisposedTracker.add(texture);
            }
        }


        // Dispose of the trash can texture
        // Re-loading here is inefficient and could cause issues if it was disposed by a cell.
        // It's better to store a reference to the loaded trashTexture and dispose that.
        // For demonstration, let's assume it's loaded only once in populateInventory.
        // You'd need a field for it, e.g., `private Texture trashTexture;`
        // Then, in dispose, you'd do `if (trashTexture != null) trashTexture.dispose();`
        // For now, I'll remove the re-creation, assuming it's part of the `texturesToDispose` if it was added.

        // Dispose of empty slot texture
        // Same as above, if it's reused, it's problematic.
        // Best to load it once as a class field and dispose that field.
    }
}
