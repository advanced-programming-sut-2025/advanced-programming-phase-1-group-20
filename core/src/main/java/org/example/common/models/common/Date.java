package org.example.common.models.common;

import org.example.common.models.App;
import org.example.common.models.MapDetails.GameMap;
import org.example.common.models.enums.Seasons;
import org.example.common.models.enums.Weather;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Random;

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
        this.season = 0;
        this.year = 1;
        this.hour = 9;
        this.minute = 0; // NEW
        this.weatherMap = new HashMap<>();


        initialWeatherMap();
        updateWeatherToday();
        updateWeatherTomorrow();

        boolean isServerEnvironment = false;
        try {
            if (com.badlogic.gdx.Gdx.files == null) {
                isServerEnvironment = true;
            }
        } catch (Exception e) {
            isServerEnvironment = true;
        }

        // Check if we're in multiplayer mode
        boolean isMultiplayerMode = false;
        try {
            if (App.getGame() != null) {
                isMultiplayerMode = App.getGame().isMultiplayer;
            }
        } catch (Exception e) {
            // If we can't check, assume single player
            isMultiplayerMode = false;
        }

        if (!isServerEnvironment && !isMultiplayerMode) {
            // Only start the time thread on client side in single player mode
            Thread timeThread = new Thread(this);
            timeThread.setDaemon(true);
            timeThread.start();
            System.out.println("DEBUG: Started time thread for single player mode");
        } else if (isServerEnvironment) {
            System.out.println("DEBUG: Server environment - time thread not started (server controls time via game loop)");
            // Don't stop the running flag on server - it needs to be able to advance time
        } else if (isMultiplayerMode) {
            System.out.println("DEBUG: Multiplayer mode - time thread not started (server controls time)");
            // In multiplayer mode, ensure the time thread is stopped for clients
            this.running = false;
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

        // Check if we're in multiplayer mode
        boolean isMultiplayerMode = false;
        try {
            if (App.getGame() != null) {
                isMultiplayerMode = App.getGame().isMultiplayer;
            }
        } catch (Exception e) {
            // If we can't check, assume single player
            isMultiplayerMode = false;
        }

        // Check if we're in a server environment
        boolean isServerEnvironment = false;
        try {
            if (com.badlogic.gdx.Gdx.files == null) {
                isServerEnvironment = true;
            }
        } catch (Exception e) {
            isServerEnvironment = true;
        }

        // In multiplayer mode, only the server should advance time
        // Clients should only advance time when syncing from server
        if (isMultiplayerMode && !isServerEnvironment) {
            System.out.println("DEBUG: Client attempting to advance time in multiplayer mode - ignoring");
            return;
        }

        // Allow time advancement on server or in single player mode
        System.out.println("DEBUG: Advancing time by " + minutes + " minutes (Server: " + isServerEnvironment + ", Multiplayer: " + isMultiplayerMode + ")");

        for (int i = 0; i < minutes; i++) {
            this.minute++;
            if (this.minute >= 60) {
                this.minute = 0;
                this.hour++;

                // Only update turns on client side
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

        // Check if we're in multiplayer mode
        boolean isMultiplayerMode = false;
        try {
            if (App.getGame() != null) {
                isMultiplayerMode = App.getGame().isMultiplayer;
            }
        } catch (Exception e) {
            // If we can't check, assume single player
            isMultiplayerMode = false;
        }

        // Check if we're in a server environment
        boolean isServerEnvironment = false;
        try {
            if (com.badlogic.gdx.Gdx.files == null) {
                isServerEnvironment = true;
            }
        } catch (Exception e) {
            isServerEnvironment = true;
        }

        // In multiplayer mode, only the server should advance days
        // Clients should only advance days when syncing from server
        if (isMultiplayerMode && !isServerEnvironment) {
            System.out.println("DEBUG: Client attempting to advance days in multiplayer mode - ignoring");
            return;
        }

        System.out.println("DEBUG: Advancing days by " + days + " (Server: " + isServerEnvironment + ", Multiplayer: " + isMultiplayerMode + ")");

        this.day += days;

        // Only update daily game on client side
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

                // Check if we're in multiplayer mode
                boolean isMultiplayerMode = false;
                try {
                    if (App.getGame() != null) {
                        isMultiplayerMode = App.getGame().isMultiplayer;
                    }
                } catch (Exception e) {
                    // If we can't check, assume single player
                    isMultiplayerMode = false;
                }

                if (!isServerEnvironment && !isMultiplayerMode && App.getGame() != null && App.getGame().getGameMap() != null) {
                    advanceMinutes(10, App.getGame().getGameMap()); // simulate 10 in-game minutes
                } else if (isMultiplayerMode) {
                    System.out.println("DEBUG: Multiplayer mode - skipping local time advancement (server controls time)");
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

        // Use truly random weather generation
         Random random = new Random();
        int randomIndex = random.nextInt(possibleWeather.size());
        this.weatherToday = possibleWeather.get(randomIndex);

        System.out.println("DEBUG: Generated weather for " + getCurrentTimeString() + ": " + this.weatherToday);
    }

    private void updateWeatherTomorrow() {
        Seasons currentSeason = Seasons.values()[this.season];
        List<Weather> possibleWeather = weatherMap.get(currentSeason);

        // Use truly random generation for tomorrow's weather
        Random tomorrowRandom = new Random();
        int randomIndex = tomorrowRandom.nextInt(possibleWeather.size());
        this.weatherTomorrow = possibleWeather.get(randomIndex);

        System.out.println("DEBUG: Generated tomorrow's weather for " + getCurrentTimeString() + ": " + this.weatherTomorrow);
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

    public void setWeatherToday(Weather weather) {
        this.weatherToday = weather;
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

    /**
     * Get a formatted string representation of the current time
     */
    public String getCurrentTimeString() {
        return String.format("Year %d, %s %02d, %02d:%02d", year, getSeason(), day, hour, minute);
    }

    private void log() {
        System.out.println("[LOG - " + getCurrentTimeString() + "] " + "time advanced");
        displayWeather();
    }

    public int getMinutes(){
        return this.minute;
    }

    /**
     * Get date state as a map for network synchronization
     */
    public Map<String, Object> getDateState() {
        Map<String, Object> state = new HashMap<>();
        state.put("day", this.day);
        state.put("season", this.season);
        state.put("year", this.year);
        state.put("hour", this.hour);
        state.put("minute", this.minute);
        state.put("weatherToday", this.weatherToday != null ? this.weatherToday.toString() : null);
        state.put("weatherTomorrow", this.weatherTomorrow != null ? this.weatherTomorrow.toString() : null);
        return state;
    }

    /**
     * Sync date state from server data
     */
    public void syncFromServer(Map<String, Object> serverDateState) {
        if (serverDateState == null) {
            System.out.println("DEBUG: Cannot sync date - serverDateState is null");
            return;
        }

        try {
            System.out.println("DEBUG: Syncing date from server - Before sync: " + getCurrentTimeString());
            System.out.println("DEBUG: Server date state: " + serverDateState);

            // Temporarily stop the time thread to prevent conflicts during sync
            boolean wasRunning = this.running;
            if (wasRunning) {
                this.running = false;
            }

            // Update date fields
            if (serverDateState.containsKey("day")) {
                Object dayObj = serverDateState.get("day");
                if (dayObj instanceof Double) {
                    this.day = ((Double) dayObj).intValue();
                } else if (dayObj instanceof Integer) {
                    this.day = (Integer) dayObj;
                }
                System.out.println("DEBUG: Synced day: " + this.day);
            }
            if (serverDateState.containsKey("season")) {
                Object seasonObj = serverDateState.get("season");
                if (seasonObj instanceof Double) {
                    this.season = ((Double) seasonObj).intValue();
                } else if (seasonObj instanceof Integer) {
                    this.season = (Integer) seasonObj;
                }
                System.out.println("DEBUG: Synced season: " + this.season);
            }
            if (serverDateState.containsKey("year")) {
                Object yearObj = serverDateState.get("year");
                if (yearObj instanceof Double) {
                    this.year = ((Double) yearObj).intValue();
                } else if (yearObj instanceof Integer) {
                    this.year = (Integer) yearObj;
                }
                System.out.println("DEBUG: Synced year: " + this.year);
            }
            if (serverDateState.containsKey("hour")) {
                Object hourObj = serverDateState.get("hour");
                if (hourObj instanceof Double) {
                    this.hour = ((Double) hourObj).intValue();
                } else if (hourObj instanceof Integer) {
                    this.hour = (Integer) hourObj;
                }
                System.out.println("DEBUG: Synced hour: " + this.hour);
            }
            if (serverDateState.containsKey("minute")) {
                Object minuteObj = serverDateState.get("minute");
                if (minuteObj instanceof Double) {
                    this.minute = ((Double) minuteObj).intValue();
                } else if (minuteObj instanceof Integer) {
                    this.minute = (Integer) minuteObj;
                }
                System.out.println("DEBUG: Synced minute: " + this.minute);
            }

            // Update weather from server (this is the authoritative source)
            if (serverDateState.containsKey("weatherToday")) {
                String weatherTodayStr = (String) serverDateState.get("weatherToday");
                if (weatherTodayStr != null) {
                    this.weatherToday = Weather.valueOf(weatherTodayStr);
                    System.out.println("DEBUG: Synced weather today from server: " + this.weatherToday);
                }
            }
            if (serverDateState.containsKey("weatherTomorrow")) {
                String weatherTomorrowStr = (String) serverDateState.get("weatherTomorrow");
                if (weatherTomorrowStr != null) {
                    this.weatherTomorrow = Weather.valueOf(weatherTomorrowStr);
                    System.out.println("DEBUG: Synced weather tomorrow from server: " + this.weatherTomorrow);
                }
            }

            // Restart the time thread if it was running before
            if (wasRunning) {
                this.running = true;
            }

            System.out.println("DEBUG: Date synced from server - After sync: " + getCurrentTimeString());
        } catch (Exception e) {
            System.err.println("DEBUG: Error syncing date from server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Weather getWeatherTomorrow() {
        return weatherTomorrow;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    @Override
    public String toString() {
        return String.format("Year %d, %s %02d, %02d:%02d", year, getSeason(), day, hour, minute);
    }
}
