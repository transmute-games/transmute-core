# Agent eval fixtures

End-to-end contracts for “prompt + assets → game that passes headless verify.”

CI runs each fixture’s golden solution. Agents are scored by whether their output
passes the same `./gradlew test verifyHeadless` checks.

## Fixtures

| Fixture | Verb | Asserts |
|---------|------|---------|
| [`fixtures/collect-coin`](fixtures/collect-coin/) | Walk onto Trigger | `collected==1`, `AudioProbe`, GameSpec spawn/trigger |
| [`fixtures/platformer-jump`](fixtures/platformer-jump/) | Body2D jump | PlaytestScript SPACE raises Y |
| [`fixtures/menu-state`](fixtures/menu-state/) | Menu → play | `state.initial=menu`, ENTER pushes `play` |

## Run locally

```bash
./gradlew :transmute-core:publishToMavenLocal
./scripts/agent-eval.sh
```

## Adding a fixture

1. Copy an existing fixture directory.
2. Write `PROMPT.md` with pass criteria.
3. Ship a golden `src/` that passes.
4. Register the path in `scripts/agent-eval.sh`.
