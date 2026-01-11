package TransmuteCore.assets.Type.Fonts;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import TransmuteCore.graphics.Bitmap;
import TransmuteCore.graphics.Context;
import TransmuteCore.graphics.sprites.Spritesheet;
import TransmuteCore.assets.types.Image;
import TransmuteCore.util.Error;
import TransmuteCore.assets.Asset;
import TransmuteCore.math.Tuple2i;

public class Font extends Asset
{
    public static final String TYPE = "Font";

    /**
     * Default font used by the engine
     */
    public static Font defaultFont;

    public static void initializeDefaultFont(String filePath)
    {
        defaultFont = new Font("$default", filePath,
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        + "abcdefghijklmnopqrstuvwxyz"
                        + "0123456789;'\"           \n"
                        + "!@#$%^&*()-+_=~.,<>?/\\[]|:", 4, 26, new Tuple2i(8, 10));

        defaultFont.addKerningRule("sxItrf<>?", 5, 0);
        defaultFont.addKerningRule("^1*()+-;:/.,", 4, 0);
        defaultFont.addKerningRule("jg", 4, 1);
        defaultFont.addKerningRule("li!", 3, 0);
    }

    /**
     * The registrar for all glyph kerning policies in this font
     */
    private final HashMap<Character, KerningRule> kerningRules = new HashMap<>();

    /**
     * The dictionary of char glyphs that corresponds to the bitmap glyphs
     */
    private String glyphMap;

    /**
     * Number of rows in the bitmap font spritesheet
     */
    private int rows;

    /**
     * Number of columns in the bitmap font spritesheet
     */
    private int columns;

    /**
     * The default kerning policy for glyphs
     */
    private KerningRule defaultRule;

    /**
     * The default glyph sizing
     */
    private Tuple2i defaultGlyphSize;

    public Font(String name, String filePath, String glyphMap, int rows, int columns, Tuple2i defaultGlyphSize)
    {
        super(Font.TYPE, name, filePath);

        if (glyphMap == null || glyphMap.isEmpty()) {
            throw new IllegalArgumentException("Glyph map cannot be null or empty");
        }
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException(
                String.format("Rows and columns must be positive. Got rows: %d, columns: %d", rows, columns)
            );
        }
        if (defaultGlyphSize == null) {
            throw new IllegalArgumentException("Default glyph size cannot be null");
        }
        if (defaultGlyphSize.x <= 0 || defaultGlyphSize.y <= 0) {
            throw new IllegalArgumentException(
                String.format("Glyph size must be positive. Got: %dx%d", defaultGlyphSize.x, defaultGlyphSize.y)
            );
        }

        this.glyphMap = glyphMap;
        this.rows = rows;
        this.columns = columns;
        this.defaultGlyphSize = defaultGlyphSize;

        defaultRule = new KerningRule(defaultGlyphSize.x, 0);
        for (int i = 0; i < glyphMap.length(); i++)
        {
            char glyph = glyphMap.charAt(i);
            kerningRules.put(glyph, defaultRule);
        }
    }

    @Override
    public void load()
    {
        if (filePath == null)
        {
            new Error(Error.filePathException(Font.TYPE));
            return;
        }

        BufferedImage image = null;

        try
        {
            image = Image.load(getClass().getClassLoader().getResourceAsStream(filePath));
        } catch (IOException e)
        {
            e.printStackTrace();
            new Error(Error.FileNotFoundException(Font.TYPE, filePath));
        }

        assert image != null;
        image = Image.convertTo(BufferedImage.TYPE_INT_ARGB, image);
        target = new Spritesheet(image, defaultGlyphSize, new Tuple2i(0, 0), 0, 0);
    }

    public static Spritesheet load(Class<?> className, String filePath, Tuple2i defaultGlyphSize)
    {
        if (filePath == null)
        {
            new Error(Error.filePathException(Font.TYPE));
        }

        BufferedImage image = null;

        try
        {
            assert filePath != null;

            image = Image.load(className.getClassLoader().getResourceAsStream(filePath));
        } catch (IOException e)
        {
            e.printStackTrace();
            new Error(Error.FileNotFoundException(Font.TYPE, filePath));
        }

        image = Image.convertTo(BufferedImage.TYPE_INT_ARGB, image);
        return new Spritesheet(image, defaultGlyphSize, new Tuple2i(0, 0), 0, 0);
    }

    @Override
    public Spritesheet getData()
    {
        if (target == null)
        {
            new Error("[" + Font.TYPE + "]: [" + fileName + "] has not been loaded.");
        }

        return (Spritesheet) target;
    }

    /**
     * Draws a string of text using the bitmap font at a specified location
     * with a color tint, custom glyph scaling and transparency.
     *
     * @param ctx   The Game render 'canvas'.
     * @param text  The string of message.
     * @param x     x co-ordinate on screen.
     * @param y     y co-ordinate on screen.
     * @param color Color tint to be applied to each glyph.
     * @param scale Custom sizing for each glyph.
     * @param alpha Custom alpha (transparency) to be applied to each glyph.
     */
    public void render(Context ctx, String text, int x, int y, int color, float scale, float alpha)
    {
        if (ctx == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        if (text == null) {
            return; // Silently ignore null text
        }
        if (scale <= 0) {
            throw new IllegalArgumentException(
                String.format("Scale must be positive. Got: %.2f", scale)
            );
        }
        if (alpha < 0 || alpha > 1) {
            throw new IllegalArgumentException(
                String.format("Alpha must be between 0 and 1. Got: %.2f", alpha)
            );
        }
        
        int xRender = x, yRender = y;

        for (int i = 0; i < text.length(); i++)
        {
            int index = glyphMap.indexOf(text.charAt(i));
            if (index < 0) continue;

            char c = glyphMap.charAt(index);
            KerningRule kerning = getKerningRuleFor(c);
            int glyphHeight = defaultGlyphSize.y;
            int glyphWidth = kerning.glyphWidth;
            int glyphSink = kerning.glyphSink;

            if (c == ' ')
            {
                xRender += (int) ((float) glyphWidth * scale);
                continue;
            }

            if (c == '\n')
            {
                xRender = x;
                yRender += (int) ((float) glyphHeight * scale / 6 * 7);
                continue;
            }

            Bitmap glyph = ((Spritesheet) target).crop(index % columns, index / columns);
            if (scale != 1.0f)
            {
                glyph = glyph.getScaled(scale);
                glyphWidth = (int) ((float) glyphWidth * scale);
                glyphSink = (int) ((float) glyphSink * scale);
                glyphHeight = (int) ((float) glyphHeight * scale);
            }

            ctx.renderBitmap(glyph, xRender, yRender + glyphSink, alpha, color);

            xRender += glyphWidth;
        }
    }

    /**
     * Assigns a spacing rule for a set of glyphs.
     *
     * @param glyphs     The string of individual affected glyphs
     * @param glyphWidth Width of affected glyphs
     * @param glyphSink  Sinking of affected glyphs, for letters such as j, q, p
     */
    public void addKerningRule(String glyphs, int glyphWidth, int glyphSink)
    {
        if (glyphs == null || glyphs.isEmpty()) {
            throw new IllegalArgumentException("Glyphs string cannot be null or empty");
        }
        if (glyphWidth < 0) {
            throw new IllegalArgumentException(
                String.format("Glyph width cannot be negative. Got: %d", glyphWidth)
            );
        }
        for (int i = 0; i < glyphs.length(); i++)
        {
            addKerningRule(glyphs.charAt(i), glyphWidth, glyphSink);
        }
    }

    /**
     * Assigns a spacing rule for a given character.
     *
     * @param c          Character affected.
     * @param glyphWidth Width of affected glyphs.
     * @param glyphSink  Sinking of affected glyphs, for letters such as j, q, p.
     */
    public void addKerningRule(char c, int glyphWidth, int glyphSink)
    {
        KerningRule rule = new KerningRule(glyphWidth, glyphSink);
        kerningRules.put(c, rule);
    }

    /**
     * Supplies the width of a string using this font at default scaling.
     *
     * @param text Text used to calculate width.
     * @return Width of the given text.
     */
    public int widthOf(String text)
    {
        return widthOf(text, 1.0f);
    }

    /**
     * Supplies the width of a string using this font at a custom scaling.
     *
     * @param text  Text used to calculate width.
     * @param scale Scale at which this string would be hypothetically rendered in.
     * @return The width of a string using this font at a custom scaling.
     */
    public int widthOf(String text, float scale)
    {
        if (text == null) {
            return 0;
        }
        if (scale <= 0) {
            throw new IllegalArgumentException(
                String.format("Scale must be positive. Got: %.2f", scale)
            );
        }
        int width = 0;
        for (int i = 0; i < text.length(); i++)
        {
            int index = text.indexOf(text.charAt(i));
            if (index < 0) continue;

            char c = text.charAt(index);
            KerningRule kerning = getKerningRuleFor(c);
            int glyphWidth = kerning.glyphWidth;
            if (scale != 1f)
            {
                glyphWidth = (int) ((float) glyphWidth * scale);
            }
            width += glyphWidth;
        }
        return width;
    }

    /**
     * Get the width of text rendered with this font.
     *
     * @param text Text to measure
     * @return Width in pixels
     */
    public int getTextWidth(String text)
    {
        return widthOf(text);
    }

    /**
     * Get the height of text rendered with this font.
     *
     * @param text Text to measure (unused, height is constant)
     * @return Height in pixels
     */
    public int getTextHeight(String text)
    {
        return defaultGlyphSize.y;
    }

    /**
     * Get the default font height.
     *
     * @return Height in pixels
     */
    public int getHeight()
    {
        return defaultGlyphSize.y;
    }

    /**
     * Supplies the character mapping of the bitmap font image.
     *
     * @return Bitmap font character map.
     */
    public String getGlyphMap()
    {
        return glyphMap;
    }

    /**
     * Supplies the number of rows in this bitmap font.
     *
     * @return Row count in the bitmap font image.
     */
    public int getRows()
    {
        return rows;
    }

    /**
     * Supplies the number of columns in this bitmap font.
     *
     * @return Column count in the bitmap font image.
     */
    public int getColumns()
    {
        return columns;
    }

    /**
     * Supplies the default kerning policy for all glyphs in this font.
     *
     * @return Default kerning policy.
     */
    public KerningRule getDefaultKerningRule()
    {
        return defaultRule;
    }

    /**
     * Supplies the default glyph size for all glyphs in this font.
     *
     * @return Default glyph size.
     */
    public Tuple2i getDefaultGlyphSize()
    {
        return defaultGlyphSize;
    }

    /**
     * Supplies the kerning rule for a given character glyph.
     *
     * @param ch The investigated character.
     * @return First encountered rule for the provided character glyph.
     */
    public KerningRule getKerningRuleFor(char ch)
    {
        return kerningRules.get(ch);
    }

    /**
     * Defines a custom set spacing preferences for a list of given
     * chars (stored as Strings). Letters such as i, j, l are much
     * thinner than m, n, etc.
     * <p>
     * As such, unless the bitmap font is monospaced, each glyph should
     * be modified to their most aesthetically pleasing kerning policies.
     */
    private class KerningRule
    {

        int glyphWidth;
        int glyphSink;

        private KerningRule(int width, int sink)
        {
            this.glyphWidth = width;
            this.glyphSink = sink;
        }
    }
}
