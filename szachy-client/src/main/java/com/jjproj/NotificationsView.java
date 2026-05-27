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

class Invitation {
    String sender;
    String text;
    boolean rejected = false;
    boolean cancelled = false;

    Invitation(String sender, String text) {
        this.sender = sender;
        this.text = text;
    }
}

public class NotificationsView {

    private static final ObservableList<Invitation> notificationsData = FXCollections.observableArrayList();

    public Scene createScene(Stage stage) {
        SceneManager.setNotificationsViewActive(true);

        // --- Elementy UI ---
        Label title = new Label("LISTA POWIADOMIEŃ");
        Label status = new Label("Status");
        
        ListView<Invitation> listView = new ListView<>(notificationsData);
        listView.setPrefHeight(400);
        listView.setMaxWidth(500);
        listView.setCellFactory(param -> new InvitationCell());

        Button backBtn = new Button("Wstecz");
        backBtn.setOnAction(e -> {
            SceneManager.setNotificationsViewActive(false); // Poprawione na false przy wyjściu
            stage.setScene(new MenuView().createScene(stage));
        });

        // --- Layout ---
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox layout = new VBox(20, title, listView, backBtn, spacer, status);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        // --- Scena i Style ---
        Scene scene = new Scene(layout, 600, 800);
        applyStyles(scene, layout, title, backBtn, status);

        SceneManager.registerStatusLabel(status);

        return scene;
    }

    private void applyStyles(Scene scene, VBox layout, Label title, Button back, Label status) {
        layout.getStyleClass().add("root-gradient");
        title.getStyleClass().add("subtitle");
        back.getStyleClass().add("btn-main");
        
        status.getStyleClass().add("error-label");
        status.setMaxWidth(Double.MAX_VALUE);
        status.setAlignment(Pos.CENTER);

        scene.getStylesheets().add(getClass().getResource("/View.css").toExternalForm());
    }

    // --- Zarządzanie danymi ---
    public static void addNotification(String sender, String message) {
        notificationsData.add(new Invitation(sender, message));
    }

    public static void markAsCancelled(String sender) {
        for (int i = 0; i < notificationsData.size(); i++) {
            Invitation inv = notificationsData.get(i);
            if (inv.sender.equals(sender) && !inv.cancelled && !inv.rejected) {
                inv.cancelled = true;
                notificationsData.set(i, inv);
            }
        }
    }

    // --- Wewnętrzna klasa komórki (Clean Architecture) ---
    private class InvitationCell extends ListCell<Invitation> {
        @Override
        protected void updateItem(Invitation item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                setGraphic(createCellContent(item));
            }
        }

        private VBox createCellContent(Invitation item) {
            Label msgLabel = new Label(item.text);
            msgLabel.setWrapText(true);
            msgLabel.setStyle("-fx-text-fill: white;");

            HBox actions = new HBox(10);
            actions.setAlignment(Pos.CENTER_RIGHT);

            if (item.rejected) {
                actions.getChildren().add(createStateLabel("odrzucono", "#ff4444"));
            } else if (item.cancelled) {
                actions.getChildren().add(createStateLabel("anulowano", "#888888"));
            } else {
                actions.getChildren().addAll(createRejectButton(item), createJoinButton(item));
            }

            VBox container = new VBox(5, msgLabel, actions);
            container.setPadding(new Insets(5));
            return container;
        }

        private Label createStateLabel(String text, String color) {
            Label label = new Label(text);
            label.setStyle("-fx-text-fill: " + color + "; -fx-font-style: italic;");
            return label;
        }

        private Button createRejectButton(Invitation item) {
            Button btn = new Button("odrzuć");
            btn.getStyleClass().add("btn-game");
            btn.setOnAction(e -> {
                item.rejected = true;
                updateItem(item, false);
                new Thread(() -> NetworkManager.sendCommand("DECLINE|" + item.sender)).start();
            });
            return btn;
        }

        private Button createJoinButton(Invitation item) {
            Button btn = new Button("dołącz");
            btn.getStyleClass().add("btn-game");
            btn.setOnAction(e -> {
                SceneManager.setStatus("Łączenie z graczem...");
                new Thread(() -> NetworkManager.sendCommand("ACCEPT|" + item.sender)).start();
            });
            return btn;
        }
    }
}