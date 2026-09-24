package TransmuteCore.util.verify;

import TransmuteCore.input.SimulatedInput;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Data-driven headless playthrough for agents.
 * <p>
 * Script format (one command per line, {@code #} comments):
 * <pre>
 * # frame  action   key
 * 0        hold     RIGHT
 * 20       release  RIGHT
 * 20       press    SPACE
 * 25       idle
 * </pre>
 * Frames are absolute from the start of {@link #play}. Between scripted frames the
 * harness steps while holding current key state.
 */
public final class PlaytestScript
{
    public enum Action
    {
        HOLD, RELEASE, PRESS, IDLE, CLEAR
    }

    public record Step(int frame, Action action, int keyCode)
    {
    }

    private final List<Step> steps;

    private PlaytestScript(List<Step> steps)
    {
        this.steps = List.copyOf(steps);
    }

    public static PlaytestScript loadClasspath(String resourcePath)
    {
        try (InputStream in = PlaytestScript.class.getClassLoader().getResourceAsStream(resourcePath))
        {
            if (in == null)
            {
                throw new IllegalArgumentException("Playtest script not found: " + resourcePath);
            }
            return parse(in);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Failed to read playtest script: " + resourcePath, e);
        }
    }

    public static PlaytestScript loadFile(Path path)
    {
        try (InputStream in = Files.newInputStream(path))
        {
            return parse(in);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Failed to read playtest script: " + path, e);
        }
    }

    public static PlaytestScript parse(InputStream in) throws IOException
    {
        List<Step> steps = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)))
        {
            String line;
            int lineNo = 0;
            while ((line = reader.readLine()) != null)
            {
                lineNo++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#"))
                {
                    continue;
                }
                String[] parts = line.split("\\s+");
                if (parts.length < 2)
                {
                    throw new IllegalArgumentException("Bad playtest line " + lineNo + ": " + line);
                }
                int frame = Integer.parseInt(parts[0]);
                Action action = Action.valueOf(parts[1].toUpperCase(Locale.ROOT));
                int key = 0;
                if (action != Action.IDLE && action != Action.CLEAR)
                {
                    if (parts.length < 3)
                    {
                        throw new IllegalArgumentException("Key required on line " + lineNo + ": " + line);
                    }
                    key = resolveKey(parts[2]);
                }
                steps.add(new Step(frame, action, key));
            }
        }
        steps.sort((a, b) -> Integer.compare(a.frame(), b.frame()));
        return new PlaytestScript(steps);
    }

    /**
     * Runs this script against {@code harness}, applying events to {@code input}.
     * Steps the harness up through the last scripted frame (inclusive idle stretch).
     *
     * @return total frames stepped
     */
    public int play(GameHarness harness, SimulatedInput input)
    {
        Objects.requireNonNull(harness, "harness");
        Objects.requireNonNull(input, "input");
        if (steps.isEmpty())
        {
            return 0;
        }
        int frame = 0;
        int index = 0;
        int lastFrame = steps.get(steps.size() - 1).frame();
        while (frame <= lastFrame)
        {
            while (index < steps.size() && steps.get(index).frame() == frame)
            {
                apply(input, steps.get(index));
                index++;
            }
            harness.step(1);
            frame++;
        }
        return frame;
    }

    public List<Step> getSteps()
    {
        return steps;
    }

    private static void apply(SimulatedInput input, Step step)
    {
        switch (step.action())
        {
            case HOLD -> input.holdKey(step.keyCode());
            case PRESS -> input.pressKey(step.keyCode());
            case RELEASE -> input.releaseKey(step.keyCode());
            case CLEAR -> input.clear();
            case IDLE ->
            {
            }
        }
    }

    static int resolveKey(String token)
    {
        String t = token.toUpperCase(Locale.ROOT);
        if (t.startsWith("VK_"))
        {
            t = t.substring(3);
        }
        return switch (t)
        {
            case "LEFT" -> KeyEvent.VK_LEFT;
            case "RIGHT" -> KeyEvent.VK_RIGHT;
            case "UP" -> KeyEvent.VK_UP;
            case "DOWN" -> KeyEvent.VK_DOWN;
            case "SPACE" -> KeyEvent.VK_SPACE;
            case "ENTER" -> KeyEvent.VK_ENTER;
            case "ESCAPE", "ESC" -> KeyEvent.VK_ESCAPE;
            case "SHIFT" -> KeyEvent.VK_SHIFT;
            case "A" -> KeyEvent.VK_A;
            case "D" -> KeyEvent.VK_D;
            case "W" -> KeyEvent.VK_W;
            case "S" -> KeyEvent.VK_S;
            case "Z" -> KeyEvent.VK_Z;
            case "X" -> KeyEvent.VK_X;
            case "C" -> KeyEvent.VK_C;
            default ->
            {
                try
                {
                    yield KeyEvent.class.getField("VK_" + t).getInt(null);
                }
                catch (ReflectiveOperationException e)
                {
                    throw new IllegalArgumentException("Unknown key: " + token);
                }
            }
        };
    }
}
