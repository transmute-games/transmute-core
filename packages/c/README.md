# Transmute Core (C)

Agent-subset C library (CMake). See [contracts/](../../contracts/).

```bash
cmake -S . -B build
cmake --build build
ctest --test-dir build --output-on-failure
./build/examples/hello_headless
```
