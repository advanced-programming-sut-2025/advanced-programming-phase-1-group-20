package org.example.server.controllers.GameControllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

    private static Animation<TextureRegion> buildAnim(TextureRegion[] row) {
        Array<TextureRegion> frames = new Array<>(3);
        for (int i = 0; i < 3; i++) {
            frames.add(row[i]);
        }
        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP_PINGPONG);
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

        // Check if player is trying to move to village
        if (player.getIsInVillage()) {
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
                player.setIsInVillage(false);
                // Reset player position to a valid farm position
                player.setPosX(10 * 60); // Move to x=10 in farm
                player.setPosY(10 * 60); // Move to y=10 in farm
                return false; // Don't allow movement until next frame
            }
        } else {
            // Player is in farm, check if trying to exit to village FIRST
            Farm currentFarm = player.getCurrentFarm();
            if (currentFarm != null) {
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
        // Convert farm coordinates to village coordinates
        Farm currentFarm = player.getCurrentFarm();
        if (currentFarm == null) return;

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

        // Set player to village with village coordinates
        player.setIsInVillage(true);
        player.setLocation(new Location(villageX, villageY, TileType.VILLAGE));

        // Update player's visual position to match village coordinates
        player.setPosX(villageX * 60);
        player.setPosY(villageY * 60);

        System.out.println("Player walked to village at village coordinates: (" + villageX + ", " + villageY + ")");
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
}
