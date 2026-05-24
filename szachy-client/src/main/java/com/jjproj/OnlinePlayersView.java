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

    public Scene createScene(Stage stage,String gameColor,String gameTime) {
        // Tytuł strony
        Label title = new Label("LISTA GRACZY ONLINE");

        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        Label status = new Label("Status");
        status.getStyleClass().add("error-label");
        status.setMaxWidth(Double.MAX_VALUE);
        status.setAlignment(Pos.CENTER);




        Button refreshBtn = new Button("Odśwież");



        HBox header = new HBox(20, title, refreshBtn);
        header.setAlignment(Pos.CENTER);

        // Lista graczy
        ListView<String> playersList = new ListView<>();
        
        // Przykładowe dane (potem tutaj da sie dane z serwera)
        ObservableList<String> players = FXCollections.observableArrayList();
        playersList.setItems(players);
        SceneManager.registerPlayersList(players);

        // Tutaj robie customowy wyglad wiersza w liscie
        playersList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty || name == null) {
                    setText(null);
                    setGraphic(null);
                } else {


                    Label playerName = new Label(name);
                    playerName.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

                    Button inviteBtn = new Button("zaproś");
                    inviteBtn.getStyleClass().add("btn-game");
                    

                    inviteBtn.setOnAction(e -> {
                        System.out.println("Zaproszono gracza: " + name + " (" + gameColor + ", " + gameTime + ")");
                        WaitingForPlayerView waitingForPlayerView = new WaitingForPlayerView();
                        stage.setScene(waitingForPlayerView.createScene(stage, name));

                        new Thread(() -> {
                            NetworkManager.sendCommand("INVITE|" + name + "|" + gameColor + "|" + gameTime);
                        }).start();
                    });


                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);


                    HBox row = new HBox(10, playerName, spacer, inviteBtn);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setPadding(new Insets(5, 10, 5, 10));

                    setGraphic(row);
                }
            }
        });

        

        refreshBtn.setOnAction(e -> {
            SceneManager.setStatus("Pobieranie listy graczy...");
            
            startGetUsersThread();
        });


        Button backBtn = new Button("Wstecz");

        backBtn.setOnAction(e -> {
            PreGameView preGame = new PreGameView();
            stage.setScene(preGame.createScene(stage));
        });


        VBox layout = new VBox(25, header, playersList, backBtn, spacer, status);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));


        // stylizowanie
        title.getStyleClass().add("subtitle");
        refreshBtn.getStyleClass().add("btn-game"); 
        backBtn.getStyleClass().add("btn-main");
        layout.getStyleClass().add("root-gradient");


        playersList.setMaxWidth(400);
        playersList.setPrefHeight(400);

        Scene scene = new Scene(layout, 600, 800);
        scene.getStylesheets().add(getClass().getResource("/View.css").toExternalForm());

        SceneManager.registerStatusLabel(status);

        startGetUsersThread();
        return scene;
    }

    private void startGetUsersThread(){
        new Thread(() -> {
            NetworkManager.sendCommand("GET_USER_LIST"); 
        }).start();
    } 
}
