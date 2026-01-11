# Transmute CLI Installer for Windows (PowerShell)
# Usage: irm https://raw.githubusercontent.com/transmute-games/transmute-core/master/scripts/install-cli.ps1 | iex

$ErrorActionPreference = "Stop"

# Configuration
$Repo = "transmute-games/transmute-core"
$InstallDir = "$env:USERPROFILE\bin"
$Version = if ($env:TRANSMUTE_CLI_VERSION) { $env:TRANSMUTE_CLI_VERSION } else { "latest" }

# Functions
function Write-ColorOutput {
    param(
        [Parameter(Mandatory=$true)]
        [string]$Message,
        [string]$Color = "White"
    )
    Write-Host $Message -ForegroundColor $Color
}

function Test-JavaInstallation {
    try {
        $javaVersion = java -version 2>&1
        if ($LASTEXITCODE -ne 0) {
            throw "Java not found"
        }
        
        $versionString = $javaVersion | Select-String -Pattern 'version "(\d+)'
        if ($versionString.Matches.Groups[1].Value -lt 17) {
            Write-ColorOutput "✗ Java 17 or higher is required" -Color Red
            Write-ColorOutput "  Please install from https://adoptium.net/" -Color Yellow
            exit 1
        }
        
        Write-ColorOutput "✓ Java detected" -Color Green
        return $true
    }
    catch {
        Write-ColorOutput "✗ Java is not installed or not in PATH" -Color Red
        Write-ColorOutput "  Please install Java 17 or higher from https://adoptium.net/" -Color Yellow
        exit 1
    }
}

function Get-LatestRelease {
    try {
        $releases = Invoke-RestMethod -Uri "https://api.github.com/repos/$Repo/releases"
        $cliRelease = $releases | Where-Object { $_.tag_name -like "cli-v*" } | Select-Object -First 1
        if ($cliRelease) {
            return $cliRelease.tag_name -replace "cli-v", ""
        }
        return $null
    }
    catch {
        Write-ColorOutput "✗ Failed to fetch releases from GitHub" -Color Red
        exit 1
    }
}

function Download-File {
    param(
        [string]$Url,
        [string]$Output
    )
    
    try {
        Write-ColorOutput "→ Downloading from $Url..." -Color Cyan
        Invoke-WebRequest -Uri $Url -OutFile $Output -UseBasicParsing
        return $true
    }
    catch {
        Write-ColorOutput "✗ Failed to download $Url" -Color Red
        Write-ColorOutput "  Error: $_" -Color Red
        return $false
    }
}

function Add-ToPath {
    param([string]$Directory)
    
    $userPath = [Environment]::GetEnvironmentVariable("Path", "User")
    if ($userPath -notlike "*$Directory*") {
        Write-ColorOutput "→ Adding $Directory to PATH..." -Color Cyan
        [Environment]::SetEnvironmentVariable(
            "Path",
            "$userPath;$Directory",
            "User"
        )
        $env:Path = "$env:Path;$Directory"
        Write-ColorOutput "✓ Added to PATH" -Color Green
        Write-ColorOutput "  Note: You may need to restart your terminal for changes to take effect" -Color Yellow
        return $true
    }
    return $false
}

# Main installation
Write-Host ""
Write-Host "Transmute CLI Installer" -ForegroundColor Cyan
Write-Host "=======================" -ForegroundColor Cyan
Write-Host ""

# Check Java
Write-ColorOutput "→ Checking dependencies..." -Color Cyan
Test-JavaInstallation

# Determine version
if ($Version -eq "latest") {
    Write-ColorOutput "→ Fetching latest CLI release..." -Color Cyan
    $Version = Get-LatestRelease
    if (-not $Version) {
        Write-ColorOutput "✗ Could not determine latest version" -Color Red
        exit 1
    }
}

Write-ColorOutput "→ Installing Transmute CLI v$Version..." -Color Cyan

# Create install directory
if (-not (Test-Path $InstallDir)) {
    New-Item -ItemType Directory -Path $InstallDir -Force | Out-Null
}

# Download URLs
$BaseUrl = "https://github.com/$Repo/releases/download/cli-v$Version"
$JarUrl = "$BaseUrl/transmute-cli.jar"
$ScriptUrl = "$BaseUrl/transmute.bat"

# Download JAR
if (-not (Download-File -Url $JarUrl -Output "$InstallDir\transmute-cli.jar")) {
    Write-ColorOutput "✗ Failed to download CLI JAR. Version v$Version may not exist." -Color Red
    Write-ColorOutput "  Check available versions at: https://github.com/$Repo/releases" -Color Yellow
    exit 1
}

# Download batch script
if (-not (Download-File -Url $ScriptUrl -Output "$InstallDir\transmute.bat")) {
    Write-ColorOutput "✗ Failed to download wrapper script" -Color Red
    exit 1
}

Write-ColorOutput "✓ Transmute CLI v$Version installed to $InstallDir" -Color Green

# Add to PATH if needed
$pathAdded = Add-ToPath -Directory $InstallDir

Write-Host ""
Write-ColorOutput "Installation complete!" -Color Green
Write-Host ""
Write-Host "Usage:"
Write-Host "  transmute new my-game              # Create a new project"
Write-Host "  transmute templates                # List available templates"
Write-Host "  transmute help                     # Show help"
Write-Host ""
Write-Host "Documentation: https://github.com/$Repo"
Write-Host ""

if ($pathAdded) {
    Write-ColorOutput "Please restart your terminal for PATH changes to take effect." -Color Yellow
}
