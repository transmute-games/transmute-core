package games.transmute.cli;

/**
 * Configuration for generated projects.
 */
public class ProjectConfig {
    public String gameTitle = "My Game";
    public String gameVersion = "1.0.0";
    public int screenWidth = 640;
    public int screenScale = 2;
    public String packageName = "com.example.game";
    public String transmuteVersion = "0.1.0-ALPHA";
    
    public int getScreenHeight() {
        return screenWidth * 3 / 4; // 4:3 aspect ratio
    }
    
    public String getPackagePath() {
        return packageName.replace('.', '/');
    }
}
