package models;

import models.enums.Seasons;
import models.enums.Weather;

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
    private int year; // year
    private int hour;
    private Weather weatherToday;
    private Weather weatherTomorrow;

    public Date() {
        this.day = 1; // first day of spring
        this.season = 1; // spring
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

    public void advanceTime(int hours) {
        if (hours < 0) {
            System.out.println("Error: Cannot advance time by negative values");
            return;
        }

        // Add the hours
        this.hour += hours;

        // Handle hour overflow - advance days if needed
        while (this.hour >= 24) {
            this.hour -= 24;
            advanceDays(1);
        }
    }

    public void cheatThor(Location location) {
        System.out.println("Thor has struck the location");
        // implementing the lightning
    }


    public void advanceDays(int days) {
        if (days < 0) {
            System.out.println("Error: Cannot advance date by negative values");
            return;
        }

        this.day += days;

        // Handle day overflow - advance seasons if needed
        while (this.day > daysPerSeason) {
            this.day -= daysPerSeason;
            this.season = (this.season + 1) % 4;

            // If we've completed a full year cycle
            if (this.season == 0) {
                this.year++;
            }
        }

        updateWeatherToday();
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
    public void goTomorrow() {
        advanceDays(1);
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
        // Calculate the day of week (0-6 representing Sunday-Saturday)
        // This is a simplified version where we start with day 1 of Spring as Monday
        int totalDays = ((year - 1) * 4 * daysPerSeason) + (season * daysPerSeason) + day;
        int dayOfWeek = (totalDays % 7); // 0 is Sunday, 1 is Monday, etc.

        String[] dayNames = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        System.out.println("Day of the week: " + dayNames[dayOfWeek]);
    }
}