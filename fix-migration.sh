#!/bin/bash
# Fix remaining migration issues

set -e

BASE_DIR="packages/core/TransmuteCore/src/TransmuteCore"

echo "Moving RenderPipeline..."
git mv "$BASE_DIR/GameEngine/RenderPipeline.java" "$BASE_DIR/graphics/"
sed -i "" "s/package TransmuteCore\.GameEngine/package TransmuteCore.graphics/g" "$BASE_DIR/graphics/RenderPipeline.java"
echo "✓ RenderPipeline moved"

echo "Fixing remaining imports..."

# Fix Type -> types imports
find "$BASE_DIR" -name "*.java" -type f -exec sed -i "" \
    -e "s/import TransmuteCore\.ecs\.Type\./import TransmuteCore.ecs.types./g" \
    -e "s/import TransmuteCore\.assets\.Type\./import TransmuteCore.assets.types./g" \
    {} \;

# Fix Exceptions -> exceptions imports 
find "$BASE_DIR" -name "*.java" -type f -exec sed -i "" \
    -e "s/import TransmuteCore\.util\.Exceptions\./import TransmuteCore.util.exceptions./g" \
    {} \;

# Fix Debug -> debug imports
find "$BASE_DIR" -name "*.java" -type f -exec sed -i "" \
    -e "s/import TransmuteCore\.util\.Debug\./import TransmuteCore.util.debug./g" \
    {} \;

# Fix HotReload -> hotreload imports
find "$BASE_DIR" -name "*.java" -type f -exec sed -i "" \
    -e "s/import TransmuteCore\.util\.HotReload\./import TransmuteCore.util.hotreload./g" \
    {} \;

# Fix Sprites -> sprites imports
find "$BASE_DIR" -name "*.java" -type f -exec sed -i "" \
    -e "s/import TransmuteCore\.graphics\.Sprites\./import TransmuteCore.graphics.sprites./g" \
    {} \;

# Fix Pathfinding -> pathfinding imports
find "$BASE_DIR" -name "*.java" -type f -exec sed -i "" \
    -e "s/import TransmuteCore\.ecs\.Pathfinding\./import TransmuteCore.ecs.pathfinding./g" \
    {} \;

# Fix internal data package references
find "$BASE_DIR/data" -name "*.java" -type f -exec sed -i "" \
    -e "s/TransmuteCore\.Serialization\./TransmuteCore.data./g" \
    {} \;

echo "✓ Imports fixed"

echo "Cleaning up old GameEngine directory..."
rmdir "$BASE_DIR/GameEngine" 2>/dev/null || true

echo "✓ Migration fixed!"
