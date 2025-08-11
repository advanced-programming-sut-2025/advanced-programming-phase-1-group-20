package org.example.common.models.enums;

import org.example.common.models.common.Location;
import org.example.common.models.enums.Types.TileType;

import java.util.Arrays;
import java.util.List;

public enum NPCRoutine {
    SEBASTIAN_ROUTINE(
        "Sebastian",
        Arrays.asList(
            // Morning: At home (basement)
            new RoutinePoint(6, 10, new Location(10, 5, TileType.VILLAGE), "Sleeping in basement"),
            // Late morning: Walking to work area
            new RoutinePoint(10, 11, new Location(15, 10, TileType.VILLAGE), "Walking to work area"),
            // Work time: At his programming workspace (near mountain area)
            new RoutinePoint(11, 16, new Location(25, 20, TileType.VILLAGE), "Working on programming projects"),
            // Afternoon: At the lake for relaxation
            new RoutinePoint(16, 18, new Location(37, 80, TileType.VILLAGE), "At the lake"),
            // Evening: At the saloon for socializing
            new RoutinePoint(18, 20, new Location(39, 78, TileType.VILLAGE), "At the Stardrop Saloon"),
            // Night: Walking home
            new RoutinePoint(20, 22, new Location(10, 5, TileType.VILLAGE), "Walking home"),
            // Late night: Back home
            new RoutinePoint(22, 6, new Location(10, 5, TileType.VILLAGE), "Sleeping")
        )
    ),
    
    ABIGAIL_ROUTINE(
        "Abigail",
        Arrays.asList(
            // Morning: At home
            new RoutinePoint(6, 8, new Location(15, 5, TileType.VILLAGE), "At home"),
            // Morning: Walking to Pierre's store
            new RoutinePoint(8, 9, new Location(18, 8, TileType.VILLAGE), "Walking to Pierre's store"),
            // Work time: At Pierre's store helping her father (different position within store)
            new RoutinePoint(9, 17, new Location(21, 125, TileType.VILLAGE), "At Pierre's General Store"),
            // Afternoon: At the mountain for adventure
            new RoutinePoint(17, 19, new Location(30, 30, TileType.VILLAGE), "Exploring the mountain"),
            // Evening: At the town square
            new RoutinePoint(19, 21, new Location(39, 78, TileType.VILLAGE), "At the town square"),
            // Night: Walking home
            new RoutinePoint(21, 22, new Location(15, 5, TileType.VILLAGE), "Walking home"),
            // Late night: Back home
            new RoutinePoint(22, 6, new Location(15, 5, TileType.VILLAGE), "Sleeping")
        )
    ),
    
    PIERRE_ROUTINE(
        "Pierre",
        Arrays.asList(
            // Morning: At home
            new RoutinePoint(6, 8, new Location(20, 5, TileType.VILLAGE), "At home"),
            // Morning: Walking to store
            new RoutinePoint(8, 9, new Location(20, 125, TileType.VILLAGE), "Opening the store"),
            // Work time: At Pierre's General Store
            new RoutinePoint(9, 17, new Location(20, 125, TileType.VILLAGE), "At Pierre's General Store"),
            // Evening: Walking home
            new RoutinePoint(17, 18, new Location(20, 5, TileType.VILLAGE), "Walking home"),
            // Evening: At home
            new RoutinePoint(18, 22, new Location(20, 5, TileType.VILLAGE), "At home"),
            // Night: Sleeping
            new RoutinePoint(22, 6, new Location(20, 5, TileType.VILLAGE), "Sleeping")
        )
    ),
    
    LEAH_ROUTINE(
        "Leah",
        Arrays.asList(
            // Morning: At home
            new RoutinePoint(6, 8, new Location(25, 5, TileType.VILLAGE), "At home"),
            // Morning: At the lake for inspiration
            new RoutinePoint(8, 12, new Location(37, 80, TileType.VILLAGE), "At the lake for artistic inspiration"),
            // Afternoon: At the mountain for foraging
            new RoutinePoint(12, 16, new Location(30, 30, TileType.VILLAGE), "Foraging in the mountain"),
            // Evening: At the museum
            new RoutinePoint(16, 18, new Location(10, 80, TileType.VILLAGE), "At the museum"),
            // Evening: At the town square
            new RoutinePoint(18, 20, new Location(39, 78, TileType.VILLAGE), "At the town square"),
            // Night: Walking home
            new RoutinePoint(20, 22, new Location(25, 5, TileType.VILLAGE), "Walking home"),
            // Late night: Back home
            new RoutinePoint(22, 6, new Location(25, 5, TileType.VILLAGE), "Sleeping")
        )
    ),
    
    WILLY_ROUTINE(
        "Willy",
        Arrays.asList(
            // Morning: At home
            new RoutinePoint(6, 8, new Location(30, 5, TileType.VILLAGE), "At home"),
            // Morning: Walking to fish shop
            new RoutinePoint(8, 9, new Location(37, 140, TileType.VILLAGE), "Opening the fish shop"),
            // Work time: At the Fish Shop
            new RoutinePoint(9, 17, new Location(37, 140, TileType.VILLAGE), "At the Fish Shop"),
            // Evening: At the fish pond
            new RoutinePoint(17, 19, new Location(55, 80, TileType.VILLAGE), "At the fish pond"),
            // Evening: Walking home
            new RoutinePoint(19, 20, new Location(30, 5, TileType.VILLAGE), "Walking home"),
            // Night: At home
            new RoutinePoint(20, 22, new Location(30, 5, TileType.VILLAGE), "At home"),
            // Late night: Sleeping
            new RoutinePoint(22, 6, new Location(30, 5, TileType.VILLAGE), "Sleeping")
        )
    ),
    
    JOJO_ROUTINE(
        "Jojo",
        Arrays.asList(
            // Morning: At home
            new RoutinePoint(6, 8, new Location(35, 5, TileType.VILLAGE), "At home"),
            // Morning: Walking to Joja Mart
            new RoutinePoint(8, 9, new Location(50, 130, TileType.VILLAGE), "Opening Joja Mart"),
            // Work time: At Joja Mart
            new RoutinePoint(9, 21, new Location(50, 130, TileType.VILLAGE), "At Joja Mart"),
            // Evening: Walking home
            new RoutinePoint(21, 22, new Location(35, 5, TileType.VILLAGE), "Walking home"),
            // Night: At home
            new RoutinePoint(22, 6, new Location(35, 5, TileType.VILLAGE), "Sleeping")
        )
    ),
    
    HARVEY_ROUTINE(
        "Harvey",
        Arrays.asList(
            // Morning: At home
            new RoutinePoint(6, 8, new Location(40, 5, TileType.VILLAGE), "At home"),
            // Morning: Walking to clinic
            new RoutinePoint(8, 9, new Location(25, 20, TileType.VILLAGE), "Opening the clinic"),
            // Work time: At the Clinic
            new RoutinePoint(9, 17, new Location(25, 20, TileType.VILLAGE), "At the Clinic"),
            // Evening: At the town hall
            new RoutinePoint(17, 19, new Location(39, 78, TileType.VILLAGE), "At the town hall"),
            // Evening: Walking home
            new RoutinePoint(19, 20, new Location(40, 5, TileType.VILLAGE), "Walking home"),
            // Night: At home
            new RoutinePoint(20, 22, new Location(40, 5, TileType.VILLAGE), "At home"),
            // Late night: Sleeping
            new RoutinePoint(22, 6, new Location(40, 5, TileType.VILLAGE), "Sleeping")
        )
    ),
    
    ROBIN_ROUTINE(
        "Robin",
        Arrays.asList(
            // Morning: At home
            new RoutinePoint(6, 8, new Location(45, 5, TileType.VILLAGE), "At home"),
            // Morning: Walking to carpenter's shop
            new RoutinePoint(8, 9, new Location(55, 125, TileType.VILLAGE), "Opening the carpenter's shop"),
            // Work time: At the Carpenter's Shop
            new RoutinePoint(9, 17, new Location(55, 125, TileType.VILLAGE), "At the Carpenter's Shop"),
            // Evening: At the mayor's house
            new RoutinePoint(17, 19, new Location(15, 120, TileType.VILLAGE), "At the mayor's house"),
            // Evening: Walking home
            new RoutinePoint(19, 20, new Location(45, 5, TileType.VILLAGE), "Walking home"),
            // Night: At home
            new RoutinePoint(20, 22, new Location(45, 5, TileType.VILLAGE), "At home"),
            // Late night: Sleeping
            new RoutinePoint(22, 6, new Location(45, 5, TileType.VILLAGE), "Sleeping")
        )
    );

    private final String npcName;
    private final List<RoutinePoint> routinePoints;

    NPCRoutine(String npcName, List<RoutinePoint> routinePoints) {
        this.npcName = npcName;
        this.routinePoints = routinePoints;
    }

    public String getNpcName() {
        return npcName;
    }

    public List<RoutinePoint> getRoutinePoints() {
        return routinePoints;
    }

    public static NPCRoutine fromNpcName(String npcName) {
        for (NPCRoutine routine : values()) {
            if (routine.getNpcName().equalsIgnoreCase(npcName)) {
                return routine;
            }
        }
        return null;
    }

    public static class RoutinePoint {
        private final int startHour;
        private final int endHour;
        private final Location location;
        private final String activity;

        public RoutinePoint(int startHour, int endHour, Location location, String activity) {
            this.startHour = startHour;
            this.endHour = endHour;
            this.location = location;
            this.activity = activity;
        }

        public int getStartHour() {
            return startHour;
        }

        public int getEndHour() {
            return endHour;
        }

        public Location getLocation() {
            return location;
        }

        public String getActivity() {
            return activity;
        }

        public boolean isActiveAt(int hour) {
            if (startHour <= endHour) {
                return hour >= startHour && hour < endHour;
            } else {
                // Handles overnight routines (e.g., 22-6)
                return hour >= startHour || hour < endHour;
            }
        }
    }
}
