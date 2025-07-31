package org.example.client.controllers.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import org.example.client.Main;
import org.example.common.models.App;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.MapDetails.GameMap;
import org.example.common.models.MapDetails.Village;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Location;
import org.example.common.models.entities.Game;
import org.example.common.models.enums.Types.TileType;
import org.example.common.models.App;

public class PlayerController {
    private static final int FRAME_W = 16;
    private static final int FRAME_H = 32;
    private static final int RENDER_W = 48;
    private static final int RENDER_H = 96;
    private static final float FRAME_DURATION = 0.2f;
    private static final int VILLAGE_TRANSITION_THRESHOLD = 3;
    private static final int FARM_EDGE_DEBUG_THRESHOLD = 5;
    private static final int MOVEMENT_ENERGY_PERCENTAGE = 5; // 0.05% of current energy per movement (5/10000 = 0.05%)
    private static final int MIN_MOVEMENT_ENERGY_COST = 1; // Minimum 1 energy cost per movement
    private long lastTransitionTime = 0;
    private static final long TRANSITION_COOLDOWN_MS = 500;

    private final Player player;
    private final Farm farm;
    private final GameMap gameMap;
    private boolean transitionInProgress = false;

    private final Animation<TextureRegion> walkDown;
    private final Animation<TextureRegion> walkLeft;
    private final Animation<TextureRegion> walkRight;
    private final Animation<TextureRegion> walkUp;

    private Animation<TextureRegion> currentAnim;
    private float stateTime = 0f;

    private enum Dir {DOWN, LEFT, RIGHT, UP}

    private Dir facing = Dir.DOWN;

    private float toolAnimTime = 0f;
    private boolean toolSwinging = false;
    private String lastToolDirection = "down";
    private float lastMouseX = 0f;
    private float lastMouseY = 0f;

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

        Texture sheet = player.getTextureSheet();
        TextureRegion[][] grid = TextureRegion.split(sheet, FRAME_W, FRAME_H);

        walkDown = buildAnim(grid[0]);
        walkLeft = buildAnim(grid[3]);
        walkRight = buildAnim(grid[1]);
        walkUp = buildAnim(grid[2]);

        currentAnim = walkDown;
    }

    public PlayerController(Player player, Farm farm, Skin skin) {
        this.player = player;
        this.farm = farm;
        this.gameMap = App.getGame().getGameMap();
        this.skin = skin;

        System.out.println("DEBUG: PlayerController constructor - Loading texture sheet for player: " + player.getUser().getUsername());
        Texture sheet = player.getTextureSheet();
        if (sheet == null) {
            System.err.println("WARNING: Player texture sheet is null, using fallback texture");
            try {
                sheet = new Texture(Gdx.files.internal("sprites/Alex.png"));
                System.out.println("DEBUG: Successfully loaded fallback texture");
            } catch (Exception e) {
                System.err.println("ERROR: Failed to load fallback texture: " + e.getMessage());
                throw new RuntimeException("Failed to load player texture", e);
            }
        }

        System.out.println("DEBUG: Creating texture regions from sheet...");
        TextureRegion[][] grid = TextureRegion.split(sheet, FRAME_W, FRAME_H);

        System.out.println("DEBUG: Building animations...");
        walkDown = buildAnim(grid[0]);
        walkLeft = buildAnim(grid[3]);
        walkRight = buildAnim(grid[1]);
        walkUp = buildAnim(grid[2]);

        currentAnim = walkDown;

        System.out.println("DEBUG: Initializing nickname font...");
        initializeNicknameFont();
        System.out.println("DEBUG: PlayerController constructor completed successfully");
    }

    private static Animation<TextureRegion> buildAnim(TextureRegion[] row) {
        Array<TextureRegion> frames = new Array<>(3);
        for (int i = 0; i < 3; i++) {
            frames.add(row[i]);
        }
        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP_PINGPONG);
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

        stateTime += delta;

        TextureRegion frame = currentAnim.getKeyFrame(stateTime, true);
        Main.getBatch().draw(
            frame,
            player.getPosX(),
            player.getPosY(),
            RENDER_W,
            RENDER_H
        );

        // Draw tool if equipped
        if (player.getCurrentTool() != null) {
            Texture toolTexture = new Texture(player.getCurrentTool().getImageFilepath());
            float playerX = player.getPosX();
            float playerY = player.getPosY();
            float centerX = playerX + RENDER_W / 2f;
            float centerY = playerY + RENDER_H / 2f;
            float toolW = 32, toolH = 32;
            // Set origin to handle (bottom-middle)
            float originX = toolW / 2f;
            float originY = toolH * 0.85f;
            // Calculate angle to mouse
            float dx = lastMouseX - centerX;
            float dy = lastMouseY - centerY;
            float angle = (float)Math.toDegrees(Math.atan2(dy, dx));
            // Swing effect
            float swingDuration = 0.18f;
            float swingArc = 60f; // degrees
            float finalAngle = angle;
            if (toolSwinging) {
                float swingProgress = Math.min(toolAnimTime / swingDuration, 1f);
                // Animate from -swingArc to +swingArc around the mouse direction
                float swingOffset = (float)Math.sin(Math.PI * swingProgress - Math.PI/2) * swingArc;
                finalAngle = angle + swingOffset;
                toolAnimTime += Gdx.graphics.getDeltaTime();
                if (toolAnimTime > swingDuration) toolSwinging = false;
            }
            // Mirror if mouse is to the left
            boolean flip = (angle > 90f || angle < -90f);
            Main.getBatch().draw(
                toolTexture,
                centerX - originX, centerY - originY,
                originX, originY,
                toolW, toolH,
                1f, 1f,
                finalAngle,
                0, 0,
                toolTexture.getWidth(), toolTexture.getHeight(),
                flip, false
            );
        }
    }


    private int calculateMovementEnergyCost() {
        // Use a fixed energy cost for movement instead of percentage-based calculation
        // This prevents the issue where players with high energy can't move due to calculation errors
        return 5; // Fixed 5 energy cost per movement
    }

    private void handlePlayerInput(float delta) {
        if (justTransitionedToVillage) {
            System.out.println("Skipping input processing due to justTransitionedToVillage flag");
            // Keep the flag for one more frame to ensure no movement
            justTransitionedToVillage = false;
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
            if (isWalkable(newX /60, newY / 60)) {
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

        // Consume energy when player moves
        if (moved && !player.isEnergyUnlimited()) {
            int energyCost = calculateMovementEnergyCost();
            System.out.println("Movement detected - Energy cost: " + energyCost + ", Current energy: " + player.getEnergy());

            if (player.canUseEnergy(energyCost)) {
                player.decreaseEnergy(energyCost);
                // Update energy used in turn
                player.addEnergyUsedInTurn(energyCost);
                System.out.println("Player moved - Energy consumed: " + energyCost + ", Remaining energy: " + player.getEnergy() + ", Energy used this turn: " + player.getEnergyUsedInTurn());

                // Check if player is out of energy and auto-advance turn if needed
                if (App.getGame() != null) {
                    App.getGame().checkAndAdvanceTurnIfEnergyDepleted();
                }
            } else {
                System.out.println("Not enough energy to move! Energy: " + player.getEnergy() + ", Required: " + energyCost + ", Energy used this turn: " + player.getEnergyUsedInTurn());
                // Revert the movement if not enough energy
                player.setPosX(player.getPosX());
                player.setPosY(player.getPosY());

                // Check if player is out of energy and auto-advance turn if needed
                if (App.getGame() != null) {
                    App.getGame().checkAndAdvanceTurnIfEnergyDepleted();
                }
            }
        } else if (moved && player.isEnergyUnlimited()) {
            System.out.println("Player moved - Energy unlimited mode");
        } else if (!moved) {
            System.out.println("No movement detected");
        }

        switch (facing) {
            case UP -> currentAnim = walkUp;
            case DOWN -> currentAnim = walkDown;
            case LEFT -> currentAnim = walkLeft;
            case RIGHT -> currentAnim = walkRight;
        }
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
}
