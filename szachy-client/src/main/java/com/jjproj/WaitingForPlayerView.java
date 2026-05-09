package com.jjproj;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WaitingForPlayerView {

    private ListView<String> statusLog;

    public Scene createScene(Stage stage, String opponentName) {
        // tytul
        Label title = new Label("OCZEKIWANIE NA GRACZA");
        title.getStyleClass().add("subtitle");

        // logi
        statusLog = new ListView<>();
        statusLog.getStyleClass().add("list-view");
        statusLog.setPrefHeight(200);
        statusLog.setMaxWidth(450);

        // Przykładowe wpisy na start - potem to wywoływać metodą addStatus
        addStatus("Zaproszenie wysłano do gracza " + opponentName);
        
        // Pomocniczy label z informacją
        Label infoLabel = new Label("Kiedy gracz przyjmie zaproszenie,\ngra rozpocznie się automatycznie.");
        infoLabel.setStyle("-fx-text-fill: #d9c7a3; -fx-text-alignment: center; -fx-font-size: 14px;");

        // Przycisk Anuluj
        Button cancelBtn = new Button("Anuluj");
        cancelBtn.getStyleClass().add("btn-main");

        cancelBtn.setOnAction(e -> {
            // Tutaj logika wysyłająca do serwera info o anulowaniu
            PreGameView preGame = new PreGameView();
            stage.setScene(preGame.createScene(stage));
        });

        // Układ
        VBox layout = new VBox(30, title, statusLog, infoLabel, cancelBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));
        layout.getStyleClass().add("root-gradient");

        Scene scene = new Scene(layout, 600, 800);
        scene.getStylesheets().add(getClass().getResource("/View.css").toExternalForm());


        return scene;
    }

    // Metoda, którą będziemy wywoływać z sieci
    public void addStatus(String message) {
        statusLog.getItems().add(message);
        statusLog.scrollTo(statusLog.getItems().size() - 1); // Zeby przewijac na dol
    }

    private void goToGame(Stage stage) {
        GameView game = new GameView();
        stage.setScene(game.createScene(stage));
    }
}