package com.example.sharedmoviewatchlist.shared;

public class Rating {
    private int id;
    private int userId;
    private int movieId;
    private int rating;

    public Rating() {
    }

    public Rating(int id, int userId, int movieId, int rating) {
        this.id = id;
        this.userId = userId;
        this.movieId = movieId;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}