#!/bin/bash

echo "🧪 Testing Server Connection"
echo "============================"

# Test if server is running on port 8080
echo "1. Checking if server is running on port 8080..."
if lsof -i :8080 > /dev/null 2>&1; then
    echo "✅ Server is running on port 8080"
else
    echo "❌ Server is not running on port 8080"
    exit 1
fi

# Test HTTP endpoint
echo "2. Testing HTTP endpoint..."
if curl -s http://localhost:8080/health > /dev/null; then
    echo "✅ HTTP endpoint is responding"
    curl -s http://localhost:8080/health | jq . 2>/dev/null || echo "Response: $(curl -s http://localhost:8080/health)"
else
    echo "❌ HTTP endpoint is not responding"
fi

# Test WebSocket endpoint (basic check)
echo "3. Testing WebSocket endpoint..."
if curl -s -i -N -H "Connection: Upgrade" -H "Upgrade: websocket" -H "Sec-WebSocket-Version: 13" -H "Sec-WebSocket-Key: x3JJHMbDL1EzLkh9GBhXDw==" http://localhost:8080/ws/game | grep -q "101 Switching Protocols"; then
    echo "✅ WebSocket endpoint is responding"
else
    echo "❌ WebSocket endpoint is not responding correctly"
fi

echo ""
echo "🎮 Client and Server Status:"
echo "============================"
echo "Server PID: $(lsof -ti :8080 2>/dev/null | head -1)"
echo "Client PID: $(ps aux | grep "org.example.lwjgl3.Lwjgl3Launcher" | grep -v grep | awk '{print $2}' | head -1)"

echo ""
echo "📝 Next Steps:"
echo "1. The client should now show more specific error messages instead of 'Unknown Error'"
echo "2. Try connecting to the server from the client UI"
echo "3. If connection fails, you should see a specific error message like:"
echo "   - 'Connection refused: Server is not running or not accessible at localhost:8080'"
echo "   - 'Connection timeout: Server did not respond within 5 seconds'"
echo "   - 'Unknown host: Cannot resolve server address localhost'"
echo ""
echo "✨ Test completed!" 