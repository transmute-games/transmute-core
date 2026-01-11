package games.transmute.cli.templates;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Utility class for common template generation tasks.
 */
public class TemplateUtils {

    /**
     * Write a string to a file, creating parent directories if needed.
     */
    public static void writeFile(Path path, String content) throws IOException {
        Files.createDirectories(path.getParent());
        Files.writeString(path, content);
    }

    /**
     * Generate build.gradle file content.
     */
    public static String generateBuildGradle(Map<String, String> vars) {
        return """
            plugins {
                id 'application'
                id 'java'
            }

            group = '%s'
            version = '%s'

            repositories {
                mavenCentral()
                maven { url 'https://jitpack.io' }
            }

            java {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }

            dependencies {
                implementation 'com.github.transmute-games.transmute-core:transmute-core:%s'
            }

            application {
                mainClass = '%s.Game'
            }

            run {
                workingDir = projectDir
            }
            """.formatted(
                vars.get("PACKAGE_NAME"),
                vars.get("GAME_VERSION"),
                vars.get("TRANSMUTE_VERSION"),
                vars.get("PACKAGE_NAME")
            );
    }

    /**
     * Generate settings.gradle file content.
     */
    public static String generateSettingsGradle(Map<String, String> vars) {
        return "rootProject.name = '" + vars.get("PROJECT_NAME") + "'\n";
    }

    /**
     * Generate README.md file content.
     */
    public static String generateREADME(Map<String, String> vars) {
        return """
            # %s

            A game built with Transmute Core Engine.

            ## Building

            ```bash
            ./gradlew build
            ```

            ## Running

            ```bash
            ./gradlew run
            ```

            ## Project Structure

            - `src/main/java` - Java source files
            - `src/main/resources` - Game assets (images, sounds, etc.)

            ## Dependencies

            This project uses Transmute Core via [JitPack](https://jitpack.io). The dependency is automatically
            configured in `build.gradle`. JitPack builds artifacts directly from GitHub releases, so no
            additional authentication or setup is required.

            ## Documentation

            For Transmute Core documentation, visit:
            https://github.com/transmute-games/transmute-core
            """.formatted(vars.get("GAME_TITLE"));
    }

    /**
     * Generate .gitignore file content.
     */
    public static String generateGitignore() {
        return """
            # Gradle
            .gradle/
            build/
            bin/

            # IDE
            .idea/
            .vscode/
            *.iml
            .classpath
            .project
            .settings/

            # OS
            .DS_Store
            Thumbs.db
            """;
    }

    /**
     * Write common project files (build.gradle, settings.gradle, README, .gitignore).
     */
    public static void writeCommonFiles(Path projectPath, Map<String, String> vars) throws IOException {
        writeFile(projectPath.resolve("build.gradle"), generateBuildGradle(vars));
        writeFile(projectPath.resolve("settings.gradle"), generateSettingsGradle(vars));
        writeFile(projectPath.resolve("README.md"), generateREADME(vars));
        writeFile(projectPath.resolve(".gitignore"), generateGitignore());
    }
}
