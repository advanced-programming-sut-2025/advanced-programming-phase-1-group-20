package org.example.server.controllers;

import org.example.common.models.App;
import org.example.common.models.MapDetails.GameMap;
import org.example.common.models.MapDetails.Village;
import org.example.common.models.common.Date;
import org.example.common.models.common.Location;
import org.example.common.models.entities.NPC;
import org.example.common.models.enums.Jobs;
import org.example.common.models.enums.Markets;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class NpcController {
    // Time periods for NPC routines
    private static final int MORNING_START = 6;  // 6:00 AM
    private static final int MORNING_END = 11;   // 11:00 AM
    private static final int AFTERNOON_START = 12; // 12:00 PM
    private static final int AFTERNOON_END = 17;   // 5:00 PM
    private static final int EVENING_START = 18;   // 6:00 PM
    private static final int EVENING_END = 21;     // 9:00 PM

    // Movement speed (tiles per second)
    private static final float MOVEMENT_SPEED = 1.0f; // 1 tile per second

    // Village coordinates for different areas
    private static final int TOWN_SQUARE_X = 39; // Center of village (78/2)
    private static final int TOWN_SQUARE_Y = 78; // Center of village (156/2)

    // NPC house positions (based on Village.java createNPCFromEnum method)
    private static final int[][] NPC_HOUSE_POSITIONS = {
        {12, 3}, // Abigail
        {25, 3}, // Pierre
        {38, 3}, // Sebastian
        {51, 3}, // Leah
        {64, 3}, // Willy
        {15, 3}  // Jojo
    };

    // Work locations for different jobs
    private static final int[][] WORK_LOCATIONS = {
        {45, 125}, // Carpenter's Shop (Robin)
        {35, 15},  // Pierre's General Store
        {5, 35},   // Fish Shop (Willy)
        {40, 20},  // Joja Mart (Jojo)
        {30, 30},  // Blacksmith (Harvey)
        {20, 20}   // Library/Study area (Sebastian, Abigail)
    };

    // Public areas for evening socializing
    private static final int[][] PUBLIC_AREAS = {
        {39, 78},  // Town Square (center)
        {39, 120}, // Upper village area
        {39, 40},  // Lower village area
        {20, 78},  // Left side of village
        {58, 78}   // Right side of village
    };

        private Random random = new Random();

    // NPC movement state tracking for village center wandering
    private Map<String, Float> npcStateTimers = new HashMap<>();
    private Map<String, Float> npcTargetX = new HashMap<>();
    private Map<String, Float> npcTargetY = new HashMap<>();
    private static final float STATE_CHANGE_INTERVAL = 3.0f; // 3 seconds between state changes
    private static final float WANDER_RADIUS = 3.0f * 60f; // 3 tiles radius for wandering


    public void updateNPCRoutines() {
        GameMap gameMap = App.getGame().getGameMap();
        if (gameMap == null || gameMap.getVillage() == null) {
            System.out.println("DEBUG: NPC Controller - GameMap or Village is null");
            return;
        }

        Village village = gameMap.getVillage();
        List<NPC> residents = village.getResidents();
        Date currentDate = App.getGame().getDate();

        if (residents == null || currentDate == null) {
            System.out.println("DEBUG: NPC Controller - Residents or Date is null");
            return;
        }

        int currentHour = currentDate.getHour();
        System.out.println("DEBUG: NPC Controller - Current hour: " + currentHour + " (" + getTimePeriodDescription(currentHour) + ")");
        System.out.println("DEBUG: NPC Controller - Updating " + residents.size() + " NPCs");

        for (int i = 0; i < residents.size(); i++) {
            NPC npc = residents.get(i);
            System.out.println("DEBUG: NPC Controller - Updating " + npc.getName() + " at position (" + npc.getPosX() + ", " + npc.getPosY() + ")");
            updateNPCRoutine(npc, currentHour, i);
        }
    }


    private void updateNPCRoutine(NPC npc, int currentHour, int npcIndex) {
        Location targetLocation = getTargetLocationForTime(npc, currentHour, npcIndex);
        if (targetLocation != null) {
            System.out.println("DEBUG: NPC " + npc.getName() + " - Target location: (" + targetLocation.xAxis + ", " + targetLocation.yAxis + ")");

            // Check if NPC is in a public area (evening time)
            if (currentHour >= EVENING_START && currentHour <= EVENING_END) {
                System.out.println("DEBUG: NPC " + npc.getName() + " - Using wandering behavior (evening)");
                // Use enhanced wandering behavior in public areas
                updateNPCWandering(npc, targetLocation);
            } else {
                System.out.println("DEBUG: NPC " + npc.getName() + " - Using normal movement");
                // Use normal movement for other times
                moveNPCToLocation(npc, targetLocation);
            }
        } else {
            System.out.println("DEBUG: NPC " + npc.getName() + " - No target location found!");
        }
    }


    private Location getTargetLocationForTime(NPC npc, int currentHour, int npcIndex) {
        if (currentHour >= MORNING_START && currentHour <= MORNING_END) {
            // Morning: Stay at home
            return getHomeLocation(npcIndex);
        } else if (currentHour >= AFTERNOON_START && currentHour <= AFTERNOON_END) {
            // Afternoon: Go to work
            return getWorkLocation(npc);
        } else if (currentHour >= EVENING_START && currentHour <= EVENING_END) {
            // Evening: Visit public areas
            return getPublicAreaLocation();
        } else {
            // Night: Return home
            return getHomeLocation(npcIndex);
        }
    }


    private Location getHomeLocation(int npcIndex) {
        if (npcIndex >= 0 && npcIndex < NPC_HOUSE_POSITIONS.length) {
            int[] housePos = NPC_HOUSE_POSITIONS[npcIndex];
            return new Location(housePos[0], housePos[1],
                org.example.common.models.enums.Types.TileType.VILLAGE);
        }
        // Default home position
        return new Location(12, 3, org.example.common.models.enums.Types.TileType.VILLAGE);
    }


    private Location getWorkLocation(NPC npc) {
        Jobs job = npc.getJobs();
        String npcName = npc.getName();

        switch (job) {
            case SELLER:
                if ("Pierre".equals(npcName)) {
                    // Pierre's General Store
                    return new Location(35, 15, org.example.common.models.enums.Types.TileType.PIERRE_GENERAL_STORE);
                } else if ("Jojo".equals(npcName)) {
                    // Joja Mart
                    return new Location(40, 20, org.example.common.models.enums.Types.TileType.JojaMart);
                } else {
                    // Default shop location
                    return new Location(30, 30, org.example.common.models.enums.Types.TileType.MARKET);
                }
            case FISHER:
                // Willy's Fish Shop
                return new Location(5, 35, org.example.common.models.enums.Types.TileType.FISH_SHOP);
            case ENGINEER:
                if ("Harvey".equals(npcName)) {
                    // Blacksmith
                    return new Location(30, 30, org.example.common.models.enums.Types.TileType.BlackSmith);
                } else {
                    // Default engineering work area
                    return new Location(20, 20, org.example.common.models.enums.Types.TileType.VILLAGE);
                }
            case STUDENT:
                if ("Sebastian".equals(npcName)) {
                    // Sebastian's programming area (basement-like location)
                    return new Location(38, 5, org.example.common.models.enums.Types.TileType.VILLAGE);
                } else if ("Abigail".equals(npcName)) {
                    // Abigail's study area
                    return new Location(12, 8, org.example.common.models.enums.Types.TileType.VILLAGE);
                } else {
                    // Default study area
                    return new Location(20, 20, org.example.common.models.enums.Types.TileType.VILLAGE);
                }
            default:
                // Default work location
                return new Location(30, 30, org.example.common.models.enums.Types.TileType.VILLAGE);
        }
    }


    private Location getPublicAreaLocation() {
        int[] publicArea = PUBLIC_AREAS[random.nextInt(PUBLIC_AREAS.length)];
        return new Location(publicArea[0], publicArea[1],
            org.example.common.models.enums.Types.TileType.VILLAGE);
    }


    private void moveNPCToLocation(NPC npc, Location targetLocation) {
        Location currentLocation = npc.getLocation();

        if (currentLocation == null) {
            // Set initial location if none exists
            System.out.println("DEBUG: NPC " + npc.getName() + " - Setting initial location");
            npc.setLocation(targetLocation);
            updateNPCPosition(npc, targetLocation);
            return;
        }

        // Calculate distance to target
        int distanceX = targetLocation.xAxis - currentLocation.xAxis;
        int distanceY = targetLocation.yAxis - currentLocation.yAxis;

        System.out.println("DEBUG: NPC " + npc.getName() + " - Distance to target: (" + distanceX + ", " + distanceY + ")");

        // If NPC is already at target location, don't move
        if (distanceX == 0 && distanceY == 0) {
            System.out.println("DEBUG: NPC " + npc.getName() + " - Already at target location");
            npc.setCurrentAnimation("down");
            npc.setMoving(false);
            return;
        }

        // Set movement animation based on direction
        setMovementAnimation(npc, distanceX, distanceY);
        npc.setMoving(true);

        // Move towards target (one tile at a time for smooth movement)
        int newX = currentLocation.xAxis;
        int newY = currentLocation.yAxis;

        if (distanceX > 0) {
            newX++;
        } else if (distanceX < 0) {
            newX--;
        }

        if (distanceY > 0) {
            newY++;
        } else if (distanceY < 0) {
            newY--;
        }

        // Update NPC location
        Location newLocation = new Location(newX, newY,
            org.example.common.models.enums.Types.TileType.VILLAGE);
        npc.setLocation(newLocation);
        updateNPCPosition(npc, newLocation);
    }


    private void setMovementAnimation(NPC npc, int distanceX, int distanceY) {
        if (Math.abs(distanceX) > Math.abs(distanceY)) {
            // Horizontal movement
            if (distanceX > 0) {
                npc.setCurrentAnimation("walk"); // Moving right
            } else {
                npc.setCurrentAnimation("walk"); // Moving left (flip sprite)
            }
        } else {
            // Vertical movement
            if (distanceY > 0) {
                npc.setCurrentAnimation("back"); // Moving up
            } else {
                npc.setCurrentAnimation("down"); // Moving down
            }
        }
    }


    private void updateNPCPosition(NPC npc, Location location) {
        // Convert tile coordinates to pixel coordinates
        float pixelX = location.xAxis * 60f; // 60 pixels per tile
        float pixelY = location.yAxis * 60f;

        // Add village offset to get world coordinates
        float worldX = (GameMap.VILLAGE_X * 60f) + pixelX;
        float worldY = (GameMap.VILLAGE_Y * 60f) + pixelY;

        npc.setPosX(worldX);
        npc.setPosY(worldY);
    }

    private void updateNPCWandering(NPC npc, Location publicAreaLocation) {
        String npcName = npc.getName();

        // Initialize state timer if not exists
        if (!npcStateTimers.containsKey(npcName)) {
            npcStateTimers.put(npcName, STATE_CHANGE_INTERVAL);
        }

        // Update state timer
        float currentTimer = npcStateTimers.get(npcName) - 0.016f; // Assuming 60 FPS
        npcStateTimers.put(npcName, currentTimer);

        // Time to change state
        if (currentTimer <= 0) {
            // 60% chance to start moving, 40% to stay idle (NPCs are more social than animals)
            if (random.nextFloat() < 0.6f) {
                npc.setMoving(true);

                // Pick a new target location within the public area
                float centerX = publicAreaLocation.xAxis * 60f + (GameMap.VILLAGE_X * 60f);
                float centerY = publicAreaLocation.yAxis * 60f + (GameMap.VILLAGE_Y * 60f);

                double angle = random.nextDouble() * 2 * Math.PI;
                double distance = random.nextDouble() * WANDER_RADIUS;

                float targetX = (float) (centerX + Math.cos(angle) * distance);
                float targetY = (float) (centerY + Math.sin(angle) * distance);

                npcTargetX.put(npcName, targetX);
                npcTargetY.put(npcName, targetY);

                System.out.println("DEBUG: NPC " + npcName + " started wandering to (" + targetX + ", " + targetY + ")");
            } else {
                npc.setMoving(false);
                npc.setCurrentAnimation("down");
                System.out.println("DEBUG: NPC " + npcName + " stopped to socialize");
            }

            // Reset timer for next state change (2 to 5 seconds)
            npcStateTimers.put(npcName, 2.0f + random.nextFloat() * 3.0f);
        }

        // If moving, update position towards target
        if (npc.isMoving() && npcTargetX.containsKey(npcName) && npcTargetY.containsKey(npcName)) {
            float currentX = npc.getPosX();
            float currentY = npc.getPosY();
            float targetX = npcTargetX.get(npcName);
            float targetY = npcTargetY.get(npcName);

            float dx = targetX - currentX;
            float dy = targetY - currentY;

            // Stop if close to the target
            if (Math.abs(dx) < 30 && Math.abs(dy) < 30) { // Half a tile
                npc.setMoving(false);
                npc.setCurrentAnimation("down");
                System.out.println("DEBUG: NPC " + npcName + " reached wandering target");
            } else {
                // Normalize direction vector for smooth movement
                float length = (float) Math.sqrt(dx * dx + dy * dy);
                float moveX = (dx / length) * npc.getSpeed() * 0.016f; // 60 FPS
                float moveY = (dy / length) * npc.getSpeed() * 0.016f;

                // Update position
                npc.setPosX(currentX + moveX);
                npc.setPosY(currentY + moveY);

                // Set animation based on movement direction
                if (Math.abs(dx) > Math.abs(dy)) {
                    npc.setCurrentAnimation("walk"); // Horizontal movement
                } else {
                    if (dy > 0) {
                        npc.setCurrentAnimation("back"); // Moving up
                    } else {
                        npc.setCurrentAnimation("down"); // Moving down
                    }
                }
            }
        }
    }

    public void forceNPCIdle(NPC npc) {
        npc.setCurrentAnimation("down");
        npc.setMoving(false);
        npc.setAnimationTimer(0f);
    }

    public String getTimePeriodDescription(int hour) {
        if (hour >= MORNING_START && hour <= MORNING_END) {
            return "Morning";
        } else if (hour >= AFTERNOON_START && hour <= AFTERNOON_END) {
            return "Afternoon";
        } else if (hour >= EVENING_START && hour <= EVENING_END) {
            return "Evening";
        } else {
            return "Night";
        }
    }
}
