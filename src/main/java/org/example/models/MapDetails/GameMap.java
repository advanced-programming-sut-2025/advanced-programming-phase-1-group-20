package org.example.models.MapDetails;

import org.example.models.Player.Player;

import java.util.List;

public class GameMap {

    private final Farm[] farms;
    private Village village;

    public GameMap() {
        this.farms = new Farm[4];
        this.village = null;
    }

    public Farm[] getFarms() {
        return farms;
    }

    public Village getVillage() {
        return village;
    }

    public void setVillage(Village village) {
        this.village = village;
    }

    public void addFarm(Farm farm) {
        farms[farm.getFarmIndex()] = farm;
    }

    public Farm getFarmByIndex(int index) {
        return farms[index];
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
            if (farm.contains(x, y) && !farm.getOwner().equals(player)) {
                return true;
            }
        }
        return false;
    }

    public void updateDailyGameMap(List<Player> players) {
        for (Player player : players) {
            Farm farm = getFarmByPlayer(player);
            farm.updatePlants();
            farm.updateArtisans();
            farm.updateLakeFish();
            village.updateShippingBin(player);
        }
    }

    public void updateTurn(List<Player> players) {
        for (Player player : players) {
            Farm farm = getFarmByPlayer(player);
            farm.updatePlants();
            farm.updateArtisans();
            farm.updateLakeFish();
            village.updateShippingBin(player);
        }
    }

}
