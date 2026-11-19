package com.vehicle.server;

import com.sun.net.httpserver.HttpServer;
import com.vehicle.config.AppConfig;
import com.vehicle.config.DBConnection;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Embedded HTTP Application Server for the Vehicle Service System.
 */
public class AppServer {
    private HttpServer server;
    private final int port;

    public AppServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api", new ApiHandler());
        server.createContext("/", new StaticFileHandler());
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();

        System.out.println("=====================================================================");
        System.out.println(" Vehicle Service Management and Maintenance Analysis System");
        System.out.println("=====================================================================");
        System.out.println(" Embedded Server started successfully on port: " + port);
        System.out.println(" Web Dashboard URL: http://localhost:" + port);
        System.out.println(" REST API Base URL: http://localhost:" + port + "/api");
        System.out.println(" Database Mode:    " + (DBConnection.isMySqlAvailable() ? "MySQL (Live JDBC)" : "In-Memory Pre-Seeded Store"));
        System.out.println("=====================================================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(1);
            System.out.println("[INFO] Server stopped.");
        }
    }

    public static void main(String[] args) {
        int port = AppConfig.getInt("server.port", 8080);
        AppServer app = new AppServer(port);
        try {
            app.start();
        } catch (IOException e) {
            System.err.println("Fatal: Failed to start AppServer on port " + port + ": " + e.getMessage());
        }
    }
}
