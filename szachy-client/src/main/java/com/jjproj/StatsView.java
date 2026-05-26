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

        ListView<String> gamesListView = new ListView<>();
        gamesListView.getStyleClass().add("list-view-custom"); 
        

        gamesListView.getItems().addAll(
            "Gra #152 - Wygrana (vs Gracz123)",
            "Gra #151 - Przegrana (vs MistrzSzachowy)",
            "Gra #150 - Wygrana (vs Janusz_IT)",
            "Gra #149 - Remis (vs Bot_Easy)",
            "Gra #148 - Wygrana (vs Anna99)",
            "Gra #148 - Wygrana (vs Anna99)",
            "Gra #148 - Wygrana (vs Anna99)",
            "Gra #148 - Wygrana (vs Anna99)",
            "Gra #148 - Wygrana (vs Anna99)",
            "Gra #148 - Wygrana (vs Anna99)"


        );
        
        VBox.setVgrow(gamesListView, Priority.ALWAYS);
        leftColumn.getChildren().addAll(leftLabel, gamesListView);

        // PRAWA STRONA
        VBox rightColumn = new VBox(10);
        rightColumn.setPrefWidth(320);
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

        VBox.setVgrow(globalStatsList, Priority.ALWAYS);
        rightColumn.getChildren().addAll(rightLabel, globalStatsList);

        mainContent.getChildren().addAll(leftColumn, rightColumn);
        root.setCenter(mainContent);

        // --- BOTTOM ---
        Button back = new Button("WSTECZ");
        back.getStyleClass().add("btn-main");
        back.setPrefWidth(150);
        back.setOnAction(e -> {
            MenuView menu = new MenuView();
            stage.setScene(menu.createScene(stage));
        });

        HBox bottomBox = new HBox(back);
        bottomBox.setAlignment(Pos.BOTTOM_RIGHT);
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 950, 650);
        scene.getStylesheets().add(getClass().getResource("/View.css").toExternalForm());
        
        return scene;
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