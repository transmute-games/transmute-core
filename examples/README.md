# Examples

| Language | Path |
|----------|------|
| Java (reference) | [java/](java/) |
| Python | [python/hello](python/hello/) |
| JavaScript | [javascript/hello](javascript/hello/) |
| C | build `packages/c` → `hello_headless` |

## Java

```bash
./gradlew :transmute-core:publishToMavenLocal
cd examples/java/hello && ./gradlew test verifyHeadless
```

## Python

```bash
python -m venv .venv && . .venv/bin/activate
pip install -e packages/python
cd examples/python/hello && python -m hello --headless
```

## JavaScript

```bash
cd packages/javascript && npm test
cd examples/javascript/hello && npm install && npm run verify
```

## C

```bash
cmake -S packages/c -B packages/c/build && cmake --build packages/c/build
packages/c/build/examples/hello_headless
```
