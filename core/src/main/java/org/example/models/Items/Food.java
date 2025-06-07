package org.example.models.Items;

import org.example.models.Player.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Food extends Item {
    private int energy;
    private String buffer;

    public Food(String foodName, int baseSellPrice, int energy, String buffer) {
        super(foodName, baseSellPrice);
        this.energy = energy;
        this.buffer = buffer;
    }

    public int getEnergy() {
        return energy;
    }


    public String getBuffer() {
        return buffer;
    }

    public void setBuffer(Player player) {
        String regex = "^(Max Energy|Farming|Mining|Fishing|Foraging)\\s*\\(\\s*(?<hours>\\d+)\\s+hours\\s*\\)$";
        if (buffer.matches(regex)) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(buffer);
            String s = matcher.group(0);
            int hours = Integer.parseInt(matcher.group(1));
            switch (s) {
                case "Max Energy" -> {
                    player.setEnergyUnlimited();
                }
                case "Farming" -> {
                    player.getSkills().get(0).maxSkill(hours, energy);
                }
                case "Mining" -> {
                    player.getSkills().get(1).maxSkill(hours, energy);
                }
                case "Foraging" -> {
                    player.getSkills().get(2).maxSkill(hours, energy);
                }
                case "Fishing" -> {
                    player.getSkills().get(3).maxSkill(hours, energy);
                }
            }
        }
    }
}
