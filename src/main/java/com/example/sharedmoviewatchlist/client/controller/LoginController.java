package com.example.sharedmoviewatchlist.client.controller;

import com.example.sharedmoviewatchlist.client.service.ApiClient;
import com.example.sharedmoviewatchlist.client.service.Session;
import javafx.scene.control.Alert;

import java.util.Map;

public class LoginController {
    private final ApiClient apiClient;

    public LoginController(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public boolean login(String username, String password) {
        try {
            Map<String, Object> response = apiClient.login(username, password);
            if (response != null) {
                int userId = ((Double) response.get("id")).intValue();
                String user = (String) response.get("username");
                Session.getInstance().setUser(userId, user);
                return true;
            } else {
                showError("Login Failed", "Invalid username or password");
                return false;
            }
        } catch (Exception e) {
            showError("Error", "Could not connect to server: " + e.getMessage());
            return false;
        }
    }

    public boolean register(String username, String password) {
        try {
            Map<String, Object> response = apiClient.register(username, password);
            if (response != null && !response.containsKey("error")) {
                int userId = ((Double) response.get("id")).intValue();
                String user = (String) response.get("username");
                Session.getInstance().setUser(userId, user);
                return true;
            } else {
                String error = response != null ? (String) response.get("error") : "Registration failed";
                showError("Registration Failed", error);
                return false;
            }
        } catch (Exception e) {
            showError("Error", "Could not connect to server: " + e.getMessage());
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