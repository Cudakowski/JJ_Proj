package com.jjproj;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.control.Label;


public class StatsView {

    public Scene createScene(Stage stage) {

        BorderPane root = new BorderPane();

        Label title = new Label("Statystyki");
        title.setStyle("-fx-font-size: 24px;");
        root.setTop(title);

        ListView<String> gamesList = new ListView<>();
        gamesList.getItems().addAll(
                "Gra 1 - wygrana",
                "Gra 2 - przerwana",
                "Gra 3 - przegrana",
                "Dokonczyc"
        );

        root.setCenter(gamesList);

        Button back = new Button("Powrót");
        back.setOnAction(e -> {
            MenuView menu = new MenuView();
            stage.setScene(menu.createScene(stage));
        });

        root.setBottom(new HBox(back));

        return new Scene(root, 600, 600);
    }
}

