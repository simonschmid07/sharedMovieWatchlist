package com.example.sharedmoviewatchlist.server.handlers;

import com.example.sharedmoviewatchlist.server.dao.MovieDAO;
import com.example.sharedmoviewatchlist.shared.Movie;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

public class MoviesHandler implements HttpHandler {
    private final Gson gson = new Gson();
    private final MovieDAO movieDAO = new MovieDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            case "PUT":
                handlePut(exchange);
                break;
            case "DELETE":
                handleDelete(exchange);
                break;
            default:
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        try {
            List<Movie> movies = movieDAO.getAllMovies();
            String response = gson.toJson(movies);
            sendResponse(exchange, 200, response);
        } catch (SQLException e) {
            sendResponse(exchange, 500, "{\"error\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Movie movie = gson.fromJson(body, Movie.class);

            if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
                sendResponse(exchange, 400, "{\"error\":\"Title is required\"}");
                return;
            }

            Movie savedMovie = movieDAO.addMovie(movie);
            if (savedMovie != null) {
                String response = gson.toJson(savedMovie);
                sendResponse(exchange, 201, response);
            } else {
                sendResponse(exchange, 500, "{\"error\":\"Failed to add movie\"}");
            }
        } catch (SQLException e) {
            sendResponse(exchange, 500, "{\"error\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    private void handlePut(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length < 3) {
                sendResponse(exchange, 400, "{\"error\":\"Movie ID is required\"}");
                return;
            }

            int movieId = Integer.parseInt(parts[2]);
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Movie movie = gson.fromJson(body, Movie.class);
            movie.setId(movieId);

            boolean success = movieDAO.updateMovie(movie);
            if (success) {
                String response = gson.toJson(movie);
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Movie not found\"}");
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid movie ID\"}");
        } catch (SQLException e) {
            sendResponse(exchange, 500, "{\"error\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length < 3) {
                sendResponse(exchange, 400, "{\"error\":\"Movie ID is required\"}");
                return;
            }

            int movieId = Integer.parseInt(parts[2]);
            boolean success = movieDAO.deleteMovie(movieId);
            if (success) {
                sendResponse(exchange, 200, "{\"message\":\"Movie deleted\"}");
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Movie not found\"}");
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid movie ID\"}");
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