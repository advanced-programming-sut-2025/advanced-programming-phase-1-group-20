package org.example.client.controllers.gameplay;

import org.example.common.models.App;
import org.example.common.models.common.Date;
import org.example.common.models.common.Location;
import org.example.common.models.entities.NPC;
import org.example.common.models.enums.NPCRoutine;
import org.example.common.models.MapDetails.Village;

import java.util.List;
import java.util.Random;

public class NPCMovementController {
    private static final float MOVEMENT_SPEED = 60f; // pixels per second (increased for smoother movement)
    private static final float ARRIVAL_THRESHOLD = 5f; // pixels
    private static final Random random = new Random();

    public void update(float deltaTime) {
        // Update NPC movement every frame for smooth movement
        updateAllNPCMovements(deltaTime);
    }

    private void updateAllNPCMovements(float deltaTime) {
        try {
            Village village = App.getGame().getGameMap().getVillage();
            if (village == null || village.getResidents() == null) {
                return;
            }

            Date currentDate = App.getGame().getDate();
            if (currentDate == null) {
                return;
            }

            int currentHour = currentDate.getHour();

            for (NPC npc : village.getResidents()) {
                updateNPCMovement(npc, currentHour, deltaTime);
            }
        } catch (Exception e) {
            System.err.println("Error updating NPC movements: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateNPCMovement(NPC npc, int currentHour, float deltaTime) {
        NPCRoutine routine = NPCRoutine.fromNpcName(npc.getName());
        if (routine == null) {
            return;
        }

        // Find the current routine point for this hour
        NPCRoutine.RoutinePoint currentRoutinePoint = getCurrentRoutinePoint(routine, currentHour);
        if (currentRoutinePoint == null) {
            return;
        }

        Location targetLocation = currentRoutinePoint.getLocation();
        if (targetLocation == null) {
            return;
        }

        // Convert target location to world pixel coordinates (add village offset)
        float targetX = (org.example.common.models.MapDetails.GameMap.VILLAGE_X * 60f) + (targetLocation.getX() * 60f);
        float targetY = (org.example.common.models.MapDetails.GameMap.VILLAGE_Y * 60f) + (targetLocation.getY() * 60f);

        // Check if NPC is already at the target location
        float currentX = npc.getPosX();
        float currentY = npc.getPosY();
        
        float distanceToTarget = (float) Math.sqrt(
            Math.pow(targetX - currentX, 2) + Math.pow(targetY - currentY, 2)
        );

        if (distanceToTarget <= ARRIVAL_THRESHOLD) {
            // NPC has arrived at destination
            npc.setPosX(targetX);
            npc.setPosY(targetY);
            npc.setMoving(false);
            npc.setCurrentAnimation("down");
            return;
        }

        // Move NPC towards target
        moveNPCTowardsTarget(npc, targetX, targetY, deltaTime);
    }

    private void moveNPCTowardsTarget(NPC npc, float targetX, float targetY, float deltaTime) {
        float currentX = npc.getPosX();
        float currentY = npc.getPosY();
        
        // Calculate direction vector
        float deltaX = targetX - currentX;
        float deltaY = targetY - currentY;
        
        // Normalize the direction vector
        float distance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (distance > 0) {
            deltaX /= distance;
            deltaY /= distance;
        }

        // Calculate new position using deltaTime for smooth movement
        float newX = currentX + deltaX * MOVEMENT_SPEED * deltaTime;
        float newY = currentY + deltaY * MOVEMENT_SPEED * deltaTime;

        // Update NPC position
        npc.setPosX(newX);
        npc.setPosY(newY);
        npc.setMoving(true);

        // Update animation based on movement direction
        updateNPCDirection(npc, deltaX, deltaY);
    }

    private void updateNPCDirection(NPC npc, float deltaX, float deltaY) {
        // Determine primary direction of movement
        if (Math.abs(deltaY) > Math.abs(deltaX)) {
            // Vertical movement is primary
            if (deltaY > 0) {
                npc.setCurrentAnimation("back"); // Moving up
                npc.setFacingLeft(false); // Reset facing direction for vertical movement
            } else {
                npc.setCurrentAnimation("down"); // Moving down
                npc.setFacingLeft(false); // Reset facing direction for vertical movement
            }
        } else {
            // Horizontal movement is primary
            if (deltaX > 0) {
                npc.setCurrentAnimation("walk"); // Moving right
                npc.setFacingLeft(false);
            } else {
                npc.setCurrentAnimation("walk"); // Moving left - use walk but will be flipped
                npc.setFacingLeft(true);
            }
        }
    }

    private NPCRoutine.RoutinePoint getCurrentRoutinePoint(NPCRoutine routine, int currentHour) {
        List<NPCRoutine.RoutinePoint> routinePoints = routine.getRoutinePoints();
        
        for (NPCRoutine.RoutinePoint point : routinePoints) {
            if (point.isActiveAt(currentHour)) {
                return point;
            }
        }
        
        // If no routine point found, return the first one as fallback
        return routinePoints.isEmpty() ? null : routinePoints.get(0);
    }

    // Method to force NPC to their current routine location immediately
    public void forceNPCToRoutineLocation(NPC npc) {
        try {
            Date currentDate = App.getGame().getDate();
            if (currentDate == null) {
                return;
            }

            int currentHour = currentDate.getHour();
            NPCRoutine routine = NPCRoutine.fromNpcName(npc.getName());
            if (routine == null) {
                return;
            }

            NPCRoutine.RoutinePoint currentRoutinePoint = getCurrentRoutinePoint(routine, currentHour);
            if (currentRoutinePoint == null) {
                return;
            }

            Location targetLocation = currentRoutinePoint.getLocation();
            if (targetLocation == null) {
                return;
            }

            // Immediately set NPC to target location (world coordinates)
            float targetX = (org.example.common.models.MapDetails.GameMap.VILLAGE_X * 60f) + (targetLocation.getX() * 60f);
            float targetY = (org.example.common.models.MapDetails.GameMap.VILLAGE_Y * 60f) + (targetLocation.getY() * 60f);
            
            npc.setPosX(targetX);
            npc.setPosY(targetY);
            npc.setMoving(false);
            npc.setCurrentAnimation("down");
            npc.setFacingLeft(false); // Reset facing direction
            
        } catch (Exception e) {
            System.err.println("Error forcing NPC to routine location: " + e.getMessage());
        }
    }

    // Method to get current activity for an NPC
    public String getNPCCurrentActivity(NPC npc) {
        try {
            Date currentDate = App.getGame().getDate();
            if (currentDate == null) {
                return "Unknown";
            }

            int currentHour = currentDate.getHour();
            NPCRoutine routine = NPCRoutine.fromNpcName(npc.getName());
            if (routine == null) {
                return "No routine";
            }

            NPCRoutine.RoutinePoint currentRoutinePoint = getCurrentRoutinePoint(routine, currentHour);
            if (currentRoutinePoint == null) {
                return "Unknown activity";
            }

            return currentRoutinePoint.getActivity();
            
        } catch (Exception e) {
            return "Error getting activity";
        }
    }

    // Method to force all NPCs to their current routine locations (for testing)
    public void forceAllNPCsToRoutineLocations() {
        try {
            Village village = App.getGame().getGameMap().getVillage();
            if (village == null || village.getResidents() == null) {
                return;
            }

            Date currentDate = App.getGame().getDate();
            if (currentDate == null) {
                return;
            }

            int currentHour = currentDate.getHour();

            for (NPC npc : village.getResidents()) {
                forceNPCToRoutineLocation(npc);
            }
        } catch (Exception e) {
            System.err.println("Error forcing NPCs to routine locations: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
