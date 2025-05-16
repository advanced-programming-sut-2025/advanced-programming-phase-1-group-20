package org.example.models.savegame;

import com.esotericsoftware.kryo.Kryo;
import org.example.models.Barn;
import org.example.models.Coop;
import org.example.models.Items.*;
import org.example.models.MapDetails.*;
import org.example.models.Market;
import org.example.models.Player.Backpack;
import org.example.models.Player.Player;
import org.example.models.Player.Refrigerator;
import org.example.models.Player.Skill;
import org.example.models.common.Date;
import org.example.models.common.Location;
import org.example.models.entities.*;
import org.example.models.entities.animal.Animal;
import org.example.models.entities.animal.BarnAnimal;
import org.example.models.entities.animal.Fish;
import org.example.models.enums.*;
import org.example.models.enums.PlayerEnums.Gender;
import org.example.models.enums.PlayerEnums.Skills;
import org.example.models.enums.PlayerEnums.Tools;
import org.example.models.enums.Types.*;

import java.util.HashMap;

public class GameSerializer {
    public static Kryo createKryo() {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(true);
        kryo.register(Game.class);
        kryo.register(Date.class);
        kryo.register(Location.class);
        kryo.register(Animal.class);
        kryo.register(BarnAnimal.class);
        kryo.register(Coop.class);
        kryo.register(Fish.class);
        kryo.register(Friendship.class);
        kryo.register(Mob.class);
        kryo.register(NPC.class);
        kryo.register(NPCFriendship.class);
        kryo.register(Quest.class);
        kryo.register(QuestManager.class);
        kryo.register(TradeRequest.class);
        kryo.register(User.class);
        kryo.register(Item.class);
        kryo.register(ArtisanItem.class);
        kryo.register(CookingItem.class);
        kryo.register(CraftingItem.class);
        kryo.register(Crop.class);
        kryo.register(Food.class);
        kryo.register(Fruit.class);
        kryo.register(Mineral.class);
        kryo.register(Plant.class);
        kryo.register(Seed.class);
        kryo.register(ShippingBin.class);
        kryo.register(Tool.class);
        kryo.register(Tree.class);
        kryo.register(Building.class);
        kryo.register(Farm.class);
        kryo.register(GameMap.class);
        kryo.register(GreenHouse.class);
        kryo.register(Lake.class);
        kryo.register(Quarry.class);
        kryo.register(Tile.class);
        kryo.register(Village.class);
        kryo.register(Well.class);
        kryo.register(Backpack.class);
        kryo.register(Player.class);
        kryo.register(Refrigerator.class);
        kryo.register(Skill.class);
        kryo.register(Barn.class);
        kryo.register(Coop.class);
        kryo.register(Market.class);

        //enums
        kryo.register(Gender.class);
        kryo.register(Skills.class);
        kryo.register(Tools.class);

        kryo.register(ArtisanType.class);
        kryo.register(BarnAnimalTypes.class);
        kryo.register(BarnTypes.class);
        kryo.register(Cages.class);
        kryo.register(CookingType.class);
//        kryo.register(CoopAnimalTypes.class);
        kryo.register(CraftingType.class);
        kryo.register(CropType.class);
        kryo.register(FishType.class);
        kryo.register(MineralType.class);
        kryo.register(PlantType.class);
        kryo.register(SeedType.class);
        kryo.register(TileType.class);
        kryo.register(ToolFunctionality.class);
        kryo.register(ToolType.class);
        kryo.register(TreeType.class);
        kryo.register(Tool.ToolType.class);
        kryo.register(Tool.ToolMaterial.class);
        kryo.register(Tool.TrashCanType.class);
        kryo.register(Backpack.Type.class);

        kryo.register(Charactristic.class);
        kryo.register(Ingredients.class);
        kryo.register(Jobs.class);
        kryo.register(Markets.class);
        kryo.register(Npcs.class);
        kryo.register(Seasons.class);
        kryo.register(Weather.class);
        kryo.register(org.example.models.MapDetails.Farm[].class);
        kryo.register(org.example.models.common.Location[][].class);
        kryo.register(org.example.models.common.Location[].class);


        kryo.register(HashMap.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(java.util.LinkedList.class);
        kryo.register(java.util.HashSet.class);
        kryo.register(java.util.TreeMap.class);
        kryo.register(java.util.concurrent.ConcurrentHashMap.class);
        kryo.register(Object[].class);
        try {
            kryo.register(Class.forName("java.util.Arrays$ArrayList"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return kryo;
    }
}
