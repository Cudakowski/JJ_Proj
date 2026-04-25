package com.jjproj.Logic;

import com.jjproj.Logic.piece.Piece;

public class MyMain {
    public static void main(String[] args){
        Board board = new Board();
        board.setupDefaultPiecesPositions();

        BoardConsoleRenderer renderer= new BoardConsoleRenderer();
        renderer.render(board);

        Piece piece = board.getPiece(new Coordinates(File.B, 1));
        piece.getAvailableMoveSquares(board);
        
    }
}
