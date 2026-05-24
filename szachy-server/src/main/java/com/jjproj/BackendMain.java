package com.jjproj;

public class BackendMain {

    public static void main(String[] args) {
    System.out.println("Start to play!\n");
        
        // Board board = new Board();
        // board.setupDefaultPiecesPositions();
        
        // Game game = new Game(board);
        // game.startGame();
        //TestLogic.test();
        //TestLogic.testGameSession();
        Server.startServer();
    }
}
