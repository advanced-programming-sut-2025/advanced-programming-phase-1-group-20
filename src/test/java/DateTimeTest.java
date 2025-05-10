import org.example.models.MapDetails.GameMap;
import org.example.models.Player.Player;
import org.example.models.common.Date;
import org.example.models.common.Location;
import org.example.models.entities.User;
import org.example.models.enums.PlayerEnums.Gender;
import org.example.models.enums.Seasons;
import org.example.models.enums.Types.TileType;
import org.example.models.enums.Weather;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class DateTimeTest {
    private Date gameDate;
    private GameMap mockGameMap;
    private Player mockPlayer;

    @Before
    public void setUp() {
        // Create a new Date instance for each test
        gameDate = new Date();

        // Create a mock Player for the GameMap
        User mockUser = new User("testUser", "password", "test@example.com", "TestUser", Gender.Male);
        mockPlayer = new Player(mockUser);

        // Create a mock GameMap
        mockGameMap = new GameMap(20, 20, mockPlayer);
    }

    @Test
    public void testInitialValues() {
        assertEquals(1, gameDate.getDay());
        assertEquals(9, gameDate.getHour());
        assertEquals(Seasons.SPRING, gameDate.getSeason());
        assertNotNull(gameDate.getWeatherToday());
    }

    @Test
    public void testAdvanceTime() {
        // Test advancing time by a few hours
        gameDate.advanceTime(3, mockGameMap);
        assertEquals(12, gameDate.getHour());
        assertEquals(1, gameDate.getDay());

        gameDate.advanceTime(13, mockGameMap);
        assertEquals(12, gameDate.getHour());
        assertEquals(2, gameDate.getDay());
    }

    @Test
    public void testAdvanceDate() {
        // Test advancing by a few days
        gameDate.advanceDays(5, mockGameMap);
        assertEquals(6, gameDate.getDay());
        assertEquals(Seasons.SPRING, gameDate.getSeason());

        // Test advancing to the next season
        gameDate.advanceDays(23, mockGameMap);
        assertEquals(1, gameDate.getDay());
        assertEquals(Seasons.SUMMER, gameDate.getSeason());

        // Test advancing a full year
        gameDate.advanceDays(28 * 3, mockGameMap);
        assertEquals(1, gameDate.getDay());
        assertEquals(Seasons.SPRING, gameDate.getSeason());
    }

    @Test
    public void testDaysPerSeason() {
        // The game should have 28 days per season
        assertEquals(1, gameDate.getDay());
        gameDate.advanceDays(28, mockGameMap);
        assertEquals(1, gameDate.getDay()); // Day should reset to 1
        assertEquals(Seasons.SUMMER, gameDate.getSeason()); // Season should advance
    }

    @Test
    public void testWeatherChanges() {
        // Save initial weather
        Weather initialWeather = gameDate.getWeatherToday();

        // Advance multiple days and check that weather changes
        boolean weatherChanged = false;
        for (int i = 0; i < 10; i++) {
            gameDate.advanceDays(1, mockGameMap);
            if (gameDate.getWeatherToday() != initialWeather) {
                weatherChanged = true;
                break;
            }
        }

        // Weather should change at some point within 10 days
        assertTrue("Weather should change within 10 days", weatherChanged);
    }

    @Test
    public void testSetWeather() {
        gameDate.setWeatherTomorrow(Weather.RAINY);
        gameDate.advanceDays(1, mockGameMap);
        assertEquals(Weather.RAINY, gameDate.getWeatherToday());

        gameDate.setWeatherTomorrow(Weather.SUNNY);
        gameDate.advanceDays(1, mockGameMap);
        assertEquals(Weather.SUNNY, gameDate.getWeatherToday());
    }

    @Test
    public void testCheatThor() {
        // Create a location for the Thor cheat
        Location location = new Location(5, 5, TileType.GRASS);

        // Capture the console output to verify the cheat message
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            // Test the Thor cheat
            gameDate.cheatThor(location, mockGameMap);

            // Verify that a message was printed
            assertTrue(outContent.toString().contains("Thor has struck the location"));
        } finally {
            // Restore the original System.out
            System.setOut(originalOut);
        }
    }

    @Test
    public void testDisplayFormatting() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            gameDate.displayTime();
            gameDate.displayDate();
            gameDate.displaySeason();
            gameDate.displayWeather();
            gameDate.displayWeatherForecast();

            String output = outContent.toString();
            assertTrue(output.contains("Time:"));
            assertTrue(output.contains("Date: Day 1"));
            assertTrue(output.contains("Season: SPRING"));
            assertTrue(output.contains("Weather today:"));
            assertTrue(output.contains("Weather forecast for tomorrow:"));
        } finally {
            // Restore the original System.out
            System.setOut(originalOut);
        }
    }

    @Test
    public void testDayOfWeek() {
        // Capture the console output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            gameDate.displayDayOfWeek();

            String output = outContent.toString();
            assertTrue(output.contains("Day of the week:"));

            assertTrue(output.contains("Sunday"));

            outContent.reset();

            // Advance 1 day and check that day of week changes
            gameDate.advanceDays(1, mockGameMap);
            gameDate.displayDayOfWeek();

            // Next day should be Monday
            output = outContent.toString();
            assertTrue(output.contains("Monday"));
        } finally {
            // Restore the original System.out
            System.setOut(originalOut);
        }
    }

    @Test
    public void testGetDaysPassed() {
        // Create a second date instance
        Date oldDate = new Date();

        // Advance the current date
        gameDate.advanceDays(10, mockGameMap);

        // Check days passed
        assertEquals(10, gameDate.getDaysPassed(oldDate));

        // Advance to next season
        gameDate.advanceDays(20, mockGameMap);

        // Should be 30 days passed
        assertEquals(30, gameDate.getDaysPassed(oldDate));
    }

    @Test
    public void testGoTomorrow() {
        Weather currentWeather = gameDate.getWeatherToday();

        gameDate.setWeatherTomorrow(Weather.STORMY);

        gameDate.goTomorrow(mockGameMap);

        assertEquals(2, gameDate.getDay());
        assertEquals(Weather.STORMY, gameDate.getWeatherToday());

        assertNotEquals(currentWeather, gameDate.getWeatherToday());
    }

    @Test
    public void testSeasonRandomSeed() {
        // Test that seasons provide different random seeds
        Seasons spring = Seasons.SPRING;
        String springSeed = spring.getRandomSeed();

        // The seed should be one of the seeds available for the season
        String[] springSeeds = spring.getSeeds();
        boolean validSeed = false;
        for (String seed : springSeeds) {
            if (seed.equals(springSeed)) {
                validSeed = true;
                break;
            }
        }

        assertTrue("Random seed should be valid for the season", validSeed);
    }
}