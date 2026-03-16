package com.example.sharedmoviewatchlist.client.view;

import com.example.sharedmoviewatchlist.client.controller.MovieController;
import com.example.sharedmoviewatchlist.client.service.ApiClient;
import com.example.sharedmoviewatchlist.client.service.Session;
import com.example.sharedmoviewatchlist.shared.Movie;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MovieListView {
    private final Stage stage;
    private final MovieController controller;
    private TableView<Movie> movieTable;
    private ObservableList<Movie> movieData;

    public MovieListView(Stage stage, ApiClient apiClient) {
        this.stage = stage;
        this.controller = new MovieController(apiClient);
    }

    public Scene createScene() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label titleLabel = new Label("Movie Watchlist - Welcome, " + Session.getInstance().getUsername());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        movieTable = new TableView<>();
        movieData = FXCollections.observableArrayList();
        movieTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Movie, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);

        TableColumn<Movie, String> genreCol = new TableColumn<>("Genre");
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        genreCol.setPrefWidth(120);

        TableColumn<Movie, Integer> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));
        yearCol.setPrefWidth(80);

        TableColumn<Movie, Boolean> watchedCol = new TableColumn<>("Watched");
        watchedCol.setCellValueFactory(new PropertyValueFactory<>("watched"));
        watchedCol.setPrefWidth(100);

        TableColumn<Movie, Double> ratingCol = new TableColumn<>("Avg Rating");
        ratingCol.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getAverageRating()));
        ratingCol.setPrefWidth(100);

        ratingCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double rating, boolean empty) {
                super.updateItem(rating, empty);

                if (empty || rating == null) {
                    setText(null);
                } else if (rating == 0.0) {
                    setText("-");
                } else {
                    setText(String.format("%.1f", rating));
                }
            }
        });

        movieTable.getColumns().addAll(titleCol, genreCol, yearCol, watchedCol, ratingCol);
        movieTable.setItems(movieData);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button addBtn = new Button("Add Movie");
        Button editBtn = new Button("Edit Movie");
        Button deleteBtn = new Button("Delete Movie");
        Button watchedBtn = new Button("Toggle Watched");
        Button rateBtn = new Button("Rate Movie");
        Button refreshBtn = new Button("Refresh");

        buttonBox.getChildren().addAll(addBtn, editBtn, deleteBtn, watchedBtn, rateBtn, refreshBtn);

        addBtn.setOnAction(e -> showAddMovieDialog());
        editBtn.setOnAction(e -> showEditMovieDialog());
        deleteBtn.setOnAction(e -> deleteSelectedMovie());
        watchedBtn.setOnAction(e -> toggleWatchedStatus());
        rateBtn.setOnAction(e -> showRatingDialog());
        refreshBtn.setOnAction(e -> refreshMovies());

        root.getChildren().addAll(titleLabel, movieTable, buttonBox);

        refreshMovies();

        return new Scene(root, 800, 500);
    }

    private void refreshMovies() {
        movieData.clear();
        movieData.addAll(controller.loadMovies());
    }

    private void showAddMovieDialog() {
        Dialog<Movie> dialog = new Dialog<>();
        dialog.setTitle("Add Movie");
        dialog.setHeaderText("Add a new movie to the watchlist");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField titleField = new TextField();
        TextField genreField = new TextField();
        TextField yearField = new TextField();

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Genre:"), 0, 1);
        grid.add(genreField, 1, 1);
        grid.add(new Label("Year:"), 0, 2);
        grid.add(yearField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String title = titleField.getText().trim();
                String genre = genreField.getText().trim();
                String yearText = yearField.getText().trim();

                if (title.isEmpty()) {
                    showAlert("Error", "Title must not be empty");
                    return null;
                }

                try {
                    int year = Integer.parseInt(yearText);
                    if (year < 1888 || year > 2100) {
                        showAlert("Error", "Year must be between 1888 and 2100");
                        return null;
                    }

                    Movie movie = controller.addMovie(title, genre, year);
                    if (movie != null) {
                        refreshMovies();
                    }
                } catch (NumberFormatException e) {
                    showAlert("Error", "Year must be a valid number");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showEditMovieDialog() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a movie to edit");
            return;
        }

        Dialog<Movie> dialog = new Dialog<>();
        dialog.setTitle("Edit Movie");
        dialog.setHeaderText("Edit movie details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField titleField = new TextField(selected.getTitle());
        TextField genreField = new TextField(selected.getGenre());
        TextField yearField = new TextField(String.valueOf(selected.getReleaseYear()));

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Genre:"), 0, 1);
        grid.add(genreField, 1, 1);
        grid.add(new Label("Year:"), 0, 2);
        grid.add(yearField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String title = titleField.getText().trim();
                String genre = genreField.getText().trim();
                String yearText = yearField.getText().trim();

                if (title.isEmpty()) {
                    showAlert("Error", "Title must not be empty");
                    return null;
                }

                try {
                    int year = Integer.parseInt(yearText);
                    if (year < 1888 || year > 2100) {
                        showAlert("Error", "Year must be between 1888 and 2100");
                        return null;
                    }

                    selected.setTitle(title);
                    selected.setGenre(genre);
                    selected.setReleaseYear(year);

                    if (controller.updateMovie(selected) != null) {
                        refreshMovies();
                    }
                } catch (NumberFormatException e) {
                    showAlert("Error", "Year must be a valid number");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void deleteSelectedMovie() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a movie to delete");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText("Delete movie");
        confirmation.setContentText("Are you sure you want to delete \"" + selected.getTitle() + "\"?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (controller.deleteMovie(selected.getId())) {
                    refreshMovies();
                }
            }
        });
    }

    private void toggleWatchedStatus() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a movie");
            return;
        }

        selected.setWatched(!selected.isWatched());
        if (controller.updateMovie(selected) != null) {
            refreshMovies();
        }
    }

    private void showRatingDialog() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a movie to rate");
            return;
        }

        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(3, 1, 2, 3, 4, 5);
        dialog.setTitle("Rate Movie");
        dialog.setHeaderText("Rate: " + selected.getTitle());
        dialog.setContentText("Choose rating (1-5):");

        dialog.showAndWait().ifPresent(rating -> {
            if (controller.addRating(selected.getId(), rating)) {
                refreshMovies();
                showAlert("Success", "Rating saved successfully");
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert.AlertType alertType = title.equalsIgnoreCase("Error")
                || title.equalsIgnoreCase("No Selection")
                ? Alert.AlertType.WARNING
                : Alert.AlertType.INFORMATION;

        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}