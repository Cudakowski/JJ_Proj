package com.jjproj;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;


public class LoginView {

    public Scene createScene(Stage stage) {
        Label title = new Label("SZACHY");
        Label subtitle = new Label("logowanie");

        title.setStyle(
            "-fx-font-size: 72px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: beige;" +
            "-fx-background-color: brown;" +
            "-fx-padding: 10; "+
            "-fx-background-radius: 10;"
        );

        subtitle.setStyle(
            "-fx-font-size: 26px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #d9c7a3;" +
            "-fx-letter-spacing: 2px;" +
            "-fx-padding: 5 0 15 0;"
        );

        TextField usernameField = new TextField();
        usernameField.setPromptText("Nazwa uzytkownika");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Hasło");

        Button loginButton = new Button("Zaloguj");
        Button exit = new Button("Wyjście");


        String buttonStyle =
        "-fx-font-size: 18px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: white;" +
        "-fx-background-color: #333;" +
        "-fx-background-radius: 10;" +
        "-fx-padding: 10 30 10 30;";

        loginButton.setStyle(buttonStyle);



        loginButton.setOnAction(e -> {
            MenuView menuView = new MenuView();
            stage.setScene(menuView.createScene(stage));
        });


        exit.setOnAction(e -> stage.close());

        VBox layout = new VBox(20, title, subtitle, usernameField, passwordField, loginButton, exit);
        layout.setAlignment(Pos.CENTER); 
        layout.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #111, #222);" +
                "-fx-padding: 40;"
        );

        String hover =
            "-fx-background-color: #555;";

            loginButton.setOnMouseEntered(e -> loginButton.setStyle(buttonStyle + hover));
            loginButton.setOnMouseExited(e -> loginButton.setStyle(buttonStyle));

            exit.setOnMouseEntered(e -> exit.setStyle(buttonStyle + hover));
            exit.setOnMouseExited(e -> exit.setStyle(buttonStyle));

            usernameField.setStyle(buttonStyle);
            passwordField.setStyle(buttonStyle);
            exit.setStyle(buttonStyle);




            Scene scene = new Scene(layout, 600, 600);

            stage.setScene(scene);

            stage.setMinWidth(400);
            stage.setMinHeight(500);

            return scene;

    }
}
