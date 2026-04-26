package com.jjproj;

import javafx.application.Application;
import javafx.stage.Stage;

public class SzachyOnline extends Application {

    @Override
    public void start(Stage stage) {
        LoginView login = new LoginView();
        stage.setScene(login.createScene(stage));
        stage.setTitle("Szachy");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}



// logowanie
// przez serwer poprawnosc wynikow 

// zapisywanie gry
// u kogo gra sie zapisuje 

// wgladzenie, zeby nie wpijac ip

// podchodze do komputera i widze liste potencjalnych graczy
// napisac o tym sprawozdanie, zasieg funkcjonalny gry
// co bedzie wysylane miedzy klientami a serwerem ,jak to bedzie wygladalo
// jak to zorganizoac w plikach 

// rankingi


