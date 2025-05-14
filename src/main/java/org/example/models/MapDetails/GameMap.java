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

    private final Farm[] farms;
    private final Player currentPlayer;

    public GameMap(Player player) {
        this.farms = new Farm[4];
        this.currentPlayer = player;
    }

//    private void connectFarmsToVillage() {
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
//                } else if (currentX > villageCenterX) {
//                    currentX--;
//                }
//
//                if (currentY < villageCenterY) {
//                    currentY++;
//                } else if (currentY > villageCenterY) {
//                    currentY--;
//                }
//
//                tiles[currentX][currentY] = new Location(currentX, currentY, TileType.PATH);
//            }
//        }
//    }

//    private boolean canPlayerModifyTile(Player player, int x, int y) {
//        for (Farm farm : farms) {
//            if (farm.contains(x, y) && farm.getOwner().equals(player)) {
//                return true;
//            }
//        }
//
//        return tiles[x][y].getType().equals("path");
//    }


//    public List<Location> getPassableNeighbors(Location location) {
//        List<Location> result = new ArrayList<>();
//        int x = location.getX();
//        int y = location.getY();
//
//        int[][] directions = {
//                {-1, -1}, {-1, 0}, {-1, 1},
//                {0, -1}, {0, 1},
//                {1, -1}, {1, 0}, {1, 1}
//        };
//
//        for (int[] dir : directions) {
//            int newX = x + dir[0];
//            int newY = y + dir[1];
//
//            if (isInBounds(newX, newY)) {
//                Location neighbor = tiles[newX][newY];
//                if (isPassable(neighbor)) {
//                    result.add(neighbor);
//                }
//            }
//        }
//
//        return result;
//    }

//    public boolean isInOtherPlayersFarm(Player player, int x, int y) {
//        for (Farm farm : farms) {
//            if (farm.contains(x, y) && !farm.getOwner().equals(player)) {
//                return true;
//            }
//        }
//        return false;
//    }

    //update part.
    public void updateDailyGameMap(List<Player> players) {
        //this is a daily update (after every day)
        updatePlants();
        for(Player player : players) {
            updateShippingBin(player);
        }

        //TODO : this method is not complete just need to get the shippingBins
    }

    public void updateTurn(List<Player> players){
        //updating artisans each turn.
        for(Player player : players) {
            updateArtisans(player);
        }
    }

    public void updateShippingBin(Player player) {
        //TODO : we must get this bin from map.
        ShippingBin[] bins = new ShippingBin[4];
        bins[0] = new ShippingBin();
        bins[1] = new ShippingBin();
        bins[2] = new ShippingBin();
        bins[3] = new ShippingBin();

        player.increaseMoney(bins[0].getIncome(player));
        bins[1].updateShippingBin(player);


        player.increaseMoney(bins[1].getIncome(player));
        bins[1].updateShippingBin(player);

        player.increaseMoney(bins[2].getIncome(player));
        bins[2].updateShippingBin(player);

        player.increaseMoney(bins[3].getIncome(player));
        bins[3].updateShippingBin(player);
    }

}
