package org.example.common.models.common;

import org.example.common.models.App;
import org.example.common.models.MapDetails.GameMap;
import org.example.common.models.enums.Seasons;
import org.example.common.models.enums.Weather;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Date implements Runnable {
    private final Map<Seasons, List<Weather>> weatherMap;
    private final int daysPerSeason = 28;
    private int day; // days (1-28)
    private int season; // 0: Spring, 1: Summer, 2: Fall, 3: Winter
    private int year;
    private int hour;
    private int minute; // NEW: minutes (0-59)
    private Weather weatherToday;
    private Weather weatherTomorrow;
    private boolean running = true;

    public Date() {
        this.day = 1;
        this.season = 1;
        this.year = 1;
        this.hour = 9;
        this.minute = 0; // NEW
        this.weatherMap = new HashMap<>();
        initialWeatherMap();
        updateWeatherToday();
        updateWeatherTomorrow();

        // Check if we're in a server environment (Gdx.files is null on server)
        boolean isServerEnvironment = false;
        try {
            // Try to access Gdx.files - if it's null, we're on the server
            if (com.badlogic.gdx.Gdx.files == null) {
                isServerEnvironment = true;
            }
        } catch (Exception e) {
            isServerEnvironment = true;
        }
        
        if (!isServerEnvironment) {
            // Only start the time thread on client side
            Thread timeThread = new Thread(this);
            timeThread.setDaemon(true);
            timeThread.start();
        }

        displayTime();
    }

    private void initialWeatherMap() {
        weatherMap.put(Seasons.SPRING, Arrays.asList(Weather.SUNNY, Weather.RAINY, Weather.STORMY));
        weatherMap.put(Seasons.SUMMER, Arrays.asList(Weather.SUNNY, Weather.RAINY, Weather.STORMY));
        weatherMap.put(Seasons.AUTUMN, Arrays.asList(Weather.SUNNY, Weather.RAINY, Weather.STORMY));
        weatherMap.put(Seasons.WINTER, Arrays.asList(Weather.SUNNY, Weather.SNOWY));
    }

    public void advanceTime(int hours, GameMap gameMap) {
        advanceMinutes(hours * 60, gameMap);
    }

    public void advanceMinutes(int minutes, GameMap gameMap) {
        if (minutes < 0) {
            System.out.println("Error: Cannot advance time by negative values");
            return;
        }

        for (int i = 0; i < minutes; i++) {
            this.minute++;
            if (this.minute >= 60) {
                this.minute = 0;
                this.hour++;
                
                // Check if we're in a server environment
                boolean isServerEnvironment = false;
                try {
                    if (com.badlogic.gdx.Gdx.files == null) {
                        isServerEnvironment = true;
                    }
                } catch (Exception e) {
                    isServerEnvironment = true;
                }
                
                if (!isServerEnvironment && App.getGame() != null) {
                    App.getGame().updateTurns();
                }
            }

            if (this.hour >= 22) {
                this.hour -= 13;
                advanceDays(1, gameMap);
            }
        }
        log();
    }

    public void cheatThor(Location location) {
        // Check if we're in a server environment
        boolean isServerEnvironment = false;
        try {
            if (com.badlogic.gdx.Gdx.files == null) {
                isServerEnvironment = true;
            }
        } catch (Exception e) {
            isServerEnvironment = true;
        }
        
        if (!isServerEnvironment && App.getGame() != null) {
            App.getGame().getGameMap().getFarmByPlayer(App.getGame().getCurrentPlayer()).thor(location);
            System.out.println("Thor has struck the location");
        }
    }

    public void advanceDays(int days, GameMap gameMap) {
        if (days < 0) {
            System.out.println("Error: Cannot advance date by negative values");
            return;
        }

        this.day += days;

        // Check if we're in a server environment
        boolean isServerEnvironment = false;
        try {
            if (com.badlogic.gdx.Gdx.files == null) {
                isServerEnvironment = true;
            }
        } catch (Exception e) {
            isServerEnvironment = true;
        }
        
        if (!isServerEnvironment && App.getGame() != null) {
            for (int i = 0; i < days; i++) {
                App.getGame().updateDailyGame();
            }
        }

        while (this.day > daysPerSeason) {
            this.day -= daysPerSeason;
            this.season = (this.season + 1) % 4;
            if (this.season == 0) {
                this.year++;
            }
        }

        if (days != 1) {
            updateWeatherToday();
        } else {
            weatherToday = weatherTomorrow;
        }
        updateWeatherTomorrow();
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(10_000); // every 10 seconds
                
                // Check if we're in a server environment
                boolean isServerEnvironment = false;
                try {
                    if (com.badlogic.gdx.Gdx.files == null) {
                        isServerEnvironment = true;
                    }
                } catch (Exception e) {
                    isServerEnvironment = true;
                }
                
                if (!isServerEnvironment && App.getGame() != null && App.getGame().getGameMap() != null) {
                    advanceMinutes(10, App.getGame().getGameMap()); // simulate 10 in-game minutes
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Time thread interrupted");
            }
        }
    }

    public void stop() {
        running = false;
    }

    private void updateWeatherToday() {
        if (weatherMap.isEmpty()) {
            System.out.println("Weather map is not initialized.");
            return;
        }
        Seasons currentSeason = Seasons.values()[this.season];
        List<Weather> possibleWeather = weatherMap.get(currentSeason);
        int randomIndex = ThreadLocalRandom.current().nextInt(possibleWeather.size());
        this.weatherToday = possibleWeather.get(randomIndex);
    }

    private void updateWeatherTomorrow() {
        Seasons currentSeason = Seasons.values()[this.season];
        List<Weather> possibleWeather = weatherMap.get(currentSeason);
        int randomIndex = ThreadLocalRandom.current().nextInt(possibleWeather.size());
        this.weatherTomorrow = possibleWeather.get(randomIndex);
    }

    public void goTomorrow(GameMap gameMap) {
        this.day++;
        this.weatherToday = weatherTomorrow;
        if (weatherToday == Weather.RAINY) {
            App.getGame().getGameMap().setMoistureForRainyDaysFarms();
        }
        updateWeatherTomorrow();
    }

    public int getDay() {
        return this.day;
    }

    public int getHour() {
        return this.hour;
    }

    public Seasons getSeason() {
        return Seasons.values()[this.season];
    }

    public String getSeasonString() {
        return switch (this.season) {
            case 0 -> "Spring";
            case 1 -> "Summer";
            case 2 -> "Fall";
            case 3 -> "Winter";
            default -> "Unexpected";
        };
    }

    public Weather getWeatherToday() {
        return this.weatherToday;
    }

    public void displayClock() {
        displayTime();
        displayDate();
        displaySeason();
    }

    public void displayTime() {
        System.out.println("Time: " + String.format("%02d:%02d", hour, minute));
    }

    public void displayDate() {
        System.out.println("Date: Day " + day + " of " + getSeason().toString());
    }

    public void displaySeason() {
        System.out.println("Season: " + getSeason().toString());
    }

    public void displayDayOfWeek() {
        int totalDays = ((year - 1) * 4 * daysPerSeason) + (season * daysPerSeason) + day - 1;
        int dayOfWeek = (totalDays % 7);

        String[] dayNames = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        System.out.println("Day of the week: " + dayNames[dayOfWeek]);
    }

    public void displayWeather() {
        System.out.println("Weather today: " + weatherToday.toString());
    }

    public void displayWeatherForecast() {
        System.out.println("Weather forecast for tomorrow: " + weatherTomorrow.toString());
    }

    public void setWeatherTomorrow(Weather weather) {
        this.weatherTomorrow = weather;
    }

    public long getDaysPassed(Date rejectDate) {
        if (rejectDate == null) {
            return 0;
        }

        long currentTotalDays = ((long) (year - 1) * 4 * daysPerSeason) + ((long) season * daysPerSeason) + day;
        long rejectTotalDays = ((long) (rejectDate.year - 1) * 4 * daysPerSeason) + ((long) rejectDate.season * daysPerSeason) + rejectDate.day;

        return currentTotalDays - rejectTotalDays;
    }

    private String getCurrentTimeString() {
        return String.format("Year %d, %s %02d, %02d:%02d", year, getSeason(), day, hour, minute);
    }

    private void log() {
        System.out.println("[LOG - " + getCurrentTimeString() + "] " + "time advanced");
        displayWeather();
    }

    public int getMinutes(){
        return this.minute;
    }

}
