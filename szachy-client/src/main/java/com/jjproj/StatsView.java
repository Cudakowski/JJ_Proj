package com.jjproj;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StatsView {
    private ListView<String> gamesListView;
    private ListView<VBox> globalStatsList;

    public Scene createScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.getStyleClass().add("root-dark");

        // --- TOP ---
        Label title = new Label("STATYSTYKI");
        title.getStyleClass().add("main-title-small");

        HBox topBox = new HBox(title);
        topBox.setAlignment(Pos.CENTER);

        root.setTop(topBox);

        // --- CENTER ---
        HBox mainContent = new HBox(20);
        mainContent.setPadding(new Insets(20, 0, 20, 0));

        // LEWA STRONA
        VBox leftColumn = new VBox(10);
        HBox.setHgrow(leftColumn, Priority.ALWAYS);

        Label leftLabel = new Label("Historia Twoich Gier");
        leftLabel.getStyleClass().add("section-label");

        gamesListView = new ListView<>();
        gamesListView.getStyleClass().add("list-view-custom");
        gamesListView.getItems().add("Ładowanie historii...");

        // gamesListView.getItems().addAll(
        //     "Gra #152 - Wygrana (vs Gracz123)",
        //     "Gra #151 - Przegrana (vs MistrzSzachowy)",
        //     "Gra #150 - Wygrana (vs Janusz_IT)",
        //     "Gra #149 - Remis (vs Bot_Easy)",
        //     "Gra #148 - Wygrana (vs Anna99)",
        //     "Gra #148 - Wygrana (vs Anna99)",
        //     "Gra #148 - Wygrana (vs Anna99)",
        //     "Gra #148 - Wygrana (vs Anna99)",
        //     "Gra #148 - Wygrana (vs Anna99)",
        //     "Gra #148 - Wygrana (vs Anna99)"
        // );

        VBox.setVgrow(gamesListView, Priority.ALWAYS);
        leftColumn.getChildren().addAll(leftLabel, gamesListView);

        // PRAWA STRONA
        VBox rightColumn = new VBox(10);
        rightColumn.setPrefWidth(320);

        Label rightLabel = new Label("Szczegółowe statystyki");
        rightLabel.getStyleClass().add("section-label");

        globalStatsList = new ListView<>();
        globalStatsList.getStyleClass().add("list-view-stats");

        // globalStatsList.getItems().addAll(
        //     createStatTile("Suma rozegranych gier", "154,200"),
        //     createStatTile("Zbite pionki (Białe)", "942,105"),
        //     createStatTile("Zbite pionki (Czarne)", "931,002"),
        //     createStatTile("Najpopularniejszy ruch", "e2 -> e4"),
        //     createStatTile("Średni czas gry", "12m 45s")
        // );

        VBox.setVgrow(globalStatsList, Priority.ALWAYS);
        rightColumn.getChildren().addAll(rightLabel, globalStatsList);

        mainContent.getChildren().addAll(leftColumn, rightColumn);

        root.setCenter(mainContent);

        // =========================
        // BOTTOM
        // =========================

        Button back = new Button("WSTECZ");
        back.getStyleClass().add("btn-main");
        back.setPrefWidth(150);

        back.setOnAction(e -> {
            MenuView menu = new MenuView();
            stage.setScene(menu.createScene(stage));
        });

        // STATUS LABEL TAKI JAK W GAMEVIEW
        Label status = new Label("Status");
        status.getStyleClass().add("error-label");
        status.setMaxWidth(Double.MAX_VALUE);
        status.setAlignment(Pos.CENTER);

        HBox backBox = new HBox(back);
        backBox.setAlignment(Pos.CENTER);

        VBox bottomContainer = new VBox(10, backBox, status);
        bottomContainer.setAlignment(Pos.CENTER);
        bottomContainer.setPadding(new Insets(10));


        root.setBottom(bottomContainer);

        Scene scene = new Scene(root, 950, 650);

        scene.getStylesheets().add(
            getClass().getResource("/View.css").toExternalForm()
        );

        // REJESTRACJA STATUSU
        SceneManager.registerStatusLabel(status);
        SceneManager.registerStatsView(this);
        
        new Thread(() -> {
            NetworkManager.sendCommand("GET_STATS");
        }).start();

        return scene;
    }

    public void populateData(String historyStr, String detailsStr) {
        gamesListView.getItems().clear();
        if (historyStr == null || historyStr.trim().isEmpty()) {
            gamesListView.getItems().add("Brak zakończonych gier.");
        } else {
            String[] games = historyStr.split(",");
            for (String g : games) {
                String[] parts = g.split(":"); // format: gameId:opponent:outcome
                if (parts.length == 3) {
                    gamesListView.getItems().add("Gra #" + parts[0] + " - " + parts[2] + " (vs " + parts[1] + ")");
                }
            }
        }

        globalStatsList.getItems().clear();
        String[] stats = detailsStr.split(","); // totalGames, winrate%, wins, draws, losses, capturedPieces
        if (stats.length == 6) {
            globalStatsList.getItems().addAll(
                createStatTile("Rozegrane partie", stats[0]),
                createStatTile("Winrate (Zwycięstwa)", stats[1]),
                createStatTile("Zwycięstwa / Remisy / Porażki", stats[2] + " / " + stats[3] + " / " + stats[4]),
                createStatTile("Zbite figury przeciwników", stats[5])
            );
        }
    }

    // Metoda dla prawych kafelków
    private VBox createStatTile(String title, String value) {

        VBox tile = new VBox(5);

        tile.setAlignment(Pos.CENTER);
        tile.setPadding(new Insets(15));

        tile.getStyleClass().add("stat-item-box");

        Label t = new Label(title);
        t.setStyle("-fx-text-fill: #d9c7a3; -fx-font-size: 13px;");

        Label v = new Label(value);
        v.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        tile.getChildren().addAll(t, v);

        return tile;
    }
}