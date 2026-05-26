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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MenuView {

    public Scene createScene(Stage stage) {

        // Powiadomienia
        

        Image bellImage = new Image(
        getClass().getResourceAsStream("/bell.png")
    );

        ImageView bellIcon = new ImageView(bellImage);

        bellIcon.setFitWidth(20);
        bellIcon.setFitHeight(20);

        Button notifications = new Button();
        notifications.setGraphic(bellIcon);

        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);


        notifications.setOnAction(e -> {
            SceneManager.setBadgeVisibility(false);

            NotificationsView notificationsView = new NotificationsView();
            stage.setScene(notificationsView.createScene(stage));
        });

        Label status = new Label("Status");
        status.getStyleClass().add("error-label");
        status.setMaxWidth(Double.MAX_VALUE);
        status.setAlignment(Pos.CENTER);
        

        Label badge = new Label();
        
        //badge.setVisible(true); // jako test widoczne

        StackPane notificationWrapper = new StackPane(notifications, badge);
        StackPane.setAlignment(badge, Pos.BOTTOM_LEFT);
        notificationWrapper.setPadding(new Insets(5));

        HBox topBar = new HBox(notificationWrapper);
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.setPadding(new Insets(10));




        Label title = new Label("SZACHY");


        //przyciski

        Button newGame = new Button("Graj");
        Button stats = new Button("Statystyki"); // TODO: pobierz statystyki
        Button logOut = new Button("Wyloguj"); 
        Button exit = new Button("Wyjście");

        newGame.setOnAction(e -> {
            PreGameView gameView = new PreGameView();
            stage.setScene(gameView.createScene(stage));
        });

        stats.setOnAction(e -> {
            StatsView statsView = new StatsView();
            stage.setScene(statsView.createScene(stage));
        });

        logOut.setOnAction(e -> {
            SceneManager.setStatus("Wylogowywanie");
            Thread logOuThread = new Thread(()->{
                NetworkManager.disconnect();
                Platform.runLater(() -> {
                    LoginView loginView = new LoginView();
                    stage.setScene(loginView.createScene(stage));
                });
            });

            logOuThread.setDaemon(true);
            logOuThread.start();
        });

        exit.setOnAction(e -> stage.close());

        VBox menu = new VBox(20, title, newGame, stats, logOut, exit, spacer,status);
        menu.setAlignment(Pos.CENTER);



        // spacery

        Region spacerTop = new Region();
        VBox.setVgrow(spacerTop, javafx.scene.layout.Priority.ALWAYS);

        Region spacerBottom = new Region();
        VBox.setVgrow(spacerBottom, javafx.scene.layout.Priority.ALWAYS);

        VBox root = new VBox();
        root.getChildren().addAll(topBar, spacerTop, menu, spacerBottom);

        Scene scene = new Scene(root, 600, 600);





        title.getStyleClass().add("main-title");
        newGame.getStyleClass().add("btn-main");
        stats.getStyleClass().add("btn-main");
        logOut.getStyleClass().add("btn-main");
        exit.getStyleClass().add("btn-main");
        notifications.getStyleClass().add("btn-notification");
        badge.getStyleClass().add("notification-badge");
        root.getStyleClass().add("root-gradient");

        scene.getStylesheets().add(
            getClass().getResource("/View.css").toExternalForm()
        );

        stage.setMinWidth(400);
        stage.setMinHeight(500);

        SceneManager.registerStatusLabel(status);
        SceneManager.registerNotificationBadge(badge);

        return scene;
    }
}
