package com.jjproj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
public class GameViewTest {

    private GameView gameView;

    @Start
    public void start(Stage stage) {
        SceneManager.setWindow(stage); 
        
        gameView = new GameView();
        Scene scene = gameView.createScene(stage, "Bialy", "Bot", "10min");
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testGameUiElementsAreVisible() {
        FxAssert.verifyThat("Powrót do menu", NodeMatchers.isVisible());
        
        Label timerLabel = FxAssert.assertContext().getNodeFinder().lookup(".timer-label").queryAs(Label.class);
        assertNotNull(timerLabel);
        assertTrue(timerLabel.isVisible());
    }



    @Test
    public void testUpdateBoardFromFENParsesCorrectly(FxRobot robot) {
        robot.interact(() -> {
            String startFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
            gameView.updateBoardFromFEN(startFEN);
        });

        assertEquals("♜", gameView.pieces[0][0]); 
        assertEquals("♟", gameView.pieces[1][4]); 
        assertEquals("", gameView.pieces[3][3]);  
        assertEquals("♙", gameView.pieces[6][3]);
        assertEquals("♖", gameView.pieces[7][7]); 
    }

@Test
    public void testClickingPieceHighlightsSquareAndShowsMoves(FxRobot robot) {
        robot.interact(() -> {
            gameView.pieces[6][4] = "♙";
            
            String customFEN = "8/8/8/8/8/8/4P3/8 w - - 0 1";
            gameView.updateBoardFromFEN(customFEN);
            
            gameView.updateLegalMoves("E2,E3,E4;");
        });

        robot.clickOn("♙");

        FxAssert.verifyThat("●", NodeMatchers.isVisible());
    }

    @Test
    public void testSyncTimeWithServerUpdatesTimerLabel(FxRobot robot) {
        robot.interact(() -> {

            gameView.syncTimeWithServer(240, 180);
        });

        FxAssert.verifyThat("BIAŁY 04:00  |  03:00 CZARNY", NodeMatchers.isVisible());
    }

    @Test
    public void testLeaveGameButtonReturnsToMenu(FxRobot robot) {
        robot.clickOn("Powrót do menu");
        
        FxAssert.verifyThat("Graj", NodeMatchers.isVisible());
    }
}