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
            System.out.println("DEBUG: Player thinks they're in village, but checking village movement...");
            System.out.println("Current tile coordinates: (" + tileX + ", " + tileY + ")");
            System.out.println("Player position in pixels: (" + player.getPosX() + ", " + player.getPosY() + ")");
            System.out.println("Player position in tiles: (" + (player.getPosX() / 60) + ", " + (player.getPosY() / 60) + ")");

            // Player is in village, check village boundaries
            if (tileX >= 0 && tileX < Village.width &&
                tileY >= 0 && tileY < Village.height) {
                // Check if trying to exit village to farms
                if (tileX <= 2 || tileX >= Village.width - 3) {
                    // Transition to farm
                    handleVillageToFarmTransition(tileX, tileY);
                    return true;
                }

                // Check if the tile is walkable in the village
                Village village = gameMap.getVillage();
                if (village != null && tileX < village.getTiles().length && tileY < village.getTiles()[0].length) {
                    Location loc = village.getTiles()[tileX][tileY];
                    if (loc != null) {
                        return loc.getTile().isWalkable();
                    }
                }
                return true; // Default to walkable within village
            } else {
                // Player thinks they're in village but are at invalid coordinates
                // This means the transition didn't complete properly
                System.out.println("ERROR: Player thinks they're in village but at invalid coordinates! Resetting to farm...");
                System.out.println("Current coordinates: tileX=" + tileX + ", tileY=" + tileY);
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
                switch (currentFarm.getFarmIndex()) {
                    case 0: // Farm 0 (Bottom-Left) - exit at right edge to village
                    case 1: // Farm 1 (Top-Left) - exit at right edge to village
                        if (tileX >= Farm.width && tileX < Farm.width + 3) {
                            if (!transitionInProgress) {
                                System.out.println("TRANSITION TRIGGERED: Farm " + currentFarm.getFarmIndex() + " to village!");
                                transitionInProgress = true;
                                // Transition to village
                                handleAreaTransition(tileX, tileY);
                                transitionInProgress = false;
                                return true;
                            }
                        }
                        break;
                    case 2: // Farm 2 (Top-Right) - exit at left edge to village
                    case 3: // Farm 3 (Bottom-Right) - exit at left edge to village
                        if (tileX < 0 && tileX >= -3) {
                            if (!transitionInProgress) {
                                System.out.println("TRANSITION TRIGGERED: Farm " + currentFarm.getFarmIndex() + " to village!");
                                transitionInProgress = true;
                                // Transition to village
                                handleAreaTransition(tileX, tileY);
                                transitionInProgress = false;
                                return true;
                            }
                        }
                        break;
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

        int villageX, villageY;

        switch (currentFarm.getFarmIndex()) {
            case 0: // Farm 0 (Bottom-Left) - enter village from left
                villageX = 0; // Left edge of village
                villageY = tileY + Village.height / 2; // Scale Y coordinate for bottom half of village
                break;
            case 1: // Farm 1 (Top-Left) - enter village from left
                villageX = 0; // Left edge of village
                villageY = tileY; // Keep same Y coordinate for top half of village
                break;
            case 2: // Farm 2 (Top-Right) - enter village from right
                villageX = Village.width - 1; // Right edge of village
                villageY = tileY; // Keep same Y coordinate for top half of village
                break;
            case 3: // Farm 3 (Bottom-Right) - enter village from right
                villageX = Village.width - 1; // Right edge of village
                villageY = tileY + Village.height / 2; // Scale Y coordinate for bottom half of village
                break;
            default:
                return;
        }

        System.out.println("Coordinate conversion: farm(" + tileX + "," + tileY + ") -> village(" + villageX + "," + villageY + ")");

        // Set player to village with village coordinates
        player.setIsInVillage(true);
        player.setLocation(new Location(villageX, villageY, TileType.VILLAGE));

        // Update player's visual position to match village coordinates
        player.setPosX(villageX * 60);
        player.setPosY(villageY * 60);

        System.out.println("TRANSITION COMPLETED: Player walked to village at village coordinates: (" + villageX + ", " + villageY + ")");
        System.out.println("Player is now in village: " + player.getIsInVillage());
        System.out.println("Player position: (" + player.getPosX() + ", " + player.getPosY() + ")");

        System.out.println("Verification - Player should now be at village coordinates: (" + (player.getPosX() / 60) + ", " + (player.getPosY() / 60) + ")");
    }

    private void handleVillageToFarmTransition(int tileX, int tileY) {
        // Determine which farm to transition to based on position
        int farmIndex;
        int farmX, farmY;

        if (tileX <= 2) {
            // Exit to left farms (0 or 1)
            if (tileY < Village.height / 2) {
                farmIndex = 1; // Top-Left farm
                farmX = Farm.width - 3; // Near right edge of farm
                farmY = tileY; // Keep same Y coordinate for top half
            } else {
                farmIndex = 0; // Bottom-Left farm
                farmX = Farm.width - 3; // Near right edge of farm
                farmY = tileY - Village.height / 2; // Adjust Y coordinate for bottom half
            }
        } else {
            // Exit to right farms (2 or 3)
            if (tileY < Village.height / 2) {
                farmIndex = 2; // Top-Right farm
                farmX = 2; // Near left edge of farm
                farmY = tileY; // Keep same Y coordinate for top half
            } else {
                farmIndex = 3; // Bottom-Right farm
                farmX = 2; // Near left edge of farm
                farmY = tileY - Village.height / 2; // Adjust Y coordinate for bottom half
            }
        }

        // Get the target farm
        Farm targetFarm = App.getGame().getGameMap().getFarmByIndex(farmIndex);
        if (targetFarm == null) return;

        // Set player to farm
        player.setIsInVillage(false);
        player.setCurrentFarm(targetFarm);
        player.setLocation(new Location(farmX, farmY, TileType.Dirt));

        // Update player's visual position to match farm coordinates
        player.setPosX(farmX * 60);
        player.setPosY(farmY * 60);

        System.out.println("Player walked to farm " + farmIndex + " at coordinates: (" + farmX + ", " + farmY + ")");
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

    /**
     * Dispose of nickname font resources
     */
    public void dispose() {
        if (nicknameFont != null) {
            nicknameFont.dispose();
        }
    }
}
