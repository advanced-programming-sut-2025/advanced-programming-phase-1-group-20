package org.example.common.models;

import org.example.common.models.Items.CookingItem;
import org.example.common.models.Items.CraftingItem;
import org.example.common.models.Items.Item;
import org.example.common.models.MapDetails.Building;
import org.example.common.models.Player.Backpack;
import org.example.common.models.Player.Player;
import org.example.common.models.enums.Seasons;
import org.example.common.models.enums.Types.CookingType;
import org.example.common.models.enums.Types.TileType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Market extends Building {
    // These lists are the "master" lists. They don't change during the day.
    private final List<Product> permanentStock;
    private final List<Product> springStock;
    private final List<Product> summerStock;
    private final List<Product> autumnStock;
    private final List<Product> winterStock;

    // This list represents the *current*, modifiable stock for the day.
    private transient List<Product> totalStock;

    // counterStock is not used for stock management anymore, but kept for compatibility.
    private transient List<Product> counterStock;

    private TileType tileType;
    private int startHour;
    private int endHour;
    private String[] menu;
    private String name;


    public Market(int x, int y, List<Product> permanentStock, List<Product> springStock,
                  List<Product> summerStock, List<Product> autumnStock, List<Product> winterStock,
                  int startHour, int endHour, String[] menu, String name, TileType tileType) {
        super(x, y, name, "market");
        this.permanentStock = permanentStock;
        this.springStock = springStock;
        this.summerStock = summerStock;
        this.autumnStock = autumnStock;
        this.winterStock = winterStock;
        this.totalStock = new ArrayList<>();
        initializeTotalStock(Seasons.SPRING);

        this.startHour = startHour;
        this.endHour = endHour;
        this.menu = menu;
        this.name = name;
        this.tileType = tileType;
    }

    public Market() {
        super(0, 0, "Market", "market");
        this.permanentStock = new ArrayList<>();
        this.springStock = new ArrayList<>();
        this.summerStock = new ArrayList<>();
        this.autumnStock = new ArrayList<>();
        this.winterStock = new ArrayList<>();
        this.totalStock = new ArrayList<>();
        this.counterStock = new ArrayList<>();
    }

    /**
     * **MODIFIED:** Creates a fresh, deep copy of products for the current day's stock.
     * This is the most important change. It ensures that we are modifying a copy,
     * not the original master lists.
     */
    public void initializeTotalStock(Seasons season) {
        // Deep copy permanent stock by creating new Product objects
        this.totalStock = permanentStock.stream()
            .map(p -> new Product(p.getItem(), p.getAmount(), p.getIngredient()))
            .collect(Collectors.toList());

        // Determine which seasonal stock to add
        List<Product> seasonal = new ArrayList<>();
        switch (season) {
            case SPRING: seasonal = springStock; break;
            case SUMMER: seasonal = summerStock; break;
            case AUTUMN: seasonal = autumnStock; break;
            case WINTER: seasonal = winterStock; break;
        }

        // Deep copy seasonal stock and add it to the total stock
        seasonal.stream()
            .map(p -> new Product(p.getItem(), p.getAmount(), p.getIngredient()))
            .forEach(this.totalStock::add);
    }

    // This method is now mostly for compatibility, the core logic relies on totalStock
    public void initializeCounterStock() {
        counterStock = new ArrayList<>();
    }

    // Helper method to find a product in a list by Item object
    private Optional<Product> findProductByItem(List<Product> productList, Item item) {
        return productList.stream()
            .filter(p -> p.getItem().getName().equalsIgnoreCase(item.getName()))
            .findFirst();
    }

    // Finds an item in the current day's available stock
    public Item getItem(String name) {
        return totalStock.stream()
            .filter(p -> p.getItem().getName().equalsIgnoreCase(name))
            .map(Product::getItem)
            .findFirst()
            .orElse(null);
    }


    public Product getProduct(String name) {
        return totalStock.stream().filter(p->p.getItem().getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * **FIXED:** Now correctly checks if the item exists in the current day's stock.
     */
    public boolean containsItem(Item item, Double count) {
        Optional<Product> productOpt = findProductByItem(totalStock, item);
        if (productOpt.isPresent()) {
            // Check if there's enough stock (for non-infinite items)
            return productOpt.get().getAmount() == Double.POSITIVE_INFINITY || productOpt.get().getAmount() >= count;
        }
        return false;
    }

    /**
     * **FIXED:** Now checks against the current day's stock (totalStock) and has correct logic.
     */
    public boolean checkItem(Player player, Item item, double count) {
        Optional<Product> productOpt = findProductByItem(totalStock, item);

        if (!productOpt.isPresent()) {
            System.out.println("Item not found in stock.");
            return false;
        }

        Product product = productOpt.get();

        if (product.getAmount() < count && product.getAmount() != Double.POSITIVE_INFINITY) {
            System.out.println("Not enough stock for this product. Available: " + (int)product.getAmount());
            return false;
        }

        if (item.getPrice() * count > player.getMoney()) {
            System.out.println("You don't have enough money.");
            return false;
        }

        // Specific checks remain the same
        switch (name) {
            case "Fish Shop":
                if (item.getName().equals("Fiberglass Rod") && player.getSkills().get(0).getLevel() <= 2) return false;
                if (item.getName().equals("Iridium Rod") && player.getSkills().get(0).getLevel() <= 4) return false;
                break;
            case "Pierre General Store":
                if (item.getName().equals("Large Pack") && player.getBackpack().getType() != Backpack.Type.Initial) return false;
                if (item.getName().equals("Deluxe Pack") && player.getBackpack().getType() != Backpack.Type.Big) return false;
                break;
        }

        return true;
    }


    /**
     * **MODIFIED:** Calls the new deductStock method upon a successful purchase.
     */
    public void checkOut(Player player, Item item, double count) {
        player.decreaseMoney((int) (item.getPrice() * count));

        // Handle special items that don't go to the backpack or have unique effects
        boolean isSpecial = handleSpecialCheckout(player, item, count);

        // If it wasn't a special item, add it to the backpack
        if (!isSpecial) {
            if (!player.getBackpack().add(item, (int) count)) {
                System.out.println("You don't have enough space in your backpack.");
                player.increaseMoney((int) (item.getPrice() * count)); // refund
                return; // Stop the transaction
            }
        }

        deductStock(item, count);
    }

    /**
     * **NEW (private):** This method contains the logic to actually reduce the stock.
     */
    private void deductStock(Item item, double count) {
        findProductByItem(totalStock, item).ifPresent(product -> {
            if (product.getAmount() != Double.POSITIVE_INFINITY) {
                product.setAmount(product.getAmount() - count);
            }
        });
    }

    /**
     * Handles items with special purchase logic. Returns true if the item was special.
     */
    private boolean handleSpecialCheckout(Player player, Item item, double count) {
        switch (item.getName()) {
            case "Large Pack":
                player.getBackpack().setType(Backpack.Type.Big);
                return true;
            case "Deluxe Pack":
                player.getBackpack().setType(Backpack.Type.Deluxe);
                return true;
            case "Dehydrator":
                player.addCraftingItem((CraftingItem) item);
                return true;
            // ... other special items
        }
        // Handle building purchases from Carpenter's Shop
        if (this.name.equals("Carpenters Shop") && (item.getName().toLowerCase().contains("barn") || item.getName().toLowerCase().contains("coop"))) {
            System.out.println("You bought a " + item.getName());
            // The logic for placing the building would be handled elsewhere
            return true;
        }
        return false;
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
    public void showProducts(List<Product> productList) {
        int c = 1;
        if (!productList.isEmpty()) {
            for (Product item : productList) {
                System.out.println("Item Code " + c + " : ");
                System.out.println("Name        : " + item.getItem().getName());
                System.out.println("Description : " + item.getItem().getDescription());
                System.out.println("Price       : " + item.getItem().getPrice());
                double stock = item.getAmount();
                System.out.println("Stock       : " + stock);
                c++;
                System.out.println("~~~~~~~~~~~~~~~~~~~");
            }
        } else {
            System.out.println("------------------------------");
            System.out.println();
            System.out.println("------------------------------");
        }
    }

    // --- GETTERS ---
    public TileType getTileType() { return tileType; }
    public List<Product> getPermanentStock() { return permanentStock; }
    public List<Product> getSpringStock() { return springStock; }
    public List<Product> getSummerStock() { return summerStock; }
    public List<Product> getAutumnStock() { return autumnStock; }
    public List<Product> getWinterStock() { return winterStock; }

    /**
     * **IMPORTANT:** This getter now returns the *modifiable* list for the current day.
     * Your UI should use this list to display the current, up-to-date stock.
     */
    public List<Product> getTotalStock() {
        return totalStock;
    }

    // This getter is kept for compatibility but is not used in the new stock logic.
    public List<Product> getCounterStock() {
        return counterStock;
    }
}
