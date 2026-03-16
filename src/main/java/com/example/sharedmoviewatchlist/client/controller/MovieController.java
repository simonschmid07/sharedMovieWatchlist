package com.example.sharedmoviewatchlist.client.controller;

import com.example.sharedmoviewatchlist.client.service.ApiClient;
import com.example.sharedmoviewatchlist.client.service.Session;
import com.example.sharedmoviewatchlist.shared.Movie;
import com.example.sharedmoviewatchlist.shared.Rating;
import javafx.scene.control.Alert;

import java.util.List;

public class MovieController {
    private final ApiClient apiClient;

    public MovieController(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public List<Movie> loadMovies() {
        try {
            return apiClient.getMovies();
        } catch (Exception e) {
            showError("Error", "Failed to load movies: " + e.getMessage());
            return List.of();
        }
    }

    public Movie addMovie(String title, String genre, int releaseYear ) {
        try {
            Movie movie = new Movie();
            movie.setTitle(title);
            movie.setGenre(genre);
            movie.setReleaseYear(releaseYear);
            movie.setWatched(false);
            movie.setAddedByUserId(Session.getInstance().getUserId());
            return apiClient.addMovie(movie);
        } catch (Exception e) {
            showError("Error", "Failed to add movie: " + e.getMessage());
            return null;
        }
    }

    public Movie updateMovie(Movie movie) {
        try {
            return apiClient.updateMovie(movie);
        } catch (Exception e) {
            showError("Error", "Failed to update movie: " + e.getMessage());
            return null;
        }
    }

    public boolean deleteMovie(int movieId) {
        try {
            apiClient.deleteMovie(movieId);
            return true;
        } catch (Exception e) {
            showError("Error", "Failed to delete movie: " + e.getMessage());
            return false;
        }
    }

    public boolean addRating(int movieId, int ratingValue) {
        try {
            Rating rating = new Rating();
            rating.setUserId(Session.getInstance().getUserId());
            rating.setMovieId(movieId);
            rating.setRating(ratingValue);
            apiClient.addRating(rating);
            return true;
        } catch (Exception e) {
            showError("Error", "Failed to add rating: " + e.getMessage());
            return false;
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}