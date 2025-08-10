package org.example.client.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import org.example.common.models.entities.NPC;
import org.example.common.models.MapDetails.Village;
import org.example.common.models.entities.Game;
import org.example.client.controllers.gameplay.NPCMovementController;

import java.util.HashMap;
import java.util.Map;

public class NPCSpriteController implements Disposable {
    private static final int FRAME_WIDTH = 16;
    private static final int FRAME_HEIGHT = 32;
    private static final float FRAME_DURATION = 0.2f;

    private Map<String, Map<String, Animation<TextureRegion>>> npcAnimations;
    private Map<String, Texture> npcTextures;
    private NPCMovementController movementController;

    public NPCSpriteController() {
        System.out.println("NPCSpriteController: Initializing...");
        this.npcAnimations = new HashMap<>();
        this.npcTextures = new HashMap<>();
        this.movementController = new NPCMovementController();
        loadNPCSprites();
        System.out.println("NPCSpriteController: Initialization complete");
    }

    private void loadNPCSprites() {
        System.out.println("NPCSpriteController: Loading NPC sprites...");
        String[] npcNames = {"Abigail", "Pierre", "Sebastian", "Leah", "Willy", "Jojo", "Harvey", "Robin"};
        String[] animationTypes = {"down", "walk", "back", "face", "fly"};

        for (String npcName : npcNames) {
            System.out.println("NPCSpriteController: Loading sprites for " + npcName);
            Map<String, Animation<TextureRegion>> animations = new HashMap<>();

            for (String animType : animationTypes) {
                Animation<TextureRegion> animation = loadNPCAnimation(npcName, animType);
                if (animation != null) {
                    animations.put(animType, animation);
                    System.out.println("NPCSpriteController: Successfully loaded " + animType + " animation for " + npcName);
                } else {
                    System.out.println("NPCSpriteController: Failed to load " + animType + " animation for " + npcName);
                }
            }

            if (animations.isEmpty()) {
                System.err.println("NPCSpriteController: WARNING - No animations loaded for " + npcName);
            }

            npcAnimations.put(npcName, animations);
            System.out.println("NPCSpriteController: Completed loading for " + npcName + " - " + animations.size() + " animations loaded");
        }
        System.out.println("NPCSpriteController: Finished loading all NPC sprites");
    }

    private Animation<TextureRegion> loadNPCAnimation(String npcName, String animationType) {
        try {
            // Load individual frames for the animation
            Array<TextureRegion> frames = new Array<>();

            // Handle Willy's special naming convention
            String basePath;
            if (npcName.equals("Willy")) {
                // Willy uses different naming: Walk_1.png, WillyFace_0.png, etc.
                switch (animationType) {
                    case "down":
                        basePath = "content/NPC/Willy/down_%d.png";
                        break;
                    case "walk":
                        basePath = "content/NPC/Willy/Walk_%d.png";
                        break;
                    case "face":
                        basePath = "content/NPC/Willy/WillyFace_%d.png";
                        break;
                    case "back":
                        basePath = "content/NPC/Willy/back_%d.png";
                        break;
                    case "fly":
                        basePath = "content/NPC/Willy/tool_%d.png"; // Willy uses tool_ for fly animation
                        break;
                    default:
                        basePath = "content/NPC/Willy/down_%d.png"; // Default to down
                        break;
                }
            } else {
                // Standard naming for other NPCs
                basePath = String.format("content/NPC/%s/%s_%%d.png", npcName, animationType);
            }

            // Most NPCs have 4 frames per animation (0-3)
            for (int i = 0; i < 4; i++) {
                String framePath = String.format(basePath, i);
                System.out.println("Loading NPC sprite: " + framePath);
                Texture frameTexture = new Texture(framePath);
                TextureRegion frame = new TextureRegion(frameTexture);
                frames.add(frame);
            }

            System.out.println("Successfully loaded animation " + animationType + " for " + npcName);
            return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
        } catch (Exception e) {
            System.err.println("Failed to load animation " + animationType + " for " + npcName + ": " + e.getMessage());
            // If animation doesn't exist, try to load just the first frame
            try {
                String framePath;
                if (npcName.equals("Willy")) {
                    // Handle Willy's special naming for fallback
                    switch (animationType) {
                        case "down":
                            framePath = "content/NPC/Willy/down_0.png";
                            break;
                        case "walk":
                            framePath = "content/NPC/Willy/Walk_0.png";
                            break;
                        case "face":
                            framePath = "content/NPC/Willy/WillyFace_0.png";
                            break;
                        case "back":
                            framePath = "content/NPC/Willy/back_0.png";
                            break;
                        case "fly":
                            framePath = "content/NPC/Willy/tool_0.png";
                            break;
                        default:
                            framePath = "content/NPC/Willy/down_0.png";
                            break;
                    }
                } else {
                    framePath = String.format("content/NPC/%s/%s_0.png", npcName, animationType);
                }

                System.out.println("Trying to load single frame: " + framePath);
                Texture frameTexture = new Texture(framePath);
                TextureRegion frame = new TextureRegion(frameTexture);

                Array<TextureRegion> frames = new Array<>();
                frames.add(frame);

                return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
            } catch (Exception e2) {
                System.err.println("Failed to load even single frame for " + npcName + " " + animationType + ": " + e2.getMessage());
                // If even the first frame doesn't exist, return null
                return null;
            }
        }
    }

    public Animation<TextureRegion> getNPCAnimation(String npcName, String animationType) {
        Map<String, Animation<TextureRegion>> npcAnims = npcAnimations.get(npcName);
        if (npcAnims != null) {
            return npcAnims.get(animationType);
        }
        return null;
    }

    public TextureRegion getCurrentFrame(NPC npc, float deltaTime) {
        String npcName = npc.getSpriteName();
        String animationType = npc.getCurrentAnimation();

        if (npcName == null || animationType == null) {
            return null;
        }



        Animation<TextureRegion> animation = getNPCAnimation(npcName, animationType);
        if (animation == null) {
            // Fallback to down animation (first frame only for idle)
            animation = getNPCAnimation(npcName, "down");
            if (animation == null) {
                return null;
            }
            // Force animation type to down if we're using fallback
            animationType = "down";
        }

        // For down animation when not moving, always return the first frame (down_0) for idle
        if ("down".equals(animationType) && !npc.isMoving()) {
            // Reset animation timer to ensure we stay on frame 0
            npc.setAnimationTimer(0f);
            return animation.getKeyFrame(0, false); // Return first frame (down_0) without animation
        }

        // Only update animation timer if NPC is moving or not on down animation
        if (npc.isMoving() || !"down".equals(animationType)) {
            float currentTimer = npc.getAnimationTimer() + deltaTime;
            npc.setAnimationTimer(currentTimer);
            return animation.getKeyFrame(currentTimer, true);
        } else {
            // Fallback: For down and not moving, return static frame (down_0)
            npc.setAnimationTimer(0f);
            return animation.getKeyFrame(0, false);
        }
    }

    public void setNPCAnimation(NPC npc, String animationType) {
        npc.setCurrentAnimation(animationType);
        npc.setAnimationTimer(0f); // Reset animation timer
    }

    public void update(float deltaTime) {
        // Update NPC movements based on routines
        if (movementController != null) {
            movementController.update(deltaTime);
        }
    }

    // Method to force all NPCs to their routine locations (for testing)
    public void forceAllNPCsToRoutineLocations() {
        if (movementController != null) {
            movementController.forceAllNPCsToRoutineLocations();
        }
    }

    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch, com.badlogic.gdx.graphics.Color lightingColor) {
        // Get the village and render all NPCs
        Game game = org.example.common.models.App.getGame();
        if (game == null || game.getGameMap() == null) return;

        Village village = game.getGameMap().getVillage();
        if (village == null) return;

        // Render NPCs from the village's residents list
        renderNPCsFromVillage(batch, lightingColor, village);
    }

    private void renderNPCsFromVillage(com.badlogic.gdx.graphics.g2d.SpriteBatch batch, com.badlogic.gdx.graphics.Color lightingColor, Village village) {
        // Set batch color for lighting
        batch.setColor(lightingColor);

        // Render NPCs from the village's residents list
        int npcCount = village.getResidents().size();
        if (npcCount > 0) {
            for (NPC npc : village.getResidents()) {
                renderNPC(batch, npc);
            }
        }

        // Reset batch colorss
        batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
    }

    private void renderNPC(com.badlogic.gdx.graphics.g2d.SpriteBatch batch, NPC npc) {
        TextureRegion currentFrame = getCurrentFrame(npc, 0.016f); // Assume 60 FPS
        if (currentFrame != null) {
            // Render NPC at their position
            float x = npc.getPosX();
            float y = npc.getPosY();

            float scale = 3.75f; // 60 pixels / 16 pixels = 3.75
            float width = currentFrame.getRegionWidth() * scale;
            float height = currentFrame.getRegionHeight() * scale;

            // Handle sprite flipping for left movement
            if (npc.isFacingLeft()) {
                // Flip the sprite horizontally by drawing it with negative width
                batch.draw(currentFrame, x + width, y, -width, height);
            } else {
                // Normal drawing
                batch.draw(currentFrame, x, y, width, height);
            }
        } else {
            System.out.println("Failed to get frame for NPC " + npc.getName() + " with sprite " + npc.getSpriteName());
            System.out.println("  Animation: " + npc.getCurrentAnimation() + ", Timer: " + npc.getAnimationTimer());
        }
    }



    @Override
    public void dispose() {
        // Dispose of all loaded textures
        for (Map<String, Animation<TextureRegion>> npcAnims : npcAnimations.values()) {
            for (Animation<TextureRegion> animation : npcAnims.values()) {
                if (animation != null) {
                    for (TextureRegion frame : animation.getKeyFrames()) {
                        if (frame.getTexture() != null) {
                            frame.getTexture().dispose();
                        }
                    }
                }
            }
        }
        npcAnimations.clear();
        npcTextures.clear();
    }
}
