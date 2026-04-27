package com.jjproj;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.scene.control.Label;


public class StatsView {

    public Scene createScene(Stage stage) {

        // Ustawiam roota - bedzie borderpane zeby moc dawac po bokach rozne info, jeszce do konca nie mam pelnego pomyslu jak to bedzie ale cos sie wymysli
        BorderPane root = new BorderPane();

        root.setPadding(new Insets(20));

    // TOP

        
    
        
        // Tytul okienka - statystyki!
        Label title = new Label("Statystyki");


        HBox topBox = new HBox(title);

        topBox.setAlignment(Pos.CENTER);

        topBox.setPadding(new Insets(10));
        
        // Dodaje na gore naszego roota ten tytul
        root.setTop(topBox);

    // CENTER

        // Tworze liste naszych gier
        ListView<String> gamesList = new ListView<>();

        // Dodawanie gier do listy, na razie bez funkcjonalnosci, tak poglądowo
        gamesList.getItems().addAll(
                "Gra 1 - wygrana",
                "Gra 2 - przerwana",
                "Gra 3 - przegrana",
                "Dokonczyc"
        );


        // dodaje na srodek listę gier
        root.setCenter(gamesList);

    // BOTTOM

        // Przycisk wyjscia do menu
        Button back = new Button("Powrót do menu");
        
        // Jak klikniemy przenosi nas do menu
        back.setOnAction(e -> {
            MenuView menu = new MenuView();
            stage.setScene(menu.createScene(stage));
        });



        // Przycisk powrotu do menu daje na sam dół (tworze HBox i do niego dala, - moze tam kilka opcji by jeszcze dodac)

        HBox bottom = new HBox(back);
        bottom.setAlignment(Pos.CENTER);
        root.setBottom(bottom);

    // SCENE

        // Tworze nowa scene i dodaje nasz root - borderpane do niej
        Scene scene = new Scene(root, 600, 600);

        // Ustawiam scene
        stage.setScene(scene);

        // Stylizowanie elementów
        
        root.getStyleClass().add("root-dark");
        title.getStyleClass().add("status-label");
        gamesList.getStyleClass().add("list-view");
        back.getStyleClass().add("btn-main");
        scene.getStylesheets().add(
                getClass().getResource("/view.css").toExternalForm()
        );

        // Minnimalny rozmiar okna

        stage.setMinWidth(400);
        stage.setMinHeight(500);

        return scene;
    }
}

