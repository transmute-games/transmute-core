package TransmuteCore.GameEngine;

/**
 * Immutable configuration class containing all game window and engine settings.
 * <p>
 * This replaces the static getters in TransmuteCore with an immutable configuration object
 * that can be passed around as needed.
 * <p>
 * Usage:
 * <pre>{@code
 * GameConfig config = new GameConfig.Builder()
 *     .title("My Game")
 *     .version("1.0")
 *     .dimensions(320, GameConfig.ASPECT_RATIO_SQUARE)
 *     .scale(3)
 *     .targetFPS(60)
 *     .build();
 * }</pre>
 */
public final class GameConfig
{
    // Aspect ratio constants
    public static final int ASPECT_RATIO_WIDESCREEN = 0x0; // 16:9
    public static final int ASPECT_RATIO_SQUARE = 0x1;     // 4:3
    
    private final String title;
    private final String version;
    private final int width;
    private final int height;
    private final int scale;
    private final int aspectRatio;
    private final int targetFPS;
    private final int bufferCount;
    private final boolean fpsVerbose;
    private final boolean showStartScreen;
    private final boolean stopOnException;
    private final boolean headless;
    
    private GameConfig(Builder builder)
    {
        this.title = builder.title;
        this.version = builder.version;
        this.width = builder.width;
        this.height = builder.height;
        this.scale = builder.scale;
        this.aspectRatio = builder.aspectRatio;
        this.targetFPS = builder.targetFPS;
        this.bufferCount = builder.bufferCount;
        this.fpsVerbose = builder.fpsVerbose;
        this.showStartScreen = builder.showStartScreen;
        this.stopOnException = builder.stopOnException;
        this.headless = builder.headless;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public String getVersion()
    {
        return version;
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public int getHeight()
    {
        return height;
    }
    
    public int getScale()
    {
        return scale;
    }
    
    public int getAspectRatio()
    {
        return aspectRatio;
    }
    
    public int getTargetFPS()
    {
        return targetFPS;
    }
    
    public int getBufferCount()
    {
        return bufferCount;
    }
    
    public boolean isFpsVerbose()
    {
        return fpsVerbose;
    }
    
    public boolean isShowStartScreen()
    {
        return showStartScreen;
    }
    
    public boolean isStopOnException()
    {
        return stopOnException;
    }
    
    public boolean isHeadless()
    {
        return headless;
    }
    
    /**
     * @return The scaled width (width * scale).
     */
    public int getScaledWidth()
    {
        return width * scale;
    }
    
    /**
     * @return The scaled height (height * scale).
     */
    public int getScaledHeight()
    {
        return height * scale;
    }
    
    @Override
    public String toString()
    {
        return "GameConfig{" +
                "title='" + title + '\'' +
                ", version='" + version + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", scale=" + scale +
                ", aspectRatio=" + aspectRatio +
                ", targetFPS=" + targetFPS +
                ", bufferCount=" + bufferCount +
                ", fpsVerbose=" + fpsVerbose +
                ", showStartScreen=" + showStartScreen +
                ", stopOnException=" + stopOnException +
                ", headless=" + headless +
                '}';
    }
    
    /**
     * Builder for GameConfig.
     */
    public static class Builder
    {
        private String title = "Untitled Game";
        private String version = "1.0";
        private int width = 320;
        private int height = 240;
        private int scale = 1;
        private int aspectRatio = ASPECT_RATIO_SQUARE;
        private int targetFPS = 60;
        private int bufferCount = 3;
        private boolean fpsVerbose = false;
        private boolean showStartScreen = true; // Default to true for backward compatibility
        private boolean stopOnException = true; // Default to true for safety
        private boolean headless = false; // Default to false (normal windowed mode)
        
        public Builder title(String title)
        {
            if (title == null || title.trim().isEmpty())
            {
                throw new IllegalArgumentException("Title cannot be null or empty");
            }
            this.title = title;
            return this;
        }
        
        public Builder version(String version)
        {
            if (version == null || version.trim().isEmpty())
            {
                throw new IllegalArgumentException("Version cannot be null or empty");
            }
            this.version = version;
            return this;
        }
        
        public Builder dimensions(int width, int aspectRatio)
        {
            if (width <= 0)
            {
                throw new IllegalArgumentException("Width must be positive. Got: " + width);
            }
            if (aspectRatio != ASPECT_RATIO_WIDESCREEN && aspectRatio != ASPECT_RATIO_SQUARE)
            {
                throw new IllegalArgumentException("Invalid aspect ratio: " + aspectRatio);
            }
            
            this.width = width;
            this.aspectRatio = aspectRatio;
            
            // Calculate height based on aspect ratio
            if (aspectRatio == ASPECT_RATIO_WIDESCREEN)
            {
                this.height = width / 16 * 9;
            }
            else // ASPECT_RATIO_SQUARE
            {
                this.height = width / 4 * 3;
            }
            
            return this;
        }
        
        public Builder scale(int scale)
        {
            if (scale <= 0)
            {
                throw new IllegalArgumentException("Scale must be positive. Got: " + scale);
            }
            this.scale = scale;
            return this;
        }
        
        public Builder targetFPS(int targetFPS)
        {
            if (targetFPS <= 0)
            {
                throw new IllegalArgumentException("Target FPS must be positive. Got: " + targetFPS);
            }
            if (targetFPS > 1000)
            {
                throw new IllegalArgumentException("Target FPS too high: " + targetFPS);
            }
            this.targetFPS = targetFPS;
            return this;
        }
        
        public Builder bufferCount(int bufferCount)
        {
            if (bufferCount < 2 || bufferCount > 4)
            {
                throw new IllegalArgumentException("Buffer count should be 2-4. Got: " + bufferCount);
            }
            this.bufferCount = bufferCount;
            return this;
        }
        
        public Builder fpsVerbose(boolean fpsVerbose)
        {
            this.fpsVerbose = fpsVerbose;
            return this;
        }
        
        public Builder showStartScreen(boolean showStartScreen)
        {
            this.showStartScreen = showStartScreen;
            return this;
        }
        
        public Builder stopOnException(boolean stopOnException)
        {
            this.stopOnException = stopOnException;
            return this;
        }
        
        public Builder headless(boolean headless)
        {
            this.headless = headless;
            return this;
        }
        
        public GameConfig build()
        {
            return new GameConfig(this);
        }
    }
}
