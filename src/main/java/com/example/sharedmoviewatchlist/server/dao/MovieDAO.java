package com.example.sharedmoviewatchlist.server.dao;

import com.example.sharedmoviewatchlist.server.database.DatabaseConnection;
import com.example.sharedmoviewatchlist.shared.Movie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    public List<Movie> getAllMovies() throws SQLException {
        List<Movie> movies = new ArrayList<>();

        String sql = """
                SELECT m.id,
                       m.title,
                       m.genre,
                       m.releaseYear,
                       m.watched,
                       m.addedByUserId,
                       AVG(r.rating) AS averageRating
                FROM movies m
                LEFT JOIN ratings r ON m.id = r.movieId
                GROUP BY m.id, m.title, m.genre, m.releaseYear, m.watched, m.addedByUserId
                ORDER BY m.id
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Movie movie = new Movie(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getInt("releaseYear"),
                        rs.getBoolean("watched"),
                        rs.getInt("addedByUserId")
                );

                double avg = rs.getDouble("averageRating");
                if (rs.wasNull()) {
                    avg = 0.0;
                }

                movie.setAverageRating(avg);
                movies.add(movie);
            }
        }

        return movies;
    }

    public Movie addMovie(Movie movie) throws SQLException {
        String sql = "INSERT INTO movies (title, genre, releaseYear, watched, addedByUserId) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, movie.getTitle());
            pstmt.setString(2, movie.getGenre());
            pstmt.setInt(3, movie.getReleaseYear());
            pstmt.setBoolean(4, movie.isWatched());
            pstmt.setInt(5, movie.getAddedByUserId());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                movie.setId(rs.getInt(1));
                return movie;
            }
        }

        return null;
    }

    public boolean updateMovie(Movie movie) throws SQLException {
        String sql = "UPDATE movies SET title = ?, genre = ?, releaseYear = ?, watched = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, movie.getTitle());
            pstmt.setString(2, movie.getGenre());
            pstmt.setInt(3, movie.getReleaseYear());
            pstmt.setBoolean(4, movie.isWatched());
            pstmt.setInt(5, movie.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteMovie(int movieId) throws SQLException {
        String deleteRatingsSql = "DELETE FROM ratings WHERE movieId = ?";
        String deleteMovieSql = "DELETE FROM movies WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement deleteRatingsStmt = conn.prepareStatement(deleteRatingsSql)) {
                deleteRatingsStmt.setInt(1, movieId);
                deleteRatingsStmt.executeUpdate();
            }

            try (PreparedStatement deleteMovieStmt = conn.prepareStatement(deleteMovieSql)) {
                deleteMovieStmt.setInt(1, movieId);
                return deleteMovieStmt.executeUpdate() > 0;
            }
        }
    }
}