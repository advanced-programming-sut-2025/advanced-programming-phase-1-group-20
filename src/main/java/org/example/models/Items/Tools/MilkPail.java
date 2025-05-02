package org.example.models.Items.Tools;

import org.example.models.Items.Tool;


public class MilkPail extends Tool {

    public MilkPail() {
        super("Milk Pail", 1000, "A tool for milking cows.",
                ToolType.MILK_PAIL, ToolMaterial.BASIC, 4, null);
    }


    @Override
    public boolean use(String direction) {
        // Implementation needed
        return true;
    }

    @Override
    public Tool upgrade() {
        return null;
    }
}