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

import java.util.HashMap;
import java.util.Map;

public class NPCSpriteController implements Disposable {
    private static final int FRAME_WIDTH = 16;
    private static final int FRAME_HEIGHT = 32;
    private static final float FRAME_DURATION = 0.2f;

    private Map<String, Map<String, Animation<TextureRegion>>> npcAnimations;
    private Map<String, Texture> npcTextures;

    public NPCSpriteController() {
        this.npcAnimations = new HashMap<>();
        this.npcTextures = new HashMap<>();
        loadNPCSprites();
    }

    private void loadNPCSprites() {
        String[] npcNames = {"Abigail", "Pierre", "Sebastian", "Leah", "Willy", "Jojo"};
        String[] animationTypes = {"idle", "walk", "back", "face", "fly"};

        for (String npcName : npcNames) {
            Map<String, Animation<TextureRegion>> animations = new HashMap<>();

            for (String animType : animationTypes) {
                Animation<TextureRegion> animation = loadNPCAnimation(npcName, animType);
                if (animation != null) {
                    animations.put(animType, animation);
                }
            }

            npcAnimations.put(npcName, animations);
        }
    }

    private Animation<TextureRegion> loadNPCAnimation(String npcName, String animationType) {
        try {
            // Load individual frames for the animation
            Array<TextureRegion> frames = new Array<>();

            // Most NPCs have 4 frames per animation (0-3)
            for (int i = 0; i < 4; i++) {
                String framePath = String.format("content/NPC/%s/%s_%d.png", npcName, animationType, i);
                Texture frameTexture = new Texture(framePath);
                TextureRegion frame = new TextureRegion(frameTexture);
                frames.add(frame);
            }

            return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
        } catch (Exception e) {
            // If animation doesn't exist, try to load just the first frame
            try {
                String framePath = String.format("content/NPC/%s/%s_0.png", npcName, animationType);
                Texture frameTexture = new Texture(framePath);
                TextureRegion frame = new TextureRegion(frameTexture);

                Array<TextureRegion> frames = new Array<>();
                frames.add(frame);

                return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
            } catch (Exception e2) {
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
            // Fallback to idle animation
            animation = getNPCAnimation(npcName, "idle");
            if (animation == null) {
                return null;
            }
        }

        // Update animation timer
        float currentTimer = npc.getAnimationTimer() + deltaTime;
        npc.setAnimationTimer(currentTimer);

        return animation.getKeyFrame(currentTimer, true);
    }

    public void setNPCAnimation(NPC npc, String animationType) {
        npc.setCurrentAnimation(animationType);
        npc.setAnimationTimer(0f); // Reset animation timer
    }

    public void update(float deltaTime) {
        // Update NPC animations and positions
        // This could include movement logic, animation state changes, etc.
    }

    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch, com.badlogic.gdx.graphics.Color lightingColor) {
        // Get the village and render all NPCs
        Game game = org.example.common.models.App.getGame();
        if (game == null || game.getGameMap() == null) return;

        Village village = game.getGameMap().getVillage();
        if (village == null) return;

        // For now, we'll render NPCs based on the Npcs enum
        // In the future, we can add a proper residents list to the Village class
        renderNPCsFromEnum(batch, lightingColor);
    }

    private void renderNPCsFromEnum(com.badlogic.gdx.graphics.g2d.SpriteBatch batch, com.badlogic.gdx.graphics.Color lightingColor) {
        // Set batch color for lighting
        batch.setColor(lightingColor);

        // Render NPCs based on the Npcs enum
        for (org.example.common.models.enums.Npcs npcEnum : org.example.common.models.enums.Npcs.values()) {
            renderNPCFromEnum(batch, npcEnum);
        }

        // Reset batch color
        batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);
    }



    private void renderNPCFromEnum(com.badlogic.gdx.graphics.g2d.SpriteBatch batch, org.example.common.models.enums.Npcs npcEnum) {
        // Create a temporary NPC for rendering
        NPC npc = new NPC(npcEnum.getCharacteristic(), npcEnum.getName(), npcEnum.getJob(), new HashMap<>());
        npc.setSpriteName(npcEnum.getName());
        npc.setPosX(npcEnum.getLocation().getX() * 60f);
        npc.setPosY(npcEnum.getLocation().getY() * 60f);

        renderNPC(batch, npc);
    }

    private void renderNPC(com.badlogic.gdx.graphics.g2d.SpriteBatch batch, NPC npc) {
        TextureRegion currentFrame = getCurrentFrame(npc, 0.016f); // Assume 60 FPS
        if (currentFrame != null) {
            // Render NPC at their position
            float x = npc.getPosX();
            float y = npc.getPosY();

            // Scale the sprite to match the game's tile size (60 pixels)
            float scale = 3.75f; // 60 pixels / 16 pixels = 3.75
            float width = currentFrame.getRegionWidth() * scale;
            float height = currentFrame.getRegionHeight() * scale;

            batch.draw(currentFrame, x, y, width, height);
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
