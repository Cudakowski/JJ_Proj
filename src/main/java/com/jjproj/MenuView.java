package com.jjproj;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class MenuView {

    public Scene createScene(Stage stage) {
        Label title = new Label("SZACHY");
        title.setStyle(
            "-fx-font-size: 72px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: beige;" +
            "-fx-background-color: brown;" +
            "-fx-padding: 10; "+
            "-fx-background-radius: 10;"
        );


        Button newGame = new Button("Nowa gra");
        Button stats = new Button("Statystyki");
        Button exit = new Button("Wyjście");

        String buttonStyle =
        "-fx-font-size: 18px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: white;" +
        "-fx-background-color: #333;" +
        "-fx-background-radius: 10;" +
        "-fx-padding: 10 30 10 30;";

        newGame.setStyle(buttonStyle);

        stats.setStyle(buttonStyle);

        exit.setStyle(buttonStyle);

        newGame.setOnAction(e -> {
            GameView gameView = new GameView();
            stage.setScene(gameView.createScene(stage));
        });

        stats.setOnAction(e -> {
            StatsView statsView = new StatsView();
            stage.setScene(statsView.createScene(stage));
        });

        exit.setOnAction(e -> stage.close());

        VBox layout = new VBox(20, title, newGame, stats, exit);
        layout.setAlignment(Pos.CENTER); 
        layout.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #111, #222);" +
                "-fx-padding: 40;"
        );

        String hover =
            "-fx-background-color: #555;";

            newGame.setOnMouseEntered(e -> newGame.setStyle(buttonStyle + hover));
            newGame.setOnMouseExited(e -> newGame.setStyle(buttonStyle));

            stats.setOnMouseEntered(e -> stats.setStyle(buttonStyle + hover));
            stats.setOnMouseExited(e -> stats.setStyle(buttonStyle));

            exit.setOnMouseEntered(e -> exit.setStyle(buttonStyle + hover));
            exit.setOnMouseExited(e -> exit.setStyle(buttonStyle));



            Scene scene = new Scene(layout, 600, 600);

            stage.setScene(scene);

            stage.setMinWidth(400);
            stage.setMinHeight(500);

            return scene;

    }
}
