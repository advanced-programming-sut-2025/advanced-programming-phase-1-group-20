package org.example.models.enums;

import java.util.Map;

public enum Ingredients {
    FriedEgg(Map.of("egg", 1)),
    BakedFish(Map.of("Sardine", 1, "Salmon", 1, "wheat", 1)),
    Salad(Map.of("leek", 1, "dandelion", 1)),
    Omelet(Map.of("egg", 1, "milk", 1)),
    pumpkinpie(Map.of("pumpkin", 1, "wheat flour", 1, "milk", 1, "sugar", 1)),
    spaghetti(Map.of("wheat flour", 1, "tomato", 1)),
    pizza(Map.of("wheat flour", 1, "tomato", 1, "cheese", 1)),
    Tortilla(Map.of("corn", 1)),
    MakiRoll(Map.of("any fish", 1, "rice", 1, "fiber", 1)),
    TripleShotEspresso(Map.of("coffee", 3)),
    Cookie(Map.of("wheat flour", 1, "sugar", 1, "egg", 1)),
    hashbrowns(Map.of("potato", 1, "oil", 1)),
    pancakes(Map.of("wheat flour", 1, "egg", 1)),
    fruitsalad(Map.of("blueberry", 1, "melon", 1, "apricot", 1)),
    redplate(Map.of("red cabbage", 1, "radish", 1)),
    bread(Map.of("wheat flour", 1)),
    salmondinner(Map.of("salmon", 1, "Amaranth", 1, "Kale", 1)),
    vegetablemedley(Map.of("tomato", 1, "beet", 1)),
    farmerslunch(Map.of("omelet", 1, "parsnip", 1)),
    survivalburger(Map.of("bread", 1, "carrot", 1, "eggplant", 1)),
    dishOtheSea(Map.of("sardines", 2, "hash browns", 1)),
    seaformPudding(Map.of("Flounder", 1, "midnight carp", 1)),
    minerstreat(Map.of("carrot", 2, "sugar", 1, "milk", 1));

    private final Map<String, Integer> ingredients;

    Ingredients(Map<String, Integer> ingredients) {
        this.ingredients = ingredients;
    }

    public Map<String, Integer> getIngredients() {
        return ingredients;
    }
}