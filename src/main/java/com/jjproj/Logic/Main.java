package com.jjproj.Logic;

public class Main {
    public static void main(String[] args){
        Board board = new Board();
        board.setupDefaultPiecesPositions();

        BoardConsoleRenderer renderer= new BoardConsoleRenderer();
        renderer.render(board);
    }
}
