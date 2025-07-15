package org.example.views;

import com.badlogic.gdx.graphics.Color;
import org.example.models.common.Date;
import org.example.models.enums.Seasons;
import org.example.models.enums.Weather;

public class Lighting {
    public enum LightLevel {
        DAWN,       // 9 AM - Early morning light
        MORNING,    // 10-12 AM - Bright morning
        MIDDAY,     // 13-15 PM - Peak daylight
        AFTERNOON,  // 16-18 PM - Warm afternoon
        EVENING,    // 19-20 PM - Golden hour
        NIGHT       // 21 PM - Dark/moonlight
    }

    private LightLevel currentLightLevel;
    private float lightIntensity; // 0.0 to 1.0
    private int[] lightColor; // RGB values

    public Lighting() {
        this.currentLightLevel = LightLevel.MORNING;
        this.lightIntensity = 0.8f;
        this.lightColor = new int[]{255, 255, 255}; // Default white light
    }


    public void updateLighting(Date gameDate) {
        int hour = gameDate.getHour();
        Seasons season = gameDate.getSeason();
        Weather weather = gameDate.getWeatherToday();

        // Determine base light level from time
        LightLevel baseLightLevel = getLightLevelFromHour(hour);

        // Adjust for season
        float seasonalIntensity = getSeasonalLightIntensity(season, baseLightLevel);

        // Adjust for weather
        float weatherIntensity = getWeatherLightModifier(weather);

        // Calculate final values
        this.currentLightLevel = baseLightLevel;
        this.lightIntensity = Math.max(0.1f, Math.min(1.0f, seasonalIntensity * weatherIntensity));
        this.lightColor = calculateLightColor(hour, season, weather);
    }

    /**
     * Determines light level based on hour of day
     */
    private LightLevel getLightLevelFromHour(int hour) {
        return switch (hour) {
            case 9 -> LightLevel.DAWN;
            case 10, 11, 12 -> LightLevel.MORNING;
            case 13, 14, 15 -> LightLevel.MIDDAY;
            case 16, 17, 18 -> LightLevel.AFTERNOON;
            case 19, 20 -> LightLevel.EVENING;
            case 21 -> LightLevel.NIGHT;
            default -> LightLevel.MORNING; // Fallback
        };
    }

    private float getSeasonalLightIntensity(Seasons season, LightLevel lightLevel) {
        float baseIntensity = switch (lightLevel) {
            case DAWN -> 0.3f;
            case MORNING -> 0.7f;
            case MIDDAY -> 1.0f;
            case AFTERNOON -> 0.8f;
            case EVENING -> 0.4f;
            case NIGHT -> 0.1f;
        };

        // Seasonal modifiers
        float seasonalModifier = switch (season) {
            case SPRING -> 0.9f;  // Bright spring light
            case SUMMER -> 1.1f;  // Intense summer light
            case AUTUMN -> 0.8f;  // Softer autumn light
            case WINTER -> 0.7f;  // Dim winter light
        };

        return baseIntensity * seasonalModifier;
    }


    private float getWeatherLightModifier(Weather weather) {
        return switch (weather) {
            case SUNNY -> 1.0f;   // Full brightness
            case RAINY -> 0.6f;   // Dimmed by clouds
            case STORMY -> 0.4f;  // Very dim, storm clouds
            case SNOWY -> 0.7f;   // Bright but diffused
            case GREENHOUSE -> 0.0F;
        };
    }

    private int[] calculateLightColor(int hour, Seasons season, Weather weather) {
        int[] baseColor = getBaseColorFromHour(hour);

        // Apply seasonal tint
        baseColor = applySeasonalTint(baseColor, season);

        // Apply weather effects
        baseColor = applyWeatherTint(baseColor, weather);

        return baseColor;
    }


    private int[] getBaseColorFromHour(int hour) {
        return switch (hour) {
            case 9 -> new int[]{255, 200, 150};  // Dawn - warm orange
            case 10, 11, 12 -> new int[]{255, 255, 240};  // Morning - soft white
            case 13, 14, 15 -> new int[]{255, 255, 255};  // Midday - pure white
            case 16, 17, 18 -> new int[]{255, 220, 180};  // Afternoon - warm
            case 19, 20 -> new int[]{255, 150, 100};      // Evening - golden
            case 21 -> new int[]{100, 100, 150};          // Night - blue tint
            default -> new int[]{255, 255, 255};          // Default white
        };
    }


    private int[] applySeasonalTint(int[] color, Seasons season) {
        float[] tint = switch (season) {
            case SPRING -> new float[]{1.0f, 1.05f, 0.95f}; // Slightly green
            case SUMMER -> new float[]{1.1f, 1.0f, 0.9f};   // Warmer, more yellow
            case AUTUMN -> new float[]{1.1f, 0.9f, 0.8f};   // Orange/red tint
            case WINTER -> new float[]{0.9f, 0.95f, 1.1f};  // Cooler, more blue
        };

        return new int[]{
            (int) Math.min(255, color[0] * tint[0]),
            (int) Math.min(255, color[1] * tint[1]),
            (int) Math.min(255, color[2] * tint[2])
        };
    }


    private int[] applyWeatherTint(int[] color, Weather weather) {
        float[] modifier = switch (weather) {
            case SUNNY -> new float[]{1.0f, 1.0f, 1.0f};    // No change
            case RAINY -> new float[]{0.8f, 0.8f, 0.9f};    // Slightly blue/gray
            case STORMY -> new float[]{0.6f, 0.6f, 0.7f};   // Dark and grayish
            case SNOWY -> new float[]{0.9f, 0.9f, 1.0f};    // Slight blue tint
            case GREENHOUSE -> null;
        };

        assert modifier != null;
        return new int[]{
            (int) (color[0] * modifier[0]),
            (int) (color[1] * modifier[1]),
            (int) (color[2] * modifier[2])
        };
    }

    public LightLevel getCurrentLightLevel() {
        return currentLightLevel;
    }

    public float getLightIntensity() {
        return lightIntensity;
    }

    public int[] getLightColor() {
        return lightColor.clone();
    }


    public Color getLibGdxColor() {
        return new Color(
            lightColor[0] / 255f,
            lightColor[1] / 255f,
            lightColor[2] / 255f,
            lightIntensity
        );
    }
}
