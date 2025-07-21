package org.example.common.models.MapDetails;

import org.example.common.models.Barn;
import org.example.common.models.Coop;
import org.example.common.models.Player.Player;
import org.example.common.models.Player.Skill;
import org.example.common.models.common.Location;
import org.example.common.models.entities.animal.BarnAnimal;
import org.example.common.models.entities.animal.CoopAnimal;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private final List<Farm> farms;
    private Village village;

    public static final int TOTAL_WIDTH = 312;  // 78 + 156 + 78
    public static final int TOTAL_HEIGHT = 468; // 78 + 312 + 78
    public static final int VILLAGE_X = 78;     // Village starts at x=78
    public static final int VILLAGE_Y = 78;     // Village starts at y=78


    public GameMap() {
        this.farms = new ArrayList<>();
        this.village = new Village("Shemroon");
    }

    public List<Farm> getFarms() {
        return farms;
    }

    public Village getVillage() {
        return village;
    }

    public void addFarm(Farm farm) {
        farms.add(farm);
    }

    public Farm getFarmByIndex(int index) {
        for (Farm farm : farms) {
            if (farm.getFarmIndex() == index) {
                return farm;
            }
        }
        return null;
    }

    public Farm getFarmByName(String name) {
        for (Farm farm : farms) {
            if (farm.getName().equals(name)) {
                return farm;
            }
        }
        return null;
    }

    public Farm getFarmByPlayer(Player player) {
        for (Farm farm : farms) {
            if (farm.getOwner().equals(player)) {
                return farm;
            }
        }
        return null;
    }

    boolean canPlayerModifyTile(Player player, int x, int y) {
        for (Farm farm : farms) {
            if (farm.contains(x, y) && farm.getOwner().equals(player)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInOtherPlayersFarm(Player player, int x, int y) {
        for (Farm farm : farms) {
            if (player.getCurrentFarm() != farm) {
                if (!farm.contains(x, y) || farm.getOwner().getUser().getUsername().equals(player.getUser().getUsername())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateDailyGameMap(List<Player> players) {
        for (Player player : players) {
            Farm farm = getFarmByPlayer(player);

            //this only need to be update daily
            farm.updatePlants();
            farm.attackOfTheCrows();

            //it only updates for one hour each night for game to be easy to render.
            farm.updateArtisans();

            farm.updateLakeFish();
            village.updateShippingBin(player);

            processAnimalsEndOfDay(farm);
        }
    }

    public void updateTurn(List<Player> players) {
        for (Player player : players) {
            Farm farm = getFarmByPlayer(player);

            farm.updateArtisans();
            farm.updateLakeFish();
            village.updateShippingBin(player);
            for (Skill skill : player.getSkills()) {
                skill.updateState();
            }

        }
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for (Farm farm : farms) {
            players.add(farm.getOwner());
        }
        return players;
    }


    public Farm getFarmAtCoordinates(int x, int y) {
        // Farm 1 (Top-Left): (0, 234) to (78, 312)
        if (x >= 0 && x < 78 && y >= 234 && y < 312) {
            return getFarmByIndex(1);
        }
        // Farm 0 (Bottom-Left): (0, 156) to (78, 234)
        else if (x >= 0 && x < 78 && y >= 156 && y < 234) {
            return getFarmByIndex(0);
        }
        // Farm 2 (Top-Right): (234, 234) to (312, 312)
        else if (x >= 234 && x < 312 && y >= 234 && y < 312) {
            return getFarmByIndex(2);
        }
        // Farm 3 (Bottom-Right): (234, 156) to (312, 234)
        else if (x >= 234 && x < 312 && y >= 156 && y < 234) {
            return getFarmByIndex(3);
        }
        return null;
    }


    public boolean isInVillageArea(int x, int y) {
        return x >= VILLAGE_X && x < VILLAGE_X + Village.width &&
               y >= VILLAGE_Y && y < VILLAGE_Y + Village.height;
    }


    public Location getVillageLocation(int globalX, int globalY) {
        if (isInVillageArea(globalX, globalY)) {
            int localX = globalX - VILLAGE_X;
            int localY = globalY - VILLAGE_Y;
            return village.getItem(localX, localY);
        }
        return null;
    }


    public Location getFarmLocation(int globalX, int globalY) {
        Farm farm = getFarmAtCoordinates(globalX, globalY);
        if (farm != null) {
            // Convert to farm-local coordinates
            int localX = globalX;
            int localY = globalY;

            // Adjust based on farm position
            if (farm.getFarmIndex() == 1 || farm.getFarmIndex() == 0) {
                // Left side farms - no X adjustment needed
                localY = globalY - 156; // Adjust Y for bottom farms
            } else {
                // Right side farms
                localX = globalX - 234; // Adjust X for right side
                localY = globalY - 156; // Adjust Y for bottom farms
            }

            return farm.getItem(localX, localY);
        }
        return null;
    }


    public void setMoistureForRainyDaysFarms() {
        for (Farm farm : farms) {
            farm.setMoistureForRainyDays();
        }
    }

    public void processAnimalsEndOfDay(Farm farm) {
        // Process barn animals
        for (Barn barn : farm.getBarns()) {
            for (BarnAnimal animal : barn.getAnimals()) {
                // Apply daily happiness changes
                if (!animal.isHasBeenFed()) {
                    animal.decreaseHappiness(20);
                }

                if (!animal.isOutside() && !animal.isPetToday()) {
                    animal.decreaseHappiness(10);
                }

                // Reset daily flags
                animal.setPetToday(false);
                animal.setHasBeenFed(false);

                // Advance animal's production timer
                animal.advanceDay();
            }
        }

        for (Coop coop : farm.getCoops()) {
            for (CoopAnimal animal : coop.getAnimals()) {
                // Apply daily happiness changes
                if (!animal.isHasBeenFed()) {
                    animal.decreaseHappiness(20); // Decrease happiness if not fed
                }

                if (!animal.isOutside() && !animal.isPetToday()) {
                    animal.decreaseHappiness(10); // Decrease happiness if not petted
                }

                // Reset daily flags
                animal.setPetToday(false);
                animal.setHasBeenFed(false);

                animal.advanceDay();
            }
        }
    }

}
