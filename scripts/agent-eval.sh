#!/usr/bin/env bash
# Runs agent-eval fixtures (Java) plus multi-language hello verifies.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

echo "==> publish transmute-core to mavenLocal"
./gradlew :transmute-core:publishToMavenLocal -q

FIXTURES=(
  eval/fixtures/collect-coin
  eval/fixtures/platformer-jump
  eval/fixtures/menu-state
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

echo "==> python hello"
if ! (
  python3 -m venv "$ROOT/.venv-eval"
  # shellcheck disable=SC1091
  . "$ROOT/.venv-eval/bin/activate"
  pip install -q -e "$ROOT/packages/python"
  cd "$ROOT/examples/python/hello" && python -m hello --headless
); then
  echo "FAIL python hello"
  failed=1
else
  echo "PASS python hello"
fi

echo "==> javascript hello"
if ! (
  cd "$ROOT/packages/javascript" && npm test --silent
  cd "$ROOT/examples/javascript/hello" && npm install --silent && npm run verify
); then
  echo "FAIL javascript hello"
  failed=1
else
  echo "PASS javascript hello"
fi

echo "==> typescript hello"
if ! (
  cd "$ROOT/packages/typescript" && npm install --silent && npm test --silent
  cd "$ROOT/examples/typescript/hello" && npm install --silent && npm run verify
); then
  echo "FAIL typescript hello"
  failed=1
else
  echo "PASS typescript hello"
fi

echo "==> c hello"
if ! (
  cmake -S "$ROOT/packages/c" -B "$ROOT/packages/c/build" >/dev/null
  cmake --build "$ROOT/packages/c/build" >/dev/null
  ctest --test-dir "$ROOT/packages/c/build" --output-on-failure
  "$ROOT/packages/c/build/examples/hello_headless"
); then
  echo "FAIL c hello"
  failed=1
else
  echo "PASS c hello"
fi

if [[ "$failed" -ne 0 ]]; then
  echo "agent-eval: one or more fixtures failed"
  exit 1
fi
echo "agent-eval: all fixtures passed"
