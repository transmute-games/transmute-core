package games.transmute.cli;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates new TransmuteCore projects from templates.
 */
public class ProjectGenerator {
    
    public void generateProject(String projectName, String templateName, ProjectConfig config) throws IOException {
        Path projectPath = Paths.get(projectName);
        
        // Check if directory already exists
        if (Files.exists(projectPath)) {
            throw new IOException("Directory '" + projectName + "' already exists");
        }
        
        // Validate project name
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IOException("Project name cannot be empty");
        }
        
        System.out.println("Generating project '" + projectName + "' with template '" + templateName + "'...");
        
        // Create project directory
        Files.createDirectories(projectPath);
        
        // Set default config values if not set
        if (config.gameTitle.equals("My Game")) {
            config.gameTitle = projectName;
        }
        if (config.packageName.equals("com.example.game")) {
            config.packageName = "com.example." + sanitizePackageName(projectName);
        }
        
        // Validate and sanitize package name
        config.packageName = validateAndSanitizePackageName(config.packageName);
        
        // Create project structure
        createProjectStructure(projectPath, config);
        
        // Generate files from template
        generateFromTemplate(projectPath, templateName, projectName, config);
        
        // Create Gradle wrapper
        copyGradleWrapper(projectPath);
        
        System.out.println("Project structure created");
    }
    
    private void createProjectStructure(Path projectPath, ProjectConfig config) throws IOException {
        // Create directory structure
        String packagePath = config.getPackagePath();
        
        Files.createDirectories(projectPath.resolve("src/main/java").resolve(packagePath));
        Files.createDirectories(projectPath.resolve("src/main/resources"));
        Files.createDirectories(projectPath.resolve("gradle/wrapper"));
    }
    
    private void generateFromTemplate(Path projectPath, String templateName, String projectName, ProjectConfig config) throws IOException {
        Map<String, String> vars = new HashMap<>();
        vars.put("PROJECT_NAME", projectName);
        vars.put("GAME_TITLE", config.gameTitle);
        vars.put("GAME_VERSION", config.gameVersion);
        vars.put("PACKAGE_NAME", config.packageName);
        vars.put("TRANSMUTE_VERSION", config.transmuteVersion);
        vars.put("SCREEN_WIDTH", String.valueOf(config.screenWidth));
        vars.put("SCREEN_HEIGHT", String.valueOf(config.getScreenHeight()));
        vars.put("SCREEN_SCALE", String.valueOf(config.screenScale));
        
        switch (templateName) {
            case "basic":
                generateBasicTemplate(projectPath, config, vars);
                break;
            case "platformer":
                generatePlatformerTemplate(projectPath, config, vars);
                break;
            case "rpg":
                generateRPGTemplate(projectPath, config, vars);
                break;
            default:
                throw new IOException("Unknown template: " + templateName);
        }
    }
    
    private void generateBasicTemplate(Path projectPath, ProjectConfig config, Map<String, String> vars) throws IOException {
        String packagePath = config.getPackagePath();
        
        // build.gradle
        writeFile(projectPath.resolve("build.gradle"), generateBuildGradle(vars));
        
        // settings.gradle
        writeFile(projectPath.resolve("settings.gradle"), "rootProject.name = '" + vars.get("PROJECT_NAME") + "'\n");
        
        // Main Game class
        String gameClass = generateBasicGameClass(vars);
        writeFile(projectPath.resolve("src/main/java").resolve(packagePath).resolve("Game.java"), gameClass);
        
        // README.md
        writeFile(projectPath.resolve("README.md"), generateREADME(vars));
        
        // .gitignore
        writeFile(projectPath.resolve(".gitignore"), generateGitignore());
    }
    
    private void generatePlatformerTemplate(Path projectPath, ProjectConfig config, Map<String, String> vars) throws IOException {
        // Start with basic template
        generateBasicTemplate(projectPath, config, vars);
        
        String packagePath = config.getPackagePath();
        
        // Add platformer-specific classes
        writeFile(projectPath.resolve("src/main/java").resolve(packagePath).resolve("Player.java"), 
                  generatePlayerClass(vars));
        writeFile(projectPath.resolve("src/main/java").resolve(packagePath).resolve("Platform.java"),
                  generatePlatformClass(vars));
    }
    
    private void generateRPGTemplate(Path projectPath, ProjectConfig config, Map<String, String> vars) throws IOException {
        // Start with basic template
        generateBasicTemplate(projectPath, config, vars);
        
        String packagePath = config.getPackagePath();
        
        // Add RPG-specific classes
        writeFile(projectPath.resolve("src/main/java").resolve(packagePath).resolve("Entity.java"),
                  generateEntityClass(vars));
        writeFile(projectPath.resolve("src/main/java").resolve(packagePath).resolve("TileMap.java"),
                  generateTileMapClass(vars));
    }
    
    private String generateBuildGradle(Map<String, String> vars) {
        return String.format(
            "plugins {\n" +
            "    id 'application'\n" +
            "    id 'java'\n" +
            "}\n\n" +
            "group = '%s'\n" +
            "version = '%s'\n\n" +
            "repositories {\n" +
            "    mavenLocal()\n" +
            "    mavenCentral()\n" +
            "}\n\n" +
            "java {\n" +
            "    sourceCompatibility = JavaVersion.VERSION_17\n" +
            "    targetCompatibility = JavaVersion.VERSION_17\n" +
            "}\n\n" +
            "dependencies {\n" +
            "    implementation 'games.transmute:transmute-core:%s'\n" +
            "}\n\n" +
            "application {\n" +
            "    mainClass = '%s.Game'\n" +
            "}\n\n" +
            "run {\n" +
            "    workingDir = projectDir\n" +
            "}\n",
            vars.get("PACKAGE_NAME"),
            vars.get("GAME_VERSION"),
            vars.get("TRANSMUTE_VERSION"),
            vars.get("PACKAGE_NAME")
        );
    }
    
    private String generateBasicGameClass(Map<String, String> vars) {
        return String.format(
            "package %s;\n\n" +
            "import TransmuteCore.core.GameConfig;\n" +
            "import TransmuteCore.core.Manager;\n" +
            "import TransmuteCore.core.TransmuteCore;\n" +
            "import TransmuteCore.core.interfaces.services.IRenderer;\n" +
            "import TransmuteCore.graphics.Color;\n" +
            "import TransmuteCore.graphics.Context;\n\n" +
            "public class Game extends TransmuteCore {\n\n" +
            "    public Game(GameConfig config) {\n" +
            "        super(config);\n" +
            "    }\n\n" +
            "    @Override\n" +
            "    public void init() {\n" +
            "        // Initialize your game here\n" +
            "    }\n\n" +
            "    @Override\n" +
            "    public void update(Manager manager, double delta) {\n" +
            "        // Update game logic here\n" +
            "    }\n\n" +
            "    @Override\n" +
            "    public void render(Manager manager, IRenderer renderer) {\n" +
            "        Context ctx = (Context) renderer;\n" +
            "        \n" +
            "        // Clear screen\n" +
            "        ctx.renderFilledRectangle(0, 0, %s, %s, \n" +
            "            Color.toPixelInt(32, 32, 64, 255));\n" +
            "        \n" +
            "        // Render game here\n" +
            "        ctx.renderText(\"%s\", 10, 10, \n" +
            "            Color.toPixelInt(255, 255, 255, 255));\n" +
            "    }\n\n" +
            "    public static void main(String[] args) {\n" +
            "        GameConfig config = new GameConfig.Builder()\n" +
            "            .title(\"%s\")\n" +
            "            .version(\"%s\")\n" +
            "            .dimensions(%s, GameConfig.ASPECT_RATIO_SQUARE)\n" +
            "            .scale(%s)\n" +
            "            .build();\n\n" +
            "        Game game = new Game(config);\n" +
            "        game.start();\n" +
            "    }\n" +
            "}\n",
            vars.get("PACKAGE_NAME"),
            vars.get("SCREEN_WIDTH"),
            vars.get("SCREEN_HEIGHT"),
            vars.get("GAME_TITLE"),
            vars.get("GAME_TITLE"),
            vars.get("GAME_VERSION"),
            vars.get("SCREEN_WIDTH"),
            vars.get("SCREEN_SCALE")
        );
    }
    
    private String generatePlayerClass(Map<String, String> vars) {
        return String.format(
            "package %s;\n\n" +
            "import TransmuteCore.graphics.Color;\n" +
            "import TransmuteCore.graphics.Context;\n\n" +
            "public class Player {\n" +
            "    private int x, y;\n" +
            "    private int velocityY = 0;\n" +
            "    private boolean onGround = false;\n\n" +
            "    public Player(int x, int y) {\n" +
            "        this.x = x;\n" +
            "        this.y = y;\n" +
            "    }\n\n" +
            "    public void update(double delta) {\n" +
            "        // Apply gravity\n" +
            "        if (!onGround) {\n" +
            "            velocityY += 1;\n" +
            "        }\n" +
            "        y += velocityY;\n" +
            "    }\n\n" +
            "    public void render(Context ctx) {\n" +
            "        ctx.renderFilledRectangle(x, y, 16, 16, \n" +
            "            Color.toPixelInt(100, 200, 255, 255));\n" +
            "    }\n\n" +
            "    public void jump() {\n" +
            "        if (onGround) {\n" +
            "            velocityY = -15;\n" +
            "            onGround = false;\n" +
            "        }\n" +
            "    }\n\n" +
            "    public void setOnGround(boolean onGround) {\n" +
            "        this.onGround = onGround;\n" +
            "        if (onGround) velocityY = 0;\n" +
            "    }\n" +
            "}\n",
            vars.get("PACKAGE_NAME")
        );
    }
    
    private String generatePlatformClass(Map<String, String> vars) {
        return String.format(
            "package %s;\n\n" +
            "import TransmuteCore.graphics.Color;\n" +
            "import TransmuteCore.graphics.Context;\n\n" +
            "public class Platform {\n" +
            "    private int x, y, width, height;\n\n" +
            "    public Platform(int x, int y, int width, int height) {\n" +
            "        this.x = x;\n" +
            "        this.y = y;\n" +
            "        this.width = width;\n" +
            "        this.height = height;\n" +
            "    }\n\n" +
            "    public void render(Context ctx) {\n" +
            "        ctx.renderFilledRectangle(x, y, width, height,\n" +
            "            Color.toPixelInt(100, 100, 100, 255));\n" +
            "    }\n" +
            "}\n",
            vars.get("PACKAGE_NAME")
        );
    }
    
    private String generateEntityClass(Map<String, String> vars) {
        return String.format(
            "package %s;\n\n" +
            "import TransmuteCore.graphics.Context;\n\n" +
            "public abstract class Entity {\n" +
            "    protected int x, y;\n" +
            "    protected int health;\n\n" +
            "    public Entity(int x, int y) {\n" +
            "        this.x = x;\n" +
            "        this.y = y;\n" +
            "        this.health = 100;\n" +
            "    }\n\n" +
            "    public abstract void update(double delta);\n" +
            "    public abstract void render(Context ctx);\n\n" +
            "    public int getX() { return x; }\n" +
            "    public int getY() { return y; }\n" +
            "}\n",
            vars.get("PACKAGE_NAME")
        );
    }
    
    private String generateTileMapClass(Map<String, String> vars) {
        return String.format(
            "package %s;\n\n" +
            "import TransmuteCore.graphics.Context;\n\n" +
            "public class TileMap {\n" +
            "    private int[][] tiles;\n" +
            "    private int tileSize;\n\n" +
            "    public TileMap(int width, int height, int tileSize) {\n" +
            "        this.tiles = new int[height][width];\n" +
            "        this.tileSize = tileSize;\n" +
            "    }\n\n" +
            "    public void setTile(int x, int y, int type) {\n" +
            "        if (x >= 0 && x < tiles[0].length && y >= 0 && y < tiles.length) {\n" +
            "            tiles[y][x] = type;\n" +
            "        }\n" +
            "    }\n\n" +
            "    public void render(Context ctx) {\n" +
            "        // Render tiles\n" +
            "    }\n" +
            "}\n",
            vars.get("PACKAGE_NAME")
        );
    }
    
    private String generateREADME(Map<String, String> vars) {
        return String.format(
            "# %s\n\n" +
            "A game built with TransmuteCore Engine.\n\n" +
            "## Building\n\n" +
            "```bash\n" +
            "./gradlew build\n" +
            "```\n\n" +
            "## Running\n\n" +
            "```bash\n" +
            "./gradlew run\n" +
            "```\n\n" +
            "## Project Structure\n\n" +
            "- `src/main/java` - Java source files\n" +
            "- `src/main/resources` - Game assets (images, sounds, etc.)\n\n" +
            "## Documentation\n\n" +
            "For TransmuteCore documentation, visit:\n" +
            "https://github.com/transmute-games/transmute-core\n",
            vars.get("GAME_TITLE")
        );
    }
    
    private String generateGitignore() {
        return "# Gradle\n" +
               ".gradle/\n" +
               "build/\n" +
               "bin/\n\n" +
               "# IDE\n" +
               ".idea/\n" +
               ".vscode/\n" +
               "*.iml\n" +
               ".classpath\n" +
               ".project\n" +
               ".settings/\n\n" +
               "# OS\n" +
               ".DS_Store\n" +
               "Thumbs.db\n";
    }
    
    private void copyGradleWrapper(Path projectPath) throws IOException {
        // Create minimal gradle wrapper files
        writeFile(projectPath.resolve("gradlew"), generateGradlewScript());
        writeFile(projectPath.resolve("gradlew.bat"), generateGradlewBat());
        
        // Make gradlew executable on Unix systems
        try {
            projectPath.resolve("gradlew").toFile().setExecutable(true);
        } catch (Exception e) {
            // Ignore on Windows
        }
        
        // Create gradle-wrapper.properties
        String wrapperProps = "distributionBase=GRADLE_USER_HOME\n" +
                              "distributionPath=wrapper/dists\n" +
                              "distributionUrl=https\\://services.gradle.org/distributions/gradle-8.5-bin.zip\n" +
                              "zipStoreBase=GRADLE_USER_HOME\n" +
                              "zipStorePath=wrapper/dists\n";
        writeFile(projectPath.resolve("gradle/wrapper/gradle-wrapper.properties"), wrapperProps);
    }
    
    private String generateGradlewScript() {
        return "#!/bin/sh\n" +
               "gradle \"$@\"\n";
    }
    
    private String generateGradlewBat() {
        return "@echo off\n" +
               "gradle %*\n";
    }
    
    private void writeFile(Path path, String content) throws IOException {
        Files.createDirectories(path.getParent());
        Files.writeString(path, content);
    }
    
    /**
     * Sanitizes a string to be used as part of a Java package name.
     * Removes all non-alphanumeric characters and converts to lowercase.
     */
    private String sanitizePackageName(String input) {
        return input.toLowerCase().replaceAll("[^a-z0-9]", "");
    }
    
    /**
     * Validates and sanitizes a full package name (with dots).
     * Ensures it follows Java package naming conventions.
     */
    private String validateAndSanitizePackageName(String packageName) throws IOException {
        if (packageName == null || packageName.trim().isEmpty()) {
            throw new IOException("Package name cannot be empty");
        }
        
        // Remove whitespace
        packageName = packageName.trim();
        
        // Split by dots
        String[] parts = packageName.split("\\.");
        StringBuilder sanitized = new StringBuilder();
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            
            // Skip empty parts
            if (part.isEmpty()) {
                continue;
            }
            
            // Sanitize each part (remove non-alphanumeric, lowercase)
            part = part.toLowerCase().replaceAll("[^a-z0-9_]", "");
            
            // Check if part starts with a digit (invalid in Java)
            if (!part.isEmpty() && Character.isDigit(part.charAt(0))) {
                part = "_" + part;
            }
            
            // Skip if sanitization resulted in empty string
            if (part.isEmpty()) {
                continue;
            }
            
            // Java keywords that can't be used as package names
            if (isJavaKeyword(part)) {
                part = "_" + part;
            }
            
            if (sanitized.length() > 0) {
                sanitized.append(".");
            }
            sanitized.append(part);
        }
        
        String result = sanitized.toString();
        
        // Validate final result
        if (result.isEmpty()) {
            throw new IOException("Invalid package name: resulted in empty string after sanitization");
        }
        
        if (result.startsWith(".") || result.endsWith(".") || result.contains("..")) {
            throw new IOException("Invalid package name: contains empty segments");
        }
        
        return result;
    }
    
    /**
     * Checks if a string is a Java keyword.
     */
    private boolean isJavaKeyword(String word) {
        String[] keywords = {
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
            "class", "const", "continue", "default", "do", "double", "else", "enum",
            "extends", "final", "finally", "float", "for", "goto", "if", "implements",
            "import", "instanceof", "int", "interface", "long", "native", "new",
            "package", "private", "protected", "public", "return", "short", "static",
            "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while", "true", "false", "null"
        };
        
        for (String keyword : keywords) {
            if (keyword.equals(word)) {
                return true;
            }
        }
        return false;
    }
}
