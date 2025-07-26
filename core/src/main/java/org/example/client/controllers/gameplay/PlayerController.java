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
import org.example.common.models.enums.Types.TileType;

public class PlayerController {

    private static final int FRAME_W = 16;
    private static final int FRAME_H = 32;
    private static final int RENDER_W = 48;
    private static final int RENDER_H = 96;
    private static final float FRAME_DURATION = 0.15f;

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

        Texture sheet = player.getTextureSheet();
        TextureRegion[][] grid = TextureRegion.split(sheet, FRAME_W, FRAME_H);

        walkDown = buildAnim(grid[0]);
        walkLeft = buildAnim(grid[3]);
        walkRight = buildAnim(grid[1]);
        walkUp = buildAnim(grid[2]);

        currentAnim = walkDown;

        initializeNicknameFont();
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

    private void handlePlayerInput(float delta) {
        // Manual reset key (R key)
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            System.out.println("MANUAL RESET TRIGGERED BY R KEY");
            player.setIsInVillage(false);
            player.setCurrentFarm(gameMap.getFarmByIndex(1));
            player.setPosX(10 * 60);
            player.setPosY(10 * 60);
            player.setLocation(new Location(10, 10, TileType.Dirt));
            System.out.println("Manual reset completed - Player in village: " + player.getIsInVillage());
            return; // Skip movement this frame
        }

        // Prevent movement for one frame after transition
        if (justTransitionedToVillage) {
            justTransitionedToVillage = false;
            return;
        }

        float newX = player.getPosX();
        float newY = player.getPosY();
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            newX -= player.getSpeed();
            if (isWalkable(newX / 60, newY / 60)) {
                player.setPosX(newX);
                facing = Dir.LEFT;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            newX += player.getSpeed();
            if (isWalkable(newX / 60, newY / 60)) {
                player.setPosX(newX);
                facing = Dir.RIGHT;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            newY += player.getSpeed();
            if (isWalkable(newX /60, newY / 60)) {
                player.setPosY(newY);
                facing = Dir.UP;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            newY -= player.getSpeed();
            if (isWalkable(newX / 60, newY / 60)) {
                player.setPosY(newY);
                facing = Dir.DOWN;
            }
        }

        // Update current animation based on facing direction
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
            justTransitionedToVillage = false;
            return false; // Skip this frame's movement check
        }

        int tileX = (int) x;
        int tileY = (int) y;

        // Debug output - only when near edges
        if (tileX >= Farm.width - 5 || tileX <= 5) {
            System.out.println("Near edge - Player in village: " + player.getIsInVillage() +
                              ", tileX: " + tileX + ", tileY: " + tileY +
                              ", current farm: " + (player.getCurrentFarm() != null ? player.getCurrentFarm().getFarmIndex() : "null"));
        }

        // Check if player is trying to move to village
        if (player.getIsInVillage()) {
            // Get current player position in tiles
            int currentTileX = (int) (player.getPosX() / 60);
            int currentTileY = (int) (player.getPosY() / 60);

            // Check if current position is within village boundaries
            if (currentTileX >= GameMap.VILLAGE_X && currentTileX < GameMap.VILLAGE_X + Village.width &&
                currentTileY >= GameMap.VILLAGE_Y && currentTileY < GameMap.VILLAGE_Y + Village.height) {

                // Player is in village, check if target position is also within village boundaries
                if (tileX >= GameMap.VILLAGE_X && tileX < GameMap.VILLAGE_X + Village.width &&
                    tileY >= GameMap.VILLAGE_Y && tileY < GameMap.VILLAGE_Y + Village.height) {

                    // Check if trying to exit village to farms
                    if (tileX <= GameMap.VILLAGE_X + 2 || tileX >= GameMap.VILLAGE_X + Village.width - 3) {
                        // Transition to farm
                        handleVillageToFarmTransition(tileX, tileY);
                        return true;
                    }

                    // Check if the tile is walkable in the village (convert to local coordinates)
                    Village village = gameMap.getVillage();
                    int localVillageX = tileX - GameMap.VILLAGE_X;
                    int localVillageY = tileY - GameMap.VILLAGE_Y;
                    if (village != null && localVillageX >= 0 && localVillageX < village.getTiles().length &&
                        localVillageY >= 0 && localVillageY < village.getTiles()[0].length) {
                        Location loc = village.getTiles()[localVillageX][localVillageY];
                        if (loc != null) {
                            return loc.getTile().isWalkable();
                        }
                    }
                    return true; // Default to walkable within village
                } else {
                    // Target position is outside village boundaries, don't allow movement
                    return false;
                }
            } else {
                System.out.println("ERROR: Player thinks they're in village but at invalid coordinates! Resetting to farm...");
                System.out.println("Current coordinates: tileX=" + currentTileX + ", tileY=" + currentTileY);
                System.out.println("Target coordinates: tileX=" + tileX + ", tileY=" + tileY);
                System.out.println("Current player position: posX=" + player.getPosX() + ", posY=" + player.getPosY());

                // Force a complete reset of player state
                player.setIsInVillage(false);
                player.setCurrentFarm(gameMap.getFarmByIndex(1)); // Force set to farm 1

                // Reset player position to a valid farm position
                player.setPosX(10 * 60); // Move to x=10 in farm
                player.setPosY(10 * 60); // Move to y=10 in farm
                player.setLocation(new Location(10, 10, TileType.Dirt));

                System.out.println("FORCE RESET COMPLETED:");
                System.out.println("- Player in village: " + player.getIsInVillage());
                System.out.println("- Player position: (" + player.getPosX() + ", " + player.getPosY() + ")");
                System.out.println("- Current farm: " + (player.getCurrentFarm() != null ? player.getCurrentFarm().getFarmIndex() : "null"));

                // Force a frame skip to prevent immediate re-entry
                return false; // Don't allow movement until next frame
            }
        } else {
            // Player is in farm, check if trying to exit to village FIRST
            Farm currentFarm = player.getCurrentFarm();
            if (currentFarm != null) {
                // Only debug when near transition zones
                if (tileX >= Farm.width - 5 || tileX <= 5) {
                    System.out.println("Checking farm transition - Farm index: " + currentFarm.getFarmIndex() +
                                      ", tileX: " + tileX + ", Farm.width: " + Farm.width);
                }

                // Check if player should transition to village based on farm-specific rules
                boolean shouldTransition = false;
                switch (currentFarm.getFarmIndex()) {
                    case 0: // Farm index 0 - exit at right edge
                        shouldTransition = tileX >= Farm.width - 3; // Within 3 tiles of right edge
                        break;
                    case 1: // Farm index 1 - exit at right edge
                        shouldTransition = tileX >= Farm.width - 3; // Within 3 tiles of right edge
                        break;
                    case 2: // Farm index 2 - exit at left edge
                        shouldTransition = tileX <= 2; // Within 3 tiles of left edge
                        break;
                    case 3: // Farm index 3 - exit at left edge
                        shouldTransition = tileX <= 2; // Within 3 tiles of left edge
                        break;
                }

                if (shouldTransition && !transitionInProgress) {
                    System.out.println("TRANSITION TRIGGERED: Farm " + currentFarm.getFarmIndex() + " to village!");
                    transitionInProgress = true;
                    // Transition to village
                    handleAreaTransition(tileX, tileY);
                    transitionInProgress = false;
                    return true;
                }
            }

            // Then check if position is within current farm boundaries
            if (farm.contains(tileX, tileY)) {
                Location loc = farm.getItem(tileX, tileY);
                return loc.getTile().isWalkable();
            }
        }

        return false;
    }

    private void handleAreaTransition(int tileX, int tileY) {
        System.out.println("handleAreaTransition called with tileX: " + tileX + ", tileY: " + tileY);
        System.out.println("Before transition - Player in village: " + player.getIsInVillage() +
                          ", posX: " + player.getPosX() + ", posY: " + player.getPosY());

        // Convert farm coordinates to village coordinates
        Farm currentFarm = player.getCurrentFarm();
        if (currentFarm == null) {
            System.out.println("ERROR: currentFarm is null!");
            return;
        }

        // Transport player to middle of village to avoid coordinate issues
        int villageX = Village.width / 2; // Middle of village
        int villageY = Village.height / 2; // Middle of village

        System.out.println("Coordinate conversion: farm(" + tileX + "," + tileY + ") -> village(" + villageX + "," + villageY + ")");

        player.setIsInVillage(true);
        player.setLocation(new Location(GameMap.VILLAGE_X + villageX, GameMap.VILLAGE_Y + villageY, TileType.VILLAGE));

        // Update player's visual position to match global village coordinates
        player.setPosX((GameMap.VILLAGE_X + villageX) * 60);
        player.setPosY((GameMap.VILLAGE_Y + villageY) * 60);

        // Set the flag to skip the next movement frame
        justTransitionedToVillage = true;

        System.out.println("TRANSITION COMPLETED: Player walked to village at village coordinates: (" + villageX + ", " + villageY + ")");
        System.out.println("Player is now in village: " + player.getIsInVillage());
        System.out.println("Player position: (" + player.getPosX() + ", " + player.getPosY() + ")");
        System.out.println("Verification - Player should now be at village coordinates: (" + (player.getPosX() / 60) + ", " + (player.getPosY() / 60) + ")");
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
        // Determine which farm to transition to based on position
        int farmIndex;
        int farmX, farmY;

        if (tileX <= 2) {
            // Exit to left farms (0 or 1)
            if (tileY < Village.height / 2) {
                farmIndex = 0; // Top-Left farm
                farmX = Farm.width - 5; // Near right edge of farm
                farmY = 5; // Near top of farm
            } else {
                farmIndex = 1; // Bottom-Left farm
                farmX = Farm.width - 5; // Near right edge of farm
                farmY = Farm.height - 5; // Near bottom of farm
            }
        } else {
            // Exit to right farms (2 or 3)
            if (tileY < Village.height / 2) {
                farmIndex = 2; // Top-Right farm
                farmX = 5; // Near left edge of farm
                farmY = 5; // Near top of farm
            } else {
                farmIndex = 3; // Bottom-Right farm
                farmX = 5; // Near left edge of farm
                farmY = Farm.height - 5; // Near bottom of farm
            }
        }

        // Get the target farm
        Farm targetFarm = App.getGame().getGameMap().getFarmByIndex(farmIndex);
        if (targetFarm == null) return;

        // Set player to farm with global farm coordinates
        player.setIsInVillage(false);
        player.setCurrentFarm(targetFarm);

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
                return;
        }

        player.setLocation(new Location(globalFarmX, globalFarmY, TileType.Dirt));

        // Update player's visual position to match global farm coordinates
        player.setPosX(globalFarmX * 60);
        player.setPosY(globalFarmY * 60);

        System.out.println("Player walked to farm " + farmIndex + " at global coordinates: (" + globalFarmX + ", " + globalFarmY + ")");
    }

    public Player getPlayer() {
        return player;
    }

    public TextureRegion getCurrentFrame() {
        return currentAnim.getKeyFrame(stateTime, true);
    }

    public Dir getFacing() {
        return facing;
    }

    /**
     * Render nickname above the player's head
     * @param batch The sprite batch to render with
     * @param currentPlayer The current player (for highlighting)
     * @param lightingColor The current lighting color to apply
     */
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
