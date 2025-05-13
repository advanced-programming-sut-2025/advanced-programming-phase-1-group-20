package org.example.models.Items;

public class ShippingBin extends Item{
    private ShippingBinType type;
    public ShippingBin(ShippingBinType type) {
        super(type.getName(), 250);
        this.type = type;
    }

    public ShippingBinType getType() {
        return type;
    }

    public double getCoefficient() {
        return type.getCoefficient();
    }


    public enum ShippingBinType {
        REGULAR("Shipping Bin Regular" , 1.0),
        SILVER("Shipping Bin Silver" ,1.25),
        GOLD("Shipping Bin Gold" ,1.5),
        IRIDIUM("Shipping Bin Iridium" ,2);
        private final String name;
        private final double coefficient;
        ShippingBinType(String name , double coefficient) {
            this.name = name;
            this.coefficient = coefficient;
        }
        public double getCoefficient() {
            return coefficient;
        }

        public String getName() {
            return name;
        }
    }
}
