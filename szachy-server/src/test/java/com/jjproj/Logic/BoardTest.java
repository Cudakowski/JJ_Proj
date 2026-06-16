package com.jjproj.Logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.jjproj.Logic.piece.Bishop;
import com.jjproj.Logic.piece.King;
import com.jjproj.Logic.piece.Knight;
import com.jjproj.Logic.piece.Pawn;
import com.jjproj.Logic.piece.Piece;
import com.jjproj.Logic.piece.Queen;
import com.jjproj.Logic.piece.Rook;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

public class BoardTest {

    private Board board;

    @BeforeEach
    public void setUp() {
        // Ta metoda wykonuje się przed każdym testem, dając nam świeżą planszę
        board = new Board();
    }
    ///testy PLANSZY
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
    //czy plansza ma 32 figury
    @Test
    public void testInitialBoardHas32Pieces() {
        board.setupDefaultPiecesPositions();
        int count = 0;
        for(int rank = 1; rank <= 8; rank++) {
            for(File file : File.values()) {
                if(!board.isSquareEmpty(new Coordinates(file, rank))) {
                    count++;
                }
            }
        }
        assertEquals(32, count, "Na starcie powinno być 32 figury");
    }

    @Test
    @DisplayName("Czy puste pole zwraca null")
    public void testEmptySquareReturnsNull() {
        Coordinates e4 = new Coordinates(File.E, 4);
        assertNull(board.getPiece(e4), "Puste pole powinno zwracać null");
        assertTrue(board.isSquareEmpty(e4));
    }

    @Test
    @DisplayName("test dla setPiece i getPiece")
    public void testSetAndGetPiece() {

        Coordinates e4 = new Coordinates(File.E, 4);
        Pawn pawn = new Pawn(Color.WHITE, e4);
        board.setPiece(e4, pawn);

        assertFalse(board.isSquareEmpty(e4));
        assertInstanceOf(Pawn.class, board.getPiece(e4));
        assertEquals(Color.WHITE, board.getPiece(e4).color);
    }
    @Test
    @DisplayName("sprawdzenie removePiece")
    public void testRemovePiece() {
        Coordinates e4 = new Coordinates(File.E, 4);
        board.setPiece(e4, new Pawn(Color.WHITE, e4));
        board.removePiece(e4);
        assertTrue(board.isSquareEmpty(e4));
    }
    @Test
    @DisplayName("sprawdzenie movePiece")
    public void testMovePiece() {

        Coordinates from = new Coordinates(File.E, 2);
        Coordinates to = new Coordinates(File.E, 4);
        board.setPiece(from, new Pawn(Color.WHITE, from));
        board.movePiece(from, to);

        assertTrue(board.isSquareEmpty(from), "Pole startowe powinno być puste");
        assertFalse(board.isSquareEmpty(to),  "Pole docelowe powinno być zajęte");
        assertInstanceOf(Pawn.class, board.getPiece(to));
    }
    @Test
    @DisplayName(" sprawdzenie copy() - kopię planszy")
    public void testBoardCopy() {
        board.setupDefaultPiecesPositions();
        Board copy = board.copy();

        // modyfikujemy kopię
        Coordinates e2 = new Coordinates(File.E, 2);
        copy.removePiece(e2);

        // oryginał nie zmieniony
        assertFalse(board.isSquareEmpty(e2), "Oryginalna plansza nie powinna być zmieniona");
        assertTrue(copy.isSquareEmpty(e2),"Kopia powinna mieć usunięty pionek");
    }
    //========Testy FIGUR=======
    // Testy Knight
    @Test
    @DisplayName("Skoczek na B1 ma 2 ruchy na starcie")
    public void testKnightOnB1HasTwoMoves() {

        board.setupDefaultPiecesPositions();
        Piece knight = board.getPiece(new Coordinates(File.B, 1));

        Set<Coordinates>moves = knight.getAvailableMoveSquares(board);

        assertEquals(2, moves.size());
        assertTrue(moves.contains(new Coordinates(File.A, 3)));
        assertTrue(moves.contains(new Coordinates(File.C, 3)));
    }

    @Test
    @DisplayName("Skoczek w centrum pustej planszy ma 8 ruchów")
    public void testKnightInCenterHasEightMoves() {
        Coordinates d4 = new Coordinates(File.D, 4);
        board.setPiece(d4, new Knight(Color.WHITE, d4));

        Set<Coordinates>moves = board.getPiece(d4).getAvailableMoveSquares(board);
        assertEquals(8, moves.size());
    }
    @Test
    @DisplayName("Skoczek moze pszeskakiwac przez figury")
    public void testKnightJumpsOverPieces() {
        board.setupDefaultPiecesPositions();
        Piece knight = board.getPiece(new Coordinates(File.B, 1));

        Set<Coordinates>moves = knight.getAvailableMoveSquares(board);
        assertFalse(moves.isEmpty(),"Skoczek powinien móc skoczyć mimo otoczenia");
    }
    // testy pionka----------
    @Test
    @DisplayName("Pionek na starcie ma 2 ruchy do przodu")
    public void testPawnFromStartHasTwoMoves() {

        Coordinates e2 = new Coordinates(File.E, 2);
        board.setPiece(e2, new Pawn(Color.WHITE, e2));
        Set<Coordinates>moves = board.getPiece(e2).getAvailableMoveSquares(board);
        assertEquals(2, moves.size());
        assertTrue(moves.contains(new Coordinates(File.E, 3)));
        assertTrue(moves.contains(new Coordinates(File.E, 4)));
    }
    @Test
    @DisplayName("Pionek zablokowany z przodu nie ma ruchów")
    public void testPawnBlockedHasNoMoves() {

        Coordinates e2 = new Coordinates(File.E, 2);
        Coordinates e3 = new Coordinates(File.E, 3);
        board.setPiece(e2, new Pawn(Color.WHITE, e2));
        board.setPiece(e3, new Pawn(Color.BLACK, e3));

        Set<Coordinates>moves = board.getPiece(e2).getAvailableMoveSquares(board);
        assertEquals(0, moves.size());
    }
    @Test
    @DisplayName("Pionek bije po skosie")
    public void testPawnCapturesDiagonally() {

        Coordinates e2 = new Coordinates(File.E, 2);
        Coordinates d3 = new Coordinates(File.D, 3);
        board.setPiece(e2, new Pawn(Color.WHITE, e2));
        board.setPiece(d3, new Pawn(Color.BLACK, d3));

        Set<Coordinates>moves = board.getPiece(e2).getAvailableMoveSquares(board);
        assertTrue(moves.contains(d3), "Pionek powinien móc bić na D3");
        
        assertEquals(3, moves.size()); 
    }
    @Test
    @DisplayName("Pionek nie bije pustego pola po skosie")
    public void testPawnCannotMoveDiagonallyToEmpty() {
        Coordinates e2 = new Coordinates(File.E, 2);
        board.setPiece(e2, new Pawn(Color.WHITE, e2));

        Set<Coordinates>moves = board.getPiece(e2).getAvailableMoveSquares(board);

        assertFalse(moves.contains(new Coordinates(File.D, 3)));
        assertFalse(moves.contains(new Coordinates(File.F, 3)));
    }
    //testy wiezy----------
    @Test
    @DisplayName("Wieża zablokowana przez własną figurę")
    public void testRookBlockedByOwnPiece() {
        Coordinates a1 = new Coordinates(File.A, 1);
        Coordinates a4 = new Coordinates(File.A, 4);
        board.setPiece(a1, new Rook(Color.WHITE, a1));
        board.setPiece(a4, new Pawn(Color.WHITE, a4));

        Set<Coordinates>moves = board.getPiece(a1).getAvailableMoveSquares(board);

        assertEquals(9, moves.size());
        assertFalse(moves.contains(a4), "Nie można wejść na własną figurę");
        assertFalse(moves.contains(new Coordinates(File.A, 5)));
    }
    @Test
    @DisplayName("Wieża bije figurę przeciwnika i nie idzie dalej")
    public void testRookCapturesAndStops() {
        Coordinates a1 = new Coordinates(File.A, 1);
        Coordinates a4 = new Coordinates(File.A, 4);
        board.setPiece(a1, new Rook(Color.WHITE, a1));
        board.setPiece(a4, new Pawn(Color.BLACK, a4));

        Set<Coordinates>moves = board.getPiece(a1).getAvailableMoveSquares(board);

        assertTrue(moves.contains(a4),  "Wieża powinna móc zbić na A4");
        assertFalse(moves.contains(new Coordinates(File.A, 5)),"Wieża nie powinna iść za zbitą figurą");
    }
    @Test
    @DisplayName("Król pod szachem od wieży")
    public void testKingInCheckByRook() {

        board.setPiece(new Coordinates(File.E, 1),new King(Color.WHITE, new Coordinates(File.E, 1)));
        board.setPiece(new Coordinates(File.E, 8),new Rook(Color.BLACK, new Coordinates(File.E, 8)));

        assertTrue(GameStateChecker.isKingInCheck(Color.WHITE, board));
    }
    @Test
    @DisplayName("Król nie jest pod szachem")
    public void testKingNotInCheck() {

        board.setPiece(new Coordinates(File.E, 1),new King(Color.WHITE, new Coordinates(File.E, 1)));
        board.setPiece(new Coordinates(File.A, 8),new Rook(Color.BLACK, new Coordinates(File.A, 8)));

        assertFalse(GameStateChecker.isKingInCheck(Color.WHITE, board));
    }
    //testy mata i pata
    @Test
    @DisplayName("Mat przez dziecięcego mata")
    public void testScholarsMate() {
        board.setPiece(new Coordinates(File.E, 1), new King(Color.WHITE, new Coordinates(File.E, 1)));
        board.setPiece(new Coordinates(File.D, 1), new Rook(Color.WHITE, new Coordinates(File.D, 1))); 
        board.setPiece(new Coordinates(File.F, 1), new Rook(Color.WHITE, new Coordinates(File.F, 1)));
        board.setPiece(new Coordinates(File.D, 2), new Pawn(Color.WHITE, new Coordinates(File.D, 2)));
        board.setPiece(new Coordinates(File.F, 2), new Pawn(Color.WHITE, new Coordinates(File.F, 2))); 
        board.setPiece(new Coordinates(File.E, 8), new King(Color.BLACK, new Coordinates(File.E, 8)));
        board.setPiece(new Coordinates(File.E, 4), new Queen(Color.BLACK, new Coordinates(File.E, 4))); 

        assertTrue(GameStateChecker.isKingInCheck(Color.WHITE, board), "Król powinien być pod szachem");
        assertTrue(GameStateChecker.isCheckMate(Color.WHITE, board), "Powinna być sytuacja mata");
    }
    @Test
    @DisplayName("Pat — brak ruchów ale nie ma szacha")
    public void testStalemate() {
        board.setPiece(new Coordinates(File.A, 8),new King(Color.WHITE, new Coordinates(File.A, 8)));
        board.setPiece(new Coordinates(File.B, 6),new Queen(Color.BLACK, new Coordinates(File.B, 6)));
        board.setPiece(new Coordinates(File.C, 7),new King(Color.BLACK, new Coordinates(File.C, 7)));

        assertFalse(GameStateChecker.isKingInCheck(Color.WHITE, board),"Król nie powinien być pod szachem");
        assertTrue(GameStateChecker.isCheckPate(Color.WHITE, board),"Powinna być sytuacja pata");
    }
    // testy promocji

    @Test
    @DisplayName("Pionek na ostatnim rzędzie powinien być promowany")
    public void testPawnShouldPromote() {
        Coordinates e8 = new Coordinates(File.E, 8);
        Pawn pawn = new Pawn(Color.WHITE, e8);
        board.setPiece(e8, pawn);

        assertTrue(GameStateChecker.shouldPromote(pawn, e8));
    }
    @Test
    @DisplayName("Pionek w środku planszy nie jest promowany")
    public void testPawnShouldNotPromoteInMiddle() {
        Coordinates e4 = new Coordinates(File.E, 4);
        Pawn pawn = new Pawn(Color.WHITE, e4);
        board.setPiece(e4, pawn);

        assertFalse(GameStateChecker.shouldPromote(pawn, e4));
    }
    @Test
    @DisplayName("Promocja zamienia pionka na hetmana")
    public void testPromotePawnToQueen() {
        Coordinates e8 = new Coordinates(File.E, 8);
        board.setPiece(e8, new Pawn(Color.WHITE, e8));

        Queen queen = new Queen(Color.WHITE, e8);
        GameStateChecker.promotePawn(e8, queen, board);

        assertInstanceOf(Queen.class, board.getPiece(e8));
        assertEquals(Color.WHITE, board.getPiece(e8).color);
    }
    /// testy bicia w pszelocie
    @Test
    @DisplayName("Pionek może bić w przelocie")
    public void testEnPassantAvailable() {

        Coordinates e5 = new Coordinates(File.E, 5);
        Coordinates d5 = new Coordinates(File.D, 5);
        Coordinates d6 = new Coordinates(File.D, 6);

        board.setPiece(e5, new Pawn(Color.WHITE, e5));
        board.setPiece(d5, new Pawn(Color.BLACK, d5));
        board.setEnPassantTarget(d6);

        Set<Coordinates>moves = board.getPiece(e5).getAvailableMoveSquares(board);
        assertTrue(moves.contains(d6),"Pionek powinien móc bić w przelocie na D6");
    }
    @Test
    @DisplayName("Pionek nie może bić w przelocie gdy brak enPassantTarget")
    public void testEnPassantNotAvailableWithoutTarget() {
        Coordinates e5 = new Coordinates(File.E, 5);
        Coordinates d5 = new Coordinates(File.D, 5);
        Coordinates d6 = new Coordinates(File.D, 6);

        board.setPiece(e5, new Pawn(Color.WHITE, e5));
        board.setPiece(d5, new Pawn(Color.BLACK, d5));
        board.setEnPassantTarget(null); //nie można bić w przelocie
        Set<Coordinates>moves = board.getPiece(e5).getAvailableMoveSquares(board);

        assertFalse(moves.contains(d6),"Nie powinno być bicia w przelocie bez enPassantTarget");
    }
    //testy roszady
    @Test
    @DisplayName("Krótka roszada dostępna gdy droga wolna")
    public void testKingsideCastlingAvailable() {
        Coordinates e1 = new Coordinates(File.E, 1);
        Coordinates h1 = new Coordinates(File.H, 1);
        Coordinates g1 = new Coordinates(File.G, 1);

        board.setPiece(e1, new King(Color.WHITE, e1));
        board.setPiece(h1, new Rook(Color.WHITE, h1));
       
        Set<Coordinates>moves = GameStateChecker.getLegalMoves(board.getPiece(e1), e1, board);

        assertTrue(moves.contains(g1),"Krótka roszada powinna być dostępna");
    }
    @Test
    @DisplayName("Roszada niedostępna gdy droga zablokowana")
    public void testCastlingBlockedByPiece() {
        Coordinates e1 = new Coordinates(File.E, 1);
        Coordinates f1 = new Coordinates(File.F, 1);
        Coordinates h1 = new Coordinates(File.H, 1);
        Coordinates g1 = new Coordinates(File.G, 1);

        board.setPiece(e1, new King(Color.WHITE, e1));
        board.setPiece(h1, new Rook(Color.WHITE, h1));
        board.setPiece(f1, new Bishop(Color.WHITE, f1));//blokuje drogę

        Set<Coordinates>moves = GameStateChecker.getLegalMoves(board.getPiece(e1), e1, board);

        assertFalse(moves.contains(g1),"Roszada nie powinna być dostępna gdy droga zablokowana");
    }
    @Test
    @DisplayName("Roszada niedostępna gdy król już chodził")
    public void testCastlingNotAvailableAfterKingMoved() {
        Coordinates e1 = new Coordinates(File.E, 1);
        Coordinates h1 = new Coordinates(File.H, 1);
        Coordinates g1 = new Coordinates(File.G, 1);

        board.setPiece(e1, new King(Color.WHITE, e1));
        board.setPiece(h1, new Rook(Color.WHITE, h1));
        board.revokeCastlingRight(Color.WHITE, true);// król już chodził

        Set<Coordinates>moves = GameStateChecker.getLegalMoves(board.getPiece(e1), e1, board);
        assertFalse(moves.contains(g1),"Roszada nie powinna być dostępna po ruchu króla");
    }
    @Test
    @DisplayName("Roszada niedostępna gdy król przechodzi przez szach")
    public void testCastlingNotAvailableThroughCheck() {
        Coordinates e1 = new Coordinates(File.E, 1);
        Coordinates h1 = new Coordinates(File.H, 1);
        Coordinates f8 = new Coordinates(File.F, 8);
        Coordinates g1 = new Coordinates(File.G, 1);

        board.setPiece(e1, new King(Color.WHITE, e1));
        board.setPiece(h1, new Rook(Color.WHITE, h1));
        board.setPiece(f8, new Rook(Color.BLACK, f8)); // atak f1
        board.setPiece(new Coordinates(File.E, 8),new King(Color.BLACK, new Coordinates(File.E, 8)));

        Set<Coordinates>moves = GameStateChecker.getLegalMoves(board.getPiece(e1), e1, board);

        assertFalse(moves.contains(g1),"Roszada nie powinna być dostępna gdy król przechodzi przez szach");
    }





}