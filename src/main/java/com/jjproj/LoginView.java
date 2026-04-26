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
        // Tytuł
        Label title = new Label("SZACHY");

        // Nagłowek
        Label subtitle = new Label("logowanie");

        // Pole do wpisania loginu
        TextField usernameField = new TextField();

        // Jest taki poczatkowy tekst, zeby uzytkownik wiedzial ze tutaj jest do wpisania loginu
        usernameField.setPromptText("Nazwa uzytkownika");

        // Pole do wpisania hasła
        PasswordField passwordField = new PasswordField();

        // Jest taki poczatkowy tekst, zeby uzytkownik wiedzial ze tutaj jest haslo
        passwordField.setPromptText("Hasło");

        // Przycisk logowania
        Button loginButton = new Button("Zaloguj");

        // Klikniecie z logowania - jak narazie do menu - dodac bazy danych
        loginButton.setOnAction(e -> {
            MenuView menuView = new MenuView();
            stage.setScene(menuView.createScene(stage));
        });


        // Przycisk wyjścia
        Button exit = new Button("Wyjście");

        // Jak klikniemy to wywala nas z szachow
        exit.setOnAction(e -> stage.close());


        // Wszystkie te elementy bedą tak jeden pod drugim, dlatego wybralam VBox

        VBox layout = new VBox(20, title, subtitle, usernameField, passwordField, loginButton, exit);

        // Bedzie to wszystko pośrodku okna
        layout.setAlignment(Pos.CENTER); 

        // Dodaje te elementy do okna
        Scene scene = new Scene(layout, 600, 600);

        // Dodaje okno do roota
        stage.setScene(scene);

        // Ustawianie wygladu - dodalam plik css zeby byl jeden uniwersalny (to wszystko bedzie eleganko pasowac i mozemy uzywac wielokrotnie)

        title.getStyleClass().add("main-title");
        subtitle.getStyleClass().add("subtitle");
        layout.getStyleClass().add("root-gradient");

        loginButton.getStyleClass().add("btn-main");
        exit.getStyleClass().add("btn-main");

        usernameField.getStyleClass().add("text-field");
        passwordField.getStyleClass().add("password-field");

        scene.getStylesheets().add(
                getClass().getResource("/view.css").toExternalForm()
        );

        
        // ustawianie minimalnego rozmiaru okna
        stage.setMinWidth(400);
        stage.setMinHeight(500);

        
        return scene;

    }
}
