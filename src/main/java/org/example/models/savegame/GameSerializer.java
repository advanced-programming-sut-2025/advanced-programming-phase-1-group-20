package org.example.models.savegame;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.MapSerializer;
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
import org.example.models.common.Result;
import org.example.models.entities.*;
import org.example.models.entities.animal.Animal;
import org.example.models.entities.animal.BarnAnimal;
import org.example.models.entities.animal.CoopAnimal;
import org.example.models.entities.animal.Fish;
import org.example.models.enums.*;
import org.example.models.enums.PlayerEnums.Gender;
import org.example.models.enums.PlayerEnums.Skills;
import org.example.models.enums.PlayerEnums.Tools;
import org.example.models.enums.Types.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for serialization and deserialization of game objects
 * using Kryo serialization library.
 */
public class GameSerializer {
    /**
     * Creates and configures a Kryo instance for serialization of the game.
     * Registers all relevant classes needed to serialize/deserialize game objects.
     *
     * @return Configured Kryo instance
     */
    public static Kryo createKryo() {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);

        // Key change #1: We'll set this to false for more flexibility with class changes
        kryo.setRegistrationRequired(false);

        // Key change #2: Set class loader to ensure all classes can be found
        kryo.setClassLoader(GameSerializer.class.getClassLoader());

        // Key change #3: Set default serializers for collections
        CollectionSerializer listSerializer = new CollectionSerializer();
        MapSerializer mapSerializer = new MapSerializer();

        // Register basic Java classes
        registerJavaClasses(kryo);

        // Register game entity classes
        registerEntityClasses(kryo);

        // Register item classes
        registerItemClasses(kryo);

        // Register map-related classes
        registerMapClasses(kryo);

        // Register player-related classes
        registerPlayerClasses(kryo);

        // Register enum classes
        registerEnumClasses(kryo);

        // Register collection classes and arrays
        registerCollectionClasses(kryo, listSerializer, mapSerializer);

        return kryo;
    }

    /**
     * Register common Java utility classes
     */
    private static void registerJavaClasses(Kryo kryo) {
        kryo.register(String.class);
        kryo.register(int.class);
        kryo.register(boolean.class);
        kryo.register(double.class);
        kryo.register(long.class);
        kryo.register(float.class);
        kryo.register(Integer.class);
        kryo.register(Boolean.class);
        kryo.register(Double.class);
        kryo.register(Long.class);
        kryo.register(Float.class);
        kryo.register(Object.class);

        // Register utility classes used in the game
        kryo.register(java.util.Date.class);
        kryo.register(java.util.Calendar.class);
        kryo.register(java.util.TimeZone.class);
        kryo.register(java.util.Locale.class);
        kryo.register(java.util.Currency.class);

        // Register primitive arrays
        kryo.register(byte[].class);
        kryo.register(char[].class);
        kryo.register(short[].class);
        kryo.register(int[].class);
        kryo.register(long[].class);
        kryo.register(float[].class);
        kryo.register(double[].class);
        kryo.register(boolean[].class);
        kryo.register(String[].class);
    }

    /**
     * Register game entity classes
     */
    private static void registerEntityClasses(Kryo kryo) {
        kryo.register(Game.class);
        kryo.register(Date.class);
        kryo.register(Location.class);
        kryo.register(Result.class);

        // Animal related classes
        kryo.register(Animal.class);
        kryo.register(BarnAnimal.class);
        kryo.register(CoopAnimal.class);
        kryo.register(Fish.class);

        // NPC and User related
        kryo.register(Friendship.class);
        kryo.register(Friendship.GiftRecord.class);
        kryo.register(Mob.class);
        kryo.register(NPC.class);
        kryo.register(NPCFriendship.class);
        kryo.register(NPCFriendship.GiftRecord.class);
        kryo.register(Quest.class);
        kryo.register(QuestManager.class);
        kryo.register(TradeRequest.class);
        kryo.register(User.class);
    }

    /**
     * Register item classes
     */
    private static void registerItemClasses(Kryo kryo) {
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
    }

    /**
     * Register map-related classes
     */
    private static void registerMapClasses(Kryo kryo) {
        kryo.register(Building.class);
        kryo.register(Farm.class);
        kryo.register(GameMap.class);
        kryo.register(GreenHouse.class);
        kryo.register(Quarry.class);
        kryo.register(Tile.class);
        kryo.register(Village.class);
        kryo.register(Well.class);

        // Key change #4: Specifically register the markets field in Village class
        // This is where your error was occurring
        try {
            kryo.register(Village.class.getDeclaredField("markets").getType());
        } catch (NoSuchFieldException e) {
            System.err.println("Warning: Could not register markets field type: " + e.getMessage());
        }

        // Also register Barn and Coop here as they relate to map structures
        kryo.register(Barn.class);
        kryo.register(Coop.class);
        kryo.register(Market.class);
    }

    /**
     * Register player-related classes
     */
    private static void registerPlayerClasses(Kryo kryo) {
        kryo.register(Backpack.class);
        kryo.register(Player.class);
        kryo.register(Refrigerator.class);
        kryo.register(Skill.class);
    }

    /**
     * Register enum classes
     */
    private static void registerEnumClasses(Kryo kryo) {
        // Player enums
        kryo.register(Gender.class);
        kryo.register(Skills.class);
        kryo.register(Tools.class);

        // Type enums
        kryo.register(ArtisanType.class);
        kryo.register(BarnAnimalTypes.class);
        kryo.register(BarnTypes.class);
        kryo.register(Cages.class);
        kryo.register(CookingType.class);
        kryo.register(CraftingType.class);
        kryo.register(CropType.class);
        kryo.register(FishType.class);
        kryo.register(MineralType.class);
        kryo.register(PlantType.class);
        kryo.register(Quality.class);
        kryo.register(SeedType.class);
        kryo.register(TileType.class);
        kryo.register(ToolFunctionality.class);
        kryo.register(ToolType.class);
        kryo.register(TreeType.class);

        // Tool-specific enums
        kryo.register(Tool.ToolType.class);
        kryo.register(Tool.ToolMaterial.class);
        kryo.register(Tool.TrashCanType.class);
        kryo.register(Backpack.Type.class);

        // Other enums
        kryo.register(Charactristic.class);
        kryo.register(Ingredients.class);
        kryo.register(Jobs.class);
        kryo.register(Markets.class);
        kryo.register(Npcs.class);
        kryo.register(Seasons.class);
        kryo.register(Weather.class);
    }

    /**
     * Register collection and array classes
     */
    private static void registerCollectionClasses(Kryo kryo, CollectionSerializer listSerializer, MapSerializer mapSerializer) {
        // Key change #5: Use specialized serializers for collections
        kryo.register(HashMap.class, mapSerializer);
        kryo.register(ArrayList.class, listSerializer);
        kryo.register(List.class, listSerializer);
        kryo.register(Map.class, mapSerializer);

        // Arrays registration
        kryo.register(int[].class);
        kryo.register(String[].class);
        kryo.register(Object[].class);
        kryo.register(Farm[].class);
        kryo.register(Location[][].class);
        kryo.register(Location[].class);
        kryo.register(Seasons[].class);

        // Key change #6: Register generic array classes
        kryo.register(Item[].class);
        kryo.register(Crop[].class);
        kryo.register(NPC[].class);
        kryo.register(Market[].class);

        // Special Java collection implementations
        try {
            Class<?> arraysListClass = Class.forName("java.util.Arrays$ArrayList");
            kryo.register(arraysListClass, listSerializer);

            // Register more generic collection implementations with appropriate serializers
            kryo.register(Class.forName("java.util.LinkedList"), listSerializer);
            kryo.register(Class.forName("java.util.HashSet"), listSerializer);
            kryo.register(Class.forName("java.util.TreeMap"), mapSerializer);
            kryo.register(Class.forName("java.util.concurrent.ConcurrentHashMap"), mapSerializer);
        } catch (ClassNotFoundException e) {
            System.err.println("Warning: Some collection classes could not be registered: " + e.getMessage());
        }
    }
}