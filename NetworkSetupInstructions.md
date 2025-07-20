# Multi-Device Multiplayer Setup Guide

## 🎯 **Quick Setup for Two Devices**

### **Step 1: Prepare the Server Device (Host)**

1. **Create a `.env` file** in the project root with these settings:
```env
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
HEARTBEAT_INTERVAL=30

# Development Configuration
DEBUG_MODE=false
LOG_LEVEL=INFO
```

2. **Find your network IP address:**

   **Windows:**
   ```bash
   ipconfig
   # Look for "IPv4 Address" under your network adapter (e.g., 192.168.1.100)
   ```

   **Mac/Linux:**
   ```bash
   ifconfig
   # Look for "inet" address under your network interface (e.g., 192.168.1.100)
   ```

   **Alternative (all platforms):**
   ```bash
   # Visit https://whatismyipaddress.com for external IP
   # Or use: nslookup myip.opendns.com resolver1.opendns.com
   ```

3. **Configure Firewall:**
   - **Windows:** Allow port 8080 through Windows Defender Firewall
   - **Mac:** System Preferences → Security & Privacy → Firewall → Options → Allow port 8080
   - **Linux:** `sudo ufw allow 8080` or configure iptables

4. **Start the server:**
   ```bash
   ./gradlew lwjgl3:run
   ```

### **Step 2: Build Client for Distribution**

Create a distributable JAR for other devices:

```bash
# Build the client JAR
./gradlew lwjgl3:jar

# The JAR will be created at: lwjgl3/build/libs/lwjgl3-1.0.jar
```

### **Step 3: Set Up Client Device**

1. **Copy the JAR file** to the second device
2. **Install Java 17+** on the client device if not already installed
3. **Run the client:**
   ```bash
   java -jar lwjgl3-1.0.jar
   ```

### **Step 4: Connect and Play**

1. **On both devices**, navigate to **Multiplayer Menu**
2. **On Device 1 (Host):**
   - Server Host: `localhost` (or your IP for testing)
   - Port: `8080`  
   - Click **CONNECT** → **CREATE GAME**

3. **On Device 2 (Client):**
   - Server Host: `[Host's IP Address]` (e.g., `192.168.1.100`)
   - Port: `8080`
   - Click **CONNECT** → **JOIN GAME** (enter game ID from host)

4. **Host starts the game** - Both players can now play simultaneously!

---

## 🌐 **Network Configurations**

### **Same WiFi Network (Recommended)**
- Devices on same network (192.168.x.x or 10.0.x.x)
- Use host's local IP address
- Fastest and most stable connection

### **Different Networks (Internet)**
- Requires port forwarding on router
- Use external IP address
- Configure router to forward port 8080 to host device
- Less stable, may have latency

### **Mobile Hotspot**
- Connect both devices to same mobile hotspot
- Find hotspot's gateway IP (usually 192.168.x.1)
- Use host device's assigned IP in that network

---

## ⚡ **Performance Optimization**

### **Server Settings**
```env
# For better performance on local networks:
GAME_TICK_RATE=30
HEARTBEAT_INTERVAL=15

# For internet play:
GAME_TICK_RATE=20  
HEARTBEAT_INTERVAL=30
```

### **Network Requirements**
- **Minimum:** 1 Mbps upload/download per player
- **Recommended:** 5 Mbps for smooth gameplay
- **Latency:** < 100ms for best experience

---

## 🔧 **Troubleshooting**

### **Connection Issues**

**"Connection failed":**
- Check if server is running
- Verify IP address is correct
- Ensure port 8080 is open in firewall
- Try connecting with `localhost` first on server device

**"Authentication failed":**
- Make sure you're logged in with different usernames
- Restart both client and server
- Check server logs for details

**"Game not found":**
- Make sure host created a game first
- Use correct Game ID
- Try creating a lobby instead

### **Performance Issues**

**Lag/Choppy gameplay:**
- Lower GAME_TICK_RATE to 15
- Check network stability
- Close other network-heavy applications
- Use wired connection if possible

**Disconnections:**
- Increase HEARTBEAT_INTERVAL to 45
- Check WiFi signal strength
- Avoid switching between WiFi networks

---

## 🛠 **Advanced Setup**

### **Dedicated Server Setup**
```bash
# Run server without GUI (headless)
java -jar core.jar org.example.server.ServerMain

# With custom port
SERVER_PORT=8081 java -jar core.jar org.example.server.ServerMain
```

### **Docker Deployment**
```dockerfile
FROM openjdk:17-jre-slim
COPY lwjgl3-1.0.jar /app/game.jar  
EXPOSE 8080
CMD ["java", "-jar", "/app/game.jar"]
```

### **Cloud Deployment**
- Deploy server to AWS/Google Cloud/Azure
- Use cloud instance's public IP
- Configure security groups/firewalls
- Consider using load balancer for multiple game sessions

---

## 📱 **Platform-Specific Notes**

### **Windows**
- May need to configure Windows Defender
- Use PowerShell for network commands
- Consider Windows Subsystem for Linux (WSL)

### **macOS**  
- May need to allow app through Gatekeeper
- Use Terminal for network configuration
- System Preferences → Sharing for network settings

### **Linux**
- Install OpenJDK: `sudo apt install openjdk-17-jre`
- Configure iptables/ufw for firewall
- Use systemd for service management

---

## 🎮 **Gameplay Features Available**

✅ **Simultaneous Actions:** Move, farm, use tools at the same time  
✅ **Real-time Chat:** Communicate during gameplay  
✅ **Trading System:** Exchange items between players  
✅ **Shared World:** All changes visible to all players instantly  
✅ **Lobby System:** Create and join game sessions  
✅ **Auto-sync:** Game state synchronized every 50ms  

---

## 📞 **Getting Help**

If you encounter issues:

1. **Check server logs** for error messages
2. **Test with localhost** first to verify local setup
3. **Use network diagnostic tools:** `ping`, `telnet`, `netstat`
4. **Try different ports** if 8080 is blocked
5. **Restart networking services** if needed

**Network Test Command:**
```bash
# Test if server is reachable
telnet [server-ip] 8080

# Check if port is open
nmap -p 8080 [server-ip]
``` 