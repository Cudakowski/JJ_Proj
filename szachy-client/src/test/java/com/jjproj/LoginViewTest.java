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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class LoginViewTest {

    @Start
    public void start(Stage stage) {
        SceneManager.setWindow(stage); 
        
        LoginView loginView = new LoginView();
        Scene scene = loginView.createScene(stage);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testLoginElementsAreVisible() {

        FxAssert.verifyThat("SZACHY", LabeledMatchers.hasText("SZACHY"));
        FxAssert.verifyThat("logowanie", LabeledMatchers.hasText("logowanie"));
        FxAssert.verifyThat("Zaloguj się", NodeMatchers.isVisible());
        FxAssert.verifyThat("Zarejestruj się", NodeMatchers.isVisible());
        FxAssert.verifyThat("Wyjście", NodeMatchers.isVisible());
    }

    @Test
    public void testTypingInCredentialsFields(FxRobot robot) {

        TextField usernameField = robot.lookup(".text-field").queryAs(TextField.class);
        PasswordField passwordField = robot.lookup(".password-field").queryAs(PasswordField.class);

        robot.clickOn(usernameField).write("testowyGracz");
        robot.clickOn(passwordField).write("SuperTajneHaslo123");

        org.junit.jupiter.api.Assertions.assertEquals("testowyGracz", usernameField.getText());
        org.junit.jupiter.api.Assertions.assertEquals("SuperTajneHaslo123", passwordField.getText());
    }

    @Test
    public void testClickingRegisterButtonChangesScene(FxRobot robot) {

        FxAssert.verifyThat("Zarejestruj się", NodeMatchers.isVisible());

        robot.clickOn("Zarejestruj się");

    }

}