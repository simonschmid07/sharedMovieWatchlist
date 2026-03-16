package com.example.sharedmoviewatchlist.server;

import com.example.sharedmoviewatchlist.server.handlers.LoginHandler;
import com.example.sharedmoviewatchlist.server.handlers.MoviesHandler;
import com.example.sharedmoviewatchlist.server.handlers.RatingsHandler;
import com.example.sharedmoviewatchlist.server.handlers.RegisterHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MovieWatchlistServer {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

            server.createContext("/register", new RegisterHandler());
            server.createContext("/login", new LoginHandler());
            server.createContext("/movies", new MoviesHandler());
            server.createContext("/ratings", new RatingsHandler());

            server.setExecutor(null);
            server.start();

            System.out.println("Server started on port " + PORT);
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}