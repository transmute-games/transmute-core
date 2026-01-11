package games.transmute.cli;

import games.transmute.cli.templates.*;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates new Transmute Core projects from templates.
 * Uses template classes to generate specific project types.
 */
public class ProjectGenerator {

    public void generateProject(String projectName, String templateName, ProjectConfig config) throws IOException {
        Path projectPath = Paths.get(projectName);

        // Check if directory already exists
        if (Files.exists(projectPath)) {
            throw new IOException("Directory '" + projectName + "' already exists");
        }

        // Validate project name
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IOException("Project name cannot be empty");
        }

        System.out.println("Generating project '" + projectName + "' with template '" + templateName + "'...");

        // Create project directory
        Files.createDirectories(projectPath);

        // Set default config values if not set
        if (config.getGameTitle().equals("My Game")) {
            config.setGameTitle(projectName);
        }
        if (config.getPackageName().equals("com.example.game")) {
            config.setPackageName("com.example." + sanitizePackageName(projectName));
        }

        // Validate and sanitize package name
        config.setPackageName(validateAndSanitizePackageName(config.getPackageName()));

        // Create project structure
        createProjectStructure(projectPath, config);

        // Generate files from template
        generateFromTemplate(projectPath, templateName, projectName, config);

        // Create Gradle wrapper
        copyGradleWrapper(projectPath);

        System.out.println("Project structure created");
    }

    private void createProjectStructure(Path projectPath, ProjectConfig config) throws IOException {
        // Create directory structure
        String packagePath = config.getPackagePath();

        Files.createDirectories(projectPath.resolve("src/main/java").resolve(packagePath));
        Files.createDirectories(projectPath.resolve("src/main/resources"));
        Files.createDirectories(projectPath.resolve("gradle/wrapper"));
    }

    private void generateFromTemplate(Path projectPath, String templateName, String projectName, ProjectConfig config) throws IOException {
        Map<String, String> vars = new HashMap<>();
        vars.put("PROJECT_NAME", projectName);
        vars.put("GAME_TITLE", config.getGameTitle());
        vars.put("GAME_VERSION", config.getGameVersion());
        vars.put("PACKAGE_NAME", config.getPackageName());
        vars.put("TRANSMUTE_VERSION", config.getTransmuteVersion());
        vars.put("SCREEN_WIDTH", String.valueOf(config.getScreenWidth()));
        vars.put("SCREEN_HEIGHT", String.valueOf(config.getScreenHeight()));
        vars.put("SCREEN_SCALE", String.valueOf(config.getScreenScale()));

        // Get appropriate template and generate
        ProjectTemplate template = getTemplate(templateName);
        template.generate(projectPath, config, vars);
    }

    private ProjectTemplate getTemplate(String templateName) throws IOException {
        return switch (templateName) {
            case "basic" -> new BasicTemplate();
            case "platformer" -> new PlatformerTemplate();
            case "rpg" -> new RPGTemplate();
            default -> throw new IOException("Unknown template: " + templateName);
        };
    }

    private void copyGradleWrapper(Path projectPath) throws IOException {
        // Create minimal gradle wrapper files
        writeFile(projectPath.resolve("gradlew"), generateGradlewScript());
        writeFile(projectPath.resolve("gradlew.bat"), generateGradlewBat());

        // Make gradlew executable on Unix systems
        try {
            projectPath.resolve("gradlew").toFile().setExecutable(true);
        } catch (Exception e) {
            // Ignore on Windows
        }

        // Create gradle-wrapper.properties
        String wrapperProps = "distributionBase=GRADLE_USER_HOME\n" +
                              "distributionPath=wrapper/dists\n" +
                              "distributionUrl=https\\://services.gradle.org/distributions/gradle-8.5-bin.zip\n" +
                              "zipStoreBase=GRADLE_USER_HOME\n" +
                              "zipStorePath=wrapper/dists\n";
        writeFile(projectPath.resolve("gradle/wrapper/gradle-wrapper.properties"), wrapperProps);
    }

    private String generateGradlewScript() {
        return "#!/bin/sh\n" +
               "gradle \"$@\"\n";
    }

    private String generateGradlewBat() {
        return "@echo off\n" +
               "gradle %*\n";
    }

    private void writeFile(Path path, String content) throws IOException {
        Files.createDirectories(path.getParent());
        Files.writeString(path, content);
    }

    /**
     * Sanitizes a string to be used as part of a Java package name.
     * Removes all non-alphanumeric characters and converts to lowercase.
     */
    private String sanitizePackageName(String input) {
        return input.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    /**
     * Validates and sanitizes a full package name (with dots).
     * Ensures it follows Java package naming conventions.
     */
    private String validateAndSanitizePackageName(String packageName) throws IOException {
        if (packageName == null || packageName.trim().isEmpty()) {
            throw new IOException("Package name cannot be empty");
        }

        // Remove whitespace
        packageName = packageName.trim();

        // Split by dots
        String[] parts = packageName.split("\\.");
        StringBuilder sanitized = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();

            // Skip empty parts
            if (part.isEmpty()) {
                continue;
            }

            // Sanitize each part (remove non-alphanumeric, lowercase)
            part = part.toLowerCase().replaceAll("[^a-z0-9_]", "");

            // Check if part starts with a digit (invalid in Java)
            if (!part.isEmpty() && Character.isDigit(part.charAt(0))) {
                part = "_" + part;
            }

            // Skip if sanitization resulted in empty string
            if (part.isEmpty()) {
                continue;
            }

            // Java keywords that can't be used as package names
            if (isJavaKeyword(part)) {
                part = "_" + part;
            }

            if (sanitized.length() > 0) {
                sanitized.append(".");
            }
            sanitized.append(part);
        }

        String result = sanitized.toString();

        // Validate final result
        if (result.isEmpty()) {
            throw new IOException("Invalid package name: resulted in empty string after sanitization");
        }

        if (result.startsWith(".") || result.endsWith(".") || result.contains("..")) {
            throw new IOException("Invalid package name: contains empty segments");
        }

        return result;
    }

    /**
     * Checks if a string is a Java keyword.
     */
    private boolean isJavaKeyword(String word) {
        String[] keywords = {
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
            "class", "const", "continue", "default", "do", "double", "else", "enum",
            "extends", "final", "finally", "float", "for", "goto", "if", "implements",
            "import", "instanceof", "int", "interface", "long", "native", "new",
            "package", "private", "protected", "public", "return", "short", "static",
            "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while", "true", "false", "null"
        };

        for (String keyword : keywords) {
            if (keyword.equals(word)) {
                return true;
            }
        }
        return false;
    }
}
