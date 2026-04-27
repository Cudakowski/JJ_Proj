package com.jjproj.Logic;

import com.jjproj.Logic.piece.Piece;

public class TestLogic {
    public static void test(){
        Board board = new Board();
        board.setupDefaultPiecesPositions();

        BoardConsoleRenderer renderer= new BoardConsoleRenderer();
        renderer.render(board);

        Piece piece = board.getPiece(new Coordinates(File.B, 1));
        piece.getAvailableMoveSquares(board);
        
    }
}
