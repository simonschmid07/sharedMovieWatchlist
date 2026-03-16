package com.example.sharedmoviewatchlist.client.service;

import com.example.sharedmoviewatchlist.shared.Movie;
import com.example.sharedmoviewatchlist.shared.Rating;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080";
    private final HttpClient httpClient;
    private final Gson gson;

    public ApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public Map<String, Object> register(String username, String password) throws IOException, InterruptedException {
        String json = gson.toJson(Map.of("username", username, "password", password));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), Map.class);
    }

    public Map<String, Object> login(String username, String password) throws IOException, InterruptedException {
        String json = gson.toJson(Map.of("username", username, "password", password));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), Map.class);
        } else {
            return null;
        }
    }

    public List<Movie> getMovies() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/movies"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("GET MOVIES STATUS: " + response.statusCode());
        System.out.println("GET MOVIES BODY: " + response.body());
        return gson.fromJson(response.body(), new TypeToken<List<Movie>>() {}.getType());
    }

    public Movie addMovie(Movie movie) throws IOException, InterruptedException {
        String json = gson.toJson(movie);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/movies"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), Movie.class);
    }

    public Movie updateMovie(Movie movie) throws IOException, InterruptedException {
        String json = gson.toJson(movie);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/movies/" + movie.getId()))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), Movie.class);
    }

    public void deleteMovie(int movieId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/movies/" + movieId))
                .DELETE()
                .build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public Rating addRating(Rating rating) throws IOException, InterruptedException {
        String json = gson.toJson(rating);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/ratings"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(response.body(), Rating.class);
    }
}