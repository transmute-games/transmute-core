package games.transmute.cli;

import static games.transmute.cli.CliConstants.*;

/**
 * Configuration for generated projects.
 * Use the Builder to create instances.
 */
public class ProjectConfig {
    private String gameTitle;
    private String gameVersion;
    private int screenWidth;
    private int screenScale;
    private String packageName;
    private String transmuteVersion;
    
    private ProjectConfig(Builder builder) {
        this.gameTitle = builder.gameTitle;
        this.gameVersion = builder.gameVersion;
        this.screenWidth = builder.screenWidth;
        this.screenScale = builder.screenScale;
        this.packageName = builder.packageName;
        this.transmuteVersion = builder.transmuteVersion;
    }
    
    // Getters
    public String getGameTitle() { return gameTitle; }
    public String getGameVersion() { return gameVersion; }
    public int getScreenWidth() { return screenWidth; }
    public int getScreenScale() { return screenScale; }
    public String getPackageName() { return packageName; }
    public String getTransmuteVersion() { return transmuteVersion; }
    
    public int getScreenHeight() {
        return screenWidth * 3 / 4; // 4:3 aspect ratio
    }
    
    public String getPackagePath() {
        return packageName.replace('.', '/');
    }
    
    // Setters (for ProjectGenerator backwards compatibility)
    public void setGameTitle(String gameTitle) { this.gameTitle = gameTitle; }
    public void setGameVersion(String gameVersion) { this.gameVersion = gameVersion; }
    public void setScreenWidth(int screenWidth) { this.screenWidth = screenWidth; }
    public void setScreenScale(int screenScale) { this.screenScale = screenScale; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public void setTransmuteVersion(String transmuteVersion) { this.transmuteVersion = transmuteVersion; }
    
    /**
     * Creates a new builder with default values.
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder for ProjectConfig.
     */
    public static class Builder {
        private String gameTitle = DEFAULT_GAME_TITLE;
        private String gameVersion = DEFAULT_GAME_VERSION;
        private int screenWidth = DEFAULT_SCREEN_WIDTH;
        private int screenScale = DEFAULT_SCREEN_SCALE;
        private String packageName = DEFAULT_PACKAGE_NAME;
        private String transmuteVersion = ENGINE_VERSION;
        
        public Builder gameTitle(String gameTitle) {
            this.gameTitle = gameTitle;
            return this;
        }
        
        public Builder gameVersion(String gameVersion) {
            this.gameVersion = gameVersion;
            return this;
        }
        
        public Builder screenWidth(int screenWidth) {
            this.screenWidth = screenWidth;
            return this;
        }
        
        public Builder screenScale(int screenScale) {
            this.screenScale = screenScale;
            return this;
        }
        
        public Builder packageName(String packageName) {
            this.packageName = packageName;
            return this;
        }
        
        public Builder transmuteVersion(String transmuteVersion) {
            this.transmuteVersion = transmuteVersion;
            return this;
        }
        
        public ProjectConfig build() {
            return new ProjectConfig(this);
        }
    }
}
