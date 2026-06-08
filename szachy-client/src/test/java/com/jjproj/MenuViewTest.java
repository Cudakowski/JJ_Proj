package com.jjproj;

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

@ExtendWith(ApplicationExtension.class)
public class MenuViewTest {

    

    // Ta metoda uruchamia wirtualne okienko przed każdym testem
    @Start
    public void start(Stage stage) {
        // Ważne: Inicjalizujemy obiekty statyczne potrzebne w MenuView
        SceneManager.setWindow(stage); 
        
        MenuView menuView = new MenuView();
        Scene scene = menuView.createScene(stage);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testMenuButtonsAreVisible() {
        // Szukamy elementów bezpośrednio po ich nazwach na ekranie
        // Zamiast ".button", używamy dokładnego tekstu
        FxAssert.verifyThat("Graj", NodeMatchers.isVisible());
        FxAssert.verifyThat("Statystyki", NodeMatchers.isVisible());
        FxAssert.verifyThat("Wyloguj", NodeMatchers.isVisible());
        FxAssert.verifyThat("Wyjście", NodeMatchers.isVisible());
    }

    @Test
    public void testTitleIsCorrect() {
        // Sprawdzamy czy etykieta tytułu ma odpowiedni tekst
        FxAssert.verifyThat("SZACHY", LabeledMatchers.hasText("SZACHY"));
    }

    @Test
    public void testClickingStatsButtonChangesScene(FxRobot robot) {
        // Upewniamy się, że jesteśmy w Menu i przycisk Statystyk istnieje
        FxAssert.verifyThat("Statystyki", NodeMatchers.isVisible());
        
        // Robot klika w przycisk Statystyk
        robot.clickOn("Statystyki");
        
        // Ponieważ Statystyki wysyłają GET_STATS w tle i ładują nowy widok,
        // sprawdzamy, czy w nowym widoku pojawił się napis charakterystyczny dla StatsView
        FxAssert.verifyThat("Szczegółowe statystyki", NodeMatchers.isVisible());
    }
}