package TransmuteCore.util.verify;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Records key events into a {@link PlaytestScript} while an agent or test drives a harness.
 * <pre>{@code
 * PlaytestRecorder rec = new PlaytestRecorder();
 * rec.hold(KeyEvent.VK_D);
 * harness.step(10); rec.advance(10);
 * rec.release(KeyEvent.VK_D);
 * rec.idle();
 * Files.writeString(path, rec.toScriptText());
 * }</pre>
 */
public final class PlaytestRecorder
{
    private final List<PlaytestScript.Step> steps = new ArrayList<>();
    private int frame;

    public int frame()
    {
        return frame;
    }

    /** Advances the recorder clock by {@code frames} (call after {@code harness.step}). */
    public PlaytestRecorder advance(int frames)
    {
        if (frames < 0)
        {
            throw new IllegalArgumentException("frames must be >= 0");
        }
        frame += frames;
        return this;
    }

    public PlaytestRecorder hold(int keyCode)
    {
        steps.add(new PlaytestScript.Step(frame, PlaytestScript.Action.HOLD, keyCode));
        return this;
    }

    public PlaytestRecorder press(int keyCode)
    {
        steps.add(new PlaytestScript.Step(frame, PlaytestScript.Action.PRESS, keyCode));
        return this;
    }

    public PlaytestRecorder release(int keyCode)
    {
        steps.add(new PlaytestScript.Step(frame, PlaytestScript.Action.RELEASE, keyCode));
        return this;
    }

    public PlaytestRecorder clear()
    {
        steps.add(new PlaytestScript.Step(frame, PlaytestScript.Action.CLEAR, 0));
        return this;
    }

    public PlaytestRecorder idle()
    {
        steps.add(new PlaytestScript.Step(frame, PlaytestScript.Action.IDLE, 0));
        return this;
    }

    public PlaytestScript toScript()
    {
        return PlaytestScript.of(steps);
    }

    public String toScriptText()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("# recorded playtest\n");
        for (PlaytestScript.Step step : steps)
        {
            sb.append(step.frame()).append(' ')
                .append(step.action().name().toLowerCase(Locale.ROOT));
            if (step.action() != PlaytestScript.Action.IDLE && step.action() != PlaytestScript.Action.CLEAR)
            {
                sb.append(' ').append(keyName(step.keyCode()));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public void write(Path path) throws IOException
    {
        Objects.requireNonNull(path, "path");
        Path parent = path.getParent();
        if (parent != null)
        {
            Files.createDirectories(parent);
        }
        Files.writeString(path, toScriptText(), StandardCharsets.UTF_8);
    }

    static String keyName(int keyCode)
    {
        return switch (keyCode)
        {
            case java.awt.event.KeyEvent.VK_LEFT -> "LEFT";
            case java.awt.event.KeyEvent.VK_RIGHT -> "RIGHT";
            case java.awt.event.KeyEvent.VK_UP -> "UP";
            case java.awt.event.KeyEvent.VK_DOWN -> "DOWN";
            case java.awt.event.KeyEvent.VK_SPACE -> "SPACE";
            case java.awt.event.KeyEvent.VK_ENTER -> "ENTER";
            case java.awt.event.KeyEvent.VK_ESCAPE -> "ESCAPE";
            case java.awt.event.KeyEvent.VK_SHIFT -> "SHIFT";
            case java.awt.event.KeyEvent.VK_A -> "A";
            case java.awt.event.KeyEvent.VK_D -> "D";
            case java.awt.event.KeyEvent.VK_W -> "W";
            case java.awt.event.KeyEvent.VK_S -> "S";
            default -> "VK_" + keyCode;
        };
    }
}
