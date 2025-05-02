package org.example.models.Items.Tools;

import org.example.models.Items.Tool;


public class Scythe extends Tool {

    public Scythe() {
        super("Scythe", 0, "A tool for harvesting crops and cutting grass.",
                ToolType.SCYTHE, ToolMaterial.BASIC, 2, null);
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