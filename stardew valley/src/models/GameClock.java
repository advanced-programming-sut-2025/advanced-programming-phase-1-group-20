package models;

import models.enums.Seasons;
import models.enums.Weather;

import java.time.LocalDateTime;
import java.util.*;

public class GameClock {
    private LocalDateTime currentTime;
    private Seasons season;
    private Weather weatherToday;
    private Weather weatherTomorrow;
    private final Map<Seasons, List<Weather>> weatherMap;
    private final int daysPerSeason = 28;

    public GameClock() {
        this.currentTime = LocalDateTime.of(2025, 1, 1, 0, 0);
        this.weatherMap = new HashMap<>();
        initialWeatherMap();
        updateSeason();
        updateWeatherToday();
        updateWeatherTomorrow();
    }

    private void initialWeatherMap() {
        weatherMap.put(Seasons.SPRING, Arrays.asList(Weather.SUNNY, Weather.RAINY, Weather.STORMY));
        weatherMap.put(Seasons.SUMMER, Arrays.asList(Weather.SUNNY, Weather.RAINY, Weather.STORMY));
        weatherMap.put(Seasons.AUTUMN, Arrays.asList(Weather.SUNNY, Weather.RAINY, Weather.STORMY));
        weatherMap.put(Seasons.WINTER, Arrays.asList(Weather.SUNNY, Weather.SNOWY));
    }

    public void advanceTime(int hours, int minutes) {
        this.currentTime = this.currentTime.plusHours(hours).plusMinutes(minutes);
        updateSeason();
    }

    public void cheatThor(){}

    public void advanceDays(int days) {
        this.currentTime = this.currentTime.plusDays(days);
        updateSeason();
        updateWeatherToday();
        updateWeatherTomorrow();
    }

    private void updateSeason() {
        int totalDays = currentTime.getDayOfYear();
        int seasonIndex = ((totalDays - 1) / daysPerSeason) % Seasons.values().length;
        this.season = Seasons.values()[seasonIndex];
    }

    private void updateWeatherToday() {

    }

    // updating weather
    private void updateWeatherTomorrow() {

    }

    //going tomorrow
    public void goTomorrow() {

    }

    public LocalDateTime getCurrentTime() {
        return currentTime;
    }

    public Seasons getSeason() {
        return season;
    }

    public Weather getWeatherToday() {
        return weatherToday;
    }

    public Weather getWeatherTomorrow() {
        return weatherTomorrow;
    }

    public void displayClock() {
        System.out.println("Time: " + currentTime.getHour() + ":" + currentTime.getMinute());
        System.out.println("Date: " + currentTime.toLocalDate());
        System.out.println("Season: " + season);
    }
    public void displayTime(){
        System.out.println("Time: " + currentTime.getHour() + ":" + currentTime.getMinute());
    }
    public void displayDate(){
        System.out.println("Date: " + currentTime.toLocalDate());
    }
    public void displaySeason(){
        System.out.println("Season: " + season);
    }

}