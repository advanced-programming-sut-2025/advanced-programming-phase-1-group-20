package org.example.models.MapDetails;

import org.example.models.Items.*;
import org.example.models.Market;
import org.example.models.Player.Player;
import org.example.models.common.Date;
import org.example.models.common.Location;
import org.example.models.enums.Markets;
import org.example.models.enums.Types.CropType;
import org.example.models.enums.Types.MineralType;
import org.example.models.enums.Types.TileType;
import org.example.models.enums.Types.TreeType;

import java.util.*;

public class GameMap {

    private Farm[] farms;
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

//        private void connectFarmsToVillage() {
//        int villageCenterX = width / 2;
//        int villageCenterY = height / 2;
//
//        for (Farm farm : farms) {
//            int farmCenterX = farm.getStartX() + farm.getWidth() / 2;
//            int farmCenterY = farm.getStartY() + farm.getHeight() / 2;
//
//            int currentX = farmCenterX;
//            int currentY = farmCenterY;
//
//            while (currentX != villageCenterX || currentY != villageCenterY) {
//                if (currentX < villageCenterX) {
//                    currentX++;
//                }
//                else if (currentX > villageCenterX) {
//                    currentX--;
//                }
//
//                if (currentY < villageCenterY) {
//                    currentY++;
//                }
//                else if (currentY > villageCenterY) {
//                    currentY--;
//                }
//
//                tiles[currentX][currentY] = new Location(currentX, currentY, TileType.PATH);
//            }
//        }
//    }

    private boolean canPlayerModifyTile(Player player, int x, int y) {
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

//    public void updateTurn(List<Player> players){
//        //updating artisans each turn.
//        for(Player player : players) {
//            updateArtisans(player);
//        }
//    }
    //TODO: چیکار میکنه دقیقا؟

}
