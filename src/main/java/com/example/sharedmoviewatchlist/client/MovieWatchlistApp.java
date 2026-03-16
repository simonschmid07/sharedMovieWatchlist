package com.example.sharedmoviewatchlist.client;

import com.example.sharedmoviewatchlist.client.service.ApiClient;
import com.example.sharedmoviewatchlist.client.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class MovieWatchlistApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Movie Watchlist");

        ApiClient apiClient = new ApiClient();
        LoginView loginView = new LoginView(primaryStage, apiClient);

        primaryStage.setScene(loginView.createScene());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}