#!/bin/bash
# TransmuteCore Package Restructure Migration Script
# This script reorganizes the package structure from feature-based to domain-driven

set -e  # Exit on error

echo "======================================"
echo "TransmuteCore Package Restructure"
echo "======================================"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

BASE_DIR="packages/core/TransmuteCore/src/TransmuteCore"

# Check we're in the right directory
if [ ! -d "$BASE_DIR" ]; then
    echo -e "${RED}Error: Cannot find $BASE_DIR${NC}"
    echo "Please run this script from the repository root"
    exit 1
fi

echo -e "${YELLOW}Step 1: Creating new package directories${NC}"
mkdir -p "$BASE_DIR/core/interfaces/services"
mkdir -p "$BASE_DIR/graphics/sprites/rendering"
mkdir -p "$BASE_DIR/input"
mkdir -p "$BASE_DIR/assets/types"
mkdir -p "$BASE_DIR/ecs/pathfinding/types"
mkdir -p "$BASE_DIR/state"
mkdir -p "$BASE_DIR/level"
mkdir -p "$BASE_DIR/data"
mkdir -p "$BASE_DIR/math"
mkdir -p "$BASE_DIR/util/debug/exceptions/hotreload"
echo -e "${GREEN}✓ Directories created${NC}"

echo ""
echo -e "${YELLOW}Step 2: Moving GameEngine → core${NC}"
# Move GameEngine files to core, preserving subdirectories
git mv "$BASE_DIR/GameEngine/TransmuteCore.java" "$BASE_DIR/core/" 2>/dev/null || true
git mv "$BASE_DIR/GameEngine/GameLoop.java" "$BASE_DIR/core/" 2>/dev/null || true
git mv "$BASE_DIR/GameEngine/GameConfig.java" "$BASE_DIR/core/" 2>/dev/null || true
git mv "$BASE_DIR/GameEngine/GameContext.java" "$BASE_DIR/core/" 2>/dev/null || true
git mv "$BASE_DIR/GameEngine/GameWindow.java" "$BASE_DIR/core/" 2>/dev/null || true
git mv "$BASE_DIR/GameEngine/GameBuilder.java" "$BASE_DIR/core/" 2>/dev/null || true
git mv "$BASE_DIR/GameEngine/Manager.java" "$BASE_DIR/core/" 2>/dev/null || true
git mv "$BASE_DIR/GameEngine/FrameMetrics.java" "$BASE_DIR/core/" 2>/dev/null || true
# Move RenderPipeline to graphics later
git mv "$BASE_DIR/GameEngine/Interfaces/"*.java "$BASE_DIR/core/interfaces/" 2>/dev/null || true
git mv "$BASE_DIR/GameEngine/Interfaces/Services/"*.java "$BASE_DIR/core/interfaces/services/" 2>/dev/null || true
echo -e "${GREEN}✓ Core package migrated${NC}"

echo ""
echo -e "${YELLOW}Step 3: Moving Graphics → graphics${NC}"
# Move Graphics files
for file in "$BASE_DIR/Graphics/"*.java; do
    [ -f "$file" ] && git mv "$file" "$BASE_DIR/graphics/" 2>/dev/null || true
done
# Move Sprites subdirectory
git mv "$BASE_DIR/Graphics/Sprites/"*.java "$BASE_DIR/graphics/sprites/" 2>/dev/null || true
# Move RenderPipeline from GameEngine to graphics
git mv "$BASE_DIR/GameEngine/RenderPipeline.java" "$BASE_DIR/graphics/rendering/" 2>/dev/null || true
echo -e "${GREEN}✓ Graphics package migrated${NC}"

echo ""
echo -e "${YELLOW}Step 4: Moving Input → input${NC}"
git mv "$BASE_DIR/Input/"*.java "$BASE_DIR/input/" 2>/dev/null || true
echo -e "${GREEN}✓ Input package migrated${NC}"

echo ""
echo -e "${YELLOW}Step 5: Moving System/Asset → assets${NC}"
# Move Asset files
for file in "$BASE_DIR/System/Asset/"*.java; do
    [ -f "$file" ] && git mv "$file" "$BASE_DIR/assets/" 2>/dev/null || true
done
# Move Asset types
git mv "$BASE_DIR/System/Asset/Type/Audio/"*.java "$BASE_DIR/assets/types/" 2>/dev/null || true
git mv "$BASE_DIR/System/Asset/Type/Images/"*.java "$BASE_DIR/assets/types/" 2>/dev/null || true
git mv "$BASE_DIR/System/Asset/Type/Fonts/"*.java "$BASE_DIR/assets/types/" 2>/dev/null || true
git mv "$BASE_DIR/System/Asset/Type/"*.java "$BASE_DIR/assets/types/" 2>/dev/null || true
echo -e "${GREEN}✓ Assets package migrated${NC}"

echo ""
echo -e "${YELLOW}Step 6: Moving Objects → ecs${NC}"
for file in "$BASE_DIR/Objects/"*.java; do
    [ -f "$file" ] && git mv "$file" "$BASE_DIR/ecs/" 2>/dev/null || true
done
git mv "$BASE_DIR/Objects/Pathfinding/"*.java "$BASE_DIR/ecs/pathfinding/" 2>/dev/null || true
git mv "$BASE_DIR/Objects/Type/"*.java "$BASE_DIR/ecs/types/" 2>/dev/null || true
echo -e "${GREEN}✓ ECS package migrated${NC}"

echo ""
echo -e "${YELLOW}Step 7: Moving States → state${NC}"
git mv "$BASE_DIR/States/"*.java "$BASE_DIR/state/" 2>/dev/null || true
echo -e "${GREEN}✓ State package migrated${NC}"

echo ""
echo -e "${YELLOW}Step 8: Moving Level → level${NC}"
git mv "$BASE_DIR/Level/"*.java "$BASE_DIR/level/" 2>/dev/null || true
echo -e "${GREEN}✓ Level package migrated${NC}"

echo ""
echo -e "${YELLOW}Step 9: Merging Serialization → data${NC}"
git mv "$BASE_DIR/Serialization/"*.java "$BASE_DIR/data/" 2>/dev/null || true
git mv "$BASE_DIR/System/Serialization/"*.java "$BASE_DIR/data/" 2>/dev/null || true
echo -e "${GREEN}✓ Data package migrated${NC}"

echo ""
echo -e "${YELLOW}Step 10: Moving Units → math${NC}"
git mv "$BASE_DIR/Units/"*.java "$BASE_DIR/math/" 2>/dev/null || true
echo -e "${GREEN}✓ Math package migrated${NC}"

echo ""
echo -e "${YELLOW}Step 11: Moving System → util${NC}"
# Move System subdirectories
git mv "$BASE_DIR/System/Debug/"*.java "$BASE_DIR/util/debug/" 2>/dev/null || true
git mv "$BASE_DIR/System/Exceptions/"*.java "$BASE_DIR/util/exceptions/" 2>/dev/null || true
git mv "$BASE_DIR/System/HotReload/"*.java "$BASE_DIR/util/hotreload/" 2>/dev/null || true
# Move remaining System files
for file in "$BASE_DIR/System/"*.java; do
    [ -f "$file" ] && git mv "$file" "$BASE_DIR/util/" 2>/dev/null || true
done
echo -e "${GREEN}✓ Util package migrated${NC}"

echo ""
echo -e "${YELLOW}Step 12: Cleaning up empty directories${NC}"
# Remove old empty directories
rmdir "$BASE_DIR/GameEngine/Interfaces/Services" 2>/dev/null || true
rmdir "$BASE_DIR/GameEngine/Interfaces" 2>/dev/null || true
rmdir "$BASE_DIR/GameEngine" 2>/dev/null || true
rmdir "$BASE_DIR/Graphics/Sprites" 2>/dev/null || true
rmdir "$BASE_DIR/Graphics" 2>/dev/null || true
rmdir "$BASE_DIR/Input" 2>/dev/null || true
rmdir "$BASE_DIR/Objects/Pathfinding" 2>/dev/null || true
rmdir "$BASE_DIR/Objects/Type" 2>/dev/null || true
rmdir "$BASE_DIR/Objects" 2>/dev/null || true
rmdir "$BASE_DIR/States" 2>/dev/null || true
rmdir "$BASE_DIR/Level" 2>/dev/null || true
rmdir "$BASE_DIR/Serialization" 2>/dev/null || true
rmdir "$BASE_DIR/Units" 2>/dev/null || true
rmdir "$BASE_DIR/System/Asset/Type/Audio" 2>/dev/null || true
rmdir "$BASE_DIR/System/Asset/Type/Images" 2>/dev/null || true
rmdir "$BASE_DIR/System/Asset/Type/Fonts" 2>/dev/null || true
rmdir "$BASE_DIR/System/Asset/Type" 2>/dev/null || true
rmdir "$BASE_DIR/System/Asset" 2>/dev/null || true
rmdir "$BASE_DIR/System/Debug" 2>/dev/null || true
rmdir "$BASE_DIR/System/Exceptions" 2>/dev/null || true
rmdir "$BASE_DIR/System/HotReload" 2>/dev/null || true
rmdir "$BASE_DIR/System/Serialization" 2>/dev/null || true
rmdir "$BASE_DIR/System" 2>/dev/null || true
echo -e "${GREEN}✓ Cleanup complete${NC}"

echo ""
echo -e "${YELLOW}Step 13: Updating package declarations${NC}"

# Update package declarations in moved files
find "$BASE_DIR/core" -name "*.java" -type f -exec sed -i '' 's/package TransmuteCore\.GameEngine/package TransmuteCore.core/g' {} \;
find "$BASE_DIR/graphics" -name "*.java" -type f -exec sed -i '' 's/package TransmuteCore\.Graphics/package TransmuteCore.graphics/g' {} \;
find "$BASE_DIR/input" -name "*.java" -type f -exec sed -i '' 's/package TransmuteCore\.Input/package TransmuteCore.input/g' {} \;
find "$BASE_DIR/assets" -name "*.java" -type f -exec sed -i '' 's/package TransmuteCore\.System\.Asset/package TransmuteCore.assets/g' {} \;
find "$BASE_DIR/ecs" -name "*.java" -type f -exec sed -i '' 's/package TransmuteCore\.Objects/package TransmuteCore.ecs/g' {} \;
find "$BASE_DIR/state" -name "*.java" -type f -exec sed -i '' 's/package TransmuteCore\.States/package TransmuteCore.state/g' {} \;
find "$BASE_DIR/level" -name "*.java" -type f -exec sed -i '' 's/package TransmuteCore\.Level/package TransmuteCore.level/g' {} \;
find "$BASE_DIR/data" -name "*.java" -type f -exec sed -i '' 's/package TransmuteCore\.Serialization/package TransmuteCore.data/g' {} \;
find "$BASE_DIR/data" -name "*.java" -type f -exec sed -i '' 's/package TransmuteCore\.System\.Serialization/package TransmuteCore.data/g' {} \;
find "$BASE_DIR/math" -name "*.java" -type f -exec sed -i '' 's/package TransmuteCore\.Units/package TransmuteCore.math/g' {} \;
find "$BASE_DIR/util" -name "*.java" -type f -exec sed -i '' 's/package TransmuteCore\.System/package TransmuteCore.util/g' {} \;

# Fix subdirectory packages
find "$BASE_DIR/graphics/rendering" -name "*.java" -type f -exec sed -i '' 's/package TransmuteCore\.core/package TransmuteCore.graphics.rendering/g' {} \;

echo -e "${GREEN}✓ Package declarations updated${NC}"

echo ""
echo -e "${YELLOW}Step 14: Updating import statements${NC}"

# Update all imports across the entire codebase
find "$BASE_DIR" -name "*.java" -type f -exec sed -i '' \
    -e 's/import TransmuteCore\.GameEngine\./import TransmuteCore.core./g' \
    -e 's/import TransmuteCore\.Graphics\./import TransmuteCore.graphics./g' \
    -e 's/import TransmuteCore\.Input\./import TransmuteCore.input./g' \
    -e 's/import TransmuteCore\.System\.Asset\./import TransmuteCore.assets./g' \
    -e 's/import TransmuteCore\.Objects\./import TransmuteCore.ecs./g' \
    -e 's/import TransmuteCore\.States\./import TransmuteCore.state./g' \
    -e 's/import TransmuteCore\.Level\./import TransmuteCore.level./g' \
    -e 's/import TransmuteCore\.Serialization\./import TransmuteCore.data./g' \
    -e 's/import TransmuteCore\.System\.Serialization\./import TransmuteCore.data./g' \
    -e 's/import TransmuteCore\.Units\./import TransmuteCore.math./g' \
    -e 's/import TransmuteCore\.System\./import TransmuteCore.util./g' \
    {} \;

echo -e "${GREEN}✓ Imports updated${NC}"

echo ""
echo -e "${YELLOW}Step 15: Updating CLI templates${NC}"

# Update CLI generator templates
CLI_GEN="packages/cli/src/main/java/games/transmute/cli/ProjectGenerator.java"
if [ -f "$CLI_GEN" ]; then
    sed -i '' \
        -e 's/TransmuteCore\.GameEngine\./TransmuteCore.core./g' \
        -e 's/TransmuteCore\.Graphics\./TransmuteCore.graphics./g' \
        -e 's/TransmuteCore\.Input\./TransmuteCore.input./g' \
        "$CLI_GEN"
    echo -e "${GREEN}✓ CLI templates updated${NC}"
else
    echo -e "${YELLOW}⚠ CLI generator not found, skipping${NC}"
fi

echo ""
echo -e "${YELLOW}Step 16: Bumping version to 0.2.0-ALPHA${NC}"

# Update version in build files
sed -i '' "s/version = '0.1.0-ALPHA'/version = '0.2.0-ALPHA'/g" build.gradle
sed -i '' "s/version = '0.1.0-ALPHA'/version = '0.2.0-ALPHA'/g" packages/core/build.gradle 2>/dev/null || true
sed -i '' 's/0\.1\.0-ALPHA/0.2.0-ALPHA/g' packages/cli/src/main/java/games/transmute/cli/TransmuteCLI.java 2>/dev/null || true
sed -i '' 's/0\.1\.0-ALPHA/0.2.0-ALPHA/g' packages/cli/src/main/java/games/transmute/cli/ProjectConfig.java 2>/dev/null || true

echo -e "${GREEN}✓ Version bumped to 0.2.0-ALPHA${NC}"

echo ""
echo "======================================"
echo -e "${GREEN}Migration Complete!${NC}"
echo "======================================"
echo ""
echo "Next steps:"
echo "1. Review the changes: git status"
echo "2. Test the build: ./gradlew clean build"
echo "3. If successful, commit: git add -A && git commit -m 'Refactor: Reorganize packages to domain-driven structure'"
echo ""
echo "If there are issues, you can rollback with: git reset --hard HEAD"
