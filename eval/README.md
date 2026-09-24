# Agent eval fixtures

Java golden solutions under `fixtures/` plus multi-language hello verifies via
`scripts/agent-eval.sh`.

| Fixture | Lang | Asserts |
|---------|------|---------|
| `fixtures/collect-coin` | Java | collect + AudioProbe + spawn/trigger |
| `fixtures/platformer-jump` | Java | Body2D jump script |
| `fixtures/menu-state` | Java | state.initial + ENTER → play |
| `examples/python/hello` | Python | clear pixel |
| `examples/javascript/hello` | JS | clear pixel |
| `examples/typescript/hello` | TS | clear pixel |
| `packages/c` hello_headless | C | clear pixel |

```bash
./scripts/agent-eval.sh
```
