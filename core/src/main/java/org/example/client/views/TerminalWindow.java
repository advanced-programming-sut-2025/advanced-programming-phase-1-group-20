package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.controllers.GameMenuController;
import org.example.common.models.App;
import org.example.common.models.common.Result;
import org.example.common.models.enums.commands.GameMenuCommands;


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
    private TextButton backButton;
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

        // Create back button
        backButton = new TextButton("Back", skin);
        backButton.addListener(event -> {
            if (event.toString().contains("touchDown")) {
                hide();
                return true;
            }
            return false;
        });

        // Layout the window
        window.add(scrollPane).expand().fill().pad(10).row();
        window.add(inputField).fillX().pad(10, 10, 5, 10).row();
        window.add(backButton).pad(5, 10, 10, 10);

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
            case TriggerLightning:
                return Result.success("Lightning triggered!");

            // Player Related
            case ShowInventory:
                gameMenuController.showInventory();
                return Result.success("Inventory displayed.");

            // Walking and navigation commands
            case Walk:
                return gameMenuController.walk(args);
            case WalkToVillage:
                return gameMenuController.walkToVillage();
            case WalkToFarm:
                return gameMenuController.walkToFarm(args);
            case TeleportToVillage:
                return gameMenuController.teleportToVillage();
            case TeleportToFarm:
                return gameMenuController.teleportToFarm();

            // Cheat commands
            case CheatAddItem:
                gameMenuController.cheatAddItem(args);
            case CheatGetFood:
                gameMenuController.getFood(args);
            case energyUnlimited:
                return gameMenuController.energyUnlimited();
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

            // Backpack and inventory commands
            case UpgradeBackpack:
                return gameMenuController.upgradeBackpack();
            case ShowBackpackInfo:
                return gameMenuController.showBackpackInfo();
            case TrashItem:
                return gameMenuController.trashItem(args);

            // Skill-related commands
            case ShowSkills:
                return gameMenuController.showSkills();
            case ShowSkillInfo:
                return gameMenuController.showSkillInfo(args);

            // Money command
            case ShowMoney:
                return gameMenuController.showMoney();

            // Tool-related commands
            case ToolEquip:
                return gameMenuController.equipTool(args);
            case ToolUse:
                return gameMenuController.useTool(args);
            case ToolShowCurrent:
                return gameMenuController.showCurrentTool();
            case ToolShowAvailable:
                return gameMenuController.showAvailableTools();
            case ToolUpgrade:
                return gameMenuController.upgradeTool(args);
            case CheatBuildGreenHouse:
                App.getGame().getCurrentPlayer().getCurrentFarm().getGreenHouse().setIsConstructed();
                return Result.success("Build Green House is displayed.");

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
        addOutput("cheat lightning - Trigger lightning effect", TEXT_COLOR);
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
        addOutput("", TEXT_COLOR);
        addOutput("Navigation Commands:", Color.YELLOW);
        addOutput("walk -l <x>,<y> - Walk to specific coordinates", TEXT_COLOR);
        addOutput("walk to village - Walk from farm to village", TEXT_COLOR);
        addOutput("walk to farm <index> - Walk from village to farm (0-3)", TEXT_COLOR);
        addOutput("teleport to village - Teleport to village", TEXT_COLOR);
        addOutput("teleport to farm - Teleport to farm", TEXT_COLOR);
        addOutput("", TEXT_COLOR);
        addOutput("Utility Commands:", Color.YELLOW);
        addOutput("screenshot - Take a screenshot of the current game view", TEXT_COLOR);
        addOutput("", TEXT_COLOR);
        addOutput("Backpack Commands:", Color.YELLOW);
        addOutput("upgrade backpack - Upgrade your backpack capacity", TEXT_COLOR);
        addOutput("backpack info - Show backpack information", TEXT_COLOR);
        addOutput("trash item <item> <amount> - Dispose of items (requires trash can)", TEXT_COLOR);
        addOutput("", TEXT_COLOR);
        addOutput("Skill Commands:", Color.YELLOW);
        addOutput("show skills - Show all skills and their levels", TEXT_COLOR);
        addOutput("skill info <skill> - Show detailed information about a skill", TEXT_COLOR);
        addOutput("", TEXT_COLOR);
        addOutput("Money Commands:", Color.YELLOW);
        addOutput("money - Show current money", TEXT_COLOR);
        addOutput("", TEXT_COLOR);
        addOutput("Tool Commands:", Color.YELLOW);
        addOutput("tools equip <tool> - Equip a tool from your backpack", TEXT_COLOR);
        addOutput("tools use -d <direction> - Use the equipped tool in a direction", TEXT_COLOR);
        addOutput("show current tool - Show the currently equipped tool", TEXT_COLOR);
        addOutput("show available tools - Show all tools in your backpack", TEXT_COLOR);
        addOutput("upgrade tool <tool> - Upgrade a tool (requires blacksmith)", TEXT_COLOR);
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
