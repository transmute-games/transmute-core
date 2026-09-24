# Tutorial 5: State Management

Stack menus and gameplay with `StateManager` after `bootstrapDefaults()`.

## What you'll learn

- Push / peek / pop states
- Menu → play → pause flow
- Keeping headless verify on the play state

## Bootstrap

```java
@Override
public void init() {
    Manager manager = getManager();
    manager.bootstrapDefaults(); // installs StateManager + AssetManager + ObjectManager
    AssetPack.create(manager.getAssetManager())
        .font(AssetPack.DEFAULT_FONT_RESOURCE)
        .ensureDefaultFont();
    manager.getStateManager().push(new MainMenuState(manager.getStateManager()));
}
```

Delegate the loop:

```java
@Override
public void update(Manager manager, double delta) {
    manager.getStateManager().update(manager, delta);
}

@Override
public void render(Manager manager, IRenderer renderer) {
    manager.getStateManager().render(manager, renderer);
}
```

## State sketch

```java
public class MainMenuState extends State {
    public MainMenuState(StateManager sm) {
        super("mainMenu", sm);
    }

    @Override
    public void update(Manager manager, double delta) {
        var input = manager.getInputHandler();
        if (input.isKeyPressed(KeyEvent.VK_ENTER)) {
            stateManager.push(new PlayState(stateManager));
        }
    }

    @Override
    public void render(Manager manager, Context ctx) {
        ctx.renderFilledRectangle(0, 0, ctx.getWidth(), ctx.getHeight(), bg);
        ctx.renderText("ENTER TO START", 80, 100, white);
    }
}
```

Pause from play:

```java
if (input.isKeyPressed(KeyEvent.VK_ESCAPE)) {
    stateManager.push(new PauseState(stateManager));
}
// PauseState: ENTER -> stateManager.pop();
```

Use public `StateManager.pop()` to leave the top state.

## Headless tip

For CI, push `PlayState` directly (skip the menu) or script `ENTER` with `PlaytestScript` / `SimulatedInput`.

## Next

[Tutorial 6: Audio System](06-audio-system.md)
