package com.example.sharedmoviewatchlist.server.handlers;

import com.example.sharedmoviewatchlist.server.dao.RatingDAO;
import com.example.sharedmoviewatchlist.shared.Rating;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class RatingsHandler implements HttpHandler {
    private final Gson gson = new Gson();
    private final RatingDAO ratingDAO = new RatingDAO();

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
            Rating rating = gson.fromJson(body, Rating.class);

            if (rating.getRating() < 1 || rating.getRating() > 5) {
                sendResponse(exchange, 400, "{\"error\":\"Rating must be between 1 and 5\"}");
                return;
            }

            Rating savedRating = ratingDAO.addOrUpdateRating(rating);
            if (savedRating != null) {
                String response = gson.toJson(savedRating);
                sendResponse(exchange, 201, response);
            } else {
                sendResponse(exchange, 500, "{\"error\":\"Failed to save rating\"}");
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