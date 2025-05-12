package org.example.models;

import org.example.models.Items.Item;
import org.example.models.MapDetails.Building;
import org.example.models.Player.Player;
import org.example.models.enums.Seasons;

import java.util.HashMap;

public class Market extends Building {
    private HashMap<Item, Double> permanentStock;
    private HashMap<Item, Double> springStock;
    private HashMap<Item, Double> summerStock;
    private HashMap<Item, Double> autumnStock;
    private HashMap<Item, Double> winterStock;
    HashMap<Item, Double> totalStock = permanentStock;
    HashMap<Item, Double> counterStock = totalStock;
    private int startHour;
    private int endHour;
    private String[] menu;
    private String name;

    public Market(int x , int y , HashMap<Item, Double> permanentStock, HashMap<Item, Double> springStock, HashMap<Item, Double> summerStock, HashMap<Item, Double> autumnStock, HashMap<Item, Double> winterStock, int startHour, int endHour, String[] menu , String name) {
        super(x,y,name,"market");
        this.permanentStock = permanentStock;
        this.springStock = springStock;
        this.summerStock = summerStock;
        this.autumnStock = autumnStock;
        this.winterStock = winterStock;
        this.startHour = startHour;
        this.endHour = endHour;
        this.menu = menu;
        this.name = name;
        initializeCounterStock();
    }


    public void initializeCounterStock(){
        counterStock.putAll(springStock);
        counterStock.putAll(summerStock);
        counterStock.putAll(autumnStock);
        counterStock.putAll(winterStock);
    }

    public void updateCounterStock(){
        counterStock.replaceAll((key , value ) -> 0.0);
    }


    public void showAllProducts() {
        System.out.println("Permanent Stock");
        showProducts(permanentStock);

        System.out.println("Spring Stock");
        showProducts(springStock);

        System.out.println("Summer Stock");
        showProducts(summerStock);

        System.out.println("Autumn Stock");
        showProducts(autumnStock);

        System.out.println("Winter Stock");
        showProducts(winterStock);
    }

    public void showAvailableProducts(Seasons season) {
        System.out.println("Permanent Stock");
        showProducts(permanentStock);
        switch (season) {
            case SPRING:
                System.out.println("Spring Stock");
                showProducts(springStock);
                break;
            case SUMMER:
                System.out.println("Summer Stock");
                showProducts(summerStock);
                break;
            case AUTUMN:
                System.out.println("Autumn Stock");
                showProducts(autumnStock);
                break;
            case WINTER:
                System.out.println("Winter Stock");
                showProducts(winterStock);
                break;
        }
    }

    public void showProducts(HashMap<Item, Double> permanentStock) {
        int c = 1;
        if (!permanentStock.isEmpty()) {
            for (Item item : permanentStock.keySet()) {
                System.out.println("Item Code " + c + " : ");
                System.out.println("Name        : " + item.getName());
                System.out.println("Description : " + item.getDescription());
                System.out.println("Price       : " + item.getPrice());
                double stock = permanentStock.get(item) - counterStock.get(item);
                String formatedStock = String.format("%.0f" , stock);
                System.out.println("Stock       : " + formatedStock);
                c++;
            }
        } else {
            System.out.println("------------------------------");
            System.out.println();
            System.out.println("------------------------------");
        }
    }


    public Item getItem(String name) {
        for(Item item : permanentStock.keySet()) {
            if(name.equals(item.getName())) {
                return item;
            }
        }

        for(Item item : springStock.keySet()) {
            if(name.equals(item.getName())) {
                return item;
            }
        }

        for(Item item : summerStock.keySet()) {
            if(name.equals(item.getName())) {
                return item;
            }
        }

        for(Item item : autumnStock.keySet()) {
            if(name.equals(item.getName())) {
                return item;
            }
        }

        for(Item item : winterStock.keySet()) {
            if(name.equals(item.getName())) {
                return item;
            }
        }

        return null;
    }

    public void initializeTotalStock(Seasons season) {
        switch (season) {
            case SPRING:
                totalStock.putAll(springStock);
                break;
            case SUMMER:
                totalStock.putAll(summerStock);
                break;
            case AUTUMN:
                totalStock.putAll(autumnStock);
                break;
            case WINTER:
                totalStock.putAll(winterStock);
                break;
        }
    }


    public boolean containsItem(Item item, Double count) {
        if (totalStock.containsKey(item)) {
            if (totalStock.get(item) >= count) {
                return true;
            } else {
                System.out.println("Not enough stock for this product");
                return false;
            }
        }
        System.out.println("Item not found");
        return false;
    }


    public boolean checkItem(Player player , Item item , double count) {
        switch (name){
            case "Fish Shop":
                return checkFishShop(player,item,count);
            case "Pierre General Store":
                return checkPirreGeneralStore(player,item,count);
            case "Black Smith":
                return checkBlackSmith(player,item,count);
            case "Star Drop Saloon":
                return checkStarDropSaloon(player,item,count);
            case "Marnie Shop":
                return checkMarnieShop(player,item,count);
            case "Carpenters Shop":
                return checkCarpentersShop(player,item,count);
            case "Joja Market":
                return checkJojaMarket(player,item,count);
            default:
                return false;
        }
    }

    private boolean checkFishShop(Player player , Item item , double count){
        if(!(item.getName().equals("Fish Smoker") || item.getName().equals("Trout Soup") || item.getName().equals("Bamboo Pole") || item.getName().equals("Training Rod"))) {
            //TODO : check skills for these items.
        }
        if(!(count + counterStock.get(item) <= totalStock.get(item))) {
            return false;
        }
        if(item.getPrice() * count <= player.getMoney()) {
            return true;
        }
        return false;
    }

    private boolean checkPirreGeneralStore(Player player , Item item , double count){
        if(item.getName().equals("Large Pack") || item.getName().equals("Deluxe Pack")) {

        }
        if(!(count + counterStock.get(item) <= totalStock.get(item))) {
            return false;
        }
        if(item.getPrice() * count <= player.getMoney()) {
            return true;
        }
        return false;
    }

    private boolean checkBlackSmith(Player player, Item item, double count) {
        if(!(count + counterStock.get(item) <= totalStock.get(item))) {
            return false;
        }
        if(item.getPrice() * count <= player.getMoney()) {
            return true;
        }
        return false;
    }

    private boolean checkStarDropSaloon(Player player, Item item , double count) {
        if(!(count + counterStock.get(item) <= totalStock.get(item))) {
            return false;
        }
        if(item.getPrice() * count <= player.getMoney()) {
            return true;
        }
        return false;
    }

    private boolean checkMarnieShop(Player player, Item item , double count) {
        //TODO : this method needed to be completed after barns ready.
        return false;
    }

    private boolean checkCarpentersShop(Player player, Item item , double count) {
        //TODO : this method needed to be completed after barns ready.
        return false;
    }

    private boolean checkJojaMarket(Player player, Item item , double count) {
        if(!(count + counterStock.get(item) <= totalStock.get(item))) {
            return false;
        }
        if(item.getPrice() * count <= player.getMoney()) {
            return true;
        }
        return false;
    }


    public void checkOut(Player player, Item item , double count) {
        switch (name){
            case "Fish Shop":
                checkOutFishShop(player,item,count);
            case "Pierre General Store":
                checkOutPirreGeneralStore(player,item,count);
            case "Black Smith":
                checkOutBlackSmith(player,item,count);
            case "Star Drop Saloon":
                checkOutStarDropSaloon(player,item,count);
            case "Marnie Shop":
                checkOutMarnieShop(player,item,count);
            case "Carpenters Shop":
                checkOutCarpentersShop(player,item,count);
            case "Joja Market":
                checkOutJojaMarket(player,item,count);
        }
    }

    private void checkOutFishShop(Player player, Item item , double count) {
        player.decreaseMoney((int) (item.getPrice()*count));
        double stock = count + counterStock.get(item);
        counterStock.put(item, stock);
    }

    private void checkOutPirreGeneralStore(Player player, Item item , double count) {
        if(item.getName().equals("Large Pack") || item.getName().equals("Deluxe Pack")) {

        }
        player.decreaseMoney((int) (item.getPrice()*count));
        double stock = count + counterStock.get(item);
        counterStock.put(item, stock);
    }

    private void checkOutBlackSmith(Player player, Item item , double count) {
        player.decreaseMoney((int) (item.getPrice()*count));
        double stock = count + counterStock.get(item);
        counterStock.put(item, stock);
    }

    private void checkOutStarDropSaloon(Player player, Item item , double count) {
        player.decreaseMoney((int) (item.getPrice()*count));
        double stock = count + counterStock.get(item);
        counterStock.put(item, stock);
    }

    private void checkOutMarnieShop(Player player, Item item , double count) {
    }

    private void checkOutCarpentersShop(Player player, Item item , double count) {
    }

    private void checkOutJojaMarket(Player player, Item item , double count) {
        player.decreaseMoney((int) (item.getPrice()*count));
        double stock = count + counterStock.get(item);
        counterStock.put(item, stock);
    }


}
