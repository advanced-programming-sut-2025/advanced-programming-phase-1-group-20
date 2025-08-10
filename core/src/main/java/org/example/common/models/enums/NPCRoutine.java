package org.example.common.models.enums;

import org.example.common.models.common.Location;
import org.example.common.models.enums.Types.TileType;

import java.util.Arrays;
import java.util.List;

public enum NPCRoutine {
    SEBASTIAN_ROUTINE(
        "Sebastian",
        Arrays.asList(
            new RoutinePoint(6, 10, new Location(10, 10, TileType.VILLAGE), "Sleeping in basement"),
            new RoutinePoint(10, 12, new Location(15, 15, TileType.VILLAGE), "Walking around village"),
            new RoutinePoint(12, 14, new Location(20, 20, TileType.VILLAGE), "At the lake"),
            new RoutinePoint(14, 16, new Location(25, 25, TileType.VILLAGE), "Visiting mountain"),
            new RoutinePoint(16, 18, new Location(30, 30, TileType.VILLAGE), "At the saloon"),
            new RoutinePoint(18, 22, new Location(10, 10, TileType.VILLAGE), "Back home"),
            new RoutinePoint(22, 6, new Location(10, 10, TileType.VILLAGE), "Sleeping")
        )
    ),
    
    ABIGAIL_ROUTINE(
        "Abigail",
        Arrays.asList(
            new RoutinePoint(6, 8, new Location(15, 15, TileType.VILLAGE), "At home"),
            new RoutinePoint(8, 10, new Location(20, 20, TileType.VILLAGE), "Walking to Pierre's"),
            new RoutinePoint(10, 12, new Location(35, 15, TileType.VILLAGE), "At Pierre's store"),
            new RoutinePoint(12, 14, new Location(25, 25, TileType.VILLAGE), "At the lake"),
            new RoutinePoint(14, 16, new Location(30, 30, TileType.VILLAGE), "At the mountain"),
            new RoutinePoint(16, 18, new Location(15, 15, TileType.VILLAGE), "Walking home"),
            new RoutinePoint(18, 22, new Location(15, 15, TileType.VILLAGE), "At home"),
            new RoutinePoint(22, 6, new Location(15, 15, TileType.VILLAGE), "Sleeping")
        )
    ),
    
    PIERRE_ROUTINE(
        "Pierre",
        Arrays.asList(
            new RoutinePoint(6, 9, new Location(35, 15, TileType.VILLAGE), "At home"),
            new RoutinePoint(9, 17, new Location(20, 125, TileType.VILLAGE), "At Pierre's General Store"),
            new RoutinePoint(17, 19, new Location(35, 15, TileType.VILLAGE), "Walking home"),
            new RoutinePoint(19, 22, new Location(35, 15, TileType.VILLAGE), "At home"),
            new RoutinePoint(22, 6, new Location(35, 15, TileType.VILLAGE), "Sleeping")
        )
    ),
    
    LEAH_ROUTINE(
        "Leah",
        Arrays.asList(
            new RoutinePoint(6, 8, new Location(25, 25, TileType.VILLAGE), "At home"),
            new RoutinePoint(8, 10, new Location(30, 30, TileType.VILLAGE), "At the mountain"),
            new RoutinePoint(10, 12, new Location(25, 25, TileType.VILLAGE), "At the lake"),
            new RoutinePoint(12, 14, new Location(20, 20, TileType.VILLAGE), "Walking around"),
            new RoutinePoint(14, 16, new Location(15, 15, TileType.VILLAGE), "Visiting Abigail"),
            new RoutinePoint(16, 18, new Location(25, 25, TileType.VILLAGE), "Back at the lake"),
            new RoutinePoint(18, 20, new Location(30, 30, TileType.VILLAGE), "At the mountain"),
            new RoutinePoint(20, 22, new Location(25, 25, TileType.VILLAGE), "At home"),
            new RoutinePoint(22, 6, new Location(25, 25, TileType.VILLAGE), "Sleeping")
        )
    ),
    
    WILLY_ROUTINE(
        "Willy",
        Arrays.asList(
            new RoutinePoint(6, 9, new Location(5, 35, TileType.VILLAGE), "At home"),
            new RoutinePoint(9, 17, new Location(37, 140, TileType.VILLAGE), "At the Fish Shop"),
            new RoutinePoint(17, 19, new Location(5, 35, TileType.VILLAGE), "Walking home"),
            new RoutinePoint(19, 22, new Location(5, 35, TileType.VILLAGE), "At home"),
            new RoutinePoint(22, 6, new Location(5, 35, TileType.VILLAGE), "Sleeping")
        )
    ),
    
    JOJO_ROUTINE(
        "Jojo",
        Arrays.asList(
            new RoutinePoint(6, 9, new Location(40, 20, TileType.VILLAGE), "At home"),
            new RoutinePoint(9, 23, new Location(50, 130, TileType.VILLAGE), "At Joja Mart"),
            new RoutinePoint(23, 6, new Location(40, 20, TileType.VILLAGE), "At home")
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
