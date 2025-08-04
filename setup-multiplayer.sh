#!/bin/bash

echo "🎮 Stardew Valley Multiplayer Setup Script"
echo "=========================================="
echo

# Function to detect OS
detect_os() {
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
        echo "linux"
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        echo "mac"
    elif [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "cygwin" ]]; then
        echo "windows"
    else
        echo "unknown"
    fi
}

# Function to get local IP address
get_local_ip() {
    local os=$(detect_os)
    case $os in
        "linux"|"mac")
            local ip=$(ifconfig | grep -E "inet ([0-9]{1,3}\.){3}[0-9]{1,3}" | grep -v 127.0.0.1 | awk '{print $2}' | head -1)
            if [[ $ip == addr:* ]]; then
                ip=${ip#addr:}
            fi
            echo $ip
            ;;
        "windows")
            ipconfig | findstr "IPv4" | head -1 | cut -d: -f2 | tr -d ' '
            ;;
        *)
            echo "Unable to detect IP automatically"
            ;;
    esac
}

# Create .env file
create_env_file() {
    echo "📝 Creating .env configuration file..."

    cat > .env << EOF
# Server Configuration
SERVER_HOST=0.0.0.0
SERVER_PORT=8080
WEBSOCKET_PATH=/ws/game

# Database Configuration
MONGODB_URI=mongodb://localhost:27017
DATABASE_NAME=stardew_valley_db

# Security Configuration
JWT_SECRET=stardew_valley_secret_key_for_jwt_authentication

# Game Configuration
MAX_PLAYERS_PER_GAME=4
GAME_TICK_RATE=20
HEARTBEAT_INTERVAL=5

# Development Configuration
DEBUG_MODE=false
LOG_LEVEL=INFO
EOF

    echo "✅ .env file created successfully!"
}

# Main setup function
main() {
    echo "Setting up multiplayer configuration..."
    echo

    # Create .env file
    create_env_file
    echo

    # Get and display local IP
    echo "🌐 Network Configuration:"
    local ip=$(get_local_ip)
    if [ ! -z "$ip" ]; then
        echo "Your local IP address: $ip"
        echo "Other devices should connect to: $ip"
    else
        echo "Could not auto-detect IP address."
        echo "Run 'ifconfig' (Mac/Linux) or 'ipconfig' (Windows) to find your IP"
    fi
    echo

    # Display next steps
    echo "🚀 Next Steps:"
    echo "1. Start the server: ./gradlew lwjgl3:run"
    echo "2. Build client JAR: ./gradlew lwjgl3:jar"
    echo "3. Copy JAR file to other devices"
    echo "4. Configure firewall to allow port 8080"
    echo

    # Platform-specific firewall instructions
    local os=$(detect_os)
    echo "🔥 Firewall Configuration:"
    case $os in
        "linux")
            echo "Run: sudo ufw allow 8080"
            ;;
        "mac")
            echo "System Preferences → Security & Privacy → Firewall → Options"
            echo "Add port 8080 to allowed connections"
            ;;
        "windows")
            echo "Windows Defender Firewall → Advanced Settings"
            echo "Create inbound rule for port 8080"
            ;;
    esac
    echo

    echo "📖 For detailed instructions, see: NetworkSetupInstructions.md"
    echo "✨ Setup complete! Happy gaming!"
}

# Run main function
main
