package com.jjproj.Logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    private Board board;

    @BeforeEach
    public void setUp() {
        // Ta metoda wykonuje się przed każdym testem, dając nam świeżą planszę
        board = new Board();
    }

    @Test
    public void testInitialBoardSetup() {
        // Arrange
        board.setupDefaultPiecesPositions();

        // Act
        Coordinates e2 = new Coordinates(File.E, 2);
        
        // Assert
        assertFalse(board.isSquareEmpty(e2), "Pole E2 nie powinno być puste na starcie");
        assertEquals(Color.WHITE, board.getPiece(e2).color, "Na E2 powinien stać biały pionek");
    }
}