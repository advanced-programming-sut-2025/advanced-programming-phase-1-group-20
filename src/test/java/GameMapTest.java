import org.example.models.MapDetails.GameMap;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


public class GameMapTest {
    @Test
    public void testPrintMap() {
        GameMap gameMap = new GameMap(101, 51, null);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            gameMap.printCurrentView(20, 15, 3);

            String printedOutput = outputStream.toString();

            if (printedOutput.isEmpty()) {
                throw new AssertionError("Map was not printed, output is empty");
            }

            if (!printedOutput.contains("@")) {
                throw new AssertionError("Player position '@' not found in the printed map");
            }

            String[] lines = printedOutput.split("\n");
            if (lines.length != 7) { // 2*3 + 1 = 7 lines for radius 3
                throw new AssertionError("Expected 7 lines in the printed map, but got " + lines.length);
            }
            

            System.out.println("[DEBUG_LOG] Map printed successfully:");
            System.out.println("[DEBUG_LOG] " + printedOutput);


        } finally {
            System.setOut(originalOut);
        }
        gameMap.printCurrentViewColored(50, 25, 50);
        System.out.println("testPrintMap: PASSED");
    }
}