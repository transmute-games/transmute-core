package Meteor.System.Asset.Type.Fonts;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

import Meteor.Graphics.Bitmap;
import Meteor.Graphics.Context;
import Meteor.Graphics.Sprites.Spritesheet;
import Meteor.System.Error;
import Meteor.System.Asset.Asset;
import Meteor.System.Asset.Type.Images.Image;
import Meteor.System.Asset.Type.Images.ImageUtils;
import Meteor.Units.Tuple2i;

public class Font extends Asset
{
    public static final String TYPE = "Font";

    /**
     * Default font used by the engine
     */
    public static Font defaultFont;

    public static final void initializeDefaultFont()
    {
        defaultFont = new Font("$default", "/font.png",
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                        + "abcdefghijklmnopqrstuvwxyz"
                        + "0123456789;'\"           \n"
                        + "!@#$%^&*()-+_=~.,<>?/\\[]|:", 4, 26, new Tuple2i(8, 10));

        defaultFont.addKerningRule("sxItrf<>?", 5, 0);
        defaultFont.addKerningRule("^1*()+-;:/.,", 4, 0);
        defaultFont.addKerningRule("j", 4, 1);
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
            image = ImageUtils.load(assetLoader.getResourceAsStream(filePath));
        } catch (IOException e)
        {
            e.printStackTrace();
            new Error(Error.FileNotFoundException(Font.TYPE, filePath));
        }

        image = ImageUtils.convertTo(BufferedImage.TYPE_INT_ARGB, image);
        Bitmap bmp = ImageUtils.getAsBitmap(image);
        target = new Spritesheet(bmp, defaultGlyphSize, new Tuple2i(0, 0), 0, 0);
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
            image = ImageUtils.load(className.getResourceAsStream(filePath));
        } catch (IOException e)
        {
            e.printStackTrace();
            new Error(Error.FileNotFoundException(Font.TYPE, filePath));
        }

        image = ImageUtils.convertTo(BufferedImage.TYPE_INT_ARGB, image);
        Bitmap bmp = ImageUtils.getAsBitmap(image);
        return new Spritesheet(bmp, defaultGlyphSize, new Tuple2i(0, 0), 0, 0);
    }

    @Override
    public Spritesheet getData()
    {
        if ((Spritesheet) target == null)
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
