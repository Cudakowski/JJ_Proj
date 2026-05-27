package com.jjproj;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView {

    public Scene createScene(Stage stage) {
        // --- Elementy tekstowe ---
        Label title = new Label("SZACHY");
        Label subtitle = new Label("logowanie");
        Label status = new Label("Status");

        // --- Pola wprowadzania ---
        TextField usernameField = new TextField();
        usernameField.setPromptText("Nazwa użytkownika");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Hasło");

        // --- Przyciski ---
        Button loginButton = new Button("Zaloguj się");
        Button registerButton = new Button("Zarejestruj się");
        Button exitButton = new Button("Wyjście");

        // --- Logika przycisków ---
        loginButton.setOnAction(e -> 
            NetworkManager.onLogin(usernameField.getText(), passwordField.getText())
        );

        registerButton.setOnAction(e -> {
            RegisterView registerView = new RegisterView();
            stage.setScene(registerView.createScene(stage));
        });

        exitButton.setOnAction(e -> stage.close());

        // --- Układ (Layout) ---
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox layout = new VBox(20, 
            title, 
            subtitle, 
            usernameField, 
            passwordField, 
            loginButton, 
            registerButton, 
            exitButton, 
            spacer, 
            status
        );
        layout.setAlignment(Pos.CENTER);

        // --- Scena i Stylizacja ---
        Scene scene = new Scene(layout, 600, 800);
        applyStyles(scene, layout, title, subtitle, loginButton, registerButton, exitButton, usernameField, passwordField, status);

        // --- Konfiguracja Stage ---
        stage.setMinWidth(400);
        stage.setMinHeight(500);

        SceneManager.registerStatusLabel(status);
        
        return scene;
    }

    private void applyStyles(Scene scene, VBox layout, Label title, Label subtitle, 
                             Button login, Button register, Button exit, 
                             TextField user, PasswordField pass, Label status) {
        
        // Klasy CSS
        layout.getStyleClass().add("root-gradient");
        title.getStyleClass().add("main-title");
        subtitle.getStyleClass().add("subtitle");
        
        login.getStyleClass().add("btn-main");
        register.getStyleClass().add("btn-main");
        exit.getStyleClass().add("btn-main");
        
        user.getStyleClass().add("text-field");
        pass.getStyleClass().add("password-field");
        
        status.getStyleClass().add("error-label");
        status.setMaxWidth(Double.MAX_VALUE);
        status.setAlignment(Pos.CENTER);

        // Arkusz stylów
        scene.getStylesheets().add(
            getClass().getResource("/View.css").toExternalForm()
        );
    }
}