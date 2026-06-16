package com.jjproj;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import javafx.scene.Scene;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class MenuViewTest {


    @Start
    public void start(Stage stage) {
        SceneManager.setWindow(stage); 
        
        MenuView menuView = new MenuView();
        Scene scene = menuView.createScene(stage);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testMenuButtonsAreVisible() {
        FxAssert.verifyThat("Graj", NodeMatchers.isVisible());
        FxAssert.verifyThat("Statystyki", NodeMatchers.isVisible());
        FxAssert.verifyThat("Wyloguj", NodeMatchers.isVisible());
        FxAssert.verifyThat("Wyjście", NodeMatchers.isVisible());
    }

    @Test
    public void testTitleIsCorrect() {
        FxAssert.verifyThat("SZACHY", LabeledMatchers.hasText("SZACHY"));
    }

    @Test
    public void testClickingStatsButtonChangesScene(FxRobot robot) {
        FxAssert.verifyThat("Statystyki", NodeMatchers.isVisible());
        
        robot.clickOn("Statystyki");
        
        FxAssert.verifyThat("Szczegółowe statystyki", NodeMatchers.isVisible());
    }
}