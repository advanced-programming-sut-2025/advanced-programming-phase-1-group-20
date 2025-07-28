package org.example.utils;

import java.net.*;
import java.util.*;

public class NetworkUtils {
    public static String getLocalIPAddress() {
        try {
            // Get all network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                // Skip loopback and inactive interfaces
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                String interfaceName = networkInterface.getDisplayName().toLowerCase();
                if (interfaceName.contains("utun") || interfaceName.contains("tun") ||
                    interfaceName.contains("p2p") || interfaceName.contains("awdl")) {
                    continue;
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    // Skip loopback and IPv6 addresses
                    if (addr.isLoopbackAddress() || addr instanceof Inet6Address) {
                        continue;
                    }

                    // Skip reserved/private IP ranges that shouldn't be used for server binding
                    String hostAddress = addr.getHostAddress();
                    if (isReservedIP(hostAddress)) {
                        continue;
                    }

                    // Return the first valid IPv4 address
                    if (addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }

            return "127.0.0.1";
        } catch (Exception e) {
            System.err.println("Error getting local IP address: " + e.getMessage());
        }

        return "127.0.0.1";
    }

    private static boolean isReservedIP(String ipAddress) {
        // Check for reserved IP ranges
        if (ipAddress.startsWith("192.0.2.") ||    // TEST-NET-1
            ipAddress.startsWith("198.51.100.") || // TEST-NET-2
            ipAddress.startsWith("203.0.113.") ||  // TEST-NET-3
            ipAddress.startsWith("169.254.") ||    // Link-local
            ipAddress.startsWith("0.") ||          // Reserved
            ipAddress.startsWith("127.")) {        // Loopback
            return true;
        }
        return false;
    }

    public static List<String> getAllIPAddresses() {
        List<String> ipAddresses = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                        String ipAddress = addr.getHostAddress();
                        String interfaceName = networkInterface.getDisplayName();
                        ipAddresses.add(ipAddress + " (" + interfaceName + ")");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting all IP addresses: " + e.getMessage());
        }

        return ipAddresses;
    }


    public static boolean isServerReachable(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static String getOperatingSystem() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            return "windows";
        } else if (osName.contains("mac") || osName.contains("darwin")) {
            return "mac";
        } else if (osName.contains("linux") || osName.contains("unix")) {
            return "linux";
        } else {
            return "unknown";
        }
    }


    public static String getFirewallInstructions(int port) {
        String os = getOperatingSystem();

        switch (os) {
            case "windows":
                return "Windows Firewall:\n" +
                       "1. Open Windows Defender Firewall\n" +
                       "2. Advanced Settings → Inbound Rules → New Rule\n" +
                       "3. Port → TCP → Specific local ports: " + port + "\n" +
                       "4. Allow the connection\n\n" +
                       "Or run as Administrator:\n" +
                       "netsh advfirewall firewall add rule name=\"Game Server\" dir=in action=allow protocol=TCP localport=" + port;

            case "mac":
                return "macOS Firewall:\n" +
                       "1. System Preferences → Security & Privacy\n" +
                       "2. Firewall → Firewall Options\n" +
                       "3. Add application or allow port " + port + "\n" +
                       "4. Allow incoming connections";

            case "linux":
                return "Linux Firewall (UFW):\n" +
                       "sudo ufw allow " + port + "\n\n" +
                       "Or for iptables:\n" +
                       "sudo iptables -A INPUT -p tcp --dport " + port + " -j ACCEPT\n" +
                       "sudo iptables-save > /etc/iptables/rules.v4";

            default:
                return "Please configure your firewall to allow incoming connections on port " + port;
        }
    }


    public static void printNetworkInfo() {
        System.out.println("=== NETWORK INFORMATION ===");

        String localIP = getLocalIPAddress();
        if (localIP != null) {
            System.out.println("Primary IP Address: " + localIP);
        } else {
            System.out.println("Could not determine primary IP address");
        }

        List<String> allIPs = getAllIPAddresses();
        if (!allIPs.isEmpty()) {
            System.out.println("\nAll Available IP Addresses:");
            for (String ip : allIPs) {
                System.out.println("  " + ip);
            }
        }

        System.out.println("\nOperating System: " + getOperatingSystem());
        System.out.println("Java Version: " + System.getProperty("java.version"));

        System.out.println("============================");
    }

    public static String generateClientInstructions(int port) {
        String localIP = getLocalIPAddress();

        StringBuilder instructions = new StringBuilder();
        instructions.append("=== CLIENT CONNECTION INSTRUCTIONS ===\n");

        if (localIP != null) {
            instructions.append("For devices on the same network:\n");
            instructions.append("  Server Host: ").append(localIP).append("\n");
            instructions.append("  Port: ").append(port).append("\n\n");
        }

        instructions.append("For local testing:\n");
        instructions.append("  Server Host: localhost\n");
        instructions.append("  Port: ").append(port).append("\n\n");

        instructions.append("Make sure:\n");
        instructions.append("1. Firewall allows port ").append(port).append("\n");
        instructions.append("2. All devices are on the same network\n");
        instructions.append("3. Server is running before connecting clients\n");

        return instructions.toString();
    }
}
