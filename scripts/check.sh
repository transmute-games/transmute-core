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
  java)
    ./gradlew build :transmute-core:publishToMavenLocal
    for example in hello platformer rpg; do
      (cd "examples/$example" && ./gradlew test verifyHeadless)
    done
    ./scripts/agent-eval.sh
    ;;
  web)
    bun run check:web
    bun run test:web
    ;;
  all)
    for check in docs java web; do bash scripts/check.sh "$check"; done
    ;;
  *) echo "Usage: $0 [docs|java|web|all]" >&2; exit 2 ;;
esac
