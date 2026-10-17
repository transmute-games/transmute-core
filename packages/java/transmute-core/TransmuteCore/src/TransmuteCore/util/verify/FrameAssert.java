package TransmuteCore.util.verify;

import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.graphics.Context;

import java.util.Arrays;
import java.util.Objects;

/**
 * Pixel-buffer assertions for headless and CI verification.
 * Agents and tests use this seam to confirm a frame looks right without a window.
 */
public final class FrameAssert
{
    private FrameAssert()
    {
    }

    /**
     * @return FNV-1a 32-bit hash of the full pixel buffer.
     */
    public static int hash(IRenderer renderer)
    {
        int[] pixels = pixelsOf(renderer);
        int hash = 0x811c9dc5;
        for (int pixel : pixels)
        {
            hash ^= pixel;
            hash *= 0x01000193;
        }
        return hash;
    }

    /**
     * Asserts the pixel at ({@code x}, {@code y}) equals {@code expectedArgb}.
     */
    public static void assertPixel(IRenderer renderer, int x, int y, int expectedArgb)
    {
        Objects.requireNonNull(renderer, "renderer");
        int width = renderer.getWidth();
        int height = renderer.getHeight();
        if (x < 0 || y < 0 || x >= width || y >= height)
        {
            throw new AssertionError(String.format(
                "Pixel (%d,%d) out of bounds for %dx%d context", x, y, width, height));
        }
        int actual = pixelsOf(renderer)[y * width + x];
        if (actual != expectedArgb)
        {
            throw new AssertionError(String.format(
                "Pixel (%d,%d): expected 0x%08X but was 0x%08X", x, y, expectedArgb, actual));
        }
    }

    /**
     * Asserts the frame hash equals {@code expectedHash}.
     */
    public static void assertHash(IRenderer renderer, int expectedHash)
    {
        int actual = hash(renderer);
        if (actual != expectedHash)
        {
            throw new AssertionError(String.format(
                "Frame hash: expected 0x%08X but was 0x%08X", expectedHash, actual));
        }
    }

    /**
     * Asserts two renderers have identical pixel buffers.
     */
    public static void assertEquals(IRenderer expected, IRenderer actual)
    {
        Objects.requireNonNull(expected, "expected");
        Objects.requireNonNull(actual, "actual");
        if (expected.getWidth() != actual.getWidth() || expected.getHeight() != actual.getHeight())
        {
            throw new AssertionError(String.format(
                "Size mismatch: expected %dx%d but was %dx%d",
                expected.getWidth(), expected.getHeight(),
                actual.getWidth(), actual.getHeight()));
        }
        if (!Arrays.equals(pixelsOf(expected), pixelsOf(actual)))
        {
            throw new AssertionError("Pixel buffers differ (hash expected=0x"
                + Integer.toHexString(hash(expected))
                + " actual=0x"
                + Integer.toHexString(hash(actual))
                + ")");
        }
    }

    private static int[] pixelsOf(IRenderer renderer)
    {
        if (renderer instanceof Context context)
        {
            return context.getPixels();
        }
        throw new IllegalArgumentException(
            "FrameAssert requires Context (or IRenderer backed by pixel data); got "
                + renderer.getClass().getName());
    }
}
