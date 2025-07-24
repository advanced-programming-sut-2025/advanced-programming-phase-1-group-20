package org.example.client.controllers.fishing;

import com.badlogic.gdx.math.MathUtils;
import org.example.client.views.fishing.FishingMiniGame;
import org.example.common.models.App;
import org.example.common.models.Player.Player;
import org.example.common.models.enums.PlayerEnums.Skills;
import org.example.common.models.enums.Types.Quality;
import org.example.common.models.enums.Types.FishType;
import org.example.common.models.enums.Seasons;
import org.example.common.models.enums.Weather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class FishingController {
    // Configuration constants
    private static final float MOVEMENT_MULTIPLIER = 12;
    private static final float FISHING_AREA_HEIGHT = 400;
    private static final float AI_UPDATE_INTERVAL = 0.6f;
    private static final float MOVEMENT_FORCE = 3;

    // State tracking
    private static float aiUpdateCounter = 0.0f;
    private static FishBehavior currentBehavior;
    private static FishAction lastAction;
    private static int idleTimeCounter = 0;

    private static final Random randomGenerator = new Random();


    public static void initializeFishBehavior(boolean isLegendaryFish) {
        if (isLegendaryFish) {
            currentBehavior = FishBehavior.DART;
        } else {
            double chance = randomGenerator.nextDouble();
            if (chance < 0.25) {
                currentBehavior = FishBehavior.MIXED;
            } else if (chance < 0.45) {
                currentBehavior = FishBehavior.SMOOTH;
            } else if (chance < 0.65) {
                currentBehavior = FishBehavior.SINKER;
            } else if (chance < 0.85) {
                currentBehavior = FishBehavior.FLOATER;
            } else {
                currentBehavior = FishBehavior.DART;
            }
        }
        lastAction = FishAction.GO_UP;
        idleTimeCounter = 0;
    }

    public static void processFishAI(FishingMiniGame gameInstance, float timeDelta) {
        float newPosition = gameInstance.getFishPosition() +
                          (timeDelta * MOVEMENT_MULTIPLIER * gameInstance.getFishVelocity());

        float maxPosition = FISHING_AREA_HEIGHT - gameInstance.getFishImage().getHeight();
        newPosition = MathUtils.clamp(newPosition, 0, maxPosition);
        gameInstance.setFishPosition(newPosition);

        // Handle boundary collisions
        handleBoundaryCollision(gameInstance, newPosition, maxPosition);

        // Update visual position
        updateFishVisualPosition(gameInstance);

        // Update velocity based on acceleration
        float newVelocity = gameInstance.getFishVelocity() +
                          (gameInstance.getFishAcceleration() * timeDelta * MOVEMENT_MULTIPLIER);
        gameInstance.setFishVelocity(newVelocity);

        aiUpdateCounter += timeDelta;
        if (aiUpdateCounter >= AI_UPDATE_INTERVAL) {
            aiUpdateCounter = 0;
            executeFishAction(gameInstance);
        }
    }


    private static void handleBoundaryCollision(FishingMiniGame gameInstance, float position, float maxPosition) {
        if (position >= maxPosition) {
            gameInstance.incrementFishPosition(-0.5f);
            gameInstance.setFishVelocity(-0.6f * gameInstance.getFishVelocity());
            gameInstance.setFishAcceleration(0);
        } else if (position <= 0) {
            gameInstance.incrementFishPosition(0.5f);
            gameInstance.setFishVelocity(-0.6f * gameInstance.getFishVelocity());
            gameInstance.setFishAcceleration(0);
        }
    }


    private static void updateFishVisualPosition(FishingMiniGame gameInstance) {
        float visualY = gameInstance.BOBBER_BASE_Y + gameInstance.getFishPosition();
        gameInstance.getFishHitbox().y = visualY;
        gameInstance.getFishImage().setY(visualY);
    }


    private static void executeFishAction(FishingMiniGame gameInstance) {
        FishAction nextAction = calculateNextAction();
        lastAction = nextAction;

        switch (nextAction) {
            case GO_UP:
                gameInstance.setFishVelocity(currentBehavior.getSpeed());
                idleTimeCounter = 0;
                if (currentBehavior == FishBehavior.FLOATER) {
                    gameInstance.setFishAcceleration(MOVEMENT_FORCE);
                } else {
                    gameInstance.setFishAcceleration(0);
                }
                break;

            case GO_DOWN:
                gameInstance.setFishVelocity(-currentBehavior.getSpeed());
                idleTimeCounter = 0;
                if (currentBehavior == FishBehavior.SINKER) {
                    gameInstance.setFishAcceleration(-MOVEMENT_FORCE);
                } else {
                    gameInstance.setFishAcceleration(0);
                }
                break;

            case STAY_PUT:
                gameInstance.setFishVelocity(0);
                gameInstance.setFishAcceleration(0);
                idleTimeCounter++;
                break;
        }
    }


    private static FishAction calculateNextAction() {
        switch (currentBehavior) {
            case MIXED:
                return FishAction.values()[randomGenerator.nextInt(3)];

            case SMOOTH:
                if (randomGenerator.nextInt(100) < 70) {
                    return lastAction;
                } else {
                    return FishAction.values()[randomGenerator.nextInt(3)];
                }

            case SINKER:
                double diveChance = randomGenerator.nextDouble();
                if (diveChance < 0.4) {
                    return FishAction.GO_DOWN;
                } else if (diveChance < 0.7) {
                    return FishAction.STAY_PUT;
                } else {
                    return FishAction.GO_UP;
                }

            case FLOATER:
                double floatChance = randomGenerator.nextDouble();
                if (floatChance < 0.4) {
                    return FishAction.GO_UP;
                } else if (floatChance < 0.7) {
                    return FishAction.STAY_PUT;
                } else {
                    return FishAction.GO_DOWN;
                }

            case DART:
                double wildChance = randomGenerator.nextDouble();
                if (wildChance < 0.4) {
                    return FishAction.GO_UP;
                } else if (wildChance < 0.8) {
                    return FishAction.GO_DOWN;
                } else {
                    return FishAction.STAY_PUT;
                }

            default:
                return FishAction.STAY_PUT;
        }
    }


    public static HashMap<String, Object> generateFishingResults(String fishingPoleName) {
        int baseFishCount = randomGenerator.nextInt(2);
        double weatherBonus = calculateWeatherBonus();
        Player currentPlayer = App.getGame().getCurrentPlayer();
        int fishingSkill = currentPlayer.getSkillLevel(Skills.FISHING);

        int totalFish = (int) (baseFishCount * weatherBonus * (fishingSkill + 2)) + 1;

        ArrayList<FishType> availableFish = getAvailableFish(App.getGame().getDate().getSeason(), fishingSkill);
        FishType caughtFish = availableFish.get(randomGenerator.nextInt(availableFish.size()));

        double poleBonus = calculatePoleBonus(fishingPoleName);
        double qualityScore = (baseFishCount * (fishingSkill + 2) * poleBonus) / (7.0 - weatherBonus);
        Quality fishQuality = determineFishQuality(qualityScore);

        HashMap<String, Object> results = new HashMap<>();
        results.put("type", caughtFish);
        results.put("quality", fishQuality);
        results.put("quantity", totalFish);
        results.put("xp", 5);

        return results;
    }

    private static ArrayList<FishType> getAvailableFish(Seasons currentSeason, int playerLevel) {
        FishType[] allFish = FishType.values();
        ArrayList<FishType> availableFish = new ArrayList<>();

        for (FishType fish : allFish) {
            if (fish.isAvailableInSeason(currentSeason)) {
                if (playerLevel >= 4 || !fish.isLegendary()) {
                    availableFish.add(fish);
                }
            }
        }

        return availableFish;
    }


    private static Quality determineFishQuality(double qualityScore) {
        if (qualityScore >= 0.5 && qualityScore < 0.7) {
            return Quality.Silver;
        } else if (qualityScore >= 0.7 && qualityScore < 0.9) {
            return Quality.Golden;
        } else if (qualityScore >= 0.9) {
            return Quality.Iridium;
        }
        return Quality.Normal;
    }

    private static double calculatePoleBonus(String poleName) {
        String lowerPoleName = poleName.toLowerCase();
        return switch (lowerPoleName) {
            case "training rod" -> 0.1;
            case "bamboo pole" -> 0.5;
            case "fiberglass rod" -> 0.9;
            case "iridium rod" -> 1.2;
            default -> 0.1;
        };
    }

    private static double calculateWeatherBonus() {
        Weather currentWeather = App.getGame().getDate().getWeatherToday();
        return switch (currentWeather) {
            case SUNNY -> 1.5;
            case RAINY -> 1.2;
            case STORMY -> 0.5;
            default -> 1.0;
        };
    }

    private static boolean checkRandomEvent(int percentage) {
        return randomGenerator.nextInt(100) < percentage;
    }

    // Fish behavior patterns
    private enum FishBehavior {
        MIXED(5),
        SMOOTH(5),
        SINKER(5),
        FLOATER(5),
        DART(9);

        private final int movementSpeed;

        FishBehavior(int speed) {
            this.movementSpeed = speed;
        }

        public int getSpeed() {
            return movementSpeed;
        }
    }

    // Possible fish actions
    private enum FishAction {
        GO_UP,
        GO_DOWN,
        STAY_PUT
    }
}
