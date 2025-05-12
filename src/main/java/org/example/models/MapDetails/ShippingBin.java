package org.example.models.MapDetails;

import java.io.Serializable;

public class ShippingBin extends Building implements Serializable {
    public ShippingBin(int x, int y, String name, String type) {
        super(x, y, name, type);
    }
}
