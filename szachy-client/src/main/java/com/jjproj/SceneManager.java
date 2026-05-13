package com.jjproj;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class SceneManager {

    public static Label status;

    private static Stage mainStage;

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
        mainStage = stage;
    }

    

    public static void switchToMenu() {
        Platform.runLater(() -> {
            if (mainStage != null) {
                MenuView menu = new MenuView(); 
                mainStage.setScene(menu.createScene(mainStage));
            }
        });
    }
}
