package com.jjproj;

import com.jjproj.Logic.TestLogic;
import com.jjproj.Logic.InputCoordinates;
import com.jjproj.Logic.Coordinates;
import com.jjproj.Logic.Board;
import com.jjproj.Logic.Game;
public class BackendMain {

    public static void main(String[] args) {
    System.out.println("Start to play!\n");
        
        Board board = new Board();
        board.setupDefaultPiecesPositions();
        
        Game game = new Game(board);
        game.gameLoop();
        //TestLogic.test();
        //Game.gameLoop();
    }
}
