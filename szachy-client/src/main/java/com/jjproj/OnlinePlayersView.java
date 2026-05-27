package com.jjproj;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OnlinePlayersView {

    public Scene createScene(Stage stage, String gameColor, String gameTime) {
        // --- Nagłówek ---
        Label title = new Label("LISTA GRACZY ONLINE");
        Button refreshBtn = new Button("Odśwież");
        
        HBox header = new HBox(20, title, refreshBtn);
        header.setAlignment(Pos.CENTER);

        // --- Lista graczy ---
        ListView<String> playersList = new ListView<>();
        ObservableList<String> players = FXCollections.observableArrayList();
        playersList.setItems(players);
        playersList.setCellFactory(param -> new PlayerCell(stage, gameColor, gameTime));
        
        // Rejestracja listy w managerze, aby serwer mógł ją aktualizować
        SceneManager.registerPlayersList(players);

        // --- Stopka i status ---
        Label status = new Label("Status");
        Button backBtn = new Button("Wstecz");
        
        Region bottomSpacer = new Region();
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

        // --- Logika przycisków ---
        refreshBtn.setOnAction(e -> {
            SceneManager.setStatus("Pobieranie listy graczy...");
            startGetUsersThread();
        });

        backBtn.setOnAction(e -> {
            PreGameView preGame = new PreGameView();
            stage.setScene(preGame.createScene(stage));
        });

        // --- Layout ---
        VBox layout = new VBox(25, header, playersList, backBtn, bottomSpacer, status);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        // --- Scena i Style ---
        Scene scene = new Scene(layout, 600, 800);
        applyStyles(scene, layout, title, refreshBtn, backBtn, playersList, status);

        SceneManager.registerStatusLabel(status);

        // Automatyczne pobranie listy przy wejściu
        startGetUsersThread();
        
        return scene;
    }

    private void startGetUsersThread() {
        new Thread(() -> NetworkManager.sendCommand("GET_USER_LIST")).start();
    }

    private void applyStyles(Scene scene, VBox layout, Label title, Button refresh, 
                             Button back, ListView<String> list, Label status) {
        
        layout.getStyleClass().add("root-gradient");
        title.getStyleClass().add("subtitle");
        refresh.getStyleClass().add("btn-game");
        back.getStyleClass().add("btn-main");
        
        list.setMaxWidth(400);
        list.setPrefHeight(400);
        
        status.getStyleClass().add("error-label");
        status.setMaxWidth(Double.MAX_VALUE);
        status.setAlignment(Pos.CENTER);

        scene.getStylesheets().add(getClass().getResource("/View.css").toExternalForm());
    }

    // --- Wewnętrzna klasa komórki gracza ---
    private class PlayerCell extends ListCell<String> {
        private final Stage stage;
        private final String color;
        private final String time;

        public PlayerCell(Stage stage, String color, String time) {
            this.stage = stage;
            this.color = color;
            this.time = time;
        }

        @Override
        protected void updateItem(String name, boolean empty) {
            super.updateItem(name, empty);
            if (empty || name == null) {
                setText(null);
                setGraphic(null);
            } else {
                setGraphic(createRowContent(name));
            }
        }

        private HBox createRowContent(String name) {
            Label playerName = new Label(name);
            playerName.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

            Button inviteBtn = new Button("zaproś");
            inviteBtn.getStyleClass().add("btn-game");
            
            inviteBtn.setOnAction(e -> {
                WaitingForPlayerView waitingView = new WaitingForPlayerView();
                stage.setScene(waitingView.createScene(stage, name));

                new Thread(() -> 
                    NetworkManager.sendCommand("INVITE|" + name + "|" + color + "|" + time)
                ).start();
            });

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox row = new HBox(10, playerName, spacer, inviteBtn);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(5, 10, 5, 10));
            return row;
        }
    }
}