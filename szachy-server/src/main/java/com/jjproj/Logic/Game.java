package com.jjproj.Logic;

import com.jjproj.Logic.piece.Piece;
import java.util.Set;

public class Game {

    private final Board board;
    private BoardConsoleRenderer render = new BoardConsoleRenderer();
    private InputCoordinates inputCoordinates = new InputCoordinates();

    public Game(Board board){
        this.board=board;
    }
    //render -> input -> make move -> pass move 
    
    public void gameLoop(){
        boolean isWhiteToMove = true;
        while(true){
            

            //renderer.render(board);

            //input
            Coordinates sourseCoordinates=InputCoordinates.inputPieceCoordinatesForColor(isWhiteToMove ? Color.WHITE : Color.BLACK, board);
            
            Piece piece= board.getPiece(sourseCoordinates);
            Set<Coordinates> availableMoveSquares =piece.getAvailableMoveSquares(board);
            Coordinates targetCoordinates =InputCoordinates.inputAvailableSquare(availableMoveSquares);
            //move
            board.movePiece(sourseCoordinates, targetCoordinates);

            //change color
            isWhiteToMove= !isWhiteToMove;
        }
    }
}
