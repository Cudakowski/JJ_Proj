package com.jjproj;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuView {

    public Scene createScene(Stage stage) {
        // --- Powiadomienia (Top Bar) ---
        Button notificationsBtn = createNotificationButton();
        Label badge = new Label();
        
        StackPane notificationWrapper = new StackPane(notificationsBtn, badge);
        StackPane.setAlignment(badge, Pos.BOTTOM_LEFT);
        notificationWrapper.setPadding(new Insets(5));

        HBox topBar = new HBox(notificationWrapper);
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setPadding(new Insets(10));

        // --- Elementy Menu ---
        Label title = new Label("SZACHY");
        Label status = new Label("Status");

        Button newGameBtn = new Button("Graj");
        Button statsBtn = new Button("Statystyki");
        Button logOutBtn = new Button("Wyloguj");
        Button exitBtn = new Button("Wyjście");

        // --- Logika Przycisków ---
        notificationsBtn.setOnAction(e -> {
            SceneManager.setBadgeVisibility(false);
            NotificationsView notificationsView = new NotificationsView();
            stage.setScene(notificationsView.createScene(stage));
        });

        newGameBtn.setOnAction(e -> {
            PreGameView preGame = new PreGameView();
            stage.setScene(preGame.createScene(stage));
        });

        statsBtn.setOnAction(e -> {
            StatsView statsView = new StatsView();
            stage.setScene(statsView.createScene(stage));
        });

        logOutBtn.setOnAction(e -> handleLogout(stage));

        exitBtn.setOnAction(e -> stage.close());

        // --- Układ (Layout) ---
        Region spacerMiddle = new Region();
        VBox.setVgrow(spacerMiddle, Priority.ALWAYS);

        VBox menuBox = new VBox(20, title, newGameBtn, statsBtn, logOutBtn, exitBtn, spacerMiddle, status);
        menuBox.setAlignment(Pos.CENTER);

        Region spacerTop = new Region();
        Region spacerBottom = new Region();
        VBox.setVgrow(spacerTop, Priority.ALWAYS);
        VBox.setVgrow(spacerBottom, Priority.ALWAYS);

        VBox root = new VBox(topBar, spacerTop, menuBox, spacerBottom);

        // --- Scena i Konfiguracja ---
        Scene scene = new Scene(root, 600, 600);
        applyStyles(scene, root, title, newGameBtn, statsBtn, logOutBtn, exitBtn, notificationsBtn, badge, status);

        stage.setMinWidth(400);
        stage.setMinHeight(500);

        SceneManager.registerStatusLabel(status);
        SceneManager.registerNotificationBadge(badge);

        return scene;
    }

    private Button createNotificationButton() {
        Image bellImage = new Image(getClass().getResourceAsStream("/bell.png"));
        ImageView bellIcon = new ImageView(bellImage);
        bellIcon.setFitWidth(20);
        bellIcon.setFitHeight(20);

        Button btn = new Button();
        btn.setGraphic(bellIcon);
        return btn;
    }

    private void handleLogout(Stage stage) {
        SceneManager.setStatus("Wylogowywanie");
        Thread logoutThread = new Thread(() -> {
            NetworkManager.disconnect();
            Platform.runLater(() -> {
                LoginView loginView = new LoginView();
                stage.setScene(loginView.createScene(stage));
            });
        });
        logoutThread.setDaemon(true);
        logoutThread.start();
    }

    private void applyStyles(Scene scene, VBox root, Label title, Button newGame, Button stats, 
                             Button logOut, Button exit, Button notify, Label badge, Label status) {
        
        root.getStyleClass().add("root-gradient");
        title.getStyleClass().add("main-title");
        
        newGame.getStyleClass().add("btn-main");
        stats.getStyleClass().add("btn-main");
        logOut.getStyleClass().add("btn-main");
        exit.getStyleClass().add("btn-main");
        
        notify.getStyleClass().add("btn-notification");
        badge.getStyleClass().add("notification-badge");
        
        status.getStyleClass().add("error-label");
        status.setMaxWidth(Double.MAX_VALUE);
        status.setAlignment(Pos.CENTER);

        scene.getStylesheets().add(getClass().getResource("/View.css").toExternalForm());
    }
}