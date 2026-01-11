#!/bin/bash
# Complete the package migration

set -e

BASE_DIR="packages/core/TransmuteCore/src/TransmuteCore"

echo "Updating remaining package declarations..."

find "$BASE_DIR/graphics" -name "*.java" -type f -exec sed -i "" "s/package TransmuteCore\.Graphics/package TransmuteCore.graphics/g" {} \;
find "$BASE_DIR/input" -name "*.java" -type f -exec sed -i "" "s/package TransmuteCore\.Input/package TransmuteCore.input/g" {} \;
find "$BASE_DIR/assets" -name "*.java" -type f -exec sed -i "" "s/package TransmuteCore\.System\.Asset/package TransmuteCore.assets/g" {} \;
find "$BASE_DIR/ecs" -name "*.java" -type f -exec sed -i "" "s/package TransmuteCore\.Objects/package TransmuteCore.ecs/g" {} \;
find "$BASE_DIR/state" -name "*.java" -type f -exec sed -i "" "s/package TransmuteCore\.States/package TransmuteCore.state/g" {} \;
find "$BASE_DIR/level" -name "*.java" -type f -exec sed -i "" "s/package TransmuteCore\.Level/package TransmuteCore.level/g" {} \;
find "$BASE_DIR/data" -name "*.java" -type f -exec sed -i "" "s/package TransmuteCore\.Serialization/package TransmuteCore.data/g" {} \;
find "$BASE_DIR/data" -name "*.java" -type f -exec sed -i "" "s/package TransmuteCore\.System\.Serialization/package TransmuteCore.data/g" {} \;
find "$BASE_DIR/math" -name "*.java" -type f -exec sed -i "" "s/package TransmuteCore\.Units/package TransmuteCore.math/g" {} \;
find "$BASE_DIR/util" -name "*.java" -type f -exec sed -i "" "s/package TransmuteCore\.System/package TransmuteCore.util/g" {} \;

echo "✓ Package declarations updated"

echo "Updating all import statements..."

find "$BASE_DIR" -name "*.java" -type f -exec sed -i "" \
    -e "s/import TransmuteCore\.GameEngine\./import TransmuteCore.core./g" \
    -e "s/import TransmuteCore\.Graphics\./import TransmuteCore.graphics./g" \
    -e "s/import TransmuteCore\.Input\./import TransmuteCore.input./g" \
    -e "s/import TransmuteCore\.System\.Asset\./import TransmuteCore.assets./g" \
    -e "s/import TransmuteCore\.Objects\./import TransmuteCore.ecs./g" \
    -e "s/import TransmuteCore\.States\./import TransmuteCore.state./g" \
    -e "s/import TransmuteCore\.Level\./import TransmuteCore.level./g" \
    -e "s/import TransmuteCore\.Serialization\./import TransmuteCore.data./g" \
    -e "s/import TransmuteCore\.System\.Serialization\./import TransmuteCore.data./g" \
    -e "s/import TransmuteCore\.Units\./import TransmuteCore.math./g" \
    -e "s/import TransmuteCore\.System\./import TransmuteCore.util./g" \
    {} \;

echo "✓ Imports updated"

echo "Updating CLI templates..."
CLI_GEN="packages/cli/src/main/java/games/transmute/cli/ProjectGenerator.java"
if [ -f "$CLI_GEN" ]; then
    sed -i "" \
        -e "s/TransmuteCore\.GameEngine\./TransmuteCore.core./g" \
        -e "s/TransmuteCore\.Graphics\./TransmuteCore.graphics./g" \
        -e "s/TransmuteCore\.Input\./TransmuteCore.input./g" \
        "$CLI_GEN"
    echo "✓ CLI templates updated"
fi

echo "Bumping version..."
sed -i "" "s/version = '0.1.0-ALPHA'/version = '0.2.0-ALPHA'/g" build.gradle
sed -i "" "s/version = '0.1.0-ALPHA'/version = '0.2.0-ALPHA'/g" packages/core/build.gradle 2>/dev/null || true
sed -i "" "s/0\.1\.0-ALPHA/0.2.0-ALPHA/g" packages/cli/src/main/java/games/transmute/cli/TransmuteCLI.java 2>/dev/null || true
sed -i "" "s/0\.1\.0-ALPHA/0.2.0-ALPHA/g" packages/cli/src/main/java/games/transmute/cli/ProjectConfig.java 2>/dev/null || true

echo "✓ Version bumped to 0.2.0-ALPHA"

echo ""
echo "Migration complete!"
echo "Run: ./gradlew clean build"
