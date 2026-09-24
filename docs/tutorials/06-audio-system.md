# Tutorial 6: Audio System

Load WAV clips via GameSpec, play with `AudioPlayer`, assert cues headless with `AudioProbe`.

## What you'll learn

- `audio.*` manifest keys
- `AudioPlayer.play` / `loop` / `stop`
- Mute + `AudioProbe` for CI (no sound device required)

## Declare

```properties
audio.jump=sounds/jump.wav
audio.bgm=music/level.wav
```

```java
GameSpec.loadClasspath("gamespec.properties").loadAssets(manager.getAssetManager());
```

Supported: WAV / AIFF / AU (Java `Clip`). Prefer WAV.

## Play

```java
AudioPlayer.play("jump");   // one-shot
AudioPlayer.loop("bgm");    // music
AudioPlayer.stop("bgm");
AudioPlayer.setMuted(true); // headless / user mute — probe still records
```

## Headless assert

```java
try (GameHarness harness = GameHarness.of(() -> new Game(headlessConfig));
     AudioProbe probe = AudioProbe.install()) {
    AudioPlayer.setMuted(true);
    SimulatedInput input = (SimulatedInput) harness.game().getManager().getInputHandler();
    input.pressKey(KeyEvent.VK_SPACE);
    harness.step(1);
    probe.assertPlayed("jump");
    probe.assertPlayCount("jump", 1);
}
```

`AudioProbe` records even when muted or when the clip asset is missing — call `AudioPlayer.play("pickup")` from game logic so agents can verify intent.

## Eval fixture

See [`eval/fixtures/collect-coin`](../../eval/fixtures/collect-coin) for a full playtest that asserts `AudioProbe.assertPlayed("pickup")`.

## Next

[Tutorial 7: Level Design](07-level-design.md)
