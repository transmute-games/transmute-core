# Agent eval fixtures

End-to-end contracts for “prompt + assets → game that passes headless verify.”

CI runs each fixture’s golden solution. Agents (or humans) are scored by whether
their output passes the same `./gradlew test verifyHeadless` checks.

## Fixtures

| Fixture | Verb | Asserts |
|---------|------|---------|
| [`fixtures/collect-coin`](fixtures/collect-coin/) | Walk onto a Trigger coin | `collected==1`, `AudioProbe.assertPlayed("pickup")`, PlaytestScript |

## Run locally

```bash
./gradlew :transmute-core:publishToMavenLocal
./scripts/agent-eval.sh
```

Or one fixture:

```bash
cd eval/fixtures/collect-coin && ./gradlew test verifyHeadless
```

## Adding a fixture

1. Copy `fixtures/collect-coin` as a template.
2. Write `PROMPT.md` with pass criteria.
3. Ship a golden `src/` that passes.
4. Register the path in `scripts/agent-eval.sh` and `.github/workflows/ci.yml`.
