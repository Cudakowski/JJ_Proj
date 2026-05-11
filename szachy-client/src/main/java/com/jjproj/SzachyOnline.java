package com.jjproj;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SzachyOnline extends Application {

    public static Label status;

    @Override
    public void start(Stage stage) {
        status = new Label("Status");
        status.getStyleClass().add("error-label");
        status.setMaxWidth(Double.MAX_VALUE);
        status.setAlignment(Pos.CENTER);

        
        LoginView login = new LoginView();

        stage.setScene(login.createScene(stage));
        stage.setTitle("Szachy");
        stage.show();
    }

    public static void main(String[] args) {
        //launch(args);
        TestLogin.test();
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


