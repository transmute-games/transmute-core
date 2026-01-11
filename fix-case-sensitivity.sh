#!/bin/bash

# Fix all remaining case-sensitivity issues in package declarations and imports
# This script ensures all package declarations match the actual lowercase directory structure

SRC_DIR="packages/core/TransmuteCore/src"

echo "Fixing case-sensitivity issues in package declarations and imports..."

# Fix all imports globally (capital to lowercase)
echo "Fixing imports..."
find "$SRC_DIR" -name "*.java" -exec sed -i '' \
    -e 's/import TransmuteCore\.Graphics\./import TransmuteCore.graphics./g' \
    -e 's/import TransmuteCore\.Input\./import TransmuteCore.input./g' \
    -e 's/import TransmuteCore\.Level\./import TransmuteCore.level./g' \
    -e 's/import TransmuteCore\.System\./import TransmuteCore.util./g' \
    -e 's/import TransmuteCore\.core\.Interfaces\./import TransmuteCore.core.interfaces./g' \
    {} \;

echo "Build complete. Run './gradlew :transmute-core:compileJava' to test."
