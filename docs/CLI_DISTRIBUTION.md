# CLI Distribution Guide

This document describes how TransmuteCore CLI is distributed and released.

## Distribution Architecture

### Core Engine via JitPack

TransmuteCore engine is distributed through [JitPack](https://jitpack.io), which builds artifacts directly from GitHub releases:

- **No manual publishing required** - JitPack automatically builds from Git tags
- **No authentication needed** - Users can download without GitHub tokens
- **On-demand builds** - Artifacts are built when first requested and then cached
- **Dependency format**: `com.github.transmute-games.transmute-core:transmute-core:VERSION`

### CLI via GitHub Releases

The CLI tool is distributed as pre-built binaries through GitHub Releases:

- **Automated builds** - GitHub Actions builds and publishes on tag push
- **One-line installers** - Curl/PowerShell scripts for quick installation
- **Manual downloads** - Direct download from Releases page
- **Cross-platform** - Supports Unix, macOS, and Windows

## Release Process

### Releasing the Core Engine

The core engine is released via Git tags. JitPack automatically detects and builds them.

1. **Update version** in `build.gradle`:
   ```gradle
   version = '1.0.0'
   ```

2. **Commit changes**:
   ```bash
   git add build.gradle
   git commit -m "Release v1.0.0"
   ```

3. **Create and push tag**:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

4. **Verify on JitPack**:
   - Visit: `https://jitpack.io/#transmute-games/transmute-core`
   - Click "Look up" to trigger the build
   - Verify build status (should show green ✓)

5. **Test the release**:
   ```bash
   # Create a test project with the new version
   # Gradle will download from JitPack
   ./gradlew build
   ```

### Releasing the CLI

The CLI is released separately with its own versioning scheme (`cli-v*`).

1. **Ensure core engine is released first** - The CLI embeds the core version in generated project templates

2. **Update CLI version** in `packages/cli/build.gradle`:
   ```gradle
   version = '0.1.0-ALPHA'
   ```

3. **Update embedded core version** in `TemplateUtils.java` if needed:
   ```java
   // Line 45
   implementation 'com.github.transmute-games.transmute-core:transmute-core:v1.0.0'
   ```

4. **Test the CLI locally**:
   ```bash
   ./gradlew :transmute-cli:fatJar
   java -jar packages/cli/build/libs/transmute-cli-*-all.jar new test-project
   cd test-project
   ./gradlew build
   ./gradlew run
   ```

5. **Commit changes**:
   ```bash
   git add packages/cli/
   git commit -m "Release CLI v0.1.0-ALPHA"
   ```

6. **Create and push CLI tag**:
   ```bash
   git tag cli-v0.1.0-ALPHA
   git push origin cli-v0.1.0-ALPHA
   ```

7. **GitHub Actions automatically**:
   - Builds the fat JAR
   - Creates a GitHub Release
   - Attaches assets (JAR + wrapper scripts)
   - Generates release notes

8. **Verify the release**:
   - Check [GitHub Releases](https://github.com/transmute-games/transmute-core/releases)
   - Verify assets are attached
   - Test the installer scripts

9. **Test end-to-end installation**:
   ```bash
   # Unix/macOS
   curl -fsSL https://raw.githubusercontent.com/transmute-games/transmute-core/master/scripts/install-cli.sh | sh

   # Verify
   transmute new test-game
   cd test-game
   ./gradlew run
   ```

## Version Numbering

### Core Engine Versions

Format: `vMAJOR.MINOR.PATCH-STAGE`

Examples:
- `v0.1.0-ALPHA` - Alpha releases
- `v0.2.0-BETA` - Beta releases
- `v1.0.0` - Stable release

### CLI Versions

Format: `cli-vMAJOR.MINOR.PATCH-STAGE`

Examples:
- `cli-v0.1.0-ALPHA` - Alpha CLI release
- `cli-v1.0.0` - Stable CLI release

**Note**: CLI and core versions are independent. CLI v0.1.0 might generate projects that use core v0.2.0.

## GitHub Actions Workflows

### CLI Release Workflow

Location: `.github/workflows/release-cli.yml`

**Trigger**: Push of tag matching `cli-v*`

**Steps**:
1. Checkout repository
2. Set up JDK 17
3. Build fat JAR via Gradle
4. Extract version from tag
5. Prepare release assets (JAR + scripts)
6. Create GitHub Release with auto-generated notes

**Secrets required**: None (uses `GITHUB_TOKEN` automatically)

## Install Scripts

### Unix/macOS Installer

Location: `scripts/install-cli.sh`

**Features**:
- Checks Java version (requires 17+)
- Downloads latest release from GitHub
- Installs to `~/.local/bin`
- Guides PATH setup if needed
- Supports version pinning via `TRANSMUTE_CLI_VERSION` env var

**Usage**:
```bash
# Latest version
curl -fsSL https://raw.githubusercontent.com/transmute-games/transmute-core/master/scripts/install-cli.sh | sh

# Specific version
TRANSMUTE_CLI_VERSION=0.1.0-ALPHA curl -fsSL https://raw.githubusercontent.com/transmute-games/transmute-core/master/scripts/install-cli.sh | sh
```

### Windows PowerShell Installer

Location: `scripts/install-cli.ps1`

**Features**:
- Checks Java version (requires 17+)
- Downloads latest release from GitHub
- Installs to `%USERPROFILE%\bin`
- Automatically adds to PATH
- Supports version pinning via `TRANSMUTE_CLI_VERSION` env var

**Usage**:
```powershell
# Latest version
irm https://raw.githubusercontent.com/transmute-games/transmute-core/master/scripts/install-cli.ps1 | iex

# Specific version
$env:TRANSMUTE_CLI_VERSION="0.1.0-ALPHA"; irm https://raw.githubusercontent.com/transmute-games/transmute-core/master/scripts/install-cli.ps1 | iex
```

## Troubleshooting Releases

### CLI Release Fails

Check GitHub Actions logs:
1. Go to Actions tab in GitHub
2. Select the failed workflow run
3. Check build logs for errors

Common issues:
- **Gradle build fails**: Fix code errors and push again
- **Assets not found**: Verify JAR path in workflow matches actual build output

### JitPack Build Fails

Visit: `https://jitpack.io/#transmute-games/transmute-core/VERSION`

Common issues:
- **Build timeout**: Large projects may timeout on first build (rare)
- **Gradle errors**: Fix in source and create new tag
- **Missing publishing config**: Verify `maven-publish` plugin is applied

### Users Report Download Issues

1. **Check GitHub Release exists**: Verify tag was pushed and workflow ran
2. **Check JitPack status**: Visit JitPack badge page
3. **Test install scripts**: Run installer manually to reproduce issue
4. **Check network**: Ensure GitHub/JitPack are accessible

## Rollback Procedure

### Rollback Core Engine

You cannot delete JitPack builds, but you can:

1. Create a new patch release fixing the issue
2. Update documentation to skip the broken version
3. Tag the new version and notify users

### Rollback CLI

GitHub Releases can be deleted/hidden:

1. Go to Releases page
2. Edit the release
3. Mark as "Draft" or delete entirely
4. Create new fixed release with same or incremented version

## Best Practices

1. **Always test before releasing** - Build and run generated projects locally
2. **Version sync** - Document which CLI versions work with which core versions
3. **Clear release notes** - Explain breaking changes and migration steps
4. **Announce releases** - Post in Discussions or social media
5. **Keep installers updated** - Test install scripts after each CLI release

## Future Distribution Plans

### Short-term
- Continue with JitPack + GitHub Releases
- Monitor usage and feedback

### Long-term (v1.0+)
- Migrate to Maven Central for better discoverability
- Create Homebrew tap for macOS/Linux
- Create Chocolatey package for Windows
- Consider standalone CLI repository if it grows significantly

## Support

For questions about releases:
- **Issues**: [GitHub Issues](https://github.com/transmute-games/transmute-core/issues)
- **Discussions**: [GitHub Discussions](https://github.com/transmute-games/transmute-core/discussions)
