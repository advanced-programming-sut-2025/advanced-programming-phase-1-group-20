package models;

import models.enums.Seasons;
import models.enums.Weather;

import java.time.LocalDateTime;
import java.util.*;

public class GameClock {
    private LocalDateTime currentTime; // زمان فعلی
    private Seasons season; // فصل فعلی
    private Weather weatherToday; // وضعیت آب‌وهوای امروز
    private Weather weatherTomorrow; // وضعیت آب‌وهوای فردا
    private final Map<Seasons, List<Weather>> weatherMap; // مپ فصل‌ها به وضعیت‌های ممکن
    private final int daysPerSeason = 28; // تعداد روزهای هر فصل

    public GameClock() {
        this.currentTime = LocalDateTime.of(2025, 1, 1, 0, 0); // شروع زمان بازی از روز اول
        this.weatherMap = new HashMap<>();
        initialWeatherMap();
        updateSeason(); // محاسبه فصل فعلی
        updateWeatherToday(); // به‌روزرسانی وضعیت امروز
        updateWeatherTomorrow(); // به‌روزرسانی وضعیت فردا
    }

    private void initialWeatherMap() {
        weatherMap.put(Seasons.SPRING, Arrays.asList(Weather.SUNNY, Weather.RAINY, Weather.STORMY));
        weatherMap.put(Seasons.SUMMER, Arrays.asList(Weather.SUNNY, Weather.RAINY, Weather.STORMY));
        weatherMap.put(Seasons.AUTUMN, Arrays.asList(Weather.SUNNY, Weather.RAINY, Weather.STORMY));
        weatherMap.put(Seasons.WINTER, Arrays.asList(Weather.SUNNY, Weather.SNOWY));
    }

    public void advanceTime(int hours, int minutes) {
        this.currentTime = this.currentTime.plusHours(hours).plusMinutes(minutes);
        updateSeason(); // به‌روزرسانی فصل بر اساس زمان
    }

    public void cheatThor(){}

    public void advanceDays(int days) {
        this.currentTime = this.currentTime.plusDays(days);
        updateSeason(); // به‌روزرسانی فصل
        updateWeatherToday(); // به‌روزرسانی وضعیت امروز
        updateWeatherTomorrow(); // به‌روزرسانی وضعیت فردا
    }

    private void updateSeason() {
        int totalDays = currentTime.getDayOfYear();
        int seasonIndex = ((totalDays - 1) / daysPerSeason) % Seasons.values().length; // محاسبه فصل فعلی
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