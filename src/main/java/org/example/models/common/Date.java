package org.example.models.common;

import org.example.models.App;
import org.example.models.MapDetails.GameMap;
import org.example.models.enums.Seasons;
import org.example.models.enums.Weather;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Date {
    private final Map<Seasons, List<Weather>> weatherMap;
    private final int daysPerSeason = 28;
    private int day; // days (1-28)
    private int season; // 0: Spring, 1: Summer, 2: Fall, 3: Winter
    private int year;
    private int hour;
    private Weather weatherToday;
    private Weather weatherTomorrow;

    public Date() {
        this.day = 1; // first day of spring
        this.season = 0; // spring
        this.year = 1;
        this.hour = 9; // 9 AM
        this.weatherMap = new HashMap<>();
        initialWeatherMap();
        updateWeatherToday();
        updateWeatherTomorrow();
    }

    private void initialWeatherMap() {
        weatherMap.put(Seasons.SPRING, Arrays.asList(Weather.SUNNY, Weather.RAINY, Weather.STORMY));
        weatherMap.put(Seasons.SUMMER, Arrays.asList(Weather.SUNNY, Weather.RAINY, Weather.STORMY));
        weatherMap.put(Seasons.AUTUMN, Arrays.asList(Weather.SUNNY, Weather.RAINY, Weather.STORMY));
        weatherMap.put(Seasons.WINTER, Arrays.asList(Weather.SUNNY, Weather.SNOWY));
    }

    public void advanceTime(int hours, GameMap gameMap) {
        if (hours < 0) {
            System.out.println("Error: Cannot advance time by negative values");
            return;
        }

        this.hour += hours;
        for (int i = 0; i < hours; i++) {
            App.getGame().updateTurns();
        }
        while (this.hour >= 22) {
            this.hour -= 13;
            advanceDays(1, gameMap);
        }
    }

    public void cheatThor(Location location) {
        App.getGame().getGameMap().getFarmByPlayer(App.getGame().getCurrentPlayer()).thor(location);
        System.out.println("Thor has struck the location");
    }


    public void advanceDays(int days, GameMap gameMap) {
        if (days < 0) {
            System.out.println("Error: Cannot advance date by negative values");
            return;
        }


        this.day += days;

        //updating daily map.
        for (int i = 0; i < days; i++) {
            App.getGame().updateDailyGame();
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

    private void updateWeatherToday() {
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

    // changing the day
    public void goTomorrow(GameMap gameMap) {
        this.day++;
        this.weatherToday = weatherTomorrow;
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

    public Weather getWeatherToday() {
        return this.weatherToday;
    }

    public void displayClock() {
        displayTime();
        displayDate();
        displaySeason();
    }

    public void displayTime() {
        System.out.println("Time: " + String.format("%02d:00", hour));
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

        // Return the difference
        return currentTotalDays - rejectTotalDays;
    }
}
