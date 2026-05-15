package com.jjproj.Logic;

import com.jjproj.Logic.piece.Piece;
import com.jjproj.Logic.piece.*;
import com.jjproj.Logic.piece.Queen;


import java.util.Scanner;
import java.util.Set;
import com.jjproj.Logic.Coordinates;
import com.jjproj.Logic.Board;
import com.jjproj.Logic.GameStateChecker;


public class Game {

    private final Board board;
    //private BoardConsoleRenderer render = new BoardConsoleRenderer();
    private final BoardConsoleRenderer renderer = new BoardConsoleRenderer();
    private final Scanner scanner = new Scanner(System.in); 
    private InputCoordinates inputCoordinates = new InputCoordinates();
  

    public Game(Board board){
        this.board=board;
    }
    //render -> input -> make move -> pass move 
    
    public void startGame(){
        boolean isWhiteToMove = true;
       // Coordinates selectedPiece = null;
        while(true){
            
        
            renderer.render(board);
            Color currentColor = isWhiteToMove ? Color.WHITE : Color.BLACK;
        

            if(GameStateChecker.isCheckMate(currentColor, board)){
                Color winner = ( currentColor == Color.WHITE) ? Color.BLACK : Color.WHITE;
                System.out.println("Szach-mat! Wygrywają " + (winner == Color.WHITE ? "BIAŁE" : "CZARNE") + "!");
                break;
            }
            
            if(GameStateChecker.isCheckPate(currentColor, board)){
                System.out.println("Pat!");
                break;
            }
            if(GameStateChecker.isKingInCheck(currentColor, board)) {
                System.out.println("Szach!");
            }
            System.out.println("\n" + (currentColor == Color.WHITE ? "BIAŁE" : "CZARNE") + " to move!");
            Coordinates from;
            Set<Coordinates> legalMoves;

            while(true){
                from = InputCoordinates.inputPieceCoordinatesForColor(currentColor, board);
                Piece piece= board.getPiece(from);
                legalMoves = GameStateChecker.getLegalMoves(piece, from, board);
                if(legalMoves.isEmpty()){
                    System.out.println("Ta figura nie ma legalnych ruchów!");
                    continue;
                }
                break;
            }

    
            Coordinates to =InputCoordinates.inputAvailableSquare(legalMoves);
            //move
            board.movePiece(from, to);

            //check if pice can change
            Piece movePiece = board.getPiece(to);
            if(GameStateChecker.shouldPromote(movePiece, to)){
                handlePromotion((to), currentColor);
            }

            //change color
            isWhiteToMove= !isWhiteToMove;
        }
    }
    private void handlePromotion(Coordinates coords, Color color) {
        System.out.println("Promocja! Wybierz: Q (hetman), R (wieża), B (goniec), N (skoczek):");
        //Scanner scanner = new Scanner(System.in);
        while (true) {
            String choice = scanner.nextLine().trim().toUpperCase();
            Piece newPiece = null;
            if(choice.equals("Q")){
                newPiece = new Queen(color, coords);
            }      
            else if (choice.equals("R")){
                newPiece = new Rook(color, coords);
            }
            else if (choice.equals("B")){
                newPiece = new Bishop(color, coords);
            } 
            else if (choice.equals("N")){
                newPiece = new Knight(color, coords);
            }
            if (newPiece != null) {
                GameStateChecker.promotePawn(coords, newPiece, board);
                break;
            }
            System.out.println("Nieprawidłowy wybór! Wpisz Q, R, B lub N:");
        }
    }

}
