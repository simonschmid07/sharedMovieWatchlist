package com.example.sharedmoviewatchlist.server.handlers;

import com.example.sharedmoviewatchlist.server.dao.UserDAO;
import com.example.sharedmoviewatchlist.shared.User;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class LoginHandler implements HttpHandler {
    private final Gson gson = new Gson();
    private final UserDAO userDAO = new UserDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            handlePost(exchange);
        } else {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> request = gson.fromJson(body, HashMap.class);

            String username = request.get("username");
            String password = request.get("password");

            if (username == null || password == null) {
                sendResponse(exchange, 400, "{\"error\":\"Username and password are required\"}");
                return;
            }

            User user = userDAO.loginUser(username, password);
            if (user != null) {
                String response = gson.toJson(Map.of("id", user.getId(), "username", user.getUsername()));
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 401, "{\"error\":\"Invalid credentials\"}");
            }
        } catch (SQLException e) {
            sendResponse(exchange, 500, "{\"error\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}