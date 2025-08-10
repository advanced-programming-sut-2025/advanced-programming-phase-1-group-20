package org.example.common.models.MapDetails;

import org.example.common.models.App;
import org.example.common.models.Barn;
import org.example.common.models.Coop;
import org.example.common.models.Market;
import org.example.common.models.Player.Player;
import org.example.common.models.Player.Skill;
import org.example.common.models.common.Location;
import org.example.common.models.entities.animal.BarnAnimal;
import org.example.common.models.entities.animal.CoopAnimal;
import org.example.common.models.enums.Types.TileType;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private final List<Farm> farms;
    private Village village;
    private Location[][] tiles; // Unified tiles array for the entire map

    public static final int TOTAL_WIDTH = 234;  // 78 + 78 + 78
    public static final int TOTAL_HEIGHT = 156; // 78 + 78
    public static final int VILLAGE_X = 78;     // Village starts at x=78
    public static final int VILLAGE_Y = 0;      // Village starts at y=0


    public GameMap() {
        this.farms = new ArrayList<>();
        this.village = new Village("Shemroon");
        this.tiles = new Location[TOTAL_WIDTH][TOTAL_HEIGHT];
        initializeTiles();
    }

    private void initializeTiles() {
        for (int x = 0; x < TOTAL_WIDTH; x++) {
            for (int y = 0; y < TOTAL_HEIGHT; y++) {
                tiles[x][y] = new Location(x, y, TileType.Dirt);
            }
        }
    }


    public Location getTile(int x, int y) {
        if (x >= 0 && x < TOTAL_WIDTH && y >= 0 && y < TOTAL_HEIGHT) {
            return tiles[x][y];
        }
        return null;
    }


    public boolean setTile(int x, int y, Location location) {
        if (x >= 0 && x < TOTAL_WIDTH && y >= 0 && y < TOTAL_HEIGHT) {
            tiles[x][y] = location;
            return true;
        }
        return false;
    }

    public Location[][] getTiles() {
        return tiles;
    }


    public void updateTilesFromRegions() {
        // Update tiles from farms
        for (Farm farm : farms) {
            updateTilesFromFarm(farm);
        }

        // Update tiles from village
        updateTilesFromVillage();
    }

    private void updateTilesFromFarm(Farm farm) {
        int farmIndex = farm.getFarmIndex();
        int startX = 0, startY = 0;
        switch (farmIndex) {
            case 0: // Top-Left
                startX = 0;
                startY = 0;
                break;
            case 1: // Bottom-Left
                startX = 0;
                startY = 78;
                break;
            case 3: // Top-Right
                startX = 156;
                startY = 0;
                break;
            case 2: // Bottom-Right
                startX = 156;
                startY = 78;
                break;
            default:
                return;
        }

        for (int x = 0; x < Farm.width; x++) {
            for (int y = 0; y < Farm.height; y++) {
                Location farmTile = farm.getItem(x, y);
                if (farmTile != null) {
                    Location globalTile = new Location(startX + x, startY + y, farmTile.getTile());
                    globalTile.setType(farmTile.getType());
                    globalTile.setItem(farmTile.getItem());
                    globalTile.setShokhm(farmTile.getShokhm());
                    globalTile.setScarecrowThere(farmTile.isScarecrowThere());
                    tiles[startX + x][startY + y] = globalTile;
                }
            }
        }
    }


    private void updateTilesFromVillage() {
        for (int x = 0; x < Village.width; x++) {
            for (int y = 0; y < Village.height; y++) {
                Location villageTile = village.getItem(x, y);
                if (villageTile != null) {
                    Location globalTile = new Location(VILLAGE_X + x, VILLAGE_Y + y, villageTile.getTile());
                    globalTile.setType(villageTile.getType());
                    globalTile.setItem(villageTile.getItem());
                    globalTile.setShokhm(villageTile.getShokhm());
                    globalTile.setScarecrowThere(villageTile.isScarecrowThere());
                    tiles[VILLAGE_X + x][VILLAGE_Y + y] = globalTile;
                }
            }
        }
    }

    public boolean contains(int x, int y) {
        return x >= 0 && x < TOTAL_WIDTH && y >= 0 && y < TOTAL_HEIGHT;
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
        if (village != null && village.getMarkets() != null) {
            for (Market market : village.getMarkets()) {
                if (market != null) {
                    market.initializeTotalStock(App.getGame().getDate().getSeason());
                }
            }
        }

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
        if (x >= 0 && x < 78 && y >= 0 && y < 78) return getFarmByIndex(0);
        if (x >= 0 && x < 78 && y >= 78 && y < 156) return getFarmByIndex(1);
        if (x >= 156 && x < 234 && y >= 0 && y < 78) return getFarmByIndex(2);
        if (x >= 156 && x < 234 && y >= 78 && y < 156) return getFarmByIndex(3);
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
            int localX = globalX, localY = globalY;
            switch (farm.getFarmIndex()) {
                case 0: localX = globalX; localY = globalY; break;
                case 1: localX = globalX; localY = globalY - 78; break;
                case 2: localX = globalX - 156; localY = globalY; break;
                case 3: localX = globalX - 156; localY = globalY - 78; break;
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

    public void setVillage(Village village) {
        this.village = village;
    }

    public void setTiles(Location[][] tiles) {
        this.tiles = tiles;
    }
}
