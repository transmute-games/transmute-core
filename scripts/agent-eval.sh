#!/usr/bin/env bash
# Runs agent-eval fixtures. Exit non-zero if any fail.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

echo "==> publish transmute-core to mavenLocal"
./gradlew :transmute-core:publishToMavenLocal -q

FIXTURES=(
  eval/fixtures/collect-coin
)

failed=0
for fixture in "${FIXTURES[@]}"; do
  echo "==> eval $fixture"
  if ! (cd "$ROOT/$fixture" && ./gradlew test verifyHeadless); then
    echo "FAIL $fixture"
    failed=1
  else
    echo "PASS $fixture"
  fi
done

if [[ "$failed" -ne 0 ]]; then
  echo "agent-eval: one or more fixtures failed"
  exit 1
fi
echo "agent-eval: all fixtures passed"
