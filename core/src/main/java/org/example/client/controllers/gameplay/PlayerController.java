package org.example.client.controllers.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import org.example.client.Main;
import org.example.client.network.NetworkClient;
import org.example.common.models.Items.*;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.MapDetails.GameMap;
import org.example.common.models.MapDetails.Village;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Location;
import org.example.common.models.enums.Types.TileType;
import org.example.common.models.App;

import static org.example.common.models.Items.Tool.ToolMaterial.*;

public class PlayerController {
    private static final int FRAME_W = 16;
    private static final int FRAME_H = 32;
    private static final int RENDER_W = 48;
    private static final int RENDER_H = 72;
    private static final float FRAME_DURATION = 0.2f;
    private static final int VILLAGE_TRANSITION_THRESHOLD = 3;
    private static final int FARM_EDGE_DEBUG_THRESHOLD = 5;
    private static final int MOVEMENT_ENERGY_PERCENTAGE = 5; // 0.05% of current energy per movement (5/10000 = 0.05%)
    private static final int MIN_MOVEMENT_ENERGY_COST = 1; // Minimum 1 energy cost per movement
    private long lastTransitionTime = 0;
    private static final long TRANSITION_COOLDOWN_MS = 500;
    private static final float TOOL_USE_DURATION = 0.5f;
    private static final float TOOL_USE_OFFSET_X = 20f;
    private static final float TOOL_USE_OFFSET_Y = 10f;

    private final Player player;
    private final Farm farm;
    private final GameMap gameMap;
    private boolean transitionInProgress = false;

    private final Animation<TextureRegion> walkDown;
    private final Animation<TextureRegion> walkLeft;
    private final Animation<TextureRegion> walkRight;
    private final Animation<TextureRegion> walkUp;
    private final Animation<TextureRegion> collapsedAnim;

    // Item animations
    private final Animation<TextureRegion> itemDown;
    private final Animation<TextureRegion> itemLeft;
    private final Animation<TextureRegion> itemRight;
    private final Animation<TextureRegion> itemUp;

    private Animation<TextureRegion> currentAnim;
    private float stateTime = 0f;

    private enum Dir {DOWN, LEFT, RIGHT, UP}

    private Dir facing = Dir.DOWN;

    private float toolAnimTime = 0f;
    private boolean toolSwinging = false;
    private String lastToolDirection = "down";
    private float lastMouseX = 0f;
    private float lastMouseY = 0f;
    private OrthographicCamera camera;

    // Nickname rendering
    private BitmapFont nicknameFont;
    private Skin skin;
    private static final float NICKNAME_OFFSET_Y = 120f;
    private static final Color NICKNAME_TEXT_COLOR = Color.WHITE;
    private static final Color CURRENT_PLAYER_TEXT_COLOR = new Color(0.8f, 1f, 0.8f, 1f);

    // Add this field to the class
    private boolean justTransitionedToVillage = false;
    private boolean showTransitionDialog = false;
    private String transitionMessage = "";
    private boolean isMoving = false;

    private boolean isUsingTool = false;
    private float toolUseTime = 0f;
    private Animation<TextureRegion> currentToolAnim;
    private Array<TextureRegion> toolUseFrames = new Array<>();

    public void triggerToolSwing(String direction, float mouseX, float mouseY) {
        toolAnimTime = 0f;
        toolSwinging = true;
        lastToolDirection = direction;
        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    public PlayerController(Player player, Farm farm) {
        this.player = player;
        this.farm = farm;
        this.gameMap = App.getGame().getGameMap();

        // Load individual sprite files instead of sprite sheet
        walkDown = buildWalkAnimation("down");
        walkLeft = buildWalkAnimation("left");
        walkRight = buildWalkAnimation("right");
        walkUp = buildWalkAnimation("up");

        // Load collapsed animation
        collapsedAnim = buildCollapsedAnim();

        // Load item animations
        itemDown = buildItemAnimation("down");
        itemLeft = buildItemAnimation("left");
        itemRight = buildItemAnimation("right");
        itemUp = buildItemAnimation("up");

        currentAnim = walkDown;
    }

    public PlayerController(Player player, Farm farm, Skin skin, OrthographicCamera camera) {
        this.player = player;
        this.farm = farm;
        this.gameMap = App.getGame().getGameMap();
        this.skin = skin;
        this.camera = camera;

        // Load individual sprite files instead of sprite sheet
        walkDown = buildWalkAnimation("down");
        walkLeft = buildWalkAnimation("left");
        walkRight = buildWalkAnimation("right");
        walkUp = buildWalkAnimation("up");

        // Load collapsed animation
        collapsedAnim = buildCollapsedAnim();

        // Load item animations
        itemDown = buildItemAnimation("down");
        itemLeft = buildItemAnimation("left");
        itemRight = buildItemAnimation("right");
        itemUp = buildItemAnimation("up");

        currentAnim = walkDown;

        initializeNicknameFont();
    }

    private Animation<TextureRegion> buildWalkAnimation(String direction) {
        Array<TextureRegion> frames = new Array<>(3);
        try {
            for (int i = 1; i <= 3; i++) {
                String spritePath = String.format("sprites/player/%s_%d.png", direction, i);
                Texture frameTexture = new Texture(Gdx.files.internal(spritePath));
                frames.add(new TextureRegion(frameTexture));
            }
        }
        catch (Exception e) {
            System.out.println("Warning: Could not load walk animation for direction " + direction + ": " + e.getMessage());
            // Create a simple fallback frame
            Texture fallbackTexture = new Texture(Gdx.files.internal("sprites/player/down_1.png"));
            TextureRegion fallbackRegion = new TextureRegion(fallbackTexture);
            frames.add(fallbackRegion);
        }
        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP_PINGPONG);
    }

    private Animation<TextureRegion> buildItemAnimation(String direction) {
        Array<TextureRegion> frames = new Array<>(1);
        try {
            // For item animations, we'll use a single static frame since we want static sprites
            String spritePath = String.format("sprites/player/item_%s.png", direction);
            Texture frameTexture = new Texture(Gdx.files.internal(spritePath));
            // Add only one frame for static sprite
            frames.add(new TextureRegion(frameTexture));
        } catch (Exception e) {
            System.out.println("Warning: Could not load item animation for direction " + direction + ": " + e.getMessage());
            // Create a simple fallback frame
            Texture fallbackTexture = new Texture(Gdx.files.internal("sprites/player/down_1.png"));
            TextureRegion fallbackRegion = new TextureRegion(fallbackTexture);
            frames.add(fallbackRegion);
        }
        // Use a very long duration so the animation doesn't loop (static sprite)
        return new Animation<>(999.0f, frames, Animation.PlayMode.NORMAL);
    }

    private Animation<TextureRegion> buildCollapsedAnim() {
        Array<TextureRegion> frames = new Array<>(2);
        try {
            Texture collapse1 = new Texture(Gdx.files.internal("sprites/player/collapse_1.png"));
            Texture collapse2 = new Texture(Gdx.files.internal("sprites/player/collapse_2.png"));
            frames.add(new TextureRegion(collapse1));
            frames.add(new TextureRegion(collapse2));
        } catch (Exception e) {
            // Fallback to a single frame if sprites can't be loaded
            System.out.println("Warning: Could not load collapsed animation sprites: " + e.getMessage());
            // Create a simple fallback frame
            Texture fallbackTexture = new Texture(Gdx.files.internal("sprites/player/down_1.png"));
            TextureRegion fallbackRegion = new TextureRegion(fallbackTexture);
            frames.add(fallbackRegion);
        }
        // Use a 10 second duration and NORMAL mode so it stops on the last frame
        return new Animation<>(10.0f, frames, Animation.PlayMode.NORMAL);
    }

    private void initializeNicknameFont() {
        try {
            // Try to load the custom font first
            nicknameFont = new BitmapFont(Gdx.files.internal("content/fonts/new.fnt"));
            nicknameFont.getData().setScale(0.6f);
        } catch (Exception e) {
            // Fallback to default font
            nicknameFont = new BitmapFont();
            nicknameFont.getData().setScale(0.6f);
        }
    }

    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        handlePlayerInput(delta);

        if (isUsingTool) {
            toolUseTime += delta;
            if (toolUseTime >= TOOL_USE_DURATION) {
                isUsingTool = false;
            }
        }

        handlePlayerInput(delta);
        stateTime += delta;

        updateCurrentAnimation();

        // Continuously update mouse position in world coordinates for tool direction
        if (camera != null) {
            // Get screen coordinates - Gdx.input.getY() returns Y from top-left, but we need bottom-left
            float screenX = Gdx.input.getX();
            float screenY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Invert Y coordinate
            Vector3 mouseScreenPos = new Vector3(screenX, screenY, 0);
            Vector3 mouseWorldPos = camera.unproject(mouseScreenPos);
            lastMouseX = mouseWorldPos.x;
            lastMouseY = mouseWorldPos.y;
        }

        stateTime += delta;

        // Use collapsed animation if player has collapsed
        TextureRegion frame;
        if (player.hasCollapsed()) {
            // For collapsed animation, play once and stay on last frame
            frame = collapsedAnim.getKeyFrame(stateTime, false);

            // Check if we're on the final frame (collapse_2) and swap width/height
            // Since we have 2 frames and 10 second duration, after 10 seconds we're on frame 1 (index 1)
            float animationTime = stateTime;
            if (animationTime >= 3.0f) { // After 5 seconds (half of 10), we're on the second frame

                Main.getBatch().draw(
                    frame,
                    player.getPosX(),
                    player.getPosY(),
                    RENDER_H,  // Use height as width
                    RENDER_W   // Use width as height
                );
            } else {
                // Normal dimensions for the first frame
                Main.getBatch().draw(
                    frame,
                    player.getPosX(),
                    player.getPosY(),
                    RENDER_W,
                    RENDER_H
                );
            }
        } else {
            // Check if we're using item animations (static sprites)
            if (player.getCurrentItem() != null && !this.isMoving) {
                // For static item sprites, use the first frame without animation
                frame = currentAnim.getKeyFrame(0, false);
            } else {
                // For walking animations, use normal animation
                frame = currentAnim.getKeyFrame(stateTime, true);
            }
            Main.getBatch().draw(
                frame,
                player.getPosX(),
                player.getPosY(),
                RENDER_W,
                RENDER_H
            );
        }

        // Draw tool if equipped (but not when holding an item)
        if (player.getCurrentTool() != null && !player.hasCollapsed() && player.getCurrentItem() == null) {
            // When standing still, show tool sprite based on direction
            if (!this.isMoving) {
                renderToolSprite();
            }
            // When moving, don't show any tool (just the walking animation)
        }
    }

    private int calculateMovementEnergyCost() {
        // Calculate 0.05% of player's current energy as movement cost
        int currentEnergy = player.getEnergy();
        int energyCost = Math.max(MIN_MOVEMENT_ENERGY_COST, currentEnergy * MOVEMENT_ENERGY_PERCENTAGE / 10000);

        // Ensure we don't consume more than 1 energy for very low energy levels
        if (currentEnergy < 2000 && energyCost > 1) {
            energyCost = 1;
        }

        // Always consume at least 1 energy for movement
        return Math.max(1, energyCost);
    }

    private void handlePlayerInput(float delta) {
        if (justTransitionedToVillage) {
            System.out.println("Skipping input processing due to justTransitionedToVillage flag");
            justTransitionedToVillage = false;
            return;
        }

        if (player.hasCollapsed()) {
            return;
        }

        float newX = player.getPosX();
        float newY = player.getPosY();
        boolean moved = false;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            newX -= player.getSpeed();
            if (isWalkable(newX / 60, newY / 60)) {
                player.setPosX(newX);
                facing = Dir.LEFT;
                moved = true;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            newX += player.getSpeed();
            if (isWalkable(newX / 60, newY / 60)) {
                player.setPosX(newX);
                facing = Dir.RIGHT;
                moved = true;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            newY += player.getSpeed();
            if (isWalkable(newX / 60, newY / 60)) {
                player.setPosY(newY);
                facing = Dir.UP;
                moved = true;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            newY -= player.getSpeed();
            if (isWalkable(newX / 60, newY / 60)) {
                player.setPosY(newY);
                facing = Dir.DOWN;
                moved = true;
            }
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (player.getCurrentTool() != null && !isUsingTool && !player.hasCollapsed()) {
                triggerToolUse();
            }
        }

        if (moved && !player.isEnergyUnlimited()) {
            int energyCost = calculateMovementEnergyCost();
            System.out.println("Movement detected - Energy cost: " + energyCost + ", Current energy: " + player.getEnergy());

            if (player.getEnergy() >= energyCost) {
                player.decreaseEnergy(energyCost);
                System.out.println("Player moved - Energy consumed: " + energyCost + ", Remaining energy: " + player.getEnergy());
                sendMovementToServer();

                if (App.getGame() != null) {
                    App.getGame().checkAndAdvanceTurnIfEnergyDepleted();
                }
            }
            else {
                System.out.println("Not enough energy to move! Energy: " + player.getEnergy() + ", Required: " + energyCost);
                player.setPosX(player.getPosX());
                player.setPosY(player.getPosY());

                if (player.getEnergy() < energyCost) {
                    player.setCollapsed(true);
                    System.out.println("Player has collapsed due to insufficient energy!");
                }

                if (App.getGame() != null) {
                    App.getGame().checkAndAdvanceTurnIfEnergyDepleted();
                }
            }
        }
        else if (moved && player.isEnergyUnlimited()) {
            System.out.println("Player moved - Energy unlimited mode");
            sendMovementToServer();
        }

        this.isMoving = moved;

        updateCurrentAnimation();
    }

    private boolean isWalkable(float x, float y) {
        // Prevent immediate reset after transition
        if (justTransitionedToVillage) {
            System.out.println("isWalkable called during transition, returning false");
            justTransitionedToVillage = false;
            return false;
        }

        int tileX = Math.round(x);
        int tileY = Math.round(y);

        // Only log if we're in village and there's an issue
        if (player.getIsInVillage() && (tileX < GameMap.VILLAGE_X || tileX >= GameMap.VILLAGE_X + Village.width ||
            tileY < GameMap.VILLAGE_Y || tileY >= GameMap.VILLAGE_Y + Village.height)) {
            System.out.println("WARNING: Player trying to move outside village bounds: (" + x + ", " + y + ") -> tile: (" + tileX + ", " + tileY + ")");
        }

        if (player.getIsInVillage()) {
            return handleVillageWalkable(tileX, tileY);
        } else {
            return handleFarmWalkable(tileX, tileY);
        }
    }

    private boolean handleVillageWalkable(int tileX, int tileY) {
        // Use the player's location instead of calculating from position to avoid coordinate drift
        int currentTileX = player.getLocation().getX();
        int currentTileY = player.getLocation().getY();

        // Only log if there's a mismatch between position and location
        if (currentTileX != player.getLocation().getX() || currentTileY != player.getLocation().getY()) {
            System.out.println("WARNING: Position/Location mismatch - Position: (" + player.getPosX() + "," + player.getPosY() + ") -> Tile: (" + currentTileX + "," + currentTileY + ")");
            System.out.println("Player location: (" + player.getLocation().getX() + "," + player.getLocation().getY() + ")");
        }

        // Check if current position is in village area
        if (currentTileX >= GameMap.VILLAGE_X && currentTileX < GameMap.VILLAGE_X + Village.width &&
            currentTileY >= GameMap.VILLAGE_Y && currentTileY < GameMap.VILLAGE_Y + Village.height) {

            // Check if target position is in village area
            if (tileX >= GameMap.VILLAGE_X && tileX < GameMap.VILLAGE_X + Village.width &&
                tileY >= GameMap.VILLAGE_Y && tileY < GameMap.VILLAGE_Y + Village.height) {

                // Check for transition to farm (when trying to exit village)
                if (tileX <= GameMap.VILLAGE_X + VILLAGE_TRANSITION_THRESHOLD ||
                    tileX >= GameMap.VILLAGE_X + Village.width - VILLAGE_TRANSITION_THRESHOLD) {
                    handleVillageToFarmTransition(tileX, tileY);
                    return true;
                }

                // Convert global coordinates to local village coordinates
                Village village = gameMap.getVillage();
                int localVillageX = tileX - GameMap.VILLAGE_X;
                int localVillageY = tileY - GameMap.VILLAGE_Y;

                // Check if local coordinates are within village bounds
                if (village != null && village.contains(localVillageX, localVillageY)) {
                    Location loc = village.getTiles()[localVillageX][localVillageY];
                    return loc != null && loc.getTile().isWalkable();
                }
                return true;
            } else {
                // Target position is outside village - check if it's a farm coordinate and transition
                System.out.println("Target position outside village - checking if farm coordinate");
                // Check if the target coordinates are within any farm bounds
                if (isFarmCoordinate(tileX, tileY)) {
                    System.out.println("Farm coordinate detected, triggering transition");
                    handleVillageToFarmTransition(tileX, tileY);
                    return true;
                }
                System.out.println("Not a farm coordinate, movement blocked");
                return false;
            }
        } else {
            handleInvalidVillagePosition(currentTileX, currentTileY, tileX, tileY);
            return false;
        }
    }

    private boolean handleFarmWalkable(int tileX, int tileY) {
        Farm currentFarm = player.getCurrentFarm();
        if (currentFarm == null) return false;

        // Debug farm edge transitions (only when close to edge)
        if (tileX >= Farm.width - FARM_EDGE_DEBUG_THRESHOLD || tileX <= FARM_EDGE_DEBUG_THRESHOLD) {
            System.out.println("Near farm edge - Farm index: " + currentFarm.getFarmIndex() +
                ", tileX: " + tileX + ", Farm.width: " + Farm.width);
        }

        if (System.currentTimeMillis() - lastTransitionTime > TRANSITION_COOLDOWN_MS) {
            boolean shouldTransition = false;
            switch (currentFarm.getFarmIndex()) {
                case 0: case 1:
                    shouldTransition = tileX >= Farm.width - VILLAGE_TRANSITION_THRESHOLD;
                    break;
                case 2: case 3:
                    shouldTransition = tileX <= VILLAGE_TRANSITION_THRESHOLD;
                    break;
            }

            if (shouldTransition) {
                lastTransitionTime = System.currentTimeMillis();
                // Set the flag immediately to prevent movement logic from interfering
                justTransitionedToVillage = true;
                handleAreaTransition(tileX, tileY);
                // Return false to prevent movement logic from calling setPosX
                return false;
            }
        }

        if (farm.contains(tileX, tileY)) {
            Location loc = farm.getItem(tileX, tileY);
            return loc != null && loc.getTile().isWalkable();
        }
        return false;
    }

    private boolean isFarmCoordinate(int tileX, int tileY) {
        // Check if coordinates are within any farm bounds
        // Farm 0: (0, 0) to (77, 77) - Top-Left
        // Farm 1: (0, 78) to (77, 155) - Bottom-Left
        // Farm 2: (156, 0) to (233, 77) - Top-Right
        // Farm 3: (156, 78) to (233, 155) - Bottom-Right

        // Check if coordinates are within any of the farm areas
        boolean inFarm0 = (tileX >= 0 && tileX < Farm.width && tileY >= 0 && tileY < Farm.height);
        boolean inFarm1 = (tileX >= 0 && tileX < Farm.width && tileY >= Farm.height && tileY < Farm.height * 2);
        boolean inFarm2 = (tileX >= Farm.width * 2 && tileX < Farm.width * 3 && tileY >= 0 && tileY < Farm.height);
        boolean inFarm3 = (tileX >= Farm.width * 2 && tileX < Farm.width * 3 && tileY >= Farm.height && tileY < Farm.height * 2);

        System.out.println("Farm coordinate check - tileX: " + tileX + ", tileY: " + tileY);
        System.out.println("Farm 0: " + inFarm0 + ", Farm 1: " + inFarm1 + ", Farm 2: " + inFarm2 + ", Farm 3: " + inFarm3);

        return inFarm0 || inFarm1 || inFarm2 || inFarm3;
    }

    private void handleInvalidVillagePosition(int currentTileX, int currentTileY, int targetTileX, int targetTileY) {
        System.out.println("Village walkable check - Position: (" + player.getPosX() + "," + player.getPosY() + ") -> Tile: (" + currentTileX + "," + currentTileY + ")");
        System.out.println("Player location: (" + player.getLocation().getX() + "," + player.getLocation().getY() + ")");
        System.out.println("Player isInVillage: " + player.getIsInVillage());

        // Check if the player is actually in a valid village position
        if (currentTileX >= GameMap.VILLAGE_X && currentTileX < GameMap.VILLAGE_X + Village.width &&
            currentTileY >= GameMap.VILLAGE_Y && currentTileY < GameMap.VILLAGE_Y + Village.height) {
            // Player is actually in village, don't reset
            System.out.println("Player is in valid village position, not resetting");
            return;
        }

        // Only reset if the player is truly outside the village bounds and not just trying to move
        // Check if the target position is also outside village bounds
        if (targetTileX >= GameMap.VILLAGE_X && targetTileX < GameMap.VILLAGE_X + Village.width &&
            targetTileY >= GameMap.VILLAGE_Y && targetTileY < GameMap.VILLAGE_Y + Village.height) {
            // Target is within village bounds, don't reset - this might be a coordinate calculation issue
            System.out.println("Target position is within village bounds, not resetting");
            return;
        }

        // Additional check: if player thinks they're in village but coordinates are wrong,
        // try to fix the coordinates instead of resetting
        if (player.getIsInVillage()) {
            System.out.println("Player thinks they're in village but coordinates are wrong. Attempting to fix...");
            // Try to place player at a safe location within village bounds
            int safeVillageX = GameMap.VILLAGE_X + Village.width / 2;
            int safeVillageY = GameMap.VILLAGE_Y + Village.height / 2;
            player.setPosX(safeVillageX * 60);
            player.setPosY(safeVillageY * 60);
            player.setLocation(new Location(safeVillageX, safeVillageY, TileType.VILLAGE));
            System.out.println("Fixed player position to safe village location: (" + safeVillageX + ", " + safeVillageY + ")");
            return;
        }

        System.out.println("ERROR: Invalid village position! Resetting player...");
        System.out.println("Current: tileX=" + currentTileX + ", tileY=" + currentTileY);
        System.out.println("Target: tileX=" + targetTileX + ", tileY=" + targetTileY);
        System.out.println("Player position: posX=" + player.getPosX() + ", posY=" + player.getPosY());

        player.setIsInVillage(false);
        player.setCurrentFarm(gameMap.getFarmByIndex(1));
        player.setPosX(10 * 60);
        player.setPosY(10 * 60);
        player.setLocation(new Location(10, 10, TileType.Dirt));
    }

    private void handleAreaTransition(int tileX, int tileY) {
        System.out.println("handleAreaTransition called with tileX: " + tileX + ", tileY: " + tileY);
        System.out.println("Before transition - Player in village: " + player.getIsInVillage() +
            ", posX: " + player.getPosX() + ", posY: " + player.getPosY());

        Farm currentFarm = player.getCurrentFarm();
        if (currentFarm == null) {
            System.out.println("ERROR: currentFarm is null!");
            return;
        }

        // Calculate proper village entrance coordinates (local village coordinates)
        int localVillageX, localVillageY;

        // For farms 0 and 1 (right edge), enter village from left side
        if (currentFarm.getFarmIndex() == 0 || currentFarm.getFarmIndex() == 1) {
            localVillageX = 10; // Local village coordinate (10 tiles from left edge)
            localVillageY = (currentFarm.getFarmIndex() == 0 ? 10 : Village.height - 10); // Top or bottom
        } else {
            // For farms 2 and 3 (left edge), enter village from right side
            localVillageX = Village.width - 10; // Local village coordinate (10 tiles from right edge)
            localVillageY = (currentFarm.getFarmIndex() == 2 ? 10 : Village.height - 10); // Top or bottom
        }

        // Convert to global coordinates for positioning
        int globalVillageX = GameMap.VILLAGE_X + localVillageX;
        int globalVillageY = GameMap.VILLAGE_Y + localVillageY;

        System.out.println("Coordinate conversion: farm(" + tileX + "," + tileY + ") -> local village(" + localVillageX + "," + localVillageY + ") -> global(" + globalVillageX + "," + globalVillageY + ")");

        // Set the flag immediately to prevent movement logic from interfering
        justTransitionedToVillage = true;

        player.setIsInVillage(true);

        // Set position using global coordinates (multiply by tile size)
        player.setPosX(globalVillageX * 60);
        player.setPosY(globalVillageY * 60);

        // Set location using global coordinates to match the position
        player.setLocation(new Location(globalVillageX, globalVillageY, TileType.VILLAGE));

        System.out.println("Position set to: (" + player.getPosX() + ", " + player.getPosY() + ")");
        System.out.println("Location set to: (" + player.getLocation().getX() + ", " + player.getLocation().getY() + ")");

        System.out.println("Transition completed, player should be at local village: (" + localVillageX + ", " + localVillageY + ")");
        System.out.println("Player actual position: (" + player.getPosX() + ", " + player.getPosY() + ")");
        System.out.println("Player location: (" + player.getLocation().getX() + ", " + player.getLocation().getY() + ")");

        System.out.println("TRANSITION COMPLETED: Player walked to village at local village coordinates: (" + localVillageX + ", " + localVillageY + ")");
        System.out.println("Player is now in village: " + player.getIsInVillage());
        System.out.println("Player position: (" + player.getPosX() + ", " + player.getPosY() + ")");
        System.out.println("Verification - Player should now be at global coordinates: (" + (player.getPosX() / 60) + ", " + (player.getPosY() / 60) + ")");

        // Add a check to see if position changes after setting
        System.out.println("Final position check - posX: " + player.getPosX() + ", posY: " + player.getPosY());
        System.out.println("Final location check - x: " + player.getLocation().getX() + ", y: " + player.getLocation().getY());
    }

    public void triggerToolUse() {
        if (player.getCurrentTool() != null && !player.hasCollapsed() && !isUsingTool) {
            isUsingTool = true;
            toolUseTime = 0f;
            loadToolAnimation();
        }
    }

//    private void triggerToolUse() {
//        isUsingTool = true;
//        toolUseTime = 0f;
//        loadToolAnimation();
//
//        int energyCost = player.getCurrentTool().getEnergyCost();
//        if (!player.isEnergyUnlimited()) {
//            player.decreaseEnergy(energyCost);
//        }
//
//        if (App.getGame() != null && App.getGame().isMultiplayer) {
//            NetworkClient.getInstance().sendToolUse(facing.toString());
//        }
//    }

    private void loadToolAnimation() {
        toolUseFrames.clear();
        Tool tool = player.getCurrentTool();
        if (tool == null) return;

        String toolName = getToolSpriteName(tool);
        String direction = getDirectionString(facing);

        try {
            String path;
            if (tool.getMaterial() == BASIC) {
                path = String.format("sprites/player/%s_%s.png", toolName, direction);
            } else {
                path = String.format("sprites/player/%s_%s.png", toolName, direction);
            }

            Gdx.app.log("ToolLoad", "Loading tool sprite: " + path);

            Texture frameTex = new Texture(Gdx.files.internal(path));
            TextureRegion frame = new TextureRegion(frameTex);

            if (facing == Dir.LEFT && tool.getMaterial() != BASIC) {
                frame.flip(true, false);
            }

            toolUseFrames.add(frame);
            currentToolAnim = new Animation<>(TOOL_USE_DURATION, toolUseFrames);
        }
        catch (Exception e) {
            Gdx.app.error("ToolAnimation", "Error loading tool sprite", e);
            isUsingTool = false;
        }
    }

    private String getToolSpriteName(Tool tool) {
        if (tool.getMaterial() == Tool.ToolMaterial.BASIC) {
            return tool.getType().toString().toLowerCase();
        }

        String material = tool.getMaterial().toString().toLowerCase();
        String type = tool.getType().toString().toLowerCase();
        return String.format("%s_%s", material, type);
    }

    private String getDirectionString(Dir direction) {
        switch (direction) {
            case LEFT: return "left";
            case RIGHT: return "right";
            case UP: return "up";
            case DOWN: return "down";
            default: return "down";
        }
    }

    private void updateCurrentAnimation() {
        if (player.hasCollapsed()) {
            currentAnim = collapsedAnim;
            return;
        }

        if (isUsingTool) {
            switch (facing) {
                case UP: currentAnim = walkUp; break;
                case DOWN: currentAnim = walkDown; break;
                case LEFT: currentAnim = walkLeft; break;
                case RIGHT: currentAnim = walkRight; break;
            }
        }
        else if (player.getCurrentItem() != null) {
            if (this.isMoving) {
                switch (facing) {
                    case UP: currentAnim = walkUp; break;
                    case DOWN: currentAnim = walkDown; break;
                    case LEFT: currentAnim = walkLeft; break;
                    case RIGHT: currentAnim = walkRight; break;
                }
            }
            else {
                switch (facing) {
                    case UP: currentAnim = itemUp; break;
                    case DOWN: currentAnim = itemDown; break;
                    case LEFT: currentAnim = itemLeft; break;
                    case RIGHT: currentAnim = itemRight; break;
                }
            }
        }
        else {
            switch (facing) {
                case UP: currentAnim = walkUp; break;
                case DOWN: currentAnim = walkDown; break;
                case LEFT: currentAnim = walkLeft; break;
                case RIGHT: currentAnim = walkRight; break;
            }
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion playerFrame = currentAnim.getKeyFrame(stateTime, true);
        batch.draw(playerFrame, player.getPosX(), player.getPosY(), RENDER_W, RENDER_H);

        if (isUsingTool && currentToolAnim != null) {
            renderToolUse(batch);
        }

        else if (player.getCurrentTool() != null && !isMoving && player.getCurrentItem() == null) {
            renderHeldTool(batch);
        }
    }

    private void renderHeldTool(SpriteBatch batch) {
        switch (facing) {
            case UP:
                batch.draw(itemUp.getKeyFrame(0), player.getPosX(), player.getPosY(), RENDER_W, RENDER_H);
                break;
            case DOWN:
                batch.draw(itemDown.getKeyFrame(0), player.getPosX(), player.getPosY(), RENDER_W, RENDER_H);
                break;
            case LEFT:
                batch.draw(itemLeft.getKeyFrame(0), player.getPosX(), player.getPosY(), RENDER_W, RENDER_H);
                break;
            case RIGHT:
                batch.draw(itemRight.getKeyFrame(0), player.getPosX(), player.getPosY(), RENDER_W, RENDER_H);
                break;
        }
    }

    private void renderToolUse(SpriteBatch batch) {
        if (currentToolAnim == null) return;

        TextureRegion toolFrame = currentToolAnim.getKeyFrame(toolUseTime, false);
        float x = player.getPosX();
        float y = player.getPosY();

        switch (facing) {
            case UP:
                x += RENDER_W/2 - toolFrame.getRegionWidth()/2;
                y += RENDER_H - 10;
                break;
            case DOWN:
                x += RENDER_W/2 - toolFrame.getRegionWidth()/2;
                y -= 10;
                break;
            case LEFT:
                x -= 15;
                y += RENDER_H/2 - toolFrame.getRegionHeight()/2;
                break;
            case RIGHT:
                x += RENDER_W - 15;
                y += RENDER_H/2 - toolFrame.getRegionHeight()/2;
                break;
        }

        batch.draw(toolFrame, x, y);
    }

    private String getMaterialFolder(Tool.ToolMaterial material) {
        switch (material) {
            case BASIC:
            case COPPER: return "copper";
            case IRON: return "steel";
            case GOLD: return "gold";
            case IRIDIUM: return "iridium";
            default: return "copper";
        }
    }

    public void confirmTransition() {
        if (showTransitionDialog) {
            showTransitionDialog = false;
            transitionMessage = "";

            // Convert farm coordinates to village coordinates
            Farm currentFarm = player.getCurrentFarm();
            if (currentFarm == null) {
                System.out.println("ERROR: currentFarm is null!");
                return;
            }

            int villageX, villageY;

            // Transport player to middle of village to avoid coordinate issues
            villageX = Village.width / 2; // Middle of village
            villageY = Village.height / 2; // Middle of village

            player.setIsInVillage(true);
            player.setLocation(new Location(GameMap.VILLAGE_X + villageX, GameMap.VILLAGE_Y + villageY, TileType.VILLAGE));

            // Update player's visual position to match global village coordinates
            player.setPosX((GameMap.VILLAGE_X + villageX) * 60);
            player.setPosY((GameMap.VILLAGE_Y + villageY) * 60);

            // Set the flag to skip the next movement frame
            justTransitionedToVillage = true;

            System.out.println("TRANSITION CONFIRMED: Player walked to village at village coordinates: (" + villageX + ", " + villageY + ")");
            System.out.println("Player is now in village: " + player.getIsInVillage());
            System.out.println("Player position: (" + player.getPosX() + ", " + player.getPosY() + ")");
        }
    }

    public void cancelTransition() {
        showTransitionDialog = false;
        transitionMessage = "";
    }

    public boolean isShowingTransitionDialog() {
        return showTransitionDialog;
    }

    public String getTransitionMessage() {
        return transitionMessage;
    }

    private void handleVillageToFarmTransition(int tileX, int tileY) {
        // Convert global coordinates to local village coordinates
        int localVillageX = tileX - GameMap.VILLAGE_X;
        int localVillageY = tileY - GameMap.VILLAGE_Y;

        // First, try to get the player's current farm (the one they own)
        Farm currentFarm = player.getCurrentFarm();
        int farmIndex = -1;
        int farmX, farmY;

        // If the player has a current farm, use that one
        if (currentFarm != null) {
            farmIndex = currentFarm.getFarmIndex();
            System.out.println("Using player's current farm: " + farmIndex);
        } else {
            // Fallback: determine which farm to transition to based on position
            if (localVillageX <= VILLAGE_TRANSITION_THRESHOLD) {
                // Exit to left farms (0 or 1)
                if (localVillageY < Village.height / 2) {
                    farmIndex = 0; // Top-Left farm
                } else {
                    farmIndex = 1; // Bottom-Left farm
                }
            } else {
                // Exit to right farms (2 or 3)
                if (localVillageY < Village.height / 2) {
                    farmIndex = 2; // Top-Right farm
                } else {
                    farmIndex = 3; // Bottom-Right farm
                }
            }
            System.out.println("No current farm, using position-based farm: " + farmIndex);
        }

        // Get the target farm
        Farm targetFarm = App.getGame().getGameMap().getFarmByIndex(farmIndex);
        if (targetFarm == null) {
            System.out.println("ERROR: Target farm is null for index: " + farmIndex);
            return;
        }

        // Set player to farm
        player.setIsInVillage(false);
        player.setCurrentFarm(targetFarm);

        // Calculate farm entrance position based on farm index
        switch (farmIndex) {
            case 0: // Top-Left farm - enter at right edge
                farmX = Farm.width - 5;
                farmY = 5;
                break;
            case 1: // Bottom-Left farm - enter at right edge
                farmX = Farm.width - 5;
                farmY = Farm.height - 5;
                break;
            case 2: // Top-Right farm - enter at left edge
                farmX = 5;
                farmY = 5;
                break;
            case 3: // Bottom-Right farm - enter at left edge
                farmX = 5;
                farmY = Farm.height - 5;
                break;
            default:
                System.out.println("ERROR: Invalid farm index: " + farmIndex);
                return;
        }

        // Calculate global farm coordinates based on farm index
        int globalFarmX, globalFarmY;
        switch (farmIndex) {
            case 0: // Top-Left farm
                globalFarmX = farmX;
                globalFarmY = farmY;
                break;
            case 1: // Bottom-Left farm
                globalFarmX = farmX;
                globalFarmY = 78 + farmY;
                break;
            case 2: // Top-Right farm
                globalFarmX = 156 + farmX;
                globalFarmY = farmY;
                break;
            case 3: // Bottom-Right farm
                globalFarmX = 156 + farmX;
                globalFarmY = 78 + farmY;
                break;
            default:
                System.out.println("ERROR: Invalid farm index in global calculation: " + farmIndex);
                return;
        }

        player.setLocation(new Location(globalFarmX, globalFarmY, TileType.Dirt));

        // Update player's visual position to match global farm coordinates
        player.setPosX(globalFarmX * 60);
        player.setPosY(globalFarmY * 60);

        System.out.println("SUCCESS: Player walked to farm " + farmIndex + " at global coordinates: (" + globalFarmX + ", " + globalFarmY + ")");
        System.out.println("Player is now in village: " + player.getIsInVillage());
        System.out.println("Player current farm: " + (player.getCurrentFarm() != null ? player.getCurrentFarm().getFarmIndex() : "null"));
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Update the player reference to follow the current player
     * This should be called when the turn advances
     */
    public void updatePlayer(Player newPlayer) {
        // Update the player reference
        // Note: We can't change the final player field, so we need to create a new PlayerController
        // This method is kept for compatibility but the actual update should be done by creating a new PlayerController
        System.out.println("PlayerController: Attempting to update player reference");
        System.out.println("PlayerController: Current player: " + (player != null ? player.getUser().getUsername() : "null"));
        System.out.println("PlayerController: New player: " + (newPlayer != null ? newPlayer.getUser().getUsername() : "null"));
    }

    public TextureRegion getCurrentFrame() {
        return currentAnim.getKeyFrame(stateTime, true);
    }

    public Dir getFacing() {
        return facing;
    }

    public void renderNickname(SpriteBatch batch, Player currentPlayer, Color lightingColor) {
        if (player == null || player.getUser() == null || nicknameFont == null || skin == null) {
            return;
        }

        String nickname = player.getUser().getNickname();
        if (nickname == null || nickname.trim().isEmpty()) {
            nickname = player.getUser().getUsername(); // Fallback to username
        }

        if (nickname == null || nickname.trim().isEmpty()) {
            return; // No nickname to display
        }

        float playerX = player.getPosX();
        float playerY = player.getPosY();

        // Calculate nickname position (centered above player head)
        float nicknameWidth = nicknameFont.draw(batch, nickname, 0, 0).width;
        float nicknameX = playerX + 30f - (nicknameWidth / 2f); // Center above player (player width is ~60)
        float nicknameY = playerY + NICKNAME_OFFSET_Y;

        // Determine text color
        Color textColor = (player.equals(currentPlayer)) ? CURRENT_PLAYER_TEXT_COLOR : NICKNAME_TEXT_COLOR;

        // Apply lighting to text color
        Color finalTextColor = new Color(textColor);
        finalTextColor.r *= lightingColor.r;
        finalTextColor.g *= lightingColor.g;
        finalTextColor.b *= lightingColor.b;

        // Draw nickname text
        Color originalColor = batch.getColor().cpy();
        batch.setColor(finalTextColor);
        nicknameFont.draw(batch, nickname, nicknameX, nicknameY);

        // Reset batch color
        batch.setColor(originalColor);
    }

    public void dispose() {
        if (nicknameFont != null) {
            nicknameFont.dispose();
        }
    }

    private void renderToolSprite() {
        Tool tool = player.getCurrentTool();
        if (tool == null) return;

        // Get the appropriate tool sprite based on tool type and direction
        String spritePath = getToolSpritePath(tool, facing);

        try {
            Texture toolTexture = new Texture(Gdx.files.internal(spritePath));
            float playerX = player.getPosX();
            float playerY = player.getPosY();

            // Position the tool above the player's head
            float toolX = playerX + RENDER_W / 2 - toolTexture.getWidth() / 2; // Center horizontally
            float toolY = playerY + RENDER_H + 10; // Position above the player with some offset

            // Scale factor to make tools more visible
            float scaleX = 3.0f;
            float scaleY = 3.0f;
            float scaledWidth = toolTexture.getWidth() * scaleX;
            float scaledHeight = toolTexture.getHeight() * scaleY;

            // Adjust position for scaled size
            toolX = playerX + RENDER_W / 2 - scaledWidth / 2;

            // For left direction, flip the sprite horizontally
            boolean flipHorizontally = (facing == Dir.LEFT);

            // Draw the tool sprite above the player's head
            if (flipHorizontally) {
                // For left direction, draw flipped
                Main.getBatch().draw(
                    toolTexture,
                    toolX + scaledWidth, // Adjust X position for flip
                    toolY,
                    -scaledWidth, // Negative width for flip
                    scaledHeight
                );
            } else {
                // Normal drawing with scaling
                Main.getBatch().draw(
                    toolTexture,
                    toolX,
                    toolY,
                    scaledWidth,
                    scaledHeight
                );
            }

            toolTexture.dispose();
        } catch (Exception e) {
            System.out.println("Warning: Could not load tool sprite: " + spritePath + " - " + e.getMessage());
        }
    }



    private String getToolSpritePath(Tool tool, Dir direction) {
        String toolType = tool.getType().toString().toLowerCase();
        String material = tool.getMaterial().toString().toLowerCase();
        String directionStr = direction.toString().toLowerCase();

        // Map tool types to sprite names and base paths
        String toolName;
        String basePath;
        switch (tool.getType()) {
            case AXE:
                toolName = "Axe";
                basePath = "content/Tools/Axe";
                break;
            case PICKAXE:
                toolName = "Pickaxe";
                basePath = "content/Tools/Pickaxe";
                break;
            case WATERING_CAN:
                toolName = "Watering_Can";
                basePath = "content/Tools/Watering_Can";
                break;
            case HOE:
                toolName = "Hoe";
                basePath = "content/Tools/Hoe";
                break;
            default:
                toolName = "Axe";
                basePath = "content/Tools/Axe";
                break;
        }

        // Determine material folder name
        String materialFolder;
        switch (tool.getMaterial()) {
            case BASIC:
                // Basic tools use copper sprites
                materialFolder = "copper";
                break;
            case COPPER:
                materialFolder = "copper";
                break;
            case IRON:
                materialFolder = "steel";
                break;
            case GOLD:
                materialFolder = "gold";
                break;
            case IRIDIUM:
                materialFolder = "iridium";
                break;
            default:
                materialFolder = "copper";
                break;
        }

        // Check if the tool has directional sprites (only Axe and Hoe have them)
        boolean hasDirectionalSprites = (tool.getType() == Tool.ToolType.AXE || tool.getType() == Tool.ToolType.HOE);

        String spritePath;
        if (hasDirectionalSprites) {
            // Use directional sprites for Axe and Hoe
            spritePath = String.format("%s/%s/%s.png", basePath, materialFolder, directionStr);

            // For left direction, we'll use right sprite and flip it
            if (direction == Dir.LEFT) {
                spritePath = String.format("%s/%s/right.png", basePath, materialFolder);
            }
        } else {
            // For other tools (Pickaxe, Watering_Can), use the main tool image
            String materialPrefix = "";
            switch (tool.getMaterial()) {
                case BASIC:
                    materialPrefix = "";
                    break;
                case COPPER:
                    materialPrefix = "Copper_";
                    break;
                case IRON:
                    materialPrefix = "Steel_";
                    break;
                case GOLD:
                    materialPrefix = "Gold_";
                    break;
                case IRIDIUM:
                    materialPrefix = "Iridium_";
                    break;
                default:
                    materialPrefix = "";
                    break;
            }
            spritePath = String.format("%s/%s%s.png", basePath, materialPrefix, toolName);
        }

        return spritePath;
    }

    private void sendMovementToServer() {
        System.out.println("🔍 DEBUG: sendMovementToServer() called");
        try {
            NetworkClient networkClient = NetworkClient.getInstance();
            System.out.println("🔍 DEBUG: NetworkClient: " + (networkClient != null));

            if (networkClient != null) {
                System.out.println("🔍 DEBUG: NetworkClient state: " + networkClient.getConnectionState());
                System.out.println("🔍 DEBUG: NetworkClient authenticated: " + networkClient.isAuthenticated());
            }

            if (App.getGame() != null) {
                System.out.println("🔍 DEBUG: Game multiplayer: " + App.getGame().isMultiplayer);
            } else {
                System.out.println("🔍 DEBUG: App.getGame() is null");
            }

            if (networkClient != null && App.getGame() != null && App.getGame().isMultiplayer) {
                // Check if we're authenticated and connected
                if (!networkClient.isAuthenticated()) {
                    System.out.println("❌ CLIENT: Not authenticated, cannot send movement");
                    return;
                }

                // Send both pixel coordinates and tile coordinates for better synchronization
                float x = player.getPosX();
                float y = player.getPosY();
                int tileX = Math.round(x / 60);
                int tileY = Math.round(y / 60);

                System.out.println("🎮 CLIENT: About to send movement - Position: (" + x + ", " + y + ") Tile: (" + tileX + ", " + tileY + ")");
                networkClient.sendPlayerMove(x, y);

                // Update player's location to match the movement
                player.setLocation(new Location(tileX, tileY, player.getLocation().getTile()));

                System.out.println("🎮 CLIENT: Sent movement update to server - Position: (" + x + ", " + y + ") Tile: (" + tileX + ", " + tileY + ")");
            } else {
                System.out.println("❌ CLIENT: Not sending movement to server - NetworkClient: " + (networkClient != null) +
                    ", Game: " + (App.getGame() != null) +
                    ", Multiplayer: " + (App.getGame() != null && App.getGame().isMultiplayer));
            }
        } catch (Exception e) {
            System.err.println("❌ CLIENT: Error sending movement to server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
