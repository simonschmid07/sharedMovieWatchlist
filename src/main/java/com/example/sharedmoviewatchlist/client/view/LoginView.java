package com.example.sharedmoviewatchlist.client.view;

import com.example.sharedmoviewatchlist.client.controller.LoginController;
import com.example.sharedmoviewatchlist.client.service.ApiClient;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LoginView {
    private final Stage stage;
    private final LoginController controller;

    public LoginView(Stage stage, ApiClient apiClient) {
        this.stage = stage;
        this.controller = new LoginController(apiClient);
    }

    public Scene createScene() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25));

        Label titleLabel = new Label("Movie Watchlist");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        grid.add(titleLabel, 0, 0, 2, 1);

        Label userLabel = new Label("Username:");
        grid.add(userLabel, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pwLabel = new Label("Password:");
        grid.add(pwLabel, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.CENTER_RIGHT);
        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Register");
        hbBtn.getChildren().addAll(loginBtn, registerBtn);
        grid.add(hbBtn, 1, 3);

        loginBtn.setOnAction(e -> {
            String username = userTextField.getText();
            String password = pwBox.getText();
            if (username.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Please enter username and password");
                return;
            }
            if (controller.login(username, password)) {
                openMovieView();
            }
        });

        registerBtn.setOnAction(e -> {
            String username = userTextField.getText();
            String password = pwBox.getText();
            if (username.isEmpty() || password.isEmpty()) {
                showAlert("Error", "Please enter username and password");
                return;
            }
            if (controller.register(username, password)) {
                openMovieView();
            }
        });

        return new Scene(grid, 400, 300);
    }

    private void openMovieView() {
        MovieListView movieView = new MovieListView(stage, new ApiClient());
        stage.setScene(movieView.createScene());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}