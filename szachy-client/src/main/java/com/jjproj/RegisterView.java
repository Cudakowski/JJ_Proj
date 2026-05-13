package com.jjproj;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class RegisterView {
    
    public Scene createScene(Stage stage) {
        // Tytuł
        Label title = new Label("SZACHY");

        Label status = new Label("Status");
        status.getStyleClass().add("error-label");
        status.setMaxWidth(Double.MAX_VALUE);
        status.setAlignment(Pos.CENTER);


        Label subtitle = new Label("rejestracja");


        TextField usernameField = new TextField();


        usernameField.setPromptText("Nazwa użytkownika");


        PasswordField passwordField = new PasswordField();


        passwordField.setPromptText("Hasło");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Powtórz hasło");

        // Przycisk logowania
        Button loginButton = new Button("Zaloguj się");

        Button registerButton = new Button("Zarejestruj się");

        registerButton.setOnAction(e -> {
            // MenuView menuView = new MenuView();
            // stage.setScene(menuView.createScene(stage));
            NetworkManager.onRegister(usernameField.getText(), passwordField.getText(),confirmPasswordField.getText());
        });


        // Klikniecie z logowania - jak narazie do menu - dodac bazy danych
        loginButton.setOnAction(e -> {
            LoginView loginView = new LoginView();
            stage.setScene(loginView.createScene(stage));
        });


        // Przycisk wyjścia
        Button exit = new Button("Wyjście");

        // Jak klikniemy to wywala nas z szachow
        exit.setOnAction(e -> stage.close());

        Region spacer = new Region();
        VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);


        // Wszystkie te elementy bedą tak jeden pod drugim, dlatego wybralam VBox

        VBox layout = new VBox(20, title, subtitle, usernameField, passwordField, confirmPasswordField, registerButton, loginButton, exit, spacer, status);


        // Bedzie to wszystko pośrodku okna
        layout.setAlignment(Pos.CENTER); 

        // Dodaje te elementy do okna
        Scene scene = new Scene(layout, 600, 800);

        // Dodaje okno do roota
        stage.setScene(scene);

        // Ustawianie wygladu - dodalam plik css zeby byl jeden uniwersalny (to wszystko bedzie eleganko pasowac i mozemy uzywac wielokrotnie)

        title.getStyleClass().add("main-title");
        subtitle.getStyleClass().add("subtitle");
        layout.getStyleClass().add("root-gradient");

        loginButton.getStyleClass().add("btn-main");
        registerButton.getStyleClass().add("btn-main");
        exit.getStyleClass().add("btn-main");

        usernameField.getStyleClass().add("text-field");
        passwordField.getStyleClass().add("password-field");
        confirmPasswordField.getStyleClass().add("password-field");


        scene.getStylesheets().add(
                getClass().getResource("/View.css").toExternalForm()
        );

        
        // ustawianie minimalnego rozmiaru okna
        stage.setMinWidth(400);
        stage.setMinHeight(500);

        SceneManager.registerStatusLabel(status);
        
        return scene;

    }
}
