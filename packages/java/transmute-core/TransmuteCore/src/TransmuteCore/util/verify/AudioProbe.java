package TransmuteCore.util.verify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Records {@link TransmuteCore.assets.types.AudioPlayer} calls for headless verify.
 * Install around a harness step; asserts do not require a sound device.
 * <pre>{@code
 * try (AudioProbe probe = AudioProbe.install()) {
 *     harness.step(1);
 *     probe.assertPlayed("jump");
 * }
 * }</pre>
 */
public final class AudioProbe implements AutoCloseable
{
    public enum Kind
    {
        PLAY,
        LOOP,
        STOP,
        RESUME
    }

    public static final class Event
    {
        private final Kind kind;
        private final String name;

        public Event(Kind kind, String name)
        {
            this.kind = Objects.requireNonNull(kind, "kind");
            this.name = Objects.requireNonNull(name, "name");
        }

        public Kind getKind()
        {
            return kind;
        }

        public String getName()
        {
            return name;
        }

        @Override
        public String toString()
        {
            return kind + ":" + name;
        }
    }

    private static final ThreadLocal<AudioProbe> CURRENT = new ThreadLocal<>();

    private final List<Event> events = new ArrayList<>();
    private boolean closed;

    private AudioProbe()
    {
    }

    /**
     * Starts recording on this thread. Close (or try-with-resources) to uninstall.
     */
    public static AudioProbe install()
    {
        AudioProbe previous = CURRENT.get();
        if (previous != null && !previous.closed)
        {
            throw new IllegalStateException("AudioProbe already installed on this thread");
        }
        AudioProbe probe = new AudioProbe();
        CURRENT.set(probe);
        return probe;
    }

    /**
     * @return the active probe for this thread, or null
     */
    public static AudioProbe current()
    {
        return CURRENT.get();
    }

    /**
     * Called by {@code AudioPlayer}; no-op when no probe is installed.
     */
    public static void record(Kind kind, String name)
    {
        AudioProbe probe = CURRENT.get();
        if (probe == null || probe.closed || name == null)
        {
            return;
        }
        probe.events.add(new Event(kind, name));
    }

    public List<Event> events()
    {
        return Collections.unmodifiableList(events);
    }

    public void clear()
    {
        events.clear();
    }

    public int count(Kind kind, String name)
    {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(name, "name");
        int n = 0;
        for (Event event : events)
        {
            if (event.kind == kind && event.name.equals(name))
            {
                n++;
            }
        }
        return n;
    }

    public void assertPlayed(String name)
    {
        assertAtLeast(Kind.PLAY, name, 1);
    }

    public void assertLooped(String name)
    {
        assertAtLeast(Kind.LOOP, name, 1);
    }

    public void assertStopped(String name)
    {
        assertAtLeast(Kind.STOP, name, 1);
    }

    public void assertPlayCount(String name, int expected)
    {
        int actual = count(Kind.PLAY, name);
        if (actual != expected)
        {
            throw new AssertionError(String.format(
                "Audio play count for '%s': expected %d but was %d (events=%s)",
                name, expected, actual, events));
        }
    }

    public void assertNothingPlayed()
    {
        if (!events.isEmpty())
        {
            throw new AssertionError("Expected no audio events but was " + events);
        }
    }

    private void assertAtLeast(Kind kind, String name, int min)
    {
        int actual = count(kind, name);
        if (actual < min)
        {
            throw new AssertionError(String.format(
                "Expected %s '%s' at least %d time(s) but was %d (events=%s)",
                kind, name, min, actual, events));
        }
    }

    @Override
    public void close()
    {
        if (closed)
        {
            return;
        }
        closed = true;
        if (CURRENT.get() == this)
        {
            CURRENT.remove();
        }
    }
}
