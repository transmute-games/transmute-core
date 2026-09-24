# GameSpec contract

Language-agnostic properties manifest (`gamespec.properties`). All Transmute Core
ports MUST parse these keys the same way. Java remains the reference for edge cases.

## Core

| Key | Type | Default | Meaning |
|-----|------|---------|---------|
| `title` | string | `Game` | Window / project title |
| `version` | string | `1.0.0` | Game version |
| `width` | int | `320` | Framebuffer width |
| `height` | int | `180` | Framebuffer height |
| `scale` | int | `3` | Window scale (display only) |
| `headless` | bool | `false` | No window |
| `clear.r` / `clear.g` / `clear.b` / `clear.a` | int 0–255 | 32,32,64,255 | Clear color |
| `font` | path | — | Bitmap font classpath/resource |
| `image.<name>` | path | — | Image asset |
| `audio.<name>` | path | — | Audio asset (WAV preferred) |
| `spritesheet.<name>` | path | — | Registers image as `sheet-<name>` |
| `spritesheet.<name>.tile` | int | — | Documented tile size convention |

## World

| Key | Type | Meaning |
|-----|------|---------|
| `world.cols` / `world.rows` | int | Required together to build a world |
| `world.tile` | int | Tile size in pixels (default 16) |
| `world.border` | bool | Fill border with solid tiles |
| `world.solid` | `tx,ty;tx,ty` | Extra solid tiles |

## Spawns / triggers / state

| Key | Type | Meaning |
|-----|------|---------|
| `spawn.<name>` | `tx,ty` | Colored actor at tile coords |
| `spawn.<name>.w` / `.h` | int | Size in tiles (default 1) |
| `spawn.<name>.color` | `r,g,b[,a]` | Actor color |
| `trigger.<name>` | `tx,ty` | Enter-once volume |
| `trigger.<name>.w` / `.h` | int | Size in tiles (default 1) |
| `trigger.<name>.audio` | string | Cue name to play on enter |
| `state.initial` | string | Initial state id (default `play`) |

## Semantics

- Tile `(tx,ty)` maps to pixel `(tx * tile, ty * tile)`.
- Triggers fire `onEnter` once per entry until the actor exits.
- Ports may omit real audio hardware; they MUST still record play intents for verify.
