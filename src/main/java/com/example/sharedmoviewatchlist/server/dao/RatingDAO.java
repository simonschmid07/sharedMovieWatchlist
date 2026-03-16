package com.example.sharedmoviewatchlist.server.dao;

import com.example.sharedmoviewatchlist.server.database.DatabaseConnection;
import com.example.sharedmoviewatchlist.shared.Rating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RatingDAO {

    public Rating addOrUpdateRating(Rating rating) throws SQLException {
        String checkSql = "SELECT id FROM ratings WHERE userId = ? AND movieId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, rating.getUserId());
            checkStmt.setInt(2, rating.getMovieId());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int existingId = rs.getInt("id");
                String updateSql = "UPDATE ratings SET rating = ? WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, rating.getRating());
                    updateStmt.setInt(2, existingId);
                    updateStmt.executeUpdate();
                    rating.setId(existingId);
                    return rating;
                }
            } else {
                String insertSql = "INSERT INTO ratings (userId, movieId, rating) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    insertStmt.setInt(1, rating.getUserId());
                    insertStmt.setInt(2, rating.getMovieId());
                    insertStmt.setInt(3, rating.getRating());
                    insertStmt.executeUpdate();

                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        rating.setId(generatedKeys.getInt(1));
                        return rating;
                    }
                }
            }
        }
        return null;
    }

    public Integer getUserRating(int userId, int movieId) throws SQLException {
        String sql = "SELECT rating FROM ratings WHERE userId = ? AND movieId = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, movieId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("rating");
            }
        }
        return null;
    }
}