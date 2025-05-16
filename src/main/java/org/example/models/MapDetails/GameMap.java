package org.example.models.MapDetails;

import org.example.models.Player.Player;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private final List<Farm> farms;
    private Village village;

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
        }
    }

    public void updateTurn(List<Player> players) {
        for (Player player : players) {
            Farm farm = getFarmByPlayer(player);

            farm.updateArtisans();
            farm.updateLakeFish();
            village.updateShippingBin(player);

        }
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        for (Farm farm : farms) {
            players.add(farm.getOwner());
        }
        return players;
    }

}
