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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

//klasa pomocnicza dla powiadomienia
class Invitation {
    String text;
    boolean rejected = false;

    Invitation(String text) { this.text = text; }
}

public class NotificationsView {


    private static final ObservableList<Invitation> notificationsData = FXCollections.observableArrayList();

    public Scene createScene(Stage stage) {
        Label title = new Label("LISTA POWIADOMIEŃ");
        title.getStyleClass().add("subtitle");

        Label status = new Label("Status");
        status.getStyleClass().add("error-label");

        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);


        ListView<Invitation> listView = new ListView<>(notificationsData);
        listView.setPrefHeight(400);
        listView.setMaxWidth(500);

        // Tutaj dalam testowe dane
        if (notificationsData.isEmpty()) {
            notificationsData.add(new Invitation("Mietek zaprasza do gry; 10min; ty białe"));
            notificationsData.add(new Invitation("Mietek zaprasza do gry; bezczasowa; losowo"));
        }

        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Invitation item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    VBox container = new VBox(5);
                    Label msgLabel = new Label(item.text);
                    msgLabel.setWrapText(true);
                    msgLabel.setStyle("-fx-text-fill: white;");

                    HBox actions = new HBox(10);
                    actions.setAlignment(Pos.CENTER_RIGHT);

                    if (item.rejected) {
                        //  odrzucone - tylko napis
                        Label rejectedLabel = new Label("odrzucono");
                        rejectedLabel.setStyle("-fx-text-fill: #ff4444; -fx-font-style: italic;");
                        actions.getChildren().add(rejectedLabel);
                    } else {
                        // otheriwse przyciski
                        Button rejectBtn = new Button("odrzuć");
                        Button joinBtn = new Button("dołącz");
                        
                        rejectBtn.getStyleClass().add("btn-game");
                        joinBtn.getStyleClass().add("btn-game");

                        rejectBtn.setOnAction(e -> {
                            item.rejected = true;

                            updateItem(item, false); 
                        });

                        joinBtn.setOnAction(e -> {
                            // Przejście do gry
                            GameView game = new GameView();
                            stage.setScene(game.createScene(stage));
                        });

                        actions.getChildren().addAll(rejectBtn, joinBtn);
                    }

                    container.getChildren().addAll(msgLabel, actions);
                    container.setPadding(new Insets(5));
                    setGraphic(container);
                }
            }
        });

        Button backBtn = new Button("Wstecz");
        backBtn.getStyleClass().add("btn-main");
        backBtn.setOnAction(e -> {
            MenuView menu = new MenuView();
            stage.setScene(menu.createScene(stage));
        });

        VBox layout = new VBox(20, title, listView, backBtn, spacer, status);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.getStyleClass().add("root-gradient");

        Scene scene = new Scene(layout, 600, 800);
        scene.getStylesheets().add(getClass().getResource("/View.css").toExternalForm());

        return scene;
    }

    public static void addNotification(String message) {
        notificationsData.add(new Invitation(message));
    }
}        