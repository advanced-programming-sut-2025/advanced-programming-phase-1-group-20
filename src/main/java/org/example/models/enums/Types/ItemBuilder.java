package org.example.models.enums.Types;

import org.example.models.Items.*;

public class ItemBuilder {
    public static Item build(String name){
        MineralType mineralType = MineralType.fromName(name);
        if(mineralType != null){
            return new Mineral(mineralType);
        }
        CookingType cookingType = CookingType.fromName(name);
        if(cookingType != null){
            return new CookingItem(cookingType);
        }
        CraftingType craftingType = CraftingType.fromName(name);
        if(craftingType != null){
            return new CraftingItem(craftingType);
        }
        CropType cropType = CropType.fromName(name);
        if(cropType != null){
            return new Crop(cropType);
        }
        PlantType plantType = PlantType.fromName(name);
        if(plantType != null){
            return new Plant(plantType);
        }
        SeedType seedType = SeedType.fromName(name);
        if(seedType != null){
            return new Seed(seedType);
        }
        TreeType treeType = TreeType.fromName(name);
        if(treeType != null){
            return new Tree(treeType);
        }
        return null;
    }
}
