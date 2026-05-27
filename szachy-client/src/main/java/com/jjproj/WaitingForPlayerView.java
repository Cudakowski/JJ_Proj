package com.jjproj;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WaitingForPlayerView {

    private ListView<String> statusLog;

    public Scene createScene(Stage stage, String opponentName) {
        // --- Elementy tekstowe ---
        Label title = new Label("OCZEKIWANIE NA GRACZA");
        Label status = new Label("Status");
        Label infoLabel = new Label("Kiedy gracz przyjmie zaproszenie,\ngra rozpocznie się automatycznie.");

        // --- Logi statusu ---
        statusLog = new ListView<>();
        addStatus("Zaproszenie wysłano do gracza " + opponentName);
        
        // --- Przyciski ---
        Button cancelBtn = new Button("Anuluj");
        cancelBtn.setOnAction(e -> handleCancel(stage, opponentName));

        // --- Układ (Layout) ---
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox layout = new VBox(30, title, statusLog, infoLabel, cancelBtn, spacer, status);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        // --- Scena i Stylizacja ---
        Scene scene = new Scene(layout, 600, 800);
        applyStyles(scene, layout, title, statusLog, infoLabel, cancelBtn, status);

        // --- Rejestracja w managerach ---
        SceneManager.registerStatusLabel(status);
        SceneManager.registerWaitingView(this);
        
        return scene;
    }

    private void handleCancel(Stage stage, String opponentName) {
        new Thread(() -> NetworkManager.sendCommand("CANCEL_INVITE|" + opponentName)).start();
        
        PreGameView preGame = new PreGameView();
        stage.setScene(preGame.createScene(stage));
    }

    private void applyStyles(Scene scene, VBox layout, Label title, ListView<String> log, 
                             Label info, Button cancel, Label status) {
        
        layout.getStyleClass().add("root-gradient");
        title.getStyleClass().add("subtitle");
        
        log.getStyleClass().add("list-view");
        log.setPrefHeight(200);
        log.setMaxWidth(450);

        info.setStyle("-fx-text-fill: #d9c7a3; -fx-text-alignment: center; -fx-font-size: 14px;");

        cancel.getStyleClass().add("btn-main");

        status.getStyleClass().add("error-label");
        status.setMaxWidth(Double.MAX_VALUE);
        status.setAlignment(Pos.CENTER);

        scene.getStylesheets().add(getClass().getResource("/View.css").toExternalForm());
    }

    /**
     * Metoda wywoływana przez NetworkManager przy zmianie statusu zaproszenia.
     */
    public void addStatus(String message) {
        statusLog.getItems().add(message);
        statusLog.scrollTo(statusLog.getItems().size() - 1);
    }
}