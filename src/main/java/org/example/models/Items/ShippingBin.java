package org.example.models.Items;

public class ShippingBin extends Item{
    private int income;
    public ShippingBin() {
        super("Shipping Bin", 250);
        income = 0;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public void increaseIncome(int amount) {
        income += amount;
    }

    public void updateShippingBin() {
        income = 0;
    }
}
