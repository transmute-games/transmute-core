package TransmuteCore.core;

import TransmuteCore.assets.AssetManager;
import TransmuteCore.assets.AssetPack;
import TransmuteCore.graphics.Color;
import TransmuteCore.world.Trigger;
import TransmuteCore.world.World;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;

/**
 * Declarative game configuration loaded from a properties manifest.
 * Agents fill {@code gamespec.properties}; the engine builds {@link GameConfig}
 * and optionally an {@link AssetPack}.
 * <p>
 * Example:
 * <pre>
 * title=My Game
 * version=1.0.0
 * width=320
 * height=180
 * scale=3
 * headless=false
 * clear.r=32
 * clear.g=32
 * clear.b=64
 * font=fonts/font.png
 * image.player=sprites/player.png
 * </pre>
 */
public final class GameSpec
{
    private final String title;
    private final String version;
    private final int width;
    private final int height;
    private final int scale;
    private final boolean headless;
    private final int clearColor;
    private final Properties raw;

    private GameSpec(Builder b)
    {
        this.title = b.title;
        this.version = b.version;
        this.width = b.width;
        this.height = b.height;
        this.scale = b.scale;
        this.headless = b.headless;
        this.clearColor = b.clearColor;
        this.raw = b.raw;
    }

    public static GameSpec loadClasspath(String resourcePath)
    {
        try (InputStream in = GameSpec.class.getClassLoader().getResourceAsStream(resourcePath))
        {
            if (in == null)
            {
                throw new IllegalArgumentException("GameSpec not found on classpath: " + resourcePath);
            }
            return parse(in);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Failed to load GameSpec: " + resourcePath, e);
        }
    }

    public static GameSpec loadFile(Path path)
    {
        try (InputStream in = Files.newInputStream(path))
        {
            return parse(in);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Failed to load GameSpec: " + path, e);
        }
    }

    public GameConfig toGameConfig()
    {
        return new GameConfig.Builder()
            .title(title)
            .version(version)
            .size(width, height)
            .scale(scale)
            .headless(headless)
            .showStartScreen(false)
            .build();
    }

    /**
     * Loads assets described in this spec into {@code assetManager}.
     */
    public AssetPack loadAssets(AssetManager assetManager)
    {
        AssetPack pack = AssetPack.create(assetManager);
        String font = raw.getProperty("font");
        if (font != null && !font.isBlank())
        {
            pack.font(font.trim());
        }
        for (String name : raw.stringPropertyNames())
        {
            String value = raw.getProperty(name).trim();
            if (name.startsWith("image."))
            {
                pack.image(name.substring("image.".length()), value);
            }
            else if (name.startsWith("audio."))
            {
                pack.sound(name.substring("audio.".length()), value);
            }
        }
        pack.registerSpritesheetsFrom(raw);
        return pack.registerAndLoad();
    }

    /**
     * Builds a {@link World} when {@code world.cols} / {@code world.rows} are present.
     * Optional keys: {@code world.tile} (default 16), {@code world.border=true},
     * {@code world.solid=5,7;6,7} (tile coordinates),
     * {@code spawn.&lt;name&gt;=tx,ty} (+ optional {@code .color=r,g,b}, {@code .w}/ {@code .h} in tiles),
     * {@code trigger.&lt;name&gt;=tx,ty} (+ optional {@code .w}/ {@code .h}, {@code .audio=cue}).
     *
     * @return the world, or null if no world keys are set
     */
    public World createWorld()
    {
        if (!raw.containsKey("world.cols") || !raw.containsKey("world.rows"))
        {
            return null;
        }
        int cols = Integer.parseInt(raw.getProperty("world.cols").trim());
        int rows = Integer.parseInt(raw.getProperty("world.rows").trim());
        int tile = Integer.parseInt(raw.getProperty("world.tile", "16").trim());
        World world = World.grid(cols, rows, tile).clearColor(clearColor);
        if (Boolean.parseBoolean(raw.getProperty("world.border", "false")))
        {
            world.fillBorder(World.SOLID);
        }
        String solids = raw.getProperty("world.solid", "").trim();
        if (!solids.isEmpty())
        {
            for (String pair : solids.split(";"))
            {
                String[] xy = pair.trim().split(",");
                if (xy.length == 2)
                {
                    world.setTile(Integer.parseInt(xy[0].trim()), Integer.parseInt(xy[1].trim()), World.SOLID);
                }
            }
        }
        applySpawns(world, tile);
        applyTriggers(world, tile);
        return world;
    }

    /**
     * {@code state.initial} from the manifest (default {@code play}).
     * Agents use this to pick which {@link TransmuteCore.state.State} to push first.
     */
    public String getInitialState()
    {
        return raw.getProperty("state.initial", "play").trim();
    }

    private void applySpawns(World world, int tile)
    {
        for (String name : baseNames("spawn."))
        {
            String pos = raw.getProperty("spawn." + name);
            if (pos == null || pos.isBlank())
            {
                continue;
            }
            int[] xy = parsePair(pos);
            int tw = Integer.parseInt(raw.getProperty("spawn." + name + ".w", "1").trim());
            int th = Integer.parseInt(raw.getProperty("spawn." + name + ".h", "1").trim());
            int color = parseColor(raw.getProperty("spawn." + name + ".color"), 0xFF6496FF);
            world.add(World.Actor.colored(xy[0] * tile, xy[1] * tile, tw * tile, th * tile, color).named(name));
        }
    }

    private void applyTriggers(World world, int tile)
    {
        for (String name : baseNames("trigger."))
        {
            String pos = raw.getProperty("trigger." + name);
            if (pos == null || pos.isBlank())
            {
                continue;
            }
            int[] xy = parsePair(pos);
            int tw = Integer.parseInt(raw.getProperty("trigger." + name + ".w", "1").trim());
            int th = Integer.parseInt(raw.getProperty("trigger." + name + ".h", "1").trim());
            String audio = raw.getProperty("trigger." + name + ".audio");
            world.addTrigger(Trigger.named(
                name, xy[0] * tile, xy[1] * tile, tw * tile, th * tile, audio));
        }
    }

    private java.util.LinkedHashSet<String> baseNames(String prefix)
    {
        java.util.LinkedHashSet<String> names = new java.util.LinkedHashSet<>();
        for (String key : raw.stringPropertyNames())
        {
            if (!key.startsWith(prefix))
            {
                continue;
            }
            String rest = key.substring(prefix.length());
            int dot = rest.indexOf('.');
            String name = dot < 0 ? rest : rest.substring(0, dot);
            if (!name.isBlank())
            {
                names.add(name);
            }
        }
        return names;
    }

    private static int[] parsePair(String value)
    {
        String[] parts = value.trim().split(",");
        if (parts.length != 2)
        {
            throw new IllegalArgumentException("Expected x,y but was: " + value);
        }
        return new int[] {
            Integer.parseInt(parts[0].trim()),
            Integer.parseInt(parts[1].trim())
        };
    }

    private static int parseColor(String rgb, int fallback)
    {
        if (rgb == null || rgb.isBlank())
        {
            return fallback;
        }
        String[] parts = rgb.trim().split(",");
        if (parts.length < 3)
        {
            return fallback;
        }
        int r = Integer.parseInt(parts[0].trim());
        int g = Integer.parseInt(parts[1].trim());
        int b = Integer.parseInt(parts[2].trim());
        int a = parts.length >= 4 ? Integer.parseInt(parts[3].trim()) : 255;
        return Color.toPixelInt(r, g, b, a);
    }

    public String getTitle()
    {
        return title;
    }

    public int getClearColor()
    {
        return clearColor;
    }

    public Properties getRaw()
    {
        return raw;
    }

    private static GameSpec parse(InputStream in) throws IOException
    {
        Properties props = new Properties();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)))
        {
            props.load(reader);
        }
        Builder b = new Builder();
        b.raw = props;
        b.title = props.getProperty("title", "Game");
        b.version = props.getProperty("version", "1.0.0");
        b.width = Integer.parseInt(props.getProperty("width", "320"));
        b.height = Integer.parseInt(props.getProperty("height", "180"));
        b.scale = Integer.parseInt(props.getProperty("scale", "3"));
        b.headless = Boolean.parseBoolean(props.getProperty("headless", "false"));
        int r = Integer.parseInt(props.getProperty("clear.r", "32"));
        int g = Integer.parseInt(props.getProperty("clear.g", "32"));
        int bl = Integer.parseInt(props.getProperty("clear.b", "64"));
        int a = Integer.parseInt(props.getProperty("clear.a", "255"));
        b.clearColor = Color.toPixelInt(r, g, bl, a);
        return b.build();
    }

    public static final class Builder
    {
        private String title = "Game";
        private String version = "1.0.0";
        private int width = 320;
        private int height = 180;
        private int scale = 3;
        private boolean headless;
        private int clearColor = Color.toPixelInt(32, 32, 64, 255);
        private Properties raw = new Properties();

        public Builder title(String title)
        {
            this.title = Objects.requireNonNull(title);
            return this;
        }

        public Builder version(String version)
        {
            this.version = Objects.requireNonNull(version);
            return this;
        }

        public Builder dimensions(int width, int height)
        {
            this.width = width;
            this.height = height;
            return this;
        }

        public Builder scale(int scale)
        {
            this.scale = scale;
            return this;
        }

        public Builder headless(boolean headless)
        {
            this.headless = headless;
            return this;
        }

        public Builder clearColor(int clearColor)
        {
            this.clearColor = clearColor;
            return this;
        }

        public GameSpec build()
        {
            return new GameSpec(this);
        }
    }
}
