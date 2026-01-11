# TransmuteCore Deployment Guide

Guide for building, packaging, and distributing your TransmuteCore games.

## Table of Contents

- [Building Executables](#building-executables)
- [Creating JAR Files](#creating-jar-files)
- [Platform-Specific Packaging](#platform-specific-packaging)
- [Distribution](#distribution)
- [Optimization](#optimization)

---

## Building Executables

### Fat JAR (Uber JAR)

Create a single JAR with all dependencies included.

**build.gradle:**
```gradle
plugins {
    id 'java'
    id 'application'
    id 'com.github.johnrengelman.shadow' version '7.1.2'
}

application {
    mainClass = 'com.yourgame.Main'
}

shadowJar {
    archiveBaseName.set('your-game')
    archiveVersion.set('1.0.0')
    archiveClassifier.set('')
}
```

**Build:**
```bash
./gradlew shadowJar

# Output: build/libs/your-game-1.0.0.jar
```

**Run:**
```bash
java -jar build/libs/your-game-1.0.0.jar
```

### Standard JAR with Dependencies

**build.gradle:**
```gradle
jar {
    manifest {
        attributes(
            'Main-Class': 'com.yourgame.Main',
            'Class-Path': configurations.runtimeClasspath.files.collect { it.name }.join(' ')
        )
    }
}

task copyDependencies(type: Copy) {
    from configurations.runtimeClasspath
    into 'build/libs/lib'
}

build.dependsOn copyDependencies
```

**Build:**
```bash
./gradlew build

# Output structure:
# build/libs/
#   your-game.jar
#   lib/
#     transmute-core-0.1.0-ALPHA.jar
#     (other dependencies)
```

---

## Creating JAR Files

### Include Resources

Ensure resources are packaged:

```gradle
sourceSets {
    main {
        resources {
            srcDirs = ['src/main/resources']
        }
    }
}
```

**Directory structure:**
```
src/main/
├── java/
│   └── com/yourgame/
│       └── Main.java
└── resources/
    ├── fonts/
    │   └── font.png
    ├── images/
    │   └── player.png
    └── audio/
        └── music.wav
```

### Manifest Configuration

```gradle
jar {
    manifest {
        attributes(
            'Main-Class': 'com.yourgame.Main',
            'Implementation-Title': 'Your Game',
            'Implementation-Version': version,
            'Built-By': System.getProperty('user.name'),
            'Built-Date': new Date().format('yyyy-MM-dd HH:mm:ss')
        )
    }
}
```

### Testing the JAR

```bash
# Build
./gradlew clean jar

# Test
java -jar build/libs/your-game.jar

# Check contents
jar tf build/libs/your-game.jar
```

---

## Platform-Specific Packaging

### Windows (.exe)

Use **Launch4j** or **jpackage** to create Windows executables.

#### Using jpackage (Java 14+)

```bash
jpackage \
  --input build/libs \
  --name "Your Game" \
  --main-jar your-game.jar \
  --main-class com.yourgame.Main \
  --type exe \
  --icon icon.ico \
  --win-console \
  --dest dist/windows
```

#### Using Launch4j

**launch4j-config.xml:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<launch4jConfig>
  <headerType>gui</headerType>
  <outfile>dist/YourGame.exe</outfile>
  <jar>your-game.jar</jar>
  <icon>icon.ico</icon>
  <jre>
    <minVersion>17</minVersion>
  </jre>
</launch4jConfig>
```

```bash
launch4jc launch4j-config.xml
```

### macOS (.app)

Create a macOS application bundle.

**Structure:**
```
YourGame.app/
├── Contents/
│   ├── Info.plist
│   ├── MacOS/
│   │   └── launcher.sh
│   ├── Resources/
│   │   ├── icon.icns
│   │   └── your-game.jar
│   └── Java/
│       └── (JRE if bundling)
```

**Info.plist:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN">
<plist version="1.0">
<dict>
    <key>CFBundleName</key>
    <string>Your Game</string>
    <key>CFBundleExecutable</key>
    <string>launcher.sh</string>
    <key>CFBundleIconFile</key>
    <string>icon.icns</string>
    <key>CFBundleVersion</key>
    <string>1.0.0</string>
</dict>
</plist>
```

**launcher.sh:**
```bash
#!/bin/bash
cd "$(dirname "$0")/../Resources"
java -jar your-game.jar
```

#### Using jpackage

```bash
jpackage \
  --input build/libs \
  --name "Your Game" \
  --main-jar your-game.jar \
  --type dmg \
  --icon icon.icns \
  --dest dist/macos
```

### Linux

Create a .deb package or AppImage.

#### Using jpackage (Debian)

```bash
jpackage \
  --input build/libs \
  --name "your-game" \
  --main-jar your-game.jar \
  --type deb \
  --icon icon.png \
  --linux-shortcut \
  --dest dist/linux
```

#### Standalone Script

**run-game.sh:**
```bash
#!/bin/bash
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
java -jar "$DIR/your-game.jar"
```

---

## Distribution

### Itch.io

Package for itch.io distribution:

**Windows:**
```
your-game-windows/
├── your-game.exe
├── your-game.jar
└── README.txt
```

**macOS:**
```
your-game-macos/
├── YourGame.app/
└── README.txt
```

**Linux:**
```
your-game-linux/
├── your-game.jar
├── run-game.sh
└── README.txt
```

**Upload:**
```bash
# Install butler
brew install butler  # macOS
# or download from itch.io

# Upload
butler push your-game-windows username/game:windows
butler push your-game-macos username/game:osx
butler push your-game-linux username/game:linux
```

### Steam

Requirements:
- Steamworks SDK integration
- Steam Depot setup
- Store page setup

**Build script:**
```bash
#!/bin/bash
# build-steam.sh

# Build game
./gradlew shadowJar

# Copy to Steam depot
cp build/libs/your-game.jar steam/depot/

# Upload with SteamCMD
steamcmd +login $STEAM_USER +run_app_build steam_build_config.vdf +quit
```

### GitHub Releases

```bash
# Tag version
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0

# Create release builds
./gradlew shadowJar

# Upload to GitHub Releases
# Use GitHub CLI or web interface
gh release create v1.0.0 build/libs/your-game-1.0.0.jar
```

---

## Optimization

### JAR Compression

```gradle
jar {
    compress = true
    preserveFileTimestamps = false
    reproducibleFileOrder = true
}
```

### ProGuard (Obfuscation & Optimization)

**build.gradle:**
```gradle
buildscript {
    dependencies {
        classpath 'com.guardsquare:proguard-gradle:7.2.0'
    }
}

task proguard(type: proguard.gradle.ProGuardTask) {
    injars 'build/libs/your-game.jar'
    outjars 'build/libs/your-game-optimized.jar'
    
    libraryjars "${System.getProperty('java.home')}/jmods"
    
    keep 'public class com.yourgame.Main { public static void main(java.lang.String[]); }'
    
    dontobfuscate  // Optional: disable obfuscation
    optimizationpasses 3
}
```

### Asset Optimization

**Images:**
```bash
# Optimize PNGs
pngquant --quality=65-80 input.png -o output.png

# Batch optimize
find res/images -name "*.png" -exec pngquant --quality=65-80 --force --ext .png {} \;
```

**Audio:**
```bash
# Convert to optimal format
ffmpeg -i input.wav -ar 22050 -ac 1 output.wav
```

### JVM Options

**Optimized launch script:**
```bash
#!/bin/bash
java -Xms256M -Xmx512M \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=10 \
     -jar your-game.jar
```

---

## Build Checklist

Before release:

- [ ] Update version numbers
- [ ] Test on all target platforms
- [ ] Include README and LICENSE
- [ ] Optimize assets
- [ ] Create platform-specific packages
- [ ] Test from clean install
- [ ] Document system requirements
- [ ] Create installer/uninstaller
- [ ] Sign executables (Windows/macOS)
- [ ] Verify all resources are included

---

## System Requirements Documentation

**README.txt:**
```
Your Game v1.0.0
================

SYSTEM REQUIREMENTS:
- Operating System: Windows 10/11, macOS 10.14+, or Linux
- Java Runtime: Version 17 or higher
- RAM: 512 MB minimum, 1 GB recommended
- Storage: 100 MB free space
- Display: 1280x720 minimum resolution

INSTALLATION:
Windows: Run YourGame.exe
macOS: Open YourGame.app
Linux: Run ./run-game.sh

CONTROLS:
Arrow Keys - Move
Space - Jump
ESC - Pause

For support, visit: https://yourgame.com/support
```

---

## Automated Build Pipeline

**GitHub Actions workflow:**

**.github/workflows/build.yml:**
```yaml
name: Build and Release

on:
  push:
    tags:
      - 'v*'

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up JDK 17
      uses: actions/setup-java@v2
      with:
        java-version: '17'
        distribution: 'adopt'
    
    - name: Build with Gradle
      run: ./gradlew shadowJar
    
    - name: Create Release
      uses: softprops/action-gh-release@v1
      with:
        files: build/libs/*.jar
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

---

## See Also

- [TROUBLESHOOTING.md](TROUBLESHOOTING.md) - Platform-specific issues
- [README.md](../README.md) - Project overview
- [Gradle Documentation](https://docs.gradle.org/)
- [jpackage Guide](https://docs.oracle.com/en/java/javase/17/jpackage/)
