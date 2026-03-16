package com.example.sharedmoviewatchlist.shared;

public class Movie {
    private int id;
    private String title;
    private String genre;
    private int releaseYear;
    private boolean watched;
    private int addedByUserId;
    private double averageRating;

    public Movie() {
    }

    public Movie(int id, String title, String genre, int releaseYear, boolean watched, int addedByUserId) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.watched = watched;
        this.addedByUserId = addedByUserId;
        this.averageRating = 0.0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public int getAddedByUserId() {
        return addedByUserId;
    }

    public void setAddedByUserId(int addedByUserId) {
        this.addedByUserId = addedByUserId;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}