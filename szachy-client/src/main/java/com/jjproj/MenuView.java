package com.jjproj;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class MenuView {

    public Scene createScene(Stage stage) {

        // Tytul naszych szachów :3
        Label title = new Label("SZACHY");

        // Przycisk rozpoczęcia gry
        Button newGame = new Button("Nowa gra");

        // Jak klikniemy to przenosi nas do gry
        newGame.setOnAction(e -> {
            GameView gameView = new GameView();
            stage.setScene(gameView.createScene(stage));
        });


        // Przycisk przejscia do statystyk
        Button stats = new Button("Statystyki");


        // Przechodzimy do statystyk po kliknięciu
        stats.setOnAction(e -> {
            StatsView statsView = new StatsView();
            stage.setScene(statsView.createScene(stage));
        });


        // Przycisk wylogowania
        Button logOut = new Button("Wyloguj");

        // Jak klikniemy to przenosi nas do strony logowania 
        // !!! Trzeba tu porawic jak będą bazy danych ogarnięte
        logOut.setOnAction(e-> {
            LoginView loginView = new LoginView();
            stage.setScene(loginView.createScene(stage));
        });


        // Przycisk wyjścia
        Button exit = new Button("Wyjście");

        // Jak klikniemy wywala nas z programu
        exit.setOnAction(e -> stage.close());


        // Tutaj robie Uklad wspolny taki zeby bylo jeden po drugim na dół
        VBox layout = new VBox(20, title, newGame, stats, logOut, exit);

        // Ustawiam to pośrodku okna
        layout.setAlignment(Pos.CENTER); 

        // Tworzenie nowego okna, jego poczatkowy rozmiar, dodajemy nasz layout
        Scene scene = new Scene(layout, 600, 600);

        // dodaje okno do roota
        stage.setScene(scene);

        // Stylizwoanie

        title.getStyleClass().add("main-title");
        newGame.getStyleClass().add("btn-main");
        stats.getStyleClass().add("btn-main");
        logOut.getStyleClass().add("btn-main");
        exit.getStyleClass().add("btn-main");
        layout.getStyleClass().add("root-gradient");

        scene.getStylesheets().add(
            getClass().getResource("/View.css").toExternalForm()
        );
            

        // Ustawiam minimalny rozmiar okna
        stage.setMinWidth(400);
        stage.setMinHeight(500);

        
        
        return scene;

    }
}
