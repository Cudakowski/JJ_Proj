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

    public Scene createScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // --- TOP ---
        Label title = new Label("STATYSTYKI");
        title.getStyleClass().add("main-title-small");
        
        HBox topBox = new HBox(title);
        topBox.setAlignment(Pos.CENTER);
        root.setTop(topBox);

        // --- CENTER (Podział na kolumny) ---
        VBox leftColumn = createGameHistoryColumn();
        VBox rightColumn = createDetailedStatsColumn();

        HBox mainContent = new HBox(20, leftColumn, rightColumn);
        mainContent.setPadding(new Insets(20, 0, 20, 0));
        root.setCenter(mainContent);

        // --- BOTTOM ---
        Label status = new Label("Status");
        Button backBtn = new Button("WSTECZ");
        
        backBtn.setOnAction(e -> stage.setScene(new MenuView().createScene(stage)));

        VBox bottomContainer = setupBottomLayout(backBtn, status);
        root.setBottom(bottomContainer);

        // --- SCENA I STYLIZACJA ---
        Scene scene = new Scene(root, 950, 650);
        applyStyles(scene, root, backBtn, status);

        SceneManager.registerStatusLabel(status);

        return scene;
    }

    private VBox createGameHistoryColumn() {
        Label leftLabel = new Label("Historia Twoich Gier");
        leftLabel.getStyleClass().add("section-label");

        ListView<String> gamesListView = new ListView<>();
        gamesListView.getStyleClass().add("list-view-custom");
        
        // TODO: W przyszłości pobieranie danych z bazy/serwera
        gamesListView.getItems().addAll(
            "Gra #152 - Wygrana (vs Gracz123)",
            "Gra #151 - Przegrana (vs MistrzSzachowy)",
            "Gra #150 - Wygrana (vs Janusz_IT)",
            "Gra #149 - Remis (vs Bot_Easy)",
            "Gra #148 - Wygrana (vs Anna99)",
            "Gra #147 - Wygrana (vs Anna99)",
            "Gra #146 - Wygrana (vs Anna99)"
        );

        VBox column = new VBox(10, leftLabel, gamesListView);
        VBox.setVgrow(gamesListView, Priority.ALWAYS);
        HBox.setHgrow(column, Priority.ALWAYS);
        
        return column;
    }

    private VBox createDetailedStatsColumn() {
        Label rightLabel = new Label("Szczegółowe statystyki");
        rightLabel.getStyleClass().add("section-label");

        ListView<VBox> globalStatsList = new ListView<>();
        globalStatsList.getStyleClass().add("list-view-stats");

        globalStatsList.getItems().addAll(
            createStatTile("Suma rozegranych gier", "154,200"),
            createStatTile("Zbite pionki (Białe)", "942,105"),
            createStatTile("Zbite pionki (Czarne)", "931,002"),
            createStatTile("Najpopularniejszy ruch", "e2 -> e4"),
            createStatTile("Średni czas gry", "12m 45s")
        );

        VBox column = new VBox(10, rightLabel, globalStatsList);
        column.setPrefWidth(320);
        VBox.setVgrow(globalStatsList, Priority.ALWAYS);
        
        return column;
    }

    private VBox setupBottomLayout(Button backBtn, Label status) {
        HBox backBox = new HBox(backBtn);
        backBox.setAlignment(Pos.CENTER);

        VBox container = new VBox(10, backBox, status);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(10));
        
        return container;
    }

    private void applyStyles(Scene scene, BorderPane root, Button back, Label status) {
        root.getStyleClass().add("root-dark");
        
        back.getStyleClass().add("btn-main");
        back.setPrefWidth(150);

        status.getStyleClass().add("error-label");
        status.setMaxWidth(Double.MAX_VALUE);
        status.setAlignment(Pos.CENTER);

        scene.getStylesheets().add(getClass().getResource("/View.css").toExternalForm());
    }

    private VBox createStatTile(String title, String value) {
        Label t = new Label(title);
        t.setStyle("-fx-text-fill: #d9c7a3; -fx-font-size: 13px;");

        Label v = new Label(value);
        v.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        VBox tile = new VBox(5, t, v);
        tile.setAlignment(Pos.CENTER);
        tile.setPadding(new Insets(15));
        tile.getStyleClass().add("stat-item-box");

        return tile;
    }
}