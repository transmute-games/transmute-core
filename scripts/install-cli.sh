#!/bin/sh
# Transmute CLI Installer for Unix/macOS
# Usage: curl -fsSL https://raw.githubusercontent.com/transmute-games/transmute-core/master/scripts/install-cli.sh | sh

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
REPO="transmute-games/transmute-core"
INSTALL_DIR="$HOME/.local/bin"
VERSION="${TRANSMUTE_CLI_VERSION:-latest}"

# Functions
print_error() {
    printf "${RED}Error: %s${NC}\n" "$1" >&2
}

print_success() {
    printf "${GREEN}✓ %s${NC}\n" "$1"
}

print_info() {
    printf "${YELLOW}→ %s${NC}\n" "$1"
}

check_dependencies() {
    if ! command -v java >/dev/null 2>&1; then
        print_error "Java is not installed or not in PATH"
        echo "Please install Java 17 or higher from https://adoptium.net/"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        print_error "Java 17 or higher is required (found Java $JAVA_VERSION)"
        exit 1
    fi
    
    if ! command -v curl >/dev/null 2>&1; then
        print_error "curl is required but not installed"
        exit 1
    fi
}

get_latest_release() {
    curl -s "https://api.github.com/repos/$REPO/releases" | \
        grep '"tag_name":' | \
        grep 'cli-v' | \
        head -n 1 | \
        sed -E 's/.*"cli-v([^"]+)".*/\1/'
}

download_file() {
    local url="$1"
    local output="$2"
    
    if ! curl -fsSL "$url" -o "$output"; then
        print_error "Failed to download $url"
        return 1
    fi
    return 0
}

main() {
    echo "Transmute CLI Installer"
    echo "======================="
    echo ""
    
    # Check dependencies
    print_info "Checking dependencies..."
    check_dependencies
    print_success "Java $(java -version 2>&1 | awk -F '"' '/version/ {print $2}') detected"
    
    # Determine version to install
    if [ "$VERSION" = "latest" ]; then
        print_info "Fetching latest CLI release..."
        VERSION=$(get_latest_release)
        if [ -z "$VERSION" ]; then
            print_error "Could not determine latest version"
            exit 1
        fi
    fi
    
    print_info "Installing Transmute CLI v$VERSION..."
    
    # Create install directory
    mkdir -p "$INSTALL_DIR"
    
    # Download URLs
    BASE_URL="https://github.com/$REPO/releases/download/cli-v$VERSION"
    JAR_URL="$BASE_URL/transmute-cli.jar"
    SCRIPT_URL="$BASE_URL/transmute"
    
    # Download JAR
    print_info "Downloading CLI JAR..."
    if ! download_file "$JAR_URL" "$INSTALL_DIR/transmute-cli.jar"; then
        print_error "Failed to download CLI JAR. Version v$VERSION may not exist."
        echo "Check available versions at: https://github.com/$REPO/releases"
        exit 1
    fi
    
    # Download wrapper script
    print_info "Downloading wrapper script..."
    if ! download_file "$SCRIPT_URL" "$INSTALL_DIR/transmute"; then
        print_error "Failed to download wrapper script"
        exit 1
    fi
    
    # Make script executable
    chmod +x "$INSTALL_DIR/transmute"
    
    print_success "Transmute CLI v$VERSION installed to $INSTALL_DIR"
    
    # Check if install directory is in PATH
    case ":$PATH:" in
        *":$INSTALL_DIR:"*)
            print_success "Installation complete!"
            ;;
        *)
            echo ""
            print_info "Add $INSTALL_DIR to your PATH to use the 'transmute' command:"
            echo ""
            echo "  # For Bash/Zsh (add to ~/.bashrc or ~/.zshrc):"
            echo "  export PATH=\"\$PATH:$INSTALL_DIR\""
            echo ""
            echo "  # For Fish (add to ~/.config/fish/config.fish):"
            echo "  set -U fish_user_paths $INSTALL_DIR \$fish_user_paths"
            echo ""
            echo "Then restart your shell or run: source ~/.bashrc (or ~/.zshrc)"
            ;;
    esac
    
    echo ""
    echo "Usage:"
    echo "  transmute new my-game              # Create a new project"
    echo "  transmute templates                # List available templates"
    echo "  transmute help                     # Show help"
    echo ""
    echo "Documentation: https://github.com/$REPO"
}

main
