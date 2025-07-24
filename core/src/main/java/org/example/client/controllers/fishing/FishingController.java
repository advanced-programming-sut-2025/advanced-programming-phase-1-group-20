package org.example.client.controllers.fishing;

import com.badlogic.gdx.math.MathUtils;
import org.example.client.views.fishing.FishingMiniGame;
import org.example.common.models.App;
import org.example.common.models.Player.Player;
import org.example.common.models.enums.Types.Quality;
import org.example.common.models.enums.Types.FishType;
import org.example.common.models.enums.Seasons;
import org.example.common.models.enums.Weather;

import java.util.ArrayList;
import java.util.HashMap;

public class FishingController {
    private enum FishMotionTypes {
        MIXED(5),
        SMOOTH(5),
        SINKER(5),
        FLOATER(5),
        DART(9);

        public final int speed;

        FishMotionTypes(int speed) {
            this.speed = speed;
        }
    }

    private enum FishMoves {
        UP,
        DOWN,
        STATIONARY
    }

    private static final float SPEED_FACTOR = 15;
    private static final float WATER_LANE_HEIGHT = 427;
    private static final float BEHAVIOUR_RESET_TIME = 0.5f;
    private static final float BASE_ACCELERATION = 2;
    private static float behaviourTimer = 0.0f;
    private static FishMotionTypes fishMotionType;
    private static FishMoves recentFishMoves;

    public static void determineFishMotionType(boolean isLegendary) {
        if (isLegendary) {
            fishMotionType = FishMotionTypes.DART;
        } else {
            if (randomQuery(60)) {
                fishMotionType = FishMotionTypes.SMOOTH;
            } else if (randomQuery(50)) {
                fishMotionType = FishMotionTypes.MIXED;
            } else if (randomQuery(50)) {
                fishMotionType = FishMotionTypes.SINKER;
            } else {
                fishMotionType = FishMotionTypes.FLOATER;
            }
        }
        recentFishMoves = FishMoves.UP;
    }

    public static void handleFishAI(FishingMiniGame anglingMiniGame, float delta) {
        anglingMiniGame.incrementFishPosition(delta * SPEED_FACTOR * anglingMiniGame.getFishVelocity());
        anglingMiniGame.setFishPosition(
            MathUtils.clamp(anglingMiniGame.getFishPosition(), 0, WATER_LANE_HEIGHT - anglingMiniGame.getFishImage().getHeight()));

        if (anglingMiniGame.getFishPosition() == WATER_LANE_HEIGHT - anglingMiniGame.getFishImage().getHeight()) {
            anglingMiniGame.incrementFishPosition(-0.5f);
            anglingMiniGame.setFishVelocity(-0.6f * anglingMiniGame.getFishVelocity());
            anglingMiniGame.setFishAcceleration(0);
        } else if (anglingMiniGame.getFishPosition() == 0) {
            anglingMiniGame.incrementFishPosition(0.5f);
            anglingMiniGame.setFishVelocity(-0.6f * anglingMiniGame.getFishVelocity());
            anglingMiniGame.setFishAcceleration(0);
        }

        anglingMiniGame.getFishHitbox().y = anglingMiniGame.BOBBER_BASE_Y + anglingMiniGame.getFishPosition();
        anglingMiniGame.getFishImage().setY(anglingMiniGame.BOBBER_BASE_Y + anglingMiniGame.getFishPosition());

        anglingMiniGame.incrementFishVelocity(anglingMiniGame.getFishAcceleration() * delta * SPEED_FACTOR);

        behaviourTimer += delta;

        if (behaviourTimer >= BEHAVIOUR_RESET_TIME) {
            behaviourTimer = 0;

            int rand = (int) (Math.random() * 3);
            FishMoves move = FishMoves.values()[rand];

            if (fishMotionType == FishMotionTypes.SMOOTH && randomQuery(70)) {
                move = recentFishMoves;
            }

            recentFishMoves = move;

            if (move == FishMoves.UP) {
                anglingMiniGame.setFishVelocity(fishMotionType.speed);

                if (fishMotionType == FishMotionTypes.FLOATER) {
                    anglingMiniGame.setFishAcceleration(BASE_ACCELERATION);
                }
            } else if (move == FishMoves.DOWN) {
                anglingMiniGame.setFishVelocity(-fishMotionType.speed);

                if (fishMotionType == FishMotionTypes.SINKER) {
                    anglingMiniGame.setFishAcceleration(-BASE_ACCELERATION);
                }
            } else {
                anglingMiniGame.setFishVelocity(0);
            }
        }
    }

    public static HashMap<String, Object> queryAnglingResult(String poleName) {
        int randomNumber = (int) (Math.random() * 2);
        double weatherModifier = setWeatherModifierAngling();
        Player player = App.getGame().getCurrentPlayer();
        int playerLevel = player.getSkillLevel(org.example.common.models.enums.PlayerEnums.Skills.FISHING);
        int numberOfFishes = (int) (((double) randomNumber)
            * weatherModifier * (double) (playerLevel + 2)) + 1;

        ArrayList<FishType> values = getValidFishTypes(App.getGame().getDate().getSeason(), playerLevel);
        int randomFishNumber = (int) (Math.random() * values.size());
        FishType fishType = values.get(randomFishNumber);

        double qualityNumber;
        double pole = setPoleModifier(poleName);
        qualityNumber = (randomNumber * (double) (playerLevel + 2) * pole) / (7.0 - weatherModifier);
        Quality fishQuality = setFishQuality(qualityNumber);
        int gainedXp = 5;

        var result = new HashMap<String, Object>();
        result.put("type", fishType);
        result.put("quality", fishQuality);
        result.put("quantity", numberOfFishes);
        result.put("xp", gainedXp);
        return result;
    }

    private static ArrayList<FishType> getValidFishTypes(Seasons season, int playerLevel) {
        if (playerLevel == 4) {
            FishType[] values = FishType.values();
            ArrayList<FishType> finalValues = new ArrayList<>();
            for (int i = 0; i < values.length; i++) {
                if (values[i].isAvailableInSeason(season)) {
                    finalValues.add(values[i]);
                }
            }
            return finalValues;
        }
        FishType[] values = FishType.values();
        ArrayList<FishType> finalValues = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            if (values[i].isAvailableInSeason(season) && !values[i].isLegendary()) {
                finalValues.add(values[i]);
            }
        }
        return finalValues;
    }

    private static Quality setFishQuality(double qualityNumber) {
        if (qualityNumber >= 0.5 && qualityNumber < 0.7)
            return Quality.Silver;
        else if (qualityNumber >= 0.7 && qualityNumber < 0.9)
            return Quality.Golden;
        else if (qualityNumber >= 0.9)
            return Quality.Iridium;
        return Quality.Normal;
    }

    private static double setPoleModifier(String poleName) {
        return switch (poleName.toLowerCase()) {
            case "training rod" -> 0.1;
            case "bamboo pole" -> 0.5;
            case "fiberglass rod" -> 0.9;
            case "iridium rod" -> 1.2;
            default -> 0.1;
        };
    }

    private static double setWeatherModifierAngling() {
        double weatherModifier;
        if (App.getGame().getDate().getWeatherToday() == Weather.SUNNY)
            weatherModifier = 1.5;
        else if (App.getGame().getDate().getWeatherToday() == Weather.RAINY)
            weatherModifier = 1.2;
        else if (App.getGame().getDate().getWeatherToday() == Weather.STORMY)
            weatherModifier = 0.5;
        else
            weatherModifier = 1.0;
        return weatherModifier;
    }

    private static boolean randomQuery(int successPercent) {
        return (int) (Math.random() * 100) < successPercent;
    }
} 