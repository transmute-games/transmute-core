# Headless harness

`GameHarness` owns a game instance and advances a chosen number of frames. Use try-with-resources so the harness always closes.

## Create a harness

```java
GameConfig config = new GameConfig.Builder()
    .title("Test").version("0").size(320, 180).scale(1)
    .headless(true).showStartScreen(false).build();

try (GameHarness harness = GameHarness.of(() -> new MyGame(config))) {
    harness.step(1);
    FrameAssert.assertPixel(harness.renderer(), 0, 0, expectedArgb);
}
```

`GameHarness` and `FrameAssert` are in `TransmuteCore.util.verify`. `MyGame` and `expectedArgb` are supplied by your test.

## Simulate input

```java
SimulatedInput input = (SimulatedInput)
    harness.game().getManager().getInputHandler();
input.pressKey(KeyEvent.VK_SPACE);
harness.step(1);
input.releaseKey(KeyEvent.VK_SPACE);
harness.step(1);
```

`SimulatedInput` is in `TransmuteCore.input`; `KeyEvent` is in `java.awt.event`. Headless mode installs simulated input automatically. Release keys explicitly when testing transitions.

## More assertions

Use `AudioProbe` to inspect requested sounds without requiring an audio device, and `PlaytestScript` for data-driven input sequences. See [headless testing](../TESTING.md) for the repository verification loop.
