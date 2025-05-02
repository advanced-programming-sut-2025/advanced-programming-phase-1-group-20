package org.example.models.Items.Tools;

import org.example.models.Items.Tool;

public class Shears extends Tool {

    public Shears() {
        super("Shears", 1000, "A tool for shearing sheep.",
                ToolType.SHEARS, ToolMaterial.BASIC, 4, null);
    }


    @Override
    public boolean use(String direction) {
        // Implementation will depend on the game mechanics
        // For now, just return true to indicate success
        return true;
    }

    @Override
    public Tool upgrade() {
        return null;
    }
}