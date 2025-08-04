@echo off
echo 🎮 Stardew Valley Multiplayer Setup Script
echo ==========================================
echo.

echo 📝 Creating .env configuration file...

(
echo # Server Configuration
echo SERVER_HOST=0.0.0.0
echo SERVER_PORT=8080
echo WEBSOCKET_PATH=/ws/game
echo.
echo # Database Configuration
echo MONGODB_URI=mongodb://localhost:27017
echo DATABASE_NAME=stardew_valley_db
echo.
echo # Security Configuration
echo JWT_SECRET=stardew_valley_secret_key_for_jwt_authentication
echo.
echo # Game Configuration
echo MAX_PLAYERS_PER_GAME=4
echo GAME_TICK_RATE=20
echo HEARTBEAT_INTERVAL=5
echo.
echo # Development Configuration
echo DEBUG_MODE=false
echo LOG_LEVEL=INFO
) > .env

echo ✅ .env file created successfully!
echo.

echo 🌐 Network Configuration:
echo Getting your IP address...
for /f "tokens=2 delims=:" %%i in ('ipconfig ^| findstr "IPv4"') do (
    set "ip=%%i"
    goto :found_ip
)

:found_ip
echo Your local IP address: %ip%
echo Other devices should connect to: %ip%
echo.

echo 🚀 Next Steps:
echo 1. Start the server: gradlew.bat lwjgl3:run
echo 2. Build client JAR: gradlew.bat lwjgl3:jar
echo 3. Copy JAR file to other devices
echo 4. Configure firewall to allow port 8080
echo.

echo 🔥 Windows Firewall Configuration:
echo 1. Open Windows Defender Firewall
echo 2. Click "Advanced Settings"
echo 3. Click "Inbound Rules" → "New Rule"
echo 4. Select "Port" → "TCP" → "Specific local ports: 8080"
echo 5. Allow the connection
echo.

echo Alternative: Run as Administrator and execute:
echo netsh advfirewall firewall add rule name="Stardew Valley Server" dir=in action=allow protocol=TCP localport=8080
echo.

echo 📖 For detailed instructions, see: NetworkSetupInstructions.md
echo ✨ Setup complete! Happy gaming!

pause
