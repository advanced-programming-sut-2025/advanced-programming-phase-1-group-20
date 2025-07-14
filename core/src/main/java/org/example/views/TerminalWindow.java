package org.example.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.controllers.GameMenuController;
import org.example.models.common.Result;
import org.example.models.enums.commands.GameMenuCommands;


public class TerminalWindow {
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 400;
    private static final Color BACKGROUND_COLOR = new Color(0, 0, 0, 0.8f);
    private static final Color TEXT_COLOR = new Color(0.8f, 0.8f, 0.8f, 1);

    private Stage stage;
    private Table window;
    private Table outputTable;
    private ScrollPane scrollPane;
    private TextField inputField;
    private boolean isVisible;
    private InputProcessor previousInputProcessor;
    private GameMenuController gameMenuController;

    public TerminalWindow(GameMenuController gameMenuController) {
        this.gameMenuController = gameMenuController;
        this.isVisible = false;

        // Create stage with its own viewport
        stage = new Stage(new ScreenViewport());

        // Create the main window
        window = new Table();
        window.setFillParent(true);
        window.setVisible(false);

        // Create the output area
        outputTable = new Table();
        outputTable.align(Align.topLeft);

        // Create a scroll pane for the output
        scrollPane = new ScrollPane(outputTable);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        // Create the input field
        Skin skin = new Skin(Gdx.files.internal("content/ui/uiskin.json"));
        inputField = new TextField("", skin);
        inputField.setMessageText("Enter command...");

        // Add enter key listener to input field
        inputField.setTextFieldListener((textField, key) -> {
            if (key == '\n' || key == '\r') {
                processCommand(textField.getText());
                textField.setText("");
            }
        });

        // Layout the window
        window.add(scrollPane).expand().fill().pad(10).row();
        window.add(inputField).fillX().pad(10, 10, 10, 10);

        // Add the window to the stage
        stage.addActor(window);

        // Add some welcome text
        addOutput("Terminal Window", Color.YELLOW);
        addOutput("Enter commands below. Type 'help' for a list of commands.", TEXT_COLOR);
        addOutput("Press ` (backtick) to close this window.", TEXT_COLOR);
    }


    public void toggle() {
        if (isVisible) {
            hide();
        } else {
            show();
        }
    }

    public void show() {
        if (!isVisible) {
            isVisible = true;
            window.setVisible(true);

            // Save the current input processor
            previousInputProcessor = Gdx.input.getInputProcessor();

            // Create an input multiplexer to handle both stage and backtick key
            InputMultiplexer multiplexer = new InputMultiplexer();

            // Add a custom input adapter to handle the backtick key
            multiplexer.addProcessor(new InputAdapter() {
                @Override
                public boolean keyDown(int keycode) {
                    if (keycode == Input.Keys.GRAVE) {
                        toggle();
                        return true;
                    }
                    return false;
                }
            });

            // Add the stage to handle UI input
            multiplexer.addProcessor(stage);

            // Set the multiplexer as the input processor
            Gdx.input.setInputProcessor(multiplexer);

            // Focus the input field
            stage.setKeyboardFocus(inputField);
        }
    }

    public void hide() {
        if (isVisible) {
            isVisible = false;
            window.setVisible(false);

            // Restore the previous input processor
            if (previousInputProcessor != null) {
                Gdx.input.setInputProcessor(previousInputProcessor);
            }
        }
    }

    private void processCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            return;
        }

        // Add the command to the output
        addOutput("> " + command, Color.GREEN);

        // Process the command
        if (command.equalsIgnoreCase("help")) {
            showHelp();
        } else if (command.equalsIgnoreCase("clear")) {
            clearOutput();
        } else {
            // Try to execute the command using the GameMenuController
            GameMenuCommands gameCommand = GameMenuCommands.getCommand(command);
            if (gameCommand != GameMenuCommands.None) {
                try {
                    // Execute the command and get the result
                    Result result = executeCommand(gameCommand, command);

                    // Display the result
                    if (result != null) {
                        if (result.success()) {
                            addOutput(result.message(), Color.WHITE);
                        } else {
                            addOutput("Error: " + result.message(), Color.RED);
                        }
                    } else {
                        addOutput("Command executed successfully.", Color.WHITE);
                    }
                } catch (Exception e) {
                    addOutput("Error executing command: " + e.getMessage(), Color.RED);
                }
            } else {
                addOutput("Unknown command. Type 'help' for a list of commands.", Color.RED);
            }
        }

        // Scroll to the bottom
        scrollPane.scrollTo(0, 0, 0, 0);
    }

    private Result executeCommand(GameMenuCommands command, String input) {
        String[] args = command.parseInput(input);

        switch (command) {
            // Game related commands
            case SelectMap:
                return gameMenuController.selectMap(args);
            case ExitGame:
                return gameMenuController.exitGame();
            case NextTurn:
                return gameMenuController.nextTurn();
            case VoteTerminate:
                return gameMenuController.voteTerminate(args);

            // Time-related commands
            case ShowTime:
                gameMenuController.showTime();
                return Result.success("Time displayed.");
            case ShowDate:
                gameMenuController.showDate();
                return Result.success("Date displayed.");
            case ShowDateTime:
                gameMenuController.showDateTime();
                return Result.success("Date and time displayed.");
            case AdvanceTime:
                return gameMenuController.advanceTime(args);
            case AdvanceDate:
                return gameMenuController.advanceDate(args);
            case DayOfWeek:
                gameMenuController.showDayOfWeek();
                return Result.success("Day of week displayed.");
            case ShowSeason:
                gameMenuController.showSeason();
                return Result.success("Season displayed.");

            // Weather related commands
            case ShowWeather:
                gameMenuController.showWeather();
                return Result.success("Weather displayed.");
            case ShowWeatherForecast:
                gameMenuController.showWeatherForecast();
                return Result.success("Weather forecast displayed.");
            case SetWeather:
                return gameMenuController.setWeather(args);
            case CheatThor:
                return gameMenuController.cheatThor(args);

            // Player Related
            case ShowInventory:
                gameMenuController.showInventory();
                return Result.success("Inventory displayed.");

            // Cheat commands
            case CheatAddFavourites:
                gameMenuController.cheatAddFavourites(args);
                return Result.success("Favorites added.");
            case CheatTeleport:
                gameMenuController.cheatTeleport(args);
                return Result.success("Teleported.");
            case CheatSetBackPackFull:
                gameMenuController.cheatBackPackFull();
                return Result.success("Backpack filled.");
            case CheatTeleportHome:
                gameMenuController.cheatTeleportHome();
                return Result.success("Teleported to home.");
            case CheatTeleportMarkets:
                gameMenuController.cheatTeleportMarkets(args);
                return Result.success("Teleported to market.");
            case CheatBuildGreenHouse:
                return gameMenuController.greenhouseBuild();
            case CheatGiveItems:
                gameMenuController.cheatGiveItems();
                return Result.success("Items given.");
            case CheatGiveAllRecipe:
                gameMenuController.cheatGiveAllRecipe();
                return Result.success("All recipes given.");
            case CheatFriendShipLevel:
                return gameMenuController.cheatFriendShipLevel(args);
            case CheatIncreaseFriendshipLevel:
                return gameMenuController.increaseFRLEVEL(args);
            case CheatIncreaseXP:
                return gameMenuController.increaseXP(args);

            // Default case for unhandled commands
            default:
                return Result.error("Command not implemented yet.");
        }
    }


    private void showHelp() {
        addOutput("Available Commands:", Color.YELLOW);
        addOutput("help - Show this help message", TEXT_COLOR);
        addOutput("clear - Clear the terminal output", TEXT_COLOR);
        addOutput("", TEXT_COLOR);
        addOutput("Game Commands:", Color.YELLOW);
        addOutput("game map <number> - Select a map", TEXT_COLOR);
        addOutput("exit game - Exit the game", TEXT_COLOR);
        addOutput("next turn - Advance to the next turn", TEXT_COLOR);
        addOutput("", TEXT_COLOR);
        addOutput("Time Commands:", Color.YELLOW);
        addOutput("time - Show the current time", TEXT_COLOR);
        addOutput("date - Show the current date", TEXT_COLOR);
        addOutput("clock/datetime - Show the current date and time", TEXT_COLOR);
        addOutput("cheat advance time <hours> - Advance time by hours", TEXT_COLOR);
        addOutput("cheat advance date <days> - Advance date by days", TEXT_COLOR);
        addOutput("day of week - Show the current day of the week", TEXT_COLOR);
        addOutput("season - Show the current season", TEXT_COLOR);
        addOutput("", TEXT_COLOR);
        addOutput("Weather Commands:", Color.YELLOW);
        addOutput("weather - Show the current weather", TEXT_COLOR);
        addOutput("weather forecast - Show the weather forecast", TEXT_COLOR);
        addOutput("cheat weather set <type> - Set the weather", TEXT_COLOR);
        addOutput("", TEXT_COLOR);
        addOutput("Cheat Commands:", Color.YELLOW);
        addOutput("cheat add favorites <character> - Add favorites for a character", TEXT_COLOR);
        addOutput("cheat teleport <x> <y> - Teleport to coordinates", TEXT_COLOR);
        addOutput("cheat set backpack full - Fill the backpack", TEXT_COLOR);
        addOutput("cheat teleport house - Teleport to house", TEXT_COLOR);
        addOutput("cheat teleport markets -m <market> - Teleport to a market", TEXT_COLOR);
        addOutput("cheat build green house - Build a greenhouse", TEXT_COLOR);
        addOutput("cheat give items - Give items", TEXT_COLOR);
        addOutput("give recepies - Give all recipes", TEXT_COLOR);
        addOutput("friendship level <name> - Show friendship level", TEXT_COLOR);
        addOutput("level <name> -a <amount> - Increase friendship level", TEXT_COLOR);
        addOutput("xp <name> -a <amount> - Increase XP", TEXT_COLOR);
    }


    public void addOutput(String text, Color color) {
        Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), color);
        Label label = new Label(text, style);
        label.setWrap(true);
        outputTable.add(label).fillX().expandX().padBottom(5).row();
    }


    public void clearOutput() {
        outputTable.clear();
        addOutput("Terminal cleared.", TEXT_COLOR);
    }


    public void render(float delta) {
        if (isVisible) {
            // Update the stage
            stage.act(delta);

            // Draw the background
            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);

            // Draw the stage
            stage.draw();

            Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
        }
    }


    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
    }
}
