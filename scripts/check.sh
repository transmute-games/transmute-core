#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
case "${1:-all}" in
  docs)
    bun run check:docs
    bun run check:budget
    bun test tests/release.test.ts
    actionlint
    shellcheck scripts/check.sh scripts/release-version.sh .githooks/pre-commit
    ;;
  java|engine)
    ./gradlew build :transmute-core:publishToMavenLocal
    for example in hello platformer rpg; do
      (cd "examples/java/$example" && ./gradlew test verifyHeadless)
    done
    python3 -m venv .venv-eval
    .venv-eval/bin/pip install -e "packages/python[dev]"
    .venv-eval/bin/pytest -q packages/python
    ./scripts/agent-eval.sh
    ;;
  web)
    bun run check:web
    bun run test:web
    ;;
  all)
    for check in docs engine web; do bash scripts/check.sh "$check"; done
    ;;
  *) echo "Usage: $0 [docs|engine|web|all]" >&2; exit 2 ;;
esac
