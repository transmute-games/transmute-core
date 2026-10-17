@echo off
REM Transmute CLI Wrapper for Windows
REM This script wraps the Java JAR to provide a cleaner CLI experience

REM Get the directory where this script is located
set SCRIPT_DIR=%~dp0

REM Remove trailing backslash
set SCRIPT_DIR=%SCRIPT_DIR:~0,-1%

REM Path to the JAR file (relative to script location)
set JAR_FILE=%SCRIPT_DIR%\transmute-cli.jar

REM Check if JAR exists
if not exist "%JAR_FILE%" (
    echo Error: transmute-cli.jar not found at %JAR_FILE%
    echo Please run: gradlew :transmute-cli:install
    exit /b 1
)

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 17 or higher
    exit /b 1
)

REM Execute the JAR with all arguments passed through
java -jar "%JAR_FILE%" %*
