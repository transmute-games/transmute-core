#!/usr/bin/env bash
set -euo pipefail
# Tag only the exact commit that passed push CI. Retry dispatch on reruns.
head="${RELEASE_SHA:?RELEASE_SHA is required}"
git fetch origin master --tags
if [ "$head" != "$(git rev-parse origin/master)" ]; then
  echo "Skipping superseded commit $head"
  exit 0
fi
existing="$(git tag --points-at "$head" | grep -E '^cli-v[0-9]+\.[0-9]+\.[0-9]+$' | sort -V | tail -1 || true)"
if [ -n "$existing" ]; then
  next="$existing"
else
  latest="$(git tag --list | grep -E '^cli-v[0-9]+\.[0-9]+\.[0-9]+$' | sort -V | tail -1 || true)"
  if [ -z "$latest" ]; then
    next=cli-v1.0.0
  else
    next="${latest%.*}.$((${latest##*.} + 1))"
  fi
  git tag "$next" "$head"
  git push origin "$next"
fi
printf 'tag=%s\n' "$next" >> "${GITHUB_OUTPUT:?GITHUB_OUTPUT is required}"
