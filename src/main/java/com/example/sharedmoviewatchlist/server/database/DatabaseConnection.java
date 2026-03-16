package com.example.sharedmoviewatchlist.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:h2:./moviedb";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            initializeDatabase();
        }
        return connection;
    }

    private static void initializeDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(255) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS movies (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "genre VARCHAR(100), " +
                    "releaseYear INT, " +
                    "watched BOOLEAN DEFAULT FALSE, " +
                    "addedByUserId INT, " +
                    "FOREIGN KEY (addedByUserId) REFERENCES users(id))");

            stmt.execute("CREATE TABLE IF NOT EXISTS ratings (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "userId INT NOT NULL, " +
                    "movieId INT NOT NULL, " +
                    "rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5), " +
                    "FOREIGN KEY (userId) REFERENCES users(id), " +
                    "FOREIGN KEY (movieId) REFERENCES movies(id), " +
                    "UNIQUE (userId, movieId))");
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}