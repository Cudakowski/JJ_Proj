package com.jjproj;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SceneManager {

    public static Label status;

    private static Stage glowneOkno;

    public static void registerStatusLabel(Label label) {
        status = label;
    }

    public static void setStatus(String s) {
        Platform.runLater(() -> {
            if (status != null) {
                status.setText(s);
            } else {
                System.out.println("[STATUS UI UKRYTY]: " + s);
            }
        });
    }

    // tylko przy starcie
    public static void setWindow(Stage stage) {
        glowneOkno = stage;
    }

    

    public static void switchToMenu() {
        Platform.runLater(() -> {
            if (glowneOkno != null) {
                // Zakładam, że Emilia stworzy klasę MenuView na wzór LoginView
                MenuView menu = new MenuView(); 
                glowneOkno.setScene(menu.createScene(glowneOkno));
                // Opcjonalnie zmiana rozmiaru lub tytułu okna:
                // glowneOkno.setTitle("Szachy - Menu Główne");
            }
        });
    }

    // Możesz tu dodawać kolejne metody w przyszłości
    public static void switchToGame() {
        Platform.runLater(() -> {
            // GameView gra = new GameView();
            // glowneOkno.setScene(gra.createScene(glowneOkno));
        });
    }
}
