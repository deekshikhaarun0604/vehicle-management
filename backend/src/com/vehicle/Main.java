package com.vehicle;

import com.vehicle.config.AppConfig;
import com.vehicle.server.AppServer;
import java.awt.Desktop;
import java.net.URI;

/**
 * Main application entry point.
 * Starts the embedded server and opens the dashboard in the default browser.
 */
public class Main {
    public static void main(String[] args) {
        int port = AppConfig.getInt("server.port", 8080);
        AppServer server = new AppServer(port);

        try {
            server.start();

            // Try to open default browser
            String url = "http://localhost:" + port;
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception ignored) {}
            }

            System.out.println("\nPress Ctrl+C to terminate the server.\n");
        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
