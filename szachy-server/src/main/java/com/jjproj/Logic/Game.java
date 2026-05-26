package com.jjproj.Logic;

import com.jjproj.Logic.piece.*;

import java.util.Scanner;
import java.util.Set;


public class Game {

    private final Board board;
    //private BoardConsoleRenderer render = new BoardConsoleRenderer();
    private final BoardConsoleRenderer renderer = new BoardConsoleRenderer();
    private final Scanner scanner = new Scanner(System.in); 
    //private InputCoordinates inputCoordinates = new InputCoordinates();
  

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

             //sprawdzamy en passant przed ruchem 
            Piece piece = board.getPiece(from);
            int fileDiff = Math.abs(to.file.ordinal() - from.file.ordinal());
            boolean isEnPassant = piece instanceof Pawn && fileDiff == 1 && board.isSquareEmpty(to);


            //move
            board.movePiece(from, to);

            //en passant: usuwamy zbitą pieszkę
            if (isEnPassant) {
                Coordinates capturedPawn = new Coordinates(to.file, from.rank);
                board.removePiece(capturedPawn);
                System.out.println("Bicie w przelocie!");
            }

            //roszada: przesuwamy wieżę
            if (piece instanceof King) {
                int kingFileDiff = to.file.ordinal() - from.file.ordinal();
                if(Math.abs(kingFileDiff) == 2) {
                    handleCastling(from, to);
                    System.out.println("Roszada!");
                }
            }
            //aktualizujemy en passant target
            board.setEnPassantTarget(null);
            if (piece instanceof Pawn && Math.abs(to.rank - from.rank) == 2) {
                int epRank = (from.rank + to.rank) / 2;
                board.setEnPassantTarget(new Coordinates(from.file, epRank));
            }
            //aktualizujemy prawa do roszady
            updateCastlingRights(from, piece);


            //check if pice can change
            Piece movePiece = board.getPiece(to);
            if(GameStateChecker.shouldPromote(movePiece, to)){
                handlePromotion((to), currentColor);
            }

            //change color
            isWhiteToMove= !isWhiteToMove;
        }
    }
    //roszada: przesuń wieżę 
    private void handleCastling(Coordinates kingFrom, Coordinates kingTo) {
        boolean kingSide = kingTo.file.ordinal() > kingFrom.file.ordinal();
        int rank = kingFrom.rank;

        Coordinates rookFrom = new Coordinates(kingSide ? File.H : File.A, rank);
        Coordinates rookTo = new Coordinates(File.values()[kingFrom.file.ordinal() + (kingSide ? 1 : -1)],rank);

        board.movePiece(rookFrom, rookTo);
    }

    // aktualizacja praw do roszady 
    private void updateCastlingRights(Coordinates from, Piece piece) {
        if (piece instanceof King) {
            board.revokeCastlingRight(piece.color, true);
            board.revokeCastlingRight(piece.color, false);
        }
        if(piece instanceof Rook) {
            if (from.equals(new Coordinates(File.H, 1)))
                board.revokeCastlingRight(Color.WHITE, true);
            if (from.equals(new Coordinates(File.A, 1)))
                board.revokeCastlingRight(Color.WHITE, false);
            if (from.equals(new Coordinates(File.H, 8)))
                board.revokeCastlingRight(Color.BLACK, true);
            if (from.equals(new Coordinates(File.A, 8)))
                board.revokeCastlingRight(Color.BLACK, false);
        }
    }
    //promocja pionka
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
