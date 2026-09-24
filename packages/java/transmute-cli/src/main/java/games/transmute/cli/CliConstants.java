package games.transmute.cli;

import java.util.Arrays;
import java.util.List;

/**
 * Constants used throughout the CLI application.
 */
public final class CliConstants {

    private CliConstants() {
        // Prevent instantiation
    }

    // Version information
    public static final String CLI_VERSION = "1.0.0";
    public static final String ENGINE_VERSION = "1.0.0";

    // Available templates
    public static final String TEMPLATE_BASIC = "basic";
    public static final String TEMPLATE_PLATFORMER = "platformer";
    public static final String TEMPLATE_RPG = "rpg";

    public static final List<String> AVAILABLE_TEMPLATES = Arrays.asList(
        TEMPLATE_BASIC,
        TEMPLATE_PLATFORMER,
        TEMPLATE_RPG
    );

    // Template descriptions
    public static final String DESC_BASIC = "Minimal game with simple rendering";
    public static final String DESC_PLATFORMER = "2D platformer with physics and collision";
    public static final String DESC_RPG = "Top-down RPG with tile-based maps";

    // Default values
    public static final String DEFAULT_TEMPLATE = TEMPLATE_BASIC;
    public static final String DEFAULT_GAME_TITLE = "My Game";
    public static final String DEFAULT_GAME_VERSION = "1.0.0";
    public static final String DEFAULT_PACKAGE_NAME = "com.example.game";
    public static final int DEFAULT_SCREEN_WIDTH = 640;
    public static final int DEFAULT_SCREEN_SCALE = 2;

    // URLs
    public static final String REPOSITORY_URL = "https://github.com/transmute-games/transmute-core";
}
