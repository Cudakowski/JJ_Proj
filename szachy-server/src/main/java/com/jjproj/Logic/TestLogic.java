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
}
