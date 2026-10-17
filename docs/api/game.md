# Game lifecycle

Configure an instance with `GameConfig`, then implement the three game callbacks. This example shows the authoring surface; supply your own rendering and main method.

## Configuration

```java
GameConfig config = new GameConfig.Builder()
    .title("My Game").version("0.1.0")
    .size(320, 180).scale(3)
    .showStartScreen(false)
    .build();
```

The logical pixel surface is 320 × 180. Scale controls the window size. For CI, add `.headless(true)` and drive frames through the harness instead of opening a window.

## Callbacks

```java
@Override
public void init() {
    getManager().bootstrapDefaults();
}

@Override
public void update(Manager manager, double delta) {
    // Read input and advance game logic.
}

@Override
public void render(Manager manager, IRenderer renderer) {
    Context context = (Context) renderer;
    // Draw the current game state.
}
```

Imports come from `TransmuteCore.core` for `TransmuteCore`, `GameConfig`, and `Manager`; `IRenderer` is in `TransmuteCore.core.interfaces.services`, and `Context` is in `TransmuteCore.graphics`.

## Services

Use `manager.getAssetManager()`, `getStateManager()`, `getObjectManager()`, and `getInputHandler()`. Bootstrap defaults before accessing default services. Prefer `getInputHandler()` because headless mode supplies a `SimulatedInput` implementation.

Use [assets and manifests](assets.md) next, or copy the complete [hello example](../../examples/java/hello).
