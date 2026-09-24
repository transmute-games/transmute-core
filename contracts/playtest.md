# PlaytestScript contract

Data-driven headless input. One command per line; `#` starts a comment.

```
# frame  action   key
0        hold     D
20       release  D
20       press    SPACE
25       idle
```

## Actions

| Action | Args | Meaning |
|--------|------|---------|
| `hold` | KEY | Key down until release |
| `release` | KEY | Key up |
| `press` | KEY | One-frame press |
| `idle` | — | No input change |
| `clear` | — | Release all keys |

Frames are **absolute** from the start of playback. Between scripted frames the
harness steps while holding current key state. After applying events at frame N,
the harness steps one frame.

## Keys

`LEFT` `RIGHT` `UP` `DOWN` `SPACE` `ENTER` `ESCAPE`/`ESC` `SHIFT`
`A` `D` `W` `S` `Z` `X` `C`, or `VK_<name>` / raw AWT-style names.

Scripts are portable across Java, Python, JavaScript, and C ports.
