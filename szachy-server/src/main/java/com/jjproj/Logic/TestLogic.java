package com.jjproj.Logic;

import java.util.Set;
import com.jjproj.Logic.piece.Piece;

public class TestLogic {
    public static void test(){
        Board board = new Board();
        board.setupDefaultPiecesPositions();

        BoardConsoleRenderer renderer= new BoardConsoleRenderer();
        renderer.render(board);
        Piece piece = board.getPiece(new Coordinates(File.B, 1));
        
        if (piece != null) {
            System.out.println("Figura na B1: " + piece.getClass().getSimpleName());
            Set<Coordinates> moves = piece.getAvailableMoveSquares(board);
            System.out.print("Dostępne ruchy: ");
            for (Coordinates move : moves) {
                System.out.print(move.file + "" + move.rank + " ");
            }
            System.out.println();
        }
    }
    // public static void testGameSession() {

    //     // создаём сессию с двумя игроками
    //     GameSession session = new GameSession("Tomek", "Anna");
        
    //     System.out.println("=== Stan początkowy ===");
    //     System.out.println(session.getInitialFEN());
        
    //     //белые ходят 
    //     String result = session.applyMove("E2", "E4", "Tomek");
    //     System.out.println("Biały E2→E4: " + result);
        
    //     //чёрные ходят
    //     result = session.applyMove("E7", "E5", "Anna");
    //     System.out.println("Czarny E7→E5: " + result);
        
    //     //белые ходят не в свою очередь
    //     result = session.applyMove("D2", "D4", "Tomek");
    //     System.out.println("Biały D2→D4: " + result);
        
    //     //чёрные пытаются нелегальный ход
    //     result = session.applyMove("E5", "E3", "Anna");
    //     System.out.println("Czarny nielegalny E5→E3: " + result);
        
    //     //тест en passant
    //     System.out.println("\n=== Test bicia w przelocie ===");
    //     GameSession epSession = new GameSession("Tomek", "Anna");
    //     epSession.applyMove("E2", "E4", "Tomek"); 
    //     epSession.applyMove("A7", "A6", "Anna");  
    //     epSession.applyMove("E4", "E5", "Tomek"); 
    //     epSession.applyMove("D7", "D5", "Anna"); 
    //     result = epSession.applyMove("E5", "D6", "Tomek"); 
    //     System.out.println("En passant E5→D6: " + result);
        
    //     // тест рокировки
    //     System.out.println("\n=== Test roszady ===");
    //     GameSession castleSession = new GameSession("Tomek", "Anna");
      
    //     castleSession.applyMove("E2", "E4", "Tomek");
    //     castleSession.applyMove("E7", "E5", "Anna");
    //     castleSession.applyMove("F1", "C4", "Tomek"); 
    //     castleSession.applyMove("A7", "A6", "Anna");
    //     castleSession.applyMove("G1", "F3", "Tomek");
    //     castleSession.applyMove("A6", "A5", "Anna");
    //     result = castleSession.applyMove("E1", "G1", "Tomek"); 
    //     System.out.println("Roszada E1→G1: " + result);
    // }
}
