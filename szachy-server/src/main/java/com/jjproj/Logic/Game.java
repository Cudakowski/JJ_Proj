package com.jjproj.Logic;

import com.jjproj.Logic.piece.Piece;
import java.util.Set;

public class Game {

    private final Board board;
    //private BoardConsoleRenderer render = new BoardConsoleRenderer();
    private final BoardConsoleRenderer renderer = new BoardConsoleRenderer(); 
    private InputCoordinates inputCoordinates = new InputCoordinates();
  

    public Game(Board board){
        this.board=board;
    }
    //render -> input -> make move -> pass move 
    
    public void startGame(){
        boolean isWhiteToMove = true;
        Coordinates selectedPiece = null;
        while(true){
            
        
            renderer.render(board);
            Color currentColor = isWhiteToMove ? Color.WHITE : Color.BLACK;
            System.out.println("\n" + (currentColor == Color.WHITE ? "BIAŁE" : "CZARNE") + " to move!");
            Coordinates from = InputCoordinates.inputPieceCoordinatesForColor(currentColor, board);

            //input
            //Coordinates sourseCoordinates=InputCoordinates.inputPieceCoordinatesForColor(isWhiteToMove ? Color.WHITE : Color.BLACK, board);
            
            Piece piece= board.getPiece(from);
            Set<Coordinates> availableMoveSquares =piece.getAvailableMoveSquares(board);
            Coordinates to =InputCoordinates.inputAvailableSquare(availableMoveSquares);
            //move
            board.movePiece(from, to);

            //change color
            isWhiteToMove= !isWhiteToMove;
        }
    }
}
